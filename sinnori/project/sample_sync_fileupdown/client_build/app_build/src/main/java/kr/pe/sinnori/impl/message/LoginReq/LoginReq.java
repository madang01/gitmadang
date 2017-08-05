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
	private byte[] idEncryptedBytes;
	private byte[] pwdEncryptedBytes;
	private byte[] sessionKeyBytes;
	private byte[] ivBytes;

	public byte[] getIdEncryptedBytes() {
		return idEncryptedBytes;
	}

	public void setIdEncryptedBytes(byte[] idEncryptedBytes) {
		this.idEncryptedBytes = idEncryptedBytes;
	}
	public byte[] getPwdEncryptedBytes() {
		return pwdEncryptedBytes;
	}

	public void setPwdEncryptedBytes(byte[] pwdEncryptedBytes) {
		this.pwdEncryptedBytes = pwdEncryptedBytes;
	}
	public byte[] getSessionKeyBytes() {
		return sessionKeyBytes;
	}

	public void setSessionKeyBytes(byte[] sessionKeyBytes) {
		this.sessionKeyBytes = sessionKeyBytes;
	}
	public byte[] getIvBytes() {
		return ivBytes;
	}

	public void setIvBytes(byte[] ivBytes) {
		this.ivBytes = ivBytes;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("class LoginReq[");
		builder.append("idEncryptedBytes=");
		builder.append(kr.pe.sinnori.common.util.HexUtil.getHexStringFromByteArray(idEncryptedBytes, 0, Math.min(idEncryptedBytes.length, 7)));
		builder.append(", pwdEncryptedBytes=");
		builder.append(kr.pe.sinnori.common.util.HexUtil.getHexStringFromByteArray(pwdEncryptedBytes, 0, Math.min(pwdEncryptedBytes.length, 7)));
		builder.append(", sessionKeyBytes=");
		builder.append(kr.pe.sinnori.common.util.HexUtil.getHexStringFromByteArray(sessionKeyBytes, 0, Math.min(sessionKeyBytes.length, 7)));
		builder.append(", ivBytes=");
		builder.append(kr.pe.sinnori.common.util.HexUtil.getHexStringFromByteArray(ivBytes, 0, Math.min(ivBytes.length, 7)));
		builder.append(", messageHeaderInfo=");
		builder.append(messageHeaderInfo.toString());
		builder.append("]");
		return builder.toString();
	}
}