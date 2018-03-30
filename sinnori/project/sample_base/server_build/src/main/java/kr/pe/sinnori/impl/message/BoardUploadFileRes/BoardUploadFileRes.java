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
package kr.pe.sinnori.impl.message.BoardUploadFileRes;

import kr.pe.sinnori.common.message.AbstractMessage;

/**
 * BoardUploadFileOutDTO 메시지
 * @author Won Jonghoon
 *
 */
public class BoardUploadFileRes extends AbstractMessage {
	private long attachId;
	private String ownerId;
	private String ip;
	private java.sql.Timestamp registerDate;
	private java.sql.Timestamp modifiedDate;
	private int attachFileCnt;

	public static class AttachFile {
		private short attachSeq;
		private String attachFileName;
		private String systemFileName;

		public short getAttachSeq() {
			return attachSeq;
		}
		
		public void setAttachSeq(short attachSeq) {
			this.attachSeq = attachSeq;
		}
		public String getAttachFileName() {
			return attachFileName;
		}

		public void setAttachFileName(String attachFileName) {
			this.attachFileName = attachFileName;
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
			builder.append("AttachFile[");
			builder.append("attachSeq=");
			builder.append(attachSeq);
			builder.append(", attachFileName=");
			builder.append(attachFileName);
			builder.append(", systemFileName=");
			builder.append(systemFileName);
			builder.append("]");
			return builder.toString();
		}
	}

	private java.util.List<AttachFile> attachFileList;

	public long getAttachId() {
		return attachId;
	}

	public void setAttachId(long attachId) {
		this.attachId = attachId;
	}
	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	public java.sql.Timestamp getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(java.sql.Timestamp registerDate) {
		this.registerDate = registerDate;
	}
	public java.sql.Timestamp getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(java.sql.Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public int getAttachFileCnt() {
		return attachFileCnt;
	}

	public void setAttachFileCnt(int attachFileCnt) {
		this.attachFileCnt = attachFileCnt;
	}
	public java.util.List<AttachFile> getAttachFileList() {
		return attachFileList;
	}

	public void setAttachFileList(java.util.List<AttachFile> attachFileList) {
		this.attachFileList = attachFileList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("boardUploadFileOutDTO[");
		builder.append("attachId=");
		builder.append(attachId);
		builder.append(", ownerId=");
		builder.append(ownerId);
		builder.append(", ip=");
		builder.append(ip);
		builder.append(", registerDate=");
		builder.append(registerDate);
		builder.append(", modifiedDate=");
		builder.append(modifiedDate);
		builder.append(", attachFileCnt=");
		builder.append(attachFileCnt);

		builder.append(", attachFileList=");
		if (null == attachFileList) {
			builder.append("null");
		} else {
			int attachFileListSize = attachFileList.size();
			if (0 == attachFileListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < attachFileListSize; i++) {
					AttachFile attachFile = attachFileList.get(i);
					if (0 == i) {
						builder.append("attachFile[");
					} else {
						builder.append(", attachFile[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(attachFile.toString());
				}
				builder.append("]");
			}
		}
		builder.append("]");
		return builder.toString();
	}
}