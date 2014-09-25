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
package kr.pe.sinnori.impl.message.MemberListResult;

import kr.pe.sinnori.common.message.AbstractMessage;
/**
 * MemberListResult 메시지
 * @author Won Jonghoon
 *
 */
public final class MemberListResult extends AbstractMessage {
	private int cnt;
	public class Member {
		private String id;
		private String pwd;
		private String email;
		private String phone;
		private java.sql.Timestamp regdate;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
		public String getPwd() {
			return pwd;
		}

		public void setPwd(String pwd) {
			this.pwd = pwd;
		}
		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}
		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}
		public java.sql.Timestamp getRegdate() {
			return regdate;
		}

		public void setRegdate(java.sql.Timestamp regdate) {
			this.regdate = regdate;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Member[");
			builder.append("id=");
			builder.append(id);
			builder.append(", pwd=");
			builder.append(pwd);
			builder.append(", email=");
			builder.append(email);
			builder.append(", phone=");
			builder.append(phone);
			builder.append(", regdate=");
			builder.append(regdate);
			builder.append("]");
			return builder.toString();
		}
	};
	private Member[] memberList;

	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}

	public Member[] getMemberList() {
		return memberList;
	}

	public void setMemberList(Member[] memberList) {
		this.memberList = memberList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("class MemberListResult[");
		builder.append("cnt=");
		builder.append(cnt);
		builder.append(", memberList=");
		if (null == memberList) {
			builder.append("null");
		} else {
			if (0 == memberList.length) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < memberList.length; i++) {
					Member member = memberList[i];
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

		builder.append(", messageHeaderInfo=");
		builder.append(messageHeaderInfo.toString());
		builder.append("]");
		return builder.toString();
	}
}