package kr.pe.sinnori.impl.servertask;

import java.util.HashMap;
import java.util.List;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.BoardReplyDTO.BoardReplyDTO;
import kr.pe.sinnori.impl.message.BoardReplyRequest.BoardReplyRequest;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.mybatis.SqlSessionFactoryManger;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class BoardReplyRequestServerTask extends AbstractServerTask {
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
		
		BoardReplyRequest inObj = (BoardReplyRequest)messageFromClient;
		
		MessageResult messageResultOutObj = new MessageResult();
		messageResultOutObj.setIsSuccess(false);
		messageResultOutObj.setTaskMessageID(inObj.getMessageID());
		
		SqlSession session = sqlSessionFactory.openSession(false);	
		try {		
			BoardReplyDTO boardReplyDTO = session.selectOne("getBoardReplyInLock", inObj);
			if (null == boardReplyDTO) {
				session.rollback();
				String errorMessage = String.format("게시판[%d]에서 부모 글[%d]이 존재하지 않습니다.", 
						inObj.getBoardId(), inObj.getParentBoardNo());
				messageResultOutObj.setResultMessage(errorMessage);
			} else {
				boardReplyDTO.setSubject(inObj.getSubject());
				boardReplyDTO.setContent(inObj.getContent());
				boardReplyDTO.setWriterId(inObj.getWriterId());
				boardReplyDTO.setIp(inObj.getIp());
				
				// FIXME!
				log.info("boardReplyDTO={}", boardReplyDTO.toString());
				
				List<HashMap<String, Object>> replyGroupHashList = session.selectList("getBoardListByGroupAndSeqInLock", boardReplyDTO);
				if (null == replyGroupHashList) {
					session.rollback();
					String errorMessage = String.format("게시판[%d]에서 게시글 그룹[%d]내 삽입할 위치[%d] 이후 락 획득 실패", 
							boardReplyDTO.getBoardId(), boardReplyDTO.getGroupNo(), boardReplyDTO.getGroupSeq());
					messageResultOutObj.setResultMessage(errorMessage);
				} else {
					int resultOfUpdate = session.update("updateReplyBoard", boardReplyDTO);
					
					// FIXME!
					log.info("resultOfUpdate={}", resultOfUpdate);
					
					int resultOfInsert = session.insert("insertReplyBoard", boardReplyDTO);
					if (resultOfInsert > 0) {
						session.commit();
						messageResultOutObj.setIsSuccess(true);
						messageResultOutObj.setResultMessage("게시판 댓글 등록이 성공하였습니다.");
					} else {
						session.rollback();
						messageResultOutObj.setResultMessage("1.게시판 댓글 등록이 실패하였습니다.");
					}
				}				
			}			
			
		} catch(Exception e) {
			session.rollback();
			log.warn("unknown error", e);			
			
			messageResultOutObj.setResultMessage("2.게시판 댓글 등록이 실패하였습니다.");
			letterSender.addSyncMessage(messageResultOutObj);
			return;
		} finally {
			session.close();
		}
		
		log.info("입력 메시지[{}] 게시판 최상의 글 등록 성공여부[{}]", inObj.toString(), messageResultOutObj.getIsSuccess());
		
		log.info("messageResultOutObj[{}]", messageResultOutObj.toString());
		
		letterSender.addSyncMessage(messageResultOutObj);
	}


}
