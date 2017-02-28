/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kr.pe.sinnori.impl.servertask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.util.NameFirstComparator;
import kr.pe.sinnori.impl.message.FileListRequest.FileListRequest;
import kr.pe.sinnori.impl.message.FileListResult.FileListResult;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractAuthServerExecutor;
import kr.pe.sinnori.server.executor.LetterSender;

/**
 * 
 * @author "Won Jonghoon"
 *
 */
public class FileListRequestServerTask extends AbstractAuthServerExecutor {

	@Override
	public void doTask(String projectName,
			LoginManagerIF loginManager,
			LetterSender letterSender, AbstractMessage inObj)
			throws Exception {
		doWork(projectName, letterSender, (FileListRequest)inObj);
	}
	
	private void doWork(String projectName,
			LetterSender letterSender, FileListRequest inObj)
			throws Exception {
		// FIXME!
		log.info(inObj.toString());
		
		// String requestDirectory = (String)inObj.getAttribute("requestDirectory");
		String requestDirectory = inObj.getRequestDirectory();
		if (null == requestDirectory) {
			String errorMessage = String.format("FileListRequest 메시지의 '요청 디렉토리명'(requestDirectory) 값이 null 입니다.");
			throw new ServerTaskException(errorMessage);
		}
		
		// FIXME!
		log.info(String.format("requestDirectory=[%s]",  requestDirectory));
		
		
		FileListResult outObj = new FileListResult();
		outObj.setRequestDirectory(requestDirectory);
		outObj.setPathSeperator(File.separator);
		/*OutputMessage outObj = messageManger.createOutputMessage("FileListResult");
		outObj.setAttribute("requestDirectory", requestDirectory);
		outObj.setAttribute("pathSeperator", File.separator);*/		
		
		File workFile = new File(requestDirectory);
		
		if (!workFile.exists()) {
			/*
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "요청한 디렉토리가 존재하지 않습니다.");
			outObj.setAttribute("cntOfDriver", 0);
			outObj.setAttribute("cntOfFile", 0);
			*/
			/*makeErrorOutMessage(outObj, "요청한 디렉토리가 존재하지 않습니다.");
			// outObj.getAttribute("fileList");
			
			// letterToClientList.addLetterToClient(fromSC, outObj);
			letterSender.sendSync(outObj);*/
			
			outObj.setTaskResult("N");
			outObj.setResultMessage("요청한 디렉토리가 존재하지 않습니다.");
			outObj.setCntOfDriver(0);
			outObj.setCntOfFile(0);
			letterSender.addSyncMessage(outObj);
			return;
		}
		
		if (!workFile.isDirectory()) {
			/*
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "요청한 디렉토리명은 서버쪽에 파일로 디렉토리가 아닙니다.");
			outObj.setAttribute("cntOfDriver", 0);
			outObj.setAttribute("cntOfFile", 0);
			*/
			/*makeErrorOutMessage(outObj, "요청한 디렉토리명은 서버쪽에 파일로 디렉토리가 아닙니다.");
			// outObj.getAttribute("fileList");
			
			// letterToClientList.addLetterToClient(fromSC, outObj);
			letterSender.sendSync(outObj);*/
			
			outObj.setTaskResult("N");
			outObj.setResultMessage("요청한 디렉토리명은 디렉토리가 아닙니다.");
			outObj.setCntOfDriver(0);
			outObj.setCntOfFile(0);
			letterSender.addSyncMessage(outObj);
			return;
		}
		
		if (!workFile.canRead()) {
			/*
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "요청한 디렉토리를 읽을수가 없습니다. 서버쪽 파일시스템 읽기 권한을 확인해주세요.");
			outObj.setAttribute("cntOfDriver", 0);
			outObj.setAttribute("cntOfFile", 0);
			*/
			/*makeErrorOutMessage(outObj, "요청한 디렉토리를 읽을수가 없습니다. 서버쪽 파일시스템 읽기 권한을 확인해주세요.");
			// outObj.getAttribute("fileList");
			
			// letterToClientList.addLetterToClient(fromSC, outObj);
			letterSender.sendSync(outObj);*/
			
			outObj.setTaskResult("N");
			outObj.setResultMessage("요청한 디렉토리를 읽을수가 없습니다. 서버쪽 파일시스템 읽기 권한을 확인해주세요.");
			outObj.setCntOfDriver(0);
			outObj.setCntOfFile(0);
			letterSender.addSyncMessage(outObj);
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
			/*makeErrorOutMessage(outObj, "입력으로 들어온 상대 경로를 포함한 경로를 절대 경로로 변환할때 에러 발생");
			// outObj.getAttribute("fileList");
			
			// letterToClientList.addLetterToClient(fromSC, outObj);
			letterSender.sendSync(outObj);*/
			
			outObj.setTaskResult("N");
			outObj.setResultMessage("입력으로 들어온 경로를 절대 경로로 변환할때 에러 발생");
			outObj.setCntOfDriver(0);
			outObj.setCntOfFile(0);
			letterSender.addSyncMessage(outObj);
			return;
		}
				
		/** requestDirectory 를 입력값이 아닌 절대 경로로 재 설정 */
		/*outObj.setAttribute("requestDirectory", workFile.getAbsolutePath());
		outObj.setAttribute("taskResult", "Y");
		outObj.setAttribute("resultMessage", "파일 목록 검색 성공하였습니다.");*/
		outObj.setRequestDirectory(workFile.getAbsolutePath());
		outObj.setTaskResult("Y");
		outObj.setResultMessage("파일 목록 검색 성공하였습니다.");
		
		String OSName = System.getProperty("os.name").toLowerCase();
		if (OSName.contains("win")) {
			File[] realDriverList = File.listRoots();
			outObj.setCntOfDriver(realDriverList.length);
			List<FileListResult.Driver> driverList = new ArrayList<FileListResult.Driver>();
			outObj.setDriverList(driverList);
			// ArrayData driverListOfOutObj = (ArrayData) outObj.getAttribute("driverList");
			
			for (int i=0; i <  realDriverList.length; i++) {
				File driverFile = realDriverList[i];
				String driverName = driverFile.getAbsolutePath();
				FileListResult.Driver driver = new FileListResult.Driver();
				driver.setDriverName(driverName);
				driverList.add(driver);
			}
		} else {
			// outObj.setAttribute("cntOfDriver", 0);
			outObj.setCntOfDriver(0);
		}
		
		File[] subFiles = workFile.listFiles();
		
		if (null == subFiles) {
			// outObj.setAttribute("cntOfFile", 0);
			outObj.setCntOfFile(0);
		} else {
			Arrays.sort(subFiles, new NameFirstComparator());
			List<File> fileList = new ArrayList<File>();
			List<File> directoryList = new ArrayList<File>();
			for (File subFile : subFiles) {
				if (workFile.isFile()) {
					fileList.add(subFile);
				} else {
					directoryList.add(subFile);
				}
			}
					
			outObj.setCntOfFile(subFiles.length);
			// outObj.setAttribute("cntOfFile", subFiles.length);
			
			// ArrayData fileListOfOutObj = (ArrayData) outObj.getAttribute("fileList");
			
			List<FileListResult.File> allFileList = new ArrayList<FileListResult.File>();
			outObj.setFileList(allFileList);
			
			for (File subFile : directoryList) {
				FileListResult.File fileOfFileListResult = new FileListResult.File();
				
				fileOfFileListResult.setFileName(subFile.getName());
				fileOfFileListResult.setFileSize(0L);
				/** 파일 종류, 1:디렉토리, 0:파일 */
				fileOfFileListResult.setFileType((byte)1);
				
				allFileList.add(fileOfFileListResult);
			}
			
			for (File subFile : fileList) {
				FileListResult.File fileOfFileListResult = new FileListResult.File();
				
				fileOfFileListResult.setFileName(subFile.getName());
				fileOfFileListResult.setFileSize(subFile.length());
				/** 파일 종류, 1:디렉토리, 0:파일 */
				fileOfFileListResult.setFileType((byte)0);
				
				allFileList.add(fileOfFileListResult);
			}			
		}
		
		// FIXME!
		// log.info(String.format("out.requestDirectory=[%s]",  (String)outObj.getAttribute("requestDirectory")));
		log.info(String.format("out.requestDirectory=[%s]",  outObj.getRequestDirectory()));

		// letterToClientList.addLetterToClient(fromSC, outObj);
		// letterSender.sendSync(outObj);
		letterSender.addSyncMessage(outObj);
	}
}
