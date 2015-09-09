package kr.pe.sinnori.impl.servertask;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.BoardDownloadFileInDTO.BoardDownloadFileInDTO;
import kr.pe.sinnori.impl.message.BoardDownloadFileOutDTO.BoardDownloadFileOutDTO;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.server.mybatis.SqlSessionFactoryManger;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class BoardDownloadFileInDTOServerTask extends AbstractServerTask {
	// private SqlSessionFactory sqlSessionFactory = null;

	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager, LetterSender letterSender,
			AbstractMessage messageFromClient) throws Exception {
		// FIXME!
		log.info(messageFromClient.toString());

		/*if (null == sqlSessionFactory) {
			sqlSessionFactory = SqlSessionFactoryManger.getInstance()
					.getSqlSessionFactory("tw_sinnoridb");
		}*/
		SqlSessionFactory sqlSessionFactory = SqlSessionFactoryManger.getInstance().getSqlSessionFactory("tw_sinnoridb");

		BoardDownloadFileInDTO inObj = (BoardDownloadFileInDTO) messageFromClient;

		BoardDownloadFileOutDTO outObj = null;

		SqlSession session = sqlSessionFactory.openSession(false);
		try {
			outObj = session.selectOne("getDownloadFileInfo", inObj);

			if (null == outObj) {
				session.commit();

				String errorMessage = "다운로드 파일 정보 얻기 실패";
				log.warn("{}, inObj={}", errorMessage, inObj.toString());

				MessageResult messageResultOutObj = new MessageResult();
				messageResultOutObj.setIsSuccess(false);
				messageResultOutObj.setTaskMessageID(inObj.getMessageID());
				messageResultOutObj.setResultMessage(errorMessage);

				letterSender.addSyncMessage(messageResultOutObj);
			} else {
				session.commit();
				letterSender.addSyncMessage(outObj);
			}
		} catch (Exception e) {
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

	}

}
