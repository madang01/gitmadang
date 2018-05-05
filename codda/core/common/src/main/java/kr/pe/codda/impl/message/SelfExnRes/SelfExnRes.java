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
package kr.pe.codda.impl.message.SelfExnRes;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * SelfExnRes 메시지
 * @author Won Jonghoon
 *
 */
public class SelfExnRes extends AbstractMessage {
	private kr.pe.codda.common.type.SelfExn.ErrorPlace errorPlace;
	private kr.pe.codda.common.type.SelfExn.ErrorType errorType;
	private String errorMessageID;
	private String errorReason;

	public kr.pe.codda.common.type.SelfExn.ErrorPlace getErrorPlace() {
		return errorPlace;
	}

	public void setErrorPlace(kr.pe.codda.common.type.SelfExn.ErrorPlace errorPlace) {
		this.errorPlace = errorPlace;
	}
	public kr.pe.codda.common.type.SelfExn.ErrorType getErrorType() {
		return errorType;
	}

	public void setErrorType(kr.pe.codda.common.type.SelfExn.ErrorType errorType) {
		this.errorType = errorType;
	}
	public String getErrorMessageID() {
		return errorMessageID;
	}

	public void setErrorMessageID(String errorMessageID) {
		this.errorMessageID = errorMessageID;
	}
	public String getErrorReason() {
		return errorReason;
	}

	public void setErrorReason(String errorReason) {
		this.errorReason = errorReason;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("selfExnRes[");
		builder.append("errorPlace=");
		builder.append(errorPlace);
		builder.append(", errorType=");
		builder.append(errorType);
		builder.append(", errorMessageID=");
		builder.append(errorMessageID);
		builder.append(", errorReason=");
		builder.append(errorReason);
		builder.append("]");
		return builder.toString();
	}
}