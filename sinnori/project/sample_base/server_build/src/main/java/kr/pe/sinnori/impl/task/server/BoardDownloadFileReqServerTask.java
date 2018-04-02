package kr.pe.sinnori.impl.task.server;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.BoardDownloadFileReq.BoardDownloadFileReq;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.task.AbstractServerTask;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class BoardDownloadFileReqServerTask extends AbstractServerTask {
	// private SqlSessionFactory sqlSessionFactory = null;

	@Override
	public void doTask(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		// FIXME!
		log.info(inputMessage.toString());
		doWork(projectName, toLetterCarrier, (BoardDownloadFileReq)inputMessage);
	}
	
	private void doWork(String projectName,
			ToLetterCarrier toLetterCarrier, BoardDownloadFileReq boardDownloadFileInObj)
			throws Exception {

		/*if (null == sqlSessionFactory) {
			sqlSessionFactory = SqlSessionFactoryManger.getInstance()
					.getSqlSessionFactory("tw_sinnoridb");
		}*/
		/*SqlSessionFactory sqlSessionFactory = MybatisSqlSessionFactoryManger.getInstance()
				.getSqlSessionFactory(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		BoardDownloadFileRes boardDownloadFileOutObj = null;

		SqlSession session = sqlSessionFactory.openSession(false);
		try {
			boardDownloadFileOutObj = session.selectOne("getDownloadFileInfo", boardDownloadFileInObj);

			if (null == boardDownloadFileOutObj) {
				session.commit();

				String errorMessage = "다운로드 파일 정보 얻기 실패";
				log.warn("{}, inObj={}", errorMessage, boardDownloadFileInObj.toString());

				MessageResultRes messageResultOutObj = new MessageResultRes();
				messageResultOutObj.setIsSuccess(false);
				messageResultOutObj.setTaskMessageID(boardDownloadFileInObj.getMessageID());
				messageResultOutObj.setResultMessage(errorMessage);

				toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
			} else {
				session.commit();
				toLetterCarrier.addSyncOutputMessage(boardDownloadFileOutObj);
			}
		} catch (Exception e) {
			session.rollback();
			log.warn("unknown error", e);

			MessageResultRes messageResultOutObj = new MessageResultRes();
			messageResultOutObj.setTaskMessageID(boardDownloadFileInObj.getMessageID());
			messageResultOutObj.setIsSuccess(false);
			messageResultOutObj.setResultMessage("알 수 없는 이유로 게시판 조회가 실패하였습니다.");
			toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
			return;
		} finally {
			session.close();
		}*/

	}

}