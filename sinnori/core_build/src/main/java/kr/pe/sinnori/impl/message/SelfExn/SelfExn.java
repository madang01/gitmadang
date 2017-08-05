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

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.message.AbstractMessage;

/**
 * SelfExn 메시지, 시스템 메시지로 필수 메시지.
 * @author "Won Jonghoon"
 *
 */
public final class SelfExn extends AbstractMessage {
	String errorPlace="";
	String errorGubun="";
	String errorMessageID="";
	String errorMessage="";
	
	public String getErrorPlace() {
		// if (null == errorWhere) errorWhere = "";
		return errorPlace;
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
	
	public void setErrorPlace(String errorPlace)  {
		if (null == errorPlace) {
			String errorMessage = "paramter errorPlace is null";
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (!errorPlace.equals("S") && !errorPlace.equals("C")) {
			String errorMessage = "paramter errorPlace value["+errorPlace+"] is a unknown value";
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		this.errorPlace = errorPlace;
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
		} else if (wantedExceptionClass.equals(ServerTaskException.class)) {
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
		builder.append(errorPlace);
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
		
		builder.append("where : ");
		
		if (errorPlace.equals("S")) {
			builder.append("server");
		} else if (errorPlace.equals("C")) {
			builder.append("client");
		} else {
			builder.append("unknown the error place[");
			builder.append(errorPlace);
			builder.append("]");
		}
		
		builder.append(CommonStaticFinalVars.NEWLINE);
		builder.append("type : ");
		
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
			builder.append("unknown error type[");
			builder.append(errorGubun);
			builder.append("]");
		}
		
		builder.append(CommonStaticFinalVars.NEWLINE);
		builder.append("message id : ");
		builder.append(errorMessageID);
		
		builder.append(CommonStaticFinalVars.NEWLINE);
		builder.append("error message : ");
		builder.append(errorMessage);
		
		return builder.toString();
	}
	
	public void throwException() throws BodyFormatException, DynamicClassCallException, 
		NoMoreDataPacketBufferException, ServerTaskException,
		NotLoginException {
		
		String errorWhereMsg = null;
		if (errorPlace.equals("S")) {
			errorWhereMsg = "server";
		} else if (errorPlace.equals("C")) {
			errorWhereMsg = "client";
		} else {
			String errorMessage = "the unknown error place, " + toString();
			throw new BodyFormatException(errorMessage);
		}
		
		if (errorGubun.equals("B")) {
			String errorMessageMsg = errorWhereMsg+"::message id["+errorMessageID+"]::"+errorMessage;
			throw new BodyFormatException(errorMessageMsg);
		} else if (errorGubun.equals("D")) {
			String errorMessageMsg = errorWhereMsg+"::message id["+errorMessageID+"]::"+errorMessage;
			throw new DynamicClassCallException(errorMessageMsg);
		} else if (errorGubun.equals("N")) {
			String errorMessageMsg = errorWhereMsg+"::message id["+errorMessageID+"]::"+errorMessage;
			throw new NoMoreDataPacketBufferException(errorMessageMsg);
		} else if (errorGubun.equals("S")) {
			String errorMessageMsg = errorWhereMsg+"::message id["+errorMessageID+"]::"+errorMessage;
			throw new ServerTaskException(errorMessageMsg);
		} else if (errorGubun.equals("A")) {
			String errorMessageMsg = errorWhereMsg+"::message id["+errorMessageID+"]::"+errorMessage;
			throw new NotLoginException(errorMessageMsg);
		} else {
			String errorMessageMsg = "the unknown error gubun, " + toString();
			throw new BodyFormatException(errorMessageMsg);
		}
	}
}
