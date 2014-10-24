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
 * @author Won Jonghoon
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

		java.util.List<FileListResult.Driver> driverList = fileListResult.getDriverList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == driverList) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != fileListResult.getCntOfDriver()) {
				String errorMessage = new StringBuilder("간접 참조 회수[")
				.append(fileListResult.getCntOfDriver())
				.append("] is not zero but ")
				.append(fileListResultSingleItemPath)
				.append(".")
				.append("driverList")
				.append("is null").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int driverListSize = driverList.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (driverListSize != fileListResult.getCntOfDriver()) {
				String errorMessage = new StringBuilder(fileListResultSingleItemPath)
				.append(".")
				.append("driverList.length[")
				.append(driverListSize)
				.append("] is not same to ")
				.append(fileListResultSingleItemPath)
				.append(".")
				.append("cntOfDriver[")
				.append(fileListResult.getCntOfDriver())
				.append("]").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}

			Object driverMiddleWriteArray = singleItemEncoder.getArrayObjFromMiddleWriteObj(fileListResultSingleItemPath, "driver", driverListSize, middleWriteObj);
			for (int i=0; i < driverListSize; i++) {
				singleItemPathStatck.push(new StringBuilder(singleItemPathStatck.getLast()).append(".").append("Driver").append("[").append(i).append("]").toString());
				String driverSingleItemPath = singleItemPathStatck.getLast();
				Object driverMiddleWriteObj = singleItemEncoder.getMiddleWriteObjFromArrayObj(driverSingleItemPath, driverMiddleWriteArray, i);
				FileListResult.Driver driver = driverList.get(i);
				singleItemEncoder.putValueToMiddleWriteObj(driverSingleItemPath, "driverName"
							, 7 // itemTypeID
							, "ub pascal string" // itemTypeName
							, driver.getDriverName() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, driverMiddleWriteObj);
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

		java.util.List<FileListResult.File> fileList = fileListResult.getFileList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == fileList) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != fileListResult.getCntOfFile()) {
				String errorMessage = new StringBuilder("간접 참조 회수[")
				.append(fileListResult.getCntOfFile())
				.append("] is not zero but ")
				.append(fileListResultSingleItemPath)
				.append(".")
				.append("fileList")
				.append("is null").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int fileListSize = fileList.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (fileListSize != fileListResult.getCntOfFile()) {
				String errorMessage = new StringBuilder(fileListResultSingleItemPath)
				.append(".")
				.append("fileList.length[")
				.append(fileListSize)
				.append("] is not same to ")
				.append(fileListResultSingleItemPath)
				.append(".")
				.append("cntOfFile[")
				.append(fileListResult.getCntOfFile())
				.append("]").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}

			Object fileMiddleWriteArray = singleItemEncoder.getArrayObjFromMiddleWriteObj(fileListResultSingleItemPath, "file", fileListSize, middleWriteObj);
			for (int i=0; i < fileListSize; i++) {
				singleItemPathStatck.push(new StringBuilder(singleItemPathStatck.getLast()).append(".").append("File").append("[").append(i).append("]").toString());
				String fileSingleItemPath = singleItemPathStatck.getLast();
				Object fileMiddleWriteObj = singleItemEncoder.getMiddleWriteObjFromArrayObj(fileSingleItemPath, fileMiddleWriteArray, i);
				FileListResult.File file = fileList.get(i);
				singleItemEncoder.putValueToMiddleWriteObj(fileSingleItemPath, "fileName"
							, 9 // itemTypeID
							, "si pascal string" // itemTypeName
							, file.getFileName() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, fileMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(fileSingleItemPath, "fileSize"
							, 6 // itemTypeID
							, "long" // itemTypeName
							, file.getFileSize() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, fileMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(fileSingleItemPath, "fileType"
							, 0 // itemTypeID
							, "byte" // itemTypeName
							, file.getFileType() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, fileMiddleWriteObj);
				singleItemPathStatck.pop();
			}
		}
	}
}