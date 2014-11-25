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

package kr.pe.sinnori.common.updownfile;

import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.lib.CommonRootIF;

/**
 * <pre>
 * 로컬 목적지 파일을 원할하게 수신하기 위한 로컬 목적지 파일 자원 큐 관리자 클래스. 
 * 주) 느슷한 구조의 큐 자원 관리자로 통제된 사용 방법외 방법으로 사용시 비 정상 동작한다.
 * </pre>
 * @author Won Jonghoon
 *
 */
public class LocalTargetFileResourceManager implements CommonRootIF {
	private final Object monitor = new Object(); 
	
	private LinkedBlockingQueue<LocalTargetFileResource> localTargetFileResourceQueue  = null;
	private Hashtable<Integer, LocalTargetFileResource> localTargetFileResourceHash = null;
	
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스
	 */
	private static final class LocalTargetFileResourceManagerHolder {
		static final LocalTargetFileResourceManager singleton = new LocalTargetFileResourceManager();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 구현 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static LocalTargetFileResourceManager getInstance() {
		return LocalTargetFileResourceManagerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 */
	private LocalTargetFileResourceManager() {
		int localTargetFileResourceCnt = (Integer)conf.getResource("common.updownfile.local_target_file_resource_cnt.value");
		
		
		localTargetFileResourceQueue = new LinkedBlockingQueue<LocalTargetFileResource>(localTargetFileResourceCnt);
		localTargetFileResourceHash = new Hashtable<Integer, LocalTargetFileResource>();
		
		for (int i=0; i < localTargetFileResourceCnt; i++) {
			localTargetFileResourceQueue.add(new LocalTargetFileResource(i));
		}
	}
	
	/**
	 * 큐에서 파일 자원 관리자를 받아서 소스 파일을 받을 목적지 파일을 준비시킨후 반환한다.
	 * 
	 * @param append 이어받기 여부
	 * @param sourceFilePathName 소스 파일 경로 이름
	 * @param sourceFileName 소스 파일명
	 * @param sourceFileSize 소스 파일 크기
	 * @param targetFilePathName 목적지 파일 경로 이름
	 * @param targetFileName 목적지 파일 이름
	 * @param targetFileSize 목적지 파일 크기
	 * @param fileBlockSize 송수신 파일 조각 크기 
	 * @return 소스 파일을 받을 목적지 파일을 준비된 파일 자원 관리자
	 * @throws IllegalArgumentException 잘못된 파라미터 입력시 던지는 예외
	 * @throws UpDownFileException 파일 송수신과 관련된 파일 관련 작업시 발생한 에러
	 */
	public LocalTargetFileResource pollLocalTargetFileResource(boolean append,
			String sourceFilePathName, String sourceFileName, long sourceFileSize,
			String targetFilePathName,String targetFileName, long targetFileSize, 
			int fileBlockSize) throws IllegalArgumentException, UpDownFileException {
		
		LocalTargetFileResource localTargetFileResource = null;
		synchronized (monitor) {
			localTargetFileResource = localTargetFileResourceQueue.poll();
			if (null == localTargetFileResource) return null;
			localTargetFileResource.queueOut();
			localTargetFileResourceHash.put(localTargetFileResource.getTargetFileID(), localTargetFileResource);
		}
		
		localTargetFileResource.readyWritingFile(append, sourceFilePathName, sourceFileName, sourceFileSize, 
				targetFilePathName, targetFileName, targetFileSize, fileBlockSize);
		return localTargetFileResource;
	}

	/**
	 * "로컬 목적 파일 자원 큐" 로 로컬 목적 파일 자원을 반환한다. 
	 * @param localTargetFileResource 반환할 로컬 목적 파일 자원
	 */
	public void putLocalTargetFileResource(LocalTargetFileResource localTargetFileResource) {
		if (null == localTargetFileResource) return;

		/**
		 * 2번 연속 반환 막기
		 */
		synchronized (monitor) {
			if (localTargetFileResource.isInQueue()) {
				log.warn(String.format("clientFileID[%d] 파일 업로드 자원 2번 연속 반환 시도", localTargetFileResource.getTargetFileID()));
				return;
			}
			localTargetFileResource.releaseFileLock();
			
			localTargetFileResource.queueIn();
			
			localTargetFileResourceHash.remove(localTargetFileResource.getTargetFileID());
			localTargetFileResourceQueue.add(localTargetFileResource);
		}
		
		// FIXME! 잠시 디버깅을 위해서 리소스 자원 많이 잡는 Throwable 객체 생성. 나중 삭제해야함.
		// Throwable t = new Throwable();
		log.info(String.format("localTargetFileID[%d] 큐 반환", localTargetFileResource.getTargetFileID()));
	}
	
	/**
	 * 목적 파일 식별자에 1:1 대응하는 로컬 목적 파일 자원을 반환한다.
	 * @param targetFileID 목적 파일 식별자
	 * @return 목적 파일 식별자에 1:1 대응하는 로컬 목적 파일 자원, 할당 받은 로컬 목적 파일 자원들중 목적 파일 식별자를 갖는것이 없을 경우 null 를 반환한다.
	 */
	public LocalTargetFileResource getLocalTargetFileResource(int targetFileID) {
		// synchronized (monitor) {
			LocalTargetFileResource localTargetFileResource = localTargetFileResourceHash.get(targetFileID);
			return localTargetFileResource;
		// }
	}
}
