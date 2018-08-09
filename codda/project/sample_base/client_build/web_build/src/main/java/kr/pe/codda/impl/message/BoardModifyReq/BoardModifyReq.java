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
	private short boardID;
	private long boardNo;
	private String subject;
	private String content;
	private String modifierID;
	private String ip;
	private int oldAttachedFileSeqCnt;

	public static class OldAttachedFileSeq {
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
			builder.append("OldAttachedFileSeq[");
			builder.append("attachedFileSeq=");
			builder.append(attachedFileSeq);
			builder.append("]");
			return builder.toString();
		}
	}

	private java.util.List<OldAttachedFileSeq> oldAttachedFileSeqList;
	private int newAttachedFileCnt;

	public static class NewAttachedFile {
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
			builder.append("NewAttachedFile[");
			builder.append("attachedFileName=");
			builder.append(attachedFileName);
			builder.append("]");
			return builder.toString();
		}
	}

	private java.util.List<NewAttachedFile> newAttachedFileList;

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
	public String getModifierID() {
		return modifierID;
	}

	public void setModifierID(String modifierID) {
		this.modifierID = modifierID;
	}
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getOldAttachedFileSeqCnt() {
		return oldAttachedFileSeqCnt;
	}

	public void setOldAttachedFileSeqCnt(int oldAttachedFileSeqCnt) {
		this.oldAttachedFileSeqCnt = oldAttachedFileSeqCnt;
	}
	public java.util.List<OldAttachedFileSeq> getOldAttachedFileSeqList() {
		return oldAttachedFileSeqList;
	}

	public void setOldAttachedFileSeqList(java.util.List<OldAttachedFileSeq> oldAttachedFileSeqList) {
		this.oldAttachedFileSeqList = oldAttachedFileSeqList;
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
		builder.append("boardID=");
		builder.append(boardID);
		builder.append(", boardNo=");
		builder.append(boardNo);
		builder.append(", subject=");
		builder.append(subject);
		builder.append(", content=");
		builder.append(content);
		builder.append(", modifierID=");
		builder.append(modifierID);
		builder.append(", ip=");
		builder.append(ip);
		builder.append(", oldAttachedFileSeqCnt=");
		builder.append(oldAttachedFileSeqCnt);

		builder.append(", oldAttachedFileSeqList=");
		if (null == oldAttachedFileSeqList) {
			builder.append("null");
		} else {
			int oldAttachedFileSeqListSize = oldAttachedFileSeqList.size();
			if (0 == oldAttachedFileSeqListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < oldAttachedFileSeqListSize; i++) {
					OldAttachedFileSeq oldAttachedFileSeq = oldAttachedFileSeqList.get(i);
					if (0 == i) {
						builder.append("oldAttachedFileSeq[");
					} else {
						builder.append(", oldAttachedFileSeq[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(oldAttachedFileSeq.toString());
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