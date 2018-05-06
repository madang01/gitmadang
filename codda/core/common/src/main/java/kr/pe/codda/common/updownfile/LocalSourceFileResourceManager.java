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

package kr.pe.codda.common.updownfile;

import java.util.HashMap;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.config.itemvalue.CommonPartConfiguration;
import kr.pe.codda.common.exception.UpDownFileException;

/**
 * <pre>
 * 로컬 원본 파일을 원할하게 송신하기 위한 로컬 원본 파일 자원 큐 관리자 클래스.
 * 주) 느슷한 구조의 큐 자원 관리자로 통제된 사용 방법외 방법으로 사용시 비 정상 동작한다.
 * </pre>
 * 
 * @author Won Jonghoon
 *
 */
public class LocalSourceFileResourceManager {
	private InternalLogger log = InternalLoggerFactory.getInstance(LocalSourceFileResourceManager.class);

	private final Object monitor = new Object();

	private int sourceFileID = Integer.MAX_VALUE;

	private int localSourceFileResourceCnt = -1;

	private HashMap<Integer, LocalSourceFileResource> localSourceFileResourceHash = null;

	private HashMap<String, Integer> ownerID2SourceFileIDHash = new HashMap<String, Integer>();

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스
	 */
	private static final class LocalSourceFileResourceManagerHolder {
		static final LocalSourceFileResourceManager singleton = new LocalSourceFileResourceManager();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 구현 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static LocalSourceFileResourceManager getInstance() {
		return LocalSourceFileResourceManagerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 */
	private LocalSourceFileResourceManager() {
		// int localSourceFileResourceCnt =
		// (Integer)conf.getResource("common.updownfile.local_source_file_resource_cnt.value");

		CoddaConfiguration sinnoriRunningProjectConfiguration = CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();
		CommonPartConfiguration commonPart = sinnoriRunningProjectConfiguration.getCommonPartConfiguration();

		localSourceFileResourceCnt = commonPart.getLocalSourceFileResourceCnt();

		localSourceFileResourceHash = new HashMap<Integer, LocalSourceFileResource>();

	}

	/**
	 * 큐에서 파일 자원 관리자를 받아서 소스 파일을 받을 목적지 파일을 준비시킨후 반환한다.
	 * 
	 * @param append
	 *            이어받기 여부
	 * @param sourceFilePathName
	 *            소스 파일 경로 이름
	 * @param sourceFileName
	 *            소스 파일명
	 * @param sourceFileSize
	 *            소스 파일 크기
	 * @param targetFilePathName
	 *            목적지 파일 경로 이름
	 * @param targetFileName
	 *            목적지 파일 이름
	 * @param targetFileSize
	 *            목적지 파일 크기
	 * @param fileBlockSize
	 *            송수신 파일 조각 크기
	 * @return 소스 파일을 받을 목적지 파일을 준비된 파일 자원 관리자
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 던지는 예외
	 * @throws UpDownFileException
	 *             파일 송수신과 관련된 파일 관련 작업시 발생한 에러
	 */
	public LocalSourceFileResource registerNewLocalSourceFileResource(String ownerID, boolean append,
			String sourceFilePathName, String sourceFileName, long sourceFileSize, String targetFilePathName,
			String targetFileName, long targetFileSize, int fileBlockSize)
			throws IllegalArgumentException, UpDownFileException {

		synchronized (monitor) {

			int localSourceFileResourceHashSize = localSourceFileResourceHash.size();
			if (localSourceFileResourceHashSize >= localSourceFileResourceCnt) {
				log.info(
						"환경 변수 '로컬 원본 파일 자원 갯수'(=common.updownfile.local_source_file_resource_cnt.value 만큼 사용중으로 더 이상 생성할 수 없습니다.");
				return null;
			}

			if (Integer.MAX_VALUE == sourceFileID) {
				sourceFileID = Integer.MIN_VALUE;
			} else {
				sourceFileID++;
			}

			LocalSourceFileResource localSourceFileResource =

					new LocalSourceFileResource(ownerID, sourceFileID, append, sourceFilePathName, sourceFileName,
							sourceFileSize, targetFilePathName, targetFileName, targetFileSize, fileBlockSize);

			localSourceFileResourceHash.put(sourceFileID, localSourceFileResource);
			ownerID2SourceFileIDHash.put(ownerID, sourceFileID);
			return localSourceFileResource;
		}

	}

	public void removeWithUnlockFile(LocalSourceFileResource localSourceFileResource) {
		if (null == localSourceFileResource) {
			throw new IllegalArgumentException("the parameter localSourceFileResource is null");
		}

		synchronized (monitor) {
			String ownerID = localSourceFileResource.getOwnerID();
			int sourceFileID = localSourceFileResource.getSourceFileID();

			if (!localSourceFileResourceHash.containsKey(sourceFileID)) {
				log.info("'로컬 소스 파일 자원 해쉬'에 지정한 '로컬 소스 파일 자원'[{}] 이 존재하지 않습니다", localSourceFileResource.toString());
				return;
			}

			releaseLocalSourceFileResource(localSourceFileResource);
			doRemove(ownerID, sourceFileID);
		}
	}

	public void removeUsingUserIDWithUnlockFile(String ownerID) {
		if (null == ownerID) {
			throw new IllegalArgumentException("the parameter ownerID is null");
		}
		synchronized (monitor) {
			Integer sourceFileID = ownerID2SourceFileIDHash.get(ownerID);
			if (null == sourceFileID) {
				log.info("소유자[{}]의 로컬 소스 자원이 '소유자별 로컬 소스 자원 해쉬'(ownerID2SourceFileIDHash)에 존재하지 않습니다.", ownerID);
				return;
			}
			LocalSourceFileResource localSourceFileResource = localSourceFileResourceHash.get(sourceFileID);
			if (null == localSourceFileResource) {
				log.info("소유자[{}]의 로컬 소스 자원[{}]이 '로컬 소스 파일 자원 해쉬'(localSourceFileResourceHash)에 존재하지 않습니다.", ownerID,
						sourceFileID);
				return;
			}

			
			releaseLocalSourceFileResource(localSourceFileResource);
			doRemove(ownerID, sourceFileID);			
		}
	}
	
	private void releaseLocalSourceFileResource(LocalSourceFileResource localSourceFileResource) {
		localSourceFileResource.releaseFileLock();
		/**
		 * <pre>
		 * '전송 처리 정보 윈도우' 가 지정되었고 전송 완료된 경우 사용자가 OK 버튼 클릭으로 창을 닫게 해주어야 하므로 이곳에서
		 * 닫지 않는다.
		 * 
		 * <pre>
		 */
		localSourceFileResource.disposeFileTranferProcessInformationDialogIfExistAndNotTransferDone();
	}

	private void doRemove(String ownerID, int sourceFileID) {
		localSourceFileResourceHash.remove(sourceFileID);
		ownerID2SourceFileIDHash.remove(ownerID);		

		log.info("2.소유자[{}]의 지정한 로컬 소스 파일 자원[{}]의 파일 락을 해제후 "
				+ "'로컬 소스 파일 자원 해쉬'와 '소유자별 로컬 소스 자원 해쉬'에서 소유자의 로컬 소스 파일 자원 삭제", ownerID, sourceFileID);
	}

	/**
	 * 원본 파일 식별자에 1:1 대응하는 로컬 원본 파일 자원을 반환한다.
	 * 
	 * @param sourceFileID
	 *            원본 파일 식별자
	 * @return 원본 파일 식별자에 1:1 대응하는 로컬 원본 파일 자원, 할당 받은 로컬 원본 파일 자원들중 원본 파일 식별자를
	 *         갖는것이 없을 경우 null 를 반환한다.
	 */
	public LocalSourceFileResource getLocalSourceFileResource(int sourceFileID) {
		synchronized (monitor) {
			LocalSourceFileResource localSourceFileResource = localSourceFileResourceHash.get(sourceFileID);
			return localSourceFileResource;
		}
	}

	public void releaseAllFileLock() {
		synchronized (monitor) {
			for (LocalSourceFileResource localSourceFileResource : localSourceFileResourceHash.values()) {
				log.info("release the file lock of LocalSourceFileResource[{}]", localSourceFileResource.toString());
				localSourceFileResource.releaseFileLock();
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		releaseAllFileLock();
	}
}
