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

import javax.swing.tree.DefaultMutableTreeNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 트리 노드 부모 클래스. 로컬이나 원격지 트리 노드들의 공통 부분.
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractFileTreeNode extends DefaultMutableTreeNode {
	protected Logger log = LoggerFactory.getLogger(AbstractFileTreeNode.class);
	
	protected String fileName = null;
	protected long fileSize = 0;
	public enum FileType { File, Directory };
	protected FileType fileType;
	
	public static byte FILE = 0;
	public static byte DIRECTORY = 1;
	
	/**
	 * 생성자
	 * @param userObject 사용자 정의 객체, 파일명
	 * @param fileSize 파일 크기
	 * @param fileTypeByte 파일 종류, byte 값이 0 이면 파일, 01 이면 디렉토리를 뜻한다.
	 */
	public AbstractFileTreeNode(Object userObject, long fileSize, byte fileTypeByte) {
		super(userObject, DIRECTORY == fileTypeByte);
		this.fileName = (String)userObject;
		
		this.fileSize = fileSize;
		
		if (DIRECTORY == fileTypeByte ) {
			fileType = FileType.Directory;
		} else if (FILE == fileTypeByte) {
			fileType = FileType.File;
		} else {
			String errorMessage = String.format("unkown file type[%d], D:Directory, F:File", fileTypeByte);
			throw new IllegalArgumentException(errorMessage);
		}
		
	}
	/**
	 * @return 파일 크기
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * @return 파일명
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * @return 파일 종류
	 */
	public FileType getFileType() {
		return fileType;
	}
	
	/**
	 * @return 디렉토리 여부
	 */
	public boolean isDirectory() {
		return (FileType.Directory == fileType);
	}
	
	/*
	@SuppressWarnings("unchecked")
	static public String encoding(FileTreeNode fileTreeNode) {
		StringBuilder builder = new StringBuilder();
		if (fileTreeNode.isRoot()) {
			builder.append(-1);
			
		} else {
			FileTreeNode parentFileTreeNode = (FileTreeNode)fileTreeNode.getParent();
			builder.append(" ");
			builder.append(parentFileTreeNode.getFileID());
		}
		
		builder.append(" ");
		builder.append(fileTreeNode.getFileID());
		
		if (FileType.File == fileTreeNode.getFileType()) {
			builder.append(" F");
		} else {
			builder.append(" D");
		}
		
		builder.append(" ");
		builder.append(fileTreeNode.getFileName());
		
		Enumeration<FileTreeNode> fileTreeNodes = fileTreeNode.children();
		while(fileTreeNodes.hasMoreElements()) {
			FileTreeNode childFileTreeNode = fileTreeNodes.nextElement();
			
			builder.append(encoding(childFileTreeNode));
		}
		
		return builder.toString();
	}
	
	
	public FileTreeNode decoding(String encodingStr) {
		
		return null;
	}
	*/
	
	/**
	 * @return 트리 노드 요약 내용, toString 이름을 사용하지 않는 이유는 {@DefaultMutableTreeNode } 에서 toString 자체 목적하에 맞게 정의하여 부품으로 사용하기때문이다. 
	 */
	public String toSummary() {
		StringBuilder builder = new StringBuilder();
		builder.append("DirectoryTreeNode [fileName=");
		builder.append(fileName);
		builder.append(", fileSize=");
		builder.append(fileSize);
		builder.append(", fileType=");
		builder.append(fileType);
		builder.append("]");
		return builder.toString();
	}
}
