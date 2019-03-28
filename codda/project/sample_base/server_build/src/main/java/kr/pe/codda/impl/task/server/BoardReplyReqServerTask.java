package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.jooq.tables.records.SbBoardHistoryTbRecord;
import kr.pe.codda.impl.jooq.tables.records.SbBoardTbRecord;
import kr.pe.codda.impl.message.BoardReplyReq.BoardReplyReq;
import kr.pe.codda.impl.message.BoardReplyRes.BoardReplyRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.BoardListType;
import kr.pe.codda.server.lib.BoardReplyPolicyType;
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.MemberActivityType;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardReplyReqServerTask extends AbstractServerTask {

	public BoardReplyReqServerTask() throws DynamicClassCallException {
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
					(BoardReplyReq) inputMessage);
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

		try {
			ValueChecker.checkValidParentBoardNo(boardReplyReq.getParentBoardNo());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ValueChecker.checkValidBoardPasswordHashBase64(boardReplyReq.getPwdHashBase64());
		} catch (RuntimeException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}

		try {
			ValueChecker.checkValidContents(boardReplyReq.getContents());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}

		try {
			ValueChecker.checkValidUserID(boardReplyReq.getRequestedUserID());
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

		if (boardReplyReq.getNewAttachedFileCnt() > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = new StringBuilder().append("첨부 파일 등록 갯수[")
					.append(boardReplyReq.getNewAttachedFileCnt()).append("]가 unsgiend byte 최대값[")
					.append(CommonStaticFinalVars.UNSIGNED_BYTE_MAX).append("]을 초과하였습니다").toString();
			throw new ServerServiceException(errorMessage);
		}

		if (boardReplyReq.getNewAttachedFileCnt() > ServerCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT) {
			String errorMessage = new StringBuilder().append("첨부 파일 등록 갯수[")
					.append(boardReplyReq.getNewAttachedFileCnt()).append("]가 첨부 파일 최대 갯수[")
					.append(ServerCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT).append("]를 초과하였습니다")
					.toString();
			throw new ServerServiceException(errorMessage);
		}

		if (boardReplyReq.getNewAttachedFileCnt() > 0) {
			int newAttachedFileCnt = boardReplyReq.getNewAttachedFileCnt();
			List<BoardReplyReq.NewAttachedFile> newAttachedFileList = boardReplyReq.getNewAttachedFileList();

			for (int i = 0; i < newAttachedFileCnt; i++) {
				BoardReplyReq.NewAttachedFile newAttachedFile = newAttachedFileList.get(i);
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

		UByte boardID = UByte.valueOf(boardReplyReq.getBoardID());
		UInteger parentBoardNo = UInteger.valueOf(boardReplyReq.getParentBoardNo());
		UInteger boardNo = null;
		String pwdHashBase64 = boardReplyReq.getPwdHashBase64();
		if (null == pwdHashBase64) {
			pwdHashBase64 = "";
		}

		DataSource dataSource = DBCPManager.getInstance().getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));

			/**
			 * '게시판 식별자 정보'(SB_BOARD_INFO_TB) 테이블에는 '다음 게시판 번호'가 있어 락을 건후 1 증가 시키고 가져온 값은 '게시판 번호'로 사용한다
			 */
			Record5<String, Byte, Byte, Byte, UInteger> boardInforRecord = create
					.select(SB_BOARD_INFO_TB.BOARD_NAME, SB_BOARD_INFO_TB.LIST_TYPE, SB_BOARD_INFO_TB.REPLY_POLICY_TYPE,
							SB_BOARD_INFO_TB.REPLY_PERMISSION_TYPE, SB_BOARD_INFO_TB.NEXT_BOARD_NO)
					.from(SB_BOARD_INFO_TB).where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).forUpdate().fetchOne();

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

			String boardName = boardInforRecord.get(SB_BOARD_INFO_TB.BOARD_NAME);
			byte boardListTypeValue = boardInforRecord.get(SB_BOARD_INFO_TB.LIST_TYPE);
			byte boardReplyPolicyTypeValue = boardInforRecord.get(SB_BOARD_INFO_TB.REPLY_POLICY_TYPE);
			byte boardReplyPermssionTypeValue = boardInforRecord.get(SB_BOARD_INFO_TB.REPLY_PERMISSION_TYPE);
			boardNo = boardInforRecord.get(SB_BOARD_INFO_TB.NEXT_BOARD_NO);

			if (boardNo.longValue() == CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("지정한 게시판[").append(boardID.shortValue())
						.append("]은 최대 갯수까지 글이 등록되어 더 이상 글을 추가 할 수 없습니다").toString();
				throw new ServerServiceException(errorMessage);
			}

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
					ValueChecker.checkValidSubject(boardReplyReq.getSubject());
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

			BoardReplyPolicyType boardReplyPolicyType = null;
			try {
				boardReplyPolicyType = BoardReplyPolicyType.valueOf(boardReplyPolicyTypeValue);
			} catch (IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = e.getMessage();
				throw new ServerServiceException(errorMessage);
			}

			PermissionType boardReplyPermissionType = null;

			try {
				boardReplyPermissionType = PermissionType.valueOf(boardReplyPermssionTypeValue);
			} catch (IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = e.getMessage();
				throw new ServerServiceException(errorMessage);
			}

			if (BoardReplyPolicyType.NO_SUPPORTED.equals(boardReplyPolicyType)) {
				// 댓글 미 지원
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder().append(boardName).append(" 게시판[").append(boardID)
						.append("]은 댓글을 쓸 수 없습니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			MemberRoleType memberRoleTypeOfRequestedUserID = ServerDBUtil.checkUserAccessRights(conn, create, log, "게시판 댓글 등록 서비스",
					boardReplyPermissionType, boardReplyReq.getRequestedUserID());

			if (MemberRoleType.GUEST.equals(memberRoleTypeOfRequestedUserID)) {
				if (pwdHashBase64.isEmpty()) {
					try {
						conn.rollback();
					} catch (Exception e1) {
						log.warn("fail to rollback");
					}

					String errorMessage = "손님의 경우 반듯이 게시글에 대한 비밀번호를 입력해야 합니다";
					throw new ServerServiceException(errorMessage);
				}

				try {
					CommonStaticUtil.Base64Decoder.decode(pwdHashBase64);
				} catch (IllegalArgumentException e) {
					try {
						conn.rollback();
					} catch (Exception e1) {
						log.warn("fail to rollback");
					}

					String errorMessage = "게시글에 대한 비밀번호가 base64 인코딩 문자열이 아닙니다";
					throw new ServerServiceException(errorMessage);
				}
			} else {
				if (!pwdHashBase64.isEmpty()) {
					try {
						CommonStaticUtil.Base64Decoder.decode(pwdHashBase64);
					} catch (IllegalArgumentException e) {
						try {
							conn.rollback();
						} catch (Exception e1) {
							log.warn("fail to rollback");
						}

						String errorMessage = "게시글에 대한 비밀번호가 base64 인코딩 문자열이 아닙니다";
						throw new ServerServiceException(errorMessage);
					}

				}
			}

			create.update(SB_BOARD_INFO_TB).set(SB_BOARD_INFO_TB.NEXT_BOARD_NO, SB_BOARD_INFO_TB.NEXT_BOARD_NO.add(1))
					.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).execute();
			
			conn.commit();

			
			Record4<UInteger, UShort, UInteger, UByte> parentBoardRecord = create
					.select(SB_BOARD_TB.GROUP_NO, SB_BOARD_TB.GROUP_SQ, SB_BOARD_TB.PARENT_NO, SB_BOARD_TB.DEPTH).from(SB_BOARD_TB)
					.where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.BOARD_NO.eq(parentBoardNo)).fetchOne();

			if (null == parentBoardRecord) {
				String errorMessage = new StringBuilder().append("부모글[").append(parentBoardNo).append("] 이 존재하지 않습니다")
						.toString();
				throw new ServerServiceException(errorMessage);
			}

			UInteger groupNoOfParentBoard = parentBoardRecord.getValue(SB_BOARD_TB.GROUP_NO);
			UShort groupSeqOfParentBoard = parentBoardRecord.getValue(SB_BOARD_TB.GROUP_SQ);
			UInteger parentNoOfParentBoard = parentBoardRecord.getValue(SB_BOARD_TB.PARENT_NO);
			UByte depthOfParentBoard = parentBoardRecord.getValue(SB_BOARD_TB.DEPTH);
			UByte nextAttachedFileSeq = UByte.valueOf(boardReplyReq.getNewAttachedFileCnt());

			if (BoardReplyPolicyType.ONLY_ROOT.equals(boardReplyPolicyType)) {
				// 본문에만 댓글
				if (0L != parentNoOfParentBoard.longValue()) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}

					String errorMessage = new StringBuilder().append(boardName).append(" 게시판[").append(boardID)
							.append("]은 본문에대한 댓글만이 허용되었습니다").toString();
					throw new ServerServiceException(errorMessage);
				}
			}

			/**
			 * 댓글은 부모가 속한 그룹의 순서를 조정하므로 그룹 전체에 대해서 동기화가 필요하기때문에 부모가 속한 그룹 최상위 글에 대해서 락을 건다
			 */
			Record1<UInteger> rootBoardRecord = create.select(SB_BOARD_TB.BOARD_NO).from(SB_BOARD_TB)
					.where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.BOARD_NO.eq(groupNoOfParentBoard))
					.forUpdate().fetchOne();

			if (null == rootBoardRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder().append("그룹 최상위 글[boardID=").append(boardID.shortValue())
						.append(", boardNo=").append(groupNoOfParentBoard.longValue()).append("] 이 존재하지 않습니다")
						.toString();
				throw new ServerServiceException(errorMessage);
			}

			/*
			UShort toGroupSeq = ServerDBUtil.getToGroupSeqOfRelativeRootBoard(create, boardID, groupSeqOfParentBoard,
					parentNoOfParentBoard);
					*/
			
			UShort toGroupSeq = ServerDBUtil.getToGroupSeqOfRelativeRootBoard(create, boardID, groupNoOfParentBoard, groupSeqOfParentBoard, depthOfParentBoard);
			
			
			Table<Record3<UByte, UInteger, UShort>>  b = create.select(SB_BOARD_TB.BOARD_ID, SB_BOARD_TB.GROUP_NO, SB_BOARD_TB.GROUP_SQ)
			.from(SB_BOARD_TB.forceIndex("sb_board_idx1"))
			.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
			.and(SB_BOARD_TB.GROUP_NO.eq(groupNoOfParentBoard))
			.and(SB_BOARD_TB.GROUP_SQ.ge(toGroupSeq))
			.orderBy(SB_BOARD_TB.GROUP_SQ.desc()).asTable("b");
			
			create.update(SB_BOARD_TB.innerJoin(b).on(SB_BOARD_TB.BOARD_ID.eq(b.field(SB_BOARD_TB.BOARD_ID)))
					.and(SB_BOARD_TB.GROUP_NO.eq(b.field(SB_BOARD_TB.GROUP_NO)))
					.and(SB_BOARD_TB.GROUP_SQ.eq(b.field(SB_BOARD_TB.GROUP_SQ))))
			.set(SB_BOARD_TB.GROUP_SQ, SB_BOARD_TB.GROUP_SQ.add(1)).execute();			
			
			/*
			create.update(SB_BOARD_TB).set(SB_BOARD_TB.GROUP_SQ, SB_BOARD_TB.GROUP_SQ.add(1))
					.where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.GROUP_NO.eq(groupNoOfParentBoard))
					.and(SB_BOARD_TB.GROUP_SQ.ge(toGroupSeq)).execute();
			*/

			InsertOnDuplicateStep<SbBoardTbRecord> boardInsertOnDuplicateStep = null;
			if (pwdHashBase64.isEmpty()) {
				boardInsertOnDuplicateStep = create
						.insertInto(SB_BOARD_TB, SB_BOARD_TB.BOARD_ID, SB_BOARD_TB.BOARD_NO, SB_BOARD_TB.GROUP_NO,
								SB_BOARD_TB.GROUP_SQ, SB_BOARD_TB.PARENT_NO, SB_BOARD_TB.DEPTH, SB_BOARD_TB.VIEW_CNT,
								SB_BOARD_TB.BOARD_ST, SB_BOARD_TB.NEXT_ATTACHED_FILE_SQ)
						.select(create
								.select(SB_BOARD_TB.BOARD_ID, DSL.val(boardNo).as(SB_BOARD_TB.BOARD_NO),
										SB_BOARD_TB.GROUP_NO, DSL.val(toGroupSeq).as(SB_BOARD_TB.GROUP_SQ),
										DSL.val(UInteger.valueOf(boardReplyReq.getParentBoardNo())).as(
												SB_BOARD_TB.PARENT_NO),
										SB_BOARD_TB.DEPTH.add(1).as(SB_BOARD_TB.DEPTH),
										DSL.val(0).as(SB_BOARD_TB.VIEW_CNT),
										DSL.val(BoardStateType.OK.getValue()).as(SB_BOARD_TB.BOARD_ST),
										DSL.val(nextAttachedFileSeq).as(SB_BOARD_TB.NEXT_ATTACHED_FILE_SQ))
								.from(SB_BOARD_TB)
								.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
								.and(SB_BOARD_TB.BOARD_NO.eq(UInteger.valueOf(boardReplyReq.getParentBoardNo()))));
			} else {
				boardInsertOnDuplicateStep = create
						.insertInto(SB_BOARD_TB, SB_BOARD_TB.BOARD_ID, SB_BOARD_TB.BOARD_NO, SB_BOARD_TB.GROUP_NO,
								SB_BOARD_TB.GROUP_SQ, SB_BOARD_TB.PARENT_NO, SB_BOARD_TB.DEPTH, SB_BOARD_TB.VIEW_CNT,
								SB_BOARD_TB.BOARD_ST, SB_BOARD_TB.NEXT_ATTACHED_FILE_SQ, SB_BOARD_TB.PWD_BASE64)
						.select(create
								.select(SB_BOARD_TB.BOARD_ID, DSL.val(boardNo).as(SB_BOARD_TB.BOARD_NO),
										SB_BOARD_TB.GROUP_NO, DSL.val(toGroupSeq).as(SB_BOARD_TB.GROUP_SQ),
										DSL.val(UInteger.valueOf(boardReplyReq.getParentBoardNo())).as(
												SB_BOARD_TB.PARENT_NO),
										SB_BOARD_TB.DEPTH.add(1).as(SB_BOARD_TB.DEPTH),
										DSL.val(0).as(SB_BOARD_TB.VIEW_CNT),
										DSL.val(BoardStateType.OK.getValue()).as(SB_BOARD_TB.BOARD_ST),
										DSL.val(nextAttachedFileSeq).as(SB_BOARD_TB.NEXT_ATTACHED_FILE_SQ),
										DSL.val(pwdHashBase64).as(SB_BOARD_TB.PWD_BASE64))
								.from(SB_BOARD_TB)
								.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
								.and(SB_BOARD_TB.BOARD_NO.eq(UInteger.valueOf(boardReplyReq.getParentBoardNo()))));
			}

			int boardInsertCount = boardInsertOnDuplicateStep.execute();

			if (0 == boardInsertCount) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				String errorMessage = "댓글 저장하는데 실패하였습니다";
				throw new ServerServiceException(errorMessage);
			}
			
			Timestamp registeredDate = new java.sql.Timestamp(System.currentTimeMillis());

			InsertSetMoreStep<SbBoardHistoryTbRecord> boardHistoryInsertSetMoreStep = null;
			if (BoardListType.ONLY_GROUP_ROOT.equals(boardListType)) {
				boardHistoryInsertSetMoreStep = create.insertInto(SB_BOARD_HISTORY_TB)
						.set(SB_BOARD_HISTORY_TB.BOARD_ID, boardID).set(SB_BOARD_HISTORY_TB.BOARD_NO, boardNo)
						.set(SB_BOARD_HISTORY_TB.HISTORY_SQ, UByte.valueOf(0))
						// .set(SB_BOARD_HISTORY_TB.SUBJECT, boardReplyReq.getSubject())
						.set(SB_BOARD_HISTORY_TB.CONTENTS, boardReplyReq.getContents())
						.set(SB_BOARD_HISTORY_TB.REGISTRANT_ID, boardReplyReq.getRequestedUserID())
						.set(SB_BOARD_HISTORY_TB.IP, boardReplyReq.getIp())
						.set(SB_BOARD_HISTORY_TB.REG_DT, registeredDate);
			} else {
				boardHistoryInsertSetMoreStep = create.insertInto(SB_BOARD_HISTORY_TB)
						.set(SB_BOARD_HISTORY_TB.BOARD_ID, boardID).set(SB_BOARD_HISTORY_TB.BOARD_NO, boardNo)
						.set(SB_BOARD_HISTORY_TB.HISTORY_SQ, UByte.valueOf(0))
						.set(SB_BOARD_HISTORY_TB.SUBJECT, boardReplyReq.getSubject())
						.set(SB_BOARD_HISTORY_TB.CONTENTS, boardReplyReq.getContents())
						.set(SB_BOARD_HISTORY_TB.REGISTRANT_ID, boardReplyReq.getRequestedUserID())
						.set(SB_BOARD_HISTORY_TB.IP, boardReplyReq.getIp())
						.set(SB_BOARD_HISTORY_TB.REG_DT, registeredDate);
			}

			int boardHistoryInsertCount = boardHistoryInsertSetMoreStep.execute();

			if (0 == boardHistoryInsertCount) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				String errorMessage = "댓글 내용을 저장하는데 실패하였습니다";
				throw new ServerServiceException(errorMessage);
			}

			if (boardReplyReq.getNewAttachedFileCnt() > 0) {
				int attachedFileListIndex = 0;
				for (BoardReplyReq.NewAttachedFile newAttachedFile : boardReplyReq.getNewAttachedFileList()) {
					int boardFileListInsertCount = create.insertInto(SB_BOARD_FILELIST_TB)
							.set(SB_BOARD_FILELIST_TB.BOARD_ID, boardID).set(SB_BOARD_FILELIST_TB.BOARD_NO, boardNo)
							.set(SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ, UByte.valueOf(attachedFileListIndex))
							.set(SB_BOARD_FILELIST_TB.ATTACHED_FNAME, newAttachedFile.getAttachedFileName())
							.set(SB_BOARD_FILELIST_TB.ATTACHED_FSIZE, newAttachedFile.getAttachedFileSize()).execute();

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

			if (BoardListType.TREE.equals(boardListType)) {
				// 계층형 목록의 경우 댓글시 목록 갯수와 전체 글수 각각 1증가
				create.update(SB_BOARD_INFO_TB).set(SB_BOARD_INFO_TB.CNT, SB_BOARD_INFO_TB.CNT.add(1))
						.set(SB_BOARD_INFO_TB.TOTAL, SB_BOARD_INFO_TB.TOTAL.add(1))
						.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).execute();
			} else {
				// 그룹 루트 목록의 경우 댓글시 전체 글수만 1 증가
				create.update(SB_BOARD_INFO_TB).set(SB_BOARD_INFO_TB.TOTAL, SB_BOARD_INFO_TB.TOTAL.add(1))
						.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).execute();
			}
			
			
			ServerDBUtil.insertMemberActivityHistory(conn, create, log, boardReplyReq.getRequestedUserID(),
					memberRoleTypeOfRequestedUserID, MemberActivityType.REPLY, boardID, boardNo,
					registeredDate);
			
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

		BoardReplyRes boardReplyRes = new BoardReplyRes();
		boardReplyRes.setBoardID(boardID.shortValue());
		boardReplyRes.setBoardNo(boardNo.longValue());

		return boardReplyRes;
	}
}
