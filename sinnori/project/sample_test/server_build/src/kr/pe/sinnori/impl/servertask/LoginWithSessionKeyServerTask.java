package kr.pe.sinnori.impl.servertask;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.HashMap;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.serverlib.ValueChecker;
import kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.SymmetricKey;
import kr.pe.sinnori.impl.message.LoginWithSessionKey.LoginWithSessionKey;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.server.mybatis.SqlSessionFactoryManger;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

import org.apache.commons.codec.binary.Base64;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class LoginWithSessionKeyServerTask extends AbstractServerTask {
	private SqlSessionFactory sqlSessionFactory = null;

	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager,
			LetterSender letterSender, AbstractMessage messageFromClient)
			throws Exception {
		LoginWithSessionKey inObj = (LoginWithSessionKey)messageFromClient;
		
		// FIXME!
		log.info(inObj.toString());		
		
		if (null == sqlSessionFactory) {
			sqlSessionFactory = SqlSessionFactoryManger.getInstance()
					.getSqlSessionFactory("tw_sinnoridb");
		}		
		
		MessageResult messageResultOutObj = new MessageResult();
		messageResultOutObj.setIsSuccess(false);
		messageResultOutObj.setTaskMessageID(inObj.getMessageID());
		
		String idCipherBase64 = inObj.getIdCipherBase64();		
		String pwdCipherBase64 = inObj.getPwdCipherBase64();		
		String sessionKeyBase64 = inObj.getSessionKeyBase64();		
		String ivBase64 = inObj.getIvBase64();		
		
		SymmetricKey symmetricKey = null;
		try {
			symmetricKey = ServerSessionKeyManager.getInstance()
					.getSymmetricKey(sessionKeyBase64, ivBase64);
		} catch (IllegalArgumentException e) {
			String errorMessage = String.format("잘못된 파라미터로 인한 대칭키 생성 실패, inObj=[%s]", inObj.toString());
			log.warn(errorMessage, e);
			messageResultOutObj.setResultMessage("잘못된 파라미터로 인한 대칭키 생성 실패");	
			
			letterSender.addSyncMessage(messageResultOutObj);
			return;
		} catch (SymmetricException e) {
			String errorMessage = String.format("알수 없는 이유로 대칭키 생성 실패, inObj=[%s]", inObj.toString());
			log.warn(errorMessage, e);			
			messageResultOutObj.setResultMessage("알수 없는 이유로 대칭키 생성 실패");

			letterSender.addSyncMessage(messageResultOutObj);
			return;
		}
		
		String userId = null;
		String password = null;
		
		try {
			userId = symmetricKey.decryptStringBase64(idCipherBase64);
		} catch (IllegalArgumentException e) {
			String errorMessage = String.format("잘못된 파라미터로 인한 아이디 복호문 얻기 실패, inObj=[%s]", inObj.toString());
			log.warn(errorMessage, e);
			messageResultOutObj.setResultMessage("잘못된 파라미터로 인한 아이디 복호문 얻기 실패");

			letterSender.addSyncMessage(messageResultOutObj);
			return;
		} catch (SymmetricException e) {
			String errorMessage = String.format("알수 없는 이유로 아이디 복호문 얻기 실패, inObj=[%s]", inObj.toString());
			log.warn(errorMessage, e);
			messageResultOutObj.setResultMessage("알수 없는 이유로 아이디 복호문 얻기 실패");

			letterSender.addSyncMessage(messageResultOutObj);
			return;
		}
		
		if (null== userId) {
			log.warn("아이디 복호문 값을 얻는데 실패하였습니다. inObj=[{}]", inObj.toString());			
			messageResultOutObj.setResultMessage("아이디 복호문 값을 얻는데 실패하였습니다.");

			letterSender.addSyncMessage(messageResultOutObj);
			return;
		}
		
		try {
			ValueChecker.checkValidUserId(userId);
		} catch(RuntimeException e) {
			String errorMessage = e.getMessage();
			log.warn("%s userId=[{}]", errorMessage, userId);			
			messageResultOutObj.setResultMessage(errorMessage);
		}

		try {
			password = symmetricKey.decryptStringBase64(pwdCipherBase64);
		} catch (IllegalArgumentException e) {
			String errorMessage = String.format("잘못된 파라미터로 인한 비밀번호 복호문 얻기 실패, inObj=[%s]", inObj.toString());
			log.warn(errorMessage, e);
			messageResultOutObj.setResultMessage("잘못된 파라미터로 인한 비밀번호 복호문 얻기 실패");

			letterSender.addSyncMessage(messageResultOutObj);
			return;
		} catch (SymmetricException e) {
			String errorMessage = String.format("알수 없는 이유로 비밀번호 복호문 얻기 실패, inObj=[%s]", inObj.toString());
			log.warn(errorMessage, e);
			messageResultOutObj.setResultMessage("알수 없는 이유로 비밀번호 복호문 얻기 실패");

			letterSender.addSyncMessage(messageResultOutObj);
			return;
		}
		
		if (null== password) {
			log.warn("비밀번호 복호문 값을 얻는데 실패하였습니다. inObj=[{}]", inObj.toString());
			
			messageResultOutObj.setResultMessage("비밀번호 복호문 값을 얻는데 실패하였습니다.");
			letterSender.addSyncMessage(messageResultOutObj);
			return;
		}
		
		try {
			ValueChecker.checkValidPwd(password);
		} catch(RuntimeException e) {
			String errorMessage = e.getMessage();
			log.warn("%s userId=[{}]", errorMessage, userId);			
			messageResultOutObj.setResultMessage(errorMessage);
		}
		
		
		/**
		 * 비밀번호는 철저히 보안을 유지 해야 하므로 찍지 않음.
		 * 필요시 특정 아이디에 한에서만 찍도록 해야함.
		 */
		log.info("로그인 아이디[{}] 처리전", userId);
			
		
		SqlSession session = sqlSessionFactory.openSession(false);
		try {
			HashMap<String, Object> memberHash = session.selectOne("getMemberByIDInLock", userId);
			if (null == memberHash) {
				messageResultOutObj.setResultMessage("아이디["+userId+"] 가 존재하지 않습니다.");				
			} else {			
				int memberGubun = (Integer)memberHash.get("memberGubun");
				int memberState = (Integer)memberHash.get("memberState");
				int pwdFailCount = (Integer)memberHash.get("pwdFailCount");
				
				if (5 <= pwdFailCount) {
					messageResultOutObj.setResultMessage("비밀번호 5회 이상 틀렸습니다. 비밀번호 초기화를 수행하시기 바랍니다.");
				} else {
					String pwdMDBase64 = (String)memberHash.get("pwdBase64");
					String pwdSaltBase64 = (String)memberHash.get("pwdSaltBase64");
					
					
					if (2 == memberGubun) {
						messageResultOutObj.setResultMessage("블락된 회원["+userId+"] 입니다.");
					} else if (0 != memberGubun && 1 != memberGubun) {
						messageResultOutObj.setResultMessage("알수없는 회원 구분["+memberGubun+"] 을 가진 회원["+userId+"] 입니다.");
					} else {						
						if (1 == memberState) {
							messageResultOutObj.setResultMessage("탈퇴한 회원["+userId+"] 입니다.");					
						} else if (0 != memberState) {
							messageResultOutObj.setResultMessage("알수없는 상태["+memberState+"] 을 가진 회원["+userId+"] 입니다.");
						} else {							
							byte[] pwdSaltByteArray = Base64.decodeBase64(pwdSaltBase64);
							byte[] passwordByteArray = password.getBytes(CommonStaticFinalVars.SINNORI_PASSWORD_CHARSET);
							
							ByteBuffer passwordByteBuffer = ByteBuffer.allocate(pwdSaltByteArray.length+passwordByteArray.length);
							passwordByteBuffer.put(pwdSaltByteArray);
							passwordByteBuffer.put(passwordByteArray);
							MessageDigest md = MessageDigest.getInstance(CommonStaticFinalVars.SINNORI_PASSWORD_ALGORITHM_NAME);
							
							md.update(passwordByteBuffer.array());
							
							// FIXME!
							// log.info(HexUtil.getAllHexStringFromByteBuffer(passwordByteBuffer));
							
							byte pwdMDByteArray[] =  md.digest();
							String userPwdMDBase64 = Base64.encodeBase64String(pwdMDByteArray);
							
							if (!pwdMDBase64.equals(userPwdMDBase64)) {
								memberHash.put("pwdFailCount", pwdFailCount+1);
								session.update("updatePwdFailCnt", memberHash);								
								messageResultOutObj.setResultMessage("비밀 번호가 다릅니다.");
							} else {
								if (pwdFailCount > 0) {
									memberHash.put("pwdFailCount", 0);								
									session.update("updatePwdFailCnt", memberHash);
									
								}

								messageResultOutObj.setIsSuccess(true);
								messageResultOutObj.setResultMessage("로그인 성공하셨습니다.");
							}
						}
					}
				}
			}
			
			session.commit();
		} catch(Exception e) {
			log.warn("unknown error", e);
			session.rollback();
			
			messageResultOutObj.setResultMessage("2.로그인이 실패했습니다.");
		} finally {
			session.close();
		}
		
		letterSender.addSyncMessage(messageResultOutObj);
		
	}

}
