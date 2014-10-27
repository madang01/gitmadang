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
package kr.pe.sinnori.common.protocol;

import kr.pe.sinnori.common.io.FreeSizeInputStream;
import kr.pe.sinnori.common.lib.CommonRootIF;


/**
 * 프로토콜 단에서 추출된 메시지 내용을 담는 클래스. 메시지 식별자, 메일 박스 식별자, 메일 식별자, 중간 다리 역활 읽기 객체를 담고 있다.
 * @author "Jonghoon Won"
 *
 */
public class ReceivedLetter implements CommonRootIF {
	private String messageID = null;
	private int mailboxID;
	private int mailID;
	private Object middleReadObj = null;
	
	public ReceivedLetter(String messageID, int mailboxID, int mailID, Object middleReadObj) {
		this.messageID = messageID;
		this.mailboxID = mailboxID;
		this.mailID = mailID;
		this.middleReadObj = middleReadObj;
	}

	public String getMessageID() {
		return messageID;
	}
	
	public int getMailboxID() {
		return mailboxID;
	}

	public int getMailID() {
		return mailID;
	}

	public Object getMiddleReadObj() {
		return middleReadObj;
	}
	
	
	public void closeMiddleReadObj() {
		if (middleReadObj instanceof FreeSizeInputStream) {
			FreeSizeInputStream bodyFreeSizeInputStream = (FreeSizeInputStream)middleReadObj;
			try {
				bodyFreeSizeInputStream.close();
				
				// FIXME!
				log.info("messageID[{}], mailboxID[{}], mailID[{}] 메시지 바디 스트림 정상 닫힘", messageID, mailboxID, mailID);
			} catch(Exception e) {
				String errorMessage = 
						String.format("messageID[%s], mailboxID[%d], mailID[%d] 메시지 바디 스트림 닫을때 알 수 없는 에러 발생", 
								messageID, mailboxID, mailID);
				log.warn(errorMessage, e);
				
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReceivedLetter [messageID=");
		builder.append(messageID);
		builder.append(", mailboxID=");
		builder.append(mailboxID);
		builder.append(", mailID=");
		builder.append(mailID);
		builder.append(", middleReadObj hashCode=");
		builder.append(middleReadObj.hashCode());
		builder.append("]");
		return builder.toString();
	}	
}
