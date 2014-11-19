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

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;

/**
 * 이진 스트림에서 각각의 데이터 타입별 쓰기 기능 제공자 인터페이스
 * 
 * @see FixedSizeOutputStream
 * @see FreeSizeOutputStream
 * @author Won Jonghoon
 * 
 */
public interface OutputStreamIF {
	/**
	 * @return 스트림 문자셋
	 */
	public Charset getCharset();
	
	
	/**
	 * 이진 스트림에 byte 타입의 데이터를 쓴다.
	 * 
	 * @param value
	 *            byte 타입의 데이터
	 * @throws BufferOverflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 벗어난 쓰기 시도시 발생
	 * @throws NoMoreDataPacketBufferException
	 *             데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */
	public void putByte(byte value) throws BufferOverflowException,
			NoMoreDataPacketBufferException;

	/**
	 * 이진 스트림에 unsigned byte 타입의 데이터를 쓴다.
	 * 
	 * @param value
	 *            unsigned byte 에 대응하는 integer 값
	 * @throws BufferOverflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 벗어난 쓰기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터를 입력하여 발생한다.
	 * @throws NoMoreDataPacketBufferException
	 *             데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */
	public void putUnsignedByte(short value) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException;
	
	/**
	 * 이진 스트림에 unsigned byte 타입의 데이터를 쓴다.
	 * 
	 * @param value
	 *            unsigned byte 에 대응하는 integer 값
	 * @throws BufferOverflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 벗어난 쓰기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터를 입력하여 발생한다.
	 * @throws NoMoreDataPacketBufferException
	 *             데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */
	public void putUnsignedByte(int value) throws BufferOverflowException,
	IllegalArgumentException, NoMoreDataPacketBufferException;

	/**
	 * 이진 스트림에 short 타입의 데이터를 쓴다.
	 * 
	 * @param value
	 *            short 타입의 데이터
	 * @throws BufferOverflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 벗어난 쓰기 시도시 발생
	 * @throws NoMoreDataPacketBufferException
	 *             데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */
	public void putShort(short value) throws BufferOverflowException,
			NoMoreDataPacketBufferException;

	/**
	 * 이진 스트림에 unsigned short 타입의 데이터를 쓴다.
	 * 
	 * @param value
	 *            unsigned short 타입에 대응하는 integer 데이터
	 * @throws BufferOverflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 벗어난 쓰기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터를 입력하여 발생한다.
	 * @throws NoMoreDataPacketBufferException
	 *             데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */
	public void putUnsignedShort(int value) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException;

	/**
	 * 이진 스트림에 integer 타입의 데이터를 쓴다.
	 * 
	 * @param value
	 *            integer 타입의 데이터
	 * @throws BufferOverflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 벗어난 쓰기 시도시 발생
	 * @throws NoMoreDataPacketBufferException
	 *             데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */
	public void putInt(int value) throws BufferOverflowException,
			NoMoreDataPacketBufferException;

	/**
	 * 이진 스트림에 unsigned integer 타입의 데이터를 쓴다.
	 * 
	 * @param value
	 *            unsigned integer 타입에 대응하는 long 데이터
	 * @throws BufferOverflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 벗어난 쓰기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터를 입력하여 발생한다.
	 * @throws NoMoreDataPacketBufferException
	 *             데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */
	public void putUnsignedInt(long value) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException;

	/**
	 * 이진 스트림에 long 타입의 데이터를 쓴다.
	 * 
	 * @param value
	 *            long 타입의 데이터
	 * @throws BufferOverflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 벗어난 쓰기 시도시 발생
	 * @throws NoMoreDataPacketBufferException
	 *             데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */
	public void putLong(long value) throws BufferOverflowException,
			NoMoreDataPacketBufferException;

	/**
	 * 문자열을 이진 스트림에 지정된 길이 만큼 지정된 문자셋으로 변환 후 저장한다.
	 * 
	 * @param len
	 *            지정된 길이, 단위 byte
	 * @param str
	 *            문자열
	 * @param wantedCharsetEncoder
	 *            변환을 원하는 문자셋의 인코더
	 * @throws BufferOverflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 벗어난 쓰기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터를 입력하여 발생한다.
	 * @throws NoMoreDataPacketBufferException
	 *             데이터 패킷 버퍼 확보 실패시 던지는 예외 
	 */
	public void putString(int len, String str,
			CharsetEncoder wantedCharsetEncoder)
			throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException;

	/**
	 * 문자열을 이진 스트림에 지정된 길이 만큼 스트림 고유 문자셋으로 변환 후 저장한다.
	 * 
	 * @param len
	 *            지정된 길이, 단위 byte
	 * @param str
	 *            문자열
	 * @throws BufferOverflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 벗어난 쓰기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터를 입력하여 발생한다.
	 * @throws NoMoreDataPacketBufferException
	 *             데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */
	public void putString(int len, String str) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException;

