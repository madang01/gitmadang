package impl.executor.server;

import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResource;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResourceManager;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.ClientResourceManagerIF;
import kr.pe.sinnori.server.executor.AbstractAuthServerExecutor;
import kr.pe.sinnori.server.io.LetterListToClient;
import kr.pe.sinnori.server.io.LetterToClient;

public final class CancelDownloadFileSExtor extends AbstractAuthServerExecutor {

	@Override
	protected void doTask(SocketChannel fromSC, InputMessage inObj,
			LetterListToClient letterToClientList,
			LinkedBlockingQueue<LetterToClient> ouputMessageQueue,
			MessageMangerIF messageManger,
			ClientResourceManagerIF clientResourceManager)
			throws MessageInfoNotFoundException, MessageItemException {
		LocalSourceFileResourceManager localSourceFileResourceManager = LocalSourceFileResourceManager.getInstance();
		OutputMessage outObj = messageManger.createOutputMessage("CancelDownloadFileResult");
		outObj.messageHeaderInfo = inObj.messageHeaderInfo;
		
		int serverSourceFileID = (Integer)inObj.getAttribute("serverSourceFileID");

		// FIXME!
		log.info(inObj.toString());
		
		LocalSourceFileResource  localSourceFileResource = null;
		
		localSourceFileResource = localSourceFileResourceManager.getLocalSourceFileResource(serverSourceFileID);
		
		if (null == localSourceFileResource) {
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", String.format("존재하지 않는 서버 원본 파일[%d] 식별자입니다.", serverSourceFileID));
			outObj.setAttribute("serverSourceFileID", serverSourceFileID);
			letterToClientList.addLetterToClient(fromSC, outObj);
			return;
		}
		
		ClientResource clientResource = clientResourceManager.getClientResource(fromSC);
		clientResource.removeLocalSourceFileID(serverSourceFileID);
		
		// localSourceFileResourceManager.putLocalSourceFileResource(localSourceFileResource);
		
		outObj.setAttribute("taskResult", "Y");
		outObj.setAttribute("resultMessage", String.format("서버 다운로드용 원본 파일[%d] 자원을 성공적으로 해제하였습니다.", serverSourceFileID));
		outObj.setAttribute("serverSourceFileID", serverSourceFileID);
		letterToClientList.addLetterToClient(fromSC, outObj);
	}
}
