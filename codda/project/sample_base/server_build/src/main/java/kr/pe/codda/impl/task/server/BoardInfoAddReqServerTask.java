package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;

import org.jooq.types.UByte;
import org.jooq.types.UInteger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardInfoAddReq.BoardInfoAddReq;
import kr.pe.codda.impl.message.BoardInfoAddRes.BoardInfoAddRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.lib.BoardListType;
import kr.pe.codda.server.lib.BoardReplyPolicyType;
import kr.pe.codda.server.lib.JooqSqlUtil;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardInfoAddReqServerTask extends AbstractServerTask {

	public BoardInfoAddReqServerTask() throws DynamicClassCallException {
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
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME,
					(BoardInfoAddReq) inputMessage);
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

			sendErrorOutputMessage("게시판 정보 등록이 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}
	
	public BoardInfoAddRes doWork(String dbcpName, BoardInfoAddReq boardInfoAddReq) throws Exception {
		// FIXME!
		log.info(boardInfoAddReq.toString());
		
		final BoardListType boardListType;
		final BoardReplyPolicyType boardReplyPolicyType;
		final PermissionType boardWritePermissionType;
		final PermissionType boardReplyPermissionType;		
		
		try {
			ValueChecker.checkValidRequestedUserID(boardInfoAddReq.getRequestedUserID());
			ValueChecker.checkValidBoardName(boardInfoAddReq.getBoardName());
			
			boardListType = BoardListType.valueOf(boardInfoAddReq.getBoardListType());
			boardReplyPolicyType = BoardReplyPolicyType.valueOf(boardInfoAddReq.getBoardReplyPolicyType());
			boardWritePermissionType = PermissionType.valueOf("본문 쓰기", boardInfoAddReq.getBoardWritePermissionType());
			boardReplyPermissionType = PermissionType.valueOf("댓글 쓰기", boardInfoAddReq.getBoardReplyPermissionType());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		BoardInfoAddRes boardInfoAddRes = new BoardInfoAddRes();
		
		ServerDBUtil.execute(dbcpName, (conn, create) -> {
			
			ServerDBUtil.checkUserAccessRights(conn, create, log, "게시판 정보 추가 서비스", PermissionType.ADMIN, boardInfoAddReq.getRequestedUserID());
			
			short boardID = create.select(JooqSqlUtil.getIfField(SB_BOARD_INFO_TB.BOARD_ID.max(), 0, SB_BOARD_INFO_TB.BOARD_ID.max().add(1)))
			.from(SB_BOARD_INFO_TB)
			.fetchOne(0, Short.class);
			
			if (boardID > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "새롭게 얻은 게시판 식별자 값이 최대값을 초과하여 더 이상 추가할 수 없습니다";
				throw new ServerServiceException(errorMessage);
			}
			
			int countOfInsert = create.insertInto(SB_BOARD_INFO_TB).set(SB_BOARD_INFO_TB.BOARD_ID, UByte.valueOf(boardID))
			.set(SB_BOARD_INFO_TB.BOARD_NAME, boardInfoAddReq.getBoardName())
			.set(SB_BOARD_INFO_TB.LIST_TYPE, boardListType.getValue())
			.set(SB_BOARD_INFO_TB.REPLY_POLICY_TYPE, boardReplyPolicyType.getValue())
			.set(SB_BOARD_INFO_TB.WRITE_PERMISSION_TYPE, boardWritePermissionType.getValue())
			.set(SB_BOARD_INFO_TB.REPLY_PERMISSION_TYPE, boardReplyPermissionType.getValue())
			.set(SB_BOARD_INFO_TB.CNT, 0L)
			.set(SB_BOARD_INFO_TB.TOTAL, 0L)
			.set(SB_BOARD_INFO_TB.NEXT_BOARD_NO, UInteger.valueOf(1)).execute();
			
			if (0 == countOfInsert) {
				String errorMessage = new StringBuilder()
						.append("게시판 정보[게시판식별자:")
						.append(boardID)
						.append(", 게시판이름:").append(boardInfoAddReq.getBoardName()).append("] 삽입 실패").toString();
				throw new Exception(errorMessage);
			}
						
			
			conn.commit();
			
			boardInfoAddRes.setBoardID(boardID);
		});		

		return boardInfoAddRes;
	}

}
