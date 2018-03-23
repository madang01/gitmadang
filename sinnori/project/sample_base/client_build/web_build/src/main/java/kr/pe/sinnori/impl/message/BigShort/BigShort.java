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
package kr.pe.sinnori.impl.message.BigShort;

import kr.pe.sinnori.common.message.AbstractMessage;

/**
 * BigShort 메시지
 * @author Won Jonghoon
 *
 */
public class BigShort extends AbstractMessage {
	private byte[] filler1;
	private short value1;
	private short value2;

	public byte[] getFiller1() {
		return filler1;
	}

	public void setFiller1(byte[] filler1) {
		this.filler1 = filler1;
	}
	public short getValue1() {
		return value1;
	}

	public void setValue1(short value1) {
		this.value1 = value1;
	}
	public short getValue2() {
		return value2;
	}

	public void setValue2(short value2) {
		this.value2 = value2;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("bigShort[");
		builder.append("filler1=");
		builder.append(kr.pe.sinnori.common.util.HexUtil.getHexStringFromByteArray(filler1, 0, Math.min(filler1.length, 7)));
		builder.append(", value1=");
		builder.append(value1);
		builder.append(", value2=");
		builder.append(value2);
		builder.append("]");
		return builder.toString();
	}
}