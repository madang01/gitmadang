package kr.pe.sinnori.impl.servertask;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Random;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.serverlib.ValueChecker;
import kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.SymmetricKey;
import kr.pe.sinnori.impl.message.MemberRegisterWithSessionKey.MemberRegisterWithSessionKey;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.mybatis.SqlSessionFactoryManger;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

import org.apache.commons.codec.binary.Base64;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;


public class MemberRegisterWithSessionKeyServerTask extends AbstractServerTask {
	private SqlSessionFactory sqlSessionFactory = null;

	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager,
			LetterSender letterSender,
			AbstractMessage messageFromClient) throws Exception {
		MemberRegisterWithSessionKey inObj = (MemberRegisterWithSessionKey)messageFromClient;
		
		if (null == sqlSessionFactory) {
			sqlSessionFactory = SqlSessionFactoryManger.getInstance().getSqlSessionFactory(serverProjectConfig);
		}
		
		String idCipherBase64 = inObj.getIdCipherBase64();
		String sessionKeyBase64 = inObj.getSessionKeyBase64();
		String ivBase64 = inObj.getIvBase64();
		String pwdCipherBase64 = inObj.getPwdCipherBase64();
		String nicknameCipherBase64 = inObj.getNicknameCipherBase64();
		String hintCipherBase64 = inObj.getHintCipherBase64();
		String answerCipherBase64 = inObj.getAnswerCipherBase64();
		
		
		MessageResult messageResultOutObj = new MessageResult();
		messageResultOutObj.setIsSuccess(false);
		messageResultOutObj.setTaskMessageID(inObj.getMessageID());
		
		
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
		String nickname = null;
		String pwdHint = null;
		String pwdAnswer = null;
		
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
			messageResultOutObj.setResultMessage("아이디 복호문 값이 null 값입니다.");

			letterSender.addSyncMessage(messageResultOutObj);
			return;
		}
				
		try {
			ValueChecker.checkValidUserId(userId);
		} catch(RuntimeException e) {
			String errorMessage = e.getMessage();
			log.warn("%s userId=[{}]", errorMessage, userId);			
			messageResultOutObj.setResultMessage(errorMessage);
			
			letterSender.addSyncMessage(messageResultOutObj);
			return;
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
			
			letterSender.addSyncMessage(messageResultOutObj);
			return;
		}

		try {
			nickname = symmetricKey
					.decryptStringBase64(nicknameCipherBase64);
		} catch (IllegalArgumentException e) {
			String errorMessage = String.format("잘못된 파라미터로 인한 별명 복호문 얻기 실패, inObj=[%s]", inObj.toString());
			log.warn(errorMessage, e);
			messageResultOutObj.setResultMessage("잘못된 파라미터로 인한 별명 복호문 얻기 실패");
	
			letterSender.addSyncMessage(messageResultOutObj);
			return;
		} catch (SymmetricException e) {
			String errorMessage = String.format("알수 없는 이유로 별명 복호문 얻기 실패, inObj=[%s]", inObj.toString());
			log.warn(errorMessage, e);
			messageResultOutObj.setResultMessage("알수 없는 이유로 별명 복호문 얻기 실패");

			letterSender.addSyncMessage(messageResultOutObj);
			return;
		}
		
		if (null== nickname) {
			log.warn("별명 복호문 값을 얻는데 실패하였습니다. inObj=[{}]", inObj.toString());			
			messageResultOutObj.setResultMessage("별명 복호문 값을 얻는데 실패하였습니다.");

			letterSender.addSyncMessage(messageResultOutObj);
			return;
		}
		
		try {
			ValueChecker.checkValidNickname(nickname);
		} catch(RuntimeException e) {
			String errorMessage = e.getMessage();
			log.warn("%s userId=[{}]", errorMessage, userId);			
			messageResultOutObj.setResultMessage(errorMessage);
			
			letterSender.addSyncMessage(messageResultOutObj);
			return;
		}

		try {
			pwdHint = symmetricKey
					.decryptStringBase64(hintCipherBase64);
		} catch (IllegalArgumentException e) {
			String errorMessage = String.format("잘못된 파라미터로 인한 비밀번호 분실시 힌트 복호문 얻기 실패, inObj=[%s]", inObj.toString());
			log.warn(errorMessage, e);
			messageResultOutObj.setResultMessage("잘못된 파라미터로 인한 비밀번호 분실시 힌트 복호문 얻기 실패");
	
			letterSender.addSyncMessage(messageResultOutObj);
			return;
		} catch (SymmetricException e) {
			String errorMessage = String.format("알수 없는 이유로 비밀번호 분실시 힌트 복호문 얻기 실패, inObj=[%s]", inObj.toString());
			log.warn(errorMessage, e);
			messageResultOutObj.setResultMessage("알수 없는 이유로 비밀번호 분실시 힌트 복호문 얻기 실패");

			letterSender.addSyncMessage(messageResultOutObj);
			return;
		}
		
		if (null== pwdHint) {
			log.warn("비밀번호 분실시 힌트 복호문 값을 얻는데 실패하였습니다. inObj=[{}]", inObj.toString());			
			messageResultOutObj.setResultMessage("비밀번호 분실시 힌트 복호문 값을 얻는데 실패하였습니다.");

			letterSender.addSyncMessage(messageResultOutObj);
			return;
		}
		
		pwdHint = pwdHint.trim();
		
		try {
			ValueChecker.checkValidPwdHint(pwdHint);
		} catch(RuntimeException e) {
			String errorMessage = e.getMessage();
			log.warn("%s userId=[{}]", errorMessage, userId);			
			messageResultOutObj.setResultMessage(errorMessage);
			
			letterSender.addSyncMessage(messageResultOutObj);
			return;
		}

		try {
			pwdAnswer = symmetricKey.decryptStringBase64(answerCipherBase64);
		} catch (IllegalArgumentException e) {
			String errorMessage = String.format("잘못된 파라미터로 인한 비밀번호 분실시 답변 복호문 얻기 실패, inObj=[%s]", inObj.toString());
			log.warn(errorMessage, e);
			messageResultOutObj.setResultMessage("잘못된 파라미터로 인한 비밀번호 분실시 답변 복호문 얻기 실패");

			letterSender.addSyncMessage(messageResultOutObj);
			return;
		} catch (SymmetricException e) {
			String errorMessage = String.format("알수 없는 이유로 비밀번호 분실시 답변 복호문 얻기 실패, inObj=[%s]", inObj.toString());
			log.warn(errorMessage, e);
			messageResultOutObj.setResultMessage("알수 없는 이유로 비밀번호 분실시 답변 복호문 얻기 실패");

			letterSender.addSyncMessage(messageResultOutObj);
			return;
		}
		
		if (null== pwdAnswer) {
			log.warn("비밀번호 분실시 답변 복호문 값을 얻는데 실패하였습니다. inObj=[{}]", inObj.toString());			
			messageResultOutObj.setResultMessage("비밀번호 분실시 답변 복호문 값을 얻는데 실패하였습니다.");

			letterSender.addSyncMessage(messageResultOutObj);
			return;
		}

		pwdAnswer = pwdAnswer.trim();
		try {
			ValueChecker.checkValidPwdAnswer(pwdAnswer);
		} catch(RuntimeException e) {
			String errorMessage = e.getMessage();
			log.warn("%s userId=[{}]", errorMessage, userId);			
			messageResultOutObj.setResultMessage(errorMessage);
			
			letterSender.addSyncMessage(messageResultOutObj);
			return;
		}

		/**
		 * 비밀번호외 항목들은 철저히 보안을 유지 해야 하므로 찍지 않음.
		 * 필요시 특정 아이디에 한에서만 찍도록 해야함.
		 */
		log.info("회원가입 아이디[{}] 처리전", userId);
		
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
		
				
		String pwdBase64 = Base64.encodeBase64String(passwordMDByteArray);
		String pwdSaltBase64 = Base64.encodeBase64String(pwdSaltByteArray);
		
		
		HashMap<String, Object> inserMemberHashMap = new HashMap<String, Object>();
		inserMemberHashMap.put("userId", userId);
		inserMemberHashMap.put("nickname", nickname);
		inserMemberHashMap.put("pwdBase64", pwdBase64);
		inserMemberHashMap.put("pwdSaltBase64", pwdSaltBase64);
		inserMemberHashMap.put("pwdHint", pwdHint);
		inserMemberHashMap.put("pwdAnswer", pwdAnswer);		
		
		SqlSession session = sqlSessionFactory.openSession(false);
		try {
			HashMap<String, Object> memberByIDHash = session.selectOne("getMemberByID", userId);
			if (null != memberByIDHash) {
				// outObj.setTaskResult("N");
				messageResultOutObj.setResultMessage("기존 회원과 중복되는 아이디["+userId+"]로는 회원 가입할 수 없습니다.");
			} else {
				HashMap<String, Object> memberByNickNameHash = session.selectOne("getMemeberByNickname", nickname);
				if (null != memberByNickNameHash) {
					messageResultOutObj.setResultMessage("기존 회원과 중복되는 별명["+nickname+"]로는 회원 가입할 수 없습니다.");
				} else {
					int resultOfInsert = session.insert("kr.pr.sinnori.testweb.insertMember", inserMemberHashMap);
					if (resultOfInsert > 0) {						
						messageResultOutObj.setIsSuccess(true);
						messageResultOutObj.setResultMessage("회원 가입이 성공하였습니다.");
					} else {
						messageResultOutObj.setResultMessage("1.회원 가입이 실패하였습니다.");
					}
				}				
			}
			
			session.commit();
		} catch(Exception e) {
			session.rollback();
			log.warn("회원 가입시 알 수 없는 에러 발생", e);			
			messageResultOutObj.setResultMessage("2.회원 가입이 실패하였습니다.");
			letterSender.addSyncMessage(messageResultOutObj);
			return;
		} finally {
			session.close();
		}		
		
		letterSender.addSyncMessage(messageResultOutObj);		
	}
}
