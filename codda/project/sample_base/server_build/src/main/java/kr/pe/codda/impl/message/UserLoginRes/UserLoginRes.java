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

package kr.pe.codda.impl.message.UserLoginRes;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * UserLoginRes message
 * @author Won Jonghoon
 *
 */
public class UserLoginRes extends AbstractMessage {
	private String userID;
	private String userName;
	private byte memberRole;

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	public byte getMemberRole() {
		return memberRole;
	}

	public void setMemberRole(byte memberRole) {
		this.memberRole = memberRole;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("userLoginRes[");
		builder.append("userID=");
		builder.append(userID);
		builder.append(", userName=");
		builder.append(userName);
		builder.append(", memberRole=");
		builder.append(memberRole);
		builder.append("]");
		return builder.toString();
	}
}