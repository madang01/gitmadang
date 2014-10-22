package kr.pe.sinnori.impl.message.SyncUpFileInfo;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResource;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResourceManager;
import kr.pe.sinnori.impl.message.UpFileInfoResult.UpFileInfoResult;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractAuthServerExecutor;
import kr.pe.sinnori.server.executor.LetterSender;

public class SyncUpFileInfoServerTask extends AbstractAuthServerExecutor {

	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager, 
			LetterSender letterSender,
			AbstractMessage messageFromClient) throws Exception {
		// FIXME!
		log.info(messageFromClient.toString());
		
		SyncUpFileInfo inObj = (SyncUpFileInfo) messageFromClient;

		byte appendByte = inObj.getAppend();
		int clientSourceFileID = inObj.getClientSourceFileID();
		String localFilePathName = inObj.getLocalFilePathName();
		String localFileName = inObj.getLocalFileName();
		long localFileSize = inObj.getLocalFileSize();
		String remoteFilePathName = inObj.getRemoteFilePathName();
		String remoteFileName = inObj.getRemoteFileName();
		long remoteFileSize = inObj.getRemoteFileSize();
		int fileBlockSize = inObj.getFileBlockSize();
		
		LocalTargetFileResourceManager localTargetFileResourceManager = LocalTargetFileResourceManager.getInstance();
		
		/** 서버 목적지 파일 식별자, 디폴트 값은 에러를 나타내는 -1 */
		// int serverTargetFileID = -1;
		
		boolean append;
		if (0 == appendByte) {
			append = false;
		} else if (1 == appendByte) {
			append = true;
		} else {
			/*sendSync("N", 
					String.format("파라미터 append 값[%x]에 알수 없는 값이 들어왔습니다.", appendByte), serverTargetFileID, 
					clientSourceFileID, letterSender, messageManger);*/
			UpFileInfoResult outObj = new UpFileInfoResult();
			outObj.setTaskResult("N");
			outObj.setResultMessage(String.format("파라미터 append 값[%x]에 알수 없는 값이 들어왔습니다.", appendByte));
			outObj.setClientSourceFileID(clientSourceFileID);
			outObj.setServerTargetFileID(-1);
			letterSender.addSyncMessage(outObj);
			return;
		}
		
		LocalTargetFileResource  localTargetFileResource = null;		
		int serverTargetFileID  = -1;
		try {
			localTargetFileResource = localTargetFileResourceManager.pollLocalTargetFileResource(append, 
					localFilePathName, localFileName, localFileSize, 
					remoteFilePathName, remoteFileName, remoteFileSize, fileBlockSize);
			
			if (null == localTargetFileResource) {
				/*outObj.setAttribute("taskResult", "N");
				outObj.setAttribute("resultMessage", "큐로부터 목적지 파일 자원 할당에 실패하였습니다.");
				outObj.setAttribute("serverTargetFileID", -1);
				
				letterSender.sendSync(outObj);*/
				/*sendSync("N", "큐로부터 목적지 파일 자원 할당에 실패하였습니다.", serverTargetFileID, 
						clientSourceFileID, letterSender, messageManger);*/
				
				UpFileInfoResult outObj = new UpFileInfoResult();
				outObj.setTaskResult("N");
				outObj.setResultMessage("큐로부터 목적지 파일 자원 할당에 실패하였습니다.");
				outObj.setClientSourceFileID(clientSourceFileID);
				outObj.setServerTargetFileID(serverTargetFileID);
				letterSender.addSyncMessage(outObj);
				return;
			}
			
			localTargetFileResource.makeZeroSizeFile();
			localTargetFileResource.setSourceFileID(clientSourceFileID);
			serverTargetFileID = localTargetFileResource.getTargetFileID();
			
			ClientResource clientResource = letterSender.getClientResource();
			clientResource.addLocalTargetFileID(serverTargetFileID);
			
			/*sendSync("Y", "파일 업로드 준비가 되었습니다.", serverTargetFileID, 
					clientSourceFileID, letterSender, messageManger);*/
			UpFileInfoResult outObj = new UpFileInfoResult();
			outObj.setTaskResult("Y");
			outObj.setResultMessage("파일 업로드 준비가 되었습니다.");
			outObj.setClientSourceFileID(clientSourceFileID);
			outObj.setServerTargetFileID(serverTargetFileID);
			letterSender.addSyncMessage(outObj);
			return;
		} catch (IllegalArgumentException e) {
			log.info("IllegalArgumentException", e);
			
			/*sendSync("N", String.format("IllegalArgumentException::%s", e.getMessage()), serverTargetFileID, 
					clientSourceFileID, letterSender, messageManger);*/
			UpFileInfoResult outObj = new UpFileInfoResult();
			outObj.setTaskResult("N");
			outObj.setResultMessage(String.format("IllegalArgumentException::%s", e.getMessage()));
			outObj.setClientSourceFileID(clientSourceFileID);
			outObj.setServerTargetFileID(serverTargetFileID);
			letterSender.addSyncMessage(outObj);
			return;
		} catch (UpDownFileException e) {
			log.info("UpDownFileException", e);
			/*
			if (null != localTargetFileResource) {
				localTargetFileResourceManager.putLocalTargetFileResource(localTargetFileResource);
			}*/
			/*sendSync("N", String.format("UpDownFileException::%s", e.getMessage()), serverTargetFileID, 
					clientSourceFileID, letterSender, messageManger);*/
			UpFileInfoResult outObj = new UpFileInfoResult();
			outObj.setTaskResult("N");
			outObj.setResultMessage(String.format("UpDownFileException::%s", e.getMessage()));
			outObj.setClientSourceFileID(clientSourceFileID);
			outObj.setServerTargetFileID(serverTargetFileID);
			letterSender.addSyncMessage(outObj);
			return;
		}
	}	
}
