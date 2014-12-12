package kr.pe.sinnori.impl.servertask;

import java.util.HashMap;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.serverlib.ValueChecker;
import kr.pe.sinnori.impl.message.BoardVoteInDTO.BoardVoteInDTO;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.server.mybatis.SqlSessionFactoryManger;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class BoardVoteInDTOServerTask extends AbstractServerTask {
	private SqlSessionFactory sqlSessionFactory = null;

	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager, LetterSender letterSender,
			AbstractMessage messageFromClient) throws Exception {
		// FIXME!
		log.info(messageFromClient.toString());		
		
		if (null == sqlSessionFactory) {
			sqlSessionFactory = SqlSessionFactoryManger.getInstance()
					.getSqlSessionFactory("tw_sinnoridb");
		}
		
		BoardVoteInDTO inObj = (BoardVoteInDTO)messageFromClient;
		
		MessageResult messageResultOutObj = new MessageResult();
		messageResultOutObj.setIsSuccess(false);
		messageResultOutObj.setTaskMessageID(inObj.getMessageID());
		
		try {
			ValueChecker.checkValidUserId(inObj.getUserId());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			messageResultOutObj.setResultMessage(e.getMessage());
			letterSender.addSyncMessage(messageResultOutObj);
			return;
		}
		
		
		try {
			ValueChecker.checkValidIP(inObj.getIp());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			messageResultOutObj.setResultMessage(e.getMessage());
			letterSender.addSyncMessage(messageResultOutObj);
			return;
		}
		
		SqlSession session = sqlSessionFactory.openSession(false);	
		try {		
			String writerId = session.selectOne("getBoardWriter", inObj);
			if (null == writerId) {
				session.rollback();
				String errorMessage = String.format("게시판[%d]에서 글[%d]이 존재하지 않습니다.", 
						inObj.getBoardId(), inObj.getBoardNo());
				messageResultOutObj.setResultMessage(errorMessage);
			} else {
				// FIXME!
				//log.info("writerId={}", writerId);	
					
				if (inObj.getUserId().equals(writerId)) {
					session.commit();
					String errorMessage = String.format("본인[%s] 스스로 본인 글을 추천을 할 수 없습니다.", 	writerId);
					messageResultOutObj.setResultMessage(errorMessage);
				} else {
					HashMap<String, Object> boardVoteHash = session.selectOne("getBoardVote", inObj);
					if (null == boardVoteHash) {
						int resultOfInsert = session.insert("insertBoardVote", inObj);						
						if (resultOfInsert > 0) {
							session.commit();
							messageResultOutObj.setIsSuccess(true);
							
							messageResultOutObj.setResultMessage(new StringBuilder("게시글[")
							.append(inObj.getBoardId())
							.append(":")
							.append(inObj.getBoardNo()).append("]이 추천 되었습니다.").toString());
						} else {
							session.rollback();
							messageResultOutObj.setResultMessage(new StringBuilder("1.게시글[").append(inObj.getBoardNo()).append("] 추천이 실패하였습니다.").toString());
						}	
					} else {
						session.commit();
						log.warn("중복 추천을 할 수 없습니다. inObj={}", inObj.toString());
						String errorMessage = "중복 추천을 할 수 없습니다.";
						messageResultOutObj.setResultMessage(errorMessage);
					}				
				}	
			}			
			
		} catch(Exception e) {
			session.rollback();
			log.warn("unknown error", e);			
			
			messageResultOutObj.setResultMessage(new StringBuilder("2.게시글[").append(inObj.getBoardNo()).append("] 추천이 실패하였습니다.").toString());
			letterSender.addSyncMessage(messageResultOutObj);
			return;
		} finally {
			session.close();
		}
		
		letterSender.addSyncMessage(messageResultOutObj);
		return;
		
	}
}
