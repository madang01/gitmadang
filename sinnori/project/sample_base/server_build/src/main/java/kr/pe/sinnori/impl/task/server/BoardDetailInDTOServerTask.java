package kr.pe.sinnori.impl.task.server;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.BoardDetailInDTO.BoardDetailInDTO;
import kr.pe.sinnori.impl.message.BoardDetailOutDTO.BoardDetailOutDTO;
import kr.pe.sinnori.impl.message.BoardDetailOutDTO.BoardDetailOutDTO.AttachFile;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.mybatis.MybatisSqlSessionFactoryManger;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.lib.ServerCommonStaticFinalVars;
import kr.pe.sinnori.server.task.AbstractServerTask;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class BoardDetailInDTOServerTask extends AbstractServerTask {
	// private SqlSessionFactory sqlSessionFactory = null;
	
	@Override
	public void doTask(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		// FIXME!
		log.info(inputMessage.toString());
				
		doWork(projectName, toLetterCarrier, (BoardDetailInDTO)inputMessage);
	}
	private void doWork(String projectName,
			ToLetterCarrier toLetterCarrier, BoardDetailInDTO boardDetailInObj)
			throws Exception {		
		SqlSessionFactory sqlSessionFactory = MybatisSqlSessionFactoryManger.getInstance()
				.getSqlSessionFactory(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		BoardDetailOutDTO boardDetailOutObj = null;
		
		SqlSession session = sqlSessionFactory.openSession(false);
		try {			
			boardDetailOutObj = session.selectOne("kr.pr.sinnori.testweb.getBoardDetail", boardDetailInObj);
			
			if (null == boardDetailOutObj) {				
				session.commit();
				
				String errorMessage = String.format("게시판 상세 조회 얻기 실패, boardDetailInObj=[%s]", boardDetailInObj.toString());
				log.warn(errorMessage);
				
				MessageResult messageResultOutObj = new MessageResult();
				messageResultOutObj.setIsSuccess(false);
				messageResultOutObj.setTaskMessageID(boardDetailInObj.getMessageID());
				messageResultOutObj.setResultMessage("게시판 상세 조회 얻기 실패");
				
				
				toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
			} else {
				java.util.List<AttachFile> attachFileList = boardDetailOutObj.getAttachFileList();
				if (null != attachFileList) {
					boardDetailOutObj.setAttachFileCnt(attachFileList.size());
				} else {
					boardDetailOutObj.setAttachFileCnt(0);
				}
				
				if (boardDetailOutObj.getBoardId() != boardDetailInObj.getBoardId()) {
					session.commit();
					
					String errorMessage = "게시판 상세 조회 결과로 얻은 게시판 식별자와 입력으로 받은 게시판 식별자가 상이합니다.";
					log.warn("{}, boardDetailInObj=", errorMessage, boardDetailInObj.toString());
					
					MessageResult messageResultOutObj = new MessageResult();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(boardDetailInObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
				} else {
					
					int resultOfUpdate = session.update("kr.pr.sinnori.testweb.updateBoardViewCnt", boardDetailInObj);
					if (resultOfUpdate > 0) {						
						session.commit();					
						
						/** 조회수 증가 성공후 조회수 보정 */
						boardDetailOutObj.setViewCount(boardDetailOutObj.getViewCount()+1);
						
						toLetterCarrier.addSyncOutputMessage(boardDetailOutObj);
					} else {
						session.rollback();
						
						String errorMessage = "게시판 상세 조회후 조회수 증가 실패";
						log.warn("{}, boardDetailInObj=", errorMessage, boardDetailInObj.toString());
						
						MessageResult messageResultOutObj = new MessageResult();
						messageResultOutObj.setIsSuccess(false);
						messageResultOutObj.setTaskMessageID(boardDetailInObj.getMessageID());
						messageResultOutObj.setResultMessage(errorMessage);
						
						toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					}
				}				
			}			
		} catch(Exception e) {
			session.rollback();
			log.warn("unknown error", e);
			
			MessageResult messageResultOutObj = new MessageResult();
			messageResultOutObj.setTaskMessageID(boardDetailInObj.getMessageID());
			messageResultOutObj.setIsSuccess(false);
			messageResultOutObj.setResultMessage("알 수 없는 이유로 게시판 조회가 실패하였습니다.");
			toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
			return;
		} finally {
			session.close();
		}		
	}
}
