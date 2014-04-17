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

package kr.pe.sinnori.common.lib;

import java.nio.ByteBuffer;

/**
 * <pre>
 * 큐 입출력 특성에 맞게 제작된 랩 버퍼 클래스.
 * 참고) 큐에 2번이상 중복 막기및 반환이 잘 되지 않을 경우 추적이 용이하기 위해서 랩 버퍼를 사용한다.
 * 추적을 위해서 가장 좋은 해결 방법은 큐에서 랩버퍼를 꺼내는 시점의 Throwable 를 기억하는 것이다.
 * 하지만 이 방법은 비용이 많이 든다. 따라서 큐에 반환되지 않고 랩 버퍼 소멸시에만 사용하기를 권한다.
 * </pre> 
 * @author Jonghoon Won
 * 
 */
public class WrapBuffer implements CommonRootIF {
	private ByteBuffer buffer = null;
	private boolean isInQueue = true;
	// private Throwable lastCallerThrowable = null;

	/**
	 * 생성자
	 * @param capacity 큐 ByteBuffer 용량
	 */
	public WrapBuffer(int capacity) {
		//buffer = ByteBuffer.allocate(capacity);
		buffer = ByteBuffer.allocateDirect(capacity);
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
		buffer.clear();
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
		log.warn("큐에 반환되지 못한 랩 버퍼 소멸");
		// log.warning(lastCallerThrowable, "랩 버퍼 파괴");
	}

	/**
	 * 랩 버퍼가 가진 알맹이 바이트 버퍼를 반환한다.
	 * 
	 * @return 랩버퍼가 감싸고 있는 바이트 버퍼
	 */
	public ByteBuffer getByteBuffer() {
		return buffer;
	}

	@Override
	public String toString() {
		StringBuilder strBuffer = new StringBuilder("ByteBuffer=[");
		strBuffer.append(buffer.toString());
		strBuffer.append("], isInQueue=[");
		strBuffer.append(isInQueue);
		strBuffer.append("]");
		return strBuffer.toString();
	}
}
