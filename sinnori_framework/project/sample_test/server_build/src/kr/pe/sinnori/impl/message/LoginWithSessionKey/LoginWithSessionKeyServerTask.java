package kr.pe.sinnori.impl.message.LoginWithSessionKey;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.HashMap;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.SymmetricKey;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.SinnoriSqlSessionFactoryIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

import org.apache.commons.codec.binary.Base64;
import org.apache.ibatis.session.SqlSession;

public class LoginWithSessionKeyServerTask extends AbstractServerTask {

	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager,
			SinnoriSqlSessionFactoryIF sqlSessionFactory,
			LetterSender letterSender, AbstractMessage messageFromClient)
			throws Exception {
		LoginWithSessionKey inObj = (LoginWithSessionKey)messageFromClient;
		
		// FIXME!
		log.info(inObj.toString());
		
		String idCipherBase64 = inObj.getIdCipherBase64();
		String pwdCipherBase64 = inObj.getPwdCipherBase64();
		String sessionKeyBase64 = inObj.getSessionKeyBase64();
		String ivBase64 = inObj.getIvBase64();
		
		MessageResult outObj = new MessageResult();
		outObj.setTaskResult("N");
		outObj.setTaskMessageID(inObj.getMessageID());
		
		SymmetricKey symmetricKey = null;
		try {
			symmetricKey = ServerSessionKeyManager.getInstance()
					.getSymmetricKey(sessionKeyBase64, ivBase64);
		} catch (IllegalArgumentException e) {
			outObj.setResultMessage(""+e.getMessage());
			
			
			letterSender.addSyncMessage(outObj);
			return;
		} catch (SymmetricException e) {
			outObj.setResultMessage(""+e.getMessage());
			letterSender.addSyncMessage(outObj);
			return;
		}
		
		String userId = null;
		String password = null;
		
		try {
			userId = symmetricKey.decryptStringBase64(idCipherBase64);
		} catch (IllegalArgumentException e) {
			outObj.setResultMessage("id::IllegalArgumentException");

			letterSender.addSyncMessage(outObj);
			return;
		} catch (SymmetricException e) {
			outObj.setResultMessage("id::SymmetricException");			
			letterSender.addSyncMessage(outObj);
			return;
		}

		try {
			password = symmetricKey.decryptStringBase64(pwdCipherBase64);
		} catch (IllegalArgumentException e) {
			outObj.setResultMessage("password::IllegalArgumentException");
			letterSender.addSyncMessage(outObj);
			return;
		} catch (SymmetricException e) {
			outObj.setResultMessage("password::SymmetricException");
			letterSender.addSyncMessage(outObj);
			return;
		}
		
		log.info(String.format("userId=[%s], password=[%s]", userId, password));
		
		/*String regexID = "^[A-Za-z][A-Za-z0-9]{3,14}$";
		if (!id.matches(regexID)) {
			outObj.setResultMessage("아이디["+id+"]는 첫 문자가 영문자 그리고 영문과 숫자로 구성되며 최소 4자, 최대 15자로 구성됩니다.");	
			letterSender.addSyncMessage(outObj);
			return;
		}*/
		
		/*
		 * var regexp_pwd = /^[A-Za-z0-9\`~!@\#$%<>\^&*\(\)\-=+_\'\[\]\{\}\\\|\:\;\"<>\?,\.\/]{8,15}$/;
		var regexp_pwd_alphabet = /[A-Za-z]{1,}/;
		var regexp_pwd_digit = /[0-9]{1,}/;
		var regexp_pwd_special = /[\`~!@\#$%<>\^&*\(\)\-=+_\'\[\]\{\}\\\|\:\;\"<>\?,\.\/]{1,}/;
		 */
		//String regexPwd = "^[A-Za-z0-9`~!@#$%<>^&*\\(\\)-=+_'\\[\\]\\{\\}|:;\"<>?,./]{8,15}$";
		
		SqlSession session = sqlSessionFactory.openSession();
		try {
			HashMap<String, Object> memberHash = session.selectOne("getMemberByIDInLock", userId);
			if (null == memberHash) {
				outObj.setResultMessage("아이디["+userId+"] 가 존재하지 않습니다.");				
			} else {			
				int memberGubun = (Integer)memberHash.get("memberGubun");
				int memberState = (Integer)memberHash.get("memberState");
				int pwdFailCount = (Integer)memberHash.get("pwdFailCount");
				
				if (5 <= pwdFailCount) {
					outObj.setResultMessage("비밀번호 5회 이상 틀렸습니다.");
				} else {
					String passwordBase64 = (String)memberHash.get("pwdBase64");
					String pwdSaltBase64 = (String)memberHash.get("pwdSaltBase64");
					
					
					if (2 == memberGubun) {
						outObj.setResultMessage("블락된 회원["+userId+"] 입니다.");
					} else if (0 != memberGubun && 1 != memberGubun) {
						outObj.setResultMessage("알수없는 회원 구분["+memberGubun+"] 을 가진 회원["+userId+"] 입니다.");
					} else {						
						if (1 == memberState) {
							outObj.setResultMessage("탈퇴한 회원["+userId+"] 입니다.");					
						} else if (0 != memberState) {
							outObj.setResultMessage("알수없는 상태["+memberState+"] 을 가진 회원["+userId+"] 입니다.");
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
							
							byte passwordMDByteArray[] =  md.digest();
							String userPasswordMDBase64 = Base64.encodeBase64String(passwordMDByteArray);
							
							if (!passwordBase64.equals(userPasswordMDBase64)) {
								memberHash.put("pwdFailCount", pwdFailCount+1);
								session.update("updatePwdFailCnt", memberHash);
								session.commit();
								
								outObj.setResultMessage("비밀 번호가 다릅니다.");
							} else {
								if (pwdFailCount > 0) {
									memberHash.put("pwdFailCount", 0);								
									session.update("updatePwdFailCnt", memberHash);
									session.commit();
								}

								outObj.setTaskResult("Y");
								outObj.setResultMessage("로그인 성공하셨습니다.");
							}
						}
					}
				}
			}
			
			
		} catch(Exception e) {
			e.printStackTrace();
			//session.rollback();
			
			outObj.setTaskResult("N");
			outObj.setResultMessage("2.로그인이 실패했습니다.");
		} finally {
			session.close();
		}
		
		letterSender.addSyncMessage(outObj);
		
	}

}
