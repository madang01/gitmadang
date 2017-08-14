package kr.pe.sinnori.impl.servertask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.util.NameFirstComparator;
import kr.pe.sinnori.impl.message.FileListRes.FileListRes;
import kr.pe.sinnori.impl.message.FileListReq.FileListReq;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractAuthServerExecutor;
import kr.pe.sinnori.server.executor.LetterSender;

public class FileListReqServerTask extends AbstractAuthServerExecutor {

	@Override
	public void doTask(String projectName,
			LoginManagerIF loginManager,
			LetterSender letterSender, AbstractMessage requestMessage)
			throws Exception {
		doWork(projectName, letterSender, (FileListReq)requestMessage);
	}
	
	 
	
	private void doWork(String projectName,
			LetterSender letterSender, FileListReq fileListReq)
			throws Exception {
		// FIXME!
		log.info(fileListReq.toString());
		
		// String requestDirectory = (String)fileListReq.getAttribute("requestDirectory");
		String requestPathName = fileListReq.getRequestPathName();
		if (null == requestPathName) {
			String errorMessage = String.format("FileListReq 메시지의 '요청 디렉토리명'(requestDirectory) 값이 null 입니다.");
			throw new ServerTaskException(errorMessage);
		}
		
		// FIXME!
		log.info(String.format("requestDirectory=[%s]",  requestPathName));
		
		
		
		/*OutputMessage fileListRes = messageManger.createOutputMessage("FileListRes");
		fileListRes.setAttribute("requestDirectory", requestDirectory);
		fileListRes.setAttribute("pathSeperator", File.separator);*/		
		
		File requestPath = new File(requestPathName);
		
		if (!requestPath.exists()) {
			sendErrorFileList(letterSender, requestPathName, "요청한 디렉토리가 존재하지 않습니다");
			return;
		}
		
		if (!requestPath.isDirectory()) {
			sendErrorFileList(letterSender, requestPathName, "요청한 디렉토리명은 디렉토리가 아닙니다");
			return;
		}
		
		if (!requestPath.canRead()) {
			sendErrorFileList(letterSender, requestPathName, "요청한 디렉토리를 읽을수가 없습니다. 서버쪽 파일시스템 읽기 권한을 확인해주세요");
			return;
		}
		
		/** 파일 목록 검색 성공시 입력메시지로 부터 얻은 파일 목록을 요청한 디레토리명은 절대 경로로 변경한다. */
		String canonicalPathNameOfRequestPath = null;
		try {
			canonicalPathNameOfRequestPath = requestPath.getCanonicalPath();
		} catch (IOException e) {
			log.warn("fail to get the canonical file", e);
			
			sendErrorFileList(letterSender, requestPathName, "입력으로 들어온 경로를 절대 경로로 변환할때 에러 발생");
			return;
		}
				
		/** requestDirectory 를 입력값이 아닌 절대 경로로 재 설정 */
		FileListRes fileListRes = new FileListRes();
		fileListRes.setRequestPathName(canonicalPathNameOfRequestPath);
		fileListRes.setIsSuccess("Y");
		fileListRes.setResultMessage("파일 목록 검색 성공하였습니다.");
		fileListRes.setPathSeperator(File.separator);
		
		String OSName = System.getProperty("os.name").toLowerCase();
		if (OSName.contains("win")) {
			File[] realDriverList = File.listRoots();
			fileListRes.setCntOfDriver(realDriverList.length);
			List<FileListRes.Driver> driverList = new ArrayList<FileListRes.Driver>();
			fileListRes.setDriverList(driverList);
			// ArrayData driverListOfOutObj = (ArrayData) fileListRes.getAttribute("driverList");
			
			for (int i=0; i <  realDriverList.length; i++) {
				File driverFile = realDriverList[i];
				String driverName = driverFile.getAbsolutePath();
				FileListRes.Driver driver = new FileListRes.Driver();
				driver.setDriverName(driverName);
				driverList.add(driver);
			}
		} else {
			fileListRes.setCntOfDriver(0);
		}
		
		File[] subFiles = requestPath.listFiles();
		
		if (null == subFiles) {
			// fileListRes.setAttribute("cntOfFile", 0);
			fileListRes.setCntOfChildFile(0);
		} else {
			Arrays.sort(subFiles, new NameFirstComparator());
			List<File> fileList = new ArrayList<File>();
			List<File> directoryList = new ArrayList<File>();
			for (File subFile : subFiles) {
				// log.info("subFile name[{}] length[{}] isDirectory[{}]", subFile.getName(), subFile.length(), subFile.isDirectory());
								
				if (subFile.isDirectory()) {
					directoryList.add(subFile);
				} else {
					fileList.add(subFile);
				}
			}
					
			fileListRes.setCntOfChildFile(subFiles.length);
			// fileListRes.setAttribute("cntOfFile", subFiles.length);
			
			// ArrayData fileListOfOutObj = (ArrayData) fileListRes.getAttribute("fileList");
			
			List<FileListRes.ChildFile> allChildFileList = new ArrayList<FileListRes.ChildFile>();
			
			
			for (File subFile : directoryList) {
				FileListRes.ChildFile fileOfFileListRes = new FileListRes.ChildFile();
				
				fileOfFileListRes.setFileName(subFile.getName());
				fileOfFileListRes.setFileSize(0L);
				/** 파일 종류, 1:디렉토리, 0:파일 */
				fileOfFileListRes.setFileType((byte)1);
				
				allChildFileList.add(fileOfFileListRes);
			}
			
			for (File subFile : fileList) {
				FileListRes.ChildFile fileOfFileListRes = new FileListRes.ChildFile();
				
				fileOfFileListRes.setFileName(subFile.getName());
				fileOfFileListRes.setFileSize(subFile.length());
				/** 파일 종류, 1:디렉토리, 0:파일 */
				fileOfFileListRes.setFileType((byte)0);
				
				allChildFileList.add(fileOfFileListRes);
			}
			
			fileListRes.setChildFileList(allChildFileList);
		}
		
		// FIXME!
		// log.info(String.format("out.requestDirectory=[%s]",  (String)fileListRes.getAttribute("requestDirectory")));
		
		letterSender.addSyncMessage(fileListRes);
	}



	private void sendErrorFileList(LetterSender letterSender, String requestPathName, String errorMessage) {
		FileListRes fileListRes = new FileListRes();
		fileListRes.setRequestPathName(requestPathName);
		fileListRes.setPathSeperator(File.separator);
		fileListRes.setIsSuccess("N");
		fileListRes.setResultMessage(errorMessage);
		fileListRes.setCntOfDriver(0);
		fileListRes.setCntOfChildFile(0);
		letterSender.addSyncMessage(fileListRes);
	}

}
