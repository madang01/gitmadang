package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
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
import org.jooq.Record3;
import org.jooq.Record8;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.jooq.tables.SbBoardHistoryTb;
import kr.pe.codda.impl.jooq.tables.SbBoardTb;
import kr.pe.codda.impl.message.BoardListReq.BoardListReq;
import kr.pe.codda.impl.message.BoardListRes.BoardListRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardListReqServerTask extends AbstractServerTask {

	public BoardListReqServerTask() throws DynamicClassCallException {
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
					(BoardListReq) inputMessage);
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

	public BoardListRes doWork(String dbcpName, BoardListReq boardListReq) throws Exception {
		// FIXME!
		log.info(boardListReq.toString());

		try {
			ValueChecker.checkValidBoardID(boardListReq.getBoardID());
		} catch (IllegalArgumentException e) {
			String errorMessage = "잘못된 게시판 식별자입니다";
			throw new ServerServiceException(errorMessage);
		}

		final String requestedUserID = boardListReq.getRequestedUserID();

		final UByte boardID = UByte.valueOf(boardListReq.getBoardID());
		final int pageNo = boardListReq.getPageNo();
		final int pageSize = boardListReq.getPageSize();
		final int offset = (pageNo - 1) * pageSize;

		int total = 0;
		java.util.List<BoardListRes.Board> boardList = new ArrayList<BoardListRes.Board>();

		DataSource dataSource = DBCPManager.getInstance().getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));

			String memberRoleOfRequestedUserID = ValueChecker.checkValidRequestedUserState(conn, create, log,
					requestedUserID);

			MemberRoleType memberRoleTypeOfRequestedUserID = null;
			try {
				memberRoleTypeOfRequestedUserID = MemberRoleType.valueOf(memberRoleOfRequestedUserID, false);
			} catch (IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("게시판 목록 요청자[").append(requestedUserID).append("]의 멤버 구분[")
						.append(memberRoleOfRequestedUserID).append("]이 잘못되었습니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			Table<Record8<UByte, UInteger, UInteger, UShort, UInteger, UByte, Integer, String>> mainTable = null;

			if (memberRoleTypeOfRequestedUserID.equals(MemberRoleType.ADMIN)) {
				total = create.select(SB_BOARD_INFO_TB.ADMIN_TOTAL).from(SB_BOARD_INFO_TB)
						.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).fetchOne(0, Integer.class);

				SbBoardTb a = SB_BOARD_TB.as("a");

				Table<Record3<UByte, UInteger, UShort>> b = create.select(a.BOARD_ID, a.GROUP_NO, a.GROUP_SQ)
						.from(a.forceIndex("sb_board_idx1")).where(a.BOARD_ID.eq(boardID))
						.orderBy(a.GROUP_NO.desc(), a.GROUP_SQ.desc())
						.offset(offset).limit(pageSize).asTable("b");

				mainTable = create
						.select(a.BOARD_ID, a.BOARD_NO, a.GROUP_NO, a.GROUP_SQ, a.PARENT_NO, a.DEPTH, a.VIEW_CNT,
								a.BOARD_ST)
						.from(a).innerJoin(b).on(a.BOARD_ID.eq(b.field(SB_BOARD_TB.BOARD_ID)))
						.and(a.GROUP_NO.eq(b.field(SB_BOARD_TB.GROUP_NO)))
						.and(a.GROUP_SQ.eq(b.field(SB_BOARD_TB.GROUP_SQ))).asTable("a");

			} else {
				total = create.select(SB_BOARD_INFO_TB.USER_TOTAL).from(SB_BOARD_INFO_TB)
						.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).fetchOne(0, Integer.class);

				SbBoardTb a = SB_BOARD_TB.as("a");

				Table<Record3<UByte, UInteger, UShort>> b = create.select(a.BOARD_ID, a.GROUP_NO, a.GROUP_SQ)
						.from(a.forceIndex("sb_board_idx1")).where(a.BOARD_ID.eq(boardID))
						.and(a.BOARD_ST.eq(BoardStateType.OK.getValue())).orderBy(a.GROUP_NO.desc(), a.GROUP_SQ.desc())
						.offset(offset).limit(pageSize).asTable("b");

				mainTable = create
						.select(a.BOARD_ID, a.BOARD_NO, a.GROUP_NO, a.GROUP_SQ, a.PARENT_NO, a.DEPTH, a.VIEW_CNT,
								a.BOARD_ST)
						.from(a).innerJoin(b).on(a.BOARD_ID.eq(b.field(SB_BOARD_TB.BOARD_ID)))
						.and(a.GROUP_NO.eq(b.field(SB_BOARD_TB.GROUP_NO)))
						.and(a.GROUP_SQ.eq(b.field(SB_BOARD_TB.GROUP_SQ))).asTable("a");
			}

			SbBoardHistoryTb b = SB_BOARD_HISTORY_TB.as("b");
			SbBoardHistoryTb c = SB_BOARD_HISTORY_TB.as("c");

			Result<Record13<UInteger, UInteger, UShort, UInteger, UByte, Integer, String, Object, String, Timestamp, String, Object, Timestamp>> boardListResult = create
					.select(mainTable.field(SB_BOARD_TB.BOARD_NO), mainTable.field(SB_BOARD_TB.GROUP_NO),
							mainTable.field(SB_BOARD_TB.GROUP_SQ), mainTable.field(SB_BOARD_TB.PARENT_NO),
							mainTable.field(SB_BOARD_TB.DEPTH), mainTable.field(SB_BOARD_TB.VIEW_CNT),
							mainTable.field(SB_BOARD_TB.BOARD_ST),
							create.selectCount().from(SB_BOARD_VOTE_TB)
									.where(SB_BOARD_VOTE_TB.BOARD_ID.eq(mainTable.field(SB_BOARD_TB.BOARD_ID)))
									.and(SB_BOARD_VOTE_TB.BOARD_NO.eq(mainTable.field(SB_BOARD_TB.BOARD_NO)))
									.asField("votes"),
							b.SUBJECT, b.REG_DT.as("last_mod_date"), c.REGISTRANT_ID,
							create.select(SB_MEMBER_TB.NICKNAME).from(SB_MEMBER_TB)
									.where(SB_MEMBER_TB.USER_ID.eq(c.REGISTRANT_ID))
									.asField(SB_MEMBER_TB.NICKNAME.getName()),
							c.REG_DT.as("first_reg_date"))
					.from(mainTable)
					.innerJoin(c).on(c.BOARD_ID.eq(mainTable.field(SB_BOARD_TB.BOARD_ID)))
						.and(c.BOARD_NO.eq(mainTable.field(SB_BOARD_TB.BOARD_NO)))
						.and(c.HISTORY_SQ.eq(UByte.valueOf(0)))
					.innerJoin(b).on(b.BOARD_ID.eq(mainTable.field(SB_BOARD_TB.BOARD_ID)))
						.and(b.BOARD_NO.eq(mainTable.field(SB_BOARD_TB.BOARD_NO)))
						.and(b.HISTORY_SQ.eq(create.select(b.HISTORY_SQ.max()).from(b)
							.where(b.BOARD_ID.eq(mainTable.field(SB_BOARD_TB.BOARD_ID)))
							.and(b.BOARD_NO.eq(mainTable.field(SB_BOARD_TB.BOARD_NO)))))
					.orderBy(mainTable.field(SB_BOARD_TB.GROUP_NO).desc(), mainTable.field(SB_BOARD_TB.GROUP_SQ).desc())
					.fetch();

			for (Record boardRecord : boardListResult) {
				UInteger boardNo = boardRecord.getValue(SB_BOARD_TB.BOARD_NO);
				UInteger groupNo = boardRecord.getValue(SB_BOARD_TB.GROUP_NO);
				UShort groupSequence = boardRecord.getValue(SB_BOARD_TB.GROUP_SQ);
				UInteger parentNo = boardRecord.getValue(SB_BOARD_TB.PARENT_NO);
				UByte depth = boardRecord.getValue(SB_BOARD_TB.DEPTH);
				int viewCount = boardRecord.getValue(SB_BOARD_TB.VIEW_CNT);
				String nativeBoardState = boardRecord.getValue(SB_BOARD_TB.BOARD_ST);
				int votes = boardRecord.getValue("votes", Integer.class);
				String subject = boardRecord.getValue(SB_BOARD_HISTORY_TB.SUBJECT);
				Timestamp lastModifiedDate = boardRecord.getValue("last_mod_date", Timestamp.class);
				String firstWriterID = boardRecord.getValue(SB_BOARD_HISTORY_TB.REGISTRANT_ID);
				String firstWriterNickName = boardRecord.getValue(SB_MEMBER_TB.NICKNAME);
				Timestamp firstRegisteredDate = boardRecord.getValue("first_reg_date", Timestamp.class);

				Record3<String, Timestamp, String> firstBoardHistoryRecord = create
						.select(SB_BOARD_HISTORY_TB.REGISTRANT_ID, SB_BOARD_HISTORY_TB.REG_DT, SB_MEMBER_TB.NICKNAME)
						.from(SB_BOARD_HISTORY_TB).join(SB_MEMBER_TB)
						.on(SB_MEMBER_TB.USER_ID.eq(SB_BOARD_HISTORY_TB.REGISTRANT_ID))
						.where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(boardNo))
						.and(SB_BOARD_HISTORY_TB.HISTORY_SQ.eq(UByte.valueOf(0))).fetchOne();

				if (null == firstBoardHistoryRecord) {
					String debugErrorMessage = new StringBuilder("해당 게시판의 글[boardID=").append(boardID)
							.append(", boardNo=").append(boardNo).append("]의 최초 작성자 정보가 존재 하지 않아 목록에서 제외하였습니다")
							.toString();
					log.warn(debugErrorMessage);

					total--;
					continue;
				}
				

				BoardListRes.Board board = new BoardListRes.Board();
				board.setBoardNo(boardNo.longValue());
				board.setGroupNo(groupNo.longValue());
				board.setGroupSeq(groupSequence.intValue());
				board.setParentNo(parentNo.longValue());
				board.setDepth(depth.shortValue());
				board.setWriterID(firstWriterID);
				board.setViewCount(viewCount);
				board.setBoardSate(nativeBoardState);
				board.setRegisteredDate(firstRegisteredDate);
				board.setNickname(firstWriterNickName);
				board.setVotes(votes);
				board.setSubject(subject);
				board.setLastModifiedDate(lastModifiedDate);

				// log.info(board.toString());
				boardList.add(board);
			}

			conn.commit();

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

		BoardListRes boardListRes = new BoardListRes();
		boardListRes.setBoardID(boardID.shortValue());
		boardListRes.setPageNo(pageNo);
		boardListRes.setPageSize(pageSize);
		boardListRes.setTotal(total);
		boardListRes.setCnt(boardList.size());
		boardListRes.setBoardList(boardList);

		return boardListRes;
	}
}
