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
package kr.pe.sinnori.impl.message.FileListResult;

import java.nio.charset.Charset;
import java.util.LinkedList;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.MessageEncoder;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * FileListResult 메시지 인코더
 * @author Jonghoon Won
 *
 */
public final class FileListResultEncoder extends MessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj)
			throws Exception {
		if (!(messageObj instanceof FileListResult)) {
			String errorMessage = String.format("메시지 객체 타입[%s]이 FileListResult 이(가) 아닙니다.", messageObj.getClass().getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
		}
		
		FileListResult fileListResult = (FileListResult) messageObj;
		encodeBody(fileListResult, singleItemEncoder, charsetOfProject, middleWriteObj);
	}

	/**
	 * <pre>
	 * FileListResult 입력 메시지의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장한다.
	 * </pre>
	 * @param fileListResult FileListResult 입력 메시지
	 * @param singleItemEncoder 단일항목 인코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleWriteObj 중간 다리 역활 쓰기 객체
	 * @throws Exception "입력/출력 메시지"의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장할때 에러 발생시 던지는 예외
	 */
	private void encodeBody(FileListResult fileListResult, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj) throws Exception {
		String fileListResultSingleItemPath = "FileListResult";
		LinkedList<String> singleItemPathStatck = new LinkedList<String>();
		singleItemPathStatck.push(fileListResultSingleItemPath);

		singleItemEncoder.putValueToMiddleWriteObj(fileListResultSingleItemPath, "requestDirectory"
					, 9 // itemTypeID
					, "si pascal string" // itemTypeName
					, fileListResult.getRequestDirectory() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(fileListResultSingleItemPath, "pathSeperator"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, fileListResult.getPathSeperator() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(fileListResultSingleItemPath, "taskResult"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, fileListResult.getTaskResult() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(fileListResultSingleItemPath, "resultMessage"
					, 9 // itemTypeID
					, "si pascal string" // itemTypeName
					, fileListResult.getResultMessage() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(fileListResultSingleItemPath, "cntOfDriver"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, fileListResult.getCntOfDriver() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);

		FileListResult.DriverList[] driverListList = fileListResult.getDriverListList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == driverListList) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != fileListResult.getCntOfDriver()) {
				String errorMessage = new StringBuilder(fileListResultSingleItemPath)
				.append(".")
				.append("driverListList")
				.append("is null").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (driverListList.length != fileListResult.getCntOfDriver()) {
				String errorMessage = new StringBuilder(fileListResultSingleItemPath)
				.append(".")
				.append("driverListList.length[")
				.append(driverListList.length)
				.append("] is not same to ")
				.append(fileListResultSingleItemPath)
				.append(".")
				.append("cntOfDriver[")
				.append(fileListResult.getCntOfDriver())
				.append("]").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}

			Object driverListMiddleWriteArray = singleItemEncoder.getArrayObjFromMiddleWriteObj(fileListResultSingleItemPath, "driverList", driverListList.length, middleWriteObj);
			for (int i=0; i < driverListList.length; i++) {
				singleItemPathStatck.push(new StringBuilder(singleItemPathStatck.getLast()).append(".").append("DriverList").append("[").append(i).append("]").toString());
				String driverListSingleItemPath = singleItemPathStatck.getLast();
				Object driverListMiddleWriteObj = singleItemEncoder.getMiddleWriteObjFromArrayObj(driverListSingleItemPath, driverListMiddleWriteArray, i);
				FileListResult.DriverList driverList = driverListList[i];
				singleItemEncoder.putValueToMiddleWriteObj(driverListSingleItemPath, "driverName"
							, 7 // itemTypeID
							, "ub pascal string" // itemTypeName
							, driverList.getDriverName() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, driverListMiddleWriteObj);
				singleItemPathStatck.pop();
			}
		}
		singleItemEncoder.putValueToMiddleWriteObj(fileListResultSingleItemPath, "cntOfFile"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, fileListResult.getCntOfFile() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);

		FileListResult.FileList[] fileListList = fileListResult.getFileListList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == fileListList) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != fileListResult.getCntOfFile()) {
				String errorMessage = new StringBuilder(fileListResultSingleItemPath)
				.append(".")
				.append("fileListList")
				.append("is null").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (fileListList.length != fileListResult.getCntOfFile()) {
				String errorMessage = new StringBuilder(fileListResultSingleItemPath)
				.append(".")
				.append("fileListList.length[")
				.append(fileListList.length)
				.append("] is not same to ")
				.append(fileListResultSingleItemPath)
				.append(".")
				.append("cntOfFile[")
				.append(fileListResult.getCntOfFile())
				.append("]").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}

			Object fileListMiddleWriteArray = singleItemEncoder.getArrayObjFromMiddleWriteObj(fileListResultSingleItemPath, "fileList", fileListList.length, middleWriteObj);
			for (int i=0; i < fileListList.length; i++) {
				singleItemPathStatck.push(new StringBuilder(singleItemPathStatck.getLast()).append(".").append("FileList").append("[").append(i).append("]").toString());
				String fileListSingleItemPath = singleItemPathStatck.getLast();
				Object fileListMiddleWriteObj = singleItemEncoder.getMiddleWriteObjFromArrayObj(fileListSingleItemPath, fileListMiddleWriteArray, i);
				FileListResult.FileList fileList = fileListList[i];
				singleItemEncoder.putValueToMiddleWriteObj(fileListSingleItemPath, "fileName"
							, 9 // itemTypeID
							, "si pascal string" // itemTypeName
							, fileList.getFileName() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, fileListMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(fileListSingleItemPath, "fileSize"
							, 6 // itemTypeID
							, "long" // itemTypeName
							, fileList.getFileSize() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, fileListMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(fileListSingleItemPath, "fileType"
							, 0 // itemTypeID
							, "byte" // itemTypeName
							, fileList.getFileType() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, fileListMiddleWriteObj);
				singleItemPathStatck.pop();
			}
		}
	}
}