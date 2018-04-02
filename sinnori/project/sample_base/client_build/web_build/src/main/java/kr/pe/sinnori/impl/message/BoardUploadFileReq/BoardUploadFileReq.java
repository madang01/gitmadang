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
package kr.pe.sinnori.impl.message.BoardUploadFileReq;

import kr.pe.sinnori.common.message.AbstractMessage;

/**
 * BoardUploadFileReq 메시지
 * @author Won Jonghoon
 *
 */
public class BoardUploadFileReq extends AbstractMessage {
	private String userId;
	private String ip;
	private long attachId;
	private int oldAttachedFileCnt;

	public static class OldAttachedFile {
		private short attachSeq;

		public short getAttachSeq() {
			return attachSeq;
		}

		public void setAttachSeq(short attachSeq) {
			this.attachSeq = attachSeq;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("OldAttachedFile[");
			builder.append("attachSeq=");
			builder.append(attachSeq);
			builder.append("]");
			return builder.toString();
		}
	}

	private java.util.List<OldAttachedFile> oldAttachedFileList;
	private int newAttachedFileCnt;

	public static class NewAttachedFile {
		private String attachedFileName;
		private String systemFileName;

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
			builder.append("NewAttachedFile[");
			builder.append("attachedFileName=");
			builder.append(attachedFileName);
			builder.append(", systemFileName=");
			builder.append(systemFileName);
			builder.append("]");
			return builder.toString();
		}
	}

	private java.util.List<NewAttachedFile> newAttachedFileList;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	public long getAttachId() {
		return attachId;
	}

	public void setAttachId(long attachId) {
		this.attachId = attachId;
	}
	public int getOldAttachedFileCnt() {
		return oldAttachedFileCnt;
	}

	public void setOldAttachedFileCnt(int oldAttachedFileCnt) {
		this.oldAttachedFileCnt = oldAttachedFileCnt;
	}
	public java.util.List<OldAttachedFile> getOldAttachedFileList() {
		return oldAttachedFileList;
	}

	public void setOldAttachedFileList(java.util.List<OldAttachedFile> oldAttachedFileList) {
		this.oldAttachedFileList = oldAttachedFileList;
	}
	public int getNewAttachedFileCnt() {
		return newAttachedFileCnt;
	}

	public void setNewAttachedFileCnt(int newAttachedFileCnt) {
		this.newAttachedFileCnt = newAttachedFileCnt;
	}
	public java.util.List<NewAttachedFile> getNewAttachedFileList() {
		return newAttachedFileList;
	}

	public void setNewAttachedFileList(java.util.List<NewAttachedFile> newAttachedFileList) {
		this.newAttachedFileList = newAttachedFileList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("boardUploadFileReq[");
		builder.append("userId=");
		builder.append(userId);
		builder.append(", ip=");
		builder.append(ip);
		builder.append(", attachId=");
		builder.append(attachId);
		builder.append(", oldAttachedFileCnt=");
		builder.append(oldAttachedFileCnt);

		builder.append(", oldAttachedFileList=");
		if (null == oldAttachedFileList) {
			builder.append("null");
		} else {
			int oldAttachedFileListSize = oldAttachedFileList.size();
			if (0 == oldAttachedFileListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < oldAttachedFileListSize; i++) {
					OldAttachedFile oldAttachedFile = oldAttachedFileList.get(i);
					if (0 == i) {
						builder.append("oldAttachedFile[");
					} else {
						builder.append(", oldAttachedFile[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(oldAttachedFile.toString());
				}
				builder.append("]");
			}
		}
		builder.append(", newAttachedFileCnt=");
		builder.append(newAttachedFileCnt);

		builder.append(", newAttachedFileList=");
		if (null == newAttachedFileList) {
			builder.append("null");
		} else {
			int newAttachedFileListSize = newAttachedFileList.size();
			if (0 == newAttachedFileListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < newAttachedFileListSize; i++) {
					NewAttachedFile newAttachedFile = newAttachedFileList.get(i);
					if (0 == i) {
						builder.append("newAttachedFile[");
					} else {
						builder.append(", newAttachedFile[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(newAttachedFile.toString());
				}
				builder.append("]");
			}
		}
		builder.append("]");
		return builder.toString();
	}
}