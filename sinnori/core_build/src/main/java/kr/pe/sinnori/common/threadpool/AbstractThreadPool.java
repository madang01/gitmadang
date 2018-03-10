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

package kr.pe.sinnori.common.threadpool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 쓰레드 폴 부모 추상화 클래스
 * 
 * @author Won Jonghoon
 * 
 */
public abstract class AbstractThreadPool implements ThreadPoolIF {
	public Logger log = LoggerFactory.getLogger(AbstractThreadPool.class);
	protected final List<Thread> pool = new ArrayList<Thread>();
	protected final Object monitor = new Object();

	@Override
	public int getPoolSize() {
		// synchronized (monitor) {
			return pool.size();
		// }
	}

	@Override
	public void startAll() {
		Iterator<Thread> iter = pool.iterator();
		while (iter.hasNext()) {
			Thread handler = iter.next();
			
			if (handler.isAlive()) continue; 
				
			handler.start();
		}
	}

	@Override
	public void stopAll() {
		Iterator<Thread> iter = pool.iterator();
		while (iter.hasNext()) {
			Thread handler = iter.next();
			handler.interrupt();
		}
		//pool.clear();
	}
	
}
