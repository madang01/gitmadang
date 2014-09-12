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

package kr.pe.sinnori.gui.screen.commonfileupdown.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.gui.screen.commonfileupdown.FileUpDownScreenIF;

/**
 * 원격지 경로 재 갱신 이벤트 클래스.
 * @author Jonghoon Won
 *
 */
@SuppressWarnings("serial")
public class RemoteReloadSwingAction extends AbstractAction implements CommonRootIF {
	private FileUpDownScreenIF fileUpDownScreen = null;
	
	
	/**
	 * 생성자
	 * @param fileUpDownScreen 파일 송수신 화면을 제어하는 기능 제공 인터페이스
	 */
	public RemoteReloadSwingAction(FileUpDownScreenIF fileUpDownScreen) {
		this.fileUpDownScreen = fileUpDownScreen;
		
		putValue(NAME, "remoteReload");
		putValue(SHORT_DESCRIPTION, "Some short description");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		log.debug(String.format("e.getID=[%d]", e.getID()));
		/*
		OutputMessage fileListOutObj = mainController
				.getRemoteFileList(remoteRootNode.getFileName());
		if (null == fileListOutObj) return;
		

		remoteRootNode.removeAllChildren();
		fileUpDownScreen.makeRemoteTreeNode(fileListOutObj, remoteRootNode);
		fileUpDownScreen.repaintTree(remoteTree);
		*/
		fileUpDownScreen.reloadRemoteFileList();
	}
}
