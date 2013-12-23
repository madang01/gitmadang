package impl.executor.server;

import java.io.File;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.message.ArrayData;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.ItemGroupDataIF;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.util.DirectoryFirstComparator;
import kr.pe.sinnori.common.util.NameFirstComparator;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.ClientResourceManagerIF;
import kr.pe.sinnori.server.executor.AbstractAuthServerExecutor;

public final class FileListRequestSExtor extends AbstractAuthServerExecutor {
	
	private void makeErrorOutMessage(OutputMessage outObj, String errorMessge) throws MessageItemException {
		outObj.setAttribute("taskResult", "N");
		outObj.setAttribute("resultMessage", errorMessge);
		outObj.setAttribute("cntOfDriver", 0);
		outObj.setAttribute("cntOfFile", 0);
	}

	@Override
	protected void doTask(SocketChannel fromSC, InputMessage inObj,
			MessageMangerIF messageManger,			
			ClientResourceManagerIF clientResourceManager)
			throws MessageInfoNotFoundException, MessageItemException {

		String requestDirectory = (String)inObj.getAttribute("requestDirectory");
		
		// FIXME!
		log.info(String.format("requestDirectory=[%s]",  requestDirectory));
		
		
		
		OutputMessage outObj = messageManger.createOutputMessage("FileListResult");
		outObj.setAttribute("requestDirectory", requestDirectory);
		outObj.setAttribute("pathSeperator", File.separator);
		
		ClientResource clientResource = clientResourceManager.getClientResource(fromSC);
		
		if (!clientResource.isLogin()) {
			makeErrorOutMessage(outObj, "로그인 서비스입니다. 로그인을 해 주세요.");
			// letterToClientList.addLetterToClient(fromSC, outObj);
			sendSelf(outObj);
			return;
		}
		
		File workFile = null;
		try {
			workFile = new File(requestDirectory).getCanonicalFile();
		} catch (IOException e) {
			log.warn(String.format("IOException"), e);
			
			makeErrorOutMessage(outObj, "요청한 디렉토리가 존재하지 않습니다.");
			/*
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "요청한 디렉토리가 존재하지 않습니다.");
			outObj.setAttribute("cntOfDriver", 0);
			outObj.setAttribute("cntOfFile", 0);
			*/
			// outObj.getAttribute("fileList");
			
			// letterToClientList.addLetterToClient(fromSC, outObj);
			sendSelf(outObj);
			return;
		}
		
		if (!workFile.exists()) {
			/*
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "요청한 디렉토리가 존재하지 않습니다.");
			outObj.setAttribute("cntOfDriver", 0);
			outObj.setAttribute("cntOfFile", 0);
			*/
			makeErrorOutMessage(outObj, "요청한 디렉토리가 존재하지 않습니다.");
			// outObj.getAttribute("fileList");
			
			// letterToClientList.addLetterToClient(fromSC, outObj);
			sendSelf(outObj);
			return;
		}
		
		if (!workFile.isDirectory()) {
			/*
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "요청한 디렉토리명은 서버쪽에 파일로 디렉토리가 아닙니다.");
			outObj.setAttribute("cntOfDriver", 0);
			outObj.setAttribute("cntOfFile", 0);
			*/
			makeErrorOutMessage(outObj, "요청한 디렉토리명은 서버쪽에 파일로 디렉토리가 아닙니다.");
			// outObj.getAttribute("fileList");
			
			// letterToClientList.addLetterToClient(fromSC, outObj);
			sendSelf(outObj);
			return;
		}
		
		if (!workFile.canRead()) {
			/*
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "요청한 디렉토리를 읽을수가 없습니다. 서버쪽 파일시스템 읽기 권한을 확인해주세요.");
			outObj.setAttribute("cntOfDriver", 0);
			outObj.setAttribute("cntOfFile", 0);
			*/
			makeErrorOutMessage(outObj, "요청한 디렉토리를 읽을수가 없습니다. 서버쪽 파일시스템 읽기 권한을 확인해주세요.");
			// outObj.getAttribute("fileList");
			
			// letterToClientList.addLetterToClient(fromSC, outObj);
			sendSelf(outObj);
			return;
		}
		
		/** 파일 목록 검색 성공시 입력메시지로 부터 얻은 파일 목록을 요청한 디레토리명은 절대 경로로 변경한다. */
		try {
			workFile = workFile.getCanonicalFile();
		} catch (IOException e) {
			e.printStackTrace();
			/*
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "입력으로 들어온 상대 경로를 포함한 경로를 절대 경로로 변환할때 에러 발생");
			outObj.setAttribute("cntOfDriver", 0);
			outObj.setAttribute("cntOfFile", 0);
			*/
			makeErrorOutMessage(outObj, "입력으로 들어온 상대 경로를 포함한 경로를 절대 경로로 변환할때 에러 발생");
			// outObj.getAttribute("fileList");
			
			// letterToClientList.addLetterToClient(fromSC, outObj);
			sendSelf(outObj);
			return;
		}
				
		/** requestDirectory 를 입력값이 아닌 절대 경로로 재 설정 */
		outObj.setAttribute("requestDirectory", workFile.getAbsolutePath());
		outObj.setAttribute("taskResult", "Y");
		outObj.setAttribute("resultMessage", "파일 목록 검색 성공하였습니다.");
		
		String OSName = System.getProperty("os.name").toLowerCase();
		if (OSName.contains("win")) {
			File[] driverList = File.listRoots();
			outObj.setAttribute("cntOfDriver", driverList.length);
			
			ArrayData driverListOfOutObj = (ArrayData) outObj.getAttribute("driverList");
			
			for (int i=0; i <  driverList.length; i++) {
				ItemGroupDataIF driverOfOutObj = driverListOfOutObj.get(i);
				
				File driverFile = driverList[i];
				String driveName = driverFile.getAbsolutePath();
				
				driverOfOutObj.setAttribute("driverName", driveName);
			}
		} else {
			outObj.setAttribute("cntOfDriver", 0);
		}
		
		
		
		File[] subFiles = workFile.listFiles();
		
		if (null == subFiles) {
			outObj.setAttribute("cntOfFile", 0);
		} else {
			Arrays.sort(subFiles, new NameFirstComparator());
			Arrays.sort(subFiles, new DirectoryFirstComparator());
			
			/*
			int gap = 1;
			for (int i = 0; i < subFiles.length; i++) {
				// log.info(String.format("1. i=[%d] fileName=[%s]", i, subFiles[i].getName()));
				if (subFiles[i].isDirectory()) continue;
				for (int j = i+gap; j < subFiles.length; j++) {
					// log.info(String.format("2. i=[%d], j=[%d] fileName=[%s]", i, j, subFiles[j].getName()));
					if (subFiles[j].isDirectory()) {
						gap = j - i;
						File tmp = subFiles[j];
						int k=j;
						for (; k > i; k--) {
							// log.info(String.format("3. move %d to %d", k-1, k));
							subFiles[k] = subFiles[k-1]; 
						}
						subFiles[i] = tmp;
						// log.info(String.format("4. i=[%d], j=[%d], k=[%d]", i, j, k));
						break;
					}
				}
				// log.info(String.format("5. i=[%d] fileName=[%s]", i, subFiles[i].getName()));
			}
			*/
			
			outObj.setAttribute("cntOfFile", subFiles.length);
			
			ArrayData fileListOfOutObj = (ArrayData) outObj.getAttribute("fileList");
			
			for (int i=0; i <  subFiles.length; i++) {
				
				ItemGroupDataIF fileOfOutObj = fileListOfOutObj.get(i);
				
				fileOfOutObj.setAttribute("fileName", subFiles[i].getName());
				fileOfOutObj.setAttribute("fileSize", subFiles[i].length());
				/** 파일 종류, 1:디렉토리, 0:파일 */
				if (subFiles[i].isDirectory()) {
					fileOfOutObj.setAttribute("fileType", (byte)1);
				} else {
					fileOfOutObj.setAttribute("fileType", (byte)0);
				}
			}
		}
		
		// FIXME!
		log.info(String.format("out.requestDirectory=[%s]",  (String)outObj.getAttribute("requestDirectory")));

		// letterToClientList.addLetterToClient(fromSC, outObj);
		sendSelf(outObj);
	}
}
