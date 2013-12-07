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

package kr.pe.sinnori.client.io;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.exception.NoMatchOutputMessage;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.ServerExcecutorUnknownException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.message.OutputMessage;

/**
 * 최종 수신을 하는 지점에 전달되는 서버로 부터 전달된 메시지 목록 클래스.<br/>
 * 참고) 클라이언트에 전달하고 싶은 에러 내용을 담고 있는 SelfExn 메시지는 <br/>
 * 최종 수신을 하는 지점에서 목록을 요청시 내용에 맞는 예외를 던진다.
 * 
 * @author Jonghoon Won
 * 
 */
public class LetterFromServer implements CommonRootIF {
	OutputMessage outObj = null;

	/**
	 * 생성자
	 */
	public LetterFromServer(OutputMessage outObj) {
		this.outObj = outObj;
	}

	
	
	/**
	 * 서버로 부터 받은 출력 메시지를 반환한다. 단 클라이언트에 전달하고 싶은 에러 내용을 담고 있는 SelfExn 메시지는
	 * 내용에 맞는 예외를 던진다.
	 * 
	 * @param wantedOutputMessageID 원하는 출력 메시지 식별자
	 * @return 서버로 부터 받은 출력 메시지
	 * @throws BodyFormatException 바디 구성 실패시 발생
	 * @throws DynamicClassCallException 동적 호출 클래스 호출 실패시 발생
	 * @throws NoMoreDataPacketBufferException 바디 버퍼 확보 실패시 발생
	 * @throws MessageInfoNotFoundException 서버에서 입력 메시지 정보 파일 미 존재 혹은 비지니스 로직 수행시 출력 메시지 정보 파일 미 존재시 발생
	 * @throws NoMatchOutputMessage 원하는 출력 메시지를 얻지 못했을때 발생
	 * @throws MessageItemException 메시지에서 항목 값을 얻어올때 혹은 항목 값을 설정할때 항목 관련 에러 발생시 던지는 예외 
	 * @throws ServerExcecutorUnknownException 서버 비지니스 로직 실행시 알수 없는 에러 발생시 던지는 예외
	 * @throws NotLoginException 로그인 서비스에 비 로그인 접근시 던지는 예외
	 */
	public OutputMessage getOutputMessage(String wantedOutputMessageID) throws 
			BodyFormatException, DynamicClassCallException,
			NoMoreDataPacketBufferException, MessageInfoNotFoundException, 
			NoMatchOutputMessage, MessageItemException, 
			ServerExcecutorUnknownException, NotLoginException {
		
		String outputMessageID = outObj.getMessageID();
		if (outputMessageID.equals("SelfExn")) {
			StringBuilder errorBuffer = new StringBuilder();

			String whereError = (String) outObj.getAttribute("whereError");
			String errorGubun = (String) outObj.getAttribute("errorGubun");
			String errorMessageID = (String) outObj.getAttribute("errorMessageID");
			String errorMessage = (String) outObj.getAttribute("errorMessage");

			// 일반 메세지
			if (whereError.equals("S")) {
				errorBuffer.append("서버에서");
			} else if (whereError.equals("C")) {
				errorBuffer.append("클라이언트에서");	
			} else {
				errorBuffer.delete(0, errorBuffer.length());
				errorBuffer.append("SelfExn 메시지에서 알수없는 에러 장소 =[");
				errorBuffer.append(outObj.toString());
				errorBuffer.append("]");
				
				log.fatal(errorBuffer.toString());
				System.exit(1);
			}
			errorBuffer.append(" ");
			
			errorBuffer.append("에러 발생, 에러가 발생한 메시지 식별자=[");
			errorBuffer.append(errorMessageID);
			errorBuffer.append("]");
			errorBuffer.append(", 에러 내용=[");
			errorBuffer.append(errorMessage.trim());
			errorBuffer.append("]");

			if (errorGubun.equals("B")) {
				throw new BodyFormatException(errorBuffer.toString());
			}
			if (errorGubun.equals("D")) {
				throw new DynamicClassCallException(errorBuffer.toString());
			}
			if (errorGubun.equals("N")) {
				throw new NoMoreDataPacketBufferException(errorBuffer.toString());
			}
			if (errorGubun.equals("M")) {
				throw new MessageInfoNotFoundException(errorBuffer.toString());
			}
			
			if (errorGubun.equals("I")) {
				throw new MessageItemException(errorBuffer.toString());
			}
			
			if (errorGubun.equals("U")) {
				throw new ServerExcecutorUnknownException(errorBuffer.toString());
			}
			
			if (errorGubun.equals("A")) {
				throw new NotLoginException(errorBuffer.toString());
			}
			
			errorBuffer.delete(0, errorBuffer.length());
			errorBuffer.append("SelfExn 메시지에서 알수없는 에러 구분=[");
			errorBuffer.append(outObj.toString());
			errorBuffer.append("]");
			
			log.fatal(errorBuffer.toString());
			System.exit(1);
		}
		
		if (!wantedOutputMessageID.equals(outputMessageID)) {
			throw new NoMatchOutputMessage(String.format("결과로 얻은 출력 메시지[%s]와 원하는 출력 메시지[%s]가 일치하지 않습니다.", outputMessageID, wantedOutputMessageID));
		}

		return outObj;
	}
}
