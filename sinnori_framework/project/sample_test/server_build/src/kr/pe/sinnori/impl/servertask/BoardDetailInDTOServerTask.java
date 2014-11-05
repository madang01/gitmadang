package kr.pe.sinnori.impl.servertask;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.serverlib.ValueChecker;
import kr.pe.sinnori.impl.message.BoardDetailInDTO.BoardDetailInDTO;
import kr.pe.sinnori.impl.message.BoardDetailOutDTO.BoardDetailOutDTO;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.mybatis.SqlSessionFactoryManger;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class BoardDetailInDTOServerTask extends AbstractServerTask {
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
		
		BoardDetailInDTO inObj = (BoardDetailInDTO)messageFromClient;
		
		try {
			ValueChecker.checkValidWriterId(inObj.getWriterId());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			
			MessageResult messageResultOutObj = new MessageResult();
			messageResultOutObj.setIsSuccess(false);
			messageResultOutObj.setTaskMessageID(inObj.getMessageID());
			messageResultOutObj.setResultMessage(e.getMessage());
			letterSender.addSyncMessage(messageResultOutObj);
			return;
		}
		
		
		try {
			ValueChecker.checkValidIP(inObj.getIp());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			
			MessageResult messageResultOutObj = new MessageResult();
			messageResultOutObj.setIsSuccess(false);
			messageResultOutObj.setTaskMessageID(inObj.getMessageID());
			messageResultOutObj.setResultMessage(e.getMessage());
			letterSender.addSyncMessage(messageResultOutObj);
			return;
		}
		
		BoardDetailOutDTO outObj = null;
		
		SqlSession session = sqlSessionFactory.openSession(false);
		try {			
			outObj = session.selectOne("getBoardDetail", inObj);
			
			if (null == outObj) {				
				session.commit();
				
				String errorMessage = String.format("게시판 상세 조회 얻기 실패, inObj=[%s]", inObj.toString());
				log.warn(errorMessage);
				
				MessageResult messageResultOutObj = new MessageResult();
				messageResultOutObj.setIsSuccess(false);
				messageResultOutObj.setTaskMessageID(inObj.getMessageID());
				messageResultOutObj.setResultMessage("게시판 상세 조회 얻기 실패");
				
				letterSender.addSyncMessage(messageResultOutObj);
			} else {
				if (outObj.getBoardId() != inObj.getBoardId()) {
					session.commit();
					
					String errorMessage = String.format("게시판 상세 조회 결과로 얻은 게시판 식별자와 입력으로 받은 게시판 식별자가 상이합니다. inObj=[%s]", 
							inObj.toString());
					log.warn(errorMessage);
					
					MessageResult messageResultOutObj = new MessageResult();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage("게시판 상세 조회 결과로 얻은 게시판 식별자와 입력으로 받은 게시판 식별자가 상이합니다.");
					
					letterSender.addSyncMessage(messageResultOutObj);
				} else {
					int resultOfUpdate = session.update("kr.pr.sinnori.testweb.updateBoardDetail", inObj);
					if (resultOfUpdate > 0) {						
						session.commit();
						
						letterSender.addSyncMessage(outObj);
					} else {
						session.rollback();
						
						String errorMessage = String.format("게시판 상세 조회후 조회수 증가 실패, inObj=[%s]", inObj.toString());
						log.warn(errorMessage);
						
						MessageResult messageResultOutObj = new MessageResult();
						messageResultOutObj.setIsSuccess(false);
						messageResultOutObj.setTaskMessageID(inObj.getMessageID());
						messageResultOutObj.setResultMessage("게시판 상세 조회후 조회수 증가 실패");
						
						letterSender.addSyncMessage(messageResultOutObj);
					}
				}				
			}			
		} catch(Exception e) {
			session.rollback();
			log.warn("unknown error", e);
			
			MessageResult messageResultOutObj = new MessageResult();
			messageResultOutObj.setTaskMessageID(inObj.getMessageID());
			messageResultOutObj.setIsSuccess(false);
			messageResultOutObj.setResultMessage("알 수 없는 이유로 게시판 조회가 실패하였습니다.");
			letterSender.addSyncMessage(messageResultOutObj);
			return;
		} finally {
			session.close();
		}		
	}
}
