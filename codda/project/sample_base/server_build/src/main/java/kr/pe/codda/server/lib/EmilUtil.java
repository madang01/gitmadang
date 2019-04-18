package kr.pe.codda.server.lib;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.SequencedPropertiesUtil;

public abstract class EmilUtil {
	public static void sendPasswordSearchEmail(AccountSearchType searchWhatType, String nickname, String email, String secretAuthenticationValue) throws Exception {

		String subject = new StringBuilder()
					.append("Codda 에서 ")
					.append(nickname)
					.append(" 님께 ")
					.append(searchWhatType.getName())
					.append(" 찾기용 비밀 값을 보내드립니다").toString();

		String body = new StringBuilder()
				.append("<!DOCTYPE html>")
				.append(CommonStaticFinalVars.NEWLINE)
				.append("<html>")
				.append(CommonStaticFinalVars.NEWLINE)
				.append("<head>")
				.append(CommonStaticFinalVars.NEWLINE)
				.append("<meta charset=\"utf-8\">")
				.append(CommonStaticFinalVars.NEWLINE)
				.append("</head>")
				.append(CommonStaticFinalVars.NEWLINE)
				.append("<body>")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(nickname)
				.append(" 님 안녕하세요.")
				.append(CommonStaticFinalVars.NEWLINE)
				.append("Codda 에서 ")
				.append(searchWhatType.getName())
				.append(" 찾기에 필요한 비밀 값 [<b>")
				.append(secretAuthenticationValue)
				.append("</b>] 을 보내드립니다<br>")
				.append(CommonStaticFinalVars.NEWLINE)
				.append("비밀 값은 베이스64로 인코딩한 문자열입니다<br>")
				.append(CommonStaticFinalVars.NEWLINE)
				.append("대괄호 안에 있는 비밀 값을 입력하여 ")
				.append(searchWhatType.getName())
				.append(" 찾기를 완료해 주시기 바랍니다")
				.append(CommonStaticFinalVars.NEWLINE)
				.append("</body>")
				.append(CommonStaticFinalVars.NEWLINE)
				.append("</html>").toString();		
		
		sendEmail(email, subject, body);
	}
	
	/**
	 * 지정한 이메일 주소로 메일을 보낸다
	 *  
	 * @param recipient 받을 사람 이메일 주소
	 * @param subject 제목
	 * @param body 내용
	 * @throws Exception
	 */
	public static void sendEmail(String recipient, String subject, String body) throws Exception {
		CoddaConfiguration mainProjectConfiguration = CoddaConfigurationManager.getInstance().getRunningProjectConfiguration();
		
		String installedPathString = mainProjectConfiguration.getInstalledPathString();
		String mainProjectName = mainProjectConfiguration.getMainProjectName();

		String projectEmailPropertiesFilePathString = ProjectBuildSytemPathSupporter.getProjectEmailPropertiesFilePathString(installedPathString, mainProjectName);
		
		
		Properties props = SequencedPropertiesUtil.loadSequencedPropertiesFile(projectEmailPropertiesFilePathString, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		
		String mailUserID = null;
		String mailUserPassword = null;
		String sender = null;
		
		mailUserID = props.getProperty("codda.email.account.id.value");
		mailUserPassword = props.getProperty("codda.email.account.pwd.value");
		sender = props.getProperty("codda.email.from.value");
				
		props.remove("codda.email.account.id.value");
		props.remove("codda.email.account.pwd.value");
		props.remove("codda.email.from.value");
		
		
		Authenticator auth = new EmailAuthenticator(mailUserID, mailUserPassword);
		  
		Session s = Session.getInstance(props,  auth);
		
		s.setDebug(true);
		  
		MimeMessage message = new MimeMessage(s);
		  
		InternetAddress from = new InternetAddress(sender, "Codda admin");
		InternetAddress to = new InternetAddress(recipient);
		  
		message.setSentDate(new Date());
		message.setFrom(from);
		message.addRecipient(Message.RecipientType.TO, to);
		  
		message.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));
		message.setContent(body, "text/html;charset=utf-8");
		
		Transport.send(message);
	}
}
