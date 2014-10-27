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
package kr.pe.sinnori.impl.message.SyncDownFileInfo;

import kr.pe.sinnori.common.message.AbstractMessage;
/**
 * SyncDownFileInfo 메시지
 * @author Won Jonghoon
 *
 */
public class SyncDownFileInfo extends AbstractMessage {
	private byte append;
	private String localFilePathName;
	private String localFileName;
	private long localFileSize;
	private String remoteFilePathName;
	private String remoteFileName;
	private long remoteFileSize;
	private int clientTargetFileID;
	private int fileBlockSize;

	public byte getAppend() {
		return append;
	}

	public void setAppend(byte append) {
		this.append = append;
	}
	public String getLocalFilePathName() {
		return localFilePathName;
	}

	public void setLocalFilePathName(String localFilePathName) {
		this.localFilePathName = localFilePathName;
	}
	public String getLocalFileName() {
		return localFileName;
	}

	public void setLocalFileName(String localFileName) {
		this.localFileName = localFileName;
	}
	public long getLocalFileSize() {
		return localFileSize;
	}

	public void setLocalFileSize(long localFileSize) {
		this.localFileSize = localFileSize;
	}
	public String getRemoteFilePathName() {
		return remoteFilePathName;
	}

	public void setRemoteFilePathName(String remoteFilePathName) {
		this.remoteFilePathName = remoteFilePathName;
	}
	public String getRemoteFileName() {
		return remoteFileName;
	}

	public void setRemoteFileName(String remoteFileName) {
		this.remoteFileName = remoteFileName;
	}
	public long getRemoteFileSize() {
		return remoteFileSize;
	}

	public void setRemoteFileSize(long remoteFileSize) {
		this.remoteFileSize = remoteFileSize;
	}
	public int getClientTargetFileID() {
		return clientTargetFileID;
	}

	public void setClientTargetFileID(int clientTargetFileID) {
		this.clientTargetFileID = clientTargetFileID;
	}
	public int getFileBlockSize() {
		return fileBlockSize;
	}

	public void setFileBlockSize(int fileBlockSize) {
		this.fileBlockSize = fileBlockSize;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("class SyncDownFileInfo[");
		builder.append("append=");
		builder.append(append);
		builder.append(", localFilePathName=");
		builder.append(localFilePathName);
		builder.append(", localFileName=");
		builder.append(localFileName);
		builder.append(", localFileSize=");
		builder.append(localFileSize);
		builder.append(", remoteFilePathName=");
		builder.append(remoteFilePathName);
		builder.append(", remoteFileName=");
		builder.append(remoteFileName);
		builder.append(", remoteFileSize=");
		builder.append(remoteFileSize);
		builder.append(", clientTargetFileID=");
		builder.append(clientTargetFileID);
		builder.append(", fileBlockSize=");
		builder.append(fileBlockSize);
		builder.append(", messageHeaderInfo=");
		builder.append(messageHeaderInfo.toString());
		builder.append("]");
		return builder.toString();
	}
}