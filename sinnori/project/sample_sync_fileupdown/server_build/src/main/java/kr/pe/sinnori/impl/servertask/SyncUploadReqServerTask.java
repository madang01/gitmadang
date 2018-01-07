package kr.pe.sinnori.impl.servertask;

import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResource;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResourceManager;
import kr.pe.sinnori.impl.message.SyncUploadReq.SyncUploadReq;
import kr.pe.sinnori.impl.message.SyncUploadRes.SyncUploadRes;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractAuthServerExecutor;
import kr.pe.sinnori.server.executor.LetterSender;

public class SyncUploadReqServerTask extends AbstractAuthServerExecutor {

	@Override
	public void doTask(String projectName, LoginManagerIF loginManager, LetterSender letterSender,
			AbstractMessage requestMessage) throws Exception {
		doWork(projectName, letterSender, (SyncUploadReq) requestMessage);
	}

	private void doWork(String projectName, LetterSender letterSender, SyncUploadReq syncUploadReq) throws Exception {
		// FIXME!
		log.info(syncUploadReq.toString());

		int clientSourceFileID = syncUploadReq.getClientSourceFileID();
		int serverTargetFileID = syncUploadReq.getServerTargetFileID();
		int fileBlockNo = syncUploadReq.getFileBlockNo();
		byte[] fileData = syncUploadReq.getFileData();

		// FIXME!
		// log.info(inObj.toString());

		LocalTargetFileResourceManager localTargetFileResourceManager = LocalTargetFileResourceManager.getInstance();
		SyncUploadRes syncUploadRes = new SyncUploadRes();
		// OutputMessage outObj =
		// messageManger.createOutputMessage("UpFileDataResult");

		syncUploadRes.setServerTargetFileID(serverTargetFileID);
		syncUploadRes.setClientSourceFileID(clientSourceFileID);
		syncUploadRes.setFileBlockNo(fileBlockNo);

		LocalTargetFileResource localTargetFileResource = localTargetFileResourceManager
				.getLocalTargetFileResource(serverTargetFileID);

		if (null == localTargetFileResource) {
			log.info(String.format("serverTargetFileID[%d] 업로드 파일을 받을 자원이 준비되지 않았습니다.", serverTargetFileID));

			/*
			 * outObj.setAttribute("taskResult", "N");
			 * outObj.setAttribute("resultMessage",
			 * "서버에서 업로드 파일을 받을 자원이 준비되지 않았습니다.");
			 * outObj.setAttribute("serverTargetFileID", serverTargetFileID);
			 * outObj.setAttribute("clientSourceFileID", -1);
			 * letterSender.sendSync(outObj);
			 */
			syncUploadRes.setTaskResult("N");
			syncUploadRes.setResultMessage("서버에서 업로드 파일을 받을 자원이 준비되지 않았습니다.");
			letterSender.addSyncMessage(syncUploadRes);
			return;
		}

		try {
			// WORK_STEP workStep = localTargetFileResource.getWorkStep();

			localTargetFileResource.writeTargetFileData(serverTargetFileID, fileBlockNo, fileData, true);
			// localTargetFileResource.turnOnWorkedFileBlockBitSetAt(fileBlockNo);

			if (localTargetFileResource.whetherAllBitOfBitSetIslTrue()) {
				log.info(String.format("clientSourceFileID[%s] to serverTargetFileID[%d] 파일 업로드 전체 완료",
						localTargetFileResource.getSourceFileID(), serverTargetFileID));

				localTargetFileResource.setWorkStep(LocalTargetFileResource.WorkStep.TRANSFER_DONE);
				localTargetFileResourceManager.removeWithUnlockFile(localTargetFileResource);
			}

			syncUploadRes.setTaskResult("Y");
			syncUploadRes.setResultMessage(
					new StringBuilder("서버에서 수신한 업로드 파일 조각[").append(fileBlockNo).append("] 저장이 완료되었습니다.").toString());
			letterSender.addSyncMessage(syncUploadRes);

		} catch (IllegalArgumentException e) {
			log.info(String.format("serverTargetFileID[%d] lock free::%s", serverTargetFileID, e.getMessage()), e);
			// clientResource.removeLocalTargetFileID(serverTargetFileID);

			/*
			 * outObj.setAttribute("taskResult", "N");
			 * outObj.setAttribute("resultMessage", "서버::"+e.getMessage());
			 * outObj.setAttribute("serverTargetFileID", serverTargetFileID);
			 * 
			 * letterSender.sendSync(outObj);
			 */
			syncUploadRes.setTaskResult("N");
			syncUploadRes.setResultMessage("서버::" + e.getMessage());
			letterSender.addSyncMessage(syncUploadRes);
			return;
		} catch (UpDownFileException e) {
			log.info(String.format("serverTargetFileID[%d] lock free::%s", serverTargetFileID, e.getMessage()), e);

			/*
			 * ClientResource clientResource =
			 * letterSender.getInObjClientResource();
			 * clientResource.removeLocalTargetFileID(serverTargetFileID);
			 * 
			 * outObj.setAttribute("taskResult", "N");
			 * outObj.setAttribute("resultMessage", "서버::"+e.getMessage());
			 * outObj.setAttribute("serverTargetFileID", serverTargetFileID);
			 * 
			 * letterSender.sendSync(outObj);
			 */
			syncUploadRes.setTaskResult("N");
			syncUploadRes.setResultMessage("서버::" + e.getMessage());
			letterSender.addSyncMessage(syncUploadRes);
			return;
		}
	}
}
