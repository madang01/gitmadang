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

package kr.pe.codda.impl.message.BoardInfoAddReq;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * BoardInfoAddReq message
 * @author Won Jonghoon
 *
 */
public class BoardInfoAddReq extends AbstractMessage {
	private String requestedUserID;
	private String boardName;
	private String boardInformation;
	private byte boardListType;
	private byte boardReplyPolicyType;
	private byte boardWritePermissionType;
	private byte boardReplyPermissionType;

	public String getRequestedUserID() {
		return requestedUserID;
	}

	public void setRequestedUserID(String requestedUserID) {
		this.requestedUserID = requestedUserID;
	}
	public String getBoardName() {
		return boardName;
	}

	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}
	public String getBoardInformation() {
		return boardInformation;
	}

	public void setBoardInformation(String boardInformation) {
		this.boardInformation = boardInformation;
	}
	public byte getBoardListType() {
		return boardListType;
	}

	public void setBoardListType(byte boardListType) {
		this.boardListType = boardListType;
	}
	public byte getBoardReplyPolicyType() {
		return boardReplyPolicyType;
	}

	public void setBoardReplyPolicyType(byte boardReplyPolicyType) {
		this.boardReplyPolicyType = boardReplyPolicyType;
	}
	public byte getBoardWritePermissionType() {
		return boardWritePermissionType;
	}

	public void setBoardWritePermissionType(byte boardWritePermissionType) {
		this.boardWritePermissionType = boardWritePermissionType;
	}
	public byte getBoardReplyPermissionType() {
		return boardReplyPermissionType;
	}

	public void setBoardReplyPermissionType(byte boardReplyPermissionType) {
		this.boardReplyPermissionType = boardReplyPermissionType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("boardInfoAddReq[");
		builder.append("requestedUserID=");
		builder.append(requestedUserID);
		builder.append(", boardName=");
		builder.append(boardName);
		builder.append(", boardInformation=");
		builder.append(boardInformation);
		builder.append(", boardListType=");
		builder.append(boardListType);
		builder.append(", boardReplyPolicyType=");
		builder.append(boardReplyPolicyType);
		builder.append(", boardWritePermissionType=");
		builder.append(boardWritePermissionType);
		builder.append(", boardReplyPermissionType=");
		builder.append(boardReplyPermissionType);
		builder.append("]");
		return builder.toString();
	}
}