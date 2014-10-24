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
package kr.pe.sinnori.impl.message.BoardListOutDTO;

import kr.pe.sinnori.common.message.AbstractMessage;
/**
 * BoardListOutDTO 메시지
 * @author Won Jonghoon
 *
 */
public final class BoardListOutDTO extends AbstractMessage {
	private long boardId;
	private int startNo;
	private int pageSize;
	private int total;
	private int cnt;
	public static class Board {
		private long boardNo;
		private long groupNo;
		private long parentNo;
		private long groupSeq;
		private int depth;
		private long boardId;
		private String title;
		private String contents;
		private String regId;
		private String nickname;
		private int viewCount;
		private int votes;
		private String deleteFlag;
		private java.sql.Timestamp registerDate;
		private java.sql.Timestamp modifiedDate;
		private String memberGubunName;

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
		public long getParentNo() {
			return parentNo;
		}

		public void setParentNo(long parentNo) {
			this.parentNo = parentNo;
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
		public long getBoardId() {
			return boardId;
		}

		public void setBoardId(long boardId) {
			this.boardId = boardId;
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
		public String getRegId() {
			return regId;
		}

		public void setRegId(String regId) {
			this.regId = regId;
		}
		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
		}
		public int getViewCount() {
			return viewCount;
		}

		public void setViewCount(int viewCount) {
			this.viewCount = viewCount;
		}
		public int getVotes() {
			return votes;
		}

		public void setVotes(int votes) {
			this.votes = votes;
		}
		public String getDeleteFlag() {
			return deleteFlag;
		}

		public void setDeleteFlag(String deleteFlag) {
			this.deleteFlag = deleteFlag;
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
		public String getMemberGubunName() {
			return memberGubunName;
		}

		public void setMemberGubunName(String memberGubunName) {
			this.memberGubunName = memberGubunName;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Board[");
			builder.append("boardNo=");
			builder.append(boardNo);
			builder.append(", groupNo=");
			builder.append(groupNo);
			builder.append(", parentNo=");
			builder.append(parentNo);
			builder.append(", groupSeq=");
			builder.append(groupSeq);
			builder.append(", depth=");
			builder.append(depth);
			builder.append(", boardId=");
			builder.append(boardId);
			builder.append(", title=");
			builder.append(title);
			builder.append(", contents=");
			builder.append(contents);
			builder.append(", regId=");
			builder.append(regId);
			builder.append(", nickname=");
			builder.append(nickname);
			builder.append(", viewCount=");
			builder.append(viewCount);
			builder.append(", votes=");
			builder.append(votes);
			builder.append(", deleteFlag=");
			builder.append(deleteFlag);
			builder.append(", registerDate=");
			builder.append(registerDate);
			builder.append(", modifiedDate=");
			builder.append(modifiedDate);
			builder.append(", memberGubunName=");
			builder.append(memberGubunName);
			builder.append("]");
			return builder.toString();
		}
	};
	private java.util.List<Board> boardList;

	public long getBoardId() {
		return boardId;
	}

	public void setBoardId(long boardId) {
		this.boardId = boardId;
	}
	public int getStartNo() {
		return startNo;
	}

	public void setStartNo(int startNo) {
		this.startNo = startNo;
	}
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
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
		builder.append("class BoardListOutDTO[");
		builder.append("boardId=");
		builder.append(boardId);
		builder.append(", startNo=");
		builder.append(startNo);
		builder.append(", pageSize=");
		builder.append(pageSize);
		builder.append(", total=");
		builder.append(total);
		builder.append(", cnt=");
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