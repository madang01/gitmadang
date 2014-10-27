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
package kr.pe.sinnori.impl.message.BoardDetailInsertRequest;

import kr.pe.sinnori.common.message.AbstractMessage;
/**
 * BoardDetailInsertRequest 메시지
 * @author Won Jonghoon
 *
 */
public class BoardDetailInsertRequest extends AbstractMessage {
	private long groupNO;
	private long parentNO;
	private long groupSeq;
	private int depth;
	private long boardTypeID;
	private String title;
	private String contents;
	private String userID;

	public long getGroupNO() {
		return groupNO;
	}

	public void setGroupNO(long groupNO) {
		this.groupNO = groupNO;
	}
	public long getParentNO() {
		return parentNO;
	}

	public void setParentNO(long parentNO) {
		this.parentNO = parentNO;
	}
	public long getGroupSeq() {
		return groupSeq;
	}

	public void setGroupSeq(long groupSeq) {
		this.groupSeq = groupSeq;
	}
	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}
	public long getBoardTypeID() {
		return boardTypeID;
	}

	public void setBoardTypeID(long boardTypeID) {
		this.boardTypeID = boardTypeID;
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("class BoardDetailInsertRequest[");
		builder.append("groupNO=");
		builder.append(groupNO);
		builder.append(", parentNO=");
		builder.append(parentNO);
		builder.append(", groupSeq=");
		builder.append(groupSeq);
		builder.append(", depth=");
		builder.append(depth);
		builder.append(", boardTypeID=");
		builder.append(boardTypeID);
		builder.append(", title=");
		builder.append(title);
		builder.append(", contents=");
		builder.append(contents);
		builder.append(", userID=");
		builder.append(userID);
		builder.append(", messageHeaderInfo=");
		builder.append(messageHeaderInfo.toString());
		builder.append("]");
		return builder.toString();
	}
}