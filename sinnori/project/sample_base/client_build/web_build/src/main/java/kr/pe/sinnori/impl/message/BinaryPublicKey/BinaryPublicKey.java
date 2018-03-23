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
package kr.pe.sinnori.impl.message.BinaryPublicKey;

import kr.pe.sinnori.common.message.AbstractMessage;

/**
 * BinaryPublicKey 메시지
 * @author Won Jonghoon
 *
 */
public class BinaryPublicKey extends AbstractMessage {
	private byte[] publicKeyBytes;

	public byte[] getPublicKeyBytes() {
		return publicKeyBytes;
	}

	public void setPublicKeyBytes(byte[] publicKeyBytes) {
		this.publicKeyBytes = publicKeyBytes;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("binaryPublicKey[");
		builder.append("publicKeyBytes=");
		builder.append(kr.pe.sinnori.common.util.HexUtil.getHexStringFromByteArray(publicKeyBytes, 0, Math.min(publicKeyBytes.length, 7)));
		builder.append("]");
		return builder.toString();
	}
}