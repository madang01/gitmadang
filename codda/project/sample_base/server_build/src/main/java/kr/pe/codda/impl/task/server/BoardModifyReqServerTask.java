package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.jooq.tables.records.SbBoardHistoryTbRecord;
import kr.pe.codda.impl.message.BoardModifyReq.BoardModifyReq;
import kr.pe.codda.impl.message.BoardModifyRes.BoardModifyRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.BoardListType;
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.JooqSqlUtil;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardModifyReqServerTask extends AbstractServerTask {
	public BoardModifyReqServerTask() throws DynamicClassCallException {
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
					(BoardModifyReq) inputMessage);
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

			sendErrorOutputMessage("게시글 수정하는데 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}

	public BoardModifyRes doWork(String dbcpName, BoardModifyReq boardModifyReq) throws Exception {
		// FIXME!
		log.info(boardModifyReq.toString());

		try {
			ValueChecker.checkValidRequestedUserID(boardModifyReq.getRequestedUserID());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ValueChecker.checkValidBoardNo(boardModifyReq.getBoardNo());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ValueChecker.checkValidBoardPasswordHashBase64(boardModifyReq.getPwdHashBase64());
		} catch (RuntimeException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}

		try {
			ValueChecker.checkValidContents(boardModifyReq.getContents());
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
		
			

		if (boardModifyReq.getOldAttachedFileCnt() > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = new StringBuilder().append("기존 첨부 파일들중 남은 갯수[")
					.append(boardModifyReq.getOldAttachedFileCnt()).append("]가 unsgiend byte 최대값[")
					.append(CommonStaticFinalVars.UNSIGNED_BYTE_MAX).append("]을 초과하였습니다").toString();
			throw new ServerServiceException(errorMessage);
		}

		if (boardModifyReq.getNewAttachedFileCnt() > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = new StringBuilder().append("신규 첨부 파일 등록 갯수[")
					.append(boardModifyReq.getNewAttachedFileCnt()).append("]가 unsgiend byte 최대값[")
					.append(CommonStaticFinalVars.UNSIGNED_BYTE_MAX).append("]을 초과하였습니다").toString();
			throw new ServerServiceException(errorMessage);
		}

		if ((boardModifyReq.getNewAttachedFileCnt() + boardModifyReq
				.getOldAttachedFileCnt()) > ServerCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT) {
			String errorMessage = new StringBuilder().append("총 첨부 파일 갯수(=신규 첨부 파일 등록 갯수[")
					.append(boardModifyReq.getNewAttachedFileCnt()).append("] + 기존 첨부 파일들중 남은 갯수[")
					.append(boardModifyReq.getOldAttachedFileCnt()).append("]) 가 첨부 파일 최대 갯수[")
					.append(ServerCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT).append("]를 초과하였습니다")
					.toString();
			throw new ServerServiceException(errorMessage);
		}

		if (boardModifyReq.getNewAttachedFileCnt() > 0) {
			int newAttachedFileCnt = boardModifyReq.getNewAttachedFileCnt();
			List<BoardModifyReq.NewAttachedFile> newAttachedFileList = boardModifyReq.getNewAttachedFileList();

			for (int i = 0; i < newAttachedFileCnt; i++) {
				BoardModifyReq.NewAttachedFile newAttachedFile = newAttachedFileList.get(i);
				try {
					ValueChecker.checkValidFileName(newAttachedFile.getAttachedFileName());
				} catch (IllegalArgumentException e) {
					String errorMessage = new StringBuilder().append(i).append("번째 파일 이름 유효성 검사 에러 메시지::")
							.append(e.getMessage()).toString();
					throw new ServerServiceException(errorMessage);
				}

				if (newAttachedFile.getAttachedFileSize() <= 0) {
					String errorMessage = new StringBuilder().append(i).append("번째 파일[")
							.append(newAttachedFile.getAttachedFileName()).append("] 크기가 0보다 작거나 같습니다").toString();
					throw new ServerServiceException(errorMessage);
				}
			}
		}

		HashSet<Short> remainingOldAttachedFileSequeceSet = new HashSet<Short>();
		for (BoardModifyReq.OldAttachedFile oldAttachedFile : boardModifyReq.getOldAttachedFileList()) {

			short oldAttachedFileSeq = oldAttachedFile.getAttachedFileSeq();

			if (remainingOldAttachedFileSequeceSet.contains(oldAttachedFileSeq)) {
				String errorMessage = new StringBuilder().append("보존을 원하는 구 첨부 파일 목록에서 증복된 첨부 파일 시퀀스[")
						.append(oldAttachedFileSeq).append("]가 존재합니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			if (oldAttachedFileSeq >= boardModifyReq.getNextAttachedFileSeq()) {
				String errorMessage = new StringBuilder().append("보존을 원하는 구 첨부 파일 시퀀스[").append(oldAttachedFileSeq)
						.append("]가 다음 첨부 파일 시퀀스[").append(boardModifyReq.getNextAttachedFileSeq())
						.append("]보다 크거나 같습니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			remainingOldAttachedFileSequeceSet.add(oldAttachedFileSeq);
		}

		UByte boardID = UByte.valueOf(boardModifyReq.getBoardID());
		UInteger boardNo = UInteger.valueOf(boardModifyReq.getBoardNo());
		HashSet<Short> deletedAttachedFileSequeceSet = new HashSet<Short>();
		int newNextAttachedFileSeq = boardModifyReq.getNextAttachedFileSeq() + boardModifyReq.getNewAttachedFileCnt();

		if (newNextAttachedFileSeq > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = new StringBuilder().append("새로운 '다음 첨부 파일 시퀀스 번호'(= 기존 '다음 첨부 파일 시퀀스 번호'[")
					.append(boardModifyReq.getNextAttachedFileSeq()).append("] + 신규 첨부 파일 갯수[")
					.append(boardModifyReq.getNewAttachedFileCnt()).append("])가 최대 값(=255)을 초과하였습니다").toString();
			throw new ServerServiceException(errorMessage);
		}

		DataSource dataSource = DBCPManager.getInstance().getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));

			MemberRoleType memberRoleTypeOfRequestedUserID = ServerDBUtil.checkUserAccessRights(conn, create, log,
					"게시판 수정 서비스", PermissionType.GUEST, boardModifyReq.getRequestedUserID());

			Record1<Byte> boardInforRecord = create.select(SB_BOARD_INFO_TB.LIST_TYPE).from(SB_BOARD_INFO_TB)
					.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).fetchOne();

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
			BoardListType boardListType = null;
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
			
			if (BoardListType.TREE.equals(boardListType)) {
				try {
					ValueChecker.checkValidSubject(boardModifyReq.getSubject());
				} catch (IllegalArgumentException e) {
					try {
						conn.rollback();
					} catch (Exception e1) {
						log.warn("fail to rollback");
					}

					String errorMessage = e.getMessage();
					throw new ServerServiceException(errorMessage);
				}
			}

			if (! MemberRoleType.ADMIN.equals(memberRoleTypeOfRequestedUserID)) {
				/** 관리자가 아닌 경우 본인 여부 확인 */
				Record1<String> firstWriterBoardRecord = create.select(SB_BOARD_HISTORY_TB.REGISTRANT_ID)
						.from(SB_BOARD_HISTORY_TB).where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(boardID))
						.and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(boardNo))
						.and(SB_BOARD_HISTORY_TB.HISTORY_SQ.eq(UByte.valueOf(0))).fetchOne();

				if (null == firstWriterBoardRecord) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}

					String errorMessage = "해당 게시글의 최초 작성자 정보가 존재 하지 않습니다";
					throw new ServerServiceException(errorMessage);
				}

				String firstWriterID = firstWriterBoardRecord.getValue(SB_BOARD_HISTORY_TB.REGISTRANT_ID);

				if (! boardModifyReq.getRequestedUserID().equals(firstWriterID)) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}

					String errorMessage = new StringBuilder("타인[").append(firstWriterID).append("] 게시글은 수정 할 수 없습니다")
							.toString();
					throw new ServerServiceException(errorMessage);
				}
			}

			// boardName = boardInforRecord.get(SB_BOARD_INFO_TB.BOARD_NAME);

			Record4<UInteger, Byte, UByte, String> boardRecord = create
					.select(SB_BOARD_TB.PARENT_NO, SB_BOARD_TB.BOARD_ST, SB_BOARD_TB.NEXT_ATTACHED_FILE_SQ,
							SB_BOARD_TB.PWD_BASE64)
					.from(SB_BOARD_TB).where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.BOARD_NO.eq(boardNo))
					.forUpdate().fetchOne();

			if (null == boardRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("해당 게시글이 존재 하지 않습니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			UInteger parenetNo = boardRecord.getValue(SB_BOARD_TB.PARENT_NO);
			byte boardStateValue = boardRecord.getValue(SB_BOARD_TB.BOARD_ST);
			UByte nextAttachedFileSeq = boardRecord.getValue(SB_BOARD_TB.NEXT_ATTACHED_FILE_SQ);
			String dbPwdHashBase64 = boardRecord.getValue(SB_BOARD_TB.PWD_BASE64);

			if (BoardListType.ONLY_GROUP_ROOT.equals(boardListType) && (parenetNo.longValue() == 0L)) {
				try {
					ValueChecker.checkValidSubject(boardModifyReq.getSubject());
				} catch (IllegalArgumentException e) {
					try {
						conn.rollback();
					} catch (Exception e1) {
						log.warn("fail to rollback");
					}

					String errorMessage = e.getMessage();
					throw new ServerServiceException(errorMessage);
				}
			}

			BoardStateType boardStateType = null;
			try {
				boardStateType = BoardStateType.valueOf(boardStateValue);
			} catch (IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("게시글의 DB 상태 값[").append(boardStateValue).append("]이 잘못되었습니다")
						.toString();
				throw new ServerServiceException(errorMessage);
			}

			if (!BoardStateType.OK.equals(boardStateType)) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = null;

				if (BoardStateType.DELETE.equals(boardStateType)) {
					errorMessage = "해당 게시글은 삭제된 글입니다";
				} else if (BoardStateType.BLOCK.equals(boardStateType)) {
					errorMessage = "해당 게시글은 관리자에 의해 차단된 글입니다";
				} else if (BoardStateType.TREEBLOCK.equals(boardStateType)) {
					errorMessage = "해당 게시글은 관리자에 의해 차단된 글을 루트로 하는 트리에 속한 글입니다";
				} else {
					errorMessage = new StringBuilder().append("해당 게시글 상태[").append(boardStateType.getName())
							.append("]가 정상이 아닙니다").toString();
				}

				throw new ServerServiceException(errorMessage);
			}

			if (boardModifyReq.getNextAttachedFileSeq() != nextAttachedFileSeq.shortValue()) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder().append("입력 메시지로 받은 '다음 첨부 파일 시퀀스 번호'[")
						.append(boardModifyReq.getNextAttachedFileSeq()).append("]가 DB 값[")
						.append(nextAttachedFileSeq.shortValue()).append("]과 다릅니다").toString();

				throw new ServerServiceException(errorMessage);
			}
			
			if (MemberRoleType.GUEST.equals(memberRoleTypeOfRequestedUserID)) {
				/** 손님 자신이 수정 요청한 경우 설정한 비밀번호가 없다면 수정 불가 */
				if (null == dbPwdHashBase64) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}

					String errorMessage = "손님으로 작성한 게시글의 비밀번호가 없습니다";

					throw new ServerServiceException(errorMessage);
				}
			}

			if ((null != dbPwdHashBase64)  && ! MemberRoleType.ADMIN.equals(memberRoleTypeOfRequestedUserID)) {
				/** 관리자가 아닌 경우 설정한 비밀번호가 있다면 게시글 비밀번호 일치 검사 */
				String pwdHashBase64 = boardModifyReq.getPwdHashBase64();
				
				if (null == pwdHashBase64) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}
					
					String errorMessage = "게시글에 대한 비밀번호를 입력해 주세요";
					throw new ServerServiceException(errorMessage);
				}

				if (pwdHashBase64.isEmpty()) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}
					
					String errorMessage = "설정한 게시글 비밀 번호가 존재합니다, 게시글 비밀번호를 입력해 주세요";
					throw new ServerServiceException(errorMessage);
				}

				if (!dbPwdHashBase64.equals(pwdHashBase64)) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}
					
					String errorMessage = "설정한 게시글 비밀 번호가 일치하지 않습니다";
					throw new ServerServiceException(errorMessage);
				}
			}

			create.update(SB_BOARD_TB).set(SB_BOARD_TB.NEXT_ATTACHED_FILE_SQ, UByte.valueOf(newNextAttachedFileSeq))
					.where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.BOARD_NO.eq(boardNo)).execute();

			UByte oldBoardHistorySeq = create.select(SB_BOARD_HISTORY_TB.HISTORY_SQ.max()).from(SB_BOARD_HISTORY_TB)
					.where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(boardNo))
					.fetchOne().value1();
			
			if (oldBoardHistorySeq.shortValue() == CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "수정 가능한 최대 횟수(255회)까지 수정하여 더 이상 수정할 수 없습니다";
				throw new ServerServiceException(errorMessage);
			}
			
			UByte newBoardHistorySeq = UByte.valueOf(oldBoardHistorySeq.shortValue()+1);

			InsertSetMoreStep<SbBoardHistoryTbRecord> boardHistoryInsertSetMoreStep = null;

			if (BoardListType.ONLY_GROUP_ROOT.equals(boardListType) && (parenetNo.longValue() != 0L)) {
				boardHistoryInsertSetMoreStep = create.insertInto(SB_BOARD_HISTORY_TB)
						.set(SB_BOARD_HISTORY_TB.BOARD_ID, boardID).set(SB_BOARD_HISTORY_TB.BOARD_NO, boardNo)
						.set(SB_BOARD_HISTORY_TB.HISTORY_SQ, newBoardHistorySeq)
						// .set(SB_BOARD_HISTORY_TB.SUBJECT, boardModifyReq.getSubject())
						.set(SB_BOARD_HISTORY_TB.CONTENTS, boardModifyReq.getContents())
						.set(SB_BOARD_HISTORY_TB.REGISTRANT_ID, boardModifyReq.getRequestedUserID())
						.set(SB_BOARD_HISTORY_TB.IP, boardModifyReq.getIp())
						.set(SB_BOARD_HISTORY_TB.REG_DT, JooqSqlUtil.getFieldOfSysDate(Timestamp.class));
			} else {
				boardHistoryInsertSetMoreStep = create.insertInto(SB_BOARD_HISTORY_TB)
						.set(SB_BOARD_HISTORY_TB.BOARD_ID, boardID).set(SB_BOARD_HISTORY_TB.BOARD_NO, boardNo)
						.set(SB_BOARD_HISTORY_TB.HISTORY_SQ, newBoardHistorySeq)
						.set(SB_BOARD_HISTORY_TB.SUBJECT, boardModifyReq.getSubject())
						.set(SB_BOARD_HISTORY_TB.CONTENTS, boardModifyReq.getContents())
						.set(SB_BOARD_HISTORY_TB.REGISTRANT_ID, boardModifyReq.getRequestedUserID())
						.set(SB_BOARD_HISTORY_TB.IP, boardModifyReq.getIp())
						.set(SB_BOARD_HISTORY_TB.REG_DT, JooqSqlUtil.getFieldOfSysDate(Timestamp.class));
			}

			int boardHistoryInsertCount = boardHistoryInsertSetMoreStep.execute();

			if (0 == boardHistoryInsertCount) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				String errorMessage = "게시판 이력 테이블에 글 내용을 저장하는데 실패하였습니다";
				throw new ServerServiceException(errorMessage);
			}

			Result<Record1<UByte>> attachFileListRecord = create.select(SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ)
					.from(SB_BOARD_FILELIST_TB).where(SB_BOARD_FILELIST_TB.BOARD_ID.eq(boardID))
					.and(SB_BOARD_FILELIST_TB.BOARD_NO.eq(boardNo)).fetch();

			if (attachFileListRecord.isEmpty()) {
				/**
				 * 첨부 파일이 실제 DB 에 없는데 1개 이상의 원소를 갖는 구 첨부 파일 목록을 파라미터 받았을 경우 에러 처리
				 */
				if (!remainingOldAttachedFileSequeceSet.isEmpty()) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}

					String errorMessage = "구 첨부 파일들이 존재 하지 않는데 보존을 원하는 구 첨부 파일 목록을 요청하셨습니다";
					throw new ServerServiceException(errorMessage);
				}
			} else {
				for (Record attachFileRecord : attachFileListRecord) {
					UByte attachedFileSeq = attachFileRecord.get(SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ);
					deletedAttachedFileSequeceSet.add(attachedFileSeq.shortValue());
				}

				if (!deletedAttachedFileSequeceSet.containsAll(remainingOldAttachedFileSequeceSet)) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}

					String errorMessage = new StringBuilder("보존을 원하는 구 첨부 파일 목록[=")
							.append(remainingOldAttachedFileSequeceSet.toString()).append("]중 실제 구 첨부 파일 목록[=")
							.append(deletedAttachedFileSequeceSet.toString()).append("]에 존재하지 않는 첨부 파일이 존재합니다")
							.toString();
					throw new ServerServiceException(errorMessage);
				}

				deletedAttachedFileSequeceSet.removeAll(remainingOldAttachedFileSequeceSet);

				if (!deletedAttachedFileSequeceSet.isEmpty()) {
					create.delete(SB_BOARD_FILELIST_TB).where(SB_BOARD_FILELIST_TB.BOARD_ID.eq(boardID))
							.and(SB_BOARD_FILELIST_TB.BOARD_NO.eq(boardNo))
							.and(SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ.in(deletedAttachedFileSequeceSet)).execute();
				}
			}

			if (boardModifyReq.getNewAttachedFileCnt() > 0) {
				int newAttachedFileListIndex = 0;
				int newAttachedFileSeq = nextAttachedFileSeq.shortValue();
				for (BoardModifyReq.NewAttachedFile newAttachedFile : boardModifyReq.getNewAttachedFileList()) {
					int boardFileListInsertCount = create.insertInto(SB_BOARD_FILELIST_TB)
							.set(SB_BOARD_FILELIST_TB.BOARD_ID, boardID).set(SB_BOARD_FILELIST_TB.BOARD_NO, boardNo)
							.set(SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ, UByte.valueOf(newAttachedFileSeq))
							.set(SB_BOARD_FILELIST_TB.ATTACHED_FNAME, newAttachedFile.getAttachedFileName())
							.set(SB_BOARD_FILELIST_TB.ATTACHED_FSIZE, newAttachedFile.getAttachedFileSize()).execute();

					if (0 == boardFileListInsertCount) {
						try {
							conn.rollback();
						} catch (Exception e) {
							log.warn("fail to rollback");
						}
						String errorMessage = new StringBuilder().append("게시판 ").append(newAttachedFileListIndex)
								.append(" 번째 첨부 파일을  저장하는데 실패하였습니다").toString();

						throw new ServerServiceException(errorMessage);
					}

					newAttachedFileSeq++;
					newAttachedFileListIndex++;
				}
			}

			conn.commit();

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

		BoardModifyRes boardModifyRes = new BoardModifyRes();

		boardModifyRes.setBoardID(boardID.shortValue());
		boardModifyRes.setBoardNo(boardNo.longValue());

		List<BoardModifyRes.DeletedAttachedFile> deletedAttachedFileList = new ArrayList<BoardModifyRes.DeletedAttachedFile>();

		for (Short deletedAttachedFileSequece : deletedAttachedFileSequeceSet) {
			BoardModifyRes.DeletedAttachedFile deletedAttachedFile = new BoardModifyRes.DeletedAttachedFile();
			deletedAttachedFile.setAttachedFileSeq(deletedAttachedFileSequece);
			deletedAttachedFileList.add(deletedAttachedFile);
		}

		boardModifyRes.setDeletedAttachedFileCnt(deletedAttachedFileList.size());
		boardModifyRes.setDeletedAttachedFileList(deletedAttachedFileList);

		return boardModifyRes;
	}
}
