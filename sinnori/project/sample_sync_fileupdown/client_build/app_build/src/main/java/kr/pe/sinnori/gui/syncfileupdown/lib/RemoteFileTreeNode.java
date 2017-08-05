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

import java.util.List;

import kr.pe.sinnori.impl.message.FileListRes.FileListRes;

/**
 * 원격지 파일 트리 노드
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class RemoteFileTreeNode extends AbstractFileTreeNode {
	
	public RemoteFileTreeNode(String fileName, long fileSize, byte fileTypeByte) {
		super(fileName, fileSize, fileTypeByte);
	}
	
	/**
	 * 파일 이름을 변경한다. 고정인 루트 노드에서만 사용된다. 
	 * @param newFileName
	 */
	public void  chageFileName(String newFileName) {
		this.fileName = newFileName;
		this.setUserObject(newFileName);
	}
	
	public void rebuildChildTreeNodes(FileListRes fileListRes) {
		removeAllChildren();
		List<FileListRes.ChildFile> childFileList = fileListRes.getChildFileList();

		for (FileListRes.ChildFile childFile : childFileList) {			
			String fileName = childFile.getFileName();
			long fileSize = childFile.getFileSize();
			/** 파일 종류, 1:디렉토리, 0:파일 */
			byte fileType = childFile.getFileType();

			RemoteFileTreeNode remoteChildNode = new RemoteFileTreeNode(fileName, fileSize,
					fileType);
			add(remoteChildNode);
		}
	}
}
