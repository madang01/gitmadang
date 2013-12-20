
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

import kr.pe.sinnori.client.AnonymousServerMessageTaskIF;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.message.OutputMessage;

/**
 * @author Jonghoon Won
 *
 */
public class FileUpDown2AnonymousServerMessageTask implements CommonRootIF, AnonymousServerMessageTaskIF {
	private MainControllerIF mainController = null;
	
	public FileUpDown2AnonymousServerMessageTask(MainControllerIF mainController) {
		this.mainController = mainController;
	}
	
	@Override
	public void doTask(String projectName, OutputMessage outObj) {
		log.info(String.format("projectName[%s] %s", projectName, outObj.toString()));
		
		mainController.doAnonymousServerMessageTask(projectName, outObj);
	}
}