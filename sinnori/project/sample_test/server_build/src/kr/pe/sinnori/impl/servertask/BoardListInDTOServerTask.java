package kr.pe.sinnori.impl.servertask;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.BoardListInDTO.BoardListInDTO;
import kr.pe.sinnori.impl.message.BoardListOutDTO.BoardListOutDTO;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.server.mybatis.SqlSessionFactoryManger;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class BoardListInDTOServerTask extends AbstractServerTask {
	

	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager,
			LetterSender letterSender, AbstractMessage messageFromClient)
			throws Exception {
		// FIXME!
		log.info(messageFromClient.toString());		
		
		SqlSessionFactory sqlSessionFactory = SqlSessionFactoryManger.getInstance().getSqlSessionFactory("tw_sinnoridb");
		
		BoardListInDTO inObj = (BoardListInDTO)messageFromClient;
		
		/*long boardID = inObj.getBoardId();
		
		if (boardID <=0) {
			String errorMessage = new StringBuilder("게시판 식별자(boardId) 값[")
			.append(boardID).append("]은 0 보다 커야합니다.").toString();
			MessageResult messageResultOutObj = new MessageResult();
			messageResultOutObj.setTaskMessageID(inObj.getMessageID());
			messageResultOutObj.setIsSuccess(false);
			messageResultOutObj.setResultMessage(errorMessage);
			letterSender.addSyncMessage(messageResultOutObj);
			return;
		}		*/
		
		SqlSession session = sqlSessionFactory.openSession(false);		
		// session.commit(false);
		
		// log.info("", session.);
		
		BoardListOutDTO outObj = null;
		
		try {			
			outObj = session.selectOne("getBoardListMap", inObj);
			session.commit();
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
		
		java.util.List<BoardListOutDTO.Board> boardList = outObj.getBoardList();
		
		if (null == boardList) {
			outObj.setCnt(0);				
		} else {
			outObj.setCnt(boardList.size());
		}
		
		//  log.info(outObj.toString());
		
		letterSender.addSyncMessage(outObj);
	}
}
