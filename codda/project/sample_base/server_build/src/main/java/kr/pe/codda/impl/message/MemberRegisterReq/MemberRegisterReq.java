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

package kr.pe.codda.impl.message.MemberRegisterReq;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * MemberRegisterReq message
 * @author Won Jonghoon
 *
 */
public class MemberRegisterReq extends AbstractMessage {
	private String idCipherBase64;
	private String pwdCipherBase64;
	private String nicknameCipherBase64;
	private String emailCipherBase64;
	private String sessionKeyBase64;
	private String ivBase64;
	private String ip;

	public String getIdCipherBase64() {
		return idCipherBase64;
	}

	public void setIdCipherBase64(String idCipherBase64) {
		this.idCipherBase64 = idCipherBase64;
	}
	public String getPwdCipherBase64() {
		return pwdCipherBase64;
	}

	public void setPwdCipherBase64(String pwdCipherBase64) {
		this.pwdCipherBase64 = pwdCipherBase64;
	}
	public String getNicknameCipherBase64() {
		return nicknameCipherBase64;
	}

	public void setNicknameCipherBase64(String nicknameCipherBase64) {
		this.nicknameCipherBase64 = nicknameCipherBase64;
	}
	public String getEmailCipherBase64() {
		return emailCipherBase64;
	}

	public void setEmailCipherBase64(String emailCipherBase64) {
		this.emailCipherBase64 = emailCipherBase64;
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
		builder.append("memberRegisterReq[");
		builder.append("idCipherBase64=");
		builder.append(idCipherBase64);
		builder.append(", pwdCipherBase64=");
		builder.append(pwdCipherBase64);
		builder.append(", nicknameCipherBase64=");
		builder.append(nicknameCipherBase64);
		builder.append(", emailCipherBase64=");
		builder.append(emailCipherBase64);
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