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

package kr.pe.codda.impl.message.IDPwdSearchRes;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * IDPwdSearchRes message
 * @author Won Jonghoon
 *
 */
public class IDPwdSearchRes extends AbstractMessage {
	private String secretAuthenticationValue;
	private java.sql.Timestamp registeredDate;

	public String getSecretAuthenticationValue() {
		return secretAuthenticationValue;
	}

	public void setSecretAuthenticationValue(String secretAuthenticationValue) {
		this.secretAuthenticationValue = secretAuthenticationValue;
	}
	public java.sql.Timestamp getRegisteredDate() {
		return registeredDate;
	}

	public void setRegisteredDate(java.sql.Timestamp registeredDate) {
		this.registeredDate = registeredDate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("iDPwdSearchRes[");
		builder.append("secretAuthenticationValue=");
		builder.append(secretAuthenticationValue);
		builder.append(", registeredDate=");
		builder.append(registeredDate);
		builder.append("]");
		return builder.toString();
	}
}