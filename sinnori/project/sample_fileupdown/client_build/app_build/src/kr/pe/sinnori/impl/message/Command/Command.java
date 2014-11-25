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
package kr.pe.sinnori.impl.message.Command;

import kr.pe.sinnori.common.message.AbstractMessage;
/**
 * Command 메시지
 * @author Won Jonghoon
 *
 */
public class Command extends AbstractMessage {
	private String commandString;

	public String getCommandString() {
		return commandString;
	}

	public void setCommandString(String commandString) {
		this.commandString = commandString;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("class Command[");
		builder.append("commandString=");
		builder.append(commandString);
		builder.append(", messageHeaderInfo=");
		builder.append(messageHeaderInfo.toString());
		builder.append("]");
		return builder.toString();
	}
}