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
package kr.pe.codda.common.util;

import java.nio.ByteBuffer;

/**
 * Hex 출력을 도와주는 유틸<br/>
 * 출력 형식은 파싱을 고려하 hex 코드를 나열하여 출력함<br/>
 * 예제) 107f<br/>
 * 
 * @author Won Jonghoon
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

	public static byte[] getByteArrayFromHexString(String hexStr)
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
	
	public static String getAllHexStringFromByteBuffer(ByteBuffer buffer) throws IllegalArgumentException {
		if (null == buffer) {
			throw new IllegalArgumentException("the parameter buffer is null");
		}
		
		int capacity = buffer.capacity();
		return getHexStringFromByteBuffer(buffer, 0, capacity);
	}
	
	public static String getHexString(byte value) throws IllegalArgumentException {
		int inx = 0xff & value;
		return hexTable[inx];
	}
	
	public static String getHexString(short value) throws IllegalArgumentException {
		byte t0 = (byte)value;
		byte t1 = (byte)(value >> 8);
		return new StringBuilder(getHexString(t1)).append(getHexString(t0)).toString();
	}
	
	public static String getHexString(int value) throws IllegalArgumentException {
		byte t0 = (byte)value;
		byte t1 = (byte)(value >>> 8);
		byte t2 = (byte)(value >>> 16);
		byte t3 = (byte)(value >>> 24);
		return new StringBuilder(getHexString(t3))
				.append(getHexString(t2))
				.append(getHexString(t1))
				.append(getHexString(t0)).toString();
	}
	
	public static String getHexString(long value) throws IllegalArgumentException {
		byte t0 = (byte)value;
		byte t1 = (byte)(value >>> 8);
		byte t2 = (byte)(value >>> 16);
		byte t3 = (byte)(value >>> 24);
		byte t4 = (byte)(value >>> 32);
		byte t5 = (byte)(value >>> 40);
		byte t6 = (byte)(value >>> 48);
		byte t7 = (byte)(value >>> 56);		
		
		return new StringBuilder(getHexString(t7))
				.append(getHexString(t6))
				.append(getHexString(t5))
				.append(getHexString(t4))
				.append(getHexString(t3))
				.append(getHexString(t2))
				.append(getHexString(t1))
				.append(getHexString(t0))
				.toString();
	}

	public static String getHexStringFromByteBuffer(ByteBuffer buffer) throws IllegalArgumentException {
		if (null == buffer) {
			throw new IllegalArgumentException("the parameter buffer is null");
		}
		
		int position = buffer.position();
		int limit = buffer.limit();
		return getHexStringFromByteBuffer(buffer, position, limit);
	}

	public static String getHexStringFromByteBuffer(ByteBuffer buffer, int offset,
			int length) throws IllegalArgumentException {
		if (null == buffer) {
			throw new IllegalArgumentException("the parameter buffer is null");
		}
		
		if (offset < 0) {
			String errorMessage = String.format("the parameter offset[%d] less than zero", offset);
			throw new IllegalArgumentException(errorMessage);
		}

		if (length < 0) {
			String errorMessage = String.format("the parameter length[%d] less than zero", length);
			throw new IllegalArgumentException(errorMessage);
		}

		int capacity = buffer.capacity();

		if (offset > capacity) {
			String errorMessage = String.format("the parameter offset[%d] over than the parameter buffer'capacity[%d]", offset, capacity);
			throw new IllegalArgumentException(errorMessage);
		}

		int size = offset + length;

		if (size > capacity) {
			String errorMessage = String.format("the sum of the parameter offset[%d] and the parameter length[%d] is over than the parameter buffer'capacity[%d]", offset, length, capacity);
			throw new IllegalArgumentException(errorMessage);
		}

		ByteBuffer dupBuffer = buffer.duplicate();
		dupBuffer.clear();

		StringBuffer strbuff = new StringBuffer();

		for (int j = offset; j < size; j++) {
			byte one_byte = dupBuffer.get(j);
			// int inx = 0xff & one_byte;
			strbuff.append(getHexString(one_byte));
		}

		return strbuff.toString();
	}

	public static String getHexStringFromByteArray(byte[] buffer) throws IllegalArgumentException {
		if (null == buffer) {
			throw new IllegalArgumentException("the parameter buffer is null");
		}
		
		return getHexStringFromByteArray(buffer, 0, buffer.length);
	}

	public static String getHexStringFromByteArray(byte[] buffer, int offset, int length) throws IllegalArgumentException {
		if (null == buffer) {
			throw new IllegalArgumentException("the parameter buffer is null");
		}
		
		if (offset < 0) {
			String errorMessage = String.format("the parameter offset[%d] less than zero", offset);
			throw new IllegalArgumentException(errorMessage);
		}

		if (length < 0) {
			String errorMessage = String.format("the parameter length[%d] less than zero", length);
			throw new IllegalArgumentException(errorMessage);
		}

		int capacity = buffer.length;

		if (offset > capacity) {
			String errorMessage = String.format("the parameter offset[%d] over than the parameter buffer'capacity[%d]", offset, capacity);
			throw new IllegalArgumentException(errorMessage);
		}

		int size = offset + length;

		if (size > capacity) {
			String errorMessage = String.format("the sum of the parameter offset[%d] and the parameter length[%d] is over than the parameter buffer'capacity[%d]", offset, length, capacity);
			throw new IllegalArgumentException(errorMessage);
		}

		StringBuffer strbuff = new StringBuffer();
		for (int j = offset; j < size; j++) {
			byte one_byte = buffer[j];
			// int inx = 0xff & one_byte;
			strbuff.append(getHexString(one_byte));
		}
		return strbuff.toString();
	}
}
