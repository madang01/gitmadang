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
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.MemberType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
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
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (BoardListReq) inputMessage);
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

		final String requestUserID = boardListReq.getRequestUserID();

		final UByte boardID = UByte.valueOf(boardListReq.getBoardID());
		final int pageNo = boardListReq.getPageNo();
		final int pageSize = boardListReq.getPageSize();
		final int offset = (pageNo - 1) * pageSize;

		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));
			create.setSchema(dbcpName);

			String nativeMemberType = ValueChecker.checkValidMemberStateForUserID(conn, create, log, requestUserID);

			MemberType meberType = null;
			try {
				meberType = MemberType.valueOf(nativeMemberType, false);
			} catch (IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("게시판 목록 요청자[").append(requestUserID).append("]의 멤버 구분[")
						.append(nativeMemberType).append("]이 잘못되었습니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			int total = 0;
			java.util.List<BoardListRes.Board> boardList = new ArrayList<BoardListRes.Board>();
			Result<Record8<UInteger, UInteger, UShort, UInteger, UByte, Integer, String, Object>> boardListResult = null;

			if (meberType.equals(MemberType.ADMIN)) {
				total = create.select(SB_BOARD_INFO_TB.ADMIN_TOTAL).from(SB_BOARD_INFO_TB)
						.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).fetchOne(0, Integer.class);

				boardListResult = create
						.select(SB_BOARD_TB.BOARD_NO, SB_BOARD_TB.GROUP_NO, SB_BOARD_TB.GROUP_SQ, SB_BOARD_TB.PARENT_NO,
								SB_BOARD_TB.DEPTH, SB_BOARD_TB.VIEW_CNT, SB_BOARD_TB.BOARD_ST,
								create.selectCount().from(SB_BOARD_VOTE_TB)
										.where(SB_BOARD_VOTE_TB.BOARD_ID.eq(SB_BOARD_TB.BOARD_ID))
										.and(SB_BOARD_VOTE_TB.BOARD_NO.eq(SB_BOARD_TB.BOARD_NO)).asField("votes"))
						.from(SB_BOARD_TB)
						.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
						.orderBy(SB_BOARD_TB.field(SB_BOARD_TB.GROUP_NO).desc(),
								SB_BOARD_TB.field(SB_BOARD_TB.GROUP_SQ).asc())
						.offset(offset).limit(pageSize).fetch();
			} else {
				total = create.select(SB_BOARD_INFO_TB.USER_TOTAL).from(SB_BOARD_INFO_TB)
						.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).fetchOne(0, Integer.class);

				boardListResult = create
						.select(SB_BOARD_TB.BOARD_NO, SB_BOARD_TB.GROUP_NO, SB_BOARD_TB.GROUP_SQ, SB_BOARD_TB.PARENT_NO,
								SB_BOARD_TB.DEPTH, SB_BOARD_TB.VIEW_CNT, SB_BOARD_TB.BOARD_ST,
								create.selectCount().from(SB_BOARD_VOTE_TB)
										.where(SB_BOARD_VOTE_TB.BOARD_ID.eq(SB_BOARD_TB.BOARD_ID))
										.and(SB_BOARD_VOTE_TB.BOARD_NO.eq(SB_BOARD_TB.BOARD_NO)).asField("votes"))
						.from(SB_BOARD_TB)
						.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
						.and(SB_BOARD_TB.BOARD_ST.eq(BoardStateType.OK.getValue()))
						.orderBy(SB_BOARD_TB.field(SB_BOARD_TB.GROUP_NO).desc(),
								SB_BOARD_TB.field(SB_BOARD_TB.GROUP_SQ).asc())
						.offset(offset).limit(pageSize).fetch();
			}

			for (Record boardRecord : boardListResult) {
				UInteger boardNo = boardRecord.getValue(SB_BOARD_TB.BOARD_NO);
				UInteger groupNo = boardRecord.getValue(SB_BOARD_TB.GROUP_NO);
				UShort groupSequence = boardRecord.getValue(SB_BOARD_TB.GROUP_SQ);
				UInteger parentNo = boardRecord.getValue(SB_BOARD_TB.PARENT_NO);
				UByte depth = boardRecord.getValue(SB_BOARD_TB.DEPTH);
				int viewCount = boardRecord.getValue(SB_BOARD_TB.VIEW_CNT);
				String  nativeBoardState = boardRecord.getValue(SB_BOARD_TB.BOARD_ST);
				int votes = boardRecord.getValue("votes", Integer.class);

				Record3<String, Timestamp, String> firstBoardHistoryRecord = create
						.select(SB_BOARD_HISTORY_TB.MODIFIER_ID, SB_BOARD_HISTORY_TB.REG_DT, SB_MEMBER_TB.NICKNAME)
						.from(SB_BOARD_HISTORY_TB).join(SB_MEMBER_TB)
						.on(SB_MEMBER_TB.USER_ID.eq(SB_BOARD_HISTORY_TB.MODIFIER_ID))
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
				
				String firstWriterID = firstBoardHistoryRecord.getValue(SB_BOARD_HISTORY_TB.MODIFIER_ID);
				Timestamp  firstRegisteredDate = firstBoardHistoryRecord.getValue(SB_BOARD_HISTORY_TB.REG_DT);
				String firstWriterNickName = firstBoardHistoryRecord.getValue(SB_MEMBER_TB.NICKNAME);
				

				Record2<String, Timestamp> lastBoardHistoryRecord = create
						.select(SB_BOARD_HISTORY_TB.SUBJECT, SB_BOARD_HISTORY_TB.REG_DT).from(SB_BOARD_HISTORY_TB)
						.where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(boardNo))
						.and(SB_BOARD_HISTORY_TB.HISTORY_SQ.eq(create.select(SB_BOARD_HISTORY_TB.HISTORY_SQ.max())
								.from(SB_BOARD_HISTORY_TB).where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(boardID))
								.and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(boardNo)))).fetchOne();
				
				String lastModifiedSubject = lastBoardHistoryRecord.getValue(SB_BOARD_HISTORY_TB.SUBJECT);
				Timestamp lastModifiedDate = lastBoardHistoryRecord.getValue(SB_BOARD_HISTORY_TB.REG_DT);

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
				board.setSubject(lastModifiedSubject);
				board.setLastModifiedDate(lastModifiedDate);

				// log.info(board.toString());
				boardList.add(board);
			}

			conn.commit();

			BoardListRes boardListRes = new BoardListRes();
			boardListRes.setRequestUserID(requestUserID);
			boardListRes.setBoardID(boardID.shortValue());
			boardListRes.setPageNo(pageNo);
			boardListRes.setPageSize(pageSize);
			boardListRes.setTotal(total);
			boardListRes.setCnt(boardList.size());
			boardListRes.setBoardList(boardList);

			return boardListRes;
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
	}
}
