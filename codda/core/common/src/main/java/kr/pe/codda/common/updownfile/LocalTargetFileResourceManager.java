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
import kr.pe.codda.common.config.Configuration;
import kr.pe.codda.common.config.ConfigurationManager;
import kr.pe.codda.common.config.itemvalue.CommonPartConfiguration;
import kr.pe.codda.common.exception.UpDownFileException;

/**
 * <pre>
 * 로컬 목적지 파일을 원할하게 수신하기 위한 로컬 목적지 파일 자원 큐 관리자 클래스. 
 * 주) 느슷한 구조의 큐 자원 관리자로 통제된 사용 방법외 방법으로 사용시 비 정상 동작한다.
 * </pre>
 * 
 * @author Won Jonghoon
 *
 */
public class LocalTargetFileResourceManager {
	private InternalLogger log = InternalLoggerFactory.getInstance(LocalTargetFileResourceManager.class);

	private final Object monitor = new Object();

	private int targetFileID = Integer.MAX_VALUE;
	private int localTargetFileResourceCnt = -1;

	private HashMap<Integer, LocalTargetFileResource> localTargetFileResourceHash = null;
	private HashMap<String, Integer> ownerID2TargetFileIDHash = new HashMap<String, Integer>();

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
		// int localTargetFileResourceCnt =
		// (Integer)conf.getResource("common.updownfile.local_target_file_resource_cnt.value");
		Configuration sinnoriRunningProjectConfiguration = ConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();
		CommonPartConfiguration commonPart = sinnoriRunningProjectConfiguration.getCommonPartConfiguration();
		localTargetFileResourceCnt = commonPart.getLocalTargetFileResourceCnt();

		localTargetFileResourceHash = new HashMap<Integer, LocalTargetFileResource>();
	}

	public LocalTargetFileResource registerNewLocalTargetFileResource(String ownerID, boolean append,
			String sourceFilePathName, String sourceFileName, long sourceFileSize, String targetFilePathName,
			String targetFileName, long targetFileSize, int fileBlockSize)
			throws IllegalArgumentException, UpDownFileException {
		if (null == ownerID) {
			throw new IllegalArgumentException("the parameter ownerID is null");
		}
		synchronized (monitor) {
			int localTargetFileResourceHashSize = localTargetFileResourceHash.size();
			if (localTargetFileResourceHashSize >= localTargetFileResourceCnt) {
				log.info(
						"환경 변수 '로컬 목적지 파일 자원 갯수'(=common.updownfile.local_target_file_resource_cnt.value 만큼 사용중으로 더 이상 생성할 수 없습니다.");
				return null;
			}

			if (Integer.MAX_VALUE == targetFileID) {
				targetFileID = Integer.MIN_VALUE;
			} else {
				targetFileID++;
			}

			LocalTargetFileResource localTargetFileResource = new LocalTargetFileResource(ownerID, targetFileID, append,
					sourceFilePathName, sourceFileName, sourceFileSize, targetFilePathName, targetFileName,
					targetFileSize, fileBlockSize);

			localTargetFileResourceHash.put(localTargetFileResource.getTargetFileID(), localTargetFileResource);
			ownerID2TargetFileIDHash.put(ownerID, targetFileID);
			return localTargetFileResource;
		}
	}

	public void removeWithUnlockFile(LocalTargetFileResource localTargetFileResource) {
		if (null == localTargetFileResource) {
			return;
		}

		synchronized (monitor) {
			String ownerID = localTargetFileResource.getOwnerID();
			int targetFileID = localTargetFileResource.getTargetFileID();

			if (!localTargetFileResourceHash.containsKey(targetFileID)) {
				log.info("'로컬 목적지 파일 자원 해쉬'에 지정한 '로컬 목적지 파일 자원'[{}] 이 존재하지 않습니다", localTargetFileResource.toString());
				return;
			}

			releaseLocalTargetFileResoruce(localTargetFileResource);
			doRemove(ownerID, targetFileID);
		}
	}

	public void removeUsingUserIDWithUnlockFile(String ownerID) {
		if (null == ownerID) {
			throw new IllegalArgumentException("the parameter ownerID is null");
		}
		synchronized (monitor) {
			Integer targetFileID = ownerID2TargetFileIDHash.get(ownerID);
			if (null == targetFileID) {
				log.info("소유자[{}]의 로컬 목적지 자원이 '소유자별 로컬 목적지 자원 해쉬'(ownerID2TargetFileIDHash)에 존재하지 않습니다.", ownerID);
				return;
			}

			LocalTargetFileResource localTargetFileResource = localTargetFileResourceHash.get(targetFileID);
			if (null == localTargetFileResource) {
				log.info("소유자[{}]의 로컬 목적지 자원[{}]이 '로컬 목적지 파일 자원 해쉬'(localTargetFileResourceHash)에 존재하지 않습니다.", ownerID,
						targetFileID);
				return;
			}

			releaseLocalTargetFileResoruce(localTargetFileResource);
			doRemove(ownerID, targetFileID);
		}
	}
	
	
	private void releaseLocalTargetFileResoruce(LocalTargetFileResource localTargetFileResource) {
		localTargetFileResource.releaseFileLock();
		/**
		 * <pre>
		 * '전송 처리 정보 윈도우' 가 지정되었고 전송 완료된 경우 사용자가 OK 버튼 클릭으로 창을 닫게 해주어야 하므로 이곳에서
		 * 닫지 않는다.
		 * 
		 * <pre>
		 */
		localTargetFileResource.disposeFileTranferProcessInformationDialogIfExistAndNotTransferDone();
	}
	
	private void doRemove(String ownerID, int targetFileID) {
		localTargetFileResourceHash.remove(targetFileID);
		ownerID2TargetFileIDHash.remove(ownerID);

		

		log.info("소유자[{}]의 지정한 로컬 목적지 파일 자원[{}]의 파일 락을 해제후 "
				+ "'로컬 목적지 파일 자원 해쉬'와 '소유자별 로컬 목적지 자원 해쉬'에서 지정한 로컬 목적지 파일 자원 삭제", ownerID, targetFileID);
	}

	

	/**
	 * 목적 파일 식별자에 1:1 대응하는 로컬 목적 파일 자원을 반환한다.
	 * 
	 * @param targetFileID
	 *            목적 파일 식별자
	 * @return 목적 파일 식별자에 1:1 대응하는 로컬 목적 파일 자원, 할당 받은 로컬 목적 파일 자원들중 목적 파일 식별자를
	 *         갖는것이 없을 경우 null 를 반환한다.
	 */
	public LocalTargetFileResource getLocalTargetFileResource(int targetFileID) {
		synchronized (monitor) {
			LocalTargetFileResource localTargetFileResource = localTargetFileResourceHash.get(targetFileID);
			return localTargetFileResource;
		}
	}
}
