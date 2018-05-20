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
package kr.pe.codda.common.protocol;

import java.nio.channels.SocketChannel;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.io.FreeSizeInputStream;


/**
 * 프로토콜 단에서 추출된 메시지 내용을 담는 클래스. 메시지 식별자, 메일 박스 식별자, 메일 식별자, 중간 다리 역활 읽기 객체를 담고 있다.
 * @author "Won Jonghoon"
 *
 */
public class WrapReadableMiddleObject {
	private InternalLogger log = InternalLoggerFactory.getInstance(WrapReadableMiddleObject.class);
	
	private String messageID = null;
	private int mailboxID;
	private int mailID;
	private Object readableMiddleObject = null;
	private SocketChannel fromSC = null;
	
	// private Date timestamp = new Date();
	
	// Intermediate object between stream and message
	public WrapReadableMiddleObject(SocketChannel fromSC, String messageID, int mailboxID, int mailID, Object readableMiddleObject) {
		this.fromSC = fromSC;
		this.messageID = messageID;
		this.mailboxID = mailboxID;
		this.mailID = mailID;
		this.readableMiddleObject = readableMiddleObject;
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

	public Object getReadableMiddleObject() {
		return readableMiddleObject;
	}
	
	/*public Date getTimestamp() {
		return timestamp;
	}*/
	public SocketChannel getFromSC() {
		return fromSC;
	}
	
	
	/**
	 * <pre>
	 * MiddleReadableObject 가 가진 자원 반환을 하는 장소는  2군데이다.
	 * 첫번째 장소는 메시지 추출 후 쓰임이 다해서 호출하는 AbstractMessageDecoder#decode 이며
	 * 두번째 장소는 2번 연속 호출해도 무방하기때문에 안전하게 자원 반환을 보장하기위한 Executor#run 이다.
	 * </pre>
	 */
	public void closeReadableMiddleObject() {
		if (readableMiddleObject instanceof FreeSizeInputStream) {
			FreeSizeInputStream messageInputStream = (FreeSizeInputStream)readableMiddleObject;
			try {
				messageInputStream.close();
				
				// FIXME!
				//log.info("messageID[{}], mailboxID[{}], mailID[{}] 메시지 바디 스트림 정상 닫힘", messageID, mailboxID, mailID);
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
		builder.append("ReceivedLetter [fromSC=");
		if (null != fromSC) {
			builder.append(fromSC.hashCode());
		} else {
			builder.append("null");
		}
		
		builder.append(", messageID=");
		builder.append(messageID);
		builder.append(", mailboxID=");
		builder.append(mailboxID);
		builder.append(", mailID=");
		builder.append(mailID);
		
		/*builder.append(", timestamp=");
		builder.append(timestamp.toString());*/
		builder.append("]");
		return builder.toString();
	}	
	
	public String toSimpleInformation() {
		StringBuilder builder = new StringBuilder();
		builder.append("fromSC=");
		if (null != fromSC) {
			builder.append(fromSC.hashCode());
		} else {
			builder.append("null");
		}
		builder.append("messageID=");
		builder.append(messageID);
		builder.append(", mailboxID=");
		builder.append(mailboxID);
		builder.append(", mailID=");
		builder.append(mailID);
		return builder.toString();
	}	
}
