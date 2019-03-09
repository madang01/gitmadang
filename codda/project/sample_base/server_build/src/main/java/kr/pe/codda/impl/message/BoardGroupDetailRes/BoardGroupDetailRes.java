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

package kr.pe.codda.impl.message.BoardGroupDetailRes;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * BoardGroupDetailRes message
 * @author Won Jonghoon
 *
 */
public class BoardGroupDetailRes extends AbstractMessage {
	private short boardID;
	private long rootBoardNo;
	private int rootViewCount;
	private String rootBoardSate;
	private String rootNickname;
	private int rootVotes;
	private String rootSubject;
	private String rootContents;
	private String rootWriterID;
	private java.sql.Timestamp rootRegisteredDate;
	private String rootLastModifierID;
	private String rootLastModifierNickName;
	private java.sql.Timestamp rootLastModifiedDate;
	private short rootNextAttachedFileSeq;
	private int rootAttachedFileCnt;

	public static class RootAttachedFile {
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
			builder.append("RootAttachedFile[");
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

	private java.util.List<RootAttachedFile> rootAttachedFileList;
	private int cnt;

	public static class ChildBoard {
		private long boardNo;
		private long groupNo;
		private int groupSeq;
		private long parentNo;
		private short depth;
		private String writerID;
		private String writerNickname;
		private int viewCount;
		private String boardSate;
		private java.sql.Timestamp registeredDate;
		private int votes;
		private String subject;
		private java.sql.Timestamp lastModifiedDate;
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
		public String getWriterID() {
			return writerID;
		}

		public void setWriterID(String writerID) {
			this.writerID = writerID;
		}
		public String getWriterNickname() {
			return writerNickname;
		}

		public void setWriterNickname(String writerNickname) {
			this.writerNickname = writerNickname;
		}
		public int getViewCount() {
			return viewCount;
		}

		public void setViewCount(int viewCount) {
			this.viewCount = viewCount;
		}
		public String getBoardSate() {
			return boardSate;
		}

		public void setBoardSate(String boardSate) {
			this.boardSate = boardSate;
		}
		public java.sql.Timestamp getRegisteredDate() {
			return registeredDate;
		}

