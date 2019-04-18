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

package kr.pe.codda.impl.message.AccountSearchProcessRes;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * AccountSearchProcessRes message
 * @author Won Jonghoon
 *
 */
public class AccountSearchProcessRes extends AbstractMessage {
	private byte accountSearchType;
	private String userID;
	private String nickname;

	public byte getAccountSearchType() {
		return accountSearchType;
	}

	public void setAccountSearchType(byte accountSearchType) {
		this.accountSearchType = accountSearchType;
	}
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("accountSearchProcessRes[");
		builder.append("accountSearchType=");
		builder.append(accountSearchType);
		builder.append(", userID=");
		builder.append(userID);
		builder.append(", nickname=");
		builder.append(nickname);
		builder.append("]");
		return builder.toString();
	}
}