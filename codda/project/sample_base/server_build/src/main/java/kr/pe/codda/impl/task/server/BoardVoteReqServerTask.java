package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.jooq.tables.SbBoardVoteTb.SB_BOARD_VOTE_TB;

import java.sql.Connection;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardVoteReq.BoardVoteReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.MemberActivityType;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardVoteReqServerTask extends AbstractServerTask {
	public BoardVoteReqServerTask() throws DynamicClassCallException {
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
	public void doTask(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		try {
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (BoardVoteReq)inputMessage);
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
						
			sendErrorOutputMessage("게시글을 추천하는데  실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}
	
	public MessageResultRes doWork(String dbcpName, BoardVoteReq boardVoteReq) throws Exception {
		// FIXME!
		log.info(boardVoteReq.toString());	
		
		try {
			ValueChecker.checkValidRequestedUserID(boardVoteReq.getRequestedUserID());
		} catch(RuntimeException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}		
		
		try {
			ValueChecker.checkValidIP(boardVoteReq.getIp());
		} catch(RuntimeException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ValueChecker.checkValidBoardID(boardVoteReq.getBoardID());
		} catch(RuntimeException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ValueChecker.checkValidBoardNo(boardVoteReq.getBoardNo());
		} catch(RuntimeException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		
		
		UByte boardID = UByte.valueOf(boardVoteReq.getBoardID());
		UInteger boardNo = UInteger.valueOf(boardVoteReq.getBoardNo());
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));
			
			MemberRoleType memberRoleTypeOfRequestedUserID = ServerDBUtil.checkUserAccessRights(conn, create, log, "게시글 추천 서비스", PermissionType.MEMBER, boardVoteReq.getRequestedUserID());
			
			
			/** 추천할 게시글에 속한 그룹의 루트 노드에 해당하는 레코드에 락을 건다  */
			ServerDBUtil.lockGroupOfGivenBoard(conn, create, log, boardID, boardNo);
			
			Record1<String> 
			firstWriterBoardRecord = create.select(SB_BOARD_HISTORY_TB.REGISTRANT_ID)
			.from(SB_BOARD_HISTORY_TB)
			.where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(boardID))
			.and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(boardNo))
			.and(SB_BOARD_HISTORY_TB.HISTORY_SQ.eq(UByte.valueOf(0)))
			.fetchOne();
			
			if (null == firstWriterBoardRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("해당 게시글의 최초 작성자 정보가 존재 하지 않습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			String firstWriterID = firstWriterBoardRecord.getValue(SB_BOARD_HISTORY_TB.REGISTRANT_ID);
			
			if (firstWriterID.equals(boardVoteReq.getRequestedUserID())) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("자신의 글[")
						.append(boardNo)
						.append("]은 본인 스스로 추천할 수 없습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			boolean isVoted = create.fetchExists(create.select()
					.from(SB_BOARD_VOTE_TB)
					.where(SB_BOARD_VOTE_TB.BOARD_ID.eq(boardID))
					.and(SB_BOARD_VOTE_TB.BOARD_NO.eq(boardNo))
					.and(SB_BOARD_VOTE_TB.USER_ID.eq(boardVoteReq.getRequestedUserID())));
			
			if (isVoted) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "이미 추천을 하셨습니다";
				throw new ServerServiceException(errorMessage);
			}
			
			Timestamp registeredDate = new java.sql.Timestamp(System.currentTimeMillis());
			
			int countOfInsert = create.insertInto(SB_BOARD_VOTE_TB)
					.set(SB_BOARD_VOTE_TB.BOARD_ID, boardID)
			.set(SB_BOARD_VOTE_TB.BOARD_NO, boardNo)
			.set(SB_BOARD_VOTE_TB.USER_ID, boardVoteReq.getRequestedUserID())
			.set(SB_BOARD_VOTE_TB.IP, boardVoteReq.getIp())
			.set(SB_BOARD_VOTE_TB.REG_DT, registeredDate)
			.execute();
			
			if (0 == countOfInsert) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "해당 글에 대한 추천이 실패하였습니다";
				throw new ServerServiceException(errorMessage);
			}
			
			conn.commit();
			
			ServerDBUtil.insertMemberActivityHistory(conn, create, log, boardVoteReq.getRequestedUserID(), 
					memberRoleTypeOfRequestedUserID, MemberActivityType.VOTE, boardID, boardNo, registeredDate);
			
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
		messageResultRes.setTaskMessageID(boardVoteReq.getMessageID());
		messageResultRes.setIsSuccess(true);
		messageResultRes.setResultMessage("게시글에 대한 추천이 성공하였습니다");
		
		return messageResultRes;
	}
}
