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

package kr.pe.sinnori.gui.syncfileupdown.lib;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kr.pe.sinnori.common.util.NameFirstComparator;

/**
 * 로컬 파일 트리 노드
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class LocalFileTreeNode extends AbstractFileTreeNode {
	
	private File fileObj = null;
	// private String absolutePath = null;
	
	public LocalFileTreeNode(File fileObj, long fileSize, byte fileTypeByte) {
		super(fileObj.getName(), fileSize, fileTypeByte);
		
		this.fileObj = fileObj;
	}
	
	/**
	 * @return 로컬 트리 노드가 가진 파일 객체
	 */
	public File getFileObj() {
		return fileObj;
	}
	
	/**
	 * @return 로컬 트리 노드에 대응하는 절대 경로
	 */
	public String getAbsolutePath() {
		return fileObj.getAbsolutePath();
	}
	
	/**
	 * 파일 객체를 변경한다. 고정인 루트 노드에서만 사용된다. 
	 * @param newFileObj
	 */
	public void changeFileObj(File newFileObj) {
		this.fileObj = newFileObj;
		this.fileName = newFileObj.getName();
		//this.absolutePath = newFileObj.getAbsolutePath();
		
		log.info("file={}, isRoot={}", fileObj.getAbsolutePath(), isRoot());
		
		this.setUserObject(fileObj.getAbsolutePath());
		
		
		rebuildChildTreeNodes();
	}
	
	
	public void rebuildChildTreeNodes() {
		
		removeAllChildren();

		File localParnetFile = getFileObj();

		File[] subFiles = localParnetFile.listFiles();

		if (null == subFiles)
			return;

		Arrays.sort(subFiles, new NameFirstComparator());

		List<File> fileList = new ArrayList<File>();
		List<File> directoryList = new ArrayList<File>();
		for (File workFile : subFiles) {
			if (workFile.isFile()) {
				fileList.add(workFile);
			} else {
				directoryList.add(workFile);
			}
		}
		for (File workFile : directoryList) {
			/**
			 * <pre>
			 * 2013.09.01
			 * (1) 경로 문자열로 넘기는 경우
			 *     - 메소드 getAbsolutePath 와 getCanonicalPath 속도 비교 결과
			 *       getAbsolutePath : 1035 ms
			 *       getCanonicalPath : 201x ms
			 *       DirectoryTreeNode 를 통한 자체적으로 경로 만들기 : 1098 ms
			 * 
			 * %% 결론적으로 getAbsolutePath 가 속도 좋음.
			 * 
			 * (2)  파일 객체 넘기는 경우
			 *    테스트 결과 985 ms, 1010 ms
			 *    
			 *  %% 결론적으로 파일 객체가 경로 문자열 보다 속도 좋음.
			 * </pre>
			 */

			LocalFileTreeNode localChildNode = new LocalFileTreeNode(workFile, 0L, RemoteFileTreeNode.DIRECTORY);

			add(localChildNode);
		}

		for (File workFile : fileList) {
			LocalFileTreeNode localChildNode = new LocalFileTreeNode(workFile, workFile.length(),
					RemoteFileTreeNode.FILE);

			add(localChildNode);
		}
	}
}
