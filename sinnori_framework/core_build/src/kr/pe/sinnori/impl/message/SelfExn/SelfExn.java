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

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.ServerExcecutorException;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.AbstractMessage;

/**
 * SelfExn 메시지, 시스템 메시지로 필수 메시지.
 * @author "Jonghoon Won"
 *
 */
public final class SelfExn extends AbstractMessage {
	String errorWhere="";
	String errorGubun="";
	String errorMessageID="";
	String errorMessage="";
	
	public String getErrorWhere() {
		// if (null == errorWhere) errorWhere = "";
		return errorWhere;
	}
	
	public String getErrorGubun() {
		return errorGubun;
	}
	
	public String getErrorMessageID() {
		return errorMessageID;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorWhere(String errorWhere)  {
		if (null == errorWhere) {
			String errorMessage = "paramter errorWhere is null";
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (!errorWhere.equals("S") && !errorWhere.equals("C")) {
			String errorMessage = "paramter errorWhere value["+errorWhere+"] is a unknown value";
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		this.errorWhere = errorWhere;
	}
	
	public void setErrorMessageID(String errorMessageID) {
		if (null == errorMessageID) {
			String errorMessage = "paramter errorMessageID is null";
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		this.errorMessageID = errorMessageID;
	}
	
	public void setErrorGubun(String errorGubun) {
		if (null == errorGubun) throw new IllegalArgumentException("paramter errorGubun is null");
		this.errorGubun = errorGubun;
	}

	public void setErrorMessage(String errorMessage) {
		if (null == errorMessage) throw new IllegalArgumentException("paramter errorMessage is null");
		this.errorMessage = errorMessage;
	}
	
	public void setErrorGubun(Class<?> wantedExceptionClass) {
		if (null == wantedExceptionClass) throw new IllegalArgumentException("paramter wantedExceptionClass is null");
		if (wantedExceptionClass.equals(BodyFormatException.class)) {
			errorGubun = "B";
		} else if (wantedExceptionClass.equals(DynamicClassCallException.class)) {
			errorGubun = "D";
		} else if (wantedExceptionClass.equals(NoMoreDataPacketBufferException.class)) {
			errorGubun = "N";
		} else if (wantedExceptionClass.equals(ServerExcecutorException.class)) {
			errorGubun = "S";
		} else if (wantedExceptionClass.equals(NotLoginException.class)) {
			errorGubun = "A";
		} else {
			new IllegalArgumentException("paramter wantedExceptionClass["+wantedExceptionClass.getCanonicalName()+"] 는 알수 없는 에러");
		}
	}
	
	/*public void setError(String errorWhere, String errorMessageID, BodyFormatException e) throws IllegalArgumentException {
		setErrorWhere(errorWhere);
		setErrorMessageID(errorMessageID);
		
		errorGubun = "B";
		errorMessage = e.getMessage();
	}
	
	public void setError(String errorWhere, String errorMessageID, DynamicClassCallException e) {
		setErrorWhere(errorWhere);
		setErrorMessageID(errorMessageID);
		
		errorGubun = "D";
		errorMessage = e.getMessage();
	}
	
	public void setError(String errorWhere, String errorMessageID, NoMoreDataPacketBufferException e) {
		setErrorWhere(errorWhere);
		setErrorMessageID(errorMessageID);
		errorGubun = "N";
		errorMessage = e.getMessage();
	}
	
	public void setError(String errorWhere, String errorMessageID, ServerExcecutorException e) {
		setErrorWhere(errorWhere);
		setErrorMessageID(errorMessageID);
		errorGubun = "S";
		errorMessage = e.getMessage();
	}
	
	public void setError(String errorWhere, String errorMessageID, NotLoginException e) {
		setErrorWhere(errorWhere);
		setErrorMessageID(errorMessageID);
		
		errorGubun = "A";
		errorMessage = e.getMessage();
	}*/
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SelfExn [errorWhere=");
		builder.append(errorWhere);
		builder.append(", errorGubun=");
		builder.append(errorGubun);
		builder.append(", errorMessageID=");
		builder.append(errorMessageID);
		builder.append(", errorMessage=");
		builder.append(errorMessage);
		builder.append("]");
		return builder.toString();
	}
	
	public String getReport() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("발생장소 : ");
		
		if (errorWhere.equals("S")) {
			builder.append("서버");
		} else if (errorWhere.equals("C")) {
			builder.append("클라이언트");
		} else {
			builder.append("알 수 없는곳[");
			builder.append(errorWhere);
			builder.append("]");
		}
		
		builder.append(CommonStaticFinalVars.NEWLINE);
		builder.append("에러구분 : ");
		
		if (errorGubun.equals("B")) {
			builder.append("BodyFormatException");
		} else if (errorGubun.equals("D")) {
			builder.append("DynamicClassCallException");
		} else if (errorGubun.equals("N")) {
			builder.append("NoMoreDataPacketBufferException");
		} else if (errorGubun.equals("S")) {
			builder.append("ServerExcecutorException");
		} else if (errorGubun.equals("A")) {
			builder.append("NotLoginException");
		} else {
			builder.append("알수 없는 에러[");
			builder.append(errorGubun);
			builder.append("]");
		}
		
		builder.append(CommonStaticFinalVars.NEWLINE);
		builder.append("에러가 발생한 메시지 식별자 : ");
		builder.append(errorMessageID);
		
		builder.append(CommonStaticFinalVars.NEWLINE);
		builder.append("에러 발생 상세 사유 : ");
		builder.append(errorMessage);
		
		return builder.toString();
	}
	
	public void toException() throws BodyFormatException, DynamicClassCallException, 
		NoMoreDataPacketBufferException, ServerExcecutorException,
		NotLoginException {
		
		String errorWhereMsg = null;
		if (errorWhere.equals("S")) {
			errorWhereMsg = "서버";
		} else if (errorWhere.equals("C")) {
			errorWhereMsg = "클라이언트";
		} else {
			String errorMessage = "알수 없는 에러 장소" + getReport();
			throw new BodyFormatException(errorMessage);
		}
		
		if (errorGubun.equals("B")) {
			String errorMessageMsg = errorWhereMsg+"::메시지["+errorMessageID+"]::"+errorMessage;
			throw new BodyFormatException(errorMessageMsg);
		} else if (errorGubun.equals("D")) {
			String errorMessageMsg = errorWhereMsg+"::메시지["+errorMessageID+"]::"+errorMessage;
			throw new DynamicClassCallException(errorMessageMsg);
		} else if (errorGubun.equals("N")) {
			String errorMessageMsg = errorWhereMsg+"::메시지["+errorMessageID+"]::"+errorMessage;
			throw new NoMoreDataPacketBufferException(errorMessageMsg);
		} else if (errorGubun.equals("S")) {
			String errorMessageMsg = errorWhereMsg+"::메시지["+errorMessageID+"]::"+errorMessage;
			throw new ServerExcecutorException(errorMessageMsg);
		} else if (errorGubun.equals("A")) {
			String errorMessageMsg = errorWhereMsg+"::메시지["+errorMessageID+"]::"+errorMessage;
			throw new NotLoginException(errorMessageMsg);
		} else {
			String errorMessageMsg = errorWhereMsg+"::메시지["+errorMessageID+"]::알수없는에러구분["+errorGubun+"]::"+errorMessage;
			throw new BodyFormatException(errorMessageMsg);
		}
	}
}
