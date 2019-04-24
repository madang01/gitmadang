package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;
import static kr.pe.codda.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.jooq.tables.SbBoardVoteTb.SB_BOARD_VOTE_TB;
import static kr.pe.codda.jooq.tables.SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Record6;
import org.jooq.Record8;
import org.jooq.Result;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardMoveReq.BoardMoveReq;
import kr.pe.codda.impl.message.BoardMoveRes.BoardMoveRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.lib.BoardListType;
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardMoveReqServerTask extends AbstractServerTask {

	public BoardMoveReqServerTask() throws DynamicClassCallException {
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
					(BoardMoveReq) inputMessage);
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

			sendErrorOutputMessage("게시글 이동 처리가 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}
	
	private void checkValidAllArgument(BoardMoveReq boardMoveReq) throws  ServerServiceException {		
		try {
			ValueChecker.checkValidRequestedUserID(boardMoveReq.getRequestedUserID());
			ValueChecker.checkValidIP(boardMoveReq.getIp());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}		
		
		if (boardMoveReq.getSourceBoardID() < 0) {
			String errorMessage = new StringBuilder("이동 전 게시판 식별자[").append(boardMoveReq.getSourceBoardID())
					.append("]가 0보다 작습니다").toString();
			throw new ServerServiceException(errorMessage);
		}

		if (boardMoveReq.getSourceBoardID() > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = new StringBuilder("이동 전 게시판 식별자[").append(boardMoveReq.getSourceBoardID())
					.append("]가 unsigned byte 최대값 255 보다 큽니다").toString();
			throw new ServerServiceException(errorMessage);
		}

		if (boardMoveReq.getSourceBoardNo() <= 0) {
			String errorMessage = new StringBuilder("이동 전 게시글 번호[").append(boardMoveReq.getSourceBoardNo())
					.append("]가 0 보다 작습니다").toString();
			throw new ServerServiceException(errorMessage);
		}

		if (boardMoveReq.getSourceBoardNo() > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			String errorMessage = new StringBuilder("이동 전 게시글 번호[").append(boardMoveReq.getSourceBoardNo())
					.append("]가 unsigned integer 최대값 4294967295 보다 큽니다").toString();
			throw new ServerServiceException(errorMessage);
		}

		if (boardMoveReq.getTargetBoardID() < 0) {
			String errorMessage = new StringBuilder("이동 후 게시판 식별자[").append(boardMoveReq.getTargetBoardID())
					.append("]가 0보다 작습니다").toString();
			throw new ServerServiceException(errorMessage);
		}

		if (boardMoveReq.getTargetBoardID() > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = new StringBuilder("이동 후 게시판 식별자[").append(boardMoveReq.getTargetBoardID())
					.append("]가 unsigned byte 최대값 255 보다 큽니다").toString();
			throw new ServerServiceException(errorMessage);
		}
		
		if (boardMoveReq.getTargetBoardID() == boardMoveReq.getSourceBoardID()) {
			String errorMessage = "동일한 게시판으로 이동할 수 없습니다";
			throw new ServerServiceException(errorMessage);
		}
				
	}

	public BoardMoveRes doWork(String dbcpName, BoardMoveReq boardMoveReq) throws Exception {
		// FIXME!
		log.info(boardMoveReq.toString());

		checkValidAllArgument(boardMoveReq);			

		final UByte sourceBoardID = UByte.valueOf(boardMoveReq.getSourceBoardID());
		final UInteger sourceBoardNo = UInteger.valueOf(boardMoveReq.getSourceBoardNo());
		final UByte targetBoardID = UByte.valueOf(boardMoveReq.getTargetBoardID());
		
		final HashMap<UInteger, UInteger> sourceToTargetBoardNoHash = new HashMap<UInteger, UInteger>();		
		final UInteger zeroUInteger = UInteger.valueOf(0);
		sourceToTargetBoardNoHash.put(zeroUInteger, zeroUInteger);		
		
		final List<BoardMoveRes.BoardMoveInfo> boardMoveInfoList = new ArrayList<BoardMoveRes.BoardMoveInfo>();

		ServerDBUtil.execute(dbcpName, (conn, create) -> {

			@SuppressWarnings("unused")
			MemberRoleType memberRoleTypeOfRequestedUserID = ServerDBUtil.checkUserAccessRights(conn, create, log,
					"게시글 이동 서비스", PermissionType.ADMIN, boardMoveReq.getRequestedUserID());

			Record4<Byte, Byte, Byte, Byte> sourceBoardInforRecord = create
					.select(SB_BOARD_INFO_TB.LIST_TYPE, SB_BOARD_INFO_TB.REPLY_POLICY_TYPE,
							SB_BOARD_INFO_TB.WRITE_PERMISSION_TYPE, SB_BOARD_INFO_TB.REPLY_PERMISSION_TYPE)
					.from(SB_BOARD_INFO_TB).where(SB_BOARD_INFO_TB.BOARD_ID.eq(sourceBoardID))
					.fetchOne();

			if (null == sourceBoardInforRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("이동 전 게시판 식별자[").append(sourceBoardID.shortValue())
						.append("]가 게시판 정보 테이블에 존재하지  않습니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			byte sourceBoardListTypeValue = sourceBoardInforRecord.get(SB_BOARD_INFO_TB.LIST_TYPE);
			byte sourceBoardReplyPolicyTypeValue = sourceBoardInforRecord.get(SB_BOARD_INFO_TB.REPLY_POLICY_TYPE);
			byte sourceBoardWritePermissionTypeValue = sourceBoardInforRecord.get(SB_BOARD_INFO_TB.WRITE_PERMISSION_TYPE);
			byte sourceBoardReplyPermissionTypeValue = sourceBoardInforRecord.get(SB_BOARD_INFO_TB.REPLY_PERMISSION_TYPE);


			BoardListType sourceBoardListType = null;
			try {
				sourceBoardListType = BoardListType.valueOf(sourceBoardListTypeValue);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder()
						.append("이동할  게시글의 ")
						.append(e.getMessage()).toString();
				throw new ServerServiceException(errorMessage);
			}
					
			
			
			/** 이동 후의 게시글 번호를 얻기 위해 레코드 락을 건다 */
			Record6<String, Byte, Byte, Byte, Byte, UInteger> targetBoardInforRecord = create
					.select(SB_BOARD_INFO_TB.BOARD_NAME, SB_BOARD_INFO_TB.LIST_TYPE, SB_BOARD_INFO_TB.REPLY_POLICY_TYPE,
							SB_BOARD_INFO_TB.WRITE_PERMISSION_TYPE, SB_BOARD_INFO_TB.REPLY_PERMISSION_TYPE,
							SB_BOARD_INFO_TB.NEXT_BOARD_NO)
					.from(SB_BOARD_INFO_TB)
					.where(SB_BOARD_INFO_TB.BOARD_ID.eq(targetBoardID))
					.forUpdate().fetchOne();

			if (null == targetBoardInforRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("이동 후 게시판 식별자[").append(targetBoardID.shortValue())
						.append("]가 게시판 정보 테이블에 존재하지  않습니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			String targetBoardName = targetBoardInforRecord.get(SB_BOARD_INFO_TB.BOARD_NAME);
			byte targetBoardListTypeValue = targetBoardInforRecord.get(SB_BOARD_INFO_TB.LIST_TYPE);
			byte targetBoardReplyPolicyTypeValue = targetBoardInforRecord.get(SB_BOARD_INFO_TB.REPLY_POLICY_TYPE);
			byte targetBoardWritePermissionTypeValue = targetBoardInforRecord.get(SB_BOARD_INFO_TB.WRITE_PERMISSION_TYPE);
			byte targetBoardReplyPermissionTypeValue = targetBoardInforRecord.get(SB_BOARD_INFO_TB.REPLY_PERMISSION_TYPE);
			/** 게시글 번호를 오름 차순으로 정렬시 첫번째는 본문글로 그룹의 루트가 되며 게시글 번호 오름 차순은 그룹에 속한 글의 직성된 시간 순서이다 */
			UInteger targetGroupNo = targetBoardInforRecord.get(SB_BOARD_INFO_TB.NEXT_BOARD_NO);
			long targetNextBoardNo = targetGroupNo.longValue();
			

			if ((sourceBoardListTypeValue != targetBoardListTypeValue)
					|| (sourceBoardReplyPolicyTypeValue != targetBoardReplyPolicyTypeValue)
					|| (sourceBoardWritePermissionTypeValue != targetBoardWritePermissionTypeValue)
					|| (sourceBoardReplyPermissionTypeValue != targetBoardReplyPermissionTypeValue)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = "이동 하고자 하는 목적지의 게시판 유형이 일치 하지 않아 이동할 수 없습니다";
				throw new ServerServiceException(errorMessage);
			}

			/** 수정할 게시글에 속한 그룹의 루트 노드에 해당하는 레코드에 락을 건다 */
			UInteger sourceGroupNo = ServerDBUtil.lockGroupOfGivenBoard(conn, create, log, sourceBoardID,
					sourceBoardNo);

			if (! sourceBoardNo.equals(sourceGroupNo)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = "이동할 게시글이 그룹의 루트인 본문글이 아닙니다";
				throw new ServerServiceException(errorMessage);
			}			
			
			/**
			 * WARNING! 게시글 번호 오름 차순은 게시글 작성 순서이다. 이 순서로 게시글 번호를 할당하면 부모 글 부터 먼저 글 번호가 확정되는것을 보장할 수 있다.
			 *          부모 글 번호를 확정해야 이동이 가능하므로 부모 글 번호 부터 먼저 확정되는것을 보장하는 '게시글 번호 오름 차순'을 꼭 지킬것.
			 */
			Result<Record8<UInteger, UShort, UInteger, UByte, Integer, Byte, UByte, String>>  
			boardResult = create.select(SB_BOARD_TB.BOARD_NO,
					SB_BOARD_TB.GROUP_SQ,
					SB_BOARD_TB.PARENT_NO, SB_BOARD_TB.DEPTH,
					SB_BOARD_TB.VIEW_CNT, SB_BOARD_TB.BOARD_ST, 
					SB_BOARD_TB.NEXT_ATTACHED_FILE_SQ,
					SB_BOARD_TB.PWD_BASE64).from(SB_BOARD_TB).where(SB_BOARD_TB.BOARD_ID.eq(sourceBoardID))
			.and(SB_BOARD_TB.GROUP_NO.eq(sourceGroupNo))
			.orderBy(SB_BOARD_TB.BOARD_NO.asc()).fetch();			
			
			for (Record8<UInteger, UShort, UInteger, UByte, Integer, Byte, UByte, String> boardRearcod : boardResult) {
				UInteger fromBoardNo = boardRearcod.get(SB_BOARD_TB.BOARD_NO);
				UShort fromGroupSeqence = boardRearcod.get(SB_BOARD_TB.GROUP_SQ);
				UInteger fromParentNo = boardRearcod.get(SB_BOARD_TB.PARENT_NO);
				UByte fromDepth = boardRearcod.get(SB_BOARD_TB.DEPTH);
				int fromViewCount = boardRearcod.get(SB_BOARD_TB.VIEW_CNT);
				byte fromBoardState = boardRearcod.getValue(SB_BOARD_TB.BOARD_ST);
				UByte fromNextAttachedFileSeq = boardRearcod.getValue(SB_BOARD_TB.NEXT_ATTACHED_FILE_SQ);
				String fromBoardPwdBase64 = boardRearcod.get(SB_BOARD_TB.PWD_BASE64);
				
				BoardStateType fromBoardStateType = null;
				
				try {
					fromBoardStateType = BoardStateType.valueOf(fromBoardState);
				} catch(IllegalArgumentException e) {
					try {
						conn.rollback();
					} catch (Exception e1) {
						log.warn("fail to rollback");
					}
					
					String errorMessage = new StringBuilder()
							.append("이동할  ")
							.append(e.getMessage()).toString();
					throw new ServerServiceException(errorMessage);
				}
				
				UInteger toBoardNo = UInteger.valueOf(targetNextBoardNo);
				sourceToTargetBoardNoHash.put(fromBoardNo, toBoardNo);
				
				final UInteger toParentNo = sourceToTargetBoardNoHash.get(fromParentNo);									
				if (null == toParentNo) {
					/**
					 * dead code
					 */
					
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}
					
					String errorMessage = "'이전 이후 게시글 번호 매칭 해쉬'에서 부모글 못찾음, \"게시글 번호 오름 차순은 그룹에 속한 글의 작성된 시간 순서다\" 라는 명제가 깨짐";
					
					// log.warn(errorMessage);
					
					throw new ServerServiceException(errorMessage);
				}
				
				create.insertInto(SB_BOARD_TB).set(SB_BOARD_TB.BOARD_ID, targetBoardID)
						.set(SB_BOARD_TB.BOARD_NO, toBoardNo)
						.set(SB_BOARD_TB.GROUP_NO, targetGroupNo)
						.set(SB_BOARD_TB.GROUP_SQ, fromGroupSeqence)
						.set(SB_BOARD_TB.PARENT_NO, toParentNo)
						.set(SB_BOARD_TB.DEPTH, fromDepth)
						.set(SB_BOARD_TB.VIEW_CNT, fromViewCount)
						.set(SB_BOARD_TB.BOARD_ST, fromBoardState)
						.set(SB_BOARD_TB.NEXT_ATTACHED_FILE_SQ, fromNextAttachedFileSeq)
						.set(SB_BOARD_TB.PWD_BASE64, fromBoardPwdBase64)
						.execute();
				
				create.update(SB_BOARD_FILELIST_TB)
				.set(SB_BOARD_FILELIST_TB.BOARD_ID, targetBoardID)
				.set(SB_BOARD_FILELIST_TB.BOARD_NO, toBoardNo)
				.where(SB_BOARD_FILELIST_TB.BOARD_ID.eq(sourceBoardID))
				.and(SB_BOARD_FILELIST_TB.BOARD_NO.eq(fromBoardNo))
				.execute();
				
				create.update(SB_BOARD_HISTORY_TB)
				.set(SB_BOARD_HISTORY_TB.BOARD_ID, targetBoardID)
				.set(SB_BOARD_HISTORY_TB.BOARD_NO, toBoardNo)
				.where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(sourceBoardID))
				.and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(fromBoardNo))
				.execute();
				
				create.update(SB_BOARD_VOTE_TB)
				.set(SB_BOARD_VOTE_TB.BOARD_ID, targetBoardID)
				.set(SB_BOARD_VOTE_TB.BOARD_NO, toBoardNo)
				.where(SB_BOARD_VOTE_TB.BOARD_ID.eq(sourceBoardID))
				.and(SB_BOARD_VOTE_TB.BOARD_NO.eq(fromBoardNo))
				.execute();
				
				create.update(SB_MEMBER_ACTIVITY_HISTORY_TB)
				.set(SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_ID, targetBoardID)
				.set(SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_NO, toBoardNo)
				.where(SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_ID.eq(sourceBoardID))
				.and(SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_NO.eq(fromBoardNo))
				.execute();
				
				create.delete(SB_BOARD_TB)
				.where(SB_BOARD_TB.BOARD_ID.eq(sourceBoardID))
				.and(SB_BOARD_TB.BOARD_NO.eq(fromBoardNo))
				.execute();
				
				long countOfBoard  = 0; 
			
				if (BoardStateType.OK.equals(fromBoardStateType)) {
					if (BoardListType.ONLY_GROUP_ROOT.equals(sourceBoardListType)) {
						if (fromBoardNo.equals(sourceGroupNo)) {
							countOfBoard = 1;
						}
					} else {
						countOfBoard = 1;
					}
				}
				
				create.update(SB_BOARD_INFO_TB)
				.set(SB_BOARD_INFO_TB.CNT, SB_BOARD_INFO_TB.CNT.sub(countOfBoard))
				.set(SB_BOARD_INFO_TB.TOTAL, SB_BOARD_INFO_TB.TOTAL.sub(1))
				.where(SB_BOARD_INFO_TB.BOARD_ID.eq(sourceBoardID))
				.execute();
				
				create.update(SB_BOARD_INFO_TB)
				.set(SB_BOARD_INFO_TB.CNT, SB_BOARD_INFO_TB.CNT.add(countOfBoard))
				.set(SB_BOARD_INFO_TB.TOTAL, SB_BOARD_INFO_TB.TOTAL.add(1))
				.where(SB_BOARD_INFO_TB.BOARD_ID.eq(targetBoardID))
				.execute();
				
				List<BoardMoveRes.BoardMoveInfo.AttachedFile> attachedFileList = new ArrayList<BoardMoveRes.BoardMoveInfo.AttachedFile>();
				
				Result<Record1<UByte>> attachedFileResult = create.select(SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ)
				.from(SB_BOARD_FILELIST_TB)
				.where(SB_BOARD_FILELIST_TB.BOARD_ID.eq(targetBoardID))
				.and(SB_BOARD_FILELIST_TB.BOARD_NO.eq(toBoardNo))
				.fetch();
				
				for (Record1<UByte> attachedFileRecord : attachedFileResult) {
					UByte attachedFileSeq = attachedFileRecord.get(SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ);
					
					BoardMoveRes.BoardMoveInfo.AttachedFile attachedFile = new BoardMoveRes.BoardMoveInfo.AttachedFile();
					attachedFile.setAttachedFileSeq(attachedFileSeq.shortValue());
					
					attachedFileList.add(attachedFile);
				}
				
				BoardMoveRes.BoardMoveInfo boardMoveInfo = new BoardMoveRes.BoardMoveInfo();
				boardMoveInfo.setFromBoardNo(fromBoardNo.longValue());
				boardMoveInfo.setToBoardNo(toBoardNo.longValue());
				boardMoveInfo.setAttachedFileCnt(attachedFileList.size());
				boardMoveInfo.setAttachedFileList(attachedFileList);
				
				boardMoveInfoList.add(boardMoveInfo);

				targetNextBoardNo++;
				
				log.info("이동전 게시글[boardID={}, boardNo={}] ---> 이동후 게시글[boardID={}, boardNo={}]",
						sourceBoardID, fromBoardNo, targetBoardID, toBoardNo);
			}

			create.update(SB_BOARD_INFO_TB)
			.set(SB_BOARD_INFO_TB.NEXT_BOARD_NO, UInteger.valueOf(targetNextBoardNo))
			.where(SB_BOARD_INFO_TB.BOARD_ID.eq(targetBoardID)).execute();
			
			conn.commit();
			
			StringBuilder siteLogStringBuilder = new StringBuilder();
			siteLogStringBuilder.append("게시글[boardID=")
			.append(sourceBoardID)
			.append(", boardNo=")
			.append(sourceBoardNo)
			.append("]에 대한 '")
			.append(targetBoardName)
			.append("' 게시판[boardID=")
			.append(targetBoardID)			
			.append("] 이동 처리가 완료되었습니다");
			
			ServerDBUtil.insertSiteLog(conn, create, log, boardMoveReq.getRequestedUserID(), siteLogStringBuilder.toString(), 
					new java.sql.Timestamp(System.currentTimeMillis()), boardMoveReq.getIp());
			
			conn.commit();			
		});
		
		BoardMoveRes boardMoveRes = new BoardMoveRes();
		boardMoveRes.setSourceBoardID(boardMoveReq.getSourceBoardID());
		boardMoveRes.setSourceBoardNo(boardMoveReq.getSourceBoardNo());
		boardMoveRes.setTargetBoardID(boardMoveReq.getTargetBoardID());
		boardMoveRes.setCnt(boardMoveInfoList.size());
		boardMoveRes.setBoardMoveInfoList(boardMoveInfoList);

		return boardMoveRes;
	}
}
