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

package kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage;

import java.util.Iterator;
import java.util.NoSuchElementException;

import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.handler.OutputMessageReader;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.handler.OutputMessageReaderIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.threadpool.AbstractThreadPool;

/**
 * 클라이언트 출력 메시지 소켓 읽기 담당 쓰레드 폴
 * 
 * @see OutputMessageReader
 * @author Won Jonghoon
 */
public class OutputMessageReaderPool extends AbstractThreadPool implements OutputMessageReaderPoolIF {
	private String projectName = null;
	private long readSelectorWakeupInterval;
	private MessageProtocolIF messageProtocol = null;

	public OutputMessageReaderPool(String projectName, int size, long readSelectorWakeupInterval,
			MessageProtocolIF messageProtocol) {
		if (size <= 0) {
			throw new IllegalArgumentException(String.format("%s 파라미터 size 는 0보다 커야 합니다.", projectName));
		}

		this.projectName = projectName;
		this.readSelectorWakeupInterval = readSelectorWakeupInterval;
		this.messageProtocol = messageProtocol;

		for (int i = 0; i < size; i++) {
			addHandler();
		}
	}

	@Override
	public void addHandler() {
		synchronized (monitor) {
			int size = pool.size();

			try {
				Thread handler = new OutputMessageReader(projectName, size, readSelectorWakeupInterval,
						messageProtocol);
				pool.add(handler);
			} catch (Exception e) {
				String errorMessage = String.format("%s OutputMessageReader[%d] 등록 실패", projectName, size);
				log.warn(errorMessage, e);
				throw new RuntimeException(errorMessage);
			}

		}
	}

	@Override
	public OutputMessageReaderIF getNextOutputMessageReader() {
		Iterator<Thread> poolIter = pool.iterator();
		int min = Integer.MAX_VALUE;

		OutputMessageReaderIF minOutputMessageReader = null;

		if (!poolIter.hasNext()) {
			throw new NoSuchElementException("ClientExecutorPool empty");
		}

		minOutputMessageReader = (OutputMessageReaderIF) poolIter.next();
		min = minOutputMessageReader.getNumberOfAsynConnection();

		while (poolIter.hasNext()) {
			OutputMessageReaderIF outputMessageReader = (OutputMessageReaderIF) poolIter.next();
			int numberOfAsynConnection = outputMessageReader.getNumberOfAsynConnection();
			if (numberOfAsynConnection < min) {
				minOutputMessageReader = outputMessageReader;
				min = numberOfAsynConnection;
			}
		}

		return minOutputMessageReader;
	}
}
