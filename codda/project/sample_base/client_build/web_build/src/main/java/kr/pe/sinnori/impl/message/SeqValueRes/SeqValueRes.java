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
package kr.pe.sinnori.impl.message.SeqValueRes;

import kr.pe.sinnori.common.message.AbstractMessage;

/**
 * SeqValueRes 메시지
 * @author Won Jonghoon
 *
 */
public class SeqValueRes extends AbstractMessage {
	private long seqValue;
	private short wantedSize;

	public long getSeqValue() {
		return seqValue;
	}

	public void setSeqValue(long seqValue) {
		this.seqValue = seqValue;
	}
	public short getWantedSize() {
		return wantedSize;
	}

	public void setWantedSize(short wantedSize) {
		this.wantedSize = wantedSize;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("seqValueRes[");
		builder.append("seqValue=");
		builder.append(seqValue);
		builder.append(", wantedSize=");
		builder.append(wantedSize);
		builder.append("]");
		return builder.toString();
	}
}