package impl.executor.server;

import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResource;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResourceManager;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.ClientResourceManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerExecutor;
import kr.pe.sinnori.server.io.LetterListToClient;
import kr.pe.sinnori.server.io.LetterToClient;

public final class CancelUploadFileSExtor extends AbstractServerExecutor {

	@Override
	protected void doTask(SocketChannel fromSC, InputMessage inObj,
			LetterListToClient letterToClientList,
			LinkedBlockingQueue<LetterToClient> ouputMessageQueue,
			MessageMangerIF messageManger,
			ClientResourceManagerIF clientResourceManager)
			throws MessageInfoNotFoundException, MessageItemException {
		LocalTargetFileResourceManager localTargetFileResourceManager = LocalTargetFileResourceManager.getInstance();
		OutputMessage outObj = messageManger.createOutputMessage("CancelUploadFileResult");
		outObj.messageHeaderInfo = inObj.messageHeaderInfo;
		
		
		int serverTargetFileID = (Integer)inObj.getAttribute("serverTargetFileID");
		
		// FIXME!
		log.info(inObj.toString());
		
		LocalTargetFileResource  localTargetFileResource = null;
		
		localTargetFileResource = localTargetFileResourceManager.getLocalTargetFileResource(serverTargetFileID);
		
		if (null == localTargetFileResource) {
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", String.format("존재하지 않는 서버 목적지 파일[%d] 식별자입니다.", serverTargetFileID));
			outObj.setAttribute("serverTargetFileID", serverTargetFileID);
			letterToClientList.addLetterToClient(fromSC, outObj);
			return;
		}
		
		ClientResource clientResource = clientResourceManager.getClientResource(fromSC);
		clientResource.removeLocalTargetFileID(serverTargetFileID);
		
		
		// localTargetFileResourceManager.putLocalTargetFileResource(localTargetFileResource);
		
		outObj.setAttribute("taskResult", "Y");
		outObj.setAttribute("resultMessage", String.format("서버 업로드용 목적지 파일[%d] 자원을 성공적으로 해제하였습니다.", serverTargetFileID));
		outObj.setAttribute("serverTargetFileID", serverTargetFileID);
		
		// FIXME!
		log.info(outObj.toString());
		
		letterToClientList.addLetterToClient(fromSC, outObj);
	}
}
