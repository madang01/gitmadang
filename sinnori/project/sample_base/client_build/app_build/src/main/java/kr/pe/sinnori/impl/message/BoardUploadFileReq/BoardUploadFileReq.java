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
	private int selectedOldAttachFileCnt;

	public static class SelectedOldAttachFile {
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
			builder.append("SelectedOldAttachFile[");
			builder.append("attachSeq=");
			builder.append(attachSeq);
			builder.append("]");
			return builder.toString();
		}
	}

	private java.util.List<SelectedOldAttachFile> selectedOldAttachFileList;
	private int newAttachFileCnt;

	public static class NewAttachFile {
		private String attachFileName;
		private String systemFileName;

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
			builder.append("NewAttachFile[");
			builder.append("attachFileName=");
			builder.append(attachFileName);
			builder.append(", systemFileName=");
			builder.append(systemFileName);
			builder.append("]");
			return builder.toString();
		}
	}

	private java.util.List<NewAttachFile> newAttachFileList;

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
	public int getSelectedOldAttachFileCnt() {
		return selectedOldAttachFileCnt;
	}

	public void setSelectedOldAttachFileCnt(int selectedOldAttachFileCnt) {
		this.selectedOldAttachFileCnt = selectedOldAttachFileCnt;
	}
	public java.util.List<SelectedOldAttachFile> getSelectedOldAttachFileList() {
		return selectedOldAttachFileList;
	}

	public void setSelectedOldAttachFileList(java.util.List<SelectedOldAttachFile> selectedOldAttachFileList) {
		this.selectedOldAttachFileList = selectedOldAttachFileList;
	}
	public int getNewAttachFileCnt() {
		return newAttachFileCnt;
	}

	public void setNewAttachFileCnt(int newAttachFileCnt) {
		this.newAttachFileCnt = newAttachFileCnt;
	}
	public java.util.List<NewAttachFile> getNewAttachFileList() {
		return newAttachFileList;
	}

	public void setNewAttachFileList(java.util.List<NewAttachFile> newAttachFileList) {
		this.newAttachFileList = newAttachFileList;
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
		builder.append(", selectedOldAttachFileCnt=");
		builder.append(selectedOldAttachFileCnt);

		builder.append(", selectedOldAttachFileList=");
		if (null == selectedOldAttachFileList) {
			builder.append("null");
		} else {
			int selectedOldAttachFileListSize = selectedOldAttachFileList.size();
			if (0 == selectedOldAttachFileListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < selectedOldAttachFileListSize; i++) {
					SelectedOldAttachFile selectedOldAttachFile = selectedOldAttachFileList.get(i);
					if (0 == i) {
						builder.append("selectedOldAttachFile[");
					} else {
						builder.append(", selectedOldAttachFile[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(selectedOldAttachFile.toString());
				}
				builder.append("]");
			}
		}
		builder.append(", newAttachFileCnt=");
		builder.append(newAttachFileCnt);

		builder.append(", newAttachFileList=");
		if (null == newAttachFileList) {
			builder.append("null");
		} else {
			int newAttachFileListSize = newAttachFileList.size();
			if (0 == newAttachFileListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < newAttachFileListSize; i++) {
					NewAttachFile newAttachFile = newAttachFileList.get(i);
					if (0 == i) {
						builder.append("newAttachFile[");
					} else {
						builder.append(", newAttachFile[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(newAttachFile.toString());
				}
				builder.append("]");
			}
		}
		builder.append("]");
		return builder.toString();
	}
}