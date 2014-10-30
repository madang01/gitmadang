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
package kr.pe.sinnori.impl.message.BoardReplyRequest;

import kr.pe.sinnori.common.message.AbstractMessage;
/**
 * BoardReplyRequest 메시지
 * @author Won Jonghoon
 *
 */
public class BoardReplyRequest extends AbstractMessage {
	private long boardId;
	private long parentBoardNo;
	private String subject;
	private String content;
	private String writerId;
	private String ip;

	public long getBoardId() {
		return boardId;
	}

	public void setBoardId(long boardId) {
		this.boardId = boardId;
	}
	public long getParentBoardNo() {
		return parentBoardNo;
	}

	public void setParentBoardNo(long parentBoardNo) {
		this.parentBoardNo = parentBoardNo;
	}
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	public String getWriterId() {
		return writerId;
	}

	public void setWriterId(String writerId) {
		this.writerId = writerId;
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
		builder.append("class BoardReplyRequest[");
		builder.append("boardId=");
		builder.append(boardId);
		builder.append(", parentBoardNo=");
		builder.append(parentBoardNo);
		builder.append(", subject=");
		builder.append(subject);
		builder.append(", content=");
		builder.append(content);
		builder.append(", writerId=");
		builder.append(writerId);
		builder.append(", ip=");
		builder.append(ip);
		builder.append(", messageHeaderInfo=");
		builder.append(messageHeaderInfo.toString());
		builder.append("]");
		return builder.toString();
	}
}