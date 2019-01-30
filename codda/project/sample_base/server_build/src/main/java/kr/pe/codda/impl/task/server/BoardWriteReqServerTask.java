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

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
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

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;

public class BoardWriteReqServerTask extends AbstractServerTask {

	public BoardWriteReqServerTask() throws DynamicClassCallException {
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
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (BoardWriteReq) inputMessage);
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

			sendErrorOutputMessage("게시판 최상의 글 등록이 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}

	public BoardWriteRes doWork(String dbcpName, BoardWriteReq boardWriteReq) throws Exception {
		// FIXME!
		log.info(boardWriteReq.toString());

		BoardType boardType = null;
		

		try {
			boardType = BoardType.valueOf(boardWriteReq.getBoardID());
		} catch (IllegalArgumentException e) {
			String errorMessage = "잘못된 게시판 식별자입니다";
			throw new ServerServiceException(errorMessage);
		}

		SequenceType boardSequenceType = boardType.toSequenceType();
		
		try {
			ValueChecker.checkValidSubject(boardWriteReq.getSubject());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}

		try {
			ValueChecker.checkValidContent(boardWriteReq.getContent());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}

		try {
			ValueChecker.checkValidWriterID(boardWriteReq.getRequestedUserID());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}

		try {
			ValueChecker.checkValidIP(boardWriteReq.getIp());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}

		if (boardWriteReq.getNewAttachedFileCnt() > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = new StringBuilder().append("첨부 파일 등록 갯수[")
					.append(boardWriteReq.getNewAttachedFileCnt()).append("]가 unsgiend byte 최대값[")
					.append(CommonStaticFinalVars.UNSIGNED_BYTE_MAX).append("]을 초과하였습니다").toString();
			throw new ServerServiceException(errorMessage);
		}
		
		if (boardWriteReq.getNewAttachedFileCnt() > ServerCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT) {
			String errorMessage = new StringBuilder().append("첨부 파일 등록 갯수[")
					.append(boardWriteReq.getNewAttachedFileCnt())
					.append("]가 첨부 파일 최대 갯수[")
					.append(ServerCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT)
					.append("]를 초과하였습니다").toString();
			throw new ServerServiceException(errorMessage);
		}
		
		if (boardWriteReq.getNewAttachedFileCnt() > 0) {			
			int newAttachedFileCnt = boardWriteReq.getNewAttachedFileCnt();
			List<BoardWriteReq.NewAttachedFile> newAttachedFileList = boardWriteReq.getNewAttachedFileList();
			
			for (int i=0; i < newAttachedFileCnt; i++) {
				BoardWriteReq.NewAttachedFile newAttachedFile = newAttachedFileList.get(i);
				try {
					ValueChecker.checkValidFileName(newAttachedFile.getAttachedFileName());
				} catch (IllegalArgumentException e) {
					String errorMessage = new StringBuilder()
							.append(i)
							.append("번째 파일 이름 유효성 검사 에러 메시지::")
							.append(e.getMessage()).toString();
					throw new ServerServiceException(errorMessage);
				}
				
				if (newAttachedFile.getAttachedFileSize() <= 0) {
					String errorMessage = new StringBuilder()
					.append(i)
					.append("번째 파일[")
					.append(newAttachedFile.getAttachedFileName())
					.append("] 크기가 0보다 작거나 같습니다").toString();
					throw new ServerServiceException(errorMessage);
				}
			}
		}
		

		UByte boardSequenceID = UByte.valueOf(boardSequenceType.getSequenceID());
		UByte boardID = UByte.valueOf(boardWriteReq.getBoardID());
		UByte nextAttachedFileSeq = UByte.valueOf(boardWriteReq.getNewAttachedFileCnt());
		UInteger boardNo = null;

		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));
			
			ValueChecker.checkValidRequestedUserState(conn, create, log, boardWriteReq.getRequestedUserID());

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

			boardNo = boardSequenceRecord.get(SB_SEQ_TB.SQ_VALUE);

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
			
			

