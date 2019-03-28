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

package kr.pe.codda.impl.message.BoardDeleteReq;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * BoardDeleteReq message
 * @author Won Jonghoon
 *
 */
public class BoardDeleteReq extends AbstractMessage {
	private String requestedUserID;
	private short boardID;
	private long boardNo;
	private String pwdHashBase64;
	private String ip;

	public String getRequestedUserID() {
		return requestedUserID;
	}

	public void setRequestedUserID(String requestedUserID) {
		this.requestedUserID = requestedUserID;
	}
	public short getBoardID() {
		return boardID;
	}

	public void setBoardID(short boardID) {
		this.boardID = boardID;
	}
	public long getBoardNo() {
		return boardNo;
	}

	public void setBoardNo(long boardNo) {
		this.boardNo = boardNo;
	}
	public String getPwdHashBase64() {
		return pwdHashBase64;
	}

	public void setPwdHashBase64(String pwdHashBase64) {
		this.pwdHashBase64 = pwdHashBase64;
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
		builder.append("boardDeleteReq[");
		builder.append("requestedUserID=");
		builder.append(requestedUserID);
		builder.append(", boardID=");
		builder.append(boardID);
		builder.append(", boardNo=");
		builder.append(boardNo);
		builder.append(", pwdHashBase64=");
		builder.append(pwdHashBase64);
		builder.append(", ip=");
		builder.append(ip);
		builder.append("]");
		return builder.toString();
	}
}