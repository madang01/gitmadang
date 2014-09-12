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

package kr.pe.sinnori.gui.screen.commonfileupdown;

import javax.swing.JTree;

import kr.pe.sinnori.gui.lib.LocalFileTreeNode;
import kr.pe.sinnori.gui.lib.RemoteFileTreeNode;
import kr.pe.sinnori.impl.message.FileListResult.FileListResult;

/**
 * 파일 송수신 화면을 제어하는 기능 제공 인터페이스. 
 * @author Jonghoon Won
 *
 */
public interface FileUpDownScreenIF {
	/**
	 * 지정된 로컬 부모 노드의 자식 노드들을 모두 삭제후 로컬 부모 노드에 대응하는 로컬 경로의 파일 목록을 읽어와 자식 노드를 구성한다.
	 * @param localParentNode 파일 목록 갱신을 원하는 경로를 갖고 있는 로컬 부모 노드
	 */
	public void makeLocalTreeNode(LocalFileTreeNode localParentNode);
	/**
	 * 지정된 트리 화면을 재 갱신한다. 파일 목록 갱신후 호출된다.
	 * @param targetTree 화면 갱신을 원하는 트리 객체
	 */
	public void repaintTree(JTree targetTree);
	/**
	 * @return 원격지 경로 구분자
	 */
	public String getRemotePathSeperator();
	/**
	 * 로컬 파일 목록을 재 갱신한다. 파일 다운로드 후 호출된다.
	 */
	public void reloadLocalFileList();
	/**
	 * 원격지 파일 목록을 재 갱신한다.파일 업로드 후 호출된다.
	 */
	public void reloadRemoteFileList();
	/**
	 * 지정된 원격지 부모 노드의 자식 노드들을 모두 삭제후 원격지 파일 목록 출력 메시지를 읽어와 자식 노드를 구성한다.
	 * @param fileListOutObj 원격지 파일 목록 메시지
	 * @param remoteParentNode 원격지 부모 노드
	 */
	public void makeRemoteTreeNode(FileListResult fileListOutObj, RemoteFileTreeNode remoteParentNode);
}