			int boardInsertCount = create.insertInto(SB_BOARD_TB).set(SB_BOARD_TB.BOARD_ID, boardID)
					.set(SB_BOARD_TB.BOARD_NO, boardNo).set(SB_BOARD_TB.GROUP_NO, boardNo)
					.set(SB_BOARD_TB.GROUP_SQ, UShort.valueOf(0)).set(SB_BOARD_TB.PARENT_NO, UInteger.valueOf(0L))
					.set(SB_BOARD_TB.DEPTH, UByte.valueOf(0)).set(SB_BOARD_TB.VIEW_CNT, Integer.valueOf(0))
					.set(SB_BOARD_TB.BOARD_ST, BoardStateType.OK.getValue())
					.set(SB_BOARD_TB.NEXT_ATTACHED_FILE_SQ, nextAttachedFileSeq).execute();

			if (0 == boardInsertCount) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				String errorMessage = "게시판 최상의 글 등록이 실패하였습니다";
				throw new ServerServiceException(errorMessage);
			}

			int boardHistoryInsertCount = create.insertInto(SB_BOARD_HISTORY_TB)
					.set(SB_BOARD_HISTORY_TB.BOARD_ID, boardID).set(SB_BOARD_HISTORY_TB.BOARD_NO, boardNo)
					.set(SB_BOARD_HISTORY_TB.HISTORY_SQ, UByte.valueOf(0))
					.set(SB_BOARD_HISTORY_TB.SUBJECT, boardWriteReq.getSubject())
					.set(SB_BOARD_HISTORY_TB.CONTENT, boardWriteReq.getContent())
					.set(SB_BOARD_HISTORY_TB.MODIFIER_ID, boardWriteReq.getRequestedUserID())
					.set(SB_BOARD_HISTORY_TB.IP, boardWriteReq.getIp())
					.set(SB_BOARD_HISTORY_TB.REG_DT, JooqSqlUtil.getFieldOfSysDate(Timestamp.class)).execute();

			if (0 == boardHistoryInsertCount) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				String errorMessage = "게시판 최상의 글 내용을 저장하는데 실패하였습니다";
				throw new ServerServiceException(errorMessage);
			}

			
			if (boardWriteReq.getNewAttachedFileCnt() > 0) {
				int attachedFileListIndex = 0;
				
				
				for (BoardWriteReq.NewAttachedFile attachedFileForRequest : boardWriteReq.getNewAttachedFileList()) {
					int boardFileListInsertCount = create.insertInto(SB_BOARD_FILELIST_TB)
							.set(SB_BOARD_FILELIST_TB.BOARD_ID, boardID).set(SB_BOARD_FILELIST_TB.BOARD_NO, boardNo)
							.set(SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ, UByte.valueOf(attachedFileListIndex))
							.set(SB_BOARD_FILELIST_TB.ATTACHED_FNAME, attachedFileForRequest.getAttachedFileName())
							.set(SB_BOARD_FILELIST_TB.ATTACHED_FSIZE, attachedFileForRequest.getAttachedFileSize())
							.execute();

					if (0 == boardFileListInsertCount) {
						try {
							conn.rollback();
						} catch (Exception e) {
							log.warn("fail to rollback");
						}
						String errorMessage = "게시판 첨부 파일을  저장하는데 실패하였습니다";
						log.warn("게시판 첨부 파일 목록내 인덱스[{}]의 첨부 파일 이름을 저장하는데 실패하였습니다", attachedFileListIndex);

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

			// log.info("게시판 최상의 글[boardID={}, boardNo={}, subject={}] 등록 성공", boardID,
			// boardNo, boardWriteReq.getSubject());

			
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
		
		BoardWriteRes boardWriteRes = new BoardWriteRes();
		boardWriteRes.setBoardID(boardID.shortValue());
		boardWriteRes.setBoardNo(boardNo.longValue());

		return boardWriteRes;
	}
}
