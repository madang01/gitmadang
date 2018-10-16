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

package kr.pe.codda.impl.message.BoardModifyRes;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * BoardModifyRes message
 * @author Won Jonghoon
 *
 */
public class BoardModifyRes extends AbstractMessage {
	private short boardID;
	private long boardNo;
	private short firstNewAttachedFileSeq;

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
	public short getFirstNewAttachedFileSeq() {
		return firstNewAttachedFileSeq;
	}

	public void setFirstNewAttachedFileSeq(short firstNewAttachedFileSeq) {
		this.firstNewAttachedFileSeq = firstNewAttachedFileSeq;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("boardModifyRes[");
		builder.append("boardID=");
		builder.append(boardID);
		builder.append(", boardNo=");
		builder.append(boardNo);
		builder.append(", firstNewAttachedFileSeq=");
		builder.append(firstNewAttachedFileSeq);
		builder.append("]");
		return builder.toString();
	}
}