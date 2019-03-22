package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record6;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardChangeHistoryReq.BoardChangeHistoryReq;
import kr.pe.codda.impl.message.BoardChangeHistoryRes.BoardChangeHistoryRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.BoardListType;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardChangeHistoryReqServerTask extends AbstractServerTask {
	
	public BoardChangeHistoryReqServerTask() throws DynamicClassCallException {
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
					(BoardChangeHistoryReq) inputMessage);
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

			sendErrorOutputMessage("게시글 수정 이력 조회가 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}
	
	public BoardChangeHistoryRes doWork(String dbcpName, BoardChangeHistoryReq boardChangeHistoryReq) throws Exception {
		try {
			ValueChecker.checkValidRequestedUserID(boardChangeHistoryReq.getRequestedUserID());
		} catch(RuntimeException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ValueChecker.checkValidBoardID(boardChangeHistoryReq.getBoardID());
		} catch(RuntimeException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ValueChecker.checkValidBoardNo(boardChangeHistoryReq.getBoardNo());
		} catch(RuntimeException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		UByte boardID = UByte.valueOf(boardChangeHistoryReq.getBoardID());
		UInteger boardNo = UInteger.valueOf(boardChangeHistoryReq.getBoardNo());
		
		BoardListType boardListType = null;
		UInteger parentNo = null;
		UInteger groupNo = null;
		
		List<BoardChangeHistoryRes.BoardChangeHistory> boardChangeHistoryList = new ArrayList<BoardChangeHistoryRes.BoardChangeHistory>();
		
		DataSource dataSource = DBCPManager.getInstance().getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));
			
			Record1<Byte> boardInforRecord = create
					.select(SB_BOARD_INFO_TB.LIST_TYPE)
					.from(SB_BOARD_INFO_TB).where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).fetchOne();

			if (null == boardInforRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("입력 받은 게시판 식별자[").append(boardID.shortValue())
						.append("]가 게시판 정보 테이블에 존재하지  않습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			byte boardListTypeValue = boardInforRecord.get(SB_BOARD_INFO_TB.LIST_TYPE);
			
			
			try {
				boardListType = BoardListType.valueOf(boardListTypeValue);
			} catch (IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = e.getMessage();
				throw new ServerServiceException(errorMessage);
			}
			
			
			ServerDBUtil.checkUserAccessRights(conn, create, log,
					"게시글 수정 이력 조회 서비스", PermissionType.GUEST, boardChangeHistoryReq.getRequestedUserID());
			
			
			Record2<UInteger, UInteger> boardRecord = create.select(SB_BOARD_TB.PARENT_NO, SB_BOARD_TB.GROUP_NO)
			.from(SB_BOARD_TB)
			.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
			.and(SB_BOARD_TB.BOARD_NO.eq(boardNo))
			.fetchOne();
			
			if (null == boardRecord) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = "지정한 게시글이 존재 하지 않습니다";
				throw new ServerServiceException(errorMessage);
			}
			
			parentNo = boardRecord.get(SB_BOARD_TB.PARENT_NO);
			groupNo = boardRecord.get(SB_BOARD_TB.GROUP_NO);
			
			Result<Record6<UByte, String, String, String, String, Timestamp>> boardHistoryResult = create.select(SB_BOARD_HISTORY_TB.HISTORY_SQ, 
					SB_BOARD_HISTORY_TB.SUBJECT, 
					SB_BOARD_HISTORY_TB.CONTENTS,
					SB_BOARD_HISTORY_TB.REGISTRANT_ID,
					SB_MEMBER_TB.NICKNAME,
					SB_BOARD_HISTORY_TB.REG_DT)
			.from(SB_BOARD_HISTORY_TB)
			.innerJoin(SB_MEMBER_TB)
			.on(SB_MEMBER_TB.USER_ID.eq(SB_BOARD_HISTORY_TB.REGISTRANT_ID))
			.where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(boardID))
			.and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(boardNo))
			.orderBy(SB_BOARD_HISTORY_TB.HISTORY_SQ.asc()).fetch();
			
			for (Record6<UByte, String, String, String, String, Timestamp> boardHistoryRecord : boardHistoryResult) {
				UByte historySeq = boardHistoryRecord.get(SB_BOARD_HISTORY_TB.HISTORY_SQ);
				String subject = boardHistoryRecord.get(SB_BOARD_HISTORY_TB.SUBJECT);
				String contents = boardHistoryRecord.get(SB_BOARD_HISTORY_TB.CONTENTS);
				String writerID = boardHistoryRecord.get(SB_BOARD_HISTORY_TB.REGISTRANT_ID);
				String writerNickname = boardHistoryRecord.get(SB_MEMBER_TB.NICKNAME);
				Timestamp registeredDate = boardHistoryRecord.get(SB_BOARD_HISTORY_TB.REG_DT);
				
				BoardChangeHistoryRes.BoardChangeHistory boardChangeHistory = new BoardChangeHistoryRes.BoardChangeHistory();
				boardChangeHistory.setHistorySeq(historySeq.shortValue());
				boardChangeHistory.setSubject((null == subject) ? "": subject);
				boardChangeHistory.setContents(contents);
				boardChangeHistory.setWriterID(writerID);
				boardChangeHistory.setWriterNickname(writerNickname);
				boardChangeHistory.setRegisteredDate(registeredDate);
				
				boardChangeHistoryList.add(boardChangeHistory);
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
		
		BoardChangeHistoryRes boardChangeHistoryRes = new BoardChangeHistoryRes();
		boardChangeHistoryRes.setBoardID(boardID.shortValue());
		boardChangeHistoryRes.setBoardNo(boardNo.longValue());
		boardChangeHistoryRes.setBoardListType(boardListType.getValue());
		boardChangeHistoryRes.setParentNo(parentNo.longValue());
		boardChangeHistoryRes.setGroupNo(groupNo.longValue());
		boardChangeHistoryRes.setBoardChangeHistoryCnt(boardChangeHistoryList.size());
		boardChangeHistoryRes.setBoardChangeHistoryList(boardChangeHistoryList);

		return boardChangeHistoryRes;
	}

}
