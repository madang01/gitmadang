package kr.pe.codda.servlet.user;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.mockito.ArgumentMatchers;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.sessionkey.ClientSessionKeyIF;
import kr.pe.codda.common.sessionkey.ClientSessionKeyManager;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.exception.WebClientException;
import nl.captcha.Captcha;

public class MembershipProcessSvlTest extends AbstractJunitTest {

	private void initServlet(HttpServlet targetServlet, String menuGroupURL) throws Exception {
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

	
	
	@Test
	public void  일반회원가입_정상() {

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
				log.warn(messageResultRes.getResultMessage());
				fail(messageResultRes.getResultMessage());
			}
			
		} catch (WebClientException e) {
			log.warn("errmsg={}, debugmsg={}", e.getErrorMessage(), e.getDebugMessage());
			fail("일반 회원 가입 서블릿 수행 실패");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("일반 회원 가입 서블릿 수행 실패");
		}
	}
}
