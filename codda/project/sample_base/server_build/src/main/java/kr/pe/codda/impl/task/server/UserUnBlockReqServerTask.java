package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;
import static kr.pe.codda.impl.jooq.tables.SbUserActionHistoryTb.SB_USER_ACTION_HISTORY_TB;

import java.sql.Connection;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.message.UserUnBlockReq.UserUnBlockReq;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.JooqSqlUtil;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.MemberStateType;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class UserUnBlockReqServerTask extends AbstractServerTask {

	public UserUnBlockReqServerTask() throws DynamicClassCallException {
		super();
	}

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
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (UserUnBlockReq)inputMessage);
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
	
	public MessageResultRes doWork(String dbcpName, UserUnBlockReq userUnBlockReq)
			throws Exception {
		String requestedUserID = userUnBlockReq.getRequestedUserID();
		String targetUserID = userUnBlockReq.getTargetUserID();
		
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
		
		if (requestedUserID.equals(targetUserID)) {
			String errorMessage = "자기 자신을 차단 해제 할 수 없습니다";
			throw new ServerServiceException(errorMessage);
		}
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));
			
			ServerDBUtil.checkUserAccessRights(conn, create, log, "사용자 차단 해제 서비스", PermissionType.ADMIN, requestedUserID);
			
			
			/** 차단 해제 대상 회원 레코드 락 */
			Record2<String, String> memberRecordOfTargetUserID = create.select(SB_MEMBER_TB.STATE, SB_MEMBER_TB.ROLE)
			.from(SB_MEMBER_TB)
			.where(SB_MEMBER_TB.USER_ID.eq(targetUserID))
			.forUpdate()
			.fetchOne();
			
			if (null == memberRecordOfTargetUserID) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("차단 해제 대상 사용자[")
						.append(targetUserID)
						.append("]가 회원 테이블에 존재하지 않습니다").toString();				
				throw new ServerServiceException(errorMessage);
			}
			
			String memberRoleOfTargetUserID = memberRecordOfTargetUserID.getValue(SB_MEMBER_TB.ROLE);
			MemberRoleType  memberRoleTypeOfTargetUserID = null;
			try {
				memberRoleTypeOfTargetUserID = MemberRoleType.valueOf(memberRoleOfTargetUserID, false);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("알 수 없는 회원[")
					.append(targetUserID)
					.append("]의 역활[")
					.append(memberRoleOfTargetUserID)
					.append("] 값입니다").toString();
				throw new ServerServiceException(errorMessage);
			}	
			
			if (MemberRoleType.ADMIN.equals(memberRoleTypeOfTargetUserID)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "관리자 아이디는 차단 대상이 아니므로 차단 해제 역시 대상이 아닙니다";
				throw new ServerServiceException(errorMessage);
			}
			
			String memeberStateOfTargetUserID = memberRecordOfTargetUserID.getValue(SB_MEMBER_TB.STATE);
			MemberStateType memberStateTypeOfTargetUserID = null;
			try {
				memberStateTypeOfTargetUserID = MemberStateType.valueOf(memeberStateOfTargetUserID, false);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("알 수 없는 회원[")
					.append(targetUserID)
					.append("]의 상태[")
					.append(memeberStateOfTargetUserID)
					.append("] 값입니다").toString();				
				throw new ServerServiceException(errorMessage);
			}
			
			if (! MemberStateType.BLOCK.equals(memberStateTypeOfTargetUserID)) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("차단 해제 대상 사용자[사용자아이디=")
						.append(targetUserID)
						.append(", 상태=")
						.append(memberStateTypeOfTargetUserID.getName())
						.append("]는 차단된 사용자가 아닙니다").toString();				
				throw new ServerServiceException(errorMessage);
			}			
			
			create.update(SB_MEMBER_TB)
			.set(SB_MEMBER_TB.STATE, MemberStateType.OK.getValue())
			.where(SB_MEMBER_TB.USER_ID.eq(targetUserID))
			.execute();
			
			create.insertInto(SB_USER_ACTION_HISTORY_TB)
			.set(SB_USER_ACTION_HISTORY_TB.USER_ID, requestedUserID)
			.set(SB_USER_ACTION_HISTORY_TB.INPUT_MESSAGE_ID, userUnBlockReq.getMessageID())
			.set(SB_USER_ACTION_HISTORY_TB.INPUT_MESSAGE, userUnBlockReq.toString())
			.set(SB_USER_ACTION_HISTORY_TB.REG_DT, JooqSqlUtil.getFieldOfSysDate(Timestamp.class))
			.execute();
			
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
		
		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(userUnBlockReq.getMessageID());
		messageResultRes.setIsSuccess(true);
		messageResultRes.setResultMessage(new StringBuilder()
				.append("차단된 사용자[")
				.append(userUnBlockReq.getTargetUserID())
				.append("]에 대한 제제를 해제하였습니다").toString());
		
		return messageResultRes;
	}

}
