package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;

import java.sql.Connection;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardInfoModifyReq.BoardInfoModifyReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardInfoModifyReqServerTask extends AbstractServerTask {

	public BoardInfoModifyReqServerTask() throws DynamicClassCallException {
		super();
	}

	private void sendErrorOutputMessage(String errorMessage, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		log.warn("{}, inObj={}", errorMessage, inputMessage.toString());

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
					(BoardInfoModifyReq) inputMessage);
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

			sendErrorOutputMessage("게시판 정보 수정이 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}
	
	public MessageResultRes doWork(String dbcpName, BoardInfoModifyReq boardInfoModifyReq) throws Exception {
		// FIXME!
		log.info(boardInfoModifyReq.toString());
		
		try {
			ValueChecker.checkValidWriterID(boardInfoModifyReq.getRequestedUserID());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		UByte boardID = UByte.valueOf(boardInfoModifyReq.getBoardID());
		
		
		DataSource dataSource = DBCPManager.getInstance().getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));
			
			String memberRoleOfRequestedUserID = ValueChecker.checkValidRequestedUserState(conn, create, log,
					boardInfoModifyReq.getRequestedUserID());			
			MemberRoleType  memberRoleTypeOfRequestedUserID = null;
			try {
				memberRoleTypeOfRequestedUserID = MemberRoleType.valueOf(memberRoleOfRequestedUserID, false);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("알 수 없는 회원[")
					.append(boardInfoModifyReq.getRequestedUserID())
					.append("]의 역활[")
					.append(memberRoleOfRequestedUserID)
					.append("] 값입니다").toString();
				throw new ServerServiceException(errorMessage);
			}	
			
			if (! MemberRoleType.ADMIN.equals(memberRoleTypeOfRequestedUserID)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "게시판 정보 수정 서비스는 관리자 전용 서비스입니다";
				throw new ServerServiceException(errorMessage);
			}
			
			boolean isBoardInfoRecord = create.fetchExists(create.select().from(SB_BOARD_INFO_TB).where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)));
			
			if (! isBoardInfoRecord) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "지정한 게시판 식별자에 대한 게시판 정보가 존재하지 않습니다";
				throw new ServerServiceException(errorMessage);
			}
			
			boolean isBoardRecord = create.fetchExists(create.select().from(SB_BOARD_TB).where(SB_BOARD_TB.BOARD_ID.eq(boardID)));
			
			if (isBoardRecord) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "수정를 원하는 게시판 식별자를 갖는 게시글이 존재하여 게시판 정보를 수정 할 수 없습니다";
				throw new ServerServiceException(errorMessage);
			}		
			
			int countOfUpdate = create.update(SB_BOARD_INFO_TB)
			.set(SB_BOARD_INFO_TB.BOARD_NAME, boardInfoModifyReq.getBoardName())
			.set(SB_BOARD_INFO_TB.BOARD_INFO, boardInfoModifyReq.getBoardInformation())
			.set(SB_BOARD_INFO_TB.LIST_TYPE, boardInfoModifyReq.getBoardListType())
			.set(SB_BOARD_INFO_TB.REPLY_POLICY_TYPE, boardInfoModifyReq.getBoardReplyPolicyType())
			.set(SB_BOARD_INFO_TB.WRITE_PERMISSION_TYPE, boardInfoModifyReq.getBoardWritePermissionType())
			.set(SB_BOARD_INFO_TB.REPLY_PERMISSION_TYPE, boardInfoModifyReq.getBoardReplyPermissionType())			
			.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).execute();			
			
			if (0 == countOfUpdate) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append("게시판 정보[")
						.append(boardInfoModifyReq.getBoardID())
						.append("]를 수정하는데 실패하였습니다").toString();
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
				} catch (Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}

		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(boardInfoModifyReq.getMessageID());
		messageResultRes.setIsSuccess(true);
		messageResultRes.setResultMessage(new StringBuilder().append("게시판 정보[")
				.append(boardInfoModifyReq.getBoardID())
				.append("]를 수정하였습니다").toString());
		

		return messageResultRes;
	}

}
