package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;

import javax.sql.DataSource;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardModifyReq.BoardModifyReq;
import kr.pe.codda.impl.message.BoardModifyRes.BoardModifyRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.BoardType;
import kr.pe.codda.server.lib.JooqSqlUtil;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;

public class BoardModifyReqServerTask extends AbstractServerTask {
	private void sendErrorOutputMessage(String errorMessage,
			ToLetterCarrier toLetterCarrier, AbstractMessage inputMessage)
			throws InterruptedException {
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
			ToLetterCarrier toLetterCarrier, AbstractMessage inputMessage)
			throws Exception {

		try {
			AbstractMessage outputMessage = doWork(
					ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME,
					(BoardModifyReq) inputMessage);
			toLetterCarrier.addSyncOutputMessage(outputMessage);
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();
			log.warn("errmsg=={}, inObj={}", errorMessage,
					inputMessage.toString());

			sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
			return;
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("unknwon errmsg=")
					.append(e.getMessage()).append(", inObj=")
					.append(inputMessage.toString()).toString();

			log.warn(errorMessage, e);

			sendErrorOutputMessage("게시글 수정하는데 실패하였습니다", toLetterCarrier,
					inputMessage);
			return;
		}
	}

	public BoardModifyRes doWork(String dbcpName, BoardModifyReq boardModifyReq)
			throws Exception {
		// FIXME!
		log.info(boardModifyReq.toString());

		try {
			BoardType.valueOf(boardModifyReq.getBoardID());
		} catch (IllegalArgumentException e) {
			String errorMessage = "잘못된 게시판 식별자입니다";
			throw new ServerServiceException(errorMessage);
		}

		try {
			ValueChecker.checkValidSubject(boardModifyReq.getSubject());
		} catch (RuntimeException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}

		try {
			ValueChecker.checkValidContent(boardModifyReq.getContent());
		} catch (RuntimeException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}

		try {
			ValueChecker.checkValidWriterID(boardModifyReq.getRequestedUserID());
		} catch (RuntimeException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}

		try {
			ValueChecker.checkValidIP(boardModifyReq.getIp());
		} catch (RuntimeException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}

		if (boardModifyReq.getOldAttachedFileSeqCnt() > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = new StringBuilder()
					.append("기존 첨부 파일들중 남은 갯수[")
					.append(boardModifyReq.getOldAttachedFileSeqCnt())
					.append("]가 unsgiend byte 최대값[")
					.append(CommonStaticFinalVars.UNSIGNED_BYTE_MAX)
					.append("]을 초과하였습니다").toString();
			throw new ServerServiceException(errorMessage);
		}

		if (boardModifyReq.getNewAttachedFileCnt() > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = new StringBuilder().append("신규 첨부 파일 등록 갯수[")
					.append(boardModifyReq.getNewAttachedFileCnt())
					.append("]가 unsgiend byte 최대값[")
					.append(CommonStaticFinalVars.UNSIGNED_BYTE_MAX)
					.append("]을 초과하였습니다").toString();
			throw new ServerServiceException(errorMessage);
		}

		if ((boardModifyReq.getNewAttachedFileCnt() + boardModifyReq
				.getOldAttachedFileSeqCnt()) > ServerCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT) {
			String errorMessage = new StringBuilder()
					.append("총 첨부 파일 갯수(=신규 첨부 파일 등록 갯수[")
					.append(boardModifyReq.getNewAttachedFileCnt())
					.append("] + 기존 첨부 파일들중 남은 갯수[")
					.append(boardModifyReq.getOldAttachedFileSeqCnt())
					.append("]) 가 첨부 파일 최대 갯수[")
					.append(ServerCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT)
					.append("]를 초과하였습니다").toString();
			throw new ServerServiceException(errorMessage);
		}

		if (boardModifyReq.getNewAttachedFileCnt() > 0) {
			int newAttachedFileCnt = boardModifyReq.getNewAttachedFileCnt();
			List<BoardModifyReq.NewAttachedFile> newAttachedFileList = boardModifyReq
					.getNewAttachedFileList();

			for (int i = 0; i < newAttachedFileCnt; i++) {
				BoardModifyReq.NewAttachedFile newAttachedFile = newAttachedFileList
						.get(i);
				try {
					ValueChecker.checkValidFileName(newAttachedFile
							.getAttachedFileName());
				} catch (IllegalArgumentException e) {
					String errorMessage = new StringBuilder().append(i)
							.append("번째 파일 이름 유효성 검사 에러 메시지::")
							.append(e.getMessage()).toString();
					throw new ServerServiceException(errorMessage);
				}

				if (newAttachedFile.getAttachedFileSize() <= 0) {
					String errorMessage = new StringBuilder().append(i)
							.append("번째 파일[")
							.append(newAttachedFile.getAttachedFileName())
							.append("] 크기가 0보다 작거나 같습니다").toString();
					throw new ServerServiceException(errorMessage);
				}
			}
		}

		UByte boardID = UByte.valueOf(boardModifyReq.getBoardID());
		UInteger boardNo = UInteger.valueOf(boardModifyReq.getBoardNo());
		
		HashSet<Short> remainingOldAttachedFileSequeceSet = new HashSet<Short>();		
		for (BoardModifyReq.OldAttachedFileSeq oldAttachedFile : boardModifyReq
				.getOldAttachedFileSeqList()) {
			
			short oldAttachedFileSeq = oldAttachedFile.getAttachedFileSeq();
			
			if (remainingOldAttachedFileSequeceSet.contains(oldAttachedFileSeq)) {
				String errorMessage = new StringBuilder()
				.append("보존을 원하는 구 첨부 파일 목록에서 증복된 첨부 파일 시퀀스[")
				.append(oldAttachedFileSeq)
				.append("]가 존재합니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			if (oldAttachedFileSeq >= boardModifyReq.getNextAttachedFileSeq()) {
				String errorMessage = new StringBuilder()
				.append("보존을 원하는 구 첨부 파일 시퀀스[")
				.append(oldAttachedFileSeq)
				.append("]가 다음 첨부 파일 시퀀스[")
				.append(boardModifyReq.getNextAttachedFileSeq())
				.append("]보다 크거나 같습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			remainingOldAttachedFileSequeceSet.add(oldAttachedFileSeq);
		}

		DataSource dataSource = DBCPManager.getInstance().getBasicDataSource(
				dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL,
					ServerDBUtil.getDBCPSettings(dbcpName));

			String memberRoleTypeValueOfRequestedUserID = ValueChecker
					.checkValidRequestedUserState(conn, create, log,
							boardModifyReq.getRequestedUserID());
			MemberRoleType memberRoleTypeOfRequestedUserID = null;
			try {
				memberRoleTypeOfRequestedUserID = MemberRoleType.valueOf(
						memberRoleTypeValueOfRequestedUserID, false);
			} catch (IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("해당 게시글 수정 요청자[")
						.append(boardModifyReq.getRequestedUserID())
						.append("]의 멤버 타입[")
						.append(memberRoleTypeValueOfRequestedUserID)
						.append("]이 잘못되어있습니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			if (! MemberRoleType.ADMIN.equals(memberRoleTypeOfRequestedUserID)) {
				Record1<String> firstWriterBoardRecord = create
						.select(SB_BOARD_HISTORY_TB.MODIFIER_ID)
						.from(SB_BOARD_HISTORY_TB)
						.where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(boardID))
						.and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(boardNo))
						.and(SB_BOARD_HISTORY_TB.HISTORY_SQ.eq(UByte.valueOf(0)))
						.fetchOne();

				if (null == firstWriterBoardRecord) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}

					String errorMessage = new StringBuilder(
							"해당 게시글의 최초 작성자 정보가 존재 하지 않습니다").toString();
					throw new ServerServiceException(errorMessage);
				}

				String firstWriterID = firstWriterBoardRecord
						.getValue(SB_BOARD_HISTORY_TB.MODIFIER_ID);

				if (!boardModifyReq.getRequestedUserID().equals(firstWriterID)) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}

					String errorMessage = new StringBuilder("타인[")
							.append(firstWriterID).append("] 게시글은 수정 할 수 없습니다")
							.toString();
					throw new ServerServiceException(errorMessage);
				}
			}

			Record2<String, UByte> boardRecord = create
					.select(SB_BOARD_TB.BOARD_ST,
							SB_BOARD_TB.NEXT_ATTACHED_FILE_SQ)
					.from(SB_BOARD_TB).where(SB_BOARD_TB.BOARD_ID.eq(boardID))
					.and(SB_BOARD_TB.BOARD_NO.eq(boardNo)).forUpdate()
					.fetchOne();

			if (null == boardRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("해당 게시글이 존재 하지 않습니다")
						.toString();
				throw new ServerServiceException(errorMessage);
			}

			String nativeBoardStateType = boardRecord
					.getValue(SB_BOARD_TB.BOARD_ST);
			UByte nextAttachedFileSeq = boardRecord
					.getValue(SB_BOARD_TB.NEXT_ATTACHED_FILE_SQ);

			BoardStateType boardStateType = null;
			try {
				boardStateType = BoardStateType.valueOf(nativeBoardStateType,
						false);
			} catch (IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("게시글의 상태 값[")
						.append(nativeBoardStateType).append("]이 잘못되었습니다")
						.toString();
				throw new ServerServiceException(errorMessage);
			}

			if (BoardStateType.DELETE.equals(boardStateType)) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("해당 게시글은 삭제된 글입니다")
						.toString();
				throw new ServerServiceException(errorMessage);
			} else if (BoardStateType.BLOCK.equals(boardStateType)) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder(
						"해당 게시글은 관리자에 의해 블락된 글입니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			if (boardModifyReq.getNextAttachedFileSeq() != nextAttachedFileSeq
					.shortValue()) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder()
						.append("입력 메시지로 받은 '다음 첨부 파일 시퀀스 번호'[")
						.append(boardModifyReq.getNextAttachedFileSeq())
						.append("]가 DB 상 값[")
						.append(nextAttachedFileSeq.shortValue())
						.append("]과 다릅니다").toString();

				throw new ServerServiceException(errorMessage);
			}

			int newNextAttachedFileSeq = nextAttachedFileSeq.shortValue()
					+ boardModifyReq.getNewAttachedFileCnt();

			if (newNextAttachedFileSeq > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder()
						.append("새로운 '다음 첨부 파일 시퀀스 번호'(= 기존 '다음 첨부 파일 시퀀스 번호'[")
						.append(nextAttachedFileSeq.shortValue())
						.append("] + 신규 첨부 파일 갯수[")
						.append(boardModifyReq.getNewAttachedFileCnt())
						.append("])가 최대 값(=255)을 초과하였습니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			create.update(SB_BOARD_TB)
					.set(SB_BOARD_TB.NEXT_ATTACHED_FILE_SQ,
							UByte.valueOf(newNextAttachedFileSeq))
					.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
					.and(SB_BOARD_TB.BOARD_NO.eq(boardNo)).execute();

			Record1<UByte> boardHistoryMaxSequenceRecord = create
					.select(SB_BOARD_HISTORY_TB.HISTORY_SQ.max().add(1))
					.from(SB_BOARD_HISTORY_TB)
					.where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(boardID))
					.and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(boardNo)).fetchOne();

			if (null == boardHistoryMaxSequenceRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder()
						.append("해당 게시판의 글[boardID=")
						.append(boardID)
						.append(", boardNo=")
						.append(boardNo)
						.append("]은  제목과 내용이 저장된 테이블(SB_BOARD_HISTORY_TB)에 미 존재합니다")
						.toString();
				throw new ServerServiceException(errorMessage);
			}

			UByte boardHistorySequence = boardHistoryMaxSequenceRecord.value1();

			int boardHistoryInsertCount = create
					.insertInto(SB_BOARD_HISTORY_TB)
					.set(SB_BOARD_HISTORY_TB.BOARD_ID, boardID)
					.set(SB_BOARD_HISTORY_TB.BOARD_NO, boardNo)
					.set(SB_BOARD_HISTORY_TB.HISTORY_SQ, boardHistorySequence)
					.set(SB_BOARD_HISTORY_TB.SUBJECT,
							boardModifyReq.getSubject())
					.set(SB_BOARD_HISTORY_TB.CONTENT,
							boardModifyReq.getContent())
					.set(SB_BOARD_HISTORY_TB.MODIFIER_ID,
							boardModifyReq.getRequestedUserID())
					.set(SB_BOARD_HISTORY_TB.IP, boardModifyReq.getIp())
					.set(SB_BOARD_HISTORY_TB.REG_DT,
							JooqSqlUtil.getFieldOfSysDate(Timestamp.class))
					.execute();

			if (0 == boardHistoryInsertCount) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				String errorMessage = "게시판 최상의 글 내용을 저장하는데 실패하였습니다";
				throw new ServerServiceException(errorMessage);
			}

			
			Result<Record1<UByte>> attachFileListRecord = create
					.select(SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ)
					.from(SB_BOARD_FILELIST_TB)
					.where(SB_BOARD_FILELIST_TB.BOARD_ID.eq(boardID))
					.and(SB_BOARD_FILELIST_TB.BOARD_NO.eq(boardNo)).fetch();

			if (null == attachFileListRecord) {
				/**
				 * 첨부 파일이 실제 DB 에 미 존재하는데 
				 * 1개 이상의 원소를 갖는 구 첨부 파일 목록을 파라미터 받았을 경우 에러 처리
				 */
				if (! remainingOldAttachedFileSequeceSet.isEmpty()) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}

					String errorMessage = new StringBuilder(
							"구 첨부 파일들이 존재 하지 않는데 보존을 원하는 구 첨부 파일 목록을 요청하셨습니다")
							.toString();
					throw new ServerServiceException(errorMessage);
				}
			} else {
				HashSet<Short> actualOldAttachedFileSequeceSet = new HashSet<Short>();

				for (Record attachFileRecord : attachFileListRecord) {
					UByte attachedFileSeq = attachFileRecord
							.get(SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ);
					actualOldAttachedFileSequeceSet.add(attachedFileSeq.shortValue());
				}

				if (! actualOldAttachedFileSequeceSet
						.containsAll(remainingOldAttachedFileSequeceSet)) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}

					String errorMessage = new StringBuilder(
							"보존을 원하는 구 첨부 파일 목록중 실제 구 첨부 파일 목록에 존재하지 않는 첨부 파일이 존재합니다, 실제 구 첨부 파일 목록=")
							.append(actualOldAttachedFileSequeceSet.toString())
							.toString();
					throw new ServerServiceException(errorMessage);
				}

				actualOldAttachedFileSequeceSet
						.removeAll(remainingOldAttachedFileSequeceSet);

				if (! actualOldAttachedFileSequeceSet.isEmpty()) {
					create.delete(SB_BOARD_FILELIST_TB)
							.where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(boardID))
							.and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(boardNo))
							.and(SB_BOARD_HISTORY_TB.HISTORY_SQ
									.in(actualOldAttachedFileSequeceSet))
							.execute();
				}
			}
			

			if (boardModifyReq.getNewAttachedFileCnt() > 0) {
				int newAttachedFileListIndex = 0;
				int newAttachedFileSeq = nextAttachedFileSeq.shortValue();
				for (BoardModifyReq.NewAttachedFile newAttachedFile : boardModifyReq
						.getNewAttachedFileList()) {
					int boardFileListInsertCount = create
							.insertInto(SB_BOARD_FILELIST_TB)
							.set(SB_BOARD_FILELIST_TB.BOARD_ID, boardID)
							.set(SB_BOARD_FILELIST_TB.BOARD_NO, boardNo)
							.set(SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ,
									UByte.valueOf(newAttachedFileSeq))
							.set(SB_BOARD_FILELIST_TB.ATTACHED_FNAME,
									newAttachedFile.getAttachedFileName())
							.set(SB_BOARD_FILELIST_TB.ATTACHED_FSIZE,
									newAttachedFile.getAttachedFileSize())
							.execute();

					if (0 == boardFileListInsertCount) {
						try {
							conn.rollback();
						} catch (Exception e) {
							log.warn("fail to rollback");
						}
						String errorMessage = "게시판 첨부 파일을  저장하는데 실패하였습니다";
						log.warn(
								"게시판 첨부 파일 목록내 인덱스[{}]의 첨부 파일 이름을 저장하는데 실패하였습니다",
								newAttachedFileListIndex);

						throw new ServerServiceException(errorMessage);
					}

					newAttachedFileSeq++;
					newAttachedFileListIndex++;
				}
			}

			conn.commit();

			BoardModifyRes boardModifyRes = new BoardModifyRes();

			boardModifyRes.setBoardID(boardID.shortValue());
			boardModifyRes.setBoardNo(boardNo.longValue());

			return boardModifyRes;
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
