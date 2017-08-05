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
package kr.pe.sinnori.impl.message.FileListRes;

import java.nio.charset.Charset;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;

/**
 * FileListRes 메시지 디코더
 * @author Won Jonghoon
 *
 */
public final class FileListResDecoder extends AbstractMessageDecoder {

	/**
	 * <pre>
	 *  "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 FileListRes 메시지를 반환한다.
	 * </pre>
	 * @param singleItemDecoder 단일항목 디코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleReadObj 중간 다리 역활 읽기 객체
	 * @return "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 FileListRes 메시지
	 * @throws OutOfMemoryError 메모리 확보 실패시 던지는 예외
	 * @throws BodyFormatException 바디 디코딩 실패시 던지는 예외
	 */
	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Charset charsetOfProject, Object  middleReadObj) throws OutOfMemoryError, BodyFormatException {
		FileListRes fileListRes = new FileListRes();
		String sigleItemPath0 = "FileListRes";

		fileListRes.setRequestPathName((String)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "requestPathName" // itemName
		, 9 // itemTypeID
		, "si pascal string" // itemTypeName
		, -1 // itemSize
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		fileListRes.setPathSeperator((String)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "pathSeperator" // itemName
		, 7 // itemTypeID
		, "ub pascal string" // itemTypeName
		, -1 // itemSize
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		fileListRes.setIsSuccess((String)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "isSuccess" // itemName
		, 7 // itemTypeID
		, "ub pascal string" // itemTypeName
		, -1 // itemSize
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		fileListRes.setResultMessage((String)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "resultMessage" // itemName
		, 9 // itemTypeID
		, "si pascal string" // itemTypeName
		, -1 // itemSize
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		fileListRes.setCntOfDriver((Integer)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "cntOfDriver" // itemName
		, 4 // itemTypeID
		, "integer" // itemTypeName
		, -1 // itemSize
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		int driverListSize = fileListRes.getCntOfDriver();
		Object driverMiddleReadArray = singleItemDecoder.getArrayObjFromMiddleReadObj(sigleItemPath0, "driver", driverListSize, middleReadObj);
		java.util.List<FileListRes.Driver> driverList = new java.util.ArrayList<FileListRes.Driver>();
		for (int i=0; i < driverListSize; i++) {
			String sigleItemPath1 = new StringBuilder(sigleItemPath0).append(".").append("Driver[").append(i).append("]").toString();
			Object driverMiddleReadObj = singleItemDecoder.getMiddleReadObjFromArrayObj(sigleItemPath1, driverMiddleReadArray, i);
			FileListRes.Driver driver = new FileListRes.Driver();
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
		fileListRes.setDriverList(driverList);

		fileListRes.setCntOfChildFile((Integer)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "cntOfChildFile" // itemName
		, 4 // itemTypeID
		, "integer" // itemTypeName
		, -1 // itemSize
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		int childFileListSize = fileListRes.getCntOfChildFile();
		Object childFileMiddleReadArray = singleItemDecoder.getArrayObjFromMiddleReadObj(sigleItemPath0, "childFile", childFileListSize, middleReadObj);
		java.util.List<FileListRes.ChildFile> childFileList = new java.util.ArrayList<FileListRes.ChildFile>();
		for (int i=0; i < childFileListSize; i++) {
			String sigleItemPath1 = new StringBuilder(sigleItemPath0).append(".").append("ChildFile[").append(i).append("]").toString();
			Object childFileMiddleReadObj = singleItemDecoder.getMiddleReadObjFromArrayObj(sigleItemPath1, childFileMiddleReadArray, i);
			FileListRes.ChildFile childFile = new FileListRes.ChildFile();
			childFileList.add(childFile);

			childFile.setFileName((String)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "fileName" // itemName
			, 9 // itemTypeID
			, "si pascal string" // itemTypeName
			, -1 // itemSize
			, null // itemCharset,
			, charsetOfProject
			, childFileMiddleReadObj));

			childFile.setFileSize((Long)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "fileSize" // itemName
			, 6 // itemTypeID
			, "long" // itemTypeName
			, -1 // itemSize
			, null // itemCharset,
			, charsetOfProject
			, childFileMiddleReadObj));

			childFile.setFileType((Byte)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "fileType" // itemName
			, 0 // itemTypeID
			, "byte" // itemTypeName
			, -1 // itemSize
			, null // itemCharset,
			, charsetOfProject
			, childFileMiddleReadObj));
		}
		fileListRes.setChildFileList(childFileList);
		return fileListRes;
	}
}