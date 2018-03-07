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
package kr.pe.sinnori.common.message;


/**
 * 메시지 운영에 필요한 메시지 헤더 정보.
 * 
 * @author Won Jonghoon
 * 
 */
public class MessageHeaderInfo {
	/**
	 * 메일함 식별자, mailboxID : unsinged short 2bytes
	 */
	public int mailboxID;
	/**
	 * 메일 식별자, mailID : int 4bytes
	 */
	public int mailID;

	public String toString() {
		StringBuilder strBuffer = new StringBuilder();
		strBuffer.append("mailboxID=[");
		strBuffer.append(mailboxID);
		strBuffer.append("], mailID=[");
		strBuffer.append(mailID);
		strBuffer.append("]");
		return strBuffer.toString();
	}
	
	public boolean equals(Object o) {
		if (null == o) {
			return false;
		}
		if (! (o instanceof MessageHeaderInfo)) {
			return false;
		}
		
		MessageHeaderInfo dst = (MessageHeaderInfo)o;
		
		if (this.mailboxID != dst.mailboxID) {
			return false;
		}
		
		if (this.mailID != dst.mailID) {
			return false;
		}
		
		return true;
	}
}