		public void setRegisteredDate(java.sql.Timestamp registeredDate) {
			this.registeredDate = registeredDate;
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
		public java.sql.Timestamp getLastModifiedDate() {
			return lastModifiedDate;
		}

		public void setLastModifiedDate(java.sql.Timestamp lastModifiedDate) {
			this.lastModifiedDate = lastModifiedDate;
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
			builder.append("ChildBoard[");
			builder.append("boardNo=");
			builder.append(boardNo);
			builder.append(", groupNo=");
			builder.append(groupNo);
			builder.append(", groupSeq=");
			builder.append(groupSeq);
			builder.append(", parentNo=");
			builder.append(parentNo);
			builder.append(", depth=");
			builder.append(depth);
			builder.append(", writerID=");
			builder.append(writerID);
			builder.append(", writerNickname=");
			builder.append(writerNickname);
			builder.append(", viewCount=");
			builder.append(viewCount);
			builder.append(", boardSate=");
			builder.append(boardSate);
			builder.append(", registeredDate=");
			builder.append(registeredDate);
			builder.append(", votes=");
			builder.append(votes);
			builder.append(", subject=");
			builder.append(subject);
			builder.append(", lastModifiedDate=");
			builder.append(lastModifiedDate);
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

	private java.util.List<ChildBoard> childBoardList;

	public short getBoardID() {
		return boardID;
	}

	public void setBoardID(short boardID) {
		this.boardID = boardID;
	}
	public long getRootBoardNo() {
		return rootBoardNo;
	}

	public void setRootBoardNo(long rootBoardNo) {
		this.rootBoardNo = rootBoardNo;
	}
	public int getRootViewCount() {
		return rootViewCount;
	}

	public void setRootViewCount(int rootViewCount) {
		this.rootViewCount = rootViewCount;
	}
	public String getRootBoardSate() {
		return rootBoardSate;
	}

	public void setRootBoardSate(String rootBoardSate) {
		this.rootBoardSate = rootBoardSate;
	}
	public String getRootNickname() {
		return rootNickname;
	}

	public void setRootNickname(String rootNickname) {
		this.rootNickname = rootNickname;
	}
	public int getRootVotes() {
		return rootVotes;
	}

	public void setRootVotes(int rootVotes) {
		this.rootVotes = rootVotes;
	}
	public String getRootSubject() {
		return rootSubject;
	}

	public void setRootSubject(String rootSubject) {
		this.rootSubject = rootSubject;
	}
	public String getRootContents() {
		return rootContents;
	}

	public void setRootContents(String rootContents) {
		this.rootContents = rootContents;
	}
	public String getRootWriterID() {
		return rootWriterID;
	}

	public void setRootWriterID(String rootWriterID) {
		this.rootWriterID = rootWriterID;
	}
	public java.sql.Timestamp getRootRegisteredDate() {
		return rootRegisteredDate;
	}

	public void setRootRegisteredDate(java.sql.Timestamp rootRegisteredDate) {
		this.rootRegisteredDate = rootRegisteredDate;
	}
	public String getRootLastModifierID() {
		return rootLastModifierID;
	}

	public void setRootLastModifierID(String rootLastModifierID) {
		this.rootLastModifierID = rootLastModifierID;
	}
	public String getRootLastModifierNickName() {
		return rootLastModifierNickName;
	}

	public void setRootLastModifierNickName(String rootLastModifierNickName) {
		this.rootLastModifierNickName = rootLastModifierNickName;
	}
	public java.sql.Timestamp getRootLastModifiedDate() {
		return rootLastModifiedDate;
	}

	public void setRootLastModifiedDate(java.sql.Timestamp rootLastModifiedDate) {
		this.rootLastModifiedDate = rootLastModifiedDate;
	}
	public short getRootNextAttachedFileSeq() {
		return rootNextAttachedFileSeq;
	}

	public void setRootNextAttachedFileSeq(short rootNextAttachedFileSeq) {
		this.rootNextAttachedFileSeq = rootNextAttachedFileSeq;
	}
	public int getRootAttachedFileCnt() {
		return rootAttachedFileCnt;
	}

	public void setRootAttachedFileCnt(int rootAttachedFileCnt) {
		this.rootAttachedFileCnt = rootAttachedFileCnt;
	}
	public java.util.List<RootAttachedFile> getRootAttachedFileList() {
		return rootAttachedFileList;
	}

	public void setRootAttachedFileList(java.util.List<RootAttachedFile> rootAttachedFileList) {
		this.rootAttachedFileList = rootAttachedFileList;
	}
	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public java.util.List<ChildBoard> getChildBoardList() {
		return childBoardList;
	}

	public void setChildBoardList(java.util.List<ChildBoard> childBoardList) {
		this.childBoardList = childBoardList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("boardGroupDetailRes[");
		builder.append("boardID=");
		builder.append(boardID);
		builder.append(", rootBoardNo=");
		builder.append(rootBoardNo);
		builder.append(", rootViewCount=");
		builder.append(rootViewCount);
		builder.append(", rootBoardSate=");
		builder.append(rootBoardSate);
		builder.append(", rootNickname=");
		builder.append(rootNickname);
		builder.append(", rootVotes=");
		builder.append(rootVotes);
		builder.append(", rootSubject=");
		builder.append(rootSubject);
		builder.append(", rootContents=");
		builder.append(rootContents);
		builder.append(", rootWriterID=");
		builder.append(rootWriterID);
		builder.append(", rootRegisteredDate=");
		builder.append(rootRegisteredDate);
		builder.append(", rootLastModifierID=");
		builder.append(rootLastModifierID);
		builder.append(", rootLastModifierNickName=");
		builder.append(rootLastModifierNickName);
		builder.append(", rootLastModifiedDate=");
		builder.append(rootLastModifiedDate);
		builder.append(", rootNextAttachedFileSeq=");
		builder.append(rootNextAttachedFileSeq);
		builder.append(", rootAttachedFileCnt=");
		builder.append(rootAttachedFileCnt);

		builder.append(", rootAttachedFileList=");
		if (null == rootAttachedFileList) {
			builder.append("null");
		} else {
			int rootAttachedFileListSize = rootAttachedFileList.size();
			if (0 == rootAttachedFileListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < rootAttachedFileListSize; i++) {
					RootAttachedFile rootAttachedFile = rootAttachedFileList.get(i);
					if (0 == i) {
						builder.append("rootAttachedFile[");
					} else {
						builder.append(", rootAttachedFile[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(rootAttachedFile.toString());
				}
				builder.append("]");
			}
		}
		builder.append(", cnt=");
		builder.append(cnt);

		builder.append(", childBoardList=");
		if (null == childBoardList) {
			builder.append("null");
		} else {
			int childBoardListSize = childBoardList.size();
			if (0 == childBoardListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < childBoardListSize; i++) {
					ChildBoard childBoard = childBoardList.get(i);
					if (0 == i) {
						builder.append("childBoard[");
					} else {
						builder.append(", childBoard[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(childBoard.toString());
				}
				builder.append("]");
			}
		}
		builder.append("]");
		return builder.toString();
	}
}