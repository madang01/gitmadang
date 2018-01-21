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
package kr.pe.sinnori.impl.message.SelfExn;

import kr.pe.sinnori.common.message.AbstractMessage;
/**
 * SelfExn 메시지
 * @author Won Jonghoon
 *
 */
public class SelfExn extends AbstractMessage {
	private String errorPlace;
	private String errorGubun;
	private String errorMessageID;
	private String errorMessage;

	public String getErrorPlace() {
		return errorPlace;
	}

	public void setErrorPlace(String errorPlace) {
		this.errorPlace = errorPlace;
	}
	public String getErrorGubun() {
		return errorGubun;
	}

	public void setErrorGubun(String errorGubun) {
		this.errorGubun = errorGubun;
	}
	public String getErrorMessageID() {
		return errorMessageID;
	}

	public void setErrorMessageID(String errorMessageID) {
		this.errorMessageID = errorMessageID;
	}
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("class SelfExn[");
		builder.append("errorPlace=");
		builder.append(errorPlace);
		builder.append(", errorGubun=");
		builder.append(errorGubun);
		builder.append(", errorMessageID=");
		builder.append(errorMessageID);
		builder.append(", errorMessage=");
		builder.append(errorMessage);
		builder.append(", messageHeaderInfo=");
		builder.append(messageHeaderInfo.toString());
		builder.append("]");
		return builder.toString();
	}
}