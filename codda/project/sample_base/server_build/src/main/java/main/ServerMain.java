package main;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.NotFoundProjectException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.server.AnyProjectServer;
import kr.pe.codda.server.MainServerManager;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;

public class ServerMain {

	public static void main(String argv[]) throws NotFoundProjectException {
		Logger log = LoggerFactory.getLogger(CommonStaticFinalVars.BASE_PACKAGE_NAME);
		
		try {
			ServerDBUtil.initializeDBEnvoroment(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME);
			
			{
				String userID = "admin";
				byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's',
						(byte) 't', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
						(byte) '$' };
				String nickname = "단위테스터용어드민";
				String email = "admin@codda.pe.kr";
				String ip = "127.0.0.1";

				try {
				ServerDBUtil.registerMember(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME,
						MemberRoleType.ADMIN, userID, nickname, email, 
						passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
				} catch (ServerServiceException e) {
					String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[")
							.append(userID)
							.append("] 입니다").toString();
					String actualErrorMessag = e.getMessage();
					
					// log.warn(actualErrorMessag, e);
					
					if (! expectedErrorMessage.equals(actualErrorMessag)) {
						throw e;
					}
				}
				
			}
			
			{
				String userID = "guest";
				byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's',
						(byte) 't', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
						(byte) '$' };
				String nickname = "손님";
				String email = "guest@codda.pe.kr";
				String ip = "127.0.0.1";

				try {
					ServerDBUtil.registerMember(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME,
							MemberRoleType.GUEST, userID, nickname, email, 
							passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
				} catch (ServerServiceException e) {
					String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[")
							.append(userID)
							.append("] 입니다").toString();
					String actualErrorMessag = e.getMessage();
					
					// log.warn(actualErrorMessag, e);
					
					if (! expectedErrorMessage.equals(actualErrorMessag)) {
						throw e;
					}
				}
				
			}
			
			AnyProjectServer mainProjectServer = MainServerManager.getInstance().getMainProjectServer();
			mainProjectServer.startServer();
		} catch (Throwable e) {
			log.warn("unknown error", e);
		}
	}
}
