package impl.executor.server;

import kr.pe.sinnori.common.configuration.ServerProjectConfigIF;
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
import kr.pe.sinnori.server.executor.LetterSender;

public final class DownFileInfoSExtor extends AbstractAuthServerExecutor {

	@Override
	protected void doTask(ServerProjectConfigIF serverProjectConfig,
			LetterSender letterSender, InputMessage inObj,
			MessageMangerIF messageManger,			
			ClientResourceManagerIF clientResourceManager)
			throws MessageInfoNotFoundException, MessageItemException {
		OutputMessage outObj = messageManger.createOutputMessage("DownFileInfoResult");

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
				// letterToClientList.addLetterToClient(fromSC, outObj);
				letterSender.sendSync(outObj);
				return;
			}
			
			log.info("localSourceFileResource 할당 성공");
			
			localSourceFileResource.setTargetFileID(clientTargetFileID);
			
			int serverSourceFileID = localSourceFileResource.getSourceFileID(); 

			ClientResource clientResource = letterSender.getInObjClientResource();
			clientResource.addLocalSourceFileID(serverSourceFileID);
			
			outObj.setAttribute("taskResult", "Y");
			outObj.setAttribute("resultMessage", "업로드할 파일을 받아줄 준비가 되었습니다.");
			outObj.setAttribute("clientTargetFileID", clientTargetFileID);
			outObj.setAttribute("serverSourceFileID", serverSourceFileID);
			letterSender.sendSync(outObj);
		} catch (IllegalArgumentException e) {
			log.info("IllegalArgumentException", e);
			
			if (null != localSourceFileResource) {
				localSourceFileResourceManager.putLocalSourceFileResource(localSourceFileResource);
			}
			
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "서버::"+e.getMessage());
			outObj.setAttribute("clientTargetFileID", clientTargetFileID);
			outObj.setAttribute("serverSourceFileID", -1);
			// letterToClientList.addLetterToClient(fromSC, outObj);
			letterSender.sendSync(outObj);
		} catch (UpDownFileException e) {
			log.info("UpDownFileException", e);
			
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "서버::"+e.getMessage());
			outObj.setAttribute("clientTargetFileID", clientTargetFileID);
			outObj.setAttribute("serverSourceFileID", -1);
			// letterToClientList.addLetterToClient(fromSC, outObj);
			letterSender.sendSync(outObj);
		}
		
		log.info("end");
	}
}
