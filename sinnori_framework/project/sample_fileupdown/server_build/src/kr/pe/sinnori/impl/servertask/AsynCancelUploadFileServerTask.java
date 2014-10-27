package kr.pe.sinnori.impl.servertask;

import java.io.File;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResource;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResourceManager;
import kr.pe.sinnori.impl.message.AsynCancelUploadFile.AsynCancelUploadFile;
import kr.pe.sinnori.impl.message.CancelUploadFileResult.CancelUploadFileResult;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractAuthServerExecutor;
import kr.pe.sinnori.server.executor.LetterSender;

/**
 * 비동기 메시지 방식의 파일 업로드 취소 서버 비지니스 로직 클래스
 * @author "Jonghoon Won"
 *
 */
public class AsynCancelUploadFileServerTask extends AbstractAuthServerExecutor {

	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager,
			LetterSender letterSender, AbstractMessage messageFromClient)
			throws Exception {
		// FIXME!
		log.info(messageFromClient.toString());
				
		AsynCancelUploadFile inObj = (AsynCancelUploadFile) messageFromClient;		
		int clientSourceFileID = inObj.getClientSourceFileID();
		int serverTargetFileID = inObj.getServerTargetFileID();
		
		LocalTargetFileResourceManager localTargetFileResourceManager = LocalTargetFileResourceManager.getInstance();
		CancelUploadFileResult outObj = new CancelUploadFileResult();
		
		/*OutputMessage outObj = messageManger.createOutputMessage("CancelUploadFileResult");
		
		int clientSourceFileID = (Integer)inObj.getAttribute("clientSourceFileID");
		int serverTargetFileID = (Integer)inObj.getAttribute("serverTargetFileID");*/
		
		LocalTargetFileResource  localTargetFileResource = null;
		
		localTargetFileResource = localTargetFileResourceManager.getLocalTargetFileResource(serverTargetFileID);
		
		if (null == localTargetFileResource) {
			/*outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", String.format("존재하지 않는 서버 목적지 파일[%d] 식별자입니다.", serverTargetFileID));
			outObj.setAttribute("clientSourceFileID", clientSourceFileID);
			outObj.setAttribute("serverTargetFileID", serverTargetFileID);
			
			//letterToClientList.addLetterToClient(fromSC, outObj);
			letterSender.sendAsyn(outObj);*/
			
			outObj.setTaskResult("N");
			outObj.setResultMessage(String.format("존재하지 않는 서버 목적지 파일[%d] 식별자입니다.", serverTargetFileID));
			outObj.setClientSourceFileID(clientSourceFileID);
			outObj.setServerTargetFileID(serverTargetFileID);
			letterSender.addAsynMessage(outObj);
			return;
		}
		
		ClientResource clientResource = letterSender.getClientResource();
		clientResource.removeLocalTargetFileID(serverTargetFileID);
		
		
		// localTargetFileResourceManager.putLocalTargetFileResource(localTargetFileResource);
		
		/*outObj.setAttribute("taskResult", "Y");
		outObj.setAttribute("resultMessage", 
				String.format("서버 업로드용 목적지 파일[%d][%s%s%s] 자원을 성공적으로 해제하였습니다.", 
						serverTargetFileID, 
						localTargetFileResource.getTargetFilePathName(),
						File.pathSeparator,
						localTargetFileResource.getTargetFileName()));
		outObj.setAttribute("clientSourceFileID", clientSourceFileID);
		outObj.setAttribute("serverTargetFileID", serverTargetFileID);*/
		
		outObj.setTaskResult("Y");
		outObj.setResultMessage(String.format("서버 업로드용 목적지 파일[%d][%s%s%s] 자원을 성공적으로 해제하였습니다.", 
				serverTargetFileID, 
				localTargetFileResource.getTargetFilePathName(),
				File.separator,
				localTargetFileResource.getTargetFileName()));
		outObj.setClientSourceFileID(clientSourceFileID);
		outObj.setServerTargetFileID(serverTargetFileID);
		
		// FIXME!
		log.info(outObj.toString());		
		
		//letterToClientList.addLetterToClient(fromSC, outObj);
		letterSender.addAsynMessage(outObj);
	}
}
