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
import org.jooq.Record14;
import org.jooq.Record15;
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
import kr.pe.sinnori.server.lib.DeleteFlag;
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
			.and(SB_BOARD_TB.DEL_FL.eq(DeleteFlag.NO.getValue())).fetchOne(0, int.class);
	
			boardListRes.setTotal(total);
			
			// outObj.setCnt(resultOfBoardList.size());
			/*select t.*, count(SB_BOARD_VOTE_TB.user_id) as votes from SB_BOARD_VOTE_TB right join (
			select t.*
			from SB_BOARD_TB join  (
				select
					SB_BOARD_TB.*, 
					SB_MEMBER_TB.nickname, 		
					if (SB_MEMBER_TB.member_gb = 1, '일반회원', if (SB_MEMBER_TB.member_gb = 0, '관리자', '알수없음')) as member_gb_nm
				from SB_BOARD_TB, SB_MEMBER_TB
				where SB_BOARD_TB.board_id = #{boardId}
				and SB_BOARD_TB.writer_id = SB_MEMBER_TB.user_id
				and SB_MEMBER_TB.member_st=0 and SB_BOARD_TB.del_fl = 'N'
			) as t on SB_BOARD_TB.board_no=t.board_no	
		) as t on SB_BOARD_VOTE_TB.board_no = t.board_no
		group by t.board_no
		order by t.group_no desc, t.group_sq asc
		limit #{startNo}, #{pageSize}*/

			// Result<Record3<UByte, Long, Integer>> resultOfBoardList =
			
			
			Table<Record14<UByte, UInteger, UInteger, UShort, UInteger, UByte, String, String, Integer, String, Timestamp, Timestamp, String, Byte>> nested = create
			.select(SB_BOARD_TB.BOARD_ID,
					SB_BOARD_TB.BOARD_NO,
					SB_BOARD_TB.GROUP_NO,
					SB_BOARD_TB.GROUP_SQ,
					SB_BOARD_TB.PARENT_NO,
					SB_BOARD_TB.DEPTH,
					SB_BOARD_TB.SUBJECT,
					SB_BOARD_TB.WRITER_ID,
					SB_BOARD_TB.VIEW_CNT,
					SB_BOARD_TB.DEL_FL,
					SB_BOARD_TB.REG_DT,
					SB_BOARD_TB.MOD_DT,
					SB_MEMBER_TB.NICKNAME,
					SB_MEMBER_TB.MEMBER_GB)
			.from(SB_BOARD_TB).join(SB_MEMBER_TB)
			.on(SB_BOARD_TB.WRITER_ID.eq(SB_MEMBER_TB.USER_ID))
			.where(SB_BOARD_TB.BOARD_ID.eq(UByte.valueOf(boardListReq.getBoardId())))
			.and(SB_MEMBER_TB.MEMBER_ST.eq(MemberStateType.OK.getValue()))
			.and(SB_BOARD_TB.DEL_FL.eq(DeleteFlag.NO.getValue())).asTable("nested");
			
			 
			Result<Record15<UByte, UInteger, UInteger, UShort, UInteger, UByte, String, String, Integer, String, Timestamp, Timestamp, Integer, String, String>>  
			resultOfBoardList = create.select(nested.field(SB_BOARD_TB.BOARD_ID),
					nested.field(SB_BOARD_TB.BOARD_NO),
					nested.field(SB_BOARD_TB.GROUP_NO),
					nested.field(SB_BOARD_TB.GROUP_SQ),
					nested.field(SB_BOARD_TB.PARENT_NO),
					nested.field(SB_BOARD_TB.DEPTH),
					nested.field(SB_BOARD_TB.SUBJECT),
					nested.field(SB_BOARD_TB.WRITER_ID),
					nested.field(SB_BOARD_TB.VIEW_CNT),
					nested.field(SB_BOARD_TB.DEL_FL),
					nested.field(SB_BOARD_TB.REG_DT),
					nested.field(SB_BOARD_TB.MOD_DT),
					DSL.count(SB_BOARD_VOTE_TB.BOARD_NO).as("votes"),
					nested.field(SB_MEMBER_TB.NICKNAME),
					JooqSqlUtil.getFieldOfMemberGbNm(nested.field(SB_MEMBER_TB.MEMBER_GB)).as("member_gb_nm"))
			.from(nested)
			.leftJoin(SB_BOARD_VOTE_TB)
			.on(nested.field(SB_BOARD_TB.BOARD_NO).eq(SB_BOARD_VOTE_TB.BOARD_NO))
			.groupBy(nested.field(SB_BOARD_TB.BOARD_NO))
			.orderBy(nested.field(SB_BOARD_TB.BOARD_NO).desc(),
					nested.field(SB_BOARD_TB.GROUP_SQ).asc())
			.limit(boardListReq.getPageSize())
			.offset((int)boardListReq.getStartNo())
			.fetch();
			
			// log.info("sql={}", sql);
						// outObj.setCnt(resultOfBoardList.size());
			
			java.util.List<BoardListRes.Board> boardList = new ArrayList<BoardListRes.Board>();
			  
			if (null != resultOfBoardList) {
				for (Record r : resultOfBoardList) {
					BoardListRes.Board board = new BoardListRes.Board();				
					board.setBoardNo(r.getValue(nested.field(SB_BOARD_TB.BOARD_NO)).longValue());
					board.setGroupNo(r.getValue(nested.field(SB_BOARD_TB.GROUP_NO)).longValue());
					board.setGroupSeq(r.getValue(nested.field(SB_BOARD_TB.GROUP_SQ)).intValue());
					board.setParentNo(r.getValue(nested.field(SB_BOARD_TB.PARENT_NO)).longValue());
					board.setDepth(r.getValue(nested.field(SB_BOARD_TB.DEPTH)).shortValue());
					board.setSubject(r.get(nested.field(SB_BOARD_TB.SUBJECT)));
					board.setWriterId(r.get(nested.field(SB_BOARD_TB.WRITER_ID)));
					board.setViewCount(r.get(nested.field(SB_BOARD_TB.VIEW_CNT)));
					board.setDeleteFlag(r.get(nested.field(SB_BOARD_TB.DEL_FL)));
					board.setRegisterDate(r.get(nested.field(SB_BOARD_TB.REG_DT)));
					board.setModifiedDate(r.get(nested.field(SB_BOARD_TB.MOD_DT)));
					board.setVotes(r.get("votes", Integer.class));
					board.setNickname(r.get(nested.field(SB_MEMBER_TB.NICKNAME)));
					board.setMemberGubunName(r.get("member_gb_nm", String.class));
					
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
