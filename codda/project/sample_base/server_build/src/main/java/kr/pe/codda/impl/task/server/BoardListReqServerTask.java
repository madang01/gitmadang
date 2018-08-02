package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardVoteTb.SB_BOARD_VOTE_TB;
import static kr.pe.codda.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record13;
import org.jooq.Record16;
import org.jooq.Record17;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.Table;
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
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.JooqSqlUtil;
import kr.pe.codda.server.lib.MemberStateType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardListReqServerTask extends AbstractServerTask {

	/*@SuppressWarnings("unused")
	private void sendErrorOutputtMessageForCommit(String errorMessage,
			Connection conn,			
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		try {
			conn.commit();
		} catch (Exception e) {
			log.warn("fail to commit");
		}
		sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
	}
	
	
	private void sendErrorOutputMessageForRollback(String errorMessage,
			Connection conn,			
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		log.warn("{}, inObj={}", errorMessage, inputMessage.toString());
		if (null != conn) {
			try {
				conn.rollback();
			} catch (Exception e) {
				log.warn("fail to rollback");
			}
		}		
		sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
	}*/
	
	private void sendErrorOutputMessage(String errorMessage,			
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		log.warn("{}, inObj={}", errorMessage, inputMessage.toString());
		
		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(inputMessage.getMessageID());
		messageResultRes.setIsSuccess(false);		
		messageResultRes.setResultMessage(errorMessage);
		toLetterCarrier.addSyncOutputMessage(messageResultRes);
	}
	
	/*private void sendSuccessOutputMessageForCommit(AbstractMessage outputMessage, Connection conn,
			ToLetterCarrier toLetterCarrier) throws InterruptedException {		
		try {
			conn.commit();
		} catch (Exception e) {
			log.warn("fail to commit");
		}
		
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}*/

	@Override
	public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		try {
			AbstractMessage outputMessage = doService((BoardListReq)inputMessage);
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
						
			sendErrorOutputMessage("회원 가입이 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}
	
	public BoardListRes doService(BoardListReq boardListReq) throws Exception {
		// FIXME!
		log.info(boardListReq.toString());
		
		try {
			ValueChecker.checkValidBoardId(boardListReq.getBoardId());
		} catch(IllegalArgumentException e) {
			/*log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardListReq);
			return;*/
			String errorMessage = "잘못된 게시판 식별자입니다";
			throw new ServerServiceException(errorMessage);
		}
		
		

		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
			
			final int total = create
			.selectCount()
			.from(SB_BOARD_TB).join(SB_MEMBER_TB)
			.on(SB_MEMBER_TB.USER_ID.eq(SB_BOARD_TB.WRITER_ID))
			.where(SB_BOARD_TB.BOARD_ID.eq(UByte.valueOf(boardListReq.getBoardId())))
			.and(SB_MEMBER_TB.MEMBER_ST.eq(MemberStateType.OK.getValue()))
			.and(SB_BOARD_TB.BOARD_ST.eq(BoardStateType.OK.getValue())).fetchOne(0, int.class);
	
			
			
			Table<Record13<UByte, UInteger, UInteger, UShort, UInteger, UByte, String, String, Integer, String, String, Timestamp, Timestamp>>  
			a =	create.select(
					SB_BOARD_TB.BOARD_ID,
					SB_BOARD_TB.BOARD_NO,
					SB_BOARD_TB.GROUP_NO,
					SB_BOARD_TB.GROUP_SQ,
					SB_BOARD_TB.PARENT_NO,
					SB_BOARD_TB.DEPTH,
					SB_BOARD_TB.SUBJECT,
					SB_BOARD_TB.WRITER_ID,
					SB_BOARD_TB.VIEW_CNT,
					SB_BOARD_TB.BOARD_ST,
					SB_BOARD_TB.IP,
					SB_BOARD_TB.REG_DT,
					SB_BOARD_TB.MOD_DT).from(SB_BOARD_TB.useIndex("sb_board_idx1"))
			.where(SB_BOARD_TB.BOARD_ID.eq(UByte.valueOf(boardListReq.getBoardId())))
			.and(SB_BOARD_TB.GROUP_NO.gt(UInteger.valueOf(0)))
			.and(SB_BOARD_TB.BOARD_ST.eq(BoardStateType.OK.getValue()))
			.orderBy(SB_BOARD_TB.field(SB_BOARD_TB.GROUP_NO).desc(),
					SB_BOARD_TB.field(SB_BOARD_TB.GROUP_SQ).asc())
			.offset(boardListReq.getStartNo())
			.limit(boardListReq.getPageSize())
			.asTable("a");		
			
						
			Table<Record13<UByte, UInteger, UInteger, UShort, UInteger, UByte, String, String, Integer, String, String, Timestamp, Timestamp>> 
			c =	create.select(
					SB_BOARD_TB.BOARD_ID,
					SB_BOARD_TB.BOARD_NO,
					SB_BOARD_TB.GROUP_NO,
					SB_BOARD_TB.GROUP_SQ,
					SB_BOARD_TB.PARENT_NO,
					SB_BOARD_TB.DEPTH,
					SB_BOARD_TB.SUBJECT,
					SB_BOARD_TB.WRITER_ID,
					SB_BOARD_TB.VIEW_CNT,
					SB_BOARD_TB.BOARD_ST,
					SB_BOARD_TB.IP,
					SB_BOARD_TB.REG_DT,
					SB_BOARD_TB.MOD_DT)
			.from(a)
			.join(SB_BOARD_TB).on(a.field(SB_BOARD_TB.BOARD_NO).eq(SB_BOARD_TB.BOARD_NO))
			.asTable("c");			
			
			Table<Record16<UByte, UInteger, UInteger, UShort, UInteger, UByte, String, String, Integer, String, String, Timestamp, Timestamp, String, String, String>> 
			t =	create.select(
					c.field(SB_BOARD_TB.BOARD_ID),
					c.field(SB_BOARD_TB.BOARD_NO),					
					c.field(SB_BOARD_TB.GROUP_NO),
					c.field(SB_BOARD_TB.GROUP_SQ),
					c.field(SB_BOARD_TB.PARENT_NO),
					c.field(SB_BOARD_TB.DEPTH),
					c.field(SB_BOARD_TB.SUBJECT),
					c.field(SB_BOARD_TB.WRITER_ID),
					c.field(SB_BOARD_TB.VIEW_CNT),
					c.field(SB_BOARD_TB.BOARD_ST),
					c.field(SB_BOARD_TB.IP),
					c.field(SB_BOARD_TB.REG_DT),
					c.field(SB_BOARD_TB.MOD_DT),
					SB_MEMBER_TB.NICKNAME,
					SB_MEMBER_TB.MEMBER_TYPE,
					SB_MEMBER_TB.MEMBER_ST)
			.from(c)
			.join(SB_MEMBER_TB)
			.on(c.field(SB_BOARD_TB.WRITER_ID).eq(SB_MEMBER_TB.USER_ID)).asTable("t");
			
			Result<Record17<UByte, UInteger, UInteger, UShort, UInteger, UByte, String, String, Integer, String, String, Timestamp, Timestamp, String, String, String, Integer>>
			boardListResult = create.select(
					t.field(SB_BOARD_TB.BOARD_ID),
					t.field(SB_BOARD_TB.BOARD_NO),	
					t.field(SB_BOARD_TB.GROUP_NO),
					t.field(SB_BOARD_TB.GROUP_SQ),
					t.field(SB_BOARD_TB.PARENT_NO),
					t.field(SB_BOARD_TB.DEPTH),
					t.field(SB_BOARD_TB.SUBJECT),
					t.field(SB_BOARD_TB.WRITER_ID),
					t.field(SB_BOARD_TB.VIEW_CNT),
					t.field(SB_BOARD_TB.BOARD_ST),
					t.field(SB_BOARD_TB.IP),
					t.field(SB_BOARD_TB.REG_DT),
					t.field(SB_BOARD_TB.MOD_DT),
					JooqSqlUtil.getFieldOfMemberGbNm(t.field(SB_MEMBER_TB.MEMBER_TYPE)).as("member_gb_nm"),
					t.field(SB_MEMBER_TB.MEMBER_ST),
					t.field(SB_MEMBER_TB.NICKNAME),
					DSL.count(SB_BOARD_VOTE_TB.BOARD_NO).as("votes")
			) .from(t).leftJoin(SB_BOARD_VOTE_TB)
			.on(t.field(SB_BOARD_TB.BOARD_NO).eq(SB_BOARD_VOTE_TB.BOARD_NO))
			.groupBy(t.field(SB_BOARD_TB.BOARD_NO))
			.orderBy(t.field(SB_BOARD_TB.GROUP_NO).desc(),
					t.field(SB_BOARD_TB.GROUP_SQ).asc()).fetch();
			
			
			
			conn.commit();
			
			
			java.util.List<BoardListRes.Board> boardList = new ArrayList<BoardListRes.Board>();
			  
			if (null != boardListResult) {
				for (Record r : boardListResult) {
					BoardListRes.Board board = new BoardListRes.Board();				
					board.setBoardNo(r.getValue(SB_BOARD_TB.BOARD_NO).longValue());
					board.setGroupNo(r.getValue(SB_BOARD_TB.GROUP_NO).longValue());
					board.setGroupSeq(r.getValue(SB_BOARD_TB.GROUP_SQ).intValue());
					board.setParentNo(r.getValue(SB_BOARD_TB.PARENT_NO).longValue());
					board.setDepth(r.getValue(SB_BOARD_TB.DEPTH).shortValue());
					board.setSubject(r.get(SB_BOARD_TB.SUBJECT));
					board.setWriterId(r.get(SB_BOARD_TB.WRITER_ID));
					board.setViewCount(r.get(SB_BOARD_TB.VIEW_CNT));
					board.setBoardSate(r.get(SB_BOARD_TB.BOARD_ST));
					board.setRegisterDate(r.get(SB_BOARD_TB.REG_DT));
					board.setModifiedDate(r.get(SB_BOARD_TB.MOD_DT));
					board.setVotes(r.get("votes", Integer.class));
					board.setNickname(r.get(SB_MEMBER_TB.NICKNAME));
					board.setMemberGubunName(r.get("member_gb_nm", String.class));	
					
					// FIXME!
					
					log.info(board.toString());
					boardList.add(board);
				}
			}
			
			BoardListRes boardListRes = new BoardListRes();
			boardListRes.setBoardId(boardListReq.getBoardId());
			boardListRes.setStartNo(boardListReq.getStartNo());
			boardListRes.setPageSize(boardListReq.getPageSize());
			boardListRes.setTotal(total);
			boardListRes.setCnt(boardList.size());
			boardListRes.setBoardList(boardList);
			// log.info(boardListRes.toString());
			
			// sendSuccessOutputMessageForCommit(boardListRes, conn, toLetterCarrier);
			return boardListRes;
		/*} catch (ServerServiceException e) {
			throw e;*/
		} catch (Exception e) {
			/*log.warn("unknown error", e);
			sendErrorOutputMessageForRollback("게시판 조회가 실패하였습니다", conn, toLetterCarrier, boardListReq);
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
				} catch(Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}
	}
}
