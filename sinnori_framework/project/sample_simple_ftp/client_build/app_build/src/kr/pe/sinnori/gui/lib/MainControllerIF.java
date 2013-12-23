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

package kr.pe.sinnori.gui.lib;

import kr.pe.sinnori.common.message.OutputMessage;

/**
 * 연결 성공일 경우 인터페이스
 * @author Jonghoon Won
 *
 */
public interface MainControllerIF {
	/**
	 * @return 파일 블락 크기, 설정파일 데이터 패킷 버퍼 크기에서 1024 bytes 를 뺀 값이다.  
	 */
	public int getFileBlockSize();
	
	/**
	 * 로그인 성공, 연결 화면에서 파일 송수신 화면으로 전환한다.
	 */
	public void loginOK();
	/**
	 * 지정된 호스트, 포트로 접속하여 공개키 데이터를 받는다.
	 * @param host 호스트명
	 * @param port 포트
	 * @return 공개키
	 */
	public byte[] connectServer(String host, int port);
	
	/**
	 * @return 이진 공개키 출력 메시지
	 */
	public OutputMessage getBinaryPublicKey();
	
	/**
	 * 로그인하여 성공여부를 반환한다.
	 * @param id 아이디
	 * @param pwd 비밀번호
	 * @return 로그인 성공 여부
	 */
	public boolean login(String id, String pwd);
	
	/**
	 * 지정된 서버쪽 디렉토리의 파일 목록이 담긴 출력 메시지를 반환한다. 
	 * @param requestDirectory 파일 목록을 가져올 디렉토리
	 * @return 파일 목록(=FileListResult) 출력 메시지
	 */
	public OutputMessage getRemoteFileList(String requestDirectory);
	
	/**
	 * 서버에 업로드 파일 준비하라는 메시지를 보낸다.
	 * @param localFilePathName 로컬 파일 경로
	 * @param localFileName 로컬 파일 이름
	 * @param localFileSize 로컬 파일 크기
	 * @param remoteFilePathName 원격지 파일 경로
	 * @param remoteFileName 원격지 파일 이름, 없다면 로컬 파일 이름으로 대체된다.
	 * @param fileBlockSize 송수신 파일 조각 크기
	 * @return 업로드 준비 출력 메시지
	 */
	public OutputMessage readyUploadFile(String localFilePathName, String localFileName, long localFileSize, 
			String remoteFilePathName, String remoteFileName, int fileBlockSize);
	
	/**
	 * {@link #readyUploadFile(String, String, long, String, String, int)} 에서 락을 걸은 로컬 원본 파일 자원을 해제한다.
	 */
	public void freeLocalSourceFileResource();
	
	/**
	 * 업로드 준비된 서버측에 업로드할 로컬 파일 조각을 담음 메시지를 보낸다. 
	 * @param serverTargetFileID 서버 송수신 자원 파일 식별자 
	 * @param fileBlockNo 파일 조각 번호
	 * @param fileData 파일 조각 데이터
	 * @return 업로드 조각 전송 출력 메시지
	 */
	public OutputMessage doUploadFile(int serverTargetFileID, int fileBlockNo, byte[] fileData);
	
	/**
	 * 서버에 다운로드 파일 준비를 하라는 메시지를 보낸다.
	 * @param localFilePathName 로컬 파일 경로
	 * @param localFileName 로컬 파일 이름
	 * @param remoteFilePathName 원격지 파일 경로
	 * @param remoteFileName 원격지 파일 이름
	 * @param remoteFileSize 원격지 파일 크기
	 * @param fileBlockSize 송수신 파일 조각 크기
	 * @return 파일 다운로드 준비 출력 메시지
	 */
	public OutputMessage readyDownloadFile(String localFilePathName, String localFileName, 
			String remoteFilePathName, String remoteFileName, long remoteFileSize, int fileBlockSize);
	
	/**
	 * {@link #readyDownloadFile(String, String, String, String, long, int)} 에서 락을 걸은 로컬 목적지 파일 자원을 해제한다.
	 */
	public void freeLocalTargetFileResource();
	
