package kr.pe.sinnori.servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.client.ClientProject;
import kr.pe.sinnori.client.ClientProjectManager;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.weblib.AbstractServlet;
import kr.pe.sinnori.impl.message.BoardDownloadFileInDTO.BoardDownloadFileInDTO;
import kr.pe.sinnori.impl.message.BoardDownloadFileOutDTO.BoardDownloadFileOutDTO;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;

@SuppressWarnings("serial")
public class BoardDownloadSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		String goPage = "/board/BoardDownload01.jsp";
		
		if (! isLogin(req)) {
			String errorMessage = new StringBuilder("파일 업로드는 로그인 서비스 입니다. 로그인 하시기 바랍니다.").toString();		
			log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
			
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);			
			return;
		}
		
		String parmAttachId = req.getParameter("attachId");
		if (null == parmAttachId) {
			String errorMessage = "업로드 식별자를 넣어주세요.";
			log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
			
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		}
		
		long attachId = 0L;
		try {
			attachId = Long.parseLong(parmAttachId);
		}catch (NumberFormatException nfe) {
			String errorMessage = new StringBuilder("자바 long 타입 변수인 업로드 식별자 값[")
			.append(parmAttachId).append("]이 잘못되었습니다.").toString();
			log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
			
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		}
		
		if (attachId <= 0) {
			String errorMessage = new StringBuilder("업로드 식별자 값[")
			.append(parmAttachId).append("]은 0 보다 커야합니다.").toString();
			log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
			
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		}
		
		String parmAttachSeq = req.getParameter("attachSeq");
		if (null == parmAttachSeq) {
			String errorMessage = "업로드 파일 순번를 넣어주세요.";
			log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
			
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		}		
		
		short attachSeq = 0;
		try {
			attachSeq = Short.parseShort(parmAttachSeq);
		}catch (NumberFormatException nfe) {
			String errorMessage = new StringBuilder("자바 short 타입 변수인 업로드 파일 순번 값[")
			.append(parmAttachId).append("]이 잘못되었습니다.").toString();
			log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
			
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		}
		
		if (attachSeq <= 0) {
			String errorMessage = new StringBuilder("업로드 파일 순번 값[")
			.append(parmAttachId).append("]은 0 보다 커야합니다.").toString();
			log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
			
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		}
		
		if (attachSeq > CommonStaticFinalVars.MAX_UNSIGNED_BYTE) {
			String errorMessage = new StringBuilder("업로드 파일 순번 값[")
			.append(parmAttachId).append("]은 ")
			.append(CommonStaticFinalVars.MAX_UNSIGNED_BYTE)
			.append(" 값 보다 작거나 같아야 합니다.").toString();
			log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
			
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		}		
		
		BoardDownloadFileInDTO bardDownloadFileInDTO = new BoardDownloadFileInDTO();
		bardDownloadFileInDTO.setAttachId(attachId);
		bardDownloadFileInDTO.setAttachSeq(attachSeq);
		
		// FIXME!
		log.debug("inObj={},  userId={}, ip={}", bardDownloadFileInDTO.toString(), getUserId(req), req.getRemoteAddr());
		
		String projectName = System.getProperty(CommonStaticFinalVars.SINNORI_PROJECT_NAME_JAVA_SYSTEM_VAR_NAME);
		ClientProject clientProject = ClientProjectManager.getInstance().getClientProject(projectName);
		
		AbstractMessage messageFromServer = clientProject.sendSyncInputMessage(bardDownloadFileInDTO);
		
		if (! (messageFromServer instanceof BoardDownloadFileOutDTO)) {
			String errorMessage = null;
			
			if (messageFromServer instanceof MessageResult) {				
				errorMessage = ((MessageResult)messageFromServer).getResultMessage();
				
				log.warn("입력 메시지[{}]의 응답 메시지로 MessageResult 메시지 도착, 응답 메시지=[{}], userId={}, ip={}", 
						bardDownloadFileInDTO.toString(), messageFromServer.toString(), getUserId(req), req.getRemoteAddr());
			} else {
				errorMessage = "게시판 목록 메시지를 얻는데 실패하였습니다.";
				
				if (messageFromServer instanceof SelfExn) {
					log.warn("입력 메시지[{}]의 응답 메시지로 SelfExn 메시지 도착, 응답 메시지=[{}]", bardDownloadFileInDTO.toString(), messageFromServer.toString());
				} else {
					log.warn("입력 메시지[{}]의 응답 메시지로 알 수 없는 메시지 도착, 응답 메시지=[{}]", bardDownloadFileInDTO.toString(), messageFromServer.toString());
				}
			}
			req.setAttribute("errorMessage", errorMessage);	
			printJspPage(req, res, goPage);
			return;
		}
				
		BoardDownloadFileOutDTO boardDownloadFileOutDTO = (BoardDownloadFileOutDTO) messageFromServer;
		
		String ownerId = boardDownloadFileOutDTO.getOwnerId();
		
		if (! ownerId.equals(getUserId(req))) {
			String errorMessage = new StringBuilder("업로드 파일 소유자[")
			.append(ownerId).append("] 와 로그인 아이디[")
			.append(getUserId(req))
			.append("] 가 다릅니다.").toString();
			log.warn("{}, ip={}", errorMessage, req.getRemoteAddr());
			
			req.setAttribute("errorMessage", errorMessage);			
			printJspPage(req, res, goPage);
			return;
		}
				
		/**
		 * 참고 사이트 : http://goodcodes.tistory.com/14
		 * JSP - File Download
		 * 2014/02/14 12:00
		 * 
		 * Posted in Tomcat & JSP by 흔들리는내마음
		 */

		File downloadFile = new File(boardDownloadFileOutDTO.getSystemFileName());
		// SecureCoding 파일명을 받는 경우 ../ 에 대한 체크가 필요하다.(지정디렉토리 이외의 디렉토리 금지 루틴)

		// 브라우저 별 처리
		if (downloadFile.exists()) {
			// 1. content-type의 세팅
			res.setContentType("application/octet-stream;charset=UTF-8");
			String filename = boardDownloadFileOutDTO.getAttachFileName();

			System.out.println("");
			// 브라우저별 한글 인코딩
			if (getBrowser(req).equals("MSIE")) {
				// URLEncode하고 +문자만 공백으로 바꾸는 경우
				filename = URLEncoder.encode(filename, "UTF-8")
						.replaceAll("\\+", "%20");
			} else if (getBrowser(req).equals("Chrome")) {
				// char단위로 검색하여 ~표시보다 char값이 높을 때(ascii코드값이 아닌경우)만 URLEncode한다.
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < filename.length(); i++) {
					char c = filename.charAt(i);
					if (c > '~') {
						sb.append(URLEncoder.encode("" + c, "UTF-8"));
					} else {
						sb.append(c);
					}
				}
				filename = sb.toString();
			} else {
				// latin1(8859_1)
				filename = new String(filename.getBytes("UTF-8"), "8859_1");
			}

			// 2. content-disposition의 세팅
			res.addHeader("Content-Disposition", "attachment;filename=\""
					+ filename + "\"");
			byte[] bytes = new byte[1024];
			FileInputStream fis = null;
			BufferedInputStream bis = null;
			fis = new FileInputStream(downloadFile);
			bis = new BufferedInputStream(fis);
			OutputStream os = res.getOutputStream();
			int i = bis.read(bytes);
			while (i != -1) {
				os.write(bytes, 0, i);
				i = bis.read(bytes);
			}
			bis.close();
			fis.close();
		}

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
