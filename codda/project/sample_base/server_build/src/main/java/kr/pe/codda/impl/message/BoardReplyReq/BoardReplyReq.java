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
	private String requestUserID;
	private short boardID;
	private long parentBoardNo;
	private String subject;
	private String content;
	private String ip;
	private short attachedFileCnt;

	public static class AttachedFile {
		private String attachedFileName;

		public String getAttachedFileName() {
			return attachedFileName;
		}

		public void setAttachedFileName(String attachedFileName) {
			this.attachedFileName = attachedFileName;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("AttachedFile[");
			builder.append("attachedFileName=");
			builder.append(attachedFileName);
			builder.append("]");
			return builder.toString();
		}
	}

	private java.util.List<AttachedFile> attachedFileList;

	public String getRequestUserID() {
		return requestUserID;
	}

	public void setRequestUserID(String requestUserID) {
		this.requestUserID = requestUserID;
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
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	public short getAttachedFileCnt() {
		return attachedFileCnt;
	}

	public void setAttachedFileCnt(short attachedFileCnt) {
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
		builder.append("boardReplyReq[");
		builder.append("requestUserID=");
		builder.append(requestUserID);
		builder.append(", boardID=");
		builder.append(boardID);
		builder.append(", parentBoardNo=");
		builder.append(parentBoardNo);
		builder.append(", subject=");
		builder.append(subject);
		builder.append(", content=");
		builder.append(content);
		builder.append(", ip=");
		builder.append(ip);
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