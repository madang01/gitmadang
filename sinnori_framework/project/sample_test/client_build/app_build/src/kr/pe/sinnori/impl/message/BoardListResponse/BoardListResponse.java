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
package kr.pe.sinnori.impl.message.BoardListResponse;

import kr.pe.sinnori.common.message.AbstractMessage;
/**
 * BoardListResponse 메시지
 * @author Won Jonghoon
 *
 */
public final class BoardListResponse extends AbstractMessage {
	private int cnt;
	public static class Board {
		private long boardNO;
		private long groupNO;
		private long parentNO;
		private long groupSeq;
		private int depth;
		private long boardTypeID;
		private String title;
		private String contents;
		private String userID;
		private int viewCnt;
		private int votes;
		private String isDeleted;
		private java.sql.Timestamp createDate;
		private java.sql.Timestamp lastModifiedDate;
		private int memberGubun;

		public long getBoardNO() {
			return boardNO;
		}

		public void setBoardNO(long boardNO) {
			this.boardNO = boardNO;
		}
		public long getGroupNO() {
			return groupNO;
		}

		public void setGroupNO(long groupNO) {
			this.groupNO = groupNO;
		}
		public long getParentNO() {
			return parentNO;
		}

		public void setParentNO(long parentNO) {
			this.parentNO = parentNO;
		}
		public long getGroupSeq() {
			return groupSeq;
		}

		public void setGroupSeq(long groupSeq) {
			this.groupSeq = groupSeq;
		}
		public int getDepth() {
			return depth;
		}

		public void setDepth(int depth) {
			this.depth = depth;
		}
		public long getBoardTypeID() {
			return boardTypeID;
		}

		public void setBoardTypeID(long boardTypeID) {
			this.boardTypeID = boardTypeID;
		}
		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
		public String getContents() {
			return contents;
		}

		public void setContents(String contents) {
			this.contents = contents;
		}
		public String getUserID() {
			return userID;
		}

		public void setUserID(String userID) {
			this.userID = userID;
		}
		public int getViewCnt() {
			return viewCnt;
		}

		public void setViewCnt(int viewCnt) {
			this.viewCnt = viewCnt;
		}
		public int getVotes() {
			return votes;
		}

		public void setVotes(int votes) {
			this.votes = votes;
		}
		public String getIsDeleted() {
			return isDeleted;
		}

		public void setIsDeleted(String isDeleted) {
			this.isDeleted = isDeleted;
		}
		public java.sql.Timestamp getCreateDate() {
			return createDate;
		}

		public void setCreateDate(java.sql.Timestamp createDate) {
			this.createDate = createDate;
		}
		public java.sql.Timestamp getLastModifiedDate() {
			return lastModifiedDate;
		}

		public void setLastModifiedDate(java.sql.Timestamp lastModifiedDate) {
			this.lastModifiedDate = lastModifiedDate;
		}
		public int getMemberGubun() {
			return memberGubun;
		}

		public void setMemberGubun(int memberGubun) {
			this.memberGubun = memberGubun;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Board[");
			builder.append("boardNO=");
			builder.append(boardNO);
			builder.append(", groupNO=");
			builder.append(groupNO);
			builder.append(", parentNO=");
			builder.append(parentNO);
			builder.append(", groupSeq=");
			builder.append(groupSeq);
			builder.append(", depth=");
			builder.append(depth);
			builder.append(", boardTypeID=");
			builder.append(boardTypeID);
			builder.append(", title=");
			builder.append(title);
			builder.append(", contents=");
			builder.append(contents);
			builder.append(", userID=");
			builder.append(userID);
			builder.append(", viewCnt=");
			builder.append(viewCnt);
			builder.append(", votes=");
			builder.append(votes);
			builder.append(", isDeleted=");
			builder.append(isDeleted);
			builder.append(", createDate=");
			builder.append(createDate);
			builder.append(", lastModifiedDate=");
			builder.append(lastModifiedDate);
			builder.append(", memberGubun=");
			builder.append(memberGubun);
			builder.append("]");
			return builder.toString();
		}
	};
	private java.util.List<Board> boardList;

	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}

	public java.util.List<Board> getBoardList() {
		return boardList;
	}

	public void setBoardList(java.util.List<Board> boardList) {
		this.boardList = boardList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("class BoardListResponse[");
		builder.append("cnt=");
		builder.append(cnt);
		builder.append(", boardList=");
		if (null == boardList) {
			builder.append("null");
		} else {
			int boardListSize = boardList.size();
			if (0 == boardListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < boardListSize; i++) {
					Board board = boardList.get(i);
					if (0 == i) {
						builder.append("board[");
					} else {
						builder.append(", board[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(board.toString());
				}
				builder.append("]");
			}
		}

		builder.append(", messageHeaderInfo=");
		builder.append(messageHeaderInfo.toString());
		builder.append("]");
		return builder.toString();
	}
}