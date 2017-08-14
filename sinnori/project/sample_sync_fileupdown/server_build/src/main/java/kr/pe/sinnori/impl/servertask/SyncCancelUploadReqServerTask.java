package kr.pe.sinnori.impl.servertask;

import java.io.File;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResource;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResource.WorkStep;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResourceManager;
import kr.pe.sinnori.impl.message.SyncCancelUploadReq.SyncCancelUploadReq;
import kr.pe.sinnori.impl.message.SyncCancelUploadRes.SyncCancelUploadRes;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractAuthServerExecutor;
import kr.pe.sinnori.server.executor.LetterSender;

public class SyncCancelUploadReqServerTask extends AbstractAuthServerExecutor {

	@Override
	public void doTask(String projectName,
			LoginManagerIF loginManager,
			LetterSender letterSender, AbstractMessage requestMessage)
			throws Exception {
		doWork(projectName, letterSender, (SyncCancelUploadReq)requestMessage);
	}
	 
	
	private void doWork(String projectName,
			LetterSender letterSender, SyncCancelUploadReq syncCancelUploadReq)
			throws Exception {
		// FIXME!
		log.info(syncCancelUploadReq.toString());
		
		int clientSourceFileID = syncCancelUploadReq.getClientSourceFileID();
		int serverTargetFileID = syncCancelUploadReq.getServerTargetFileID();
		
		LocalTargetFileResourceManager localTargetFileResourceManager = LocalTargetFileResourceManager.getInstance();
		SyncCancelUploadRes syncCancelUploadRes = new SyncCancelUploadRes();
		
		
		LocalTargetFileResource  localTargetFileResource = localTargetFileResourceManager.getLocalTargetFileResource(serverTargetFileID);
		
		if (null == localTargetFileResource) {
			syncCancelUploadRes.setTaskResult("N");
			syncCancelUploadRes.setResultMessage(String.format("존재하지 않는 서버 목적지 파일[%d] 식별자입니다.", serverTargetFileID));
			syncCancelUploadRes.setClientSourceFileID(clientSourceFileID);
			syncCancelUploadRes.setServerTargetFileID(serverTargetFileID);
			letterSender.addSyncMessage(syncCancelUploadRes);
			return;
		}
		
		localTargetFileResource.setWorkStep(WorkStep.CANCEL_DONE);
		localTargetFileResourceManager.removeWithUnlockFile(localTargetFileResource);
		
		syncCancelUploadRes.setTaskResult("Y");
		syncCancelUploadRes.setResultMessage(String.format("서버 업로드용 목적지 파일[%d][%s%s%s] 자원을 성공적으로 해제하였습니다.", 
				serverTargetFileID, 
				localTargetFileResource.getTargetFilePathName(),
				File.separator,
				localTargetFileResource.getTargetFileName()));
		syncCancelUploadRes.setClientSourceFileID(clientSourceFileID);
		syncCancelUploadRes.setServerTargetFileID(serverTargetFileID);
		
		log.info(syncCancelUploadRes.toString());
		
		letterSender.addSyncMessage(syncCancelUploadRes);
	}
}
