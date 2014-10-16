package kr.pe.sinnori.impl.message.BoardListRequest;

import java.util.HashMap;
import java.util.List;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.BoardListResponse.BoardListResponse;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.SinnoriSqlSessionFactoryIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

import org.apache.ibatis.session.SqlSession;

public class BoardListRequestServerTask extends AbstractServerTask {

	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager,
			SinnoriSqlSessionFactoryIF sqlSessionFactory,
			LetterSender letterSender, AbstractMessage messageFromClient)
			throws Exception {
		// FIXME!
		log.info(messageFromClient.toString());
		
		BoardListRequest inObj = (BoardListRequest)messageFromClient;
		long boardTypeID = inObj.getBoardTypeID();
		BoardListResponse outObj = new BoardListResponse();
		
		SqlSession session = sqlSessionFactory.openSession();
		int total;
		List<HashMap<String, Object>> boardHashList = null;
		try {
			total = session.selectOne("getTotalOfBoard", boardTypeID);
			boardHashList = session.selectList("getBoardList", boardTypeID);
		} catch(Exception e) {
			// e.printStackTrace();
			//session.rollback();
			log.warn("unknown error", e);
			
			outObj.setCnt(0);
			letterSender.addSyncMessage(outObj);
			return;
		} finally {
			session.close();
		}
		
		if (null == boardHashList) {
			outObj.setCnt(0);
		} else {
			int cnt = boardHashList.size();
			outObj.setCnt(cnt);			
			BoardListResponse.Board boardList[] = new BoardListResponse.Board[cnt];
			for (int i=0; i < cnt; i++) {
				HashMap<String, Object> boardHash = boardHashList.get(i);
				boardList[i].setBoardNO((Long)boardHash.get("board_no"));
			}
			outObj.setBoardList(boardList);
			
		}
		letterSender.addSyncMessage(outObj);
		return;
	}
}
