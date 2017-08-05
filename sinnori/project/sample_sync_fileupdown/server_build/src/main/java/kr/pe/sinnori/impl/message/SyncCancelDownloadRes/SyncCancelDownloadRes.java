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
package kr.pe.sinnori.impl.message.SyncCancelDownloadRes;

import kr.pe.sinnori.common.message.AbstractMessage;
/**
 * SyncCancelDownloadRes 메시지
 * @author Won Jonghoon
 *
 */
public class SyncCancelDownloadRes extends AbstractMessage {
	private String taskResult;
	private String resultMessage;
	private int serverSourceFileID;
	private int clientTargetFileID;

	public String getTaskResult() {
		return taskResult;
	}

	public void setTaskResult(String taskResult) {
		this.taskResult = taskResult;
	}
	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
	public int getServerSourceFileID() {
		return serverSourceFileID;
	}

	public void setServerSourceFileID(int serverSourceFileID) {
		this.serverSourceFileID = serverSourceFileID;
	}
	public int getClientTargetFileID() {
		return clientTargetFileID;
	}

	public void setClientTargetFileID(int clientTargetFileID) {
		this.clientTargetFileID = clientTargetFileID;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("class SyncCancelDownloadRes[");
		builder.append("taskResult=");
		builder.append(taskResult);
		builder.append(", resultMessage=");
		builder.append(resultMessage);
		builder.append(", serverSourceFileID=");
		builder.append(serverSourceFileID);
		builder.append(", clientTargetFileID=");
		builder.append(clientTargetFileID);
		builder.append(", messageHeaderInfo=");
		builder.append(messageHeaderInfo.toString());
		builder.append("]");
		return builder.toString();
	}
}