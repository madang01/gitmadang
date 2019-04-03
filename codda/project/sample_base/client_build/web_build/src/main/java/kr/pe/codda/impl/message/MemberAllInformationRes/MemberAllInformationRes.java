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

package kr.pe.codda.impl.message.MemberAllInformationRes;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * MemberAllInformationRes message
 * @author Won Jonghoon
 *
 */
public class MemberAllInformationRes extends AbstractMessage {
	private String nickname;
	private byte state;
	private byte role;
	private String passwordHint;
	private String passwordAnswer;
	private short passwordFailedCount;
	private String ip;
	private java.sql.Timestamp registeredDate;
	private java.sql.Timestamp lastModifiedDate;

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}
	public byte getRole() {
		return role;
	}

	public void setRole(byte role) {
		this.role = role;
	}
	public String getPasswordHint() {
		return passwordHint;
	}

	public void setPasswordHint(String passwordHint) {
		this.passwordHint = passwordHint;
	}
	public String getPasswordAnswer() {
		return passwordAnswer;
	}

	public void setPasswordAnswer(String passwordAnswer) {
		this.passwordAnswer = passwordAnswer;
	}
	public short getPasswordFailedCount() {
		return passwordFailedCount;
	}

	public void setPasswordFailedCount(short passwordFailedCount) {
		this.passwordFailedCount = passwordFailedCount;
	}
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	public java.sql.Timestamp getRegisteredDate() {
		return registeredDate;
	}

	public void setRegisteredDate(java.sql.Timestamp registeredDate) {
		this.registeredDate = registeredDate;
	}
	public java.sql.Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(java.sql.Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("memberAllInformationRes[");
		builder.append("nickname=");
		builder.append(nickname);
		builder.append(", state=");
		builder.append(state);
		builder.append(", role=");
		builder.append(role);
		builder.append(", passwordHint=");
		builder.append(passwordHint);
		builder.append(", passwordAnswer=");
		builder.append(passwordAnswer);
		builder.append(", passwordFailedCount=");
		builder.append(passwordFailedCount);
		builder.append(", ip=");
		builder.append(ip);
		builder.append(", registeredDate=");
		builder.append(registeredDate);
		builder.append(", lastModifiedDate=");
		builder.append(lastModifiedDate);
		builder.append("]");
		return builder.toString();
	}
}