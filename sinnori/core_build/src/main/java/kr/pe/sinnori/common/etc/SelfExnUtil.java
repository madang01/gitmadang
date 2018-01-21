package kr.pe.sinnori.common.etc;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;

public abstract class SelfExnUtil {
	
	public static String getSelfExnErrorGubun(Class<?> wantedExceptionClass) {
		if (null == wantedExceptionClass) throw new IllegalArgumentException("paramter wantedExceptionClass is null");
		
		String errorGubun = null;
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
		
		// sourceSelfExn.setErrorGubun(errorGubun);
		return errorGubun;
	}
	
	public static String getSelfExnReport(SelfExn selfExn) {
		String errorPlace = selfExn.getErrorPlace();
		String errorGubun = selfExn.getErrorGubun();
		String errorMessageID = selfExn.getErrorMessageID();
		String errorMessage = selfExn.getErrorMessage();
		
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
	
	public static void throwSelfExnException(SelfExn selfExn) throws BodyFormatException, DynamicClassCallException, 
		NoMoreDataPacketBufferException, ServerTaskException,
		NotLoginException {
		
		String errorPlace = selfExn.getErrorPlace();
		String errorGubun = selfExn.getErrorGubun();
		String errorMessageID = selfExn.getErrorMessageID();
		String errorMessage = selfExn.getErrorMessage();
		
		String errorWhereMsg = null;
		if (errorPlace.equals("S")) {
			errorWhereMsg = "server";
		} else if (errorPlace.equals("C")) {
			errorWhereMsg = "client";
		} else {
			String finalErrorMessage = "the unknown error place, " + selfExn.toString();
			throw new BodyFormatException(finalErrorMessage);
		}
		
		if (errorGubun.equals("B")) {
			String finalErrorMessage = errorWhereMsg+"::message id["+errorMessageID+"]::"+errorMessage;
			throw new BodyFormatException(finalErrorMessage);
		} else if (errorGubun.equals("D")) {
			String finalErrorMessage = errorWhereMsg+"::message id["+errorMessageID+"]::"+errorMessage;
			throw new DynamicClassCallException(finalErrorMessage);
		} else if (errorGubun.equals("N")) {
			String finalErrorMessage = errorWhereMsg+"::message id["+errorMessageID+"]::"+errorMessage;
			throw new NoMoreDataPacketBufferException(finalErrorMessage);
		} else if (errorGubun.equals("S")) {
			String finalErrorMessage = errorWhereMsg+"::message id["+errorMessageID+"]::"+errorMessage;
			throw new ServerTaskException(finalErrorMessage);
		} else if (errorGubun.equals("A")) {
			String finalErrorMessage = errorWhereMsg+"::message id["+errorMessageID+"]::"+errorMessage;
			throw new NotLoginException(finalErrorMessage);
		} else {
			String finalErrorMessage = "the unknown error gubun, " + selfExn.toString();
			throw new BodyFormatException(finalErrorMessage);
		}
	}
}
