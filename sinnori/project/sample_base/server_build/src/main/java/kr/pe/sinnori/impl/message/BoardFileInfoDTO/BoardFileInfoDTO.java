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
package kr.pe.sinnori.impl.message.BoardFileInfoDTO;

import kr.pe.sinnori.common.message.AbstractMessage;

/**
 * BoardFileInfoDTO 메시지
 * @author Won Jonghoon
 *
 */
public class BoardFileInfoDTO extends AbstractMessage {
	private long attachId;
	private String ownerId;
	private String ip;
	private java.sql.Timestamp registerDate;
	private java.sql.Timestamp modifiedDate;

	public long getAttachId() {
		return attachId;		
	}

	public void setAttachId(long attachId) {
		this.attachId = attachId;
	}
	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	public java.sql.Timestamp getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(java.sql.Timestamp registerDate) {
		this.registerDate = registerDate;
	}
	public java.sql.Timestamp getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(java.sql.Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("boardFileInfoDTO[");
		builder.append("attachId=");
		builder.append(attachId);
		builder.append(", ownerId=");
		builder.append(ownerId);
		builder.append(", ip=");
		builder.append(ip);
		builder.append(", registerDate=");
		builder.append(registerDate);
		builder.append(", modifiedDate=");
		builder.append(modifiedDate);
		builder.append("]");
		return builder.toString();
	}
}