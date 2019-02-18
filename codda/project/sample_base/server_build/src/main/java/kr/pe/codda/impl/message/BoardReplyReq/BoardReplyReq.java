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

package kr.pe.codda.impl.message.BoardReplyReq;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * BoardReplyReq message
 * @author Won Jonghoon
 *
 */
public class BoardReplyReq extends AbstractMessage {
	private String requestedUserID;
	private short boardID;
	private long parentBoardNo;
	private String subject;
	private String contents;
	private String ip;
	private short newAttachedFileCnt;

	public static class NewAttachedFile {
		private String attachedFileName;
		private long attachedFileSize;

		public String getAttachedFileName() {
			return attachedFileName;
		}

		public void setAttachedFileName(String attachedFileName) {
			this.attachedFileName = attachedFileName;
		}
		public long getAttachedFileSize() {
			return attachedFileSize;
		}

		public void setAttachedFileSize(long attachedFileSize) {
			this.attachedFileSize = attachedFileSize;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("NewAttachedFile[");
			builder.append("attachedFileName=");
			builder.append(attachedFileName);
			builder.append(", attachedFileSize=");
			builder.append(attachedFileSize);
			builder.append("]");
			return builder.toString();
		}
	}

	private java.util.List<NewAttachedFile> newAttachedFileList;

	public String getRequestedUserID() {
		return requestedUserID;
	}

	public void setRequestedUserID(String requestedUserID) {
		this.requestedUserID = requestedUserID;
	}
	public short getBoardID() {
		return boardID;
	}

	public void setBoardID(short boardID) {
		this.boardID = boardID;
	}
	public long getParentBoardNo() {
		return parentBoardNo;
	}

	public void setParentBoardNo(long parentBoardNo) {
		this.parentBoardNo = parentBoardNo;
	}
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	public short getNewAttachedFileCnt() {
		return newAttachedFileCnt;
	}

	public void setNewAttachedFileCnt(short newAttachedFileCnt) {
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
		builder.append("boardReplyReq[");
		builder.append("requestedUserID=");
		builder.append(requestedUserID);
		builder.append(", boardID=");
		builder.append(boardID);
		builder.append(", parentBoardNo=");
		builder.append(parentBoardNo);
		builder.append(", subject=");
		builder.append(subject);
		builder.append(", contents=");
		builder.append(contents);
		builder.append(", ip=");
		builder.append(ip);
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