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

package kr.pe.codda.common.sessionkey;

import kr.pe.codda.common.util.HexUtil;

/**
 * 공개키 알고리즘에서 개인키에 대한 출력 객체
 * 
 * @author Won Jonghoon
 * 
 */
public class PrivateKeyOutput {
	public byte[] privateKeyHeader = new byte[30];
	public byte[] separator1 = new byte[2];
	public byte[] algorithmVersion = new byte[1];
	public byte[] separator2 = new byte[3];
	public byte[] modulus = new byte[129];
	public byte[] separator3 = new byte[2];
	public byte[] publicExponent = new byte[3];
	public byte[] separator4 = new byte[3];
	public byte[] privateExponent = new byte[128];
	public byte[] separator5 = new byte[2];
	public byte[] prime1 = new byte[65];
	public byte[] separator6 = new byte[2];
	public byte[] prime2 = new byte[65];
	public byte[] separator7 = new byte[2];
	public byte[] exponent1 = new byte[65];
	public byte[] separator8 = new byte[2];
	public byte[] exponent2 = new byte[64];
	public byte[] separator9 = new byte[2];
	public byte[] coefficient = new byte[65];

	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("privateKeyHeader=[");
		strBuffer.append(HexUtil.getHexStringFromByteArray(privateKeyHeader));
		strBuffer.append("]");
		strBuffer.append(System.getProperty("line.separator"));

		strBuffer.append("separator1=[");
		strBuffer.append(HexUtil.getHexStringFromByteArray(separator1));
		strBuffer.append("]");
		strBuffer.append(System.getProperty("line.separator"));

		strBuffer.append("algorithmVersion=[");
		strBuffer.append(HexUtil.getHexStringFromByteArray(algorithmVersion));
		strBuffer.append("]");
		strBuffer.append(System.getProperty("line.separator"));

		strBuffer.append("separator2=[");
		strBuffer.append(HexUtil.getHexStringFromByteArray(separator2));
		strBuffer.append("]");
		strBuffer.append(System.getProperty("line.separator"));

		strBuffer.append("modulus=[");
		strBuffer.append(HexUtil.getHexStringFromByteArray(modulus));
		strBuffer.append("]");
		strBuffer.append(System.getProperty("line.separator"));

		strBuffer.append("separator3=[");
		strBuffer.append(HexUtil.getHexStringFromByteArray(separator3));
		strBuffer.append("]");
		strBuffer.append(System.getProperty("line.separator"));

		strBuffer.append("publicExponent=[");
		strBuffer.append(HexUtil.getHexStringFromByteArray(publicExponent));
		strBuffer.append("]");
		strBuffer.append(System.getProperty("line.separator"));

		strBuffer.append("separator4=[");
		strBuffer.append(HexUtil.getHexStringFromByteArray(separator4));
		strBuffer.append("]");
		strBuffer.append(System.getProperty("line.separator"));

		strBuffer.append("privateExponent=[");
		strBuffer.append(HexUtil.getHexStringFromByteArray(privateExponent));
		strBuffer.append("]");
		strBuffer.append(System.getProperty("line.separator"));

		strBuffer.append("separator5=[");
		strBuffer.append(HexUtil.getHexStringFromByteArray(separator5));
		strBuffer.append("]");
		strBuffer.append(System.getProperty("line.separator"));

		strBuffer.append("prime1=[");
		strBuffer.append(HexUtil.getHexStringFromByteArray(prime1));
		strBuffer.append("]");
		strBuffer.append(System.getProperty("line.separator"));

		strBuffer.append("separator6=[");
		strBuffer.append(HexUtil.getHexStringFromByteArray(separator6));
		strBuffer.append("]");
		strBuffer.append(System.getProperty("line.separator"));

		strBuffer.append("prime2=[");
		strBuffer.append(HexUtil.getHexStringFromByteArray(prime2));
		strBuffer.append("]");
		strBuffer.append(System.getProperty("line.separator"));

		strBuffer.append("separator7=[");
		strBuffer.append(HexUtil.getHexStringFromByteArray(separator7));
		strBuffer.append("]");
		strBuffer.append(System.getProperty("line.separator"));

		strBuffer.append("exponent1=[");
		strBuffer.append(HexUtil.getHexStringFromByteArray(exponent1));
		strBuffer.append("]");
		strBuffer.append(System.getProperty("line.separator"));

		strBuffer.append("separator8=[");
		strBuffer.append(HexUtil.getHexStringFromByteArray(separator8));
		strBuffer.append("]");
		strBuffer.append(System.getProperty("line.separator"));

		strBuffer.append("exponent2=[");
		strBuffer.append(HexUtil.getHexStringFromByteArray(exponent2));
		strBuffer.append("]");
		strBuffer.append(System.getProperty("line.separator"));

		strBuffer.append("separator9=[");
		strBuffer.append(HexUtil.getHexStringFromByteArray(separator9));
		strBuffer.append("]");
		strBuffer.append(System.getProperty("line.separator"));

		strBuffer.append("coefficient=[");
		strBuffer.append(HexUtil.getHexStringFromByteArray(coefficient));
		strBuffer.append("]");
		// strBuffer.append(System.getProperty("line.separator"));

		return strBuffer.toString();

	}
}
