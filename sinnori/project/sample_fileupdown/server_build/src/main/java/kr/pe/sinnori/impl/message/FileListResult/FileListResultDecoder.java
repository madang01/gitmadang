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
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;

/**
 * FileListResult 메시지 디코더
 * @author Won Jonghoon
 *
 */
public final class FileListResultDecoder extends AbstractMessageDecoder {

	/**
	 * <pre>
	 *  "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 FileListResult 메시지를 반환한다.
	 * </pre>
	 * @param singleItemDecoder 단일항목 디코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleReadObj 중간 다리 역활 읽기 객체
	 * @return "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 FileListResult 메시지
	 * @throws OutOfMemoryError 메모리 확보 실패시 던지는 예외
	 * @throws BodyFormatException 바디 디코딩 실패시 던지는 예외
	 */
	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Charset charsetOfProject, Object  middleReadObj) throws OutOfMemoryError, BodyFormatException {
		FileListResult fileListResult = new FileListResult();
		String sigleItemPath0 = "FileListResult";

		fileListResult.setRequestDirectory((String)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "requestDirectory" // itemName
		, 9 // itemTypeID
		, "si pascal string" // itemTypeName
		, -1 // itemSize
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		fileListResult.setPathSeperator((String)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "pathSeperator" // itemName
		, 7 // itemTypeID
		, "ub pascal string" // itemTypeName
		, -1 // itemSize
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		fileListResult.setTaskResult((String)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "taskResult" // itemName
		, 7 // itemTypeID
		, "ub pascal string" // itemTypeName
		, -1 // itemSize
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		fileListResult.setResultMessage((String)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "resultMessage" // itemName
		, 9 // itemTypeID
		, "si pascal string" // itemTypeName
		, -1 // itemSize
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		fileListResult.setCntOfDriver((Integer)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "cntOfDriver" // itemName
		, 4 // itemTypeID
		, "integer" // itemTypeName
		, -1 // itemSize
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		int driverListSize = fileListResult.getCntOfDriver();
		Object driverMiddleReadArray = singleItemDecoder.getArrayObjFromMiddleReadObj(sigleItemPath0, "driver", driverListSize, middleReadObj);
		java.util.List<FileListResult.Driver> driverList = new java.util.ArrayList<FileListResult.Driver>();
		for (int i=0; i < driverListSize; i++) {
			String sigleItemPath1 = new StringBuilder(sigleItemPath0).append(".").append("Driver[").append(i).append("]").toString();
			Object driverMiddleReadObj = singleItemDecoder.getMiddleReadObjFromArrayObj(sigleItemPath1, driverMiddleReadArray, i);
			FileListResult.Driver driver = new FileListResult.Driver();
			driverList.add(driver);

			driver.setDriverName((String)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "driverName" // itemName
			, 7 // itemTypeID
			, "ub pascal string" // itemTypeName
			, -1 // itemSize
			, null // itemCharset,
			, charsetOfProject
			, driverMiddleReadObj));
		}
		fileListResult.setDriverList(driverList);

		fileListResult.setCntOfFile((Integer)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "cntOfFile" // itemName
		, 4 // itemTypeID
		, "integer" // itemTypeName
		, -1 // itemSize
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		int fileListSize = fileListResult.getCntOfFile();
		Object fileMiddleReadArray = singleItemDecoder.getArrayObjFromMiddleReadObj(sigleItemPath0, "file", fileListSize, middleReadObj);
		java.util.List<FileListResult.File> fileList = new java.util.ArrayList<FileListResult.File>();
		for (int i=0; i < fileListSize; i++) {
			String sigleItemPath1 = new StringBuilder(sigleItemPath0).append(".").append("File[").append(i).append("]").toString();
			Object fileMiddleReadObj = singleItemDecoder.getMiddleReadObjFromArrayObj(sigleItemPath1, fileMiddleReadArray, i);
			FileListResult.File file = new FileListResult.File();
			fileList.add(file);

			file.setFileName((String)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "fileName" // itemName
			, 9 // itemTypeID
			, "si pascal string" // itemTypeName
			, -1 // itemSize
			, null // itemCharset,
			, charsetOfProject
			, fileMiddleReadObj));

			file.setFileSize((Long)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "fileSize" // itemName
			, 6 // itemTypeID
			, "long" // itemTypeName
			, -1 // itemSize
			, null // itemCharset,
			, charsetOfProject
			, fileMiddleReadObj));

			file.setFileType((Byte)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "fileType" // itemName
			, 0 // itemTypeID
			, "byte" // itemTypeName
			, -1 // itemSize
			, null // itemCharset,
			, charsetOfProject
			, fileMiddleReadObj));
		}
		fileListResult.setFileList(fileList);
		return fileListResult;
	}
}