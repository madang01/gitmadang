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

/**
 * 원격지 파일 트리 노드
 * @author Jonghoon Won
 *
 */
@SuppressWarnings("serial")
public class RemoteFileTreeNode extends AbstractFileTreeNode {
	
	public RemoteFileTreeNode(String fileName, long fileSize, byte fileTypeByte) {
		super(fileName, fileSize, fileTypeByte);

		/*
		if (!this.isRoot()) {
			setFileName(String.format("%s %d byte(s)", fileName, fileSize));
		}
		*/
	}
	
	/**
	 * 파일 이름을 변경한다. 고정인 루트 노드에서만 사용된다. 
	 * @param newFileName
	 */
	public void  chageFileName(String newFileName) {
		this.fileName = newFileName;
		this.setUserObject(newFileName);
	}
}
