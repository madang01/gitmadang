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

import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.protocol.ReceivedLetter;

/**
 * 랩 출력 메시지큐. 출력 메시지를 알맹이로 감싼 랩 클래스.
 * @author Jonghoon Won
 *
 */
public class ClientWrapOutputMessageQueue implements CommonRootIF {
	private boolean isInQueue = true;
	private LinkedBlockingQueue<ReceivedLetter> outputMessageQueue = null;
	
	public ClientWrapOutputMessageQueue(LinkedBlockingQueue<ReceivedLetter> outputMessageQueue) {
		this.outputMessageQueue = outputMessageQueue;
	}
	
	/**
	 * @return 큐에 들어간 상태 여부
	 */
	public boolean isInQueue() {
		return isInQueue;
	}

	/**
	 * 큐에 들어갈대 상태 변화를 
	 */
	public void queueIn() {
		isInQueue = true;
		// lastCallerThrowable = null;
		int i=0;
		while (!outputMessageQueue.isEmpty()) {
			ReceivedLetter receivedLetter = outputMessageQueue.poll();
			if (null == receivedLetter) break;
			log.warn(String.format("랩 출력 메시지큐를 큐에 반환하므로 가지고 있던 receivedLetter[%d][%s] 삭제", i++, receivedLetter.toString()));
		}
	}

	/**
	 * 큐에서 나올때 상태 변화를 주는 클래스
	 */
	public void queueOut() {
		isInQueue = false;
		// lastCallerThrowable = new Throwable();
	}

	@Override
	public void finalize() {
		log.warn("큐에 반환되지 못한 랩 출력 메시지큐 소멸");
		// log.warning(lastCallerThrowable, "랩 출력 메시지큐 파괴");
	}

	/**
	 * 랩 출력 메시지큐가 가진 알맹이  출력 메시지큐를 반환한다.
	 * 
	 * @return 랩 출력 메시지큐가 감싸고 있는 출력 메시지큐
	 */
	public LinkedBlockingQueue<ReceivedLetter> getOutputMessageQueue() {
		return outputMessageQueue;
	}

	@Override
	public String toString() {
		StringBuilder strBuffer = new StringBuilder("ClientWrapOutputMessageQueue=[");
		strBuffer.append(outputMessageQueue.toString());
		strBuffer.append("], isInQueue=[");
		strBuffer.append(isInQueue);
		strBuffer.append("]");
		return strBuffer.toString();
	}
}
