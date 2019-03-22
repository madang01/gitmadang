package kr.pe.codda.servlet.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.BoardInfoAddReq.BoardInfoAddReq;
import kr.pe.codda.impl.message.BoardInfoAddRes.BoardInfoAddRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.BoardListType;
import kr.pe.codda.weblib.common.BoardReplyPolicyType;
import kr.pe.codda.weblib.common.PermissionType;
import kr.pe.codda.weblib.common.ValueChecker;
import kr.pe.codda.weblib.jdf.AbstractAdminLoginServlet;

public class BoardInfoAddSvl extends AbstractAdminLoginServlet {
	
	private static final long serialVersionUID = -2112970254265229650L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		/**************** 파라미터 시작 *******************/
		String paramBoardName = req.getParameter("boardName");
		String paramBoardListType = req.getParameter("boardListType");
		String paramBoardReplyPolicyType = req.getParameter("boardReplyPolicyType");
		String paramBoardWritePermissionType = req.getParameter("boardWritePermissionType");
		String paramBoardReplyPermissionType = req.getParameter("boardReplyPermissionType");
		/**************** 파라미터 종료 *******************/

		try {
			ValueChecker.checkValidBoardName(paramBoardName);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (null == paramBoardListType) {
			String errorMessage = "게시판 목록 유형을 입력해 주세요";
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		BoardListType boardListType = null;
		try {			
			boardListType = BoardListType.valueOf(Byte.parseByte(paramBoardListType));
		} catch(NumberFormatException e) {
			String errorMessage = "게시판 목록 유형값을 다시 넣어 주세요";
			String debugMessage = new StringBuilder()
					.append("파라미터 '게시판 목록 유형값'[")
					.append(paramBoardListType)
					.append("]이 잘못되었습니다").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (null == paramBoardReplyPolicyType) {
			String errorMessage = "게시판 댓글 정책 유형을 입력해 주세요";
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		BoardReplyPolicyType boardReplyPolicyType = null;
		try {			
			boardReplyPolicyType = BoardReplyPolicyType.valueOf(Byte.parseByte(paramBoardReplyPolicyType));
		} catch(NumberFormatException e) {
			String errorMessage = "게시판 댓글 정책 유형값을 다시 넣어 주세요";
			String debugMessage = new StringBuilder()
					.append("파라미터 '게시판 댓글 정책 유형값'[")
					.append(paramBoardReplyPolicyType)
					.append("]이 잘못되었습니다").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		if (null == paramBoardWritePermissionType) {
			String errorMessage = "본문 쓰기 권한 유형을 입력해 주세요";
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		PermissionType boardWritePermissionType = null;
		try {			
			boardWritePermissionType = PermissionType.valueOf(Byte.parseByte(paramBoardWritePermissionType));
		} catch(NumberFormatException e) {
			String errorMessage = "본문 쓰기 권한 유형값을 다시 넣어 주세요";
			String debugMessage = new StringBuilder()
					.append("파라미터 '본문 쓰기 권한 유형값'[")
					.append(paramBoardWritePermissionType)
					.append("]이 잘못되었습니다").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		if (null == paramBoardReplyPermissionType) {
			String errorMessage = "댓글 쓰기 권한 유형을 입력해 주세요";
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		PermissionType boardReplyPermissionType = null;
		try {			
			boardReplyPermissionType = PermissionType.valueOf(Byte.parseByte(paramBoardReplyPermissionType));
		} catch(NumberFormatException e) {
			String errorMessage = "댓글 쓰기 권한 유형값을 다시 넣어 주세요";
			String debugMessage = new StringBuilder()
					.append("파라미터 '댓글 쓰기 권한 유형값'[")
					.append(paramBoardReplyPermissionType)
					.append("]이 잘못되었습니다").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}		
		
		BoardInfoAddReq boardInfoAddReq = new BoardInfoAddReq();
		boardInfoAddReq.setRequestedUserID(getLoginedAdminIDFromHttpSession(req));
		boardInfoAddReq.setBoardName(paramBoardName);
		boardInfoAddReq.setBoardListType(boardListType.getValue());
		boardInfoAddReq.setBoardReplyPolicyType(boardReplyPolicyType.getValue());
		boardInfoAddReq.setBoardWritePermissionType(boardWritePermissionType.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(boardReplyPermissionType.getValue());
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), boardInfoAddReq);
		
		if (! (outputMessage instanceof BoardInfoAddRes)) {
			if ((outputMessage instanceof MessageResultRes)) {			
				MessageResultRes  messageResultRes = (MessageResultRes)outputMessage;
				String errorMessage = new StringBuilder().append("게시판 정보[")
						.append(boardInfoAddReq.toString())
						.append("] 추가하는데 실패했습니다, 실패사유:")
						.append(messageResultRes.getResultMessage()).toString();
				
				String debugMessage = null;

				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			} else {
				String errorMessage = new StringBuilder().append("게시판 정보[")
						.append(boardInfoAddReq.toString())
						.append("]를 추가하는데 실패했습니다").toString();
				
				String debugMessage = new StringBuilder("입력 메시지[")
						.append(boardInfoAddReq.getMessageID())
						.append("]에 대한 비 정상 출력 메시지[")
						.append(outputMessage.toString())
						.append("] 도착").toString();
				
				log.warn(debugMessage);

				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
		}
		
		BoardInfoAddRes boardInfoAddRes = (BoardInfoAddRes)outputMessage;
		req.setAttribute("boardID", String.valueOf(boardInfoAddRes.getBoardID()));		
		printJspPage(req, res, "/jsp/board/BoardInfoAdd.jsp");
		return;
	}
}
