package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.jooq.tables.SbBoardTb.SB_BOARD_TB;

import java.util.HashSet;

import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardUnBlockReq.BoardUnBlockReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.lib.BoardListType;
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardUnBlockReqServerTask extends AbstractServerTask {
	public BoardUnBlockReqServerTask() throws DynamicClassCallException {
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
					(BoardUnBlockReq) inputMessage);
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

			sendErrorOutputMessage("게시글 차단 해제하는데 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}

	public MessageResultRes doWork(String dbcpName, BoardUnBlockReq boardUnBlockReq) throws Exception {

		try {
			ValueChecker.checkValidRequestedUserID(boardUnBlockReq.getRequestedUserID());
			ValueChecker.checkValidIP(boardUnBlockReq.getIp());
			ValueChecker.checkValidBoardID(boardUnBlockReq.getBoardID());		
			ValueChecker.checkValidBoardNo(boardUnBlockReq.getBoardNo());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}

		final String requestedUserID = boardUnBlockReq.getRequestedUserID();
		final UByte boardID = UByte.valueOf(boardUnBlockReq.getBoardID());
		final UInteger boardNo = UInteger.valueOf(boardUnBlockReq.getBoardNo());
		
		StringBuilder resultMessageStringBuilder = new StringBuilder();
		
		ServerDBUtil.execute(dbcpName, (conn, create) -> {
			
			ServerDBUtil.checkUserAccessRights(conn, create, log, "게시글 차단 해제 서비스", PermissionType.ADMIN,
					requestedUserID);

			Record2<String, Byte> boardInforRecord = create
					.select(SB_BOARD_INFO_TB.BOARD_NAME, SB_BOARD_INFO_TB.LIST_TYPE).from(SB_BOARD_INFO_TB)
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

			String boardName = boardInforRecord.get(SB_BOARD_INFO_TB.BOARD_NAME);
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
		
			/** 차단 해제할 게시글에 속한 그룹의 루트 노드에 해당하는 레코드에 락을 건다  */
			UInteger groupNo = ServerDBUtil.lockGroupOfGivenBoard(conn, create, log, boardID, boardNo);

			Record4<UShort, UInteger, UByte, Byte> boardRecord = create
					.select(SB_BOARD_TB.GROUP_SQ, SB_BOARD_TB.PARENT_NO, SB_BOARD_TB.DEPTH, SB_BOARD_TB.BOARD_ST)
					.from(SB_BOARD_TB).where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.BOARD_NO.eq(boardNo))
					.fetchOne();

			if (null == boardRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = "2.해당 게시글이 존재 하지 않습니다";
				throw new ServerServiceException(errorMessage);
			}

			UShort groupSeq = boardRecord.getValue(SB_BOARD_TB.GROUP_SQ);
			UInteger parentNo = boardRecord.getValue(SB_BOARD_TB.PARENT_NO);
			UByte depth = boardRecord.getValue(SB_BOARD_TB.DEPTH);
			byte boardStateValue = boardRecord.getValue(SB_BOARD_TB.BOARD_ST);

			BoardStateType boardStateType = null;
			try {
				boardStateType = BoardStateType.valueOf(boardStateValue);
			} catch (IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("게시글의 상태 값[").append(boardStateValue).append("]이 잘못되었습니다")
						.toString();
				throw new ServerServiceException(errorMessage);
			}

			if (!BoardStateType.BLOCK.equals(boardStateType)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder().append("차단된 글[").append(boardStateType.getName())
						.append("]이 아닙니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			/** 직계 부모 노드의 게시판 상태가 정상인지 여부 검사 */
			UInteger directParentNo = parentNo;
			while (0 != directParentNo.longValue()) {
				/**
				 * 게시글 차단은 게시판 트리 하단부터 상단으로 올라가며 수행되며 게시글 차단 해제는 게시글 차단 역순 즉 상단부터 하단 순으로 수행된다.
				 * 하여 게시글 차단 해제는 오직 직계 부모 노드중 게시판 상태가 정상인 경우에만 수행될 수 있다.
				 */
				Record2<UInteger, Byte> directParentBoardRecord = create
						.select(SB_BOARD_TB.PARENT_NO, SB_BOARD_TB.BOARD_ST).from(SB_BOARD_TB)
						.where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.BOARD_NO.eq(directParentNo))
						.fetchOne();

				if (null == directParentBoardRecord) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}

					String errorMessage = new StringBuilder().append("직계 부모 게시글[boardID=").append(boardID.shortValue())
							.append(", boardNo=").append(directParentNo.longValue()).append("]이 존재 하지 않습니다").toString();
					throw new ServerServiceException(errorMessage);
				}

				UInteger parentNoOfDirectParentNo = directParentBoardRecord.getValue(SB_BOARD_TB.PARENT_NO);
				byte directParentBoardStateValue = directParentBoardRecord.getValue(SB_BOARD_TB.BOARD_ST);

				BoardStateType directParentBoardStateType = null;
				try {
					directParentBoardStateType = BoardStateType.valueOf(directParentBoardStateValue);
				} catch (IllegalArgumentException e) {
					try {
						conn.rollback();
					} catch (Exception e1) {
						log.warn("fail to rollback", e1);
					}

					String errorMessage = new StringBuilder("직계 조상 게시글[boardID=").append(boardID.shortValue())
							.append(", boardNo=").append(directParentNo.longValue()).append("] 의 상태 값[")
							.append(directParentBoardStateValue).append("] 이 잘못되었습니다").toString();
					throw new ServerServiceException(errorMessage);
				}

				if (!BoardStateType.OK.equals(directParentBoardStateType)) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}

					String errorMessage = new StringBuilder().append("직계 조상 게시글[boardID=").append(boardID.shortValue())
							.append(", boardNo=").append(directParentNo.longValue()).append("]이 정상[")
							.append(directParentBoardStateType.getName()).append("]이 아닌 게시글은 차단 해제 할 수 없습니다")
							.toString();
					throw new ServerServiceException(errorMessage);
				}

				directParentNo = parentNoOfDirectParentNo;
			}

			HashSet<Long> unBlockBoardNoSet = new HashSet<Long>();

			unBlockBoardNoSet.add(boardNo.longValue());

			// int fromGroupSeq = groupSeq.intValue() - 1;

			Result<Record3<UInteger, UByte, Byte>> childBoardResult = create
					.select(SB_BOARD_TB.BOARD_NO, SB_BOARD_TB.DEPTH, SB_BOARD_TB.BOARD_ST).from(SB_BOARD_TB)
					.where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.GROUP_NO.eq(groupNo))
					.and(SB_BOARD_TB.GROUP_SQ.lt(groupSeq)).orderBy(SB_BOARD_TB.GROUP_SQ.desc()).fetch();

			while (childBoardResult.isNotEmpty()) {
				Record3<UInteger, UByte, Byte> childBoardRecord = childBoardResult.remove(0);

				UInteger childBoardNo = childBoardRecord.getValue(SB_BOARD_TB.BOARD_NO);
				UByte childDepth = childBoardRecord.getValue(SB_BOARD_TB.DEPTH);
				byte childBoardState = childBoardRecord.getValue(SB_BOARD_TB.BOARD_ST);
				
				
				BoardStateType childBoardStateType = null;
				try {
					childBoardStateType = BoardStateType.valueOf(childBoardState);
				} catch (IllegalArgumentException e) {
					try {
						conn.rollback();
					} catch (Exception e1) {
						log.warn("fail to rollback", e1);
					}

					String errorMessage = new StringBuilder("차단 해제 트리에 속한 게시글[boardID=").append(boardID.shortValue())
							.append(", boardNo=").append(childBoardNo.longValue()).append("] 의 상태 값[")
							.append(childBoardState).append("] 이 잘못되었습니다").toString();
					throw new ServerServiceException(errorMessage);
				}
				

				/*
				 * log.info("1.boardNo={}, depth={}, boardState={}, target depth={}",
				 * childBoardNo, childDepth, childBoardState, depth);
				 */

				if (childDepth.shortValue() <= depth.shortValue()) {
					break;
				}

				if (BoardStateType.BLOCK.equals(childBoardStateType)) {
					/**
					 * INFO! 차단 해제 대상 글에 대한 차단 사유와 하위 경로상 글의 차단 사유가 다르다고 판단하기때문에 하위 경로상 글은 차단 해제에서
					 * 제외한다.
					 */

					UByte depthOfRelativeRootNode = childDepth;

					while (childBoardResult.isNotEmpty()) {
						childBoardRecord = childBoardResult.get(0);

						childBoardNo = childBoardRecord.getValue(SB_BOARD_TB.BOARD_NO);
						childDepth = childBoardRecord.getValue(SB_BOARD_TB.DEPTH);
						childBoardState = childBoardRecord.getValue(SB_BOARD_TB.BOARD_ST);

						/*
						 * log.info("2.boardNo={}, depth={}, boardState={}, depthOfRelativeRootNode={}",
						 * childBoardNo, childDepth, childBoardState, depthOfRelativeRootNode);
						 */

						if (childDepth.shortValue() <= depthOfRelativeRootNode.shortValue()) {
							break;
						}

						childBoardResult.remove(0);

						if (BoardStateType.OK.equals(childBoardStateType)) {
							log.warn("1.게시판 트리 점검 필요, childBoardNo={}, {}", childBoardNo, boardUnBlockReq.toString());

							try {
								conn.rollback();
							} catch (Exception e) {
								log.warn("fail to rollback");
							}
							String errorMessage = "게시판 트리 점검 필요";
							throw new ServerServiceException(errorMessage);
						}
					}
				} else if (BoardStateType.OK.equals(childBoardStateType)) {
					log.warn("2.게시판 트리 점검 필요, childBoardNo={}, {}", childBoardNo, boardUnBlockReq.toString());

					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}
					String errorMessage = "게시판 트리 점검 필요";
					throw new ServerServiceException(errorMessage);
				} else if (BoardStateType.TREEBLOCK.equals(childBoardStateType)) {
					unBlockBoardNoSet.add(childBoardNo.longValue());
				}
			}

			create.update(SB_BOARD_TB).set(SB_BOARD_TB.BOARD_ST, BoardStateType.OK.getValue())
					.where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.BOARD_NO.in(unBlockBoardNoSet)).execute();

			if (BoardListType.TREE.equals(boardListType)) {
				// 계층형 목록일때 목록 갯수에 정상 상태에서 차단상태로된 모든 갯수 추가
				create.update(SB_BOARD_INFO_TB)
						.set(SB_BOARD_INFO_TB.CNT, SB_BOARD_INFO_TB.CNT.add(unBlockBoardNoSet.size()))
						.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).execute();
			} else {
				// 그룹 루트만으로 이루어진 목록일때 그룹 루트에 대한 차단시에만 목록 갯수 1 추가
				if (0L == parentNo.longValue()) {
					create.update(SB_BOARD_INFO_TB).set(SB_BOARD_INFO_TB.CNT, SB_BOARD_INFO_TB.CNT.add(1))
							.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).execute();
				}
			}

			conn.commit();
			
			ServerDBUtil.insertSiteLog(conn, create, log, requestedUserID, boardUnBlockReq.toString(), 
					new java.sql.Timestamp(System.currentTimeMillis()), boardUnBlockReq.getIp());
			
			conn.commit();
			
			resultMessageStringBuilder.append(boardName).append(" 게시판[").append(boardID)
			.append("] 의 게시글[").append(boardNo.longValue()).append("]을 차단 해제하였습니다");
		});

		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(boardUnBlockReq.getMessageID());
		messageResultRes.setIsSuccess(true);
		messageResultRes.setResultMessage(resultMessageStringBuilder.toString());

		return messageResultRes;
	}

}
