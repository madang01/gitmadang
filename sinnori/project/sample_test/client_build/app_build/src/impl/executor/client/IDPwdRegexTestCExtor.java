package impl.executor.client;

import java.net.SocketTimeoutException;

import kr.pe.sinnori.client.ClientProjectIF;
import kr.pe.sinnori.common.configuration.ClientProjectConfig;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.util.AbstractClientExecutor;

public class IDPwdRegexTestCExtor extends AbstractClientExecutor {

	@Override
	protected void doTask(ClientProjectConfig clientProjectConfig,
			ClientProjectIF clientProject) throws SocketTimeoutException,
			ServerNotReadyException, NoMoreDataPacketBufferException,
			BodyFormatException, DynamicClassCallException,
			ServerTaskException, NotLoginException {
		String id = null;
		String pwd = null;
		
		String regexID = "^\\p{Alpha}\\p{Alnum}{3,14}$";
		
		id = "한abc";		
		if (!id.matches(regexID)) {
			System.out.println(String.format("id[%s] 정규식[%s] 실패", id, regexID));
		} else {
			System.out.println(String.format("id[%s] 정규식[%s] 성공", id, regexID));
		}
		
		
		id = "abc";		
		if (!id.matches(regexID)) {
			System.out.println(String.format("id[%s] 정규식[%s] 실패", id, regexID));
		} else {
			System.out.println(String.format("id[%s] 정규식[%s] 성공", id, regexID));
		}
		
		id = "1abc";		
		if (!id.matches(regexID)) {
			System.out.println(String.format("id[%s] 정규식[%s] 실패", id, regexID));
		} else {
			System.out.println(String.format("id[%s] 정규식[%s] 성공", id, regexID));
		}
		
		id = "1abc4";		
		if (!id.matches(regexID)) {
			System.out.println(String.format("id[%s] 정규식[%s] 실패", id, regexID));
		} else {
			System.out.println(String.format("id[%s] 정규식[%s] 성공", id, regexID));
		}
		
		id = "1abct";		
		if (!id.matches(regexID)) {
			System.out.println(String.format("id[%s] 정규식[%s] 실패", id, regexID));
		} else {
			System.out.println(String.format("id[%s] 정규식[%s] 성공", id, regexID));
		}
		
		id = "abc4d";		
		if (!id.matches(regexID)) {
			System.out.println(String.format("id[%s] 정규식[%s] 실패", id, regexID));
		} else {
			System.out.println(String.format("id[%s] 정규식[%s] 성공", id, regexID));
		}
		
		id = "abcttw";		
		if (!id.matches(regexID)) {
			System.out.println(String.format("id[%s] 정규식[%s] 실패", id, regexID));
		} else {
			System.out.println(String.format("id[%s] 정규식[%s] 성공", id, regexID));
		}
		
		
		/*
		 * var regexp_pwd = /^[A-Za-z0-9\`~!@\#$%<>\^&*\(\)\-=+_\'\[\]\{\}\\\|\:\;\"<>\?,\.\/]{8,15}$/;
		var regexp_pwd_alphabet = /[A-Za-z]{1,}/;
		var regexp_pwd_digit = /[0-9]{1,}/;
		var regexp_pwd_special = /[\`~!@\#$%<>\^&*\(\)\-=+_\'\[\]\{\}\\\|\:\;\"<>\?,\.\/]{1,}/;
		 */
		
		String regexPwd = "^\\p{Graph}{1,50}$";
		String regexPwdAlpha = ".*\\p{Alpha}{1,}.*";
		String regexPwdDigit = ".*\\p{Digit}{1,}.*";
		String regexPwdPunct = ".*\\p{Punct}{1,}.*";
		/** \p{Graph} : !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~ */
		
		pwd = "!{}";	
		if (!pwd.matches(regexPwd)) {
			System.out.println(String.format("pwd[%s] 정규식[%s] 실패", pwd, regexPwd));
		} else {
			System.out.println(String.format("pwd[%s] 정규식[%s] 성공", pwd, regexPwd));
		}	
				
		pwd = "`~!@#$%^1a&#12&12**ab(23a)-1=_{}[]\\|;':\",./<>?한글";
		if (!pwd.matches(regexPwd)) {
			System.out.println(String.format("pwd[%s] 정규식[%s] 실패", pwd, regexPwd));
		} else {
			System.out.println(String.format("pwd[%s] 정규식[%s] 성공", pwd, regexPwd));
		}	
		
		pwd = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";	
		if (!pwd.matches(regexPwd)) {
			System.out.println(String.format("pwd[%s] 정규식[%s] 실패", pwd, regexPwd));
		} else {
			System.out.println(String.format("pwd[%s] 정규식[%s] 성공", pwd, regexPwd));
		}	
		
		pwd = "!\"<=>?@[\\]^_`{|}~";
		if (!pwd.matches(regexPwdAlpha)) {
			System.out.println(String.format("pwd[%s] 정규식[%s] 실패", pwd, regexPwdAlpha));
		} else {
			System.out.println(String.format("pwd[%s] 정규식[%s] 성공", pwd, regexPwdAlpha));
		}	
		
		
		pwd = "!\"/:;a<=>?@[\\]^_`{|}~";
		if (!pwd.matches(regexPwdAlpha)) {
			System.out.println(String.format("pwd[%s] 정규식[%s] 실패", pwd, regexPwdAlpha));
		} else {
			System.out.println(String.format("pwd[%s] 정규식[%s] 성공", pwd, regexPwdAlpha));
		}	
		
		pwd = "!\"<=>?@[\\]^_`{|}~";
		if (!pwd.matches(regexPwdDigit)) {
			System.out.println(String.format("pwd[%s] 정규식[%s] 실패", pwd, regexPwdDigit));
		} else {
			System.out.println(String.format("pwd[%s] 정규식[%s] 성공", pwd, regexPwdDigit));
		}	
		
		
		pwd = "!\"/:;1<=>?@[\\]^_`{|}~";
		if (!pwd.matches(regexPwdDigit)) {
			System.out.println(String.format("pwd[%s] 정규식[%s] 실패", pwd, regexPwdDigit));
		} else {
			System.out.println(String.format("pwd[%s] 정규식[%s] 성공", pwd, regexPwdDigit));
		}
		
		pwd = "aaaaaaabbbbbb";
		if (!pwd.matches(regexPwdPunct)) {
			System.out.println(String.format("pwd[%s] 정규식[%s] 실패", pwd, regexPwdPunct));
		} else {
			System.out.println(String.format("pwd[%s] 정규식[%s] 성공", pwd, regexPwdPunct));
		}	
		
		
		pwd = "aaaaaaa.bbbbbb";
		if (!pwd.matches(regexPwdPunct)) {
			System.out.println(String.format("pwd[%s] 정규식[%s] 실패", pwd, regexPwdPunct));
		} else {
			System.out.println(String.format("pwd[%s] 정규식[%s] 성공", pwd, regexPwdPunct));
		}
		
	}


}
