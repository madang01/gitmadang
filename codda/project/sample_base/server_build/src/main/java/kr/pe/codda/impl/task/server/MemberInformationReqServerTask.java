package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.sql.Connection;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record5;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.MemberInformationReq.MemberInformationReq;
import kr.pe.codda.impl.message.MemberInformationRes.MemberInformationRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.MemberStateType;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class MemberInformationReqServerTask extends AbstractServerTask {
	public MemberInformationReqServerTask() throws DynamicClassCallException {
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
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME,
					(MemberInformationReq) inputMessage);
			toLetterCarrier.addSyncOutputMessage(outputMessage);
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();
			log.warn("errmsg=={}, inObj={}", errorMessage, inputMessage.toString());

			sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
			return;
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("unknwon errmsg=").append(e.getMessage())
					.append(", inObj=").append(inputMessage.toString()).toString();

			log.warn(errorMessage, e);

			sendErrorOutputMessage("개인 정보 조회가  실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}

	public MemberInformationRes doWork(String dbcpName, MemberInformationReq memberInformationReq) throws Exception {
		// FIXME!
		log.info(memberInformationReq.toString());

		try {
			ValueChecker.checkValidUserID(memberInformationReq.getRequestedUserID());
		} catch (RuntimeException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}

		try {
			ValueChecker.checkValidActivtyTargetUserID(memberInformationReq.getTargetUserID());
		} catch (RuntimeException e) {
			String errorMessage = e.getMessage();
			log.warn(errorMessage, e);

			throw new ServerServiceException(errorMessage);
		}
		
		String targetUserNickname = null;
		MemberRoleType targetUserMemberRoleType = null;
		MemberStateType targetUserMemberStateType = null;
		Timestamp targetUserRegisteredDate = null;
		Timestamp targetUserLastModifiedDate = null;
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));
			
			@SuppressWarnings("unused")
			MemberRoleType memberRoleTypeOfRequestedUserID = ServerDBUtil.checkUserAccessRights(conn, create, log, "개인 정보 조회 서비스", PermissionType.GUEST, memberInformationReq.getRequestedUserID());
			
			Record5<String, Byte, Byte, Timestamp, Timestamp> targetUserMemberRecord = create.select(
					SB_MEMBER_TB.NICKNAME,
					SB_MEMBER_TB.ROLE,
					SB_MEMBER_TB.STATE,
					SB_MEMBER_TB.REG_DT,
					SB_MEMBER_TB.LAST_MOD_DT)
				.from(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(memberInformationReq.getTargetUserID())).fetchOne();
			
			if (null == targetUserMemberRecord) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("개인 정보 조회 대상 사용자[")
						.append(memberInformationReq.getTargetUserID())
						.append("]가 회원 테이블에 존재하지 않습니다").toString();	
				
				throw new ServerServiceException(errorMessage);
			}
			
			
			targetUserNickname = targetUserMemberRecord.get(SB_MEMBER_TB.NICKNAME);
			byte targetUserMemberRole = targetUserMemberRecord.get(SB_MEMBER_TB.ROLE);
			byte targetUserMemberState = targetUserMemberRecord.get(SB_MEMBER_TB.STATE);
			targetUserRegisteredDate = targetUserMemberRecord.get(SB_MEMBER_TB.REG_DT);
			targetUserLastModifiedDate = targetUserMemberRecord.get(SB_MEMBER_TB.LAST_MOD_DT);
			
			
			try {
				targetUserMemberRoleType = MemberRoleType.valueOf(targetUserMemberRole);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("개인 정보 조회 대상 사용자[")
						.append(memberInformationReq.getTargetUserID())
						.append("]의 멤버 구분[")
						.append(targetUserMemberRole)
						.append("]이 잘못되었습니다").toString();
				
				throw new ServerServiceException(errorMessage);
			}
			
			if (MemberRoleType.GUEST.equals(targetUserMemberRoleType)) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "개인 정보 조회 대상은 회원만 가능합니다";
				
				throw new ServerServiceException(errorMessage);
			}
			
			
			try {
				targetUserMemberStateType = MemberStateType.valueOf(targetUserMemberState);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("개인 정보 조회 대상 사용자[")
						.append(memberInformationReq.getTargetUserID())
						.append("]의 멤버 상태[")
						.append(targetUserMemberState)
						.append("]가 잘못되었습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
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
		
		MemberInformationRes memberInformationRes = new MemberInformationRes();
		memberInformationRes.setTargetUserID(memberInformationReq.getTargetUserID());
		memberInformationRes.setNickname(targetUserNickname);
		memberInformationRes.setRole(targetUserMemberRoleType.getValue());
		memberInformationRes.setState(targetUserMemberStateType.getValue());
		memberInformationRes.setRegisteredDate(targetUserRegisteredDate);
		memberInformationRes.setLastModifiedDate(targetUserLastModifiedDate);
		
		
		return memberInformationRes;
	}

}
