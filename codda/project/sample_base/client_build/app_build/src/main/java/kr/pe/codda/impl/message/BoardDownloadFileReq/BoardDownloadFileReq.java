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
 * @author Won Jonghooon
 *
 */
public class BoardDownloadFileReq extends AbstractMessage {
	private long attachId;
	private long attachSeq;

	public long getAttachId() {
		return attachId;
	}

	public void setAttachId(long attachId) {
		this.attachId = attachId;
	}
	public long getAttachSeq() {
		return attachSeq;
	}

	public void setAttachSeq(long attachSeq) {
		this.attachSeq = attachSeq;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("boardDownloadFileReq[");
		builder.append("attachId=");
		builder.append(attachId);
		builder.append(", attachSeq=");
		builder.append(attachSeq);
		builder.append("]");
		return builder.toString();
	}
}