package kr.pe.codda.impl.task.server;


import static kr.pe.codda.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.sql.Connection;
import java.sql.Timestamp;

import javax.sql.DataSource;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.message.UserLoginReq.UserLoginReq;
import kr.pe.codda.impl.message.UserLoginRes.UserLoginRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.JooqSqlUtil;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.MemberStateType;
import kr.pe.codda.server.lib.PasswordPairOfMemberTable;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;

public class UserLoginReqServerTask extends AbstractServerTask {
	
	public UserLoginReqServerTask() throws DynamicClassCallException {
		super();
	}


	private void sendErrorOutputMessage(String errorMessage,			
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		log.warn("{}, inObj=", errorMessage, inputMessage.toString());
		
		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(inputMessage.getMessageID());
		messageResultRes.setIsSuccess(false);		
		messageResultRes.setResultMessage(errorMessage);
		toLetterCarrier.addSyncOutputMessage(messageResultRes);
	}
	
	
	private String getDecryptedString(byte[] cipherBytes, ServerSymmetricKeyIF serverSymmetricKey)
			throws InterruptedException, IllegalArgumentException, SymmetricException {		
		byte[] valueBytes = serverSymmetricKey.decrypt(cipherBytes);
		String decryptedString = new String(valueBytes, CommonStaticFinalVars.CIPHER_CHARSET);
		return decryptedString;
	}

