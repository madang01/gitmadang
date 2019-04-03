package kr.pe.codda.servlet.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.sessionkey.ClientSessionKeyIF;
import kr.pe.codda.common.sessionkey.ClientSessionKeyManager;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.message.BoardModifyRes.BoardModifyRes;
import kr.pe.codda.impl.message.BoardReplyRes.BoardReplyRes;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.MockServletInputStream;
import kr.pe.codda.weblib.common.MemberRoleType;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.exception.WebClientException;
import nl.captcha.Captcha;

public class BoardIntegrationSvlTest extends AbstractJunitTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();
		
		class MembershipRequestMockBuilder {
			public HttpServletRequest build(String userID, String pwd, String nickname, String pwdHint,
					String pwdAnswer) {
				Captcha captcha = new Captcha.Builder(200, 50).build();	
				String captchaAnswer = captcha.getAnswer();
				
				String sessionKeyBase64 = null;
				String ivBase64 = null;
				String paramUserIDBase64 = null;
				String paramPwdBase64 = null;
				String paramNicknameBase64 = null;
				String paramPwdHintBase64 = null;
				String paramPwdAnswerBase64 = null;
				String paramCaptchaAnswerBase64 = null;

				ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();

				ClientSessionKeyManager clientSessionKeyManager = ClientSessionKeyManager.getInstance();
				ServerSessionkeyIF mainProjectServerSessionkey = null;
				try {
					mainProjectServerSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();
				} catch (SymmetricException e2) {
					fail("fail to get a intanace of ServerSessionkeyIF class");
				}

				ClientSessionKeyIF clientSessionKey = null;
				try {
					clientSessionKey = clientSessionKeyManager
							.getNewClientSessionKey(mainProjectServerSessionkey.getDupPublicKeyBytes(), true);
				} catch (SymmetricException e1) {
					fail("fail to get a intanace of ClientSessionKeyIF class");
				}

				try {
					sessionKeyBase64 = CommonStaticUtil.Base64Encoder.encodeToString(clientSessionKey.getDupSessionKeyBytes());
					ivBase64 = CommonStaticUtil.Base64Encoder.encodeToString(clientSessionKey.getDupIVBytes());

					if (null != userID) {
						byte[] bytesOfUserID = userID.getBytes(CommonStaticFinalVars.CIPHER_CHARSET);
						byte[] encryptedBytesOfUserID = clientSessionKey.getClientSymmetricKey().encrypt(bytesOfUserID);
						paramUserIDBase64 = CommonStaticUtil.Base64Encoder.encodeToString(encryptedBytesOfUserID);
					}

					if (null != pwd) {
						byte[] bytesOfPwd = pwd.getBytes(CommonStaticFinalVars.CIPHER_CHARSET);
						byte[] encryptedBytesOfPwd = clientSessionKey.getClientSymmetricKey().encrypt(bytesOfPwd);
						paramPwdBase64 = CommonStaticUtil.Base64Encoder.encodeToString(encryptedBytesOfPwd);
					}

					if (null != nickname) {
						byte[] bytesOfNickname = nickname.getBytes(CommonStaticFinalVars.CIPHER_CHARSET);
						byte[] encryptedBytesOfNickname = clientSessionKey.getClientSymmetricKey().encrypt(bytesOfNickname);
						paramNicknameBase64 = CommonStaticUtil.Base64Encoder.encodeToString(encryptedBytesOfNickname);
					}

					if (null != pwdHint) {
						byte[] bytesOfPwdHint = pwdHint.getBytes(CommonStaticFinalVars.CIPHER_CHARSET);
						byte[] encryptedBytesOfPwdHint = clientSessionKey.getClientSymmetricKey().encrypt(bytesOfPwdHint);
						paramPwdHintBase64 = CommonStaticUtil.Base64Encoder.encodeToString(encryptedBytesOfPwdHint);
					}

					if (null != pwdAnswer) {
						byte[] bytesOfPwdAnswer = pwdAnswer.getBytes(CommonStaticFinalVars.CIPHER_CHARSET);
						byte[] encryptedBytesOfPwdAnswer = clientSessionKey.getClientSymmetricKey().encrypt(bytesOfPwdAnswer);
						paramPwdAnswerBase64 = CommonStaticUtil.Base64Encoder.encodeToString(encryptedBytesOfPwdAnswer);
					}

					if (null != captchaAnswer) {
						byte[] bytesOfCaptchaAnswer = captchaAnswer.getBytes(CommonStaticFinalVars.CIPHER_CHARSET);
						byte[] encryptedBytesOfCaptchaAnswer = clientSessionKey.getClientSymmetricKey().encrypt(bytesOfCaptchaAnswer);
						paramCaptchaAnswerBase64 = CommonStaticUtil.Base64Encoder.encodeToString(encryptedBytesOfCaptchaAnswer);
					}

				} catch (Exception e) {
					log.warn("파라미터 구성 실패", e);
					fail("파라미터 구성 실패");
				}
				
				HttpSession sessionMock = mock(HttpSession.class);
				when(sessionMock.getAttribute(Captcha.NAME))
						.thenReturn(captcha);

				HttpServletRequest requestMock = mock(HttpServletRequest.class);
				when(requestMock.getMethod()).thenReturn("POST");
				when(requestMock.getSession()).thenReturn(sessionMock);
				when(requestMock.getRequestURI()).thenReturn("/servlet/MembershipProcess");
				when(requestMock.getRemoteHost()).thenReturn("");
				when(requestMock.getRemoteAddr()).thenReturn("172.0.1.32");
				when(requestMock.getRemoteUser()).thenReturn("");

				/************* 파라미터 구성 시작 *************/
				when(requestMock.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY))
						.thenReturn(sessionKeyBase64);
				when(requestMock.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV))
						.thenReturn(ivBase64);
				when(requestMock.getParameter("userID")).thenReturn(paramUserIDBase64);
				when(requestMock.getParameter("pwd")).thenReturn(paramPwdBase64);
				when(requestMock.getParameter("nickname")).thenReturn(paramNicknameBase64);
				when(requestMock.getParameter("pwdHint")).thenReturn(paramPwdHintBase64);
				when(requestMock.getParameter("pwdAnswer")).thenReturn(paramPwdAnswerBase64);
				when(requestMock.getParameter("captchaAnswer")).thenReturn(paramCaptchaAnswerBase64);
				/************* 파라미터 구성 종료 *************/

				return requestMock;
			}
		}

		HttpServletRequest requestMock = new MembershipRequestMockBuilder().build("test00", "test1234$", "별명00", "질문", "답변");
		HttpServletResponse responseMock = mock(HttpServletResponse.class);

		MemberRegisterProcessSvl membershipProcessSvl = new MemberRegisterProcessSvl();

		try {
			initServlet(membershipProcessSvl, "/servlet/UserSiteMembershipInput");
		} catch (Exception e) {
			log.warn("서블릿 초기화 실패", e);
			fail("서블릿 초기화 실패");
		}

		MessageResultRes messageResultRes = null;
		try {
			messageResultRes = membershipProcessSvl.doWork(requestMock, responseMock);
			
			if (! messageResultRes.getIsSuccess()) {
				assertEquals("중복 아이디 검사", "기존 회원과 중복되는 아이디[test00] 입니다", messageResultRes.getResultMessage());
			}
			
		} catch (WebClientException e) {
			log.warn("errmsg={}, debugmsg={}", e.getErrorMessage(), e.getDebugMessage());
			fail("일반 회원 가입 서블릿 수행 실패");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("일반 회원 가입 서블릿 수행 실패");
		}
	}
	
	private static void initServlet(HttpServlet targetServlet, String menuGroupURL) throws Exception {
		RequestDispatcher requestDispatcherMock = mock(RequestDispatcher.class);

		ServletContext servletContextMock = mock(ServletContext.class);
		when(servletContextMock.getRequestDispatcher(ArgumentMatchers.any(String.class)))
				.thenReturn(requestDispatcherMock);

		ServletConfig servletConfigMock = mock(ServletConfig.class);
		when(servletConfigMock.getInitParameter(WebCommonStaticFinalVars.SERVLET_INIT_PARM_KEY_NAME_OF_MENU_GROUP_URL))
				.thenReturn(menuGroupURL);
		when(servletConfigMock.getServletContext()).thenReturn(servletContextMock);

		targetServlet.init(servletConfigMock);
	}
	
	private BoardWriteRes executeBoardWriteProcessServlet(String loginID, 
			String paramBoardID, String paramSubject, String paramContents, List<File> newAttachedFileList) {
		
		class BoardWriteProcessRequestMockBuilder {
			public HttpServletRequest build(String loginID, 
					String paramBoardID, String paramSubject, String paramContents, List<File> newAttachedFileList) {
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
				
				String sessionKeyBase64 = null;
				String ivBase64 = null;	
				try {
					sessionKeyBase64 = CommonStaticUtil.Base64Encoder.encodeToString(clientSessionKey.getDupSessionKeyBytes());
					ivBase64 = CommonStaticUtil.Base64Encoder.encodeToString(clientSessionKey.getDupIVBytes());
				} catch (Exception e) {
					log.warn("파라미터 구성 실패", e);
					fail("파라미터 구성 실패");
				}
				
				MultipartEntityBuilder writeBuilder = MultipartEntityBuilder.create();

				writeBuilder.addTextBody(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY, 
						sessionKeyBase64, 
						ContentType.create("text/plain", "UTF-8"));
				
				writeBuilder.addTextBody(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV, 
						ivBase64, 
						ContentType.create("text/plain", "UTF-8"));
				
				writeBuilder.addTextBody("boardID", paramBoardID, 
						ContentType.create("text/plain", "UTF-8"));				
				
				writeBuilder.addTextBody("subject", paramSubject, 
						ContentType.create("text/plain", "UTF-8"));
				
				writeBuilder.addTextBody("contents", paramContents, 
						ContentType.create("text/plain", "UTF-8"));	

				for (File newAttachedFile : newAttachedFileList) {
					byte[] contentsOfUploadFile = null;
					try {
						contentsOfUploadFile = CommonStaticUtil.readFileToByteArray(newAttachedFile, WebCommonStaticFinalVars.ATTACHED_FILE_MAX_SIZE);
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
					
					writeBuilder.addBinaryBody("newAttachedFile", contentsOfUploadFile, ContentType.create(mimeTypeOfUploadFile), newAttachedFile.getName());
				}		
				
				HttpEntity writeEntity = writeBuilder.build();

		        ByteArrayOutputStream writeByteArrayOputStream = new ByteArrayOutputStream();
		        try {
					writeEntity.writeTo(writeByteArrayOputStream);
				} catch (IOException e) {
					log.warn("입출력 에러로 HttpEntity 의 내용을 출력 스트림에 쓰기  실패, errmsg={}", e.getMessage());
					fail("입출력 에러로 HttpEntity 의 내용을 출력 스트림에 쓰기  실패");
				}
		        
		        ByteArrayInputStream writeByteArrayInputStream = new ByteArrayInputStream(writeByteArrayOputStream.toByteArray());
		        
		        HttpSession sessionMock = mock(HttpSession.class);
				when(sessionMock.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_USER_INFORMATION))
				.thenReturn(new AccessedUserInformation(true, loginID, "테스터", MemberRoleType.MEMBER));
				
				HttpServletRequest requestMock = mock(HttpServletRequest.class);
				when(requestMock.getMethod()).thenReturn("POST");
				when(requestMock.getContentType()).thenReturn(writeEntity.getContentType().getValue());
		        when(requestMock.getContentLength()).thenReturn((int)writeEntity.getContentLength());
		        try {
					when(requestMock.getInputStream()).thenReturn(new MockServletInputStream(writeByteArrayInputStream));
				} catch (IOException e) {
					fail("dead code");
				}
				when(requestMock.getSession()).thenReturn(sessionMock);
				when(requestMock.getRequestURI()).thenReturn("/servlet/BoardWriteProcess");
				when(requestMock.getRemoteHost()).thenReturn("");
				when(requestMock.getRemoteAddr()).thenReturn("172.0.1.32");
				when(requestMock.getRemoteUser()).thenReturn("");
						
				// Create a factory for disk-based file items
				DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();

				// Set factory constraints
				diskFileItemFactory.setSizeThreshold(WebCommonStaticFinalVars.APACHE_FILEUPLOAD_MAX_MEMORY_SIZE);
				
				CoddaConfigurationManager configurationManager = CoddaConfigurationManager.getInstance();
				CoddaConfiguration runningProjectConfiguration = configurationManager.getRunningProjectConfiguration();
				String mainProjectName = runningProjectConfiguration.getMainProjectName();
				String installedPathString = runningProjectConfiguration.getInstalledPathString();
				String userWebTempPathString = WebRootBuildSystemPathSupporter.getUserWebTempPathString(installedPathString,
						mainProjectName);
				
				diskFileItemFactory.setRepository(new File(userWebTempPathString));

				// Create a new file upload handler
				ServletFileUpload upload = new ServletFileUpload(diskFileItemFactory);
				// upload.setHeaderEncoding("UTF-8");
				// log.info("upload.getHeaderEncoding={}", upload.getHeaderEncoding());
				// log.info("req.getCharacterEncoding={}", req.getCharacterEncoding());

				// Set overall request size constraint
				upload.setSizeMax(WebCommonStaticFinalVars.TOTAL_ATTACHED_FILE_MAX_SIZE);
				upload.setFileSizeMax(WebCommonStaticFinalVars.ATTACHED_FILE_MAX_SIZE);

				// Parse the request
				List<FileItem> fileItemList = null;
				try {
					/**
					 * WARNING! 파싱은 request 가 가진  입력 스트림을 소진합니다, 하여 파싱후 그 결과를 전달해 주어야 합니다.
					 * 이때 쓰레드 세이프 문제 때문에 변수 fileItemList 는 멤버 변수가 아닌 request 객체를 통해 전달합니다.
					 */
					fileItemList = upload.parseRequest(requestMock);					
					when(requestMock.getAttribute("fileItemList")).thenReturn(fileItemList);
				} catch (FileUploadException e) {
					log.error("dead code", e);
					System.exit(1);

				}

				return requestMock;
			}
		}
		
		HttpServletRequest requestMock = new BoardWriteProcessRequestMockBuilder().build(loginID,
				paramBoardID,
				paramSubject, paramContents, newAttachedFileList);
		HttpServletResponse responseMock = mock(HttpServletResponse.class);
				
				
		BoardWriteProcessSvl boardWriteProcessSvl = new BoardWriteProcessSvl();
		
		try {
			initServlet(boardWriteProcessSvl, "/servlet/BoardList");
		} catch (Exception e) {
			log.warn("서블릿 초기화 실패", e);
			fail("서블릿 초기화 실패");
		}
		
		BoardWriteRes boardWriteRes = null;
		
		try {
			// boardWriteProcessSvl.service(requestMock, responseMock);			
			boardWriteRes = boardWriteProcessSvl.doWork(requestMock, responseMock);
			
			// log.info(boardWriteRes.toString());
		} catch (WebClientException e) {
			log.warn("errmsg={}, debugmsg={}", e.getErrorMessage(), e.getDebugMessage());
			fail("게시판 쓰기 서블릿 수행 실패");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("게시판 쓰기 서블릿 수행 실패");
		}
		
		return boardWriteRes;
	}
	
	
	private BoardReplyRes execuateBoardReplyProcessServlet(String loginID, 
			String paramBoardID, String paramParentBoardNo, String paramSubject, 
			String paramContents, List<File> attachedFileList) {
		
		class BoardReplyProcessRequestMockBuilder {
			public HttpServletRequest build(String loginID, 
					String paramBoardID, String paramParentBoardNo, String paramSubject, 
					String paramContents, List<File> attachedFileList) {
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
				
				final String sessionKeyBase64 = CommonStaticUtil.Base64Encoder.encodeToString(clientSessionKey.getDupSessionKeyBytes());
				final String ivBase64 = CommonStaticUtil.Base64Encoder.encodeToString(clientSessionKey.getDupIVBytes());
				
				MultipartEntityBuilder replyBuilder = MultipartEntityBuilder.create();

				replyBuilder.addTextBody(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY, 
						sessionKeyBase64, 
						ContentType.create("text/plain", "UTF-8"));
				
				replyBuilder.addTextBody(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV, 
						ivBase64, 
						ContentType.create("text/plain", "UTF-8"));
				
				replyBuilder.addTextBody("boardID", paramBoardID, 
						ContentType.create("text/plain", "UTF-8"));
				
				replyBuilder.addTextBody("parentBoardNo", paramParentBoardNo, 
						ContentType.create("text/plain", "UTF-8"));
				
				replyBuilder.addTextBody("subject", paramSubject, 
						ContentType.create("text/plain", "UTF-8"));
				
				replyBuilder.addTextBody("contents", paramContents, 
						ContentType.create("text/plain", "UTF-8"));
				
				
				for (File attachedFile : attachedFileList) {
					byte[] contentsOfUploadFile = null;
					try {
						contentsOfUploadFile = CommonStaticUtil.readFileToByteArray(attachedFile, WebCommonStaticFinalVars.ATTACHED_FILE_MAX_SIZE);
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
					
					replyBuilder.addBinaryBody("newAttachedFile", contentsOfUploadFile, ContentType.create(mimeTypeOfUploadFile), attachedFile.getName());
				}
				
				
				HttpEntity replyEntity = replyBuilder.build();
				
				ByteArrayOutputStream replyByteArrayOputStream = new ByteArrayOutputStream();
		        try {
		        	replyEntity.writeTo(replyByteArrayOputStream);
				} catch (IOException e) {
					log.warn("입출력 에러로 HttpEntity 의 내용을 출력 스트림에 쓰기  실패, errmsg={}", e.getMessage());
					fail("입출력 에러로 HttpEntity 의 내용을 출력 스트림에 쓰기  실패");
				}
		        
		        ByteArrayInputStream replyByteArrayInputStream = new ByteArrayInputStream(replyByteArrayOputStream.toByteArray());
		        
		        HttpSession sessionMock = mock(HttpSession.class);
				when(sessionMock.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_USER_INFORMATION))
				.thenReturn(new AccessedUserInformation(true, loginID, "테스터", MemberRoleType.MEMBER));
				
		        HttpServletRequest requestMock = mock(HttpServletRequest.class);
				when(requestMock.getMethod()).thenReturn("POST");
				when(requestMock.getContentType()).thenReturn(replyEntity.getContentType().getValue());
		        when(requestMock.getContentLength()).thenReturn((int)replyEntity.getContentLength());
		        try {
					when(requestMock.getInputStream()).thenReturn(new MockServletInputStream(replyByteArrayInputStream));
				} catch (IOException e) {
					fail("dead code");
				}
				when(requestMock.getSession()).thenReturn(sessionMock);
				when(requestMock.getRequestURI()).thenReturn("/servlet/BoardReplyProcess");
				when(requestMock.getRemoteHost()).thenReturn("");
				when(requestMock.getRemoteAddr()).thenReturn("172.0.1.32");
				when(requestMock.getRemoteUser()).thenReturn("");
				
				// Create a factory for disk-based file items
				DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();

				// Set factory constraints
				diskFileItemFactory.setSizeThreshold(WebCommonStaticFinalVars.APACHE_FILEUPLOAD_MAX_MEMORY_SIZE);
				
				CoddaConfigurationManager configurationManager = CoddaConfigurationManager.getInstance();
				CoddaConfiguration runningProjectConfiguration = configurationManager.getRunningProjectConfiguration();
				String mainProjectName = runningProjectConfiguration.getMainProjectName();
				String installedPathString = runningProjectConfiguration.getInstalledPathString();
				String userWebTempPathString = WebRootBuildSystemPathSupporter.getUserWebTempPathString(installedPathString,
						mainProjectName);
				
				diskFileItemFactory.setRepository(new File(userWebTempPathString));

				// Create a new file upload handler
				ServletFileUpload upload = new ServletFileUpload(diskFileItemFactory);
				// upload.setHeaderEncoding("UTF-8");
				// log.info("upload.getHeaderEncoding={}", upload.getHeaderEncoding());
				// log.info("req.getCharacterEncoding={}", req.getCharacterEncoding());

				// Set overall request size constraint
				upload.setSizeMax(WebCommonStaticFinalVars.TOTAL_ATTACHED_FILE_MAX_SIZE);
				upload.setFileSizeMax(WebCommonStaticFinalVars.ATTACHED_FILE_MAX_SIZE);

				// Parse the request
				List<FileItem> fileItemList = null;
				try {
					/**
					 * WARNING! 파싱은 request 가 가진  입력 스트림을 소진합니다, 하여 파싱후 그 결과를 전달해 주어야 합니다.
					 * 이때 쓰레드 세이프 문제 때문에 변수 fileItemList 는 멤버 변수가 아닌 request 객체를 통해 전달합니다.
					 */
					fileItemList = upload.parseRequest(requestMock);					
					when(requestMock.getAttribute("fileItemList")).thenReturn(fileItemList);
				} catch (FileUploadException e) {
					log.error("dead code", e);
					System.exit(1);

				}

				return requestMock;
			}
		}
		
		HttpServletRequest requestMock = new BoardReplyProcessRequestMockBuilder().build(loginID,
				paramBoardID,
				paramParentBoardNo,
				paramSubject, paramContents, attachedFileList);
		HttpServletResponse responseMock = mock(HttpServletResponse.class);
				
		BoardReplyProcessSvl boardReplyProcessSvl = new BoardReplyProcessSvl();
		
		try {
			initServlet(boardReplyProcessSvl, "/servlet/BoardList");
		} catch (Exception e) {
			log.warn("서블릿 초기화 실패", e);
			fail("서블릿 초기화 실패");
		}
		
		BoardReplyRes boardReplyRes = null;
		
		try {
			boardReplyRes = boardReplyProcessSvl.doWork(requestMock, responseMock);
			
			// log.info(boardReplyRes.toString());
		} catch (WebClientException e) {
			log.warn("errmsg={}, debugmsg={}", e.getErrorMessage(), e.getDebugMessage());
			fail("게시판 댓글 서블릿 수행 실패");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("게시판 댓글 서블릿 수행 실패");
		}
		
		return boardReplyRes;
	}

	
	
	private BoardModifyRes executeBoardModifyProcessServlet(String loginID, 
			String paramBoardID, String paramBoardNo, String paramSubject, String paramContents, 
			String paramNextAttachedFileSeq,
			List<Short> oldAttachedFileSeqList, List<File> newAttachedFileList) {
		
		class BoardModifyProcessRequestMockBuilder {
			public HttpServletRequest build(String loginID, 
					String paramBoardID, String paramBoardNo, String paramSubject, String paramContents, 
					String paramNextAttachedFileSeq,
					List<Short> oldAttachedFileSeqList, List<File> newAttachedFileList) {
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
				
				String sessionKeyBase64 = null;
				String ivBase64 = null;	
				try {
					sessionKeyBase64 = CommonStaticUtil.Base64Encoder.encodeToString(clientSessionKey.getDupSessionKeyBytes());
					ivBase64 = CommonStaticUtil.Base64Encoder.encodeToString(clientSessionKey.getDupIVBytes());
				} catch (Exception e) {
					log.warn("파라미터 구성 실패", e);
					fail("파라미터 구성 실패");
				}
				
				MultipartEntityBuilder writeBuilder = MultipartEntityBuilder.create();

				writeBuilder.addTextBody(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY, 
						sessionKeyBase64, 
						ContentType.create("text/plain", "UTF-8"));
				
				writeBuilder.addTextBody(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV, 
						ivBase64, 
						ContentType.create("text/plain", "UTF-8"));
				
				writeBuilder.addTextBody("boardID", paramBoardID, 
						ContentType.create("text/plain", "UTF-8"));		
				
				writeBuilder.addTextBody("boardNo", paramBoardNo, 
						ContentType.create("text/plain", "UTF-8"));
				
				writeBuilder.addTextBody("subject", paramSubject, 
						ContentType.create("text/plain", "UTF-8"));
				
				writeBuilder.addTextBody("contents", paramContents, 
						ContentType.create("text/plain", "UTF-8"));
				
				writeBuilder.addTextBody("nextAttachedFileSeq", paramNextAttachedFileSeq, 
						ContentType.create("text/plain", "UTF-8"));				

				for (short oldAttachedFileSeq : oldAttachedFileSeqList) {
					writeBuilder.addTextBody("oldAttachedFileSeq", String.valueOf(oldAttachedFileSeq), 
							ContentType.create("text/plain", "UTF-8"));
				}

				for (File newAttachedFile : newAttachedFileList) {
					byte[] contentsOfUploadFile = null;
					try {
						contentsOfUploadFile = CommonStaticUtil.readFileToByteArray(newAttachedFile, WebCommonStaticFinalVars.ATTACHED_FILE_MAX_SIZE);
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
					
					writeBuilder.addBinaryBody("newAttachedFile", contentsOfUploadFile, ContentType.create(mimeTypeOfUploadFile), newAttachedFile.getName());
				}		
				
				HttpEntity writeEntity = writeBuilder.build();

		        ByteArrayOutputStream writeByteArrayOputStream = new ByteArrayOutputStream();
		        try {
					writeEntity.writeTo(writeByteArrayOputStream);
				} catch (IOException e) {
					log.warn("입출력 에러로 HttpEntity 의 내용을 출력 스트림에 쓰기  실패, errmsg={}", e.getMessage());
					fail("입출력 에러로 HttpEntity 의 내용을 출력 스트림에 쓰기  실패");
				}
		        
		        ByteArrayInputStream writeByteArrayInputStream = new ByteArrayInputStream(writeByteArrayOputStream.toByteArray());
		        
		        HttpSession sessionMock = mock(HttpSession.class);
				when(sessionMock.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_USER_INFORMATION))
				.thenReturn(new AccessedUserInformation(true, loginID, "테스터", MemberRoleType.MEMBER));
				
				HttpServletRequest requestMock = mock(HttpServletRequest.class);
				when(requestMock.getMethod()).thenReturn("POST");
				when(requestMock.getContentType()).thenReturn(writeEntity.getContentType().getValue());
		        when(requestMock.getContentLength()).thenReturn((int)writeEntity.getContentLength());
		        try {
					when(requestMock.getInputStream()).thenReturn(new MockServletInputStream(writeByteArrayInputStream));
				} catch (IOException e) {
					fail("dead code");
				}
				when(requestMock.getSession()).thenReturn(sessionMock);
				when(requestMock.getRequestURI()).thenReturn("/servlet/BoardWriteProcess");
				when(requestMock.getRemoteHost()).thenReturn("");
				when(requestMock.getRemoteAddr()).thenReturn("172.0.1.32");
				when(requestMock.getRemoteUser()).thenReturn("");
				
				// Create a factory for disk-based file items
				DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();

				// Set factory constraints
				diskFileItemFactory.setSizeThreshold(WebCommonStaticFinalVars.APACHE_FILEUPLOAD_MAX_MEMORY_SIZE);
				
				CoddaConfigurationManager configurationManager = CoddaConfigurationManager.getInstance();
				CoddaConfiguration runningProjectConfiguration = configurationManager.getRunningProjectConfiguration();
				String mainProjectName = runningProjectConfiguration.getMainProjectName();
				String installedPathString = runningProjectConfiguration.getInstalledPathString();
				String userWebTempPathString = WebRootBuildSystemPathSupporter.getUserWebTempPathString(installedPathString,
						mainProjectName);
				
				diskFileItemFactory.setRepository(new File(userWebTempPathString));

				// Create a new file upload handler
				ServletFileUpload upload = new ServletFileUpload(diskFileItemFactory);
				// upload.setHeaderEncoding("UTF-8");
				// log.info("upload.getHeaderEncoding={}", upload.getHeaderEncoding());
				// log.info("req.getCharacterEncoding={}", req.getCharacterEncoding());

				// Set overall request size constraint
				upload.setSizeMax(WebCommonStaticFinalVars.TOTAL_ATTACHED_FILE_MAX_SIZE);
				upload.setFileSizeMax(WebCommonStaticFinalVars.ATTACHED_FILE_MAX_SIZE);

				// Parse the request
				List<FileItem> fileItemList = null;
				try {
					/**
					 * WARNING! 파싱은 request 가 가진  입력 스트림을 소진합니다, 하여 파싱후 그 결과를 전달해 주어야 합니다.
					 * 이때 쓰레드 세이프 문제 때문에 변수 fileItemList 는 멤버 변수가 아닌 request 객체를 통해 전달합니다.
					 */
					fileItemList = upload.parseRequest(requestMock);					
					when(requestMock.getAttribute("fileItemList")).thenReturn(fileItemList);
				} catch (FileUploadException e) {
					log.error("dead code", e);
					System.exit(1);

				}

				return requestMock;
			}
		}
		
		HttpServletRequest requestMock = new BoardModifyProcessRequestMockBuilder().build(loginID,
				paramBoardID, paramBoardNo,
				paramSubject, paramContents, paramNextAttachedFileSeq, oldAttachedFileSeqList, newAttachedFileList);
		HttpServletResponse responseMock = mock(HttpServletResponse.class);
				
				
		BoardModifyProcessSvl boardModifyProcessSvl = new BoardModifyProcessSvl();
		
		try {
			initServlet(boardModifyProcessSvl, "/servlet/BoardList");
		} catch (Exception e) {
			log.warn("서블릿 초기화 실패", e);
			fail("서블릿 초기화 실패");
		}
		
		BoardModifyRes boardModifyRes = null;
		
		try {
			// boardWriteProcessSvl.service(requestMock, responseMock);			
			boardModifyRes = boardModifyProcessSvl.doWork(requestMock, responseMock);
			
			// log.info(boardWriteRes.toString());
		} catch (WebClientException e) {
			log.warn("errmsg={}, debugmsg={}", e.getErrorMessage(), e.getDebugMessage());
			fail("게시판 수정 서블릿 수행 실패");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("게시판 수정 서블릿 수행 실패");
		}
		
		return boardModifyRes;
	}
	
	
	@Test
	public void 게시글본문쓰기_정상() {
		List<File> attachedFileList = new ArrayList<File>();
		
		String[] attachedFileShortNames = {"sinnori_server_framework01.png", 
				"sinnori_server_framework02.png"};
		
		for (String attachedFileShortName : attachedFileShortNames) {
			String writeUploadFilePathString = new StringBuilder()
			.append(WebRootBuildSystemPathSupporter.getUserWebRootPathString(installedPath.getAbsolutePath(), mainProjectName))
			.append(File.separator)
			.append("images")
			.append(File.separator).append(attachedFileShortName).toString();
			
			File writeUploadFile = new File(writeUploadFilePathString);
			
			if (! writeUploadFile.exists()) {
				log.warn("업로드할 대상 파일[{}]이 존재하지 않습니다", writeUploadFilePathString);
				fail("업로드할 대상 파일이 존재하지 않습니다");
			}
			
			if (! writeUploadFile.isFile()) {
				log.warn("업로드할 대상 파일[{}]이 일반 파일이 아닙니다", writeUploadFilePathString);
				fail("업로드할 대상 파일이 존재하지 않습니다");
			}
			
			attachedFileList.add(writeUploadFile);
		}		
		
		String paramBoardID = "3";
		
		BoardWriteRes boardWriteRes = executeBoardWriteProcessServlet("test00", 
				paramBoardID, 
				"본문제목::01", "본문내용::01", attachedFileList);
		log.info(boardWriteRes.toString());
	}

	@Test
	public void 게시글댓글_정상() {
		List<File> attachedFileList = new ArrayList<File>();
		
		String writeUploadFilePathString = new StringBuilder()
		.append(WebRootBuildSystemPathSupporter.getUserWebRootPathString(installedPath.getAbsolutePath(), mainProjectName))
		.append(File.separator)
		.append("images")
		.append(File.separator).append("sinnori_server_framework01.png").toString();
		
		File writeUploadFile = new File(writeUploadFilePathString);
		
		if (! writeUploadFile.exists()) {
			log.warn("업로드할 대상 파일[{}]이 존재하지 않습니다", writeUploadFilePathString);
			fail("업로드할 대상 파일이 존재하지 않습니다");
		}
		
		if (! writeUploadFile.isFile()) {
			log.warn("업로드할 대상 파일[{}]이 일반 파일이 아닙니다", writeUploadFilePathString);
			fail("업로드할 대상 파일이 존재하지 않습니다");
		}
		
		attachedFileList.add(writeUploadFile);
		
		String paramBoardID = "3";
		
		BoardWriteRes boardWriteRes = executeBoardWriteProcessServlet("test00", 
				paramBoardID, 
				"본문제목::02", "본문내용::02", attachedFileList);
		
		attachedFileList.clear();
		
		String replyUploadFilePathString = new StringBuilder()
		.append(WebRootBuildSystemPathSupporter.getUserWebRootPathString(installedPath.getAbsolutePath(), mainProjectName))
		.append(File.separator)
		.append("images")
		.append(File.separator).append("sinnori_webpage_frame.png").toString();
		
		File replyUploadFile = new File(replyUploadFilePathString);
		
		if (! replyUploadFile.exists()) {
			log.warn("업로드할 대상 파일[{}]이 존재하지 않습니다", replyUploadFilePathString);
			fail("업로드할 대상 파일이 존재하지 않습니다");
		}
		
		if (! replyUploadFile.isFile()) {
			log.warn("업로드할 대상 파일[{}]이 일반 파일이 아닙니다", replyUploadFilePathString);
			fail("업로드할 대상 파일이 존재하지 않습니다");
		}		
		
		attachedFileList.add(replyUploadFile);	
		
		
		
		BoardReplyRes boardReplyRes = execuateBoardReplyProcessServlet("test00",
				paramBoardID,
				String.valueOf(boardWriteRes.getBoardNo()), 
				"댓글제목::02-01", "본문내용::02-01", attachedFileList);
		
		log.info(boardReplyRes.toString());		
	}
	
	@Test
	public void 게시글수정_정상() {
		List<File> attachedFileList = new ArrayList<File>();
		
		String[] attachedFileShortNames = {"sinnori_server_framework01.png", 
				"sinnori_server_framework02.png"};
		
		for (String attachedFileShortName : attachedFileShortNames) {
			String writeUploadFilePathString = new StringBuilder()
			.append(WebRootBuildSystemPathSupporter.getUserWebRootPathString(installedPath.getAbsolutePath(), mainProjectName))
			.append(File.separator)
			.append("images")
			.append(File.separator).append(attachedFileShortName).toString();
			
			File writeUploadFile = new File(writeUploadFilePathString);
			
			if (! writeUploadFile.exists()) {
				log.warn("업로드할 대상 파일[{}]이 존재하지 않습니다", writeUploadFilePathString);
				fail("업로드할 대상 파일이 존재하지 않습니다");
			}
			
			if (! writeUploadFile.isFile()) {
				log.warn("업로드할 대상 파일[{}]이 일반 파일이 아닙니다", writeUploadFilePathString);
				fail("업로드할 대상 파일이 존재하지 않습니다");
			}
			
			attachedFileList.add(writeUploadFile);
		}		
		
		String paramBoardID = "3";
		
		BoardWriteRes boardWriteRes = executeBoardWriteProcessServlet("test00", 
				paramBoardID, 
				"본문제목::03", "본문내용::03", attachedFileList);
		
		List<File> newAttachedFileList = new ArrayList<File>();
		String[] newAttachedFileShortNames = {"sinnori_web_network_diagram.png"};
		
		for (String newAttachedFileShortName : newAttachedFileShortNames) {
			String writeUploadFilePathString = new StringBuilder()
			.append(WebRootBuildSystemPathSupporter.getUserWebRootPathString(installedPath.getAbsolutePath(), mainProjectName))
			.append(File.separator)
			.append("images")
			.append(File.separator).append(newAttachedFileShortName).toString();
			
			File writeUploadFile = new File(writeUploadFilePathString);
			
			if (! writeUploadFile.exists()) {
				log.warn("업로드할 대상 파일[{}]이 존재하지 않습니다", writeUploadFilePathString);
				fail("업로드할 대상 파일이 존재하지 않습니다");
			}
			
			if (! writeUploadFile.isFile()) {
				log.warn("업로드할 대상 파일[{}]이 일반 파일이 아닙니다", writeUploadFilePathString);
				fail("업로드할 대상 파일이 존재하지 않습니다");
			}
			
			newAttachedFileList.add(writeUploadFile);
		}	
		
		List<Short> oldAttachedFileSeqList = new ArrayList<Short>();
		oldAttachedFileSeqList.add((short)1);		
		
		BoardModifyRes boardModifyRes = executeBoardModifyProcessServlet("test00", paramBoardID,
				String.valueOf(boardWriteRes.getBoardNo()),
				"본문제목::수정03", "본문내용::수정03", String.valueOf(attachedFileList.size()), 
				oldAttachedFileSeqList, newAttachedFileList);
		
		log.info(boardModifyRes.toString());
		
	}
}
