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

	private void sendSync(
			String taskResult, String resultMessage, int serverSourceFileID, 
			int clientTargetFileID, LetterSender letterSender, MessageMangerIF messageManger) throws IllegalArgumentException, MessageInfoNotFoundException, MessageItemException {
		OutputMessage outObj = messageManger.createOutputMessage("DownFileInfoResult");
		outObj.setAttribute("taskResult", taskResult);
		outObj.setAttribute("resultMessage", resultMessage);
		outObj.setAttribute("clientTargetFileID", clientTargetFileID);
		outObj.setAttribute("serverSourceFileID", serverSourceFileID);
		letterSender.sendSync(outObj);
	}
	
	@Override
	protected void doTask(ServerProjectConfigIF serverProjectConfig,
			LetterSender letterSender, InputMessage inObj,
			MessageMangerIF messageManger,			
			ClientResourceManagerIF clientResourceManager)
			throws MessageInfoNotFoundException, MessageItemException {
		// OutputMessage outObj = messageManger.createOutputMessage("DownFileInfoResult");

		byte appendByte = (Byte)inObj.getAttribute("append");
		String localFilePathName = (String)inObj.getAttribute("localFilePathName");
		String localFileName = (String)inObj.getAttribute("localFileName");
		long localFileSize = (Long)inObj.getAttribute("localFileSize");
		String remoteFilePathName = (String)inObj.getAttribute("remoteFilePathName");
		String remoteFileName = (String)inObj.getAttribute("remoteFileName");
		long remoteFileSize = (Long)inObj.getAttribute("remoteFileSize");
		int clientTargetFileID = (Integer)inObj.getAttribute("clientTargetFileID");
		int fileBlockSize = (Integer)inObj.getAttribute("fileBlockSize");
		
		// FIXME!
		log.info(inObj.toString());
		
		/** 서버 원본 파일 식별자, 디폴트 값은 에러를 나타내는 -1 */
		int serverSourceFileID = -1;
		
		boolean append;
		if (0 == appendByte) {
			append = false;
		} else if (1 == appendByte) {
			append = true;
		} else {
			/*outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", String.format("서버::파라미터 append 값[%x]에 알수없는 값이 들어왔습니다.", append));
			outObj.setAttribute("clientTargetFileID", clientTargetFileID);
			outObj.setAttribute("serverSourceFileID", -1);
			letterSender.sendSync(outObj);*/
			sendSync("N", 
					String.format("파라미터 append 값[%x]에 알수 없는 값이 들어왔습니다.", appendByte), serverSourceFileID, 
					clientTargetFileID, letterSender, messageManger);
			return;
		}
		
		LocalSourceFileResourceManager localSourceFileResourceManager = LocalSourceFileResourceManager.getInstance();
		
		LocalSourceFileResource localSourceFileResource = null;
		try {
			localSourceFileResource = localSourceFileResourceManager.pollLocalSourceFileResource(
					append,
					remoteFilePathName, remoteFileName, remoteFileSize, 
					localFilePathName, localFileName, localFileSize,
					fileBlockSize);
			
			if (null == localSourceFileResource) {
				/*outObj.setAttribute("taskResult", "N");
				outObj.setAttribute("resultMessage", "큐로부터 원본 파일 자원 할당에 실패하였습니다.");
				outObj.setAttribute("clientTargetFileID", clientTargetFileID);
				outObj.setAttribute("serverSourceFileID", -1);
				// letterToClientList.addLetterToClient(fromSC, outObj);
				letterSender.sendSync(outObj);*/
				sendSync("N", "큐로부터 원본 파일 자원 할당에 실패하였습니다.", serverSourceFileID, 
						clientTargetFileID, letterSender, messageManger);
				return;
			}
			
			log.info("localSourceFileResource 할당 성공");
			
			localSourceFileResource.setTargetFileID(clientTargetFileID);
			
			serverSourceFileID = localSourceFileResource.getSourceFileID(); 

			ClientResource clientResource = letterSender.getInObjClientResource();
			clientResource.addLocalSourceFileID(serverSourceFileID);
			
			/*outObj.setAttribute("taskResult", "Y");
			outObj.setAttribute("resultMessage", "업로드할 파일을 받아줄 준비가 되었습니다.");
			outObj.setAttribute("clientTargetFileID", clientTargetFileID);
			outObj.setAttribute("serverSourceFileID", serverSourceFileID);
			letterSender.sendSync(outObj);*/
			sendSync("Y", "파일 업로드 준비가 되었습니다.", serverSourceFileID, 
					clientTargetFileID, letterSender, messageManger);
		} catch (IllegalArgumentException e) {
			log.info("IllegalArgumentException", e);
			
			if (null != localSourceFileResource) {
				localSourceFileResourceManager.putLocalSourceFileResource(localSourceFileResource);
			}
			
			/*outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "서버::"+e.getMessage());
			outObj.setAttribute("clientTargetFileID", clientTargetFileID);
			outObj.setAttribute("serverSourceFileID", -1);
			// letterToClientList.addLetterToClient(fromSC, outObj);
			letterSender.sendSync(outObj);*/
			sendSync("N", String.format("IllegalArgumentException::%s", e.getMessage()), serverSourceFileID,
					clientTargetFileID, letterSender, messageManger);
		} catch (UpDownFileException e) {
			log.info("UpDownFileException", e);
			
			/*outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "서버::"+e.getMessage());
			outObj.setAttribute("clientTargetFileID", clientTargetFileID);
			outObj.setAttribute("serverSourceFileID", -1);
			// letterToClientList.addLetterToClient(fromSC, outObj);
			letterSender.sendSync(outObj);*/
			sendSync("N", String.format("UpDownFileException::%s", e.getMessage()), serverSourceFileID,
					clientTargetFileID, letterSender, messageManger);
		}
		
		log.info("end");
	}
}
