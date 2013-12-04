package impl.executor.server;

import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResource;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResourceManager;
import kr.pe.sinnori.server.ClientResourceManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerExecutor;
import kr.pe.sinnori.server.io.LetterListToClient;
import kr.pe.sinnori.server.io.LetterToClient;

public final class UpFileInfoSExtor extends AbstractServerExecutor {

	@Override
	protected void doTask(SocketChannel fromSC, InputMessage inObj,
			LetterListToClient letterToClientList,
			LinkedBlockingQueue<LetterToClient> ouputMessageQueue,
			MessageMangerIF messageManger,
			ClientResourceManagerIF clientResourceManager)
			throws MessageInfoNotFoundException, MessageItemException {
		LocalTargetFileResourceManager localTargetFileResourceManager = LocalTargetFileResourceManager.getInstance();
		OutputMessage outObj = messageManger.createOutputMessage("UpFileInfoResult");
		outObj.messageHeaderInfo = inObj.messageHeaderInfo;
		
		String localFilePathName = (String)inObj.getAttribute("localFilePathName");
		String localFileName = (String)inObj.getAttribute("localFileName");
		Long localFileSize = (Long)inObj.getAttribute("localFileSize");
		String remoteFilePathName = (String)inObj.getAttribute("remoteFilePathName");
		String remoteFileName = (String)inObj.getAttribute("remoteFileName");
		int fileBlockSize = (Integer)inObj.getAttribute("fileBlockSize");
		
		// FIXME!
		log.info(inObj.toString());
		
		LocalTargetFileResource  localTargetFileResource = null;
		
		
		try {
			localTargetFileResource = localTargetFileResourceManager.pollLocalTargetFileResource(localFilePathName, localFileName, localFileSize, remoteFilePathName, remoteFileName, fileBlockSize);
			
			if (null == localTargetFileResource) {
				outObj.setAttribute("taskResult", "N");
				outObj.setAttribute("resultMessage", "큐로부터 목적지 파일 자원 할당에 실패하였습니다.");
				outObj.setAttribute("serverTargetFileID", -1);
				letterToClientList.addLetterToClient(fromSC, outObj);
				return;
			}
			
			outObj.setAttribute("taskResult", "Y");
			outObj.setAttribute("resultMessage", "업로드할 파일을 받아줄 준비가 되었습니다.");
			outObj.setAttribute("serverTargetFileID", localTargetFileResource.getTargetFileID());
			
			/*
			LetterToClient letterToClient = new LetterToClient(fromSC, outObj);
			
			try {
				ouputMessageQueue.put(letterToClient);
			} catch (InterruptedException e1) {
				// 출력 메시지 큐 담는 과정에서 인터럽트 발생시 로그만 남기고 무시
				log.warn("업로드 파일 준비 실패했다는 내용을 담은 출력 메시지[UpFileInfoResult]를 출력 메시지 큐 담는 과정에서 인터럽트 발생", e1);
				return;
			}
		*/
			letterToClientList.addLetterToClient(fromSC, outObj);
		} catch (IllegalArgumentException e) {
			log.info("IllegalArgumentException", e);
			
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", e.getMessage());
			outObj.setAttribute("serverTargetFileID", -1);
			/*
			LetterToClient letterToClient = new LetterToClient(fromSC, outObj);
			try {
				ouputMessageQueue.put(letterToClient);
			} catch (InterruptedException e1) {
				//  출력 메시지 큐 담는 과정에서 인터럽트 발생시 로그만 남기고 무시 
				log.warn("업로드 파일 준비 실패했다는 내용을 담은 출력 메시지[UpFileInfoResult]를 출력 메시지 큐 담는 과정에서 인터럽트 발생", e1);
			}
			return;
			*/
			letterToClientList.addLetterToClient(fromSC, outObj);
		} catch (UpDownFileException e) {
			log.info("UpDownFileException", e);
			
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "서버::"+e.getMessage());
			outObj.setAttribute("serverTargetFileID", -1);
			/*
			LetterToClient letterToClient = new LetterToClient(fromSC, outObj);
			try {
				ouputMessageQueue.put(letterToClient);
			} catch (InterruptedException e1) {
				// 출력 메시지 큐 담는 과정에서 인터럽트 발생시 로그만 남기고 무시 
				log.warn("업로드 파일 준비 실패했다는 내용을 담은 출력 메시지[UpFileInfoResult]를 출력 메시지 큐 담는 과정에서 인터럽트 발생", e1);
			}
			return;
			*/
			letterToClientList.addLetterToClient(fromSC, outObj);
		}
	}
}
