package kr.pe.sinnori.impl.servertask;

import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResource;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResourceManager;
import kr.pe.sinnori.impl.message.ReadyToUploadReq.ReadyToUploadReq;
import kr.pe.sinnori.impl.message.ReadyToUploadRes.ReadyToUploadRes;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractAuthServerExecutor;
import kr.pe.sinnori.server.executor.LetterSender;

public class ReadyToUploadReqServerTask extends AbstractAuthServerExecutor {

	@Override
	public void doTask(String projectName,
			LoginManagerIF loginManager,
			LetterSender letterSender, AbstractMessage requestMessage)
			throws Exception {
		doWork(projectName, letterSender, (ReadyToUploadReq)requestMessage);
	}
	 
	
	private void doWork(String projectName,
			LetterSender letterSender, ReadyToUploadReq readyToUploadReq)
			throws Exception {
		// FIXME!
		log.info(readyToUploadReq.toString());
		
		
		byte whetherToAppend = readyToUploadReq.getWhetherToAppend();
		int clientSourceFileID = readyToUploadReq.getClientSourceFileID();
		String localFilePathName = readyToUploadReq.getLocalFilePathName();
		String localFileName = readyToUploadReq.getLocalFileName();
		long localFileSize = readyToUploadReq.getLocalFileSize();
		String remoteFilePathName = readyToUploadReq.getRemoteFilePathName();
		String remoteFileName = readyToUploadReq.getRemoteFileName();
		long remoteFileSize = readyToUploadReq.getRemoteFileSize();
		int fileBlockSize = readyToUploadReq.getFileBlockSize();
		
		LocalTargetFileResourceManager localTargetFileResourceManager = LocalTargetFileResourceManager.getInstance();
		
		/** 서버 목적지 파일 식별자, 디폴트 값은 에러를 나타내는 -1 */
		// int serverTargetFileID = -1;
		
		boolean append;
		if (0 == whetherToAppend) {
			append = false;
		} else if (1 == whetherToAppend) {
			append = true;
		} else {
			/*sendSync("N", 
					String.format("파라미터 append 값[%x]에 알수 없는 값이 들어왔습니다.", appendByte), serverTargetFileID, 
					clientSourceFileID, letterSender, messageManger);*/
			ReadyToUploadRes readyToUploadRes = new ReadyToUploadRes();
			readyToUploadRes.setTaskResult("N");
			readyToUploadRes.setResultMessage(String.format("파라미터 append 값[%x]에 알수 없는 값이 들어왔습니다.", whetherToAppend));
			readyToUploadRes.setClientSourceFileID(clientSourceFileID);
			readyToUploadRes.setServerTargetFileID(-1);
			letterSender.addSyncMessage(readyToUploadRes);
			return;
		}
		
		String loginID = letterSender.getClientResource().getLoginID();
		
				
		int serverTargetFileID  = -1;
		try {
			LocalTargetFileResource  localTargetFileResource = 
					localTargetFileResourceManager.registerNewLocalTargetFileResource(loginID, append, 
					localFilePathName, localFileName, localFileSize, 
					remoteFilePathName, remoteFileName, remoteFileSize, fileBlockSize);
			
			localTargetFileResource.makeZeroSizeIfOverwrite();
			localTargetFileResource.setSourceFileID(clientSourceFileID);
			serverTargetFileID = localTargetFileResource.getTargetFileID();
			
			/*sendSync("Y", "파일 업로드 준비가 되었습니다.", serverTargetFileID, 
					clientSourceFileID, letterSender, messageManger);*/
			ReadyToUploadRes readyToUploadRes = new ReadyToUploadRes();
			readyToUploadRes.setTaskResult("Y");
			readyToUploadRes.setResultMessage("파일 업로드 준비가 되었습니다.");
			readyToUploadRes.setClientSourceFileID(clientSourceFileID);
			readyToUploadRes.setServerTargetFileID(serverTargetFileID);
			
			log.info(readyToUploadRes.toString());
			
			letterSender.addSyncMessage(readyToUploadRes);
			return;
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			
			/*sendSync("N", String.format("IllegalArgumentException::%s", e.getMessage()), serverTargetFileID, 
					clientSourceFileID, letterSender, messageManger);*/
			ReadyToUploadRes readyToUploadRes = new ReadyToUploadRes();
			readyToUploadRes.setTaskResult("N");
			readyToUploadRes.setResultMessage(e.getMessage());
			readyToUploadRes.setClientSourceFileID(clientSourceFileID);
			readyToUploadRes.setServerTargetFileID(serverTargetFileID);
			
			log.info(readyToUploadRes.toString());
			
			letterSender.addSyncMessage(readyToUploadRes);
			return;
		} catch (UpDownFileException e) {
			log.warn("UpDownFileException", e);
			
			ReadyToUploadRes readyToUploadRes = new ReadyToUploadRes();
			readyToUploadRes.setTaskResult("N");
			readyToUploadRes.setResultMessage(e.getMessage());
			readyToUploadRes.setClientSourceFileID(clientSourceFileID);
			readyToUploadRes.setServerTargetFileID(serverTargetFileID);
			
			log.info(readyToUploadRes.toString());
			
			letterSender.addSyncMessage(readyToUploadRes);
			return;
		}
	}
}
