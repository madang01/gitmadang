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


package kr.pe.sinnori.client;

/**
 * 클라리언트 프로젝트 모니터링 정보
 * @author Jonghoon Won
 *
 */
public class ClientProjectMonitorInfo {
	public String projectName;
	public int dataPacketBufferQueueSize;
	
	public int usedMailboxCnt;
	public int totalMailbox;
	
	public int inputMessageQueueSize;
	public int syncOutputMessageQueueQueueSize;
	public int AsynOutputMessageQueueSize;
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientProjectInfo [projectName=");
		builder.append(projectName);
		builder.append(", dataPacketBufferQueueSize=");
		builder.append(dataPacketBufferQueueSize);
		
		builder.append(", usedMailboxCnt=");
		builder.append(usedMailboxCnt);
		
		builder.append(", totalMailbox=");
		builder.append(totalMailbox);
		
		builder.append(", inputMessageQueueSize=");
		builder.append(inputMessageQueueSize);
		
		builder.append(", SyncOutputMessageQueueQueueSize=");
		builder.append(syncOutputMessageQueueQueueSize);
		
		builder.append(", AsynOutputMessageQueueSize=");
		builder.append(AsynOutputMessageQueueSize);
		
		builder.append("]");
		return builder.toString();
	}
	
}
