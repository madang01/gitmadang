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
package kr.pe.sinnori.impl.message.FileListReq;

import kr.pe.sinnori.common.message.AbstractMessage;
/**
 * FileListReq 메시지
 * @author Won Jonghoon
 *
 */
public class FileListReq extends AbstractMessage {
	private String requestPathName;

	public String getRequestPathName() {
		return requestPathName;
	}

	public void setRequestPathName(String requestPathName) {
		this.requestPathName = requestPathName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("class FileListReq[");
		builder.append("requestPathName=");
		builder.append(requestPathName);
		builder.append(", messageHeaderInfo=");
		builder.append(messageHeaderInfo.toString());
		builder.append("]");
		return builder.toString();
	}
}