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

package kr.pe.sinnori.common.io;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import kr.pe.sinnori.common.exception.SinnoriBufferUnderflowException;
import kr.pe.sinnori.common.exception.SinnoriCharsetCodingException;

/**
 * 이진 스트림에서 각각의 데이터 타입별 읽기 기능 제공자 인터페이스
 * 
 * @see FixedSizeInputStream
 * @see FreeSizeInputStream
 * @author Won Jonghoon
 * 
 */
public interface InputStreamIF {
	/**
	 * @return byte 데이터
	 * @throws SinnoriBufferUnderflowException
	 *             버퍼 크기를 넘어서는 읽기 시도시 발생
	 */
	public byte getByte() throws SinnoriBufferUnderflowException;

	/**
	 * unsigned byte 데이터를 읽어 반환한다.
	 * 
	 * @return unsigned byte 데이터
	 * @throws SinnoriBufferUnderflowException
	 *             버퍼 크기를 넘어서는 읽기 시도시 발생
	 */
	public short getUnsignedByte() throws SinnoriBufferUnderflowException;

	/**
	 * short 데이터를 읽어 반환한다.
	 * 
	 * @return short 데이터
	 * @throws SinnoriBufferUnderflowException
	 *             버퍼 크기를 넘어서는 읽기 시도시 발생
	 */
	public short getShort() throws SinnoriBufferUnderflowException;

	/**
	 * unsigned short 데이터를 읽어 반환한다.
	 * 
	 * @return unsigned short 데이터
	 * @throws SinnoriBufferUnderflowException
	 *             버퍼 크기를 넘어서는 읽기 시도시 발생
	 */
	public int getUnsignedShort() throws SinnoriBufferUnderflowException;

	/**
	 * integer 데이터를 읽어 반환한다.
	 * 
	 * @return integer 데이터
	 * @throws SinnoriBufferUnderflowException
	 *             버퍼 크기를 넘어서는 읽기 시도시 발생
	 */
	public int getInt() throws SinnoriBufferUnderflowException;

	/**
	 * unsigned integer 데이터를 읽어 반환한다.
	 * 
	 * @return unsigned integer 데이터
	 * @throws SinnoriBufferUnderflowException
	 *             버퍼 크기를 넘어서는 읽기 시도시 발생
	 */
	public long getUnsignedInt() throws SinnoriBufferUnderflowException;

	/**
	 * long 데이터를 읽어 반환한다.
	 * 
	 * @return long 데이터
	 * @throws SinnoriBufferUnderflowException
	 *             버퍼 크기를 넘어서는 읽기 시도시 발생
	 */
	public long getLong() throws SinnoriBufferUnderflowException;

	/**
	 * 이진 스트림에서 지정된 길이의 데이터를 지정된 문자셋으로 읽어서 얻은 문자열을 반환한다.
	 * 
	 * @param len
	 *            지정된 길이, 단위 byte
	 * @param wantedCharsetDecoder
	 *            지정된 문자셋 디코더
	 * @return 이진 스트림에서 지정된 길이의 데이터를 지정된 문자셋으로 읽어서 얻은 문자열
	 * @throws SinnoriBufferUnderflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 넘어서는 읽기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생
	 * @throws SinnoriCharsetCodingException 문자셋 디코딩시 에러 발생시 던지는 예외
	 */
	public String getString(final int len, final CharsetDecoder wantedCharsetDecoder)
			throws SinnoriBufferUnderflowException, IllegalArgumentException, SinnoriCharsetCodingException;

	/**
	 * 이진 스트림에서 지정된 길이의 데이터를 스트림 고유 문자셋으로 읽어서 얻은 문자열을 반환한다.
	 * 
	 * @param len
	 *            지정된 길이, 단위 byte
	 * @return 이진 스트림에서 지정된 길이의 데이터를 스트림 고유 문자셋으로 읽어서 얻은 문자열
	 * @throws SinnoriBufferUnderflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 넘어서는 읽기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생
	 * @throws SinnoriCharsetCodingException 문자셋 디코딩시 에러 발생시 던지는 예외
	 */
	public String getString(final int len) throws SinnoriBufferUnderflowException,
			IllegalArgumentException, SinnoriCharsetCodingException;

	/**
	 * 남아 있는 모든 데이터를 스트림 고유 문자셋으로 읽어서 얻은 문자열을 반환한다.
	 * 
	 * @return 남아 있는 모든 데이터를 스트림 고유 문자셋으로 읽어서 얻은 문자열
	 * @throws SinnoriBufferUnderflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 넘어서는 읽기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생
	 * @throws SinnoriCharsetCodingException 문자셋 디코딩시 에러 발생시 던지는 예외
	 */
	public String getStringAll() throws SinnoriBufferUnderflowException,
			IllegalArgumentException, SinnoriCharsetCodingException;

