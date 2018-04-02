package kr.pe.sinnori.impl.task.server;

import static kr.pe.sinnori.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.sinnori.impl.jooq.tables.SbBoardVoteTb.SB_BOARD_VOTE_TB;
import static kr.pe.sinnori.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;

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

import kr.pe.sinnori.common.etc.DBCPManager;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.BoardListReq.BoardListReq;
import kr.pe.sinnori.impl.message.BoardListRes.BoardListRes;
import kr.pe.sinnori.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.lib.BoardStateType;
import kr.pe.sinnori.server.lib.JooqSqlUtil;
import kr.pe.sinnori.server.lib.MemberStateType;
import kr.pe.sinnori.server.lib.ServerCommonStaticFinalVars;
import kr.pe.sinnori.server.lib.ValueChecker;
import kr.pe.sinnori.server.task.AbstractServerTask;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class BoardListReqServerTask extends AbstractServerTask {
	
	@SuppressWarnings("unused")
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
	}
	
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
	
	private void sendSuccessOutputMessageForCommit(AbstractMessage outputMessage, Connection conn,
			ToLetterCarrier toLetterCarrier) throws InterruptedException {		
		try {
			conn.commit();
		} catch (Exception e) {
			log.warn("fail to commit");
		}
		
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	@Override
	public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		doWork(projectName, personalLoginManager, toLetterCarrier, (BoardListReq)inputMessage);
	}
	
	public void doWork(String projectName, PersonalLoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			BoardListReq boardListReq) throws Exception {
		// FIXME!
		log.info(boardListReq.toString());
		
		try {
			ValueChecker.checkValidBoardId(boardListReq.getBoardId());
		} catch(IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardListReq);
			return;
		}
		
		BoardListRes boardListRes = new BoardListRes();
		boardListRes.setBoardId(boardListReq.getBoardId());
		boardListRes.setStartNo(boardListReq.getStartNo());
		boardListRes.setPageSize(boardListReq.getPageSize());

		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
			
			int total = create
			.selectCount()
			.from(SB_BOARD_TB).join(SB_MEMBER_TB)
			.on(SB_MEMBER_TB.USER_ID.eq(SB_BOARD_TB.WRITER_ID))
			.where(SB_BOARD_TB.BOARD_ID.eq(UByte.valueOf(boardListReq.getBoardId())))
			.and(SB_MEMBER_TB.MEMBER_ST.eq(MemberStateType.OK.getValue()))
			.and(SB_BOARD_TB.DEL_FL.eq(BoardStateType.NO.getValue())).fetchOne(0, int.class);
	
			boardListRes.setTotal(total);
			
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
					SB_BOARD_TB.DEL_FL,
					SB_BOARD_TB.IP,
					SB_BOARD_TB.REG_DT,
					SB_BOARD_TB.MOD_DT).from(SB_BOARD_TB.useIndex("tw_board_02_idx"))
			.where(SB_BOARD_TB.BOARD_ID.eq(UByte.valueOf(boardListReq.getBoardId())))
			.and(SB_BOARD_TB.GROUP_NO.gt(UInteger.valueOf(0)))
			.and(SB_BOARD_TB.DEL_FL.eq(BoardStateType.NO.getValue()))
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
					SB_BOARD_TB.DEL_FL,
					SB_BOARD_TB.IP,
					SB_BOARD_TB.REG_DT,
					SB_BOARD_TB.MOD_DT)
			.from(a)
			.join(SB_BOARD_TB).on(a.field(SB_BOARD_TB.BOARD_NO).eq(SB_BOARD_TB.BOARD_NO))
			.asTable("c");			
			
			Table<Record16<UByte, UInteger, UInteger, UShort, UInteger, UByte, String, String, Integer, String, String, Timestamp, Timestamp, String, Byte, Byte>> 
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
					c.field(SB_BOARD_TB.DEL_FL),
					c.field(SB_BOARD_TB.IP),
					c.field(SB_BOARD_TB.REG_DT),
					c.field(SB_BOARD_TB.MOD_DT),
					SB_MEMBER_TB.NICKNAME,
					SB_MEMBER_TB.MEMBER_GB,
					SB_MEMBER_TB.MEMBER_ST)
			.from(c)
			.join(SB_MEMBER_TB)
			.on(c.field(SB_BOARD_TB.WRITER_ID).eq(SB_MEMBER_TB.USER_ID)).asTable("t");
			
			Result<Record17<UByte, UInteger, UInteger, UShort, UInteger, UByte, String, String, Integer, String, String, Timestamp, Timestamp, String, Byte, String, Integer>>
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
					t.field(SB_BOARD_TB.DEL_FL),
					t.field(SB_BOARD_TB.IP),
					t.field(SB_BOARD_TB.REG_DT),
					t.field(SB_BOARD_TB.MOD_DT),
					JooqSqlUtil.getFieldOfMemberGbNm(t.field(SB_MEMBER_TB.MEMBER_GB)).as("member_gb_nm"),
					t.field(SB_MEMBER_TB.MEMBER_ST),
					t.field(SB_MEMBER_TB.NICKNAME),
					DSL.count(SB_BOARD_VOTE_TB.BOARD_NO).as("votes")
			) .from(t).leftJoin(SB_BOARD_VOTE_TB)
			.on(t.field(SB_BOARD_TB.BOARD_NO).eq(SB_BOARD_VOTE_TB.BOARD_NO))
			.groupBy(t.field(SB_BOARD_TB.BOARD_NO))
			.orderBy(t.field(SB_BOARD_TB.GROUP_NO).desc(),
					t.field(SB_BOARD_TB.GROUP_SQ).asc()).fetch();
			
			
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
					board.setDeleteFlag(r.get(SB_BOARD_TB.DEL_FL));
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
			

			boardListRes.setCnt(boardList.size());
			boardListRes.setBoardList(boardList);
			
			// log.info(boardListRes.toString());
			
			sendSuccessOutputMessageForCommit(boardListRes, conn, toLetterCarrier);
			return;
		} catch (Exception e) {
			log.warn("unknown error", e);
			sendErrorOutputMessageForRollback("게시판 조회가 실패하였습니다", conn, toLetterCarrier, boardListReq);
			return;

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
