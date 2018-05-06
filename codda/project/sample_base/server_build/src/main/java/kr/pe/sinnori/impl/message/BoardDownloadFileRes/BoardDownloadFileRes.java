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
package kr.pe.sinnori.impl.message.BoardDownloadFileRes;

import kr.pe.sinnori.common.message.AbstractMessage;

/**
 * BoardDownloadFileRes 메시지
 * @author Won Jonghoon
 *
 */
public class BoardDownloadFileRes extends AbstractMessage {
	private String ownerId;
	private long attachId;
	private long attachSeq;
	private String attachFiledName;
	private String systemFileName;

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	public long getAttachId() {
		return attachId;
	}

	public void setAttachId(long attachId) {
		this.attachId = attachId;
	}
	public long getAttachSeq() {
		return attachSeq;
	}

	public void setAttachSeq(long attachSeq) {
		this.attachSeq = attachSeq;
	}
	public String getAttachFiledName() {
		return attachFiledName;
	}

	public void setAttachFiledName(String attachFiledName) {
		this.attachFiledName = attachFiledName;
	}
	public String getSystemFileName() {
		return systemFileName;
	}

	public void setSystemFileName(String systemFileName) {
		this.systemFileName = systemFileName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("boardDownloadFileRes[");
		builder.append("ownerId=");
		builder.append(ownerId);
		builder.append(", attachId=");
		builder.append(attachId);
		builder.append(", attachSeq=");
		builder.append(attachSeq);
		builder.append(", attachFiledName=");
		builder.append(attachFiledName);
		builder.append(", systemFileName=");
		builder.append(systemFileName);
		builder.append("]");
		return builder.toString();
	}
}