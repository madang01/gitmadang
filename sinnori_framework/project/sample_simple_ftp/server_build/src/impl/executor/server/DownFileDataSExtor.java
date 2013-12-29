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

public final class DownFileDataSExtor extends AbstractAuthServerExecutor {

	
	@Override
	protected void doTask(ServerProjectConfigIF serverProjectConfig,
			LetterSender letterSender, InputMessage inObj,
			MessageMangerIF messageManger,			
			ClientResourceManagerIF clientResourceManager)
			throws MessageInfoNotFoundException, MessageItemException {
		LocalSourceFileResourceManager localTargetFileResourceManager = LocalSourceFileResourceManager.getInstance();
		
		OutputMessage outObj = messageManger.createOutputMessage("DownFileDataResult");

		int serverSourceFileID = (Integer)inObj.getAttribute("serverSourceFileID");
		int fileBlockNo = (Integer)inObj.getAttribute("fileBlockNo");
		// byte[] fileData = (byte[])inObj.getAttribute("fileData");
		
		// FIXME!
		// log.info(inObj.toString());
		
		LocalSourceFileResource localSourceFileResource = localTargetFileResourceManager.getLocalSourceFileResource(serverSourceFileID);
		
		if (null == localSourceFileResource) {
			log.warn(String.format("serverSourceFileID[%d] 다운로드 파일을 받을 자원이 준비되지 않았습니다.", serverSourceFileID));
			
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "서버에서 다운로드 파일을 받을 자원이 준비되지 않았습니다.");
			outObj.setAttribute("serverSourceFileID", serverSourceFileID);
			outObj.setAttribute("fileBlockNo", fileBlockNo);
			outObj.setAttribute("fileData", new byte[0]);
			
			// letterToClientList.addLetterToClient(fromSC, outObj);
			letterSender.sendSelf(outObj);
			return;
		}
		
		boolean isCompletedReadingFile = false; 
		try {
			byte[] fileData = null;
			fileData = localSourceFileResource.getByteArrayOfFileBlockNo(fileBlockNo);
			isCompletedReadingFile = localSourceFileResource.readSourceFileData(fileBlockNo, fileData, true);
			
			// FIXME!
			// log.info(String.format("파일 읽기 결과[%s]", isCompletedReadingFile));
			
			outObj.setAttribute("taskResult", "Y");
			outObj.setAttribute("resultMessage", "서버에서 요청한 파일 조각을 성공적으로 읽었습니다.");
			outObj.setAttribute("serverSourceFileID", serverSourceFileID);
			outObj.setAttribute("fileBlockNo", fileBlockNo);
			outObj.setAttribute("fileData", fileData);
			
			try {
				// letterToClientList.addLetterToClient(fromSC, outObj);
				letterSender.sendSelf(outObj);
			} finally {
				if (isCompletedReadingFile) {
					ClientResource clientResource = letterSender.getInObjClientResource();
					clientResource.removeLocalSourceFileID(serverSourceFileID);
					// localTargetFileResourceManager.putLocalSourceFileResource(localSourceFileResource);
				}
			}
			
		} catch (IllegalArgumentException e) {
			log.warn(String.format("serverSourceFileID[%d] lock free::%s", serverSourceFileID, e.getMessage()), e);
			
			ClientResource clientResource = letterSender.getInObjClientResource();
			clientResource.removeLocalSourceFileID(serverSourceFileID);
			
			outObj.setAttribute("taskResult", "N");
			
			outObj.setAttribute("resultMessage", new StringBuilder("서버 에러 메시지\n").append(e.getMessage()).toString());
			outObj.setAttribute("serverSourceFileID", serverSourceFileID);
			outObj.setAttribute("fileBlockNo", fileBlockNo);
			outObj.setAttribute("fileData", new byte[0]);
			
			// letterToClientList.addLetterToClient(fromSC, outObj);
			letterSender.sendSelf(outObj);
			return;
		} catch (UpDownFileException e) {
			log.warn(String.format("serverSourceFileID[%d] lock free::%s", serverSourceFileID, e.getMessage()), e);
			
			ClientResource clientResource = letterSender.getInObjClientResource();
			clientResource.removeLocalSourceFileID(serverSourceFileID);
			
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", new StringBuilder("서버 에러 메시지\n").append(e.getMessage()).toString());
			outObj.setAttribute("serverSourceFileID", serverSourceFileID);
			outObj.setAttribute("fileBlockNo", fileBlockNo);
			outObj.setAttribute("fileData", new byte[0]);
			
			// letterToClientList.addLetterToClient(fromSC, outObj);
			letterSender.sendSelf(outObj);
			return;
		}
	}
}