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

package kr.pe.codda.impl.message.PasswordSearchProcessReq;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * PasswordSearchProcessReq message
 * @author Won Jonghoon
 *
 */
public class PasswordSearchProcessReq extends AbstractMessage {
	private byte searchWhatType;
	private String emailCipherBase64;
	private String secretAuthenticationValueCipherBase64;
	private String newPwdCipherBase64;
	private String sessionKeyBase64;
	private String ivBase64;
	private String ip;

	public byte getSearchWhatType() {
		return searchWhatType;
	}

	public void setSearchWhatType(byte searchWhatType) {
		this.searchWhatType = searchWhatType;
	}
	public String getEmailCipherBase64() {
		return emailCipherBase64;
	}

	public void setEmailCipherBase64(String emailCipherBase64) {
		this.emailCipherBase64 = emailCipherBase64;
	}
	public String getSecretAuthenticationValueCipherBase64() {
		return secretAuthenticationValueCipherBase64;
	}

	public void setSecretAuthenticationValueCipherBase64(String secretAuthenticationValueCipherBase64) {
		this.secretAuthenticationValueCipherBase64 = secretAuthenticationValueCipherBase64;
	}
	public String getNewPwdCipherBase64() {
		return newPwdCipherBase64;
	}

	public void setNewPwdCipherBase64(String newPwdCipherBase64) {
		this.newPwdCipherBase64 = newPwdCipherBase64;
	}
	public String getSessionKeyBase64() {
		return sessionKeyBase64;
	}

	public void setSessionKeyBase64(String sessionKeyBase64) {
		this.sessionKeyBase64 = sessionKeyBase64;
	}
	public String getIvBase64() {
		return ivBase64;
	}

	public void setIvBase64(String ivBase64) {
		this.ivBase64 = ivBase64;
	}
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("passwordSearchProcessReq[");
		builder.append("searchWhatType=");
		builder.append(searchWhatType);
		builder.append(", emailCipherBase64=");
		builder.append(emailCipherBase64);
		builder.append(", secretAuthenticationValueCipherBase64=");
		builder.append(secretAuthenticationValueCipherBase64);
		builder.append(", newPwdCipherBase64=");
		builder.append(newPwdCipherBase64);
		builder.append(", sessionKeyBase64=");
		builder.append(sessionKeyBase64);
		builder.append(", ivBase64=");
		builder.append(ivBase64);
		builder.append(", ip=");
		builder.append(ip);
		builder.append("]");
		return builder.toString();
	}
}