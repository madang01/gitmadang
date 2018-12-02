package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;

import java.sql.Connection;

import javax.sql.DataSource;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardBlockReq.BoardBlockReq;
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
import org.jooq.Record4;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;

public class BoardBlockReqServerTask extends AbstractServerTask {
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
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (BoardBlockReq)inputMessage);
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

	private boolean isChildNode(UShort fromGroupSeq, UShort toGroupSeq){
		return (fromGroupSeq.intValue() != toGroupSeq.intValue());
	}
	public MessageResultRes doWork(String dbcpName, BoardBlockReq boardBlockReq)
			throws Exception {
		try {
			BoardType.valueOf(boardBlockReq.getBoardID());
		} catch (IllegalArgumentException e) {			
			String errorMessage = "잘못된 게시판 식별자입니다";
			throw new ServerServiceException(errorMessage);
		}
		
		if (boardBlockReq.getBoardNo() < 0 || boardBlockReq.getBoardNo() > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			String errorMessage = "게시판 번호가 unsigned integer type 의 최대값(=4294967295) 보다 큽니다";
			throw new ServerServiceException(errorMessage);
		}
		
		if (null == boardBlockReq.getRequestUserID()) {
			String errorMessage = "요청자 아이디가 null 입니다";
			throw new ServerServiceException(errorMessage);
		}
		
		String requestUserID = boardBlockReq.getRequestUserID();
		UByte boardID = UByte.valueOf(boardBlockReq.getBoardID());
		UInteger boardNo = UInteger.valueOf(boardBlockReq.getBoardNo());
		

		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(dbcpName);

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
			
			Record4<UInteger, UShort, UInteger, String> 
			boardRecord = create.select(SB_BOARD_TB.GROUP_NO, 
					SB_BOARD_TB.GROUP_SQ, 
					SB_BOARD_TB.PARENT_NO,
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
			// UByte depth = boardRecord.getValue(SB_BOARD_TB.DEPTH);
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
			
			if (BoardStateType.DELETE.equals(boardStateType)) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "해당 게시글은 삭제된 글입니다";
				throw new ServerServiceException(errorMessage);
			} else if (BoardStateType.BLOCK.equals(boardStateType)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "해당 게시글은 관리자에 의해 차단된 글입니다";
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
			
			/*
			HashSet<Long> childBoardNoSet = new HashSet<Long>();
						
			Result<Record3<UInteger, UByte, String>> 
			childBoardResult = create.select(SB_BOARD_TB.BOARD_NO, 
					SB_BOARD_TB.DEPTH, SB_BOARD_TB.BOARD_ST)
			.from(SB_BOARD_TB)
			.where(SB_BOARD_TB.BOARD_ID.eq(boardID))				
			.and(SB_BOARD_TB.GROUP_NO.eq(groupNo))
			.and(SB_BOARD_TB.GROUP_SQ.lt(groupSeq))
			.orderBy(SB_BOARD_TB.GROUP_SQ.desc())
			.fetch();
			
			for (Record3<UInteger, UByte, String> childBoardRecord : childBoardResult) {
				UInteger childBoardNo  = childBoardRecord.getValue(SB_BOARD_TB.BOARD_NO);
				UByte childDepth = childBoardRecord.getValue(SB_BOARD_TB.DEPTH);
				String childBoardState  = childBoardRecord.getValue(SB_BOARD_TB.BOARD_ST);
				
				if (childDepth.shortValue() <= depth.shortValue()) {
					break;
				}
				
				if (BoardStateType.OK.getValue().equals(childBoardState)) {
					childBoardNoSet.add(childBoardNo.longValue());
				}
			}		*/
			UShort fromGroupSeq = groupSeq;
			UShort toGroupSeq = ServerDBUtil.getToGroupSeqOfRelativeRootBoard(create, boardID, groupSeq, parentNo);
			
			int updateCount = create.update(SB_BOARD_TB)
			.set(SB_BOARD_TB.BOARD_ST, BoardStateType.BLOCK.getValue())
			.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
			.and(SB_BOARD_TB.BOARD_NO.eq(boardNo))
			.execute();
			
			if (isChildNode(fromGroupSeq, toGroupSeq)) {
				updateCount += create.update(SB_BOARD_TB)
						.set(SB_BOARD_TB.BOARD_ST, BoardStateType.TREEBLOCK.getValue())
						.where(SB_BOARD_TB.BOARD_ID.eq(boardID))					
						.and(SB_BOARD_TB.GROUP_NO.eq(groupNo))					
						.and(SB_BOARD_TB.GROUP_SQ.lt(fromGroupSeq))
						.and(SB_BOARD_TB.GROUP_SQ.ge(toGroupSeq))
						.and(SB_BOARD_TB.BOARD_ST.eq(BoardStateType.OK.getValue()))
						.execute();	
			}					
						
			create.update(SB_BOARD_INFO_TB)
			.set(SB_BOARD_INFO_TB.USER_TOTAL, SB_BOARD_INFO_TB.USER_TOTAL.sub(updateCount))
			.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID))
			.execute();
			
			conn.commit();			

			MessageResultRes messageResultRes = new MessageResultRes();
			messageResultRes.setTaskMessageID(boardBlockReq.getMessageID());
			messageResultRes.setIsSuccess(true);
			messageResultRes.setResultMessage(new StringBuilder()
					.append(BoardType.valueOf(boardID.shortValue()).getName())
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
