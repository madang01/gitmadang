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

package kr.pe.codda.common.io;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import kr.pe.codda.common.exception.CharsetDecoderException;
import kr.pe.codda.common.exception.BufferUnderflowExceptionWithMessage;

/**
 * 이진 스트림에서 각각의 데이터 타입별 읽기 기능 제공자 인터페이스
 * 
 * @see FixedSizeInputStream
 * @see FreeSizeInputStream
 * @author Won Jonghoon
 * 
 */
public interface BinaryInputStreamIF {	
	public byte getByte() throws BufferUnderflowExceptionWithMessage;

	
	public short getUnsignedByte() throws BufferUnderflowExceptionWithMessage;

	
	public short getShort() throws BufferUnderflowExceptionWithMessage;

	
	public int getUnsignedShort() throws BufferUnderflowExceptionWithMessage;

	
	public int getInt() throws BufferUnderflowExceptionWithMessage;

	
	public long getUnsignedInt() throws BufferUnderflowExceptionWithMessage;

	
	public long getLong() throws BufferUnderflowExceptionWithMessage;

	
	public String getFixedLengthString(final int fixedLength, final CharsetDecoder wantedCharsetDecoder)
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException;

	
	public String getFixedLengthString(final int fixedLength) throws BufferUnderflowExceptionWithMessage,
			IllegalArgumentException, CharsetDecoderException;

	
	public String getStringAll() throws BufferUnderflowExceptionWithMessage,
			IllegalArgumentException, CharsetDecoderException;
	
	public String getStringAll(Charset wantedCharset) throws BufferUnderflowExceptionWithMessage,
	IllegalArgumentException, CharsetDecoderException;

	
	public String getPascalString() throws BufferUnderflowExceptionWithMessage,
			IllegalArgumentException, CharsetDecoderException;
	
	public String getPascalString(Charset wantedCharset) throws BufferUnderflowExceptionWithMessage,
	IllegalArgumentException, CharsetDecoderException;

	
	public String getSIPascalString() throws BufferUnderflowExceptionWithMessage,
			IllegalArgumentException, CharsetDecoderException;
	
	public String getSIPascalString(Charset wantedCharset) throws BufferUnderflowExceptionWithMessage,
	IllegalArgumentException, CharsetDecoderException;

	
	public String getUSPascalString() throws BufferUnderflowExceptionWithMessage,
			IllegalArgumentException, CharsetDecoderException;
	
	public String getUSPascalString(Charset wantedCharset) throws BufferUnderflowExceptionWithMessage,
	IllegalArgumentException, CharsetDecoderException;

	
	public String getUBPascalString() throws BufferUnderflowExceptionWithMessage,
	IllegalArgumentException, CharsetDecoderException;
	
	public String getUBPascalString(Charset wantedCharset) throws BufferUnderflowExceptionWithMessage,
	IllegalArgumentException, CharsetDecoderException;

	
	public void getBytes(byte[] dst, int offset, int length)
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException;

	
	public void getBytes(byte[] dst) throws BufferUnderflowExceptionWithMessage,
			IllegalArgumentException;

	
	public byte[] getBytes(int length) throws BufferUnderflowExceptionWithMessage, IllegalArgumentException;

	
	public void skip(int n) throws BufferUnderflowExceptionWithMessage,
			IllegalArgumentException;

	
	public Charset getCharset();
	
	
	public ByteOrder getByteOrder();

	
	public long available();
	
	
	/**
	 * 현재 작업 커서 이후로 검색할 바이트 배열과 일치하는 위치를 반환한다.
	 * @param searchBytes 검색할 바이트 배열
	 * @return 현재 작업 커서 이후의 검색할 바이트 배열과 일치하는 첫번째 위치, 못찾았거나 혹은 스트림이 닫혔다면 -1을 반환한다.
	 */
	public long indexOf(byte[] searchBytes);
	
	public void close();
}
