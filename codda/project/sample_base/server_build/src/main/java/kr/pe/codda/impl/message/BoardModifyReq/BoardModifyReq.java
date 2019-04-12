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

package kr.pe.codda.impl.message.BoardModifyReq;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * BoardModifyReq message
 * @author Won Jonghoon
 *
 */
public class BoardModifyReq extends AbstractMessage {
	private String requestedUserID;
	private String ip;
	private short boardID;
	private long boardNo;
	private String pwdHashBase64;
	private String subject;
	private String contents;
	private short nextAttachedFileSeq;
	private int oldAttachedFileCnt;

	public static class OldAttachedFile {
		private short attachedFileSeq;

		public short getAttachedFileSeq() {
			return attachedFileSeq;
		}

		public void setAttachedFileSeq(short attachedFileSeq) {
			this.attachedFileSeq = attachedFileSeq;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("OldAttachedFile[");
			builder.append("attachedFileSeq=");
			builder.append(attachedFileSeq);
			builder.append("]");
			return builder.toString();
		}
	}

	private java.util.List<OldAttachedFile> oldAttachedFileList;
	private int newAttachedFileCnt;

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
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	public short getBoardID() {
		return boardID;
	}

	public void setBoardID(short boardID) {
		this.boardID = boardID;
	}
	public long getBoardNo() {
		return boardNo;
	}

	public void setBoardNo(long boardNo) {
		this.boardNo = boardNo;
	}
	public String getPwdHashBase64() {
		return pwdHashBase64;
	}

	public void setPwdHashBase64(String pwdHashBase64) {
		this.pwdHashBase64 = pwdHashBase64;
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
	public short getNextAttachedFileSeq() {
		return nextAttachedFileSeq;
	}

	public void setNextAttachedFileSeq(short nextAttachedFileSeq) {
		this.nextAttachedFileSeq = nextAttachedFileSeq;
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
		builder.append("boardModifyReq[");
		builder.append("requestedUserID=");
		builder.append(requestedUserID);
		builder.append(", ip=");
		builder.append(ip);
		builder.append(", boardID=");
		builder.append(boardID);
		builder.append(", boardNo=");
		builder.append(boardNo);
		builder.append(", pwdHashBase64=");
		builder.append(pwdHashBase64);
		builder.append(", subject=");
		builder.append(subject);
		builder.append(", contents=");
		builder.append(contents);
		builder.append(", nextAttachedFileSeq=");
		builder.append(nextAttachedFileSeq);
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