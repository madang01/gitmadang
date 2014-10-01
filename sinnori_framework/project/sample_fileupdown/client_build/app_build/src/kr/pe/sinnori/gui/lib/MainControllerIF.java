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

import kr.pe.sinnori.impl.message.CancelDownloadFileResult.CancelDownloadFileResult;
import kr.pe.sinnori.impl.message.CancelUploadFileResult.CancelUploadFileResult;
import kr.pe.sinnori.impl.message.DownFileInfoResult.DownFileInfoResult;
import kr.pe.sinnori.impl.message.FileListResult.FileListResult;
import kr.pe.sinnori.impl.message.UpFileInfoResult.UpFileInfoResult;


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
	 * 지정된 호스트, 포트로 접속하여 공개키 데이터를 받는다. 공개키를 요구하여 성공시에는 공개키를 실패시 null 을 반환한다.
	 * @param host 호스트명
	 * @param port 포트
	 * @return 공개키를 요구하여 성공시에는 공개키를 실패시 null 을 반환한다.
	 */
	public byte[] getBinaryPublicKey(String host, int port);
	
	/**
	 * 로그인하여 성공여부를 반환한다.
	 * @param id 아이디
	 * @param pwd 비밀번호
	 */
	public void login(String id, String pwd);
	
	
	
	/**
	 * 지정된 서버쪽 디렉토리의 파일 목록이 담긴 출력 메시지를 반환한다. 
	 * @param requestDirectory 파일 목록을 가져올 디렉토리
	 * @return 파일 목록(=FileListResult) 출력 메시지
	 */
	public FileListResult getRemoteFileList(String requestDirectory);
	
	/**
	 * 서버에 업로드 파일 준비하라는 메시지를 보낸다.
	 * @param append 덧붙이기 여부, true : 덧붙이기(=append), false : 덮어쓰기(=overwrite) 
	 * @param localFilePathName 로컬 파일 경로
	 * @param localFileName 로컬 파일 이름
	 * @param localFileSize 로컬 파일 크기
	 * @param remoteFilePathName 원격지 파일 경로
	 * @param remoteFileName 원격지 파일 이름, 없다면 로컬 파일 이름으로 대체된다.
	 * @param remoteFileSize 원격지 파일 크기
	 * @param fileBlockSize 송수신 파일 조각 크기
	 * @return 업로드 준비 출력 메시지
	 */
	public UpFileInfoResult readyUploadFile(boolean append, 
			String localFilePathName, String localFileName, long localFileSize, 
			String remoteFilePathName, String remoteFileName, long remoteFileSize,
			int fileBlockSize);
	
	/**
	 * {@link #readyUploadFile(String, String, long, String, String, int)} 에서 락을 걸은 로컬 원본 파일 자원을 해제한다.
	 */
	public void freeLocalSourceFileResource();
	
	
	
	/**
	 * 서버에 다운로드 파일 준비를 하라는 메시지를 보낸다.
	 * @param append 덧붙이기 여부, true : 덧붙이기(=append), false : 덮어쓰기(=overwrite)
	 * @param localFilePathName 로컬 파일 경로
	 * @param localFileName 로컬 파일 이름
	 * @param localFileSize 로컬 파일 크기
	 * @param remoteFilePathName 원격지 파일 경로
	 * @param remoteFileName 원격지 파일 이름
	 * @param remoteFileSize 원격지 파일 크기
	 * @param fileBlockSize 송수신 파일 조각 크기
	 * @return 파일 다운로드 준비 출력 메시지
	 */
	public DownFileInfoResult readyDownloadFile(boolean append, 
			String localFilePathName, String localFileName, long localFileSize, 
			String remoteFilePathName, String remoteFileName, long remoteFileSize, 
			int fileBlockSize);
	
	/**
	 * @return 로컬 목적지 파일 크기 재조정 실패여부, 참고) 만약 중복 받기이면 0으로 이어받기이면 아무 동작 안한다.
	 */
	public boolean makeZeroToDownloadFileSize();
	
	/**
	 * 다운로드할 목적지 파일 자원을 해제한다. 서버에 파일 다운로드 준비를 요청하여 실패시에만 호출된다. 
	 */
	public void freeLocalTargetFileResource();
	
	
	
	/**
	 * 파일 업로드 진행 상태 모달 윈도우를 띄운다. 내부적으로 "파일 업로드 진행 작업 쓰레드" 를 수행한다. 
	 * @param serverTargetFileID 서버 목적지 파일 식별자
	 * @param mesg 메시지
	 * @param fileSize 파일 크기 
	 * @param totalReceivedDataSize 이미 받은 데이터 크기
	 */
	public void openUploadProcessDialog(int serverTargetFileID, String mesg, long fileSize, long totalReceivedDataSize);
	
	/**
	 * 파일 업로드 진행 작업 쓰레드 종료후 호출 되는 메소드이다.
	 */
	public void endUploadTask();
	
	/**
	 * 파일 다운로드 진행 상태 모달 윈도우를 띄운다. 내부적으로 "파일 다운로드 진행 작업 쓰레드" 를 수행한다.
	 * @param serverSourceFileID
	 * @param mesg 메시지
	 * @param fileSize 전송할 파일 크기
	 * @param totalReceivedDataSize 이미 받은 데이터 크기
	 */
	public void openDownloadProcessDialog(int serverSourceFileID, String mesg, long fileSize, long totalReceivedDataSize);

	/**
	 * 파일 다운로드 진행 작업 쓰레드 종료후 호출 되는 메소드이다.
	 */
	public void endDownloadTask();
	
	/**
	 * 서버에 업로드 취소를 알린다.
	 * @return 서버에 업로드 취소 결과 출력 메시지
	 */
	public CancelUploadFileResult cancelUploadFile();
	
	/**
	 * 서버에 다운로드 취소를 알린다.
	 * @return 서버에 다운로드 취소 결과 출력 메시지
	 */
	public CancelDownloadFileResult cancelDownloadFile();
	
	
}