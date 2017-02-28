package kr.pe.sinnori.impl.servertask;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.serverlib.ServerCommonStaticFinalVars;
import kr.pe.sinnori.impl.message.BoardDownloadFileInDTO.BoardDownloadFileInDTO;
import kr.pe.sinnori.impl.message.BoardDownloadFileOutDTO.BoardDownloadFileOutDTO;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.server.mybatis.MybatisSqlSessionFactoryManger;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

public class BoardDownloadFileInDTOServerTask extends AbstractServerTask {
	// private SqlSessionFactory sqlSessionFactory = null;

	@Override
	public void doTask(String projectName,
			LoginManagerIF loginManager, LetterSender letterSender,
			AbstractMessage messageFromClient) throws Exception {
		// FIXME!
		log.info(messageFromClient.toString());
		doWork(projectName, letterSender, (BoardDownloadFileInDTO)messageFromClient);
	}
	
	private void doWork(String projectName,
			LetterSender letterSender, BoardDownloadFileInDTO boardDownloadFileInObj)
			throws Exception {

		/*if (null == sqlSessionFactory) {
			sqlSessionFactory = SqlSessionFactoryManger.getInstance()
					.getSqlSessionFactory("tw_sinnoridb");
		}*/
		SqlSessionFactory sqlSessionFactory = MybatisSqlSessionFactoryManger.getInstance().getSqlSessionFactory(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		BoardDownloadFileOutDTO boardDownloadFileOutObj = null;

		SqlSession session = sqlSessionFactory.openSession(false);
		try {
			boardDownloadFileOutObj = session.selectOne("getDownloadFileInfo", boardDownloadFileInObj);

			if (null == boardDownloadFileOutObj) {
				session.commit();

				String errorMessage = "다운로드 파일 정보 얻기 실패";
				log.warn("{}, inObj={}", errorMessage, boardDownloadFileInObj.toString());

				MessageResult messageResultOutObj = new MessageResult();
				messageResultOutObj.setIsSuccess(false);
				messageResultOutObj.setTaskMessageID(boardDownloadFileInObj.getMessageID());
				messageResultOutObj.setResultMessage(errorMessage);

				letterSender.addSyncMessage(messageResultOutObj);
			} else {
				session.commit();
				letterSender.addSyncMessage(boardDownloadFileOutObj);
			}
		} catch (Exception e) {
			session.rollback();
			log.warn("unknown error", e);

			MessageResult messageResultOutObj = new MessageResult();
			messageResultOutObj.setTaskMessageID(boardDownloadFileInObj.getMessageID());
			messageResultOutObj.setIsSuccess(false);
			messageResultOutObj.setResultMessage("알 수 없는 이유로 게시판 조회가 실패하였습니다.");
			letterSender.addSyncMessage(messageResultOutObj);
			return;
		} finally {
			session.close();
		}

	}

}
