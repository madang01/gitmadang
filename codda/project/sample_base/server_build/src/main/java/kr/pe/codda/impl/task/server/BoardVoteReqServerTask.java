package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardVoteTb.SB_BOARD_VOTE_TB;

import java.sql.Connection;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardVoteReq.BoardVoteReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.JooqSqlUtil;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardVoteReqServerTask extends AbstractServerTask {
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
			AbstractMessage outputMessage = doService((BoardVoteReq)inputMessage);
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
						
			sendErrorOutputMessage("3. 게시글에 대한 추천이 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}
	
	public MessageResultRes doService(BoardVoteReq boardVoteReq) throws Exception {
		// FIXME!
		log.info(boardVoteReq.toString());	
		
		try {
			ValueChecker.checkValidUserId(boardVoteReq.getUserId());
		} catch(RuntimeException e) {
			/*log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardVoteReq);
			return;*/
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		
		try {
			ValueChecker.checkValidIP(boardVoteReq.getIp());
		} catch(RuntimeException e) {
			/*log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardVoteReq);
			return;*/
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
			
			Record1<String> boardRecod = create.select(SB_BOARD_TB.WRITER_ID)
			.from(SB_BOARD_TB)
			.where(SB_BOARD_TB.BOARD_NO.eq(UInteger.valueOf(boardVoteReq.getBoardNo()))).fetchOne();
			
			if (null == boardRecod) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("해당 게시글[")
						.append(boardVoteReq.getBoardNo())
						.append("]이 존재하지 않습니다").toString();
				
				/*sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, boardVoteReq);
				return;*/
				throw new ServerServiceException(errorMessage);
			}
			
			String writerID = boardRecod.getValue(SB_BOARD_TB.WRITER_ID);
			
			if (writerID.equals(boardVoteReq.getUserId())) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("자기글[")
						.append(boardVoteReq.getBoardNo())
						.append("]은 추천할 수 없습니다").toString();
				
				/*
				sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, boardVoteReq);
				return;*/
				throw new ServerServiceException(errorMessage);
			}
			
			boolean isVoted = create.fetchExists(create.select()
					.from(SB_BOARD_VOTE_TB)
					.where(SB_BOARD_VOTE_TB.BOARD_NO.eq(UInteger.valueOf(boardVoteReq.getBoardNo())))
					.and(SB_BOARD_VOTE_TB.USER_ID.eq(boardVoteReq.getUserId())));
			
			if (isVoted) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "이미 추천을 했습니다";
				/*sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, boardVoteReq);
				return;*/
				throw new ServerServiceException(errorMessage);
			}
			
			int countOfInsert = create.insertInto(SB_BOARD_VOTE_TB)
			.set(SB_BOARD_VOTE_TB.BOARD_NO, UInteger.valueOf(boardVoteReq.getBoardNo()))
			.set(SB_BOARD_VOTE_TB.USER_ID, boardVoteReq.getUserId())
			.set(SB_BOARD_VOTE_TB.IP, boardVoteReq.getIp())
			.set(SB_BOARD_VOTE_TB.REG_DT, JooqSqlUtil.getFieldOfSysDate(Timestamp.class))
			.execute();
			
			if (0 == countOfInsert) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "1.게시글에 대한 추천이 실패하였습니다";
				/*sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, boardVoteReq);
				return;*/
				throw new ServerServiceException(errorMessage);
			}
			
			
			conn.commit();
			
			
			MessageResultRes messageResultRes = new MessageResultRes();
			messageResultRes.setTaskMessageID(boardVoteReq.getMessageID());
			messageResultRes.setIsSuccess(true);
			messageResultRes.setResultMessage("게시글에 대한 추천이 성공하였습니다");
			/*sendSuccessOutputMessageForCommit(messageResultRes, conn, toLetterCarrier);
			return;*/
			return messageResultRes;
		} catch (ServerServiceException e) {
			throw e;
		} catch (Exception e) {
			/*log.warn("unknown error", e);

			sendErrorOutputMessageForRollback("2.게시글에 대한 추천이 실패하였습니다", conn, toLetterCarrier, boardVoteReq);
			return;*/
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
	}
}
