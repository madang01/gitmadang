package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardVoteTb.SB_BOARD_VOTE_TB;
import static kr.pe.codda.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record8;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;

import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardListReq.BoardListReq;
import kr.pe.codda.impl.message.BoardListRes.BoardListRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardListReqServerTask extends AbstractServerTask {

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
			AbstractMessage outputMessage = doService((BoardListReq) inputMessage);
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

			sendErrorOutputMessage("회원 가입이 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}

	public BoardListRes doService(BoardListReq boardListReq) throws Exception {
		// FIXME!
		log.info(boardListReq.toString());
		
		try {
			ValueChecker.checkValidBoardId(boardListReq.getBoardID());
		} catch(IllegalArgumentException e) {			
			String errorMessage = "잘못된 게시판 식별자입니다";
			throw new ServerServiceException(errorMessage);
		}
		
		UByte boardID = UByte.valueOf(boardListReq.getBoardID());

		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
			
			int total = create
			.selectCount()
			.from(SB_BOARD_TB)
			.where(SB_BOARD_TB.BOARD_ID.eq(UByte.valueOf(boardListReq.getBoardID())))
			.fetchOne(0, int.class);		
			
			Result<Record8<UInteger, UInteger, UShort, UInteger, UByte, Integer, String, Object>>  
			boardListResult = create.select(SB_BOARD_TB.BOARD_NO, 
					SB_BOARD_TB.GROUP_NO,
					SB_BOARD_TB.GROUP_SQ,
					SB_BOARD_TB.PARENT_NO,
					SB_BOARD_TB.DEPTH,					
					SB_BOARD_TB.VIEW_CNT,
					SB_BOARD_TB.BOARD_ST,										
					create.selectCount().from(SB_BOARD_VOTE_TB)
					.where(SB_BOARD_VOTE_TB.BOARD_ID.eq(SB_BOARD_TB.BOARD_ID))
					.and(SB_BOARD_VOTE_TB.BOARD_NO.eq(SB_BOARD_TB.BOARD_NO)).asField("votes"))
				.from(SB_BOARD_TB)						
			.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
			.orderBy(SB_BOARD_TB.field(SB_BOARD_TB.GROUP_NO).desc(),
					SB_BOARD_TB.field(SB_BOARD_TB.GROUP_SQ).asc())
			.offset(boardListReq.getPageOffset())
			.limit(boardListReq.getPageLength())
			.fetch();
			
			java.util.List<BoardListRes.Board> boardList = new ArrayList<BoardListRes.Board>();
			for (Record boardRecord : boardListResult) {
				UInteger boardNo = boardRecord.getValue(SB_BOARD_TB.BOARD_NO);
				
				Record3<String, Timestamp, String> 
				firstWriterBoardRecord = create.select(SB_BOARD_HISTORY_TB.MODIFIER_ID,
						SB_BOARD_HISTORY_TB.REG_DT,
						SB_MEMBER_TB.NICKNAME)
				.from(SB_BOARD_HISTORY_TB)
				.join(SB_MEMBER_TB)
				.on(SB_MEMBER_TB.USER_ID.eq(SB_BOARD_HISTORY_TB.MODIFIER_ID))
				.where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(boardID))
				.and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(boardNo))
				.and(SB_BOARD_HISTORY_TB.HISTORY_SQ.eq(UByte.valueOf(0)))
				.fetchOne();
				
				if (null == firstWriterBoardRecord) {
					String debugErrorMessage = new StringBuilder("해당 게시판의 글[boardID=")
							.append(boardID)
							.append(", boardNo=")
							.append(boardNo)
							.append("]의 최초 작성자 정보가 존재 하지 않아 목록에서 제외하였습니다")
							.toString();
					log.warn(debugErrorMessage);
					
					total--;
					continue;
					
				}
				
				
				Record1<UByte> boardHistoryMaxSequenceRecord = create.select(SB_BOARD_HISTORY_TB.HISTORY_SQ.max())
				.from(SB_BOARD_HISTORY_TB)
				.where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(boardID))
				.and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(boardNo)).fetchOne();				
				
				if (null == boardHistoryMaxSequenceRecord) {
					String debugErrorMessage = new StringBuilder().append("해당 게시판의 글[boardID=")
							.append(boardID)
							.append(", boardNo=")
							.append(boardNo)
							.append("]은  제목과 내용이 저장된 테이블(SB_BOARD_HISTORY_TB)에 미 존재하여 목록에서 제외하였습니다").toString();
					log.warn(debugErrorMessage);
					
					total--;
					continue;
				}
				
				UByte  boardHistoryMaxSequence = boardHistoryMaxSequenceRecord.value1();
				
				Record2<String, Timestamp> finalModifiedBoardHistoryRecord = create.select(SB_BOARD_HISTORY_TB.SUBJECT, SB_BOARD_HISTORY_TB.REG_DT)
				.from(SB_BOARD_HISTORY_TB)
				.where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(boardID))
				.and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(boardNo))
				.and(SB_BOARD_HISTORY_TB.HISTORY_SQ.eq(boardHistoryMaxSequence)).fetchOne();				
				
				BoardListRes.Board board = new BoardListRes.Board();				
				board.setBoardNo(boardNo.longValue());
				board.setGroupNo(boardRecord.getValue(SB_BOARD_TB.GROUP_NO).longValue());
				board.setGroupSeq(boardRecord.getValue(SB_BOARD_TB.GROUP_SQ).intValue());
				board.setParentNo(boardRecord.getValue(SB_BOARD_TB.PARENT_NO).longValue());
				board.setDepth(boardRecord.getValue(SB_BOARD_TB.DEPTH).shortValue());
				board.setWriterID(firstWriterBoardRecord.getValue(SB_BOARD_HISTORY_TB.MODIFIER_ID));
				board.setViewCount(boardRecord.getValue(SB_BOARD_TB.VIEW_CNT));
				board.setBoardSate(boardRecord.getValue(SB_BOARD_TB.BOARD_ST));
				board.setRegisteredDate(firstWriterBoardRecord.getValue(SB_BOARD_HISTORY_TB.REG_DT));
				board.setNickname(firstWriterBoardRecord.getValue(SB_MEMBER_TB.NICKNAME));				
				board.setVotes(boardRecord.getValue("votes", Integer.class));
				board.setSubject(finalModifiedBoardHistoryRecord.getValue(SB_BOARD_HISTORY_TB.SUBJECT));
				board.setFinalModifiedDate(finalModifiedBoardHistoryRecord.getValue(SB_BOARD_HISTORY_TB.REG_DT));				
				
				log.info(board.toString());
				boardList.add(board);
			}
			
			conn.commit();
			
			
			BoardListRes boardListRes = new BoardListRes();
			boardListRes.setBoardID(boardListReq.getBoardID());
			boardListRes.setPageOffset(boardListReq.getPageOffset());
			boardListRes.setPageLength(boardListReq.getPageLength());
			boardListRes.setTotal(total);
			boardListRes.setCnt(boardList.size());
			boardListRes.setBoardList(boardList);
			
			return boardListRes;
		//} catch (ServerServiceException e) {
		//	throw e;
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
	}
}
