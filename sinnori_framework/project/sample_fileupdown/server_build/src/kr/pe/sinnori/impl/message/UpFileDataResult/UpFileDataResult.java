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
package kr.pe.sinnori.impl.message.UpFileDataResult;

import kr.pe.sinnori.common.message.AbstractMessage;
/**
 * UpFileDataResult 메시지
 * @author Won Jonghoon
 *
 */
public final class UpFileDataResult extends AbstractMessage {
	private int clientSourceFileID;
	private int serverTargetFileID;
	private int fileBlockNo;
	private String taskResult;
	private String resultMessage;

	public int getClientSourceFileID() {
		return clientSourceFileID;
	}

	public void setClientSourceFileID(int clientSourceFileID) {
		this.clientSourceFileID = clientSourceFileID;
	}
	public int getServerTargetFileID() {
		return serverTargetFileID;
	}

	public void setServerTargetFileID(int serverTargetFileID) {
		this.serverTargetFileID = serverTargetFileID;
	}
	public int getFileBlockNo() {
		return fileBlockNo;
	}

	public void setFileBlockNo(int fileBlockNo) {
		this.fileBlockNo = fileBlockNo;
	}
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("class UpFileDataResult[");
		builder.append("clientSourceFileID=");
		builder.append(clientSourceFileID);
		builder.append(", serverTargetFileID=");
		builder.append(serverTargetFileID);
		builder.append(", fileBlockNo=");
		builder.append(fileBlockNo);
		builder.append(", taskResult=");
		builder.append(taskResult);
		builder.append(", resultMessage=");
		builder.append(resultMessage);
		builder.append(", messageHeaderInfo=");
		builder.append(messageHeaderInfo.toString());
		builder.append("]");
		return builder.toString();
	}
}