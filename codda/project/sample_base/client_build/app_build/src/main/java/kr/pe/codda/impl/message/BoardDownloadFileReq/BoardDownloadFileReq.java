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

package kr.pe.codda.impl.message.BoardDownloadFileReq;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * BoardDownloadFileReq message
 * @author Won Jonghoon
 *
 */
public class BoardDownloadFileReq extends AbstractMessage {
	private short boardID;
	private long boardNo;
	private short attachedFileSeq;
	private String requestUserID;

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
	public short getAttachedFileSeq() {
		return attachedFileSeq;
	}

	public void setAttachedFileSeq(short attachedFileSeq) {
		this.attachedFileSeq = attachedFileSeq;
	}
	public String getRequestUserID() {
		return requestUserID;
	}

	public void setRequestUserID(String requestUserID) {
		this.requestUserID = requestUserID;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("boardDownloadFileReq[");
		builder.append("boardID=");
		builder.append(boardID);
		builder.append(", boardNo=");
		builder.append(boardNo);
		builder.append(", attachedFileSeq=");
		builder.append(attachedFileSeq);
		builder.append(", requestUserID=");
		builder.append(requestUserID);
		builder.append("]");
		return builder.toString();
	}
}