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
package kr.pe.sinnori.impl.message.LoginReq;

import kr.pe.sinnori.common.message.AbstractMessage;

/**
 * LoginReq 메시지
 * @author Won Jonghoon
 *
 */
public class LoginReq extends AbstractMessage {
	private String idCipherBase64;
	private String pwdCipherBase64;
	private String sessionKeyBase64;
	private String ivBase64;

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("loginReq[");
		builder.append("idCipherBase64=");
		builder.append(idCipherBase64);
		builder.append(", pwdCipherBase64=");
		builder.append(pwdCipherBase64);
		builder.append(", sessionKeyBase64=");
		builder.append(sessionKeyBase64);
		builder.append(", ivBase64=");
		builder.append(ivBase64);
		builder.append("]");
		return builder.toString();
	}
}