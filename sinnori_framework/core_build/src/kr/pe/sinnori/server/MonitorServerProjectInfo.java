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

package kr.pe.sinnori.server;

import java.util.ArrayList;

/**
 * @author Won Jonghoon
 *
 */
public class MonitorServerProjectInfo {
	public String projectName;
	public int dataPacketBufferQueueSize;
	public int acceptQueueSize;
	public int inputMessageQueueSize;
	public int outputMessageQueueSize;
	public int clientCnt;
	
	
	public ArrayList<MonitorClientInfo> monitorClientInfoList = new ArrayList<MonitorClientInfo>();

	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServerProjectInfo [projectName=");
		builder.append(projectName);
		builder.append(", dataPacketBufferQueueSize=");
		builder.append(dataPacketBufferQueueSize);
		builder.append(", acceptQueueSize=");
		builder.append(acceptQueueSize);
		builder.append(", inputMessageQueueSize=");
		builder.append(inputMessageQueueSize);
		builder.append(", outputMessageQueueSize=");
		builder.append(outputMessageQueueSize);
		builder.append(", clientCnt=");
		builder.append(clientCnt);
		
		int monitorClientInfoListSize = monitorClientInfoList.size();
		for (int i=0; i < monitorClientInfoListSize; i++) {
			builder.append(", MonitorClientInfo[");
			builder.append(i);
			builder.append("]={");
			builder.append(monitorClientInfoList.get(i).toString());
			builder.append("}");
		}
		
		builder.append("]");
		return builder.toString();
	}
	
	
}
