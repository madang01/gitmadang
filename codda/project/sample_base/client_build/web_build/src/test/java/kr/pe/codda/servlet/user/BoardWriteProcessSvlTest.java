package kr.pe.codda.servlet.user;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.sessionkey.ClientSessionKeyIF;
import kr.pe.codda.common.sessionkey.ClientSessionKeyManager;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
import kr.pe.codda.weblib.MockServletInputStream;
import kr.pe.codda.weblib.common.BoardType;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

public class BoardWriteProcessSvlTest extends AbstractJunitTest {
	
	@Test
	public void testDoWork_ok() {
		ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager
				.getInstance();
		
		ClientSessionKeyManager clientSessionKeyManager = ClientSessionKeyManager.getInstance();
		ServerSessionkeyIF mainProjectServerSessionkey = null;
		try {
			mainProjectServerSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();
		} catch (SymmetricException e2) {
			fail("fail to get a intanace of ServerSessionkeyIF class");
		}
		
		ClientSessionKeyIF clientSessionKey = null;
		try {
			clientSessionKey = clientSessionKeyManager.getNewClientSessionKey(mainProjectServerSessionkey.getDupPublicKeyBytes(), true);
		} catch (SymmetricException e1) {
			fail("fail to get a intanace of ClientSessionKeyIF class");
		}
		
		final String sessionKeyBase64 = org.apache.commons.codec.binary.Base64.encodeBase64String(clientSessionKey.getDupSessionKeyBytes());
		final String ivBase64 = org.apache.commons.codec.binary.Base64.encodeBase64String(clientSessionKey.getDupIVBytes());
		final String subject = "테스트주제1_그림";
		final String content = "테스트내용1_한글사랑";

		HashMap<String, String> parameterHash = new HashMap<String, String>();
		parameterHash.put(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY, sessionKeyBase64);
		parameterHash.put(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV, ivBase64);
		parameterHash.put("boardID", String.valueOf(BoardType.FREE.getBoardID()));
		parameterHash.put("subject", subject);
		parameterHash.put("content", content);
		
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		for (String key : parameterHash.keySet()) {
			builder.addTextBody(key, parameterHash.get(key), 
					ContentType.create("text/plain", "UTF-8"));
		}		

		String selectedUploadFilePathString = new StringBuilder()
		.append(WebRootBuildSystemPathSupporter.getUserWebRootPathString(installedPath.getAbsolutePath(), mainProjectName))
		.append(File.separator)
		.append("images")
		.append(File.separator).append("sinnori_server_framework01.png").toString();
		
		File selectedUploadFile = new File(selectedUploadFilePathString);
		
		if (! selectedUploadFile.exists()) {
			log.warn("업로드할 대상 파일[{}]이 존재하지 않습니다", selectedUploadFilePathString);
			fail("업로드할 대상 파일이 존재하지 않습니다");
		}
		
		if (! selectedUploadFile.isFile()) {
			log.warn("업로드할 대상 파일[{}]이 일반 파일이 아닙니다", selectedUploadFilePathString);
			fail("업로드할 대상 파일이 존재하지 않습니다");
		}
		
		byte[] contentsOfUploadFile = null;
		try {
			contentsOfUploadFile = FileUtils.readFileToByteArray(selectedUploadFile);
		} catch (IOException e) {
			fail("첨부 파일로 지정한 파일 읽기 실패");
		}
		
		InputStream attachedFileInputStream = new ByteArrayInputStream(contentsOfUploadFile);
		String mimeTypeOfUploadFile = null;
		try {
			mimeTypeOfUploadFile = URLConnection
					.guessContentTypeFromStream(attachedFileInputStream);
		} catch (IOException e) {
			log.warn("입출력 에러가 발생하여 파일 유형 추출 실패, errmsg={}", e.getMessage());
			fail("입출력 에러가 발생하여 파일 유형 추출 실패");
		} finally {
			try {
				attachedFileInputStream.close();
			} catch (IOException e) {
			}
		}
		
		if (null == mimeTypeOfUploadFile) {
			fail("파일 유형을 알 수 없습니다");
		}
		
		builder.addBinaryBody("newAttachedFile", contentsOfUploadFile, ContentType.create(mimeTypeOfUploadFile), selectedUploadFile.getName());
		
		HttpEntity entity = builder.build();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
			entity.writeTo(os);
		} catch (IOException e) {
			log.warn("입출력 에러로 HttpEntity 의 내용을 출력 스트림에 쓰기  실패, errmsg={}", e.getMessage());
			fail("입출력 에러로 HttpEntity 의 내용을 출력 스트림에 쓰기  실패");
		}
        
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        
        HttpSession session = mock(HttpSession.class);
		when(session.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_USER_ID)).thenReturn("test00");
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn("POST");
		when(request.getContentType()).thenReturn(entity.getContentType().getValue());
        when(request.getContentLength()).thenReturn((int)entity.getContentLength());
        try {
			when(request.getInputStream()).thenReturn(new MockServletInputStream(is));
		} catch (IOException e) {
			fail("dead code");
		}
		when(request.getSession()).thenReturn(session);
		when(request.getRequestURI()).thenReturn("/servlet/BoardWriteProcess");
		when(request.getRemoteHost()).thenReturn("");
		when(request.getRemoteAddr()).thenReturn("172.0.1.32");
		when(request.getRemoteUser()).thenReturn("");
		
		HttpServletResponse response = mock(HttpServletResponse.class);

		BoardWriteProcessSvl boardWriteProcessSvl = new BoardWriteProcessSvl();
		
		RequestDispatcher requestDispatcherMock = mock(RequestDispatcher.class);		
		
		ServletContext servletContextMock = mock(ServletContext.class);
		when(servletContextMock.getRequestDispatcher(ArgumentMatchers.any(String.class)))
		.thenReturn(requestDispatcherMock);		
		
		ServletConfig servletConfigMock = mock(ServletConfig.class);
		when(servletConfigMock.getInitParameter(WebCommonStaticFinalVars.SERVLET_INIT_PARM_KEY_NAME_OF_MENU_GROUP_URL))
		.thenReturn("/servlet/BoardList");		
		when(servletConfigMock.getServletContext()).thenReturn(servletContextMock);
				
		BoardWriteRes boardWriteRes = null;
		
		try {
			//boardWriteProcessSvl.service(request, response);
			
			boardWriteProcessSvl.init(servletConfigMock);
			boardWriteRes = boardWriteProcessSvl.doWork(request, response);
			
			log.info(boardWriteRes.toString());
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("게시판 쓰기 서블릿 수행 실패");
		}
	}
}
