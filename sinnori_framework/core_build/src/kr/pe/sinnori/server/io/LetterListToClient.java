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
package kr.pe.sinnori.server.io;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonStaticFinal;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.server.ClientResourceManagerIF;

/**
 * 메시지 운영 정보가 세팅된 클라이언트에게 보내는 편지들 묶음을 작성해 주는 클래스<br/>
 * 참고) 클라이언트에게 보내는 편지를 모아서 최종적으로 클라이언트에게 보내기전 메시지 운영에 필요한 정보를 세팅해 준다.
 * 
 * @author Jonghoon Won
 * 
 */
public class LetterListToClient implements CommonRootIF {
	private SocketChannel fromSC = null;
	private InputMessage inObj = null;
	private ClientResourceManagerIF clientResourceManager;
	
	private ArrayList<LetterToClient> letterListToClient = new ArrayList<LetterToClient>();

	
	/**
	 * 생성자
	 * @param fromSC 입력 메시지를 보낸 클라이언트(=소켓 채널)
	 * @param inObj 입력 메시지
	 * @param clientResourceManager 클라이언트 자원 관리자
	 */
	public LetterListToClient(SocketChannel fromSC, InputMessage inObj,
			ClientResourceManagerIF clientResourceManager) {
		if (null == fromSC)
			throw new IllegalArgumentException("파라미터 입력메시지를 보낸 소켓 채널이 널입니다.");
		this.fromSC = fromSC;
		this.inObj = inObj;
		this.clientResourceManager = clientResourceManager;
	}

	/**
	 * 클라이언트에게 보내는 편지들 목록에 신규 편지를 추가한다.
	 * 
	 * @param toSC
	 *            출력 메시지를 받을 소켓 채널
	 * @param outObj
	 *            출력 메시지
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생
	 */
	public void addLetterToClient(SocketChannel toSC,
			OutputMessage outObj) throws IllegalArgumentException {

		if (null == toSC)
			throw new IllegalArgumentException(
					"파라미터 출력메시지를 보낼 소켓 채널이 null 입니다.");

		if (null == outObj)
			throw new IllegalArgumentException(
					"파라미터 출력메시지가 null 입니다.");

		if (fromSC.equals(toSC)) {
			outObj.messageHeaderInfo = inObj.messageHeaderInfo;
		} else {
			outObj.messageHeaderInfo.mailboxID = CommonStaticFinal.SERVER_MAILBOX_ID;
			outObj.messageHeaderInfo.mailID = clientResourceManager.getClientResource(toSC).getServerMailID();
		}

		LetterToClient letterToClient = new LetterToClient(toSC,
				outObj);
		letterListToClient.add(letterToClient);
	}
	
	/**
	 * <pre>
	 * 클라이언트로 보내는 편지 목록을 모두 삭제한다. 
	 * 처리 중간에 클라이언트에게 알려야할 에러가 발생하여 
	 * SelfExn 메시지만 클라이언트로 보내는 편지 목록에 담겨야 할 경우 호출된다.
	 * </pre> 
	 */
	/*
	public void clear() {
		letterListToClient.clear();
	}
	*/

	/**
	 * 클라이언트에게 보내는 편지 묶음을 반환한다.
	 * 
	 * @return 메시지 운영 정보가 세팅된 클라이언트에게 보내는 편지들 묶음
	 */
	public ArrayList<LetterToClient> getList() {
		return letterListToClient;
	}
}
