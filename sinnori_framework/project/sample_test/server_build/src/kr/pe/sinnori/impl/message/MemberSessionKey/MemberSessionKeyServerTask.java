package kr.pe.sinnori.impl.message.MemberSessionKey;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Random;

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


public class MemberSessionKeyServerTask extends AbstractServerTask {

	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager, SinnoriSqlSessionFactoryIF sqlSessionFactory,
			LetterSender letterSender,
			AbstractMessage messageFromClient) throws Exception {
		MemberSessionKey inObj = (MemberSessionKey)messageFromClient;
		String idCipherBase64 = inObj.getIdCipherBase64();
		String sessionKeyBase64 = inObj.getSessionKeyBase64();
		String ivBase64 = inObj.getIvBase64();
		String pwdCipherBase64 = inObj.getPwdCipherBase64();
		String nicknameCipherBase64 = inObj.getNicknameCipherBase64();
		String questionCipherBase64 = inObj.getQuestionCipherBase64();
		String answerCipherBase64 = inObj.getAnswerCipherBase64();
		
		
		
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
		String nickname = null;
		String question = null;
		String answer = null;
		
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

		try {
			nickname = symmetricKey
					.decryptStringBase64(nicknameCipherBase64);
		} catch (IllegalArgumentException e) {
			outObj.setResultMessage("nickname::IllegalArgumentException");
			letterSender.addSyncMessage(outObj);
			return;
		} catch (SymmetricException e) {
			outObj.setResultMessage("nickname::SymmetricException");
			letterSender.addSyncMessage(outObj);
			return;
		}

		try {
			question = symmetricKey
					.decryptStringBase64(questionCipherBase64);
		} catch (IllegalArgumentException e) {
			outObj.setResultMessage("question::IllegalArgumentException");
			letterSender.addSyncMessage(outObj);
			return;
		} catch (SymmetricException e) {
			outObj.setResultMessage("question::SymmetricException");
			letterSender.addSyncMessage(outObj);
			return;
		}

		try {
			answer = symmetricKey.decryptStringBase64(answerCipherBase64);
		} catch (IllegalArgumentException e) {
			outObj.setResultMessage("answer::IllegalArgumentException");
			letterSender.addSyncMessage(outObj);
			return;
		} catch (SymmetricException e) {
			outObj.setResultMessage("answer::SymmetricException");
			letterSender.addSyncMessage(outObj);
			return;
		}

		log.info(String.format("id=[%s], password=[%s], nickname=[%s], question=[%s], answer=[%s]"
				, id, password, nickname, question, answer));
		
		Random random = new Random();
		byte[] pwdSaltByteArray = new byte[8];
		random.nextBytes(pwdSaltByteArray);
		byte[] passwordByteArray = password.getBytes(CommonStaticFinalVars.SINNORI_PASSWORD_CHARSET);
		
		ByteBuffer passwordByteBuffer = ByteBuffer.allocate(pwdSaltByteArray.length+passwordByteArray.length);
		passwordByteBuffer.put(pwdSaltByteArray);
		passwordByteBuffer.put(passwordByteArray);
		
		// FIXME!
		// log.info(HexUtil.getAllHexStringFromByteBuffer(passwordByteBuffer));
		
		MessageDigest md = MessageDigest.getInstance(CommonStaticFinalVars.SINNORI_PASSWORD_ALGORITHM_NAME);
		
		md.update(passwordByteBuffer.array());
		
		byte passwordMDByteArray[] =  md.digest();
		
				
		String passwordMDBase64 = Base64.encodeBase64String(passwordMDByteArray);
		String pwdSaltBase64 = Base64.encodeBase64String(pwdSaltByteArray);
		
		
		HashMap<String, Object> inserMemberHashMap = new HashMap<String, Object>();
		inserMemberHashMap.put("userid", id);
		inserMemberHashMap.put("nickname", nickname);
		inserMemberHashMap.put("password", passwordMDBase64);
		inserMemberHashMap.put("pwd_salt", pwdSaltBase64);
		inserMemberHashMap.put("pwd_question", question);
		inserMemberHashMap.put("pwd_answer", answer);		
		
		SqlSession session = sqlSessionFactory.openSession();
		try {
			HashMap<String, Object> memberByIDHash = session.selectOne("getMemberByID", id);
			if (null != memberByIDHash) {
				// outObj.setTaskResult("N");
				outObj.setResultMessage("기존 회원과 중복되는 아이디["+id+"]로는 회원 가입할 수 없습니다.");
			} else {
				HashMap<String, Object> memberByNickNameHash = session.selectOne("getMemeberByNickname", nickname);
				if (null != memberByNickNameHash) {
					outObj.setResultMessage("기존 회원과 중복되는 별명["+nickname+"]로는 회원 가입할 수 없습니다.");
				} else {
					int resultOfInsert = session.insert("kr.pr.sinnori.testweb.insertMember", inserMemberHashMap);
					if (resultOfInsert > 0) {
						session.commit();
						
						outObj.setTaskResult("Y");
						outObj.setResultMessage("회원 가입이 성공하였습니다.");
					} else {
						// session.rollback();
						
						outObj.setTaskResult("N");
						outObj.setResultMessage("1.회원 가입이 실패하였습니다.");
					}
				}				
			}
		} catch(Exception e) {
			e.printStackTrace();
			
			outObj.setTaskResult("N");
			outObj.setResultMessage("2.회원 가입이 실패하였습니다.");
		} finally {
			session.close();
		}
		
		
		letterSender.addSyncMessage(outObj);
		
	}

}
