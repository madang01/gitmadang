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

import java.io.File;

/**
 * 로컬 파일 트리 노드
 * @author Jonghoon Won
 *
 */
@SuppressWarnings("serial")
public class LocalFileTreeNode extends AbstractFileTreeNode {
	private File fileObj = null;
	// private String absolutePath = null;
	
	public LocalFileTreeNode(File fileObj, long fileSize, byte fileTypeByte) {
		super(fileObj.getName(), fileSize, fileTypeByte);
		
		this.fileObj = fileObj;
		
		// this.absolutePath = fileObj.getAbsolutePath();
		/*
		if (this.isRoot()) {
			this.setUserObject(fileObj.getAbsolutePath());
		}
		*/
		
		log.info(String.format("fileName=[%s], userObject=[%s], isRoot=[%s]", fileName, getUserObject(), isRoot()));
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
		
		if (this.isRoot()) {
			this.setUserObject(fileObj.getAbsolutePath());
		} else {
			this.setUserObject(fileName);
		}
	}
}
