package kr.pe.sinnori.impl.servertask;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.BoardWriteInDTO.BoardWriteInDTO;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.mybatis.SqlSessionFactoryManger;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class BoardWriteInDTOServerTask extends AbstractServerTask {
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
		
		BoardWriteInDTO inObj = (BoardWriteInDTO)messageFromClient;
		
		MessageResult messageResultOutObj = new MessageResult();
		messageResultOutObj.setIsSuccess(false);
		messageResultOutObj.setTaskMessageID(inObj.getMessageID());
				
		SqlSession session = sqlSessionFactory.openSession(false);	
		try {			
			int resultOfInsert = session.insert("insertRootBoard", inObj);
			if (resultOfInsert > 0) {
				int resultOfUpdate = session.update("updateBoardGropNo", inObj);
				if (resultOfUpdate > 0) {
					session.commit();
					messageResultOutObj.setIsSuccess(true);
					messageResultOutObj.setResultMessage("게시판 최상의 글 등록이 성공하였습니다.");
				} else {
					session.rollback();
					messageResultOutObj.setResultMessage("1.게시판 최상의 글 등록이 실패하였습니다.");
				}
			} else {
				session.rollback();
				messageResultOutObj.setResultMessage("2.게시판 최상의 글 등록이 실패하였습니다.");
			}			
		} catch(Exception e) {
			session.rollback();
			log.warn("unknown error", e);			
			
			messageResultOutObj.setResultMessage("3.게시판 최상의 글 등록이 실패하였습니다.");
			letterSender.addSyncMessage(messageResultOutObj);
			return;
		} finally {
			session.close();
		}
			
		log.info("입력 메시지[{}] 게시판 최상의 글 등록 성공여부[{}]", inObj.toString(), messageResultOutObj.getIsSuccess());
		
		letterSender.addSyncMessage(messageResultOutObj);
	}
}
