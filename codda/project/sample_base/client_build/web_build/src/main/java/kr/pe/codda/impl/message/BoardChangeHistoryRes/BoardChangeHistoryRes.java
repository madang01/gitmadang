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

package kr.pe.codda.impl.message.BoardChangeHistoryRes;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * BoardChangeHistoryRes message
 * @author Won Jonghoon
 *
 */
public class BoardChangeHistoryRes extends AbstractMessage {
	private short boardID;
	private long boardNo;
	private byte boardListType;
	private long parentNo;
	private long groupNo;
	private int boardChangeHistoryCnt;

	public static class BoardChangeHistory {
		private short historySeq;
		private String subject;
		private String contents;
		private String writerID;
		private String writerNickname;
		private java.sql.Timestamp registeredDate;

		public short getHistorySeq() {
			return historySeq;
		}

		public void setHistorySeq(short historySeq) {
			this.historySeq = historySeq;
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
		public java.sql.Timestamp getRegisteredDate() {
			return registeredDate;
		}

		public void setRegisteredDate(java.sql.Timestamp registeredDate) {
			this.registeredDate = registeredDate;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("BoardChangeHistory[");
			builder.append("historySeq=");
			builder.append(historySeq);
			builder.append(", subject=");
			builder.append(subject);
			builder.append(", contents=");
			builder.append(contents);
			builder.append(", writerID=");
			builder.append(writerID);
			builder.append(", writerNickname=");
			builder.append(writerNickname);
			builder.append(", registeredDate=");
			builder.append(registeredDate);
			builder.append("]");
			return builder.toString();
		}
	}

	private java.util.List<BoardChangeHistory> boardChangeHistoryList;

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
	public byte getBoardListType() {
		return boardListType;
	}

	public void setBoardListType(byte boardListType) {
		this.boardListType = boardListType;
	}
	public long getParentNo() {
		return parentNo;
	}

	public void setParentNo(long parentNo) {
		this.parentNo = parentNo;
	}
	public long getGroupNo() {
		return groupNo;
	}

	public void setGroupNo(long groupNo) {
		this.groupNo = groupNo;
	}
	public int getBoardChangeHistoryCnt() {
		return boardChangeHistoryCnt;
	}

	public void setBoardChangeHistoryCnt(int boardChangeHistoryCnt) {
		this.boardChangeHistoryCnt = boardChangeHistoryCnt;
	}
	public java.util.List<BoardChangeHistory> getBoardChangeHistoryList() {
		return boardChangeHistoryList;
	}

	public void setBoardChangeHistoryList(java.util.List<BoardChangeHistory> boardChangeHistoryList) {
		this.boardChangeHistoryList = boardChangeHistoryList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("boardChangeHistoryRes[");
		builder.append("boardID=");
		builder.append(boardID);
		builder.append(", boardNo=");
		builder.append(boardNo);
		builder.append(", boardListType=");
		builder.append(boardListType);
		builder.append(", parentNo=");
		builder.append(parentNo);
		builder.append(", groupNo=");
		builder.append(groupNo);
		builder.append(", boardChangeHistoryCnt=");
		builder.append(boardChangeHistoryCnt);

		builder.append(", boardChangeHistoryList=");
		if (null == boardChangeHistoryList) {
			builder.append("null");
		} else {
			int boardChangeHistoryListSize = boardChangeHistoryList.size();
			if (0 == boardChangeHistoryListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < boardChangeHistoryListSize; i++) {
					BoardChangeHistory boardChangeHistory = boardChangeHistoryList.get(i);
					if (0 == i) {
						builder.append("boardChangeHistory[");
					} else {
						builder.append(", boardChangeHistory[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(boardChangeHistory.toString());
				}
				builder.append("]");
			}
		}
		builder.append("]");
		return builder.toString();
	}
}