	@Override
	public void doTask(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		try {
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (UserLoginReq)inputMessage);
			toLetterCarrier.addSyncOutputMessage(outputMessage);
		} catch(ServerServiceException e) {
			String errorMessage = e.getMessage();
			log.warn("errmsg=={}, inObj={}", errorMessage, inputMessage.toString());
			
			sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
			return;
		} catch(Exception e) {
			String errorMessage = new StringBuilder().append("unknwon errmsg=")
					.append(e.getMessage())
					.append(", inObj=")
					.append(inputMessage.toString()).toString();
			
			log.warn(errorMessage, e);			
			
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, inputMessage);
			return;
		}
	}
	
	
	public UserLoginRes doWork(String dbcpName, UserLoginReq loginReq) throws Exception {
		// FIXME!
		log.info(loginReq.toString());		
		
		String idCipherBase64 = loginReq.getIdCipherBase64();		
		String pwdCipherBase64 = loginReq.getPwdCipherBase64();		
		String sessionKeyBase64 = loginReq.getSessionKeyBase64();		
		String ivBase64 = loginReq.getIvBase64();
		
		if (null == idCipherBase64) {
			String errorMessage = "아이디를 입력해 주세요";
			throw new ServerServiceException(errorMessage);
		}
		
		if (null == pwdCipherBase64) {
			String errorMessage = "비밀번호를 입력해 주세요";
			throw new ServerServiceException(errorMessage);
		}
		
		if (null == sessionKeyBase64) {
			String errorMessage = "세션키를 입력해 주세요";
			throw new ServerServiceException(errorMessage);
		}
		
		if (null == ivBase64) {
			String errorMessage = "비밀번호를 입력해 주세요";
			throw new ServerServiceException(errorMessage);
		}
		
		byte[] idCipherBytes = null;
		byte[] pwdCipherBytes = null;
		byte[] sessionKeyBytes = null;
		byte[] ivBytes = null;
		
		try {
			idCipherBytes = CommonStaticUtil.Base64Decoder.decode(idCipherBase64);
		} catch(Exception e) {
			String errorMessage = "아이디 암호문은 base64 인코딩되지 않았습니다";
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			pwdCipherBytes = CommonStaticUtil.Base64Decoder.decode(pwdCipherBase64);
		} catch(Exception e) {
			String errorMessage = "비밀번호 암호문은 base64 인코딩되지 않았습니다";
			throw new ServerServiceException(errorMessage);
		}
		try {
			sessionKeyBytes = CommonStaticUtil.Base64Decoder.decode(sessionKeyBase64);
		} catch(Exception e) {
			String errorMessage = "세션키는 base64 인코딩되지 않았습니다";
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ivBytes = CommonStaticUtil.Base64Decoder.decode(ivBase64);
		} catch(Exception e) {
			String errorMessage = "세션키 소금값은 base64 인코딩되지 않았습니다";
			throw new ServerServiceException(errorMessage);
		}
		
		ServerSymmetricKeyIF serverSymmetricKey = null;
		try {
			ServerSessionkeyIF serverSessionkey = ServerSessionkeyManager.getInstance().getMainProjectServerSessionkey();
			
			serverSymmetricKey = serverSessionkey.getNewInstanceOfServerSymmetricKey(sessionKeyBytes, ivBytes);
					
		} catch (IllegalArgumentException e) {
			String errorMessage = "서버 세션키를 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ServerServiceException(errorMessage);
		} catch (SymmetricException e) {
			String errorMessage = "서버 세션키를 얻는데 실패하였습니다";
			log.warn(loginReq.toString(), e);
			throw new ServerServiceException(errorMessage);
		}
		
		String userID = null;		
		
		try {
			userID = getDecryptedString(idCipherBytes, serverSymmetricKey);
		} catch (IllegalArgumentException e) {
			String errorMessage = "아이디에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ServerServiceException(errorMessage);
		} catch (SymmetricException e) {
			String errorMessage = "아이디에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(loginReq.toString(), e);
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ValueChecker.checkValidUserID(userID);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		MemberRoleType memberRoleType = null;
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));
			
			
			
			/*
			select 
			user_id as userId, 
			nickname,
			pwd_base64 as pwdBase64,
			pwd_salt_base64 as pwdSaltBase64,
			member_gb as memberGubun,
			member_st as memberState,
			pwd_hint as pwdHint,
			pwd_answer as pwdAnswer,
			pwd_fail_cnt as pwdFailCount,
			reg_dt as registerDate,
			mod_dt as modifiedDate
			from SB_MEMBER_TB where user_id=#{userId} for update
			*/
			
			/*
			update SB_MEMBER_TB set pwd_fail_cnt=#{pwdFailCount}, mod_dt=sysdate() where user_id=#{userId} and member_gb=1 and member_st=0
			*/
			
			Record resultOfMember = create.select(
					SB_MEMBER_TB.ROLE,
					SB_MEMBER_TB.STATE,
					SB_MEMBER_TB.PWD_FAIL_CNT,
					SB_MEMBER_TB.PWD_BASE64,
					SB_MEMBER_TB.PWD_SALT_BASE64)
				.from(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(userID))
				.forUpdate().fetchOne();
			
			if (null == resultOfMember) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("아이디[")
						.append(userID)
						.append("]가 존재하지 않습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			String memberRole = resultOfMember.get(SB_MEMBER_TB.ROLE);
			String memberState = resultOfMember.get(SB_MEMBER_TB.STATE);
			short pwdFailedCount = resultOfMember.get(SB_MEMBER_TB.PWD_FAIL_CNT).shortValue();
			String pwdMDBase64 =  resultOfMember.get(SB_MEMBER_TB.PWD_BASE64);
			String pwdSaltBase64 = resultOfMember.get(SB_MEMBER_TB.PWD_SALT_BASE64);
			
			
			try {
				memberRoleType = MemberRoleType.valueOf(memberRole, false);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("회원[")
						.append(userID)
						.append("]의 멤버 구분[")
						.append(memberRole)
						.append("]이 잘못되었습니다").toString();
				
				// log.warn(errorMessage);
				
				throw new ServerServiceException(errorMessage);
			}
			
			if (MemberRoleType.GUEST.equals(memberRoleType)) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("손님으로 지정된 회원[")
						.append(userID)
						.append("]은 로그인 할 수 없습니다").toString();
				
				throw new ServerServiceException(errorMessage);
			}
			
			MemberStateType memberStateType = null;
			try {
				memberStateType = MemberStateType.valueOf(memberState, false);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("회원[")
						.append(userID)
						.append("]의 멤버 상태[")
						.append(memberState)
						.append("]가 잘못되었습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			if (memberStateType.equals(MemberStateType.BLOCK)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("블락된 회원[")
						.append(userID)
						.append("] 입니다").toString();
				throw new ServerServiceException(errorMessage);
			} else if (memberStateType.equals(MemberStateType.WITHDRAWAL)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("탈퇴한 회원[")
						.append(userID)
						.append("] 입니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			if (ServerCommonStaticFinalVars.MAX_COUNT_OF_PASSWORD_FAILURES <= pwdFailedCount) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("최대 비밀번호 실패 횟수[")
						.append(ServerCommonStaticFinalVars.MAX_COUNT_OF_PASSWORD_FAILURES)
						.append("] 이상으로 비밀번호가 틀렸습니다, 비밀번호 초기화를 수행하시기 바랍니다").toString();
				throw new ServerServiceException(errorMessage);
			}			
					
			
			byte[] passwordBytes = null;
			

			try {
				passwordBytes = serverSymmetricKey.decrypt(pwdCipherBytes);
			} catch (IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "비밀번호 복호문을 얻는데 실패하였습니다";
				log.warn(errorMessage, e);
				throw new ServerServiceException(errorMessage);
			} catch (SymmetricException e) {
				String errorMessage = "비밀번호 복호문을 얻는데 실패하였습니다";
				log.warn(errorMessage, e);
				throw new ServerServiceException(errorMessage);
			}
						
			try {
				ValueChecker.checkValidPwd(passwordBytes);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "잘못된 비밀번호입니다";
				log.warn(errorMessage, e);
				throw new ServerServiceException(errorMessage);
			}
			
			byte[] pwdSaltBytes = CommonStaticUtil.Base64Decoder.decode(pwdSaltBase64);			
			
			PasswordPairOfMemberTable passwordPairOfMemberTable = ServerDBUtil.toPasswordPairOfMemberTable(passwordBytes, pwdSaltBytes);
			
			if (! pwdMDBase64.equals(passwordPairOfMemberTable.getPasswordBase64())) {
				/*
				update SB_MEMBER_TB set pwd_fail_cnt=#{pwdFailCount}, mod_dt=sysdate() where user_id=#{userId} and member_gb=1 and member_st=0
				*/				
				int countOfPwdFailedCountUpdate = create.update(SB_MEMBER_TB)
					.set(SB_MEMBER_TB.PWD_FAIL_CNT, UByte.valueOf(pwdFailedCount+1))
					.set(SB_MEMBER_TB.MOD_DT, JooqSqlUtil.getFieldOfSysDate(Timestamp.class))
					.where(SB_MEMBER_TB.USER_ID.eq(userID))
				.execute();
				
				if (0  == countOfPwdFailedCountUpdate) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}
					
					String errorMessage = "비밀 번호 실패 횟수 갱신이 실패하였습니다";
					throw new ServerServiceException(errorMessage);
				}
				
				conn.commit();
				
				String errorMessage = "비밀 번호가 틀렸습니다";
				throw new ServerServiceException(errorMessage);
			}
			
			
			conn.commit();
			
		} catch (ServerServiceException e) {
			throw e;
		} catch (Exception e) {
			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
			}
			
			throw e;
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch(Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}
		
		UserLoginRes userLoginRes = new UserLoginRes();
		userLoginRes.setUserID(userID);
		userLoginRes.setMemberRole(memberRoleType.getValue());

		return userLoginRes;
	}
}
