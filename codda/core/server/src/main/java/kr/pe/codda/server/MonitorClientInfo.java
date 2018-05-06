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


package kr.pe.codda.server;

import java.nio.channels.SocketChannel;

/**
 * 모니터링용 클라이언트 정보
 * @author Won Jonghoon
 *
 */
public class MonitorClientInfo {
	public SocketChannel sc;
	public SocketResource cr;
	public int scHashCode;
	public boolean isConnected;
	/**
	 * <pre>
	 * 클라이언트에서 메시지 시작 데이터를 보낸후 
	 * 지정 시간을 초과하도록 송신이 없는 경우 
	 * 최종 읽은 시간과 현재 시간과의 차이 값이 설정되고,
	 * 지정 시간안에 데이터 송신이 있었을 경우 -1 값으로 설정된다.
	 * </pre>
	 */
	public long timeout=-1;
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("scHashCode=");
		builder.append(scHashCode);
		builder.append(", isConnected=");
		builder.append(isConnected);
		builder.append(", timeout=");
		builder.append(timeout);
		return builder.toString();
	}
}
