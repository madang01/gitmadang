package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;

import java.sql.Connection;
import java.util.HashSet;

import javax.sql.DataSource;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardUnBlockReq.BoardUnBlockReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.BoardType;
import kr.pe.codda.server.lib.MemberType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record5;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;

public class BoardUnBlockReqServerTask extends AbstractServerTask {
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
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (BoardUnBlockReq)inputMessage);
			toLetterCarrier.addSyncOutputMessage(outputMessage);
		} catch(ServerServiceException e) {
			String errorMessage = e.getMessage();
			log.warn("errmsg=={}, inObj={}", errorMessage, inputMessage.toString());
			
			sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
			return;
		} catch(Exception e) {
			String errorMessage = new StringBuilder().append("unknwon errmsg=")
					.append(e.getMessage())
					.append(", inObj=")
					.append(inputMessage.toString()).toString();
			
			log.warn(errorMessage, e);
						
			sendErrorOutputMessage("게시글 가져오는데 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}
	
	public MessageResultRes doWork(String dbcpName, BoardUnBlockReq boardUnBlockReq)
			throws Exception {

		BoardType boardType = null;
		try {
			boardType = BoardType.valueOf(boardUnBlockReq.getBoardID());
		} catch (IllegalArgumentException e) {			
			String errorMessage = "잘못된 게시판 식별자입니다";
			throw new ServerServiceException(errorMessage);
		}
		
		if (boardUnBlockReq.getBoardNo() < 0 || boardUnBlockReq.getBoardNo() > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			String errorMessage = "게시판 번호가 unsigned integer type 의 최대값(=4294967295) 보다 큽니다";
			throw new ServerServiceException(errorMessage);
		}
		
		if (null == boardUnBlockReq.getRequestUserID()) {
			String errorMessage = "요청자 아이디가 null 입니다";
			throw new ServerServiceException(errorMessage);
		}
		
		String requestUserID = boardUnBlockReq.getRequestUserID();
		UByte boardID = UByte.valueOf(boardUnBlockReq.getBoardID());
		UInteger boardNo = UInteger.valueOf(boardUnBlockReq.getBoardNo());
		

		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));
			
			String nativeRequestUserIDMemberType = ValueChecker.checkValidMemberStateForUserID(conn, create, log, requestUserID);	
			MemberType  requestUserIDMemberType = null;
			try {
				requestUserIDMemberType = MemberType.valueOf(nativeRequestUserIDMemberType, false);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("해당 게시글 삭제 요청자의 멤버 타입[")
						.append(nativeRequestUserIDMemberType)
						.append("]이 잘못되어있습니다").toString();
				throw new ServerServiceException(errorMessage);
			}	
			
			if (! MemberType.ADMIN.equals(requestUserIDMemberType)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "게시글 차단은 관리자 전용 서비스입니다";
				throw new ServerServiceException(errorMessage);
			}
			
			Record5<UInteger, UShort, UInteger, UByte, String> 
			boardRecord = create.select(SB_BOARD_TB.GROUP_NO, 
					SB_BOARD_TB.GROUP_SQ, 
					SB_BOARD_TB.PARENT_NO,
					SB_BOARD_TB.DEPTH,
					SB_BOARD_TB.BOARD_ST)
					.from(SB_BOARD_TB)					
					.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
					.and(SB_BOARD_TB.BOARD_NO.eq(boardNo))
					.fetchOne();

			if (null == boardRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "해당 게시글이 존재 하지 않습니다";
				throw new ServerServiceException(errorMessage);
			}
			
			UInteger groupNo = boardRecord.getValue(SB_BOARD_TB.GROUP_NO);
			UShort groupSeq = boardRecord.getValue(SB_BOARD_TB.GROUP_SQ);
			UInteger parentNo = boardRecord.getValue(SB_BOARD_TB.PARENT_NO);
			UByte depth = boardRecord.getValue(SB_BOARD_TB.DEPTH);
			String boardState = boardRecord.getValue(SB_BOARD_TB.BOARD_ST);
			
			
			BoardStateType boardStateType = null;
			try {
				boardStateType = BoardStateType.valueOf(boardState, false);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("게시글의 상태 값[")
						.append(boardState)
						.append("]이 잘못되었습니다").toString();
				throw new ServerServiceException(errorMessage);
			}	
			
			if (! BoardStateType.BLOCK.equals(boardStateType)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "차단된 글이 아닙니다";
				throw new ServerServiceException(errorMessage);
			}
			
			Record1<UInteger> rootBoardRecord = create.select(SB_BOARD_TB.BOARD_NO)
					.from(SB_BOARD_TB)
					.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
					.and(SB_BOARD_TB.BOARD_NO.eq(groupNo))
					.forUpdate().fetchOne();
					
			if (null == rootBoardRecord) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder().append("그룹 최상위 글[boardID=")
						.append(boardID.longValue())
						.append(", boardNo=").append(groupNo.longValue())
						.append("] 이 존재하지 않습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			UInteger directParentNo = parentNo;
			while (true) {
				Record2<UInteger, String> 
				directParentBoardRecord = create.select(
						SB_BOARD_TB.PARENT_NO,
						SB_BOARD_TB.BOARD_ST)
						.from(SB_BOARD_TB)					
						.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
						.and(SB_BOARD_TB.BOARD_NO.eq(directParentNo))
						.fetchOne();
				
				if (null == directParentBoardRecord) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}
					
					String errorMessage = new StringBuilder()
					.append("직계 부모 게시글[boardID=")
					.append(boardID.shortValue())
					.append(", boardNo=")
					.append(directParentNo.longValue())
					.append("]이 존재 하지 않습니다").toString();
					throw new ServerServiceException(errorMessage);
				}
				
				directParentNo = directParentBoardRecord.getValue(SB_BOARD_TB.PARENT_NO);			
				String directParentBoardState = directParentBoardRecord.getValue(SB_BOARD_TB.BOARD_ST);
				
				BoardStateType directParentBoardStateType = null;
				try {
					directParentBoardStateType = BoardStateType.valueOf(directParentBoardState, false);
				} catch(IllegalArgumentException e) {
					try {
						conn.rollback();
					} catch (Exception e1) {
						log.warn("fail to rollback");
					}
					
					String errorMessage = new StringBuilder("직계 부모 게시글[boardID=")
						.append(boardID.shortValue())
						.append(", boardNo=")
						.append(directParentNo.longValue())
						.append("] 의 상태 값[")
						.append(directParentBoardState)
						.append("] 이 잘못되었습니다").toString();
					throw new ServerServiceException(errorMessage);
				}
				
				if (BoardStateType.BLOCK.equals(directParentBoardStateType)) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}
					
					String errorMessage = new StringBuilder()
					.append("직계 부모 게시글[boardID=")
					.append(boardID.shortValue())
					.append(", boardNo=")
					.append(directParentNo.longValue())
					.append("]이 차단된 게시글은 차단 해제 할 수 없습니다").toString();
					throw new ServerServiceException(errorMessage);
				}
				
				if (directParentNo.equals(groupNo)) {
					break;
				}
			}			
			
			HashSet<Long> boardNoSet = new HashSet<Long>();
			
			boardNoSet.add(boardNo.longValue());
			
			// int fromGroupSeq = groupSeq.intValue() - 1;			
			
			Result<Record3<UInteger, UByte, String>> 
			childBoardResult = create.select(SB_BOARD_TB.BOARD_NO, 
					SB_BOARD_TB.DEPTH, SB_BOARD_TB.BOARD_ST)
			.from(SB_BOARD_TB)
			.where(SB_BOARD_TB.BOARD_ID.eq(boardID))				
			.and(SB_BOARD_TB.GROUP_NO.eq(groupNo))
			.and(SB_BOARD_TB.GROUP_SQ.lt(groupSeq))
			.orderBy(SB_BOARD_TB.GROUP_SQ.desc())
			.fetch();
			
			while (childBoardResult.isNotEmpty()) {
				Record3<UInteger, UByte, String> childBoardRecord = childBoardResult.remove(0);
				
				UInteger childBoardNo  = childBoardRecord.getValue(SB_BOARD_TB.BOARD_NO);
				UByte childDepth = childBoardRecord.getValue(SB_BOARD_TB.DEPTH);
				String childBoardState  = childBoardRecord.getValue(SB_BOARD_TB.BOARD_ST);
				
				if (childDepth.shortValue() <= depth.shortValue()) {
					break;
				}
				
				outer:
				// if (childGroupSeq.intValue() <= fromGroupSeq) {
					if (BoardStateType.BLOCK.getValue().equals(childBoardState)) {
						/**
						 * INFO! 차단 해제 대상 글에 대한 차단 사유와 하위 경로상 글의 차단 사유가 다르다고 판단하기때문에
						 * 하위 경로상 글은 차단 해제에서 제외한다.
						 */
						
						UByte depthOfRelativeRootNode = childDepth;
						
						while (childBoardResult.isNotEmpty()) {
							childBoardRecord = childBoardResult.remove(0);
							
							childBoardNo  = childBoardRecord.getValue(SB_BOARD_TB.BOARD_NO);
							childDepth = childBoardRecord.getValue(SB_BOARD_TB.DEPTH);
							childBoardState  = childBoardRecord.getValue(SB_BOARD_TB.BOARD_ST);
							
							if (childDepth.shortValue() <= depthOfRelativeRootNode.shortValue()) {
								break outer;
							}
						}
						
						/*fromGroupSeq = ServerDBUtil
								.getToGroupSeqOfRelativeRootBoard(create, boardID, groupNo, 
										childGroupSeq, childDepth).intValue() - 1;*/
					} else if (BoardStateType.TREEBLOCK.getValue().equals(childBoardState)) {
						log.error("게시판 트리 점검 필요, {}", boardUnBlockReq.toString());
						
						try {
							conn.rollback();
						} catch (Exception e) {
							log.warn("fail to rollback");
						}
						String errorMessage = "게시판 트리 점검 필요";
							throw new ServerServiceException(errorMessage);
					}  else {
						boardNoSet.add(childBoardNo.longValue());
					}				
					
				// }
			}
			
			create.update(SB_BOARD_TB)
			.set(SB_BOARD_TB.BOARD_ST, BoardStateType.OK.getValue())
			.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
			.and(SB_BOARD_TB.BOARD_NO.in(boardNoSet))
			.execute();
						
			create.update(SB_BOARD_INFO_TB)
			.set(SB_BOARD_INFO_TB.USER_TOTAL, SB_BOARD_INFO_TB.USER_TOTAL.add(boardNoSet.size()))
			.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID))
			.execute();
			
			conn.commit();			

			MessageResultRes messageResultRes = new MessageResultRes();
			messageResultRes.setTaskMessageID(boardUnBlockReq.getMessageID());
			messageResultRes.setIsSuccess(true);
			messageResultRes.setResultMessage(new StringBuilder()
					.append(boardType.getName())
					.append(" 게시판의 글[")
					.append(boardNo.longValue())
					.append("] 차단이 완료되었습니다").toString());
			
			return messageResultRes;
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
