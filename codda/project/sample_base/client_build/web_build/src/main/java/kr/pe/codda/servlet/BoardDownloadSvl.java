package kr.pe.codda.servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardDownloadFileReq.BoardDownloadFileReq;
import kr.pe.codda.impl.message.BoardDownloadFileRes.BoardDownloadFileRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.BoardType;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractLoginServlet;

@SuppressWarnings("serial")
public class BoardDownloadSvl extends AbstractLoginServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {		
		
		/**************** 파라미터 시작 *******************/
		String paramBoardID = req.getParameter("boardID");
		String paramBoardNo = req.getParameter("boardNo");
		String paramAttachedFileSeq = req.getParameter("attachedFileSeq");
		/**************** 파라미터 종료 *******************/
		
		if (null == paramBoardID) {
			String errorMessage = "게시판 식별자 값을 넣어 주세요.";
			String debugMessage = "the web parameter 'boardID' is null";			
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		short boardID = 0;
		try {
			boardID = Short.parseShort(paramBoardID);
		} catch (NumberFormatException nfe) {
			String errorMessage = "잘못된 게시판 식별자 입니다";
			String debugMessage = new StringBuilder("the web parameter 'boardID'[").append(paramBoardID)
					.append("] is not a short").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		try {
			BoardType.valueOf(boardID);
		} catch (IllegalArgumentException e) {
			String errorMessage = "알 수 없는 게시판 식별자 입니다";
			String debugMessage = new StringBuilder("the web parameter 'boardID'[").append(paramBoardID)
					.append("] is not a element of set[").append(BoardType.getSetString()).append("]").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}		

		if (null == paramBoardNo) {
			String errorMessage = "게시판 번호 값을 넣어 주세요";
			String debugMessage = "the web parameter 'boardNo' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		long boardNo = 0L;

		try {
			boardNo = Long.parseLong(paramBoardNo);
		} catch (NumberFormatException nfe) {
			String errorMessage = "잘못된 게시판 번호입니다";
			String debugMessage = new StringBuilder("the web parameter \"boardNo\"'s value[").append(paramBoardNo)
					.append("] is not a Long").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		if (boardNo <= 0) {
			String errorMessage = "게시판 번호는 0 보다 커야 합니다";
			String debugMessage = new StringBuilder("the web parameter \"boardNo\"'s value[").append(paramBoardNo)
					.append("] is less than or equal to zero").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
				
		
		if (null == paramAttachedFileSeq) {
			String errorMessage = "첨부 파일 순번를 넣어주세요.";
			log.warn("{}, userId={}, ip={}", errorMessage, getLoginedUserIDFromHttpSession(req), req.getRemoteAddr());
			
			printBoardProcessFailureCallBackPage(req, res, errorMessage);
			return;
		}		
		
		short attachedFileSeq = 0;
		try {
			attachedFileSeq = Short.parseShort(paramAttachedFileSeq);
		}catch (NumberFormatException nfe) {
			String errorMessage = new StringBuilder("자바 short 타입 변수인 첨부 파일 순번 값[")
			.append(paramAttachedFileSeq).append("]이 잘못되었습니다.").toString();
			log.warn("{}, userId={}, ip={}", errorMessage, getLoginedUserIDFromHttpSession(req), req.getRemoteAddr());
			
			printBoardProcessFailureCallBackPage(req, res, errorMessage);
			return;
		}
		
		if (attachedFileSeq < 0) {
			String errorMessage = new StringBuilder("첨부 파일 순번 값[")
			.append(paramAttachedFileSeq).append("]은 0 보다 작거나 커야합니다.").toString();
			log.warn("{}, userId={}, ip={}", errorMessage, getLoginedUserIDFromHttpSession(req), req.getRemoteAddr());
			
			printBoardProcessFailureCallBackPage(req, res, errorMessage);
			return;
		}
		
		if (attachedFileSeq > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = new StringBuilder("업로드 파일 순번 값[")
			.append(paramAttachedFileSeq).append("]은 ")
			.append(CommonStaticFinalVars.UNSIGNED_BYTE_MAX)
			.append(" 값 보다 작거나 같아야 합니다.").toString();
			log.warn("{}, userId={}, ip={}", errorMessage, getLoginedUserIDFromHttpSession(req), req.getRemoteAddr());
			
			printBoardProcessFailureCallBackPage(req, res, errorMessage);
			return;
		}		
		
		
		BoardDownloadFileReq boardDownloadFileReq = new BoardDownloadFileReq();
		boardDownloadFileReq.setBoardID(boardID);
		boardDownloadFileReq.setBoardNo(boardNo);
		boardDownloadFileReq.setAttachedFileSeq(attachedFileSeq);
		
		// FIXME!
		log.debug("inObj={},  userId={}, ip={}", boardDownloadFileReq.toString(), getLoginedUserIDFromHttpSession(req), req.getRemoteAddr());
		
	
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		
		
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(boardDownloadFileReq);
		
		if ((outputMessage instanceof MessageResultRes)) {
			MessageResultRes messageResultRes = (MessageResultRes)outputMessage;
			String errorMessage = messageResultRes.getResultMessage();
			String debugMessage = "";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		} else if (! (outputMessage instanceof BoardDownloadFileRes)) {
			String errorMessage = "다운 로드 파일 정보를 얻는데 실패하였습니다";
				
			String debugMessage = new StringBuilder("입력 메시지[")
						.append(boardDownloadFileReq.getMessageID())
						.append("]에 대한 비 정상 출력 메시지[")
						.append(outputMessage.toString())
						.append("] 도착").toString();
				
				log.error(debugMessage);
		
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
				
		BoardDownloadFileRes boardDownloadFileRes = (BoardDownloadFileRes) outputMessage;
				
		/**
		 * 참고 사이트 : http://goodcodes.tistory.com/14
		 * JSP - File Download
		 * 2014/02/14 12:00
		 * 
		 * Posted in Tomcat & JSP by 흔들리는내마음
		 */

		CoddaConfiguration runningProjectConfiguration = CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();
		String mainProjectName = runningProjectConfiguration.getMainProjectName();
		String sinnoriInstalledPathString = runningProjectConfiguration.getInstalledPathString();
		String attachedFileFullName = new StringBuilder(WebRootBuildSystemPathSupporter
				.getWebUploadPathString(sinnoriInstalledPathString, mainProjectName)).append(File.separator)
						.append(WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_PREFIX).append("_BoardID")
						.append(boardID).append("_BoardNo")
						.append(boardNo).append("Seq").append(attachedFileSeq)
						.append(WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_SUFFIX).toString();
		File downloadFile = new File(attachedFileFullName);
		// SecureCoding 파일명을 받는 경우 ../ 에 대한 체크가 필요하다.(지정디렉토리 이외의 디렉토리 금지 루틴)

		if (! downloadFile.exists()) {
			String errorMessage = new StringBuilder().append("다운 로드 할 대상 파일[")
					.append(attachedFileFullName)
					.append("]이 존재 하지 않습니다").toString();
			String debugMessage = "";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		// 브라우저 별 처리
		if (downloadFile.exists()) {
			// 1. content-type의 세팅
			res.setContentType("application/octet-stream;charset=UTF-8");
			String attachedFileName = boardDownloadFileRes.getAttachedFileName();

			// 브라우저별 한글 인코딩
			if (getBrowser(req).equals("MSIE")) {
				// URLEncode하고 +문자만 공백으로 바꾸는 경우
				attachedFileName = URLEncoder.encode(attachedFileName, "UTF-8")
						.replaceAll("\\+", "%20");
			} else if (getBrowser(req).equals("Chrome")) {
				// char단위로 검색하여 ~표시보다 char값이 높을 때(ascii코드값이 아닌경우)만 URLEncode한다.
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < attachedFileName.length(); i++) {
					char c = attachedFileName.charAt(i);
					if (c > '~') {
						sb.append(URLEncoder.encode("" + c, "UTF-8"));
					} else {
						sb.append(c);
					}
				}
				attachedFileName = sb.toString();
			} else {
				// latin1(8859_1)
				attachedFileName = new String(attachedFileName.getBytes("UTF-8"), "8859_1");
			}

			// 2. content-disposition의 세팅
			res.addHeader("Content-Disposition", "attachment;filename=\""
					+ attachedFileName + "\"");
			byte[] bytes = new byte[1024];
			FileInputStream fis = null;
			BufferedInputStream bis = null;
			try {
				fis = new FileInputStream(downloadFile);
				bis = new BufferedInputStream(fis);
				OutputStream os = res.getOutputStream();
				int i = bis.read(bytes);
				while (i != -1) {
					os.write(bytes, 0, i);
					i = bis.read(bytes);
				}
			} catch(IOException e) {
				log.warn("다운로드 파일[{}] 처리중 입출력 에러 발생", boardDownloadFileReq.toString());
			} finally {
				try {
					bis.close();
				} catch(Exception e) {					
				}
				try {
					fis.close();
				} catch(Exception e) {					
				}
			}	
		}
	}

	private void printBoardProcessFailureCallBackPage(HttpServletRequest req, HttpServletResponse res, String errorMessage) {
		req.setAttribute("errorMessage", errorMessage);
		printJspPage(req, res, "/jsp/community/BoardProcessFailureCallBack.jsp");
	}

	// HTTP/1.1 헤더로부터 브라우저를 가져온다.
	private String getBrowser(HttpServletRequest request) {
		String header = request.getHeader("User-Agent");
		if (header.indexOf("MSIE") > -1) {
			return "MSIE";
			// IE8 ~ IE11
		} else if (header.indexOf("Trident") > -1) {
			return "MSIE";
		} else if (header.indexOf("OPR") > -1) {
			return "OPR";
		} else if (header.indexOf("Chrome") > -1) {
			return "Chrome";
		} else if (header.indexOf("Opera") > -1) {
			return "Opera";
		} else if (header.indexOf("Firefox") > -1) {
			return "Firefox";
		} else if (header.indexOf("Safari") > -1) {
			return "Safari";
		} else {
			return "UNKOWN";
		}
	}
}
