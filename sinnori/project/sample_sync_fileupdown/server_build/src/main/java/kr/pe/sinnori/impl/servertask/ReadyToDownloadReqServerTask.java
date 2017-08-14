package kr.pe.sinnori.impl.servertask;

import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResource;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResourceManager;
import kr.pe.sinnori.impl.message.ReadyToDownloadReq.ReadyToDownloadReq;
import kr.pe.sinnori.impl.message.ReadyToDownloadRes.ReadyToDownloadRes;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractAuthServerExecutor;
import kr.pe.sinnori.server.executor.LetterSender;

public class ReadyToDownloadReqServerTask extends AbstractAuthServerExecutor {

	@Override
	public void doTask(String projectName,
			LoginManagerIF loginManager,
			LetterSender letterSender, AbstractMessage readyToDownloadReq)
			throws Exception {
		doWork(projectName, letterSender, (ReadyToDownloadReq)readyToDownloadReq);
	}
	 
	
	private void doWork(String projectName,
			LetterSender letterSender, ReadyToDownloadReq readyToDownloadReq)
			throws Exception {
		// FIXME!
		log.info(readyToDownloadReq.toString());
		
		byte appendByte = readyToDownloadReq.getAppend();
		String localFilePathName = readyToDownloadReq.getLocalFilePathName();
		String localFileName = readyToDownloadReq.getLocalFileName();
		long localFileSize = readyToDownloadReq.getLocalFileSize();
		String remoteFilePathName = readyToDownloadReq.getRemoteFilePathName();
		String remoteFileName = readyToDownloadReq.getRemoteFileName();
		long remoteFileSize = readyToDownloadReq.getRemoteFileSize();
		int clientTargetFileID = readyToDownloadReq.getClientTargetFileID();
		int fileBlockSize = readyToDownloadReq.getFileBlockSize();
		
		
		
		boolean append;
		if (0 == appendByte) {
			append = false;
		} else if (1 == appendByte) {
			append = true;
		} else {
			/*readyToDownloadRes.setAttribute("taskResult", "N");
			readyToDownloadRes.setAttribute("resultMessage", String.format("서버::파라미터 append 값[%x]에 알수없는 값이 들어왔습니다.", append));
			readyToDownloadRes.setAttribute("clientTargetFileID", clientTargetFileID);
			readyToDownloadRes.setAttribute("serverSourceFileID", -1);
			letterSender.sendSync(readyToDownloadRes);*/
			/*sendSync("N", 
					String.format("파라미터 append 값[%x]에 알수 없는 값이 들어왔습니다.", appendByte), serverSourceFileID, 
					clientTargetFileID, letterSender, messageManger);*/
			
			ReadyToDownloadRes readyToDownloadRes = new ReadyToDownloadRes();
			readyToDownloadRes.setTaskResult("N");
			readyToDownloadRes.setResultMessage(String.format("서버::파라미터 append 값[%x]에 알수없는 값이 들어왔습니다.", appendByte));
			readyToDownloadRes.setClientTargetFileID(clientTargetFileID);
			readyToDownloadRes.setServerSourceFileID(-1);
			letterSender.addSyncMessage(readyToDownloadRes);
			return;
		}
		
		String loginID = letterSender.getClientResource().getLoginID();
		
		LocalSourceFileResourceManager localSourceFileResourceManager = LocalSourceFileResourceManager.getInstance();
		int serverSourceFileID = -1;
		try {
			LocalSourceFileResource localSourceFileResource = localSourceFileResourceManager.registerNewLocalSourceFileResource(
					loginID,
					append,
					remoteFilePathName, remoteFileName, remoteFileSize, 
					localFilePathName, localFileName, localFileSize,
					fileBlockSize);			
			
			serverSourceFileID = localSourceFileResource.getSourceFileID();
			localSourceFileResource.setTargetFileID(clientTargetFileID);			
			
			ReadyToDownloadRes readyToDownloadRes = new ReadyToDownloadRes();
			readyToDownloadRes.setTaskResult("Y");
			readyToDownloadRes.setResultMessage("업로드할 파일을 받아줄 준비가 되었습니다.");
			readyToDownloadRes.setClientTargetFileID(clientTargetFileID);
			readyToDownloadRes.setServerSourceFileID(serverSourceFileID);
			
			log.info(readyToDownloadRes.toString());
			
			letterSender.addSyncMessage(readyToDownloadRes);
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			
			ReadyToDownloadRes readyToDownloadRes = new ReadyToDownloadRes();
			readyToDownloadRes.setTaskResult("N");
			readyToDownloadRes.setResultMessage(e.getMessage());
			readyToDownloadRes.setClientTargetFileID(clientTargetFileID);
			readyToDownloadRes.setServerSourceFileID(serverSourceFileID);
			
			log.info(readyToDownloadRes.toString());
			
			letterSender.addSyncMessage(readyToDownloadRes);
			return;
		} catch (UpDownFileException e) {
			log.warn("UpDownFileException", e);			
			
			ReadyToDownloadRes readyToDownloadRes = new ReadyToDownloadRes();
			readyToDownloadRes.setTaskResult("N");
			readyToDownloadRes.setResultMessage(e.getMessage());
			readyToDownloadRes.setClientTargetFileID(clientTargetFileID);
			readyToDownloadRes.setServerSourceFileID(serverSourceFileID);
			
			log.info(readyToDownloadRes.toString());
			
			letterSender.addSyncMessage(readyToDownloadRes);
			return;
		}
			
		
	}

}
