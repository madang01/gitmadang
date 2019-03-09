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

package kr.pe.codda.impl.message.BoardInfoListRes;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * BoardInfoListRes message
 * @author Won Jonghoon
 *
 */
public class BoardInfoListRes extends AbstractMessage {
	private int cnt;

	public static class BoardInfo {
		private short boardID;
		private String boardName;
		private String boardInformation;
		private byte boardListType;
		private byte boardReplyPolicyType;
		private byte boardWritePermissionType;
		private byte boardReplyPermissionType;
		private int cnt;
		private int total;
		private long nextBoardNo;

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
		public String getBoardInformation() {
			return boardInformation;
		}

		public void setBoardInformation(String boardInformation) {
			this.boardInformation = boardInformation;
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
		public byte getBoardWritePermissionType() {
			return boardWritePermissionType;
		}

		public void setBoardWritePermissionType(byte boardWritePermissionType) {
			this.boardWritePermissionType = boardWritePermissionType;
		}
		public byte getBoardReplyPermissionType() {
			return boardReplyPermissionType;
		}

		public void setBoardReplyPermissionType(byte boardReplyPermissionType) {
			this.boardReplyPermissionType = boardReplyPermissionType;
		}
		public int getCnt() {
			return cnt;
		}

		public void setCnt(int cnt) {
			this.cnt = cnt;
		}
		public int getTotal() {
			return total;
		}

		public void setTotal(int total) {
			this.total = total;
		}
		public long getNextBoardNo() {
			return nextBoardNo;
		}

		public void setNextBoardNo(long nextBoardNo) {
			this.nextBoardNo = nextBoardNo;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("BoardInfo[");
			builder.append("boardID=");
			builder.append(boardID);
			builder.append(", boardName=");
			builder.append(boardName);
			builder.append(", boardInformation=");
			builder.append(boardInformation);
			builder.append(", boardListType=");
			builder.append(boardListType);
			builder.append(", boardReplyPolicyType=");
			builder.append(boardReplyPolicyType);
			builder.append(", boardWritePermissionType=");
			builder.append(boardWritePermissionType);
			builder.append(", boardReplyPermissionType=");
			builder.append(boardReplyPermissionType);
			builder.append(", cnt=");
			builder.append(cnt);
			builder.append(", total=");
			builder.append(total);
			builder.append(", nextBoardNo=");
			builder.append(nextBoardNo);
			builder.append("]");
			return builder.toString();
		}
	}

	private java.util.List<BoardInfo> boardInfoList;

	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public java.util.List<BoardInfo> getBoardInfoList() {
		return boardInfoList;
	}

	public void setBoardInfoList(java.util.List<BoardInfo> boardInfoList) {
		this.boardInfoList = boardInfoList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("boardInfoListRes[");
		builder.append("cnt=");
		builder.append(cnt);

		builder.append(", boardInfoList=");
		if (null == boardInfoList) {
			builder.append("null");
		} else {
			int boardInfoListSize = boardInfoList.size();
			if (0 == boardInfoListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < boardInfoListSize; i++) {
					BoardInfo boardInfo = boardInfoList.get(i);
					if (0 == i) {
						builder.append("boardInfo[");
					} else {
						builder.append(", boardInfo[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(boardInfo.toString());
				}
				builder.append("]");
			}
		}
		builder.append("]");
		return builder.toString();
	}
}