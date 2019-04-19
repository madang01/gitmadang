package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;

import org.jooq.Record1;
import org.jooq.types.UByte;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardInfoModifyReq.BoardInfoModifyReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.lib.BoardReplyPolicyType;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

/**
 * WARNING! 게시판 수정은 비용이 많이 드는 작업이므로 운영중에 사용하지 말것  
 * 
 * @author Won Jonghoon
 *
 */
public class BoardInfoModifyReqServerTask extends AbstractServerTask {

	public BoardInfoModifyReqServerTask() throws DynamicClassCallException {
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
					(BoardInfoModifyReq) inputMessage);
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

			sendErrorOutputMessage("게시판 정보 수정이 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}
	
	public MessageResultRes doWork(String dbcpName, BoardInfoModifyReq boardInfoModifyReq) throws Exception {
		// FIXME!
		log.info(boardInfoModifyReq.toString());
		
		final BoardReplyPolicyType boardReplyPolicyType;
		final PermissionType boardWritePermissionType;
		final PermissionType boardReplyPermissionType;
		
		try {
			ValueChecker.checkValidWriterID(boardInfoModifyReq.getRequestedUserID());
			ValueChecker.checkValidBoardName(boardInfoModifyReq.getBoardName());
			
			boardReplyPolicyType = BoardReplyPolicyType.valueOf(boardInfoModifyReq.getBoardReplyPolicyType());
			
			boardWritePermissionType = PermissionType.valueOf("본문 쓰기", boardInfoModifyReq.getBoardWritePermissionType());
			boardReplyPermissionType = PermissionType.valueOf("댓글 쓰기", boardInfoModifyReq.getBoardReplyPermissionType());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}		
		
		UByte boardID = UByte.valueOf(boardInfoModifyReq.getBoardID());
		String boardName = boardInfoModifyReq.getBoardName();
		
		ServerDBUtil.execute(dbcpName, (conn, create) -> {
			
			ServerDBUtil.checkUserAccessRights(conn, create, log, "게시판 정보 수정 서비스", PermissionType.ADMIN, boardInfoModifyReq.getRequestedUserID());
			
			Record1<UByte> boardInfoRecord = create.select(SB_BOARD_INFO_TB.BOARD_ID).from(SB_BOARD_INFO_TB)
			.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).fetchOne();

			if (null == boardInfoRecord) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "지정한 게시판 식별자에 대한 게시판 정보가 존재하지 않습니다";
				throw new ServerServiceException(errorMessage);
			}			
			
			
			int countOfUpdate = create.update(SB_BOARD_INFO_TB)
						.set(SB_BOARD_INFO_TB.BOARD_NAME, boardName)
						.set(SB_BOARD_INFO_TB.REPLY_POLICY_TYPE, boardReplyPolicyType.getValue())
						.set(SB_BOARD_INFO_TB.WRITE_PERMISSION_TYPE, boardWritePermissionType.getValue())
						.set(SB_BOARD_INFO_TB.REPLY_PERMISSION_TYPE, boardReplyPermissionType.getValue())
						.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).execute();									
			
			if (0 == countOfUpdate) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append("게시판 정보[")
						.append(boardInfoModifyReq.getBoardID())
						.append("]를 수정하는데 실패하였습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
						
			conn.commit();
		});
		

		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(boardInfoModifyReq.getMessageID());
		messageResultRes.setIsSuccess(true);
		messageResultRes.setResultMessage(new StringBuilder().append("게시판 정보[")
				.append(boardInfoModifyReq.getBoardID())
				.append("]를 수정하였습니다").toString());
		

		return messageResultRes;
	}

}
