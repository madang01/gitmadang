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

package kr.pe.codda.impl.message.MemberInformationRes;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * MemberInformationRes message
 * @author Won Jonghoon
 *
 */
public class MemberInformationRes extends AbstractMessage {
	private String targetUserID;
	private String nickname;
	private String email;
	private byte role;
	private byte state;
	private java.sql.Timestamp registeredDate;
	private java.sql.Timestamp lastNicknameModifiedDate;
	private java.sql.Timestamp lastEmailModifiedDate;
	private java.sql.Timestamp lastPasswordModifiedDate;
	private java.sql.Timestamp lastStateModifiedDate;

	public String getTargetUserID() {
		return targetUserID;
	}

	public void setTargetUserID(String targetUserID) {
		this.targetUserID = targetUserID;
	}
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	public byte getRole() {
		return role;
	}

	public void setRole(byte role) {
		this.role = role;
	}
	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
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
		builder.append("memberInformationRes[");
		builder.append("targetUserID=");
		builder.append(targetUserID);
		builder.append(", nickname=");
		builder.append(nickname);
		builder.append(", email=");
		builder.append(email);
		builder.append(", role=");
		builder.append(role);
		builder.append(", state=");
		builder.append(state);
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