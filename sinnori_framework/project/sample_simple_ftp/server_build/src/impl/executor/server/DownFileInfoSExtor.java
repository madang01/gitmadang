package impl.executor.server;

import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.exception.UpDownFileException;
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

public final class DownFileInfoSExtor extends AbstractAuthServerExecutor {

	@Override
	protected void doTask(SocketChannel fromSC, InputMessage inObj,
			LetterListToClient letterToClientList,
			LinkedBlockingQueue<LetterToClient> ouputMessageQueue,
			MessageMangerIF messageManger,
			ClientResourceManagerIF clientResourceManager)
			throws MessageInfoNotFoundException, MessageItemException {
		OutputMessage outObj = messageManger.createOutputMessage("DownFileInfoResult");
		outObj.messageHeaderInfo = inObj.messageHeaderInfo;
		
		String localFilePathName = (String)inObj.getAttribute("localFilePathName");
		String localFileName = (String)inObj.getAttribute("localFileName");
		String remoteFilePathName = (String)inObj.getAttribute("remoteFilePathName");
		String remoteFileName = (String)inObj.getAttribute("remoteFileName");
		Long remoteFileSize = (Long)inObj.getAttribute("remoteFileSize");
		int clientTargetFileID = (Integer)inObj.getAttribute("clientTargetFileID");
		int fileBlockSize = (Integer)inObj.getAttribute("fileBlockSize");
		
		// FIXME!
		log.info(inObj.toString());
		
		LocalSourceFileResourceManager localSourceFileResourceManager = LocalSourceFileResourceManager.getInstance();
		
		LocalSourceFileResource localSourceFileResource = null;
		try {
			localSourceFileResource = localSourceFileResourceManager.pollLocalSourceFileResource(remoteFilePathName, remoteFileName, remoteFileSize, localFilePathName, localFileName, fileBlockSize);
			
			if (null == localSourceFileResource) {
				outObj.setAttribute("taskResult", "N");
				outObj.setAttribute("resultMessage", "큐로부터 원본 파일 자원 할당에 실패하였습니다.");
				outObj.setAttribute("clientTargetFileID", clientTargetFileID);
				outObj.setAttribute("serverSourceFileID", -1);
				letterToClientList.addLetterToClient(fromSC, outObj);
				return;
			}
			
			int serverSourceFileID = localSourceFileResource.getSourceFileID(); 

			ClientResource clientResource = clientResourceManager.getClientResource(fromSC);
			clientResource.addLocalSourceFileID(serverSourceFileID);
			
			outObj.setAttribute("taskResult", "Y");
			outObj.setAttribute("resultMessage", "업로드할 파일을 받아줄 준비가 되었습니다.");
			outObj.setAttribute("clientTargetFileID", clientTargetFileID);
			outObj.setAttribute("serverSourceFileID", serverSourceFileID);
			letterToClientList.addLetterToClient(fromSC, outObj);
		} catch (IllegalArgumentException e) {
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "서버::"+e.getMessage());
			outObj.setAttribute("clientTargetFileID", clientTargetFileID);
			outObj.setAttribute("serverSourceFileID", -1);
			letterToClientList.addLetterToClient(fromSC, outObj);
		} catch (UpDownFileException e) {
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "서버::"+e.getMessage());
			outObj.setAttribute("clientTargetFileID", clientTargetFileID);
			outObj.setAttribute("serverSourceFileID", -1);
			letterToClientList.addLetterToClient(fromSC, outObj);
		}
		
	}

}
