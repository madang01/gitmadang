package kr.pe.sinnori.impl.servertask;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.serverlib.ServerCommonStaticFinalVars;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.message.SeqValueInDTO.SeqValueInDTO;
import kr.pe.sinnori.impl.message.SeqValueOutDTO.SeqValueOutDTO;
import kr.pe.sinnori.impl.server.mybatis.SqlSessionFactoryManger;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class SeqValueInDTOServerTask extends AbstractServerTask {

	@Override
	public void doTask(String projectName,
			LoginManagerIF loginManager, LetterSender letterSender,
			AbstractMessage messageFromClient) throws Exception {
		// FIXME!
		log.info(messageFromClient.toString());
		
		SqlSessionFactory sqlSessionFactory = SqlSessionFactoryManger.getInstance().getSqlSessionFactory(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);
		SeqValueInDTO inObj = (SeqValueInDTO)messageFromClient;
		
		SeqValueOutDTO outObj = null;
		
		SqlSession session = sqlSessionFactory.openSession(false);
		try {
			outObj = session.selectOne("getSeqValueInLock", inObj);
			if (null == outObj) {
				session.rollback();
				
				String errorMessage = "업로드 파일명 시퀀스 조회가 실패하였습니다.";
				
				log.warn("{}, inObj={}", errorMessage, inObj.toString());
				
				MessageResult messageResultOutObj = new MessageResult();
				messageResultOutObj.setIsSuccess(false);
				messageResultOutObj.setTaskMessageID(inObj.getMessageID());
				messageResultOutObj.setResultMessage(errorMessage);
				letterSender.addSyncMessage(messageResultOutObj);
				return;
			} else {
				int cntOfUpdateSeqValue = session.update("updateSeqValue", inObj);
				if (0 == cntOfUpdateSeqValue) {
					session.rollback();
					
					String errorMessage = "업로드 파일명 시퀀스 +1 증가 수정이 실패하였습니다";
					
					log.warn("{}, inObj={}", errorMessage, inObj.toString());
					
					MessageResult messageResultOutObj = new MessageResult();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					letterSender.addSyncMessage(messageResultOutObj);
					return;
				}
				
				session.commit();
				
				letterSender.addSyncMessage(outObj);
				return;
			}
		} catch(Exception e) {
			session.rollback();
			
			String errorMessage = new StringBuilder("알수 없는 이유로 시퀀스 조회가 실패하였습니다. inObj=")
			.append(inObj.toString().toString()).toString();
			log.warn(errorMessage, e);
			
			MessageResult messageResultOutObj = new MessageResult();
			messageResultOutObj.setTaskMessageID(inObj.getMessageID());
			messageResultOutObj.setIsSuccess(false);
			messageResultOutObj.setResultMessage(errorMessage);
			letterSender.addSyncMessage(messageResultOutObj);
			return;
		} finally {
			session.close();
		}
	}
}
