package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.impl.jooq.tables.SbSeqTb.SB_SEQ_TB;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardReplyReq.BoardReplyReq;
import kr.pe.codda.impl.message.BoardReplyRes.BoardReplyRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.BoardType;
import kr.pe.codda.server.lib.JooqSqlUtil;
import kr.pe.codda.server.lib.SequenceType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardReplyReqServerTask extends AbstractServerTask {

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
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (BoardReplyReq) inputMessage);
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

			sendErrorOutputMessage("게시글 댓글 쓰기가 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}

	public BoardReplyRes doWork(String dbcpName, BoardReplyReq boardReplyReq) throws Exception {
		// FIXME!
		log.info(boardReplyReq.toString());

		BoardType boardType = null;
		try {
			boardType = BoardType.valueOf(boardReplyReq.getBoardID());
		} catch (IllegalArgumentException e) {			
			String errorMessage = "잘못된 게시판 식별자입니다";
			throw new ServerServiceException(errorMessage);
		}

		SequenceType boardSequenceType = boardType.toSequenceType();

		try {
			ValueChecker.checkValidParentBoardNo(boardReplyReq.getParentBoardNo());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);

		}

		try {
			ValueChecker.checkValidSubject(boardReplyReq.getSubject());
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}

		try {
			ValueChecker.checkValidContent(boardReplyReq.getContent());
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}

		try {
			ValueChecker.checkValidUserID(boardReplyReq.getRequestUserID());
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}

		try {
			ValueChecker.checkValidIP(boardReplyReq.getIp());
		} catch (IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		if (boardReplyReq.getAttachedFileCnt() > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = new StringBuilder().append("첨부 파일 등록 갯수[")
					.append(boardReplyReq.getAttachedFileCnt())
					.append("]가 최대 첨부 파일 등록 갯수[")
					.append(CommonStaticFinalVars.UNSIGNED_BYTE_MAX)
					.append("]를 초과하였습니다").toString();
			throw new ServerServiceException(errorMessage);
		}
		
		if (boardReplyReq.getAttachedFileCnt() > 0) {			
			int newAttachedFileCnt = boardReplyReq.getAttachedFileCnt();
			List<BoardReplyReq.AttachedFile> newAttachedFileList = boardReplyReq.getAttachedFileList();
			
			for (int i=0; i < newAttachedFileCnt; i++) {
				BoardReplyReq.AttachedFile attachedFileForRequest = newAttachedFileList.get(i);
				try {
					ValueChecker.checkValidFileName(attachedFileForRequest.getAttachedFileName());
				} catch (IllegalArgumentException e) {
					String errorMessage = new StringBuilder()
							.append(i)
							.append("번째 파일 이름 유효성 검사 에러 메시지::")
							.append(e.getMessage()).toString();
					throw new ServerServiceException(errorMessage);
				}			
			}
		}

		UByte boardID = UByte.valueOf(boardReplyReq.getBoardID());
		UByte boardSequenceID = UByte.valueOf(boardSequenceType.getSequenceID());
		UInteger parentBoardNo = UInteger.valueOf(boardReplyReq.getParentBoardNo());

		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));
			
			ValueChecker.checkValidMemberStateForUserID(conn, create, log, boardReplyReq.getRequestUserID());
			
			Record boardSequenceRecord = create.select(SB_SEQ_TB.SQ_VALUE).from(SB_SEQ_TB)
					.where(SB_SEQ_TB.SQ_ID.eq(boardSequenceID)).forUpdate().fetchOne();

			if (null == boardSequenceRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("게시판 번호로 사용할 게시판[").append(boardType.getName())
						.append("] 시퀀스 식별자[").append(boardSequenceID).append("]의 시퀀스를 가져오는데 실패하였습니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			UInteger boardNo = boardSequenceRecord.get(SB_SEQ_TB.SQ_VALUE);

			int countOfUpdate = create.update(SB_SEQ_TB).set(SB_SEQ_TB.SQ_VALUE, SB_SEQ_TB.SQ_VALUE.add(1))
					.where(SB_SEQ_TB.SQ_ID.eq(boardSequenceID)).execute();

			if (0 == countOfUpdate) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("게시판 시퀀스 식별자[").append(boardSequenceID)
						.append("]의 시퀀스 갱신이 실패하였습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			conn.commit();
			
				

			Record2<UInteger, UShort> parentBoardRecord = create.select(SB_BOARD_TB.GROUP_NO, SB_BOARD_TB.GROUP_SQ)
					.from(SB_BOARD_TB).where(SB_BOARD_TB.BOARD_ID.eq(boardID))
					.and(SB_BOARD_TB.BOARD_NO.eq(parentBoardNo)).fetchOne();

			if (null == parentBoardRecord) {
				String errorMessage = new StringBuilder().append("부모글[")
						.append(parentBoardNo)
						.append("] 이 존재하지 않습니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			UInteger groupNoOfParentBoard = parentBoardRecord.getValue(SB_BOARD_TB.GROUP_NO);
			UShort groupSeqOfParentBoard = parentBoardRecord.getValue(SB_BOARD_TB.GROUP_SQ);
			

			/** 댓글은 부모가 속한 그룹의 순서를 조정하므로 그룹 전체에 대해서 동기화가 필요하기때문에 부모가 속한 그룹 최상위 글에 대해서 락을 건다 */
			Result<Record1<UInteger>> rootBoardResult = create.select(SB_BOARD_TB.BOARD_NO)
					.from(SB_BOARD_TB)
					.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
					.and(SB_BOARD_TB.BOARD_NO.eq(groupNoOfParentBoard)).forUpdate().fetch();

			if (null == rootBoardResult) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder().append("그룹 최상위 글[").append(groupNoOfParentBoard.longValue())
						.append("] 이 존재하지 않습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			

			create.update(SB_BOARD_TB).set(SB_BOARD_TB.GROUP_SQ, SB_BOARD_TB.GROUP_SQ.add(1))
					.where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.GROUP_NO.eq(groupNoOfParentBoard))
					.and(SB_BOARD_TB.GROUP_SQ.gt(groupSeqOfParentBoard)).execute();			

			int boardInsertCount = create
					.insertInto(SB_BOARD_TB, SB_BOARD_TB.BOARD_ID, SB_BOARD_TB.BOARD_NO, SB_BOARD_TB.GROUP_NO,
							SB_BOARD_TB.GROUP_SQ, SB_BOARD_TB.PARENT_NO, SB_BOARD_TB.DEPTH,
							SB_BOARD_TB.VIEW_CNT, SB_BOARD_TB.BOARD_ST)
					.select(create
							.select(SB_BOARD_TB.BOARD_ID, DSL.val(boardNo).as(SB_BOARD_TB.BOARD_NO),
									SB_BOARD_TB.GROUP_NO, SB_BOARD_TB.GROUP_SQ.add(1).as(SB_BOARD_TB.GROUP_SQ),
									DSL.inline(UInteger.valueOf(boardReplyReq.getParentBoardNo()))
											.as(SB_BOARD_TB.PARENT_NO),
									SB_BOARD_TB.DEPTH.add(1).as(SB_BOARD_TB.DEPTH),									
									DSL.val(0).as(SB_BOARD_TB.VIEW_CNT),
									DSL.val(BoardStateType.OK.getValue()).as(SB_BOARD_TB.BOARD_ST))
							.from(SB_BOARD_TB)
							.where(SB_BOARD_TB.BOARD_NO.eq(UInteger.valueOf(boardReplyReq.getParentBoardNo()))))
					.execute();

			if (0 == boardInsertCount) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				String errorMessage = "댓글 저장하는데 실패하였습니다";
				throw new ServerServiceException(errorMessage);
			}

			int boardHistoryInsertCount = create.insertInto(SB_BOARD_HISTORY_TB)
					.set(SB_BOARD_HISTORY_TB.BOARD_ID, boardID).set(SB_BOARD_HISTORY_TB.BOARD_NO, boardNo)
					.set(SB_BOARD_HISTORY_TB.HISTORY_SQ, UByte.valueOf(0))
					.set(SB_BOARD_HISTORY_TB.SUBJECT, boardReplyReq.getSubject())
					.set(SB_BOARD_HISTORY_TB.CONTENT, boardReplyReq.getContent())
					.set(SB_BOARD_HISTORY_TB.MODIFIER_ID, boardReplyReq.getRequestUserID())
					.set(SB_BOARD_HISTORY_TB.IP, boardReplyReq.getIp())
					.set(SB_BOARD_HISTORY_TB.REG_DT, JooqSqlUtil.getFieldOfSysDate(Timestamp.class)).execute();

			if (0 == boardHistoryInsertCount) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				String errorMessage = "댓글 내용을 저장하는데 실패하였습니다";
				throw new ServerServiceException(errorMessage);
			}

			if (boardReplyReq.getAttachedFileCnt() > 0) {	
				int attachedFileListIndex = 0;			
				for (BoardReplyReq.AttachedFile attachedFileForRequest : boardReplyReq.getAttachedFileList()) {
					int boardFileListInsertCount = create.insertInto(SB_BOARD_FILELIST_TB)
							.set(SB_BOARD_FILELIST_TB.BOARD_ID, boardID).set(SB_BOARD_FILELIST_TB.BOARD_NO, boardNo)
							.set(SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ, UByte.valueOf(attachedFileListIndex))
							.set(SB_BOARD_FILELIST_TB.ATTACHED_FNAME, attachedFileForRequest.getAttachedFileName())
							.execute();

					if (0 == boardFileListInsertCount) {
						try {
							conn.rollback();
						} catch (Exception e) {
							log.warn("fail to rollback");
						}
						String errorMessage = "댓글의 첨부 파일을  저장하는데 실패하였습니다";
						log.warn("댓글의 첨부 파일 목록내 인덱스[{}]의 첨부 파일 이름을 저장하는데 실패하였습니다", attachedFileListIndex);

						throw new ServerServiceException(errorMessage);
					}

					attachedFileListIndex++;
				}
			}
			
			create.update(SB_BOARD_INFO_TB)
			.set(SB_BOARD_INFO_TB.ADMIN_TOTAL, SB_BOARD_INFO_TB.ADMIN_TOTAL.add(1))
			.set(SB_BOARD_INFO_TB.USER_TOTAL, SB_BOARD_INFO_TB.USER_TOTAL.add(1))
			.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).execute();

			conn.commit();

			BoardReplyRes boardReplyRes = new BoardReplyRes();
			boardReplyRes.setBoardID(boardID.shortValue());
			boardReplyRes.setBoardNo(boardNo.longValue());
			
			return boardReplyRes;
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
	}
}
