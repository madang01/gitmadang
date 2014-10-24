package kr.pe.sinnori.impl.message.BoardListInDTO;

import java.util.List;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.BoardListOutDTO.BoardListOutDTO;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.mybatis.SqlSessionFactoryManger;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class BoardListInDTOServerTask extends AbstractServerTask {
	private SqlSessionFactory sqlSessionFactory = null;

	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager,
			LetterSender letterSender, AbstractMessage messageFromClient)
			throws Exception {
		// FIXME!
		log.info(messageFromClient.toString());
		
		
		if (null == sqlSessionFactory) {
			sqlSessionFactory = SqlSessionFactoryManger.getInstance().getSqlSessionFactory(serverProjectConfig);
		}
		
		BoardListInDTO inObj = (BoardListInDTO)messageFromClient;
		// long boardTypeID = inObj.getBoardId();
		// BoardListOutDTO outObj = new BoardListOutDTO();
		
		
		SqlSession session = sqlSessionFactory.openSession(false);		
		// session.commit(false);
		
		// log.info("", session.);
		
		
		// List<HashMap<String, Object>> boardHashList = null;
		try {
			List<BoardListOutDTO.Board> boardList = session.selectList("getBoardList", inObj);
			
			BoardListOutDTO outObj = new BoardListOutDTO();
			outObj.setBoardId(inObj.getBoardId());
			outObj.setStartNo(inObj.getStartNo());
			outObj.setPageSize(inObj.getPageSize());
			outObj.setCnt(boardList.size());
			outObj.setBoardList(boardList);
			// boardHashList = session.selectList("getBoardList", boardTypeID);
			
			int total = session.selectOne("getTotalOfBoard", inObj);
			
			outObj.setTotal(total);
			
			letterSender.addSyncMessage(outObj);
			//session.commit();
		} catch(Exception e) {
			// e.printStackTrace();
			//session.rollback();
			log.warn("unknown error", e);
			
			MessageResult messageResultOutObj = new MessageResult();
			messageResultOutObj.setTaskMessageID(inObj.getMessageID());
			messageResultOutObj.setTaskResult("N");
			messageResultOutObj.setResultMessage("게시판 전체 갯수 가져오기 실패");
			letterSender.addSyncMessage(messageResultOutObj);
			return;
		} finally {
			session.close();
		}
	}
}
