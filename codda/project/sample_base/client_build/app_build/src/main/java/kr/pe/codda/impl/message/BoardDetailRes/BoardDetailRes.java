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

package kr.pe.codda.impl.message.BoardDetailRes;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * BoardDetailRes message
 * @author Won Jonghoon
 *
 */
public class BoardDetailRes extends AbstractMessage {
	private short boardID;
	private String boardName;
	private byte boardListType;
	private byte boardReplyPolicyType;
	private byte boardReplyPermssionType;
	private long boardNo;
	private long groupNo;
	private int groupSeq;
	private long parentNo;
	private short depth;
	private int viewCount;
	private byte boardSate;
	private int votes;
	private String subject;
	private String contents;
	private String firstWriterID;
	private String firstWriterNickname;
	private java.sql.Timestamp firstRegisteredDate;
	private String lastModifierID;
	private String lastModifierNickName;
	private java.sql.Timestamp lastModifiedDate;
	private short nextAttachedFileSeq;
	private boolean isBoardPassword;
	private int attachedFileCnt;

	public static class AttachedFile {
		private short attachedFileSeq;
		private String attachedFileName;
		private long attachedFileSize;

		public short getAttachedFileSeq() {
			return attachedFileSeq;
		}

		public void setAttachedFileSeq(short attachedFileSeq) {
			this.attachedFileSeq = attachedFileSeq;
		}
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
			builder.append("AttachedFile[");
			builder.append("attachedFileSeq=");
			builder.append(attachedFileSeq);
			builder.append(", attachedFileName=");
			builder.append(attachedFileName);
			builder.append(", attachedFileSize=");
			builder.append(attachedFileSize);
			builder.append("]");
			return builder.toString();
		}
	}

	private java.util.List<AttachedFile> attachedFileList;
	private int childNodeCnt;

	public static class ChildNode {
		private long boardNo;
		private int groupSeq;
		private long parentNo;
		private short depth;
		private String contents;
		private int votes;
		private byte boardSate;
		private String firstWriterID;
		private String firstWriterNickname;
		private java.sql.Timestamp firstRegisteredDate;
		private String lastModifierID;
		private String lastModifierNickName;
		private java.sql.Timestamp lastModifiedDate;
		private short nextAttachedFileSeq;
		private boolean isBoardPassword;
		private int attachedFileCnt;

		public static class AttachedFile {
			private short attachedFileSeq;
			private String attachedFileName;
			private long attachedFileSize;

			public short getAttachedFileSeq() {
				return attachedFileSeq;
			}

			public void setAttachedFileSeq(short attachedFileSeq) {
				this.attachedFileSeq = attachedFileSeq;
			}
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
				builder.append("AttachedFile[");
				builder.append("attachedFileSeq=");
				builder.append(attachedFileSeq);
				builder.append(", attachedFileName=");
				builder.append(attachedFileName);
				builder.append(", attachedFileSize=");
				builder.append(attachedFileSize);
				builder.append("]");
				return builder.toString();
			}
		}

		private java.util.List<AttachedFile> attachedFileList;

		public long getBoardNo() {
			return boardNo;
		}

		public void setBoardNo(long boardNo) {
			this.boardNo = boardNo;
		}
		public int getGroupSeq() {
			return groupSeq;
		}

		public void setGroupSeq(int groupSeq) {
			this.groupSeq = groupSeq;
		}
		public long getParentNo() {
			return parentNo;
		}

		public void setParentNo(long parentNo) {
			this.parentNo = parentNo;
		}
		public short getDepth() {
			return depth;
		}

		public void setDepth(short depth) {
			this.depth = depth;
		}
		public String getContents() {
			return contents;
		}

		public void setContents(String contents) {
			this.contents = contents;
		}
		public int getVotes() {
			return votes;
		}

		public void setVotes(int votes) {
			this.votes = votes;
		}
		public byte getBoardSate() {
			return boardSate;
		}

		public void setBoardSate(byte boardSate) {
			this.boardSate = boardSate;
		}
		public String getFirstWriterID() {
			return firstWriterID;
		}

		public void setFirstWriterID(String firstWriterID) {
			this.firstWriterID = firstWriterID;
		}
		public String getFirstWriterNickname() {
			return firstWriterNickname;
		}

		public void setFirstWriterNickname(String firstWriterNickname) {
			this.firstWriterNickname = firstWriterNickname;
		}
		public java.sql.Timestamp getFirstRegisteredDate() {
			return firstRegisteredDate;
		}

		public void setFirstRegisteredDate(java.sql.Timestamp firstRegisteredDate) {
			this.firstRegisteredDate = firstRegisteredDate;
		}
		public String getLastModifierID() {
			return lastModifierID;
		}

		public void setLastModifierID(String lastModifierID) {
			this.lastModifierID = lastModifierID;
		}
		public String getLastModifierNickName() {
			return lastModifierNickName;
		}

		public void setLastModifierNickName(String lastModifierNickName) {
			this.lastModifierNickName = lastModifierNickName;
		}
		public java.sql.Timestamp getLastModifiedDate() {
			return lastModifiedDate;
		}

		public void setLastModifiedDate(java.sql.Timestamp lastModifiedDate) {
			this.lastModifiedDate = lastModifiedDate;
		}
		public short getNextAttachedFileSeq() {
			return nextAttachedFileSeq;
		}

		public void setNextAttachedFileSeq(short nextAttachedFileSeq) {
			this.nextAttachedFileSeq = nextAttachedFileSeq;
		}
		public boolean getIsBoardPassword() {
			return isBoardPassword;
		}

		public void setIsBoardPassword(boolean isBoardPassword) {
			this.isBoardPassword = isBoardPassword;
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
			builder.append("ChildNode[");
			builder.append("boardNo=");
			builder.append(boardNo);
			builder.append(", groupSeq=");
			builder.append(groupSeq);
			builder.append(", parentNo=");
			builder.append(parentNo);
			builder.append(", depth=");
			builder.append(depth);
			builder.append(", contents=");
			builder.append(contents);
			builder.append(", votes=");
			builder.append(votes);
			builder.append(", boardSate=");
			builder.append(boardSate);
			builder.append(", firstWriterID=");
			builder.append(firstWriterID);
			builder.append(", firstWriterNickname=");
			builder.append(firstWriterNickname);
			builder.append(", firstRegisteredDate=");
			builder.append(firstRegisteredDate);
			builder.append(", lastModifierID=");
			builder.append(lastModifierID);
			builder.append(", lastModifierNickName=");
			builder.append(lastModifierNickName);
			builder.append(", lastModifiedDate=");
			builder.append(lastModifiedDate);
			builder.append(", nextAttachedFileSeq=");
			builder.append(nextAttachedFileSeq);
			builder.append(", isBoardPassword=");
			builder.append(isBoardPassword);
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

	private java.util.List<ChildNode> childNodeList;

	public short getBoardID() {
		return boardID;
	}

	public void setBoardID(short boardID) {
		this.boardID = boardID;
	}
	public String getBoardName() {
		return boardName;
	}

	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}
	public byte getBoardListType() {
		return boardListType;
	}

	public void setBoardListType(byte boardListType) {
		this.boardListType = boardListType;
	}
	public byte getBoardReplyPolicyType() {
		return boardReplyPolicyType;
	}

	public void setBoardReplyPolicyType(byte boardReplyPolicyType) {
		this.boardReplyPolicyType = boardReplyPolicyType;
	}
	public byte getBoardReplyPermssionType() {
		return boardReplyPermssionType;
	}

	public void setBoardReplyPermssionType(byte boardReplyPermssionType) {
		this.boardReplyPermssionType = boardReplyPermssionType;
	}
	public long getBoardNo() {
		return boardNo;
	}

	public void setBoardNo(long boardNo) {
		this.boardNo = boardNo;
	}
	public long getGroupNo() {
		return groupNo;
	}

	public void setGroupNo(long groupNo) {
		this.groupNo = groupNo;
	}
	public int getGroupSeq() {
		return groupSeq;
	}

	public void setGroupSeq(int groupSeq) {
		this.groupSeq = groupSeq;
	}
	public long getParentNo() {
		return parentNo;
	}

	public void setParentNo(long parentNo) {
		this.parentNo = parentNo;
	}
	public short getDepth() {
		return depth;
	}

	public void setDepth(short depth) {
		this.depth = depth;
	}
	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}
	public byte getBoardSate() {
		return boardSate;
	}

	public void setBoardSate(byte boardSate) {
		this.boardSate = boardSate;
	}
	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
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
	public String getFirstWriterID() {
		return firstWriterID;
	}

	public void setFirstWriterID(String firstWriterID) {
		this.firstWriterID = firstWriterID;
	}
	public String getFirstWriterNickname() {
		return firstWriterNickname;
	}

	public void setFirstWriterNickname(String firstWriterNickname) {
		this.firstWriterNickname = firstWriterNickname;
	}
	public java.sql.Timestamp getFirstRegisteredDate() {
		return firstRegisteredDate;
	}

	public void setFirstRegisteredDate(java.sql.Timestamp firstRegisteredDate) {
		this.firstRegisteredDate = firstRegisteredDate;
	}
	public String getLastModifierID() {
		return lastModifierID;
	}

	public void setLastModifierID(String lastModifierID) {
		this.lastModifierID = lastModifierID;
	}
	public String getLastModifierNickName() {
		return lastModifierNickName;
	}

	public void setLastModifierNickName(String lastModifierNickName) {
		this.lastModifierNickName = lastModifierNickName;
	}
	public java.sql.Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(java.sql.Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public short getNextAttachedFileSeq() {
		return nextAttachedFileSeq;
	}

	public void setNextAttachedFileSeq(short nextAttachedFileSeq) {
		this.nextAttachedFileSeq = nextAttachedFileSeq;
	}
	public boolean getIsBoardPassword() {
		return isBoardPassword;
	}

	public void setIsBoardPassword(boolean isBoardPassword) {
		this.isBoardPassword = isBoardPassword;
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
	public int getChildNodeCnt() {
		return childNodeCnt;
	}

	public void setChildNodeCnt(int childNodeCnt) {
		this.childNodeCnt = childNodeCnt;
	}
	public java.util.List<ChildNode> getChildNodeList() {
		return childNodeList;
	}

	public void setChildNodeList(java.util.List<ChildNode> childNodeList) {
		this.childNodeList = childNodeList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("boardDetailRes[");
		builder.append("boardID=");
		builder.append(boardID);
		builder.append(", boardName=");
		builder.append(boardName);
		builder.append(", boardListType=");
		builder.append(boardListType);
		builder.append(", boardReplyPolicyType=");
		builder.append(boardReplyPolicyType);
		builder.append(", boardReplyPermssionType=");
		builder.append(boardReplyPermssionType);
		builder.append(", boardNo=");
		builder.append(boardNo);
		builder.append(", groupNo=");
		builder.append(groupNo);
		builder.append(", groupSeq=");
		builder.append(groupSeq);
		builder.append(", parentNo=");
		builder.append(parentNo);
		builder.append(", depth=");
		builder.append(depth);
		builder.append(", viewCount=");
		builder.append(viewCount);
		builder.append(", boardSate=");
		builder.append(boardSate);
		builder.append(", votes=");
		builder.append(votes);
		builder.append(", subject=");
		builder.append(subject);
		builder.append(", contents=");
		builder.append(contents);
		builder.append(", firstWriterID=");
		builder.append(firstWriterID);
		builder.append(", firstWriterNickname=");
		builder.append(firstWriterNickname);
		builder.append(", firstRegisteredDate=");
		builder.append(firstRegisteredDate);
		builder.append(", lastModifierID=");
		builder.append(lastModifierID);
		builder.append(", lastModifierNickName=");
		builder.append(lastModifierNickName);
		builder.append(", lastModifiedDate=");
		builder.append(lastModifiedDate);
		builder.append(", nextAttachedFileSeq=");
		builder.append(nextAttachedFileSeq);
		builder.append(", isBoardPassword=");
		builder.append(isBoardPassword);
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
		builder.append(", childNodeCnt=");
		builder.append(childNodeCnt);

		builder.append(", childNodeList=");
		if (null == childNodeList) {
			builder.append("null");
		} else {
			int childNodeListSize = childNodeList.size();
			if (0 == childNodeListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < childNodeListSize; i++) {
					ChildNode childNode = childNodeList.get(i);
					if (0 == i) {
						builder.append("childNode[");
					} else {
						builder.append(", childNode[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(childNode.toString());
				}
				builder.append("]");
			}
		}
		builder.append("]");
		return builder.toString();
	}
}