	/**
	 * 문자열 길이 타입을 unsigned byte 로 하는 원조 파스칼 문자열을 반환한다.
	 * 
	 * @return 문장열
	 * @throws SinnoriBufferUnderflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 넘어서는 읽기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생
	 * @throws SinnoriCharsetCodingException 문자셋 디코딩시 에러 발생시 던지는 예외
	 */
	public String getPascalString() throws SinnoriBufferUnderflowException,
			IllegalArgumentException, SinnoriCharsetCodingException;

	/**
	 * 문자열 길이 타입을 integer 로 하는 파스칼 문자열을 반환한다.
	 * 
	 * @return 문장열
	 * @throws SinnoriBufferUnderflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 넘어서는 읽기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생
	 * @throws SinnoriCharsetCodingException 문자셋 디코딩시 에러 발생시 던지는 예외
	 */
	public String getSIPascalString() throws SinnoriBufferUnderflowException,
			IllegalArgumentException, SinnoriCharsetCodingException;

	/**
	 * 문자열 길이 타입을 unsigned short 로 하는 파스칼 문자열을 반환한다.
	 * 
	 * @return 문장열
	 * @throws SinnoriBufferUnderflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 넘어서는 읽기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생
	 * @throws SinnoriCharsetCodingException 문자셋 디코딩시 에러 발생시 던지는 예외
	 */
	public String getUSPascalString() throws SinnoriBufferUnderflowException,
			IllegalArgumentException, SinnoriCharsetCodingException;

	/**
	 * 문자열 길이 타입을 unsigned byte 로 하는 파스칼 문자열을 반환한다.
	 * 
	 * @return 문장열
	 * @throws SinnoriBufferUnderflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 넘어서는 읽기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생
	 * @throws SinnoriCharsetCodingException 문자셋 디코딩시 에러 발생시 던지는 예외
	 */
	public String getUBPascalString() throws SinnoriBufferUnderflowException,
	IllegalArgumentException, SinnoriCharsetCodingException;

	/**
	 * 이진 스트림에서 지정된 크기 만큼 읽어서 바이트 배열의 지정된 위치에 저장한다.
	 * 
	 * @param dstBuffer
	 *            목적지 바이트 배열
	 * @param offset
	 *            목저지 바이트 배열내에 데이터가 저장될 시작 위치
	 * @param len
	 *            길이
	 * @throws SinnoriBufferUnderflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 넘어서는 읽기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생한다.
	 */
	public void getBytes(byte[] dstBuffer, int offset, int len)
			throws SinnoriBufferUnderflowException, IllegalArgumentException;

	/**
	 * 이진 스트림에서 목적지 바이트 배열의 크기 만큼 읽어서 목적지 바이트 배열로 데이터를 복사한다.
	 * 
	 * @param dstBuffer
	 *            목적지 바이트 배열
	 * @throws SinnoriBufferUnderflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 넘어서는 읽기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생한다.
	 */
	public void getBytes(byte[] dstBuffer) throws SinnoriBufferUnderflowException,
			IllegalArgumentException;

	/**
	 * 이진 스트림에서 지정된 길이 만큼 읽어서 바이트 배열에 넣어 반환한다.
	 * 
	 * @param len
	 *            지정된 길이
	 * @return 스트림에서 읽은 지정된 길이의 데이터를 가지는 바이트 배열
	 * @throws SinnoriBufferUnderflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 넘어서는 읽기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생한다.
	 */
	public byte[] getBytes(int len) throws SinnoriBufferUnderflowException,
			IllegalArgumentException;

	/**
	 * 지정된 크기 만큼 읽을 위치를 이동시킨다.
	 * 
	 * @param len
	 *            건너 뛰기를 원하는 길이
	 * @throws SinnoriBufferUnderflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 넘어서는 읽기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생한다.
	 */
	public void skip(int len) throws SinnoriBufferUnderflowException,
			IllegalArgumentException;

	/**
	 * @return 스트림 문자셋
	 */
	public Charset getCharset();
	
	/**
	 * 스트림의 바이트 순서를 반환한다.
	 * 
	 * @return 바이트 순서
	 */
	public ByteOrder getByteOrder();

	/**
	 * 남아 있는 용량을 반환한다.
	 * 
	 * @return 남아 있는 용량
	 */
	public long remaining();

	/**
	 * 스트림 안에서의 위치를 반환한다.
	 * 
	 * @return 스트림 안에서의 위치
	 */
	public long position();
	
	
	
	/**
	 * 현재 작업 커서 이후로 검색할 바이트 배열과 일치하는 위치를 반환한다.
	 * @param searchBytes 검색할 바이트 배열
	 * @return 현재 작업 커서 이후의 검색할 바이트 배열과 일치하는 첫번째 위치, 못찾았거나 혹은 스트림이 닫혔다면 -1을 반환한다.
	 */
	public long indexOf(byte[] searchBytes);
	
	public void close();
}
