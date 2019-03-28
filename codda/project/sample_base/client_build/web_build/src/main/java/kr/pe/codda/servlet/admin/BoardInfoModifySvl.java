package kr.pe.codda.servlet.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.BoardInfoModifyReq.BoardInfoModifyReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.BoardReplyPolicyType;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.common.PermissionType;
import kr.pe.codda.weblib.jdf.AbstractAdminLoginServlet;

public class BoardInfoModifySvl extends AbstractAdminLoginServlet {
	
	private static final long serialVersionUID = 8360878301816265271L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		/**************** 파라미터 시작 *******************/
		String paramBoardID = req.getParameter("boardID");
		String paramBoardName = req.getParameter("boardName");
		String paramBoardReplyPolicyType = req.getParameter("boardReplyPolicyType");
		String paramBoardWritePermissionType = req.getParameter("boardWritePermissionType");
		String paramBoardReplyPermissionType = req.getParameter("boardReplyPermissionType");
		/**************** 파라미터 종료 *******************/
		
		short boardID = -1;
		try {			
			boardID = Short.parseShort(paramBoardID);
		} catch(NumberFormatException e) {
			String errorMessage = "게시판 식별자를 다시 넣어 주세요";
			String debugMessage = new StringBuilder()
					.append("파라미터 '게시판 식별자'[")
					.append(paramBoardID)
					.append("]가 잘못되었습니다").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (boardID < 0) {
			String errorMessage = "게시판 식별자 값이 음수입니다";
			String debugMessage = new StringBuilder()
					.append("파라미터 '게시판 식별자'[")
					.append(paramBoardID)
					.append("]값이 음수입니다").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (boardID > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = "게시판 식별자 값이 unsigned byte 최대값 보다 큽니다";
			String debugMessage = new StringBuilder()
					.append("파라미터 '게시판 식별자'[")
					.append(paramBoardID)
					.append("]값이 unsigned byte 최대값[")
					.append(CommonStaticFinalVars.UNSIGNED_BYTE_MAX)
					.append("보다 큽니다").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (null == paramBoardName) {
			String errorMessage = "게시판 이름을 입력해 주세요";
			String debugMessage = null;
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
		
		AccessedUserInformation accessedUserformation = getAccessedUserInformation(req);
		
		BoardInfoModifyReq boardInfoModifyReq = new BoardInfoModifyReq();
		boardInfoModifyReq.setRequestedUserID(accessedUserformation.getUserID());
		boardInfoModifyReq.setBoardID(boardID);
		boardInfoModifyReq.setBoardName(paramBoardName);
		boardInfoModifyReq.setBoardReplyPolicyType(boardReplyPolicyType.getValue());
		boardInfoModifyReq.setBoardWritePermissionType(boardWritePermissionType.getValue());
		boardInfoModifyReq.setBoardReplyPermissionType(boardReplyPermissionType.getValue());
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), boardInfoModifyReq);
		
		if (! (outputMessage instanceof MessageResultRes)) {	
			String errorMessage = new StringBuilder().append("게시판 정보[")
					.append(boardID)
					.append("]를 수정하는데 실패했습니다").toString();
			
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(boardInfoModifyReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString())
					.append("] 도착").toString();
			
			log.warn(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		MessageResultRes  messageResultRes = (MessageResultRes)outputMessage;
		if (! messageResultRes.getIsSuccess()) {
			String errorMessage = new StringBuilder().append("게시판 정보[")
					.append(boardID)
					.append("]를 수정하는데 실패했습니다, 실패사유:")
					.append(messageResultRes.getResultMessage()).toString();
			
			String debugMessage = null;

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		req.setAttribute("boardID", paramBoardID);
		printJspPage(req, res, "/jsp/board/BoardInfoModify.jsp");
		return;
	}
}
