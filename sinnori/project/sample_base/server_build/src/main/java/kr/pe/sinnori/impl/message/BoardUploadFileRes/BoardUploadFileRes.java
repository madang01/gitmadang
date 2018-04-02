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
 * BoardUploadFileRes 메시지
 * @author Won Jonghoon
 *
 */
public class BoardUploadFileRes extends AbstractMessage {
	private long attachId;
	private String ownerId;
	private String ip;
	private java.sql.Timestamp registerDate;
	private java.sql.Timestamp modifiedDate;
	private int attachedFileCnt;

	public static class AttachedFile {
		private short attachSeq;
		private String attachedFileName;
		private String systemFileName;

		public short getAttachSeq() {
			return attachSeq;
		}

		public void setAttachSeq(short attachSeq) {
			this.attachSeq = attachSeq;
		}
		public String getAttachedFileName() {
			return attachedFileName;
		}

		public void setAttachedFileName(String attachedFileName) {
			this.attachedFileName = attachedFileName;
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
			builder.append("AttachedFile[");
			builder.append("attachSeq=");
			builder.append(attachSeq);
			builder.append(", attachedFileName=");
			builder.append(attachedFileName);
			builder.append(", systemFileName=");
			builder.append(systemFileName);
			builder.append("]");
			return builder.toString();
		}
	}

	private java.util.List<AttachedFile> attachedFileList;

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
	public int getAttachedFileCnt() {
		return attachedFileCnt;
	}

	public void setAttachedFileCnt(int attachedFileCnt) {
		this.attachedFileCnt = attachedFileCnt;
	}
	public java.util.List<AttachedFile> getAttachedFileList() {
		return attachedFileList;
	}

	public void setAttachedFileList(java.util.List<AttachedFile> attachedFileList) {
		this.attachedFileList = attachedFileList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("boardUploadFileRes[");
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
		builder.append(", attachedFileCnt=");
		builder.append(attachedFileCnt);

		builder.append(", attachedFileList=");
		if (null == attachedFileList) {
			builder.append("null");
		} else {
			int attachedFileListSize = attachedFileList.size();
			if (0 == attachedFileListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < attachedFileListSize; i++) {
					AttachedFile attachedFile = attachedFileList.get(i);
					if (0 == i) {
						builder.append("attachedFile[");
					} else {
						builder.append(", attachedFile[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(attachedFile.toString());
				}
				builder.append("]");
			}
		}
		builder.append("]");
		return builder.toString();
	}
}