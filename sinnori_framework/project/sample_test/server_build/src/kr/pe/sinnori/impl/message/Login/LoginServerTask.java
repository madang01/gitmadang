package kr.pe.sinnori.impl.message.Login;

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

public class LoginServerTask extends AbstractServerTask {

	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager,
			SinnoriSqlSessionFactoryIF sqlSessionFactory,
			LetterSender letterSender, AbstractMessage messageFromClient)
			throws Exception {
		Login inObj = (Login)messageFromClient;
		
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
		
		String id = null;
		String password = null;
		
		try {
			id = symmetricKey.decryptStringBase64(idCipherBase64);
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
		
		log.info(String.format("id=[%s], password=[%s]", id, password));
		
		SqlSession session = sqlSessionFactory.openSession();
		try {
			HashMap<String, Object> memberHash = session.selectOne("getMemberByIDInLock", id);
			if (null == memberHash) {
				outObj.setResultMessage("아이디["+id+"] 가 존재하지 않습니다.");				
			} else {			
				int memberGubun = (Integer)memberHash.get("member_gubun");
				int status = (Integer)memberHash.get("status");
				int pwdFailCnt = (Integer)memberHash.get("pwd_fail_cnt");
				
				if (5 <= pwdFailCnt) {
					outObj.setResultMessage("비밀번호 5회 이상 틀렸습니다.");
				} else {
					String passwordMDBase64 = (String)memberHash.get("password");
					String pwdSaltBase64 = (String)memberHash.get("pwd_salt");
					
					
					if (2 == memberGubun) {
						outObj.setResultMessage("블락된 회원["+id+"] 입니다.");
					} else if (0 != memberGubun && 1 != memberGubun) {
						outObj.setResultMessage("알수없는 회원 구분["+memberGubun+"] 을 가진 회원["+id+"] 입니다.");
					} else {						
						if (1 == status) {
							outObj.setResultMessage("탈퇴한 회원["+id+"] 입니다.");					
						} else if (0 != status) {
							outObj.setResultMessage("알수없는 상태["+status+"] 을 가진 회원["+id+"] 입니다.");
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
							
							if (!passwordMDBase64.equals(userPasswordMDBase64)) {
								memberHash.put("pwd_fail_cnt", pwdFailCnt+1);
								session.update("updatePwdFailCnt", memberHash);
								session.commit();
								
								outObj.setResultMessage("비밀 번호가 다릅니다.");
							} else {
								if (pwdFailCnt > 0) {
									memberHash.put("pwd_fail_cnt", 0);								
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
