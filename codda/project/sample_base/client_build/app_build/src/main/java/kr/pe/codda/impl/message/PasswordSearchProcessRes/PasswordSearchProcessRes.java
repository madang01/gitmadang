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

package kr.pe.codda.impl.message.PasswordSearchProcessRes;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * PasswordSearchProcessRes message
 * @author Won Jonghoon
 *
 */
public class PasswordSearchProcessRes extends AbstractMessage {
	private byte searchWhatType;
	private String userID;
	private String nickname;

	public byte getSearchWhatType() {
		return searchWhatType;
	}

	public void setSearchWhatType(byte searchWhatType) {
		this.searchWhatType = searchWhatType;
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
		builder.append("passwordSearchProcessRes[");
		builder.append("searchWhatType=");
		builder.append(searchWhatType);
		builder.append(", userID=");
		builder.append(userID);
		builder.append(", nickname=");
		builder.append(nickname);
		builder.append("]");
		return builder.toString();
	}
}