	/**
	 * 문자열 전체를 이진 스트림에 스트림 고유 문자셋으로 변환 후 저장한다.
	 * 
	 * @param str
	 *            문자열
	 * @throws BufferOverflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 벗어난 쓰기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터를 입력하여 발생한다.
	 * @throws NoMoreDataPacketBufferException
	 *             데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */
	public void putStringAll(String str) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException;

	/**
	 * 문자열 길이 타입을 unsigned byte 로 하는 원조 파스칼 문자열을 이진 스트림에 스트림 고유 문자셋으로 변환 후 저장한다.
	 * 
	 * @param str
	 *            문자열
	 * @throws BufferOverflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 벗어난 쓰기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터를 입력하여 발생한다.
	 * @throws NoMoreDataPacketBufferException
	 *             데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */
	public void putPascalString(String str) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException;

	/**
	 * 문자열 길이 타입을 integer 로 하는 파스칼 문자열을 이진 스트림에 스트림 고유 문자셋으로 변환 후 저장한다.
	 * 
	 * @param str
	 *            문자열
	 * @throws BufferOverflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 벗어난 쓰기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터를 입력하여 발생한다.
	 * @throws NoMoreDataPacketBufferException
	 *             데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */
	public void putSIPascalString(String str) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException;

	/**
	 * 문자열 길이 타입을 unsigned short 로 하는 파스칼 문자열을 이진 스트림에 스트림 고유 문자셋으로 변환 후 저장한다.
	 * 
	 * @param str
	 *            문자열
	 * @throws BufferOverflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 벗어난 쓰기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터를 입력하여 발생한다.
	 * @throws NoMoreDataPacketBufferException
	 *             데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */
	public void putUSPascalString(String str) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException;

	/**
	 * 문자열 길이 타입을 unsigned byte 로 하는 파스칼 문자열을 이진 스트림에 스트림 고유 문자셋으로 변환 후 저장한다.
	 * 
	 * @param str
	 *            문자열
	 * @throws BufferOverflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 벗어난 쓰기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터를 입력하여 발생한다.
	 * @throws NoMoreDataPacketBufferException
	 *             데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */
	public void putUBPascalString(String str) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException;

	/**
	 * 바이트 배열의 시작 위치에서 지정된 크기 만큼의 데이터를 이진 스트림에 저장한다.
	 * 
	 * @param srcBuffer
	 *            이진 스트림에 저장을 원하는 바이트 배열
	 * @param offset
	 *            바이트 배열내 시작 위치
	 * @param length
	 *            지정된 크기
	 * @throws BufferOverflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 벗어난 쓰기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터를 입력하여 발생한다.
	 * @throws NoMoreDataPacketBufferException
	 *             데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */

	public void putBytes(byte[] srcBuffer, int offset, int length)
			throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException;

	/**
	 * 바이트 배열 데이터를 이진 스트림에 저장한다.
	 * 
	 * @param srcBuffer
	 *            이진 스트림에 저장을 원하는 바이트 배열
	 * @throws BufferOverflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 벗어난 쓰기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터를 입력하여 발생한다.
	 * @throws NoMoreDataPacketBufferException
	 *             데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */
	public void putBytes(byte[] srcBuffer) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException;

	/**
	 * 바이트 버퍼 데이터를 이진 스트림에 저장한다.
	 * 
	 * @param srcBuffer
	 *            이진 스트림에 저장을 원하는 바이트 버퍼
	 * @throws BufferOverflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 벗어난 쓰기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터를 입력하여 발생한다.
	 * @throws NoMoreDataPacketBufferException
	 *             데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */
	public void putBytes(ByteBuffer srcBuffer) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException;

	/**
	 * 이진 스트림에서 지정된 크기만큼 거넌 뛰기를 한다.
	 * 
	 * @param skipBytes
	 *            지정된 크기, 단위 byte
	 * @throws BufferOverflowException
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 벗어난 쓰기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터를 입력하여 발생한다.
	 * @throws NoMoreDataPacketBufferException
	 *             데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */
	public void skip(int skipBytes) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException;

	/**
	 * 이진 스트림에서 남아 있는 바이트 수를 반환한다. 참고) 메시지당 최대 가질 수 있는 데이터 패킷 갯수가 환경 변수로 정해 진다.  
	 * 
	 * @return 이진 스트림에서 남아 있는 바이트 수
	 */
	public long remaining();
	
	/**
	 * @return 스트림 위치, 다른 말로 지금까지 쓰기 작업한 스트림 크기.
	 */
	public long postion();
}
