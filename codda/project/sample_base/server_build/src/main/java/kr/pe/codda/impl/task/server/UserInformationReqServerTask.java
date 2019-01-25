package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.sql.Connection;
import java.sql.Timestamp;

import javax.sql.DataSource;

import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.message.UserInformationReq.UserInformationReq;
import kr.pe.codda.impl.message.UserInformationRes.UserInformationRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

import org.jooq.DSLContext;
import org.jooq.Record9;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;

public class UserInformationReqServerTask extends AbstractServerTask {

	private void sendErrorOutputMessage(String errorMessage, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		log.warn("{}, inObj=", errorMessage, inputMessage.toString());

		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(inputMessage.getMessageID());
		messageResultRes.setIsSuccess(false);
		messageResultRes.setResultMessage(errorMessage);
		toLetterCarrier.addSyncOutputMessage(messageResultRes);
	}
	
	@Override
	public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		try {
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (UserInformationReq)inputMessage);
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
	
	public UserInformationRes doWork(String dbcpName, UserInformationReq userInformationReq)
			throws Exception {
		String requestedUserID = userInformationReq.getRequestedUserID();
		String targetUserID = userInformationReq.getTargetUserID();
		
		if (null == requestedUserID) {
			String errorMessage = "요청한 사용자 아이디를 넣어주세요";
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ValueChecker.checkValidRequestedUserID(requestedUserID);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		if (null == targetUserID) {
			String errorMessage = "차단 해제할 사용자 아이디를 넣어주세요";
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ValueChecker.checkValidUnBlockUserID(targetUserID);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		String nickname = null;
		String memeberState = null;
		String role = null;
		String passwordHint = null;
		String passwordAnswer = null;
		UByte passwordFailedCount = null;
		String ip = null;
		Timestamp registeredDate = null;
		Timestamp lastModifiedDate = null;
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));
			
			String memberRoleOfRequestedUserID = ValueChecker.checkValidRequestedUserState(conn, create, log, requestedUserID);	
			MemberRoleType  memberRoleTypeOfRequestedUserID = null;
			try {
				memberRoleTypeOfRequestedUserID = MemberRoleType.valueOf(memberRoleOfRequestedUserID, false);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("사용자 차단 해제 요청자의 멤버 타입[")
						.append(memberRoleOfRequestedUserID)
						.append("]이 잘못되어있습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			if (! MemberRoleType.ADMIN.equals(memberRoleTypeOfRequestedUserID)) {
				if (! requestedUserID.equals(targetUserID)) {
					String errorMessage = "타인의 사용자 정보는 검색할 수 없습니다";
					throw new ServerServiceException(errorMessage);
				}
			}
			
			Record9<String, String, String, String, String, UByte, String, Timestamp, Timestamp> 
			memberRecordOfTargetUserID = create.select(SB_MEMBER_TB.NICKNAME,
					SB_MEMBER_TB.STATE, SB_MEMBER_TB.ROLE,
					SB_MEMBER_TB.PWD_HINT,
					SB_MEMBER_TB.PWD_ANSWER,
					SB_MEMBER_TB.PWD_FAIL_CNT,
					SB_MEMBER_TB.IP,
					SB_MEMBER_TB.REG_DT,
					SB_MEMBER_TB.MOD_DT)
			.from(SB_MEMBER_TB)
			.where(SB_MEMBER_TB.USER_ID.eq(targetUserID))
			.fetchOne();
			
			if (null == memberRecordOfTargetUserID) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("회원 조회를 원하는 사용자[")
						.append(targetUserID)
						.append("]가 회원 테이블에 존재하지 않습니다").toString();				
				throw new ServerServiceException(errorMessage);
			}
			
			nickname = memberRecordOfTargetUserID.get(SB_MEMBER_TB.NICKNAME);
			memeberState = memberRecordOfTargetUserID.get(SB_MEMBER_TB.STATE);			
			role = memberRecordOfTargetUserID.get(SB_MEMBER_TB.ROLE);
			passwordHint = memberRecordOfTargetUserID.get(SB_MEMBER_TB.PWD_HINT);
			passwordAnswer = memberRecordOfTargetUserID.get(SB_MEMBER_TB.PWD_ANSWER);
			passwordFailedCount = memberRecordOfTargetUserID.get(SB_MEMBER_TB.PWD_FAIL_CNT);
			ip = memberRecordOfTargetUserID.get(SB_MEMBER_TB.IP);
			registeredDate = memberRecordOfTargetUserID.get(SB_MEMBER_TB.REG_DT);
			lastModifiedDate = memberRecordOfTargetUserID.get(SB_MEMBER_TB.MOD_DT);

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
				} catch (Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}		
		
		UserInformationRes userInformationRes = new UserInformationRes();
		userInformationRes.setNickname(nickname);
		userInformationRes.setState(memeberState);		
		userInformationRes.setRole(role);
		userInformationRes.setPasswordHint(passwordHint);
		userInformationRes.setPasswordAnswer(passwordAnswer);
		userInformationRes.setPasswordFailedCount(passwordFailedCount.shortValue());
		userInformationRes.setIp(ip);
		userInformationRes.setRegisteredDate(registeredDate);
		userInformationRes.setLastModifiedDate(lastModifiedDate);
		
		return userInformationRes;
	}

}