	/**
	 * 다운로드 준비된 서버측에 다운 로드 파일 조각을 요구하는 메시지를 보낸다.
	 * @param serverSourceFileID 클라이언트 송수신 자원 파일 식별자
	 * @param fileBlockNo 파일 조각 번호
	 * @return 파일 다운로드 요청 출력 메시지 
	 */
	public OutputMessage doDownloadFile(int serverSourceFileID, int fileBlockNo);
	
	
	public OutputMessage doDownloadFileAll();
	
	/**
	 * 파일 업로드 진행 상태 모달 윈도우를 띄운다. 내부적으로 "파일 업로드 진행 작업 쓰레드" 를 수행한다. 
	 * @param serverTargetFileID 서버 목적지 파일 식별자
	 * @param mesg 메시지
	 * @param fileSize 파일 크기
	 */
	public void openUploadProcessDialog(int serverTargetFileID, String mesg, long fileSize);
	
	/**
	 * 파일 업로드 진행 작업 쓰레드 종료후 호출 되는 메소드이다.
	 */
	public void endUploadTask();
	
	/**
	 * 파일 다운로드 진행 상태 모달 윈도우를 띄운다. 내부적으로 "파일 다운로드 진행 작업 쓰레드" 를 수행한다.
	 * @param serverSourceFileID
	 * @param mesg 메시지
	 * @param fileSize 전송할 파일 크기
	 */
	public void openDownloadProcessDialog(int serverSourceFileID, String mesg, long fileSize);
	/**
	 * 파일 다운로드 진행 작업 쓰레드 종료후 호출 되는 메소드이다.
	 */
	public void endDownloadTask();
	
	
	/**
	 * <pre>
	 * 전송 받은 파일 조각 크기를 "파일 전송 현황 창"에 알린다. 
	 * 이때  "파일 전송 현황 창" 준비 여부를 반환한다.
	 * 
	 * 파일 조각을 송신 혹은 수신하는 측에서는 
	 * "파일 전송 현황 창" 에서 사용자가 취소 할 경우 파일 조각 송신 혹은 수신을 중단해야 한다.
	 * 그렇기때문에 파일 조각을 송수신하는 측에서 파일 조각 크기를 "파일 전송 현황 창"에 알려줄때,
	 * 취소 이벤트 발생 여부를 받아서 이를 근거로 파일 조각 송수신을 중단하게 한다.
	 * 전제 조건 :   "파일 전송 현황 창" 준비여부가 취소 이벤트 발생 여부가 될려면 다음과 같은 조건을 만족해야한다. 
	 * 첫번째 "파일 전송 현황 창" 오픈후 파일 조각 송수신 혹은 파일 종료가 일어남을 보장해야 한다.
	 * 두번째 "파일 전송 현황 창" 오픈, 파일 조각 송수신, 종료는 각각 다른 쓰레드에서 수행되어도 되지만, 
	 * 오직 전담 쓰레드에서 수행되어야 하며 오픈과 종료는 딱 1번 수행되어 한다.
	 * 다시말하자면 병렬 수행 금지이다.  
	 * 
	 *  </pre>    
	 * @param receivedDataSize 전송 받은 파일 조각 크기
	 */
	// public void noticeAddingFileDataToFileTransferProcessDialog(int receivedDataSize);

	
	/**
	 * 서버에 업로드 취소를 알린다.
	 * @return 서버에 업로드 취소 결과 출력 메시지
	 */
	public OutputMessage cancelUploadFile();
	
	/**
	 * 서버에 다운 로드 취소를 알린다.
	 * @return 서버에 다운로드 취소 결과 출력 메시지
	 */
	public OutputMessage cancelDownloadFile();
	
	/**
	 * 익명 메시지 처리
	 * @param projectName 프로젝트 이름
	 * @param outObj 출력 메시지
	 */
	public void doAnonymousServerMessageTask(String projectName, OutputMessage outObj);
}
