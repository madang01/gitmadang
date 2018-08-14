package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Arrays;

import javax.sql.DataSource;

import org.apache.commons.codec.binary.Base64;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.impl.message.AdminLoginReq.AdminLoginReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.JooqSqlUtil;
import kr.pe.codda.server.lib.MemberStateType;
import kr.pe.codda.server.lib.MemberType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class AdminLoginReqServerTask extends AbstractServerTask {	
	private void sendErrorOutputMessage(String errorMessage,			
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		// log.warn("{}, inObj=", errorMessage, inputMessage.toString());
		
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
			AbstractMessage outputMessage = doService((AdminLoginReq)inputMessage);
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
						
			sendErrorOutputMessage("게시글 가져오는데 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}
	
	
	public MessageResultRes doService(AdminLoginReq adminLoginReq) throws Exception {
		// FIXME!
		log.info(adminLoginReq.toString());
		
		String idCipherBase64 = adminLoginReq.getIdCipherBase64();		
		String pwdCipherBase64 = adminLoginReq.getPwdCipherBase64();		
		String sessionKeyBase64 = adminLoginReq.getSessionKeyBase64();		
		String ivBase64 = adminLoginReq.getIvBase64();
		
		if (null == idCipherBase64) {
			/*sendErrorOutputMessage("아이디를 입력해 주세요", toLetterCarrier, adminLoginReq);
			return;*/
			String errorMessage = "아이디를 입력해 주세요";
			throw new ServerServiceException(errorMessage);
		}
		
		if (null == pwdCipherBase64) {
			/*sendErrorOutputMessage("비밀번호를 입력해 주세요", toLetterCarrier, adminLoginReq);
			return;*/
			String errorMessage = "비밀번호를 입력해 주세요";
			throw new ServerServiceException(errorMessage);
		}
		
		if (null == sessionKeyBase64) {
			/*sendErrorOutputMessage("세션키를 입력해 주세요", toLetterCarrier, adminLoginReq);
			return;*/
			String errorMessage = "세션키를 입력해 주세요";
			throw new ServerServiceException(errorMessage);
		}
		
		if (null == ivBase64) {
			/*sendErrorOutputMessage("세션키 소금값을 입력해 주세요", toLetterCarrier, adminLoginReq);
			return;*/
			String errorMessage = "세션키 소금값을 입력해 주세요";
			throw new ServerServiceException(errorMessage);
		}
		
		byte[] idCipherBytes = null;
		byte[] pwdCipherBytes = null;
		byte[] sessionKeyBytes = null;
		byte[] ivBytes = null;
		
		try {
			idCipherBytes = Base64.decodeBase64(idCipherBase64);
		} catch(Exception e) {
			/*sendErrorOutputMessage("아이디 암호문은 base64 인코딩되지 않았습니다", toLetterCarrier, adminLoginReq);
			return;*/
			String errorMessage = "아이디 암호문은 base64 인코딩되지 않았습니다";
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			pwdCipherBytes = Base64.decodeBase64(pwdCipherBase64);
		} catch(Exception e) {
			/*sendErrorOutputMessage("비밀번호 암호문은 base64 인코딩되지 않았습니다", toLetterCarrier, adminLoginReq);
			return;*/
			String errorMessage = "비밀번호 암호문은 base64 인코딩되지 않았습니다";
			throw new ServerServiceException(errorMessage);
		}
		try {
			sessionKeyBytes = Base64.decodeBase64(sessionKeyBase64);
		} catch(Exception e) {
			/*sendErrorOutputMessage("세션키는 base64 인코딩되지 않았습니다", toLetterCarrier, adminLoginReq);
			return;*/
			String errorMessage = "세션키는 base64 인코딩되지 않았습니다";
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ivBytes = Base64.decodeBase64(ivBase64);
		} catch(Exception e) {
			/*sendErrorOutputMessage("세션키 소금값은 base64 인코딩되지 않았습니다", toLetterCarrier, adminLoginReq);
			return;*/
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
			/*sendErrorOutputMessage(errorMessage, toLetterCarrier, adminLoginReq);
			return;*/			
			throw new ServerServiceException(errorMessage);
		} catch (SymmetricException e) {
			String errorMessage = "서버 세션키를 얻는데 실패하였습니다";
			log.warn(adminLoginReq.toString(), e);
			/*sendErrorOutputMessage(errorMessage, toLetterCarrier, adminLoginReq);
			return;*/
			throw new ServerServiceException(errorMessage);
		}
		
		String userId = null;		
		
		try {
			userId = getDecryptedString(idCipherBytes, serverSymmetricKey);
		} catch (IllegalArgumentException e) {
			String errorMessage = "아이디에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			/*sendErrorOutputMessage(errorMessage, toLetterCarrier, adminLoginReq);
			return;*/
			throw new ServerServiceException(errorMessage);
		} catch (SymmetricException e) {
			String errorMessage = "아이디에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(adminLoginReq.toString(), e);
			/*sendErrorOutputMessage(errorMessage, toLetterCarrier, adminLoginReq);
			return;*/
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ValueChecker.checkValidUserID(userId);
		} catch(IllegalArgumentException e) {						
			/*sendErrorOutputMessage(e.getMessage(), toLetterCarrier, adminLoginReq);
			return;*/
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		/*if (null== userId) {
			log.warn("아이디 복호문 값을 얻는데 실패하였습니다. inObj=[{}]", sessionKeyLoginReq.toString());			
			messageResultRes.setResultMessage("아이디 복호문 값을 얻는데 실패하였습니다");

			toLetterCarrier.addSyncOutputMessage(messageResultRes);
			return;
		}*/
		
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
			
			Record resultOfMember = create.select(
					SB_MEMBER_TB.MEMBER_TYPE,
					SB_MEMBER_TB.MEMBER_ST,
					SB_MEMBER_TB.PWD_FAIL_CNT,
					SB_MEMBER_TB.PWD_BASE64,
					SB_MEMBER_TB.PWD_SALT_BASE64)
				.from(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(userId))
				.forUpdate().fetchOne();
			
			if (null == resultOfMember) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("아이디[")
						.append(userId)
						.append("]가 존재하지 않습니다").toString();
				
				/*sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, adminLoginReq);
				return;*/
				throw new ServerServiceException(errorMessage);
			}
			
			String nativeMemberType = resultOfMember.get(SB_MEMBER_TB.MEMBER_TYPE);
			String memberState = resultOfMember.get(SB_MEMBER_TB.MEMBER_ST);
			short pwdFailedCount = resultOfMember.get(SB_MEMBER_TB.PWD_FAIL_CNT).shortValue();
			String pwdMDBase64 =  resultOfMember.get(SB_MEMBER_TB.PWD_BASE64);
			String pwdSaltBase64 = resultOfMember.get(SB_MEMBER_TB.PWD_SALT_BASE64);	
			
			
			MemberType membershipLevel = null;
			try {
				membershipLevel = MemberType.valueOf(nativeMemberType, false);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("회원[")
						.append(userId)
						.append("] 구분[")
						.append(nativeMemberType)
						.append("]가 잘못되었습니다").toString();
				
				// log.warn(errorMessage);
				
				/*sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, adminLoginReq);
				return;*/
				throw new ServerServiceException(errorMessage);
			}
			
			if (! membershipLevel.equals(MemberType.ADMIN)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("회원[")
						.append(userId)
						.append("] 구분[")
						.append(nativeMemberType)
						.append("]이 어드민이 아닙니다").toString();				
				// log.warn(errorMessage);
				
				/*sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, adminLoginReq);
				return;*/
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
						.append(userId)
						.append("]의 멤버 상태[")
						.append(memberState)
						.append("]가 잘못되었습니다").toString();
				/*sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, adminLoginReq);
				return;*/
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
				/*sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, adminLoginReq);
				return;*/
				throw new ServerServiceException(errorMessage);
			}
			
			if (memberStateType.equals(MemberStateType.BLOCK)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("블락된 회원[")
						.append(userId)
						.append("] 입니다").toString();
				/*sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, adminLoginReq);
				return;*/
				throw new ServerServiceException(errorMessage);
			}
			
			if (memberStateType.equals(MemberStateType.WITHDRAWAL)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("탈퇴한 회원[")
						.append(userId)
						.append("] 입니다").toString();
				/*sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, adminLoginReq);
				return;*/
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
				/*sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, adminLoginReq);
				return;*/
				throw new ServerServiceException(errorMessage);
			} catch (SymmetricException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "비밀번호 복호문을 얻는데 실패하였습니다";
				log.warn(errorMessage, e);				
				/*sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, adminLoginReq);
				return;*/
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
				/*sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, adminLoginReq);
				return;*/
				throw new ServerServiceException(errorMessage);
			}
			
			byte[] pwdSaltBytes = Base64.decodeBase64(pwdSaltBase64);
			// byte[] passwordByteArray = password.getBytes(CommonStaticFinalVars.SINNORI_CIPHER_CHARSET);
			
			ByteBuffer passwordByteBuffer = ByteBuffer.allocate(pwdSaltBytes.length+passwordBytes.length);
			passwordByteBuffer.put(pwdSaltBytes);
			passwordByteBuffer.put(passwordBytes);
			MessageDigest md = MessageDigest.getInstance(CommonStaticFinalVars.PASSWORD_ALGORITHM_NAME);
			
			md.update(passwordByteBuffer.array());
			
			// FIXME!
			// log.info(HexUtil.getAllHexStringFromByteBuffer(passwordByteBuffer));
			
			byte pwdMDBytes[] =  md.digest();
			
			
			/** 복호환 비밀번호 초기화 */
			Arrays.fill(passwordBytes, CommonStaticFinalVars.ZERO_BYTE);
			
			String userPwdMDBase64 = Base64.encodeBase64String(pwdMDBytes);
			
			if (! pwdMDBase64.equals(userPwdMDBase64)) {
				/*
				update SB_MEMBER_TB set pwd_fail_cnt=#{pwdFailCount}, mod_dt=sysdate() where user_id=#{userId} and member_gb=1 and member_st=0
				*/				
				int countOfPwdFailedCountUpdate = create.update(SB_MEMBER_TB)
					.set(SB_MEMBER_TB.PWD_FAIL_CNT, UByte.valueOf(pwdFailedCount+1))
					.set(SB_MEMBER_TB.MOD_DT, JooqSqlUtil.getFieldOfSysDate(Timestamp.class))
					.where(SB_MEMBER_TB.USER_ID.eq(userId))
				.execute();
				
				if (0  == countOfPwdFailedCountUpdate) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}
					
					String errorMessage = "비밀 번호 실패 횟수 갱신이 실패하였습니다";
					/*sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, adminLoginReq);
					return;*/
					throw new ServerServiceException(errorMessage);
				}				
				
				conn.commit();				
				
				
				String errorMessage = "비밀 번호가 틀렸습니다";
				/*sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, adminLoginReq);
				return;*/
				throw new ServerServiceException(errorMessage);
			}
			
			conn.commit();
			
			MessageResultRes messageResultRes = new MessageResultRes();
			messageResultRes.setTaskMessageID(adminLoginReq.getMessageID());
			messageResultRes.setIsSuccess(true);		
			messageResultRes.setResultMessage("로그인 성공하셨습니다");
			// sendSuccessOutputMessageForCommit(messageResultRes, conn, toLetterCarrier);
			return messageResultRes;
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
			
			/* String errorMessage = new StringBuilder("unknown error, inObj=")
					.append(adminLoginReq.toString()).toString();
			log.warn(errorMessage, e);

					
			
			sendErrorOutputMessageForRollback("로그인 실패하였습니다", conn, toLetterCarrier, adminLoginReq);
			return;*/
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
	}
}
