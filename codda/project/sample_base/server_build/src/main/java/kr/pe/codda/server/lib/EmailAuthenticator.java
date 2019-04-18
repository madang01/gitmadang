package kr.pe.codda.server.lib;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class EmailAuthenticator extends Authenticator {
	final PasswordAuthentication pa;
	
	public EmailAuthenticator(String userID, String password) {
		pa = new PasswordAuthentication(userID, password);
	}
	// 시스템에서 사용하는 인증정보
    public PasswordAuthentication getPasswordAuthentication() {
        return pa;
    }
}
