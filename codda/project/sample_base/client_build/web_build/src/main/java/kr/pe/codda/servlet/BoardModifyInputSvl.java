/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kr.pe.codda.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardDetailReq.BoardDetailReq;
import kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.BoardType;
import kr.pe.codda.weblib.jdf.AbstractMultipartServlet;

/**
 * 게시판 글 수정 처리
 * 
 * @author Won Jonghoon
 *
 */

public class BoardModifyInputSvl extends AbstractMultipartServlet {

	private static final long serialVersionUID = -8497600784868532827L;

	private void printBoardErrorCallBackPage(HttpServletRequest req, HttpServletResponse res, String errorMessage) {
		final String goPage = "/jsp/community/BoardErrorCallBack.jsp";
		req.setAttribute("errorMessage", errorMessage);
		printJspPage(req, res, goPage);
	}

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {		

		String paramBoardId = req.getParameter("boardId");
		if (null == paramBoardId) {
			String errorMessage = "게시판 식별자 값을 넣어 주세요.";
			String debugMessage = "the web parameter 'boardId' is null";
			log.warn(debugMessage);
			
			printBoardErrorCallBackPage(req, res, errorMessage);
			return;
		}

		short boardId = 0;
		try {
			boardId = Short.parseShort(paramBoardId);
		} catch (NumberFormatException nfe) {
			String errorMessage = "잘못된 게시판 식별자 입니다";
			String debugMessage = new StringBuilder("the web parameter 'boardId'[").append(paramBoardId)
					.append("] is not a short").toString();
			log.warn(debugMessage);
			
			printBoardErrorCallBackPage(req, res, errorMessage);
			return;
		}

		try {
			BoardType.valueOf(boardId);
		} catch (IllegalArgumentException e) {
			String errorMessage = "알 수 없는 게시판 식별자 입니다";
			String debugMessage = new StringBuilder("the web parameter 'boardId'[").append(paramBoardId)
					.append("] is not a element of set[").append(BoardType.getSetString()).append("]").toString();
			log.warn(debugMessage);
			printBoardErrorCallBackPage(req, res, errorMessage);
			return;
		}

		String paramBoardNo = req.getParameter("boardNo");
		if (null == paramBoardNo) {
			String errorMessage = "게시판 번호를 넣어주세요";
			String debugMessage = "the web parameter 'boardNo' is null";
			log.warn(debugMessage);
			
			printBoardErrorCallBackPage(req, res, errorMessage);
			return;
		}

		long boardNo = 0L;
		try {
			boardNo = Long.parseLong(paramBoardNo);
		} catch (NumberFormatException nfe) {
			String errorMessage = new StringBuilder("자바 long 타입 변수인 게시판 번호 값[").append(paramBoardId)
					.append("]이 잘못되었습니다.").toString();
			log.warn("{}, userId={}, ip={}", errorMessage, getLoginedUserIDFromHttpSession(req), req.getRemoteAddr());
			
			
			printBoardErrorCallBackPage(req, res, errorMessage);
			return;
		}

		if (boardNo <= 0) {
			String errorMessage = new StringBuilder("게시판 번호 값[").append(paramBoardId).append("]은 0 보다 커야합니다.")
					.toString();
			log.warn("{}, userId={}, ip={}", errorMessage, getLoginedUserIDFromHttpSession(req), req.getRemoteAddr());

			printBoardErrorCallBackPage(req, res, errorMessage);
			return;
		}

		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setBoardID(boardId);
		boardDetailReq.setBoardNo(boardNo);

		// FIXME!
		log.debug("inObj={}, userId={}, ip={}", boardDetailReq.toString(), getLoginedUserIDFromHttpSession(req),
				req.getRemoteAddr());

		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance()
				.getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(boardDetailReq);

		// FIXME!
		log.debug("inObj={}, messageFromServer={}, userId={}, ip={}", boardDetailReq.toString(), outputMessage.toString(),
				getLoginedUserIDFromHttpSession(req), req.getRemoteAddr());
		
		if (outputMessage instanceof MessageResultRes) {
			MessageResultRes messageResultRes = (MessageResultRes) outputMessage;

			if (messageResultRes.getIsSuccess()) {
				log.error("입력 메시지[{}]에 대한 에러 결과를 담은 출력 메시지[{}]가 아닙니다", boardDetailReq.getMessageID(),
						messageResultRes.toString());
				System.exit(1);
			}

			String errorMessage = "게시판 글 수정이 실패하였습니다";
			String debugMessage = messageResultRes.getResultMessage();
			log.warn(debugMessage);
			printBoardErrorCallBackPage(req, res, errorMessage);
			return;
		} else if (! (outputMessage instanceof BoardDetailRes)){
			log.error("입력 메시지[{}]에 대한 비 정상 출력 메시지[{}] 도착", boardDetailReq.getMessageID(), outputMessage.toString());
			System.exit(1);
		}
		

		
		BoardDetailRes boardDetailRes = (BoardDetailRes)outputMessage;
		req.setAttribute("boardDetailRes", boardDetailRes);
		printJspPage(req, res, "/menu/board/BoardDetail01.jsp");
		return; 
	}

}
