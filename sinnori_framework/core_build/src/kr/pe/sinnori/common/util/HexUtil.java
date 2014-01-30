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
package kr.pe.sinnori.common.util;

import java.nio.ByteBuffer;

/**
 * Hex 출력을 도와주는 유틸<br/>
 * 출력 형식은 hex 코드를 대괄호로 묶어 출력함<br/>
 * 예제1) [0x10 0x20]<br/>
 * 
 * @author Jonghoon Won
 */

public class HexUtil {
	static final String[] hexTable = { "00", "01", "02", "03", "04", "05",
			"06", "07", "08", "09", "0a", "0b", "0c", "0d", "0e", "0f", "10",
			"11", "12", "13", "14", "15", "16", "17", "18", "19", "1a", "1b",
			"1c", "1d", "1e", "1f", "20", "21", "22", "23", "24", "25", "26",
			"27", "28", "29", "2a", "2b", "2c", "2d", "2e", "2f", "30", "31",
			"32", "33", "34", "35", "36", "37", "38", "39", "3a", "3b", "3c",
			"3d", "3e", "3f", "40", "41", "42", "43", "44", "45", "46", "47",
			"48", "49", "4a", "4b", "4c", "4d", "4e", "4f", "50", "51", "52",
			"53", "54", "55", "56", "57", "58", "59", "5a", "5b", "5c", "5d",
			"5e", "5f", "60", "61", "62", "63", "64", "65", "66", "67", "68",
			"69", "6a", "6b", "6c", "6d", "6e", "6f", "70", "71", "72", "73",
			"74", "75", "76", "77", "78", "79", "7a", "7b", "7c", "7d", "7e",
			"7f", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89",
			"8a", "8b", "8c", "8d", "8e", "8f", "90", "91", "92", "93", "94",
			"95", "96", "97", "98", "99", "9a", "9b", "9c", "9d", "9e", "9f",
			"a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8", "a9", "aa",
			"ab", "ac", "ad", "ae", "af", "b0", "b1", "b2", "b3", "b4", "b5",
			"b6", "b7", "b8", "b9", "ba", "bb", "bc", "bd", "be", "bf", "c0",
			"c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "c9", "ca", "cb",
			"cc", "cd", "ce", "cf", "d0", "d1", "d2", "d3", "d4", "d5", "d6",
			"d7", "d8", "d9", "da", "db", "dc", "dd", "de", "df", "e0", "e1",
			"e2", "e3", "e4", "e5", "e6", "e7", "e8", "e9", "ea", "eb", "ec",
			"ed", "ee", "ef", "f0", "f1", "f2", "f3", "f4", "f5", "f6", "f7",
			"f8", "f9", "fa", "fb", "fc", "fd", "fe", "ff" };

	public static byte[] hexToByteArray(String hexStr)
			throws NumberFormatException {
		if (hexStr == null || hexStr.length() == 0) {
			return null;
		}

		byte[] retBytes = new byte[hexStr.length() / 2];
		for (int i = 0; i < retBytes.length; i++) {
			retBytes[i] = (byte) Integer.parseInt(
					hexStr.substring(2 * i, 2 * i + 2), 16);
		}
		return retBytes;
	}
	
	
	public static void hexToByteArray(String hexStr, byte[] dst, int offset)
			throws NumberFormatException {
		if (hexStr == null || hexStr.length() == 0) {
			throw new IllegalArgumentException("param hexStr is null or empty");
		}
		
		if (dst == null) {
			throw new IllegalArgumentException("param dst is null");
		}
		
		if (offset < 0) {
			throw new IllegalArgumentException("param offset less than zero");
		}
		
		int hexStrLen = hexStr.length();
		
		if (0 != hexStrLen % 2) {
			String errorMessage = String.format("파라미터 핵사 문자열의 크기[%d]가 짝수가 아닙니다.", hexStrLen);
			throw new IllegalArgumentException(errorMessage);
		}
		
		int dataLen = hexStrLen / 2;
		
		if ((dst.length - offset) < dataLen) {
			String errorMessage = String.format("목적지 버퍼[%d]의 오프셋[%d] 이후 남은 크기가 핵사 문자열[%d]을 담을 수 없을 만큼 작습니다.", dst.length, offset, dataLen);
			throw new IllegalArgumentException(errorMessage);
		}

		
		for (int i = 0; i < dataLen; i++) {
			dst[offset+i] = (byte) Integer.parseInt(
					hexStr.substring(2 * i, 2 * i + 2), 16);
		}
		
	}

	public static String byteBufferAllToHex(ByteBuffer buffer) throws IllegalArgumentException {
		if (null == buffer) {
			throw new IllegalArgumentException("parm buffer is null");
		}
		
		int capacity = buffer.capacity();
		return byteBufferToHex(buffer, 0, capacity);
	}

	public static String byteBufferAvailableToHex(ByteBuffer buffer) throws IllegalArgumentException {
		if (null == buffer) {
			throw new IllegalArgumentException("parm buffer is null");
		}
		
		int position = buffer.position();
		int limit = buffer.limit();
		return byteBufferToHex(buffer, position, limit);
	}

	public static String byteBufferToHex(ByteBuffer buffer, int offset,
			int length) throws IllegalArgumentException {
		if (null == buffer) {
			throw new IllegalArgumentException("parm buffer is null");
		}
		
		if (offset < 0) {
			throw new IllegalArgumentException("parm offset less than zero");
		}

		if (length < 0) {
			throw new IllegalArgumentException("parm length less than zero");
		}

		int capacity = buffer.capacity();

		if (offset > capacity) {
			String errorMessage = String.format("parm offset[%d] over than parm buffer'capacity[%d]", offset, capacity);
			throw new IllegalArgumentException(errorMessage);
		}

		int size = offset + length;

		if (size > capacity) {
			String errorMessage = String.format("sum of parm offset[%d] and parm length[%d] over than parm buffer'capacity[%d]", offset, length, capacity);
			throw new IllegalArgumentException(errorMessage);
		}

		ByteBuffer dupBuffer = buffer.duplicate();
		dupBuffer.clear();

		StringBuffer strbuff = new StringBuffer();

		for (int j = offset; j < size; j++) {
			byte one_byte = dupBuffer.get(j);
			int inx = 0xff & one_byte;
			strbuff.append(hexTable[inx]);
		}

		return strbuff.toString();
	}

	public static String byteArrayAllToHex(byte[] buffer) throws IllegalArgumentException {
		return byteArrayToHex(buffer, 0, buffer.length);
	}

	public static String byteArrayToHex(byte[] buffer, int offset, int length) throws IllegalArgumentException {
		if (null == buffer) {
			throw new IllegalArgumentException("parm buffer is null");
		}
		
		if (offset < 0) {
			throw new IllegalArgumentException("parm offset less than zero");
		}

		if (length < 0) {
			throw new IllegalArgumentException("parm length less than zero");
		}

		int capacity = buffer.length;

		if (offset > capacity) {
			String errorMessage = String.format("parm offset[%d] over than parm buffer'capacity[%d]", offset, buffer.length);
			throw new IllegalArgumentException(errorMessage);
		}

		int size = offset + length;

		if (size > capacity) {
			String errorMessage = String.format("sum of parm offset[%d] and parm length[%d] over than parm buffer'capacity[%d]", offset, length, buffer.length);
			throw new IllegalArgumentException(errorMessage);
		}

		StringBuffer strbuff = new StringBuffer();
		for (int j = offset; j < size; j++) {
			byte one_byte = buffer[j];
			int inx = 0xff & one_byte;
			strbuff.append(hexTable[inx]);
		}
		return strbuff.toString();
	}
}
