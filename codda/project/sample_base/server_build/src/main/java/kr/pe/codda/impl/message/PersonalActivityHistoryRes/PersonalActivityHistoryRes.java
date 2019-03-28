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

package kr.pe.codda.impl.message.PersonalActivityHistoryRes;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * PersonalActivityHistoryRes message
 * @author Won Jonghoon
 *
 */
public class PersonalActivityHistoryRes extends AbstractMessage {
	private String targetUserID;
	private String targetUserNickname;
	private long total;
	private int cnt;

	public static class PersonalActivity {
		private byte memberActivityType;
		private String boardName;
		private byte boardListType;
		private short boardID;
		private long boardNo;
		private long groupNo;
		private byte boardSate;
		private java.sql.Timestamp registeredDate;
		private String sourceSubject;
		private String sourceWriterID;
		private String sourceWriterNickname;

		public byte getMemberActivityType() {
			return memberActivityType;
		}

		public void setMemberActivityType(byte memberActivityType) {
			this.memberActivityType = memberActivityType;
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
		public long getGroupNo() {
			return groupNo;
		}

		public void setGroupNo(long groupNo) {
			this.groupNo = groupNo;
		}
		public byte getBoardSate() {
			return boardSate;
		}

		public void setBoardSate(byte boardSate) {
			this.boardSate = boardSate;
		}
		public java.sql.Timestamp getRegisteredDate() {
			return registeredDate;
		}

		public void setRegisteredDate(java.sql.Timestamp registeredDate) {
			this.registeredDate = registeredDate;
		}
		public String getSourceSubject() {
			return sourceSubject;
		}

		public void setSourceSubject(String sourceSubject) {
			this.sourceSubject = sourceSubject;
		}
		public String getSourceWriterID() {
			return sourceWriterID;
		}

		public void setSourceWriterID(String sourceWriterID) {
			this.sourceWriterID = sourceWriterID;
		}
		public String getSourceWriterNickname() {
			return sourceWriterNickname;
		}

		public void setSourceWriterNickname(String sourceWriterNickname) {
			this.sourceWriterNickname = sourceWriterNickname;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("PersonalActivity[");
			builder.append("memberActivityType=");
			builder.append(memberActivityType);
			builder.append(", boardName=");
			builder.append(boardName);
			builder.append(", boardListType=");
			builder.append(boardListType);
			builder.append(", boardID=");
			builder.append(boardID);
			builder.append(", boardNo=");
			builder.append(boardNo);
			builder.append(", groupNo=");
			builder.append(groupNo);
			builder.append(", boardSate=");
			builder.append(boardSate);
			builder.append(", registeredDate=");
			builder.append(registeredDate);
			builder.append(", sourceSubject=");
			builder.append(sourceSubject);
			builder.append(", sourceWriterID=");
			builder.append(sourceWriterID);
			builder.append(", sourceWriterNickname=");
			builder.append(sourceWriterNickname);
			builder.append("]");
			return builder.toString();
		}
	}

	private java.util.List<PersonalActivity> personalActivityList;

	public String getTargetUserID() {
		return targetUserID;
	}

	public void setTargetUserID(String targetUserID) {
		this.targetUserID = targetUserID;
	}
	public String getTargetUserNickname() {
		return targetUserNickname;
	}

	public void setTargetUserNickname(String targetUserNickname) {
		this.targetUserNickname = targetUserNickname;
	}
	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}
	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public java.util.List<PersonalActivity> getPersonalActivityList() {
		return personalActivityList;
	}

	public void setPersonalActivityList(java.util.List<PersonalActivity> personalActivityList) {
		this.personalActivityList = personalActivityList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("personalActivityHistoryRes[");
		builder.append("targetUserID=");
		builder.append(targetUserID);
		builder.append(", targetUserNickname=");
		builder.append(targetUserNickname);
		builder.append(", total=");
		builder.append(total);
		builder.append(", cnt=");
		builder.append(cnt);

		builder.append(", personalActivityList=");
		if (null == personalActivityList) {
			builder.append("null");
		} else {
			int personalActivityListSize = personalActivityList.size();
			if (0 == personalActivityListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < personalActivityListSize; i++) {
					PersonalActivity personalActivity = personalActivityList.get(i);
					if (0 == i) {
						builder.append("personalActivity[");
					} else {
						builder.append(", personalActivity[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(personalActivity.toString());
				}
				builder.append("]");
			}
		}
		builder.append("]");
		return builder.toString();
	}
}