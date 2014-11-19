package kr.pe.sinnori.impl.servertask;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.message.SeqValueInDTO.SeqValueInDTO;
import kr.pe.sinnori.impl.message.SeqValueOutDTO.SeqValueOutDTO;
import kr.pe.sinnori.impl.mybatis.SqlSessionFactoryManger;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class SeqValueInDTOServerTask extends AbstractServerTask {
	private SqlSessionFactory sqlSessionFactory = null;

	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager, LetterSender letterSender,
			AbstractMessage messageFromClient) throws Exception {
		// FIXME!
		log.info(messageFromClient.toString());
		
		if (null == sqlSessionFactory) {
			sqlSessionFactory = SqlSessionFactoryManger.getInstance().getSqlSessionFactory(serverProjectConfig);
		}
		SeqValueInDTO inObj = (SeqValueInDTO)messageFromClient;
		
		SqlSession session = sqlSessionFactory.openSession(false);
		
		SeqValueOutDTO outObj = null;
		
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
			
			letterSender.addSyncMessage(outObj);
			return;
		}
	}
}
