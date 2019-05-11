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

package kr.pe.codda.impl.message.MemberManagerRes;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * MemberManagerRes message
 * @author Won Jonghoon
 *
 */
public class MemberManagerRes extends AbstractMessage {
	private byte memberState;
	private String searchID;
	private String fromDateString;
	private String toDateString;
	private int pageNo;
	private int pageSize;
	private boolean isNextPage;
	private int cnt;

	public static class Member {
		private String userID;
		private String nickname;
		private java.sql.Timestamp registeredDate;
		private java.sql.Timestamp lastNicknameModifiedDate;
		private java.sql.Timestamp lastEmailModifiedDate;
		private java.sql.Timestamp lastPasswordModifiedDate;
		private java.sql.Timestamp lastStateModifiedDate;

		public String getUserID() {
			return userID;
		}

		public void setUserID(String userID) {
			this.userID = userID;
		}
		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
		}
		public java.sql.Timestamp getRegisteredDate() {
			return registeredDate;
		}

		public void setRegisteredDate(java.sql.Timestamp registeredDate) {
			this.registeredDate = registeredDate;
		}
		public java.sql.Timestamp getLastNicknameModifiedDate() {
			return lastNicknameModifiedDate;
		}

		public void setLastNicknameModifiedDate(java.sql.Timestamp lastNicknameModifiedDate) {
			this.lastNicknameModifiedDate = lastNicknameModifiedDate;
		}
		public java.sql.Timestamp getLastEmailModifiedDate() {
			return lastEmailModifiedDate;
		}

		public void setLastEmailModifiedDate(java.sql.Timestamp lastEmailModifiedDate) {
			this.lastEmailModifiedDate = lastEmailModifiedDate;
		}
		public java.sql.Timestamp getLastPasswordModifiedDate() {
			return lastPasswordModifiedDate;
		}

		public void setLastPasswordModifiedDate(java.sql.Timestamp lastPasswordModifiedDate) {
			this.lastPasswordModifiedDate = lastPasswordModifiedDate;
		}
		public java.sql.Timestamp getLastStateModifiedDate() {
			return lastStateModifiedDate;
		}

		public void setLastStateModifiedDate(java.sql.Timestamp lastStateModifiedDate) {
			this.lastStateModifiedDate = lastStateModifiedDate;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Member[");
			builder.append("userID=");
			builder.append(userID);
			builder.append(", nickname=");
			builder.append(nickname);
			builder.append(", registeredDate=");
			builder.append(registeredDate);
			builder.append(", lastNicknameModifiedDate=");
			builder.append(lastNicknameModifiedDate);
			builder.append(", lastEmailModifiedDate=");
			builder.append(lastEmailModifiedDate);
			builder.append(", lastPasswordModifiedDate=");
			builder.append(lastPasswordModifiedDate);
			builder.append(", lastStateModifiedDate=");
			builder.append(lastStateModifiedDate);
			builder.append("]");
			return builder.toString();
		}
	}

	private java.util.List<Member> memberList;

	public byte getMemberState() {
		return memberState;
	}

	public void setMemberState(byte memberState) {
		this.memberState = memberState;
	}
	public String getSearchID() {
		return searchID;
	}

	public void setSearchID(String searchID) {
		this.searchID = searchID;
	}
	public String getFromDateString() {
		return fromDateString;
	}

	public void setFromDateString(String fromDateString) {
		this.fromDateString = fromDateString;
	}
	public String getToDateString() {
		return toDateString;
	}

	public void setToDateString(String toDateString) {
		this.toDateString = toDateString;
	}
	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public boolean getIsNextPage() {
		return isNextPage;
	}

	public void setIsNextPage(boolean isNextPage) {
		this.isNextPage = isNextPage;
	}
	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public java.util.List<Member> getMemberList() {
		return memberList;
	}

	public void setMemberList(java.util.List<Member> memberList) {
		this.memberList = memberList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("memberManagerRes[");
		builder.append("memberState=");
		builder.append(memberState);
		builder.append(", searchID=");
		builder.append(searchID);
		builder.append(", fromDateString=");
		builder.append(fromDateString);
		builder.append(", toDateString=");
		builder.append(toDateString);
		builder.append(", pageNo=");
		builder.append(pageNo);
		builder.append(", pageSize=");
		builder.append(pageSize);
		builder.append(", isNextPage=");
		builder.append(isNextPage);
		builder.append(", cnt=");
		builder.append(cnt);

		builder.append(", memberList=");
		if (null == memberList) {
			builder.append("null");
		} else {
			int memberListSize = memberList.size();
			if (0 == memberListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < memberListSize; i++) {
					Member member = memberList.get(i);
					if (0 == i) {
						builder.append("member[");
					} else {
						builder.append(", member[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(member.toString());
				}
				builder.append("]");
			}
		}
		builder.append("]");
		return builder.toString();
	}
}