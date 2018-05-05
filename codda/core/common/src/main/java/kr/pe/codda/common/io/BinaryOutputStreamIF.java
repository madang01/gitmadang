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

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import kr.pe.codda.common.exception.CharsetEncoderException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.exception.BufferOverflowExceptionWithMessage;

/**
 * 이진 스트림에서 각각의 데이터 타입별 쓰기 기능 제공자 인터페이스
 * 
 * @see FixedSizeOutputStream
 * @see FreeSizeOutputStream
 * @author Won Jonghoon
 * 
 */
public interface BinaryOutputStreamIF {
	/**
	 * @return 스트림 문자셋
	 */
	public Charset getCharset();

	public void putByte(byte value)
			throws BufferOverflowException, BufferOverflowExceptionWithMessage, NoMoreDataPacketBufferException;

	public void putUnsignedByte(short value) throws BufferOverflowException, BufferOverflowExceptionWithMessage,
			IllegalArgumentException, NoMoreDataPacketBufferException;

	public void putUnsignedByte(int value) throws BufferOverflowException, BufferOverflowExceptionWithMessage,
			IllegalArgumentException, NoMoreDataPacketBufferException;

	public void putUnsignedByte(long value) throws BufferOverflowException, BufferOverflowExceptionWithMessage,
			IllegalArgumentException, NoMoreDataPacketBufferException;

	public void putShort(short value)
			throws BufferOverflowException, BufferOverflowExceptionWithMessage, NoMoreDataPacketBufferException;

	public void putUnsignedShort(int value) throws BufferOverflowException, BufferOverflowExceptionWithMessage,
			IllegalArgumentException, NoMoreDataPacketBufferException;

	public void putUnsignedShort(long value) throws BufferOverflowException, BufferOverflowExceptionWithMessage,
			IllegalArgumentException, NoMoreDataPacketBufferException;

	public void putInt(int value)
			throws BufferOverflowException, BufferOverflowExceptionWithMessage, NoMoreDataPacketBufferException;

	public void putUnsignedInt(long value) throws BufferOverflowException, BufferOverflowExceptionWithMessage,
			IllegalArgumentException, NoMoreDataPacketBufferException;

	public void putLong(long value)
			throws BufferOverflowException, BufferOverflowExceptionWithMessage, NoMoreDataPacketBufferException;

	public void putFixedLengthString(int fixedLength, String src)
			throws BufferOverflowException, BufferOverflowExceptionWithMessage, IllegalArgumentException,
			NoMoreDataPacketBufferException, CharsetEncoderException;
	
	public void putFixedLengthString(int fixedLength, String src, CharsetEncoder wantedCharsetEncoder)
			throws BufferOverflowException, BufferOverflowExceptionWithMessage, IllegalArgumentException,
			NoMoreDataPacketBufferException, CharsetEncoderException;	

	public void putStringAll(String src) throws BufferOverflowException, BufferOverflowExceptionWithMessage,
			IllegalArgumentException, NoMoreDataPacketBufferException, CharsetEncoderException;
	
	public void putStringAll(String src, Charset wantedCharset) throws BufferOverflowException, BufferOverflowExceptionWithMessage,
	IllegalArgumentException, NoMoreDataPacketBufferException, CharsetEncoderException;

	public void putPascalString(String src) throws BufferOverflowException, BufferOverflowExceptionWithMessage,
			IllegalArgumentException, NoMoreDataPacketBufferException, CharsetEncoderException;
	
	public void putPascalString(String src, Charset wantedCharset) throws BufferOverflowException, BufferOverflowExceptionWithMessage,
	IllegalArgumentException, NoMoreDataPacketBufferException, CharsetEncoderException;

	public void putSIPascalString(String src) throws BufferOverflowException, BufferOverflowExceptionWithMessage,
			IllegalArgumentException, NoMoreDataPacketBufferException, CharsetEncoderException;
	
	public void putSIPascalString(String src, Charset wantedCharset) throws BufferOverflowException, BufferOverflowExceptionWithMessage,
	IllegalArgumentException, NoMoreDataPacketBufferException, CharsetEncoderException;

	public void putUSPascalString(String src) throws BufferOverflowException, BufferOverflowExceptionWithMessage,
			IllegalArgumentException, NoMoreDataPacketBufferException, CharsetEncoderException;
	
	public void putUSPascalString(String src, Charset wantedCharset) throws BufferOverflowException, BufferOverflowExceptionWithMessage,
	IllegalArgumentException, NoMoreDataPacketBufferException, CharsetEncoderException;

	public void putUBPascalString(String src) throws BufferOverflowException, BufferOverflowExceptionWithMessage,
			IllegalArgumentException, NoMoreDataPacketBufferException, CharsetEncoderException;
	
	public void putUBPascalString(String src, Charset wantedCharset) throws BufferOverflowException, BufferOverflowExceptionWithMessage,
	IllegalArgumentException, NoMoreDataPacketBufferException, CharsetEncoderException;

	public void putBytes(byte[] src, int offset, int length) throws BufferOverflowException, IllegalArgumentException,
			BufferOverflowExceptionWithMessage, NoMoreDataPacketBufferException;

	public void putBytes(byte[] src) throws BufferOverflowException, BufferOverflowExceptionWithMessage,
			IllegalArgumentException, NoMoreDataPacketBufferException;

	public void putBytes(ByteBuffer src) throws BufferOverflowException, BufferOverflowExceptionWithMessage,
			IllegalArgumentException, NoMoreDataPacketBufferException;

	public void skip(int n) throws BufferOverflowException, BufferOverflowExceptionWithMessage, IllegalArgumentException,
			NoMoreDataPacketBufferException;

	public void close();
}
