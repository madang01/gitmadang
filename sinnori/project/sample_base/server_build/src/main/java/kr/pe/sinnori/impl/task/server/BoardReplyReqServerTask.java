package kr.pe.sinnori.impl.task.server;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.BoardReplyReq.BoardReplyReq;
import kr.pe.sinnori.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.lib.ValueChecker;
import kr.pe.sinnori.server.task.AbstractServerTask;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class BoardReplyReqServerTask extends AbstractServerTask {	
	@Override
	public void doTask(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		
		doWork(projectName, personalLoginManager, toLetterCarrier, (BoardReplyReq)inputMessage);
	}
	
	public void doWork(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			BoardReplyReq boardReplyReq) throws Exception {
		// FIXME!
		log.info(boardReplyReq.toString());		
		
		
		
				
		
		MessageResultRes messageResultOutObj = new MessageResultRes();
		messageResultOutObj.setIsSuccess(false);
		messageResultOutObj.setTaskMessageID(boardReplyReq.getMessageID());
		
		/*try {
			ValueChecker.checkValidBoardId(boardReplyReq.getBoardId());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			messageResultOutObj.setResultMessage(e.getMessage());
		}
		
		try {
			ValueChecker.checkValidParentBoardNo(boardReplyReq.getParentBoardNo());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			messageResultOutObj.setResultMessage(e.getMessage());
		}*/
		/**
		 * 게시판 식별자, 게시판 부모 번호 모두 unsigned integer 이므로 조건 검사 필요 없다.
		 */
		try {
			ValueChecker.checkValidSubject(boardReplyReq.getSubject());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			messageResultOutObj.setResultMessage(e.getMessage());
			toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
			return;
		}
		
		try {
			ValueChecker.checkValidContent(boardReplyReq.getContent());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			messageResultOutObj.setResultMessage(e.getMessage());
			toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
			return;
		}
		
		try {
			ValueChecker.checkValidUserId(boardReplyReq.getUserId());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			messageResultOutObj.setResultMessage(e.getMessage());
			toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
			return;
		}
		
		
		try {
			ValueChecker.checkValidIP(boardReplyReq.getIp());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			messageResultOutObj.setResultMessage(e.getMessage());
			toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
			return;
		}
		
		/*
		SqlSessionFactory sqlSessionFactory = MybatisSqlSessionFactoryManger.getInstance()
				.getSqlSessionFactory(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);
		SqlSession session = sqlSessionFactory.openSession(false);	
		try {
			*//** 부모 게시글의 계층형 게시판 정보를 바탕으로 댓글 게시판 트리 정보를 가져온다 *//*
			BoardReplyDTO boardReplyDTO = session.selectOne("getBoardTreeInfoFromParent", boardReplyReq);
			if (null == boardReplyDTO) {
				session.rollback();
				String errorMessage = String.format("게시판[%d]에서 부모 글[%d]이 존재하지 않습니다.", 
						boardReplyReq.getBoardId(), boardReplyReq.getParentBoardNo());
				log.warn("{}, boardReplyReq={}", errorMessage, boardReplyReq.toString());
				
				messageResultOutObj.setResultMessage(errorMessage);
			} else {
				// FIXME!
				//log.info("boardReplyDTO={}", boardReplyDTO.toString());
				
				if (boardReplyReq.getBoardId() != boardReplyDTO.getBoardId()) {
					session.commit();
					String errorMessage = String.format("부모글의 게시판 식별자[%d]와 파라미터로 넘어온 게시판 식별자[%d]가 상이합니다.", 
							boardReplyDTO.getBoardId(), boardReplyReq.getBoardId());
					log.warn("{}, boardReplyReq={}", errorMessage, boardReplyReq.toString());
					
					messageResultOutObj.setResultMessage(errorMessage);
					
				} else {
					*//** 입력 받은 댓글 내용으로 채움 *//*
					boardReplyDTO.setSubject(boardReplyReq.getSubject());
					boardReplyDTO.setContent(boardReplyReq.getContent());
					boardReplyDTO.setWriterId(boardReplyReq.getUserId());
					boardReplyDTO.setAttachId(boardReplyReq.getAttachId());
					boardReplyDTO.setIp(boardReplyReq.getIp());				
					
					List<HashMap<String, Object>> replyGroupHashList = session.selectList("getBoardListByGroupAndSeqInLock", boardReplyDTO);
					if (null == replyGroupHashList) {
						session.rollback();
						String errorMessage = String.format("게시판[%d]에서 게시글 그룹[%d]내 삽입할 위치[%d] 이후 락 획득 실패", 
								boardReplyDTO.getBoardId(), boardReplyDTO.getGroupNo(), boardReplyDTO.getGroupSeq());
						log.warn("{}, boardReplyReq={}", errorMessage, boardReplyReq.toString());
						
						messageResultOutObj.setResultMessage(errorMessage);		
						
					} else {
						@SuppressWarnings("unused")
						int resultOfUpdate = session.update("updateReplyBoard", boardReplyDTO);
						
						// FIXME!
						// log.info("resultOfUpdate={}", resultOfUpdate);
						
						int resultOfInsert = session.insert("insertReplyBoard", boardReplyDTO);
						if (resultOfInsert > 0) {
							session.commit();
							messageResultOutObj.setIsSuccess(true);
							messageResultOutObj.setResultMessage("게시판 댓글 등록이 성공하였습니다.");
						} else {
							session.rollback();
							
							String errorMessage = "1.게시판 댓글 등록이 실패하였습니다.";
							log.warn("{}, boardReplyReq={}", errorMessage, boardReplyReq.toString());
							
							messageResultOutObj.setResultMessage("1.게시판 댓글 등록이 실패하였습니다.");
						}
					}	
				}			
			}			
			
		} catch(Exception e) {
			session.rollback();
			log.warn("unknown error", e);			
			
			messageResultOutObj.setResultMessage("2.게시판 댓글 등록이 실패하였습니다.");
			toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
			return;
		} finally {
			session.close();
		}
		
		//log.info("입력 메시지[{}] 게시판 최상의 글 등록 성공여부[{}]", boardReplyReq.toString(), messageResultOutObj.getIsSuccess());
		
		//log.info("messageResultOutObj[{}]", messageResultOutObj.toString());
		
		toLetterCarrier.addSyncOutputMessage(messageResultOutObj);*/
	}


}
