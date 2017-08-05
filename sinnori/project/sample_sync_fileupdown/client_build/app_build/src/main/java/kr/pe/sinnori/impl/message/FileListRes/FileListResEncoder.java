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
import java.util.LinkedList;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * FileListRes 메시지 인코더
 * @author Won Jonghoon
 *
 */
public final class FileListResEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj)
			throws Exception {
		if (!(messageObj instanceof FileListRes)) {
			String errorMessage = String.format("메시지 객체 타입[%s]이 FileListRes 이(가) 아닙니다.", messageObj.getClass().getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
		}
		
		FileListRes fileListRes = (FileListRes) messageObj;
		encodeBody(fileListRes, singleItemEncoder, charsetOfProject, middleWriteObj);
	}

	/**
	 * <pre>
	 * FileListRes 입력 메시지의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장한다.
	 * </pre>
	 * @param fileListRes FileListRes 입력 메시지
	 * @param singleItemEncoder 단일항목 인코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleWriteObj 중간 다리 역활 쓰기 객체
	 * @throws Exception "입력/출력 메시지"의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장할때 에러 발생시 던지는 예외
	 */
	private void encodeBody(FileListRes fileListRes, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj) throws Exception {
		String fileListResSingleItemPath = "FileListRes";
		LinkedList<String> singleItemPathStatck = new LinkedList<String>();
		singleItemPathStatck.push(fileListResSingleItemPath);

		singleItemEncoder.putValueToMiddleWriteObj(fileListResSingleItemPath, "requestPathName"
					, 9 // itemTypeID
					, "si pascal string" // itemTypeName
					, fileListRes.getRequestPathName() // itemValue
					, -1 // itemSize
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(fileListResSingleItemPath, "pathSeperator"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, fileListRes.getPathSeperator() // itemValue
					, -1 // itemSize
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(fileListResSingleItemPath, "isSuccess"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, fileListRes.getIsSuccess() // itemValue
					, -1 // itemSize
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(fileListResSingleItemPath, "resultMessage"
					, 9 // itemTypeID
					, "si pascal string" // itemTypeName
					, fileListRes.getResultMessage() // itemValue
					, -1 // itemSize
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(fileListResSingleItemPath, "cntOfDriver"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, fileListRes.getCntOfDriver() // itemValue
					, -1 // itemSize
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);

		java.util.List<FileListRes.Driver> driverList = fileListRes.getDriverList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == driverList) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != fileListRes.getCntOfDriver()) {
				String errorMessage = new StringBuilder("간접 참조 회수[")
				.append(fileListRes.getCntOfDriver())
				.append("] is not zero but ")
				.append(fileListResSingleItemPath)
				.append(".")
				.append("driverList")
				.append("is null").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int driverListSize = driverList.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (driverListSize != fileListRes.getCntOfDriver()) {
				String errorMessage = new StringBuilder(fileListResSingleItemPath)
				.append(".")
				.append("driverList.length[")
				.append(driverListSize)
				.append("] is not same to ")
				.append(fileListResSingleItemPath)
				.append(".")
				.append("cntOfDriver[")
				.append(fileListRes.getCntOfDriver())
				.append("]").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}

			Object driverMiddleWriteArray = singleItemEncoder.getArrayObjFromMiddleWriteObj(fileListResSingleItemPath, "driver", driverListSize, middleWriteObj);
			for (int i=0; i < driverListSize; i++) {
				singleItemPathStatck.push(new StringBuilder(singleItemPathStatck.getLast()).append(".").append("Driver").append("[").append(i).append("]").toString());
				String driverSingleItemPath = singleItemPathStatck.getLast();
				Object driverMiddleWriteObj = singleItemEncoder.getMiddleWriteObjFromArrayObj(driverSingleItemPath, driverMiddleWriteArray, i);
				FileListRes.Driver driver = driverList.get(i);
				singleItemEncoder.putValueToMiddleWriteObj(driverSingleItemPath, "driverName"
							, 7 // itemTypeID
							, "ub pascal string" // itemTypeName
							, driver.getDriverName() // itemValue
							, -1 // itemSize
							, null // itemCharset,
							, charsetOfProject
							, driverMiddleWriteObj);
				singleItemPathStatck.pop();
			}
		}
		singleItemEncoder.putValueToMiddleWriteObj(fileListResSingleItemPath, "cntOfChildFile"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, fileListRes.getCntOfChildFile() // itemValue
					, -1 // itemSize
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);

		java.util.List<FileListRes.ChildFile> childFileList = fileListRes.getChildFileList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == childFileList) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != fileListRes.getCntOfChildFile()) {
				String errorMessage = new StringBuilder("간접 참조 회수[")
				.append(fileListRes.getCntOfChildFile())
				.append("] is not zero but ")
				.append(fileListResSingleItemPath)
				.append(".")
				.append("childFileList")
				.append("is null").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int childFileListSize = childFileList.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (childFileListSize != fileListRes.getCntOfChildFile()) {
				String errorMessage = new StringBuilder(fileListResSingleItemPath)
				.append(".")
				.append("childFileList.length[")
				.append(childFileListSize)
				.append("] is not same to ")
				.append(fileListResSingleItemPath)
				.append(".")
				.append("cntOfChildFile[")
				.append(fileListRes.getCntOfChildFile())
				.append("]").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}

			Object childFileMiddleWriteArray = singleItemEncoder.getArrayObjFromMiddleWriteObj(fileListResSingleItemPath, "childFile", childFileListSize, middleWriteObj);
			for (int i=0; i < childFileListSize; i++) {
				singleItemPathStatck.push(new StringBuilder(singleItemPathStatck.getLast()).append(".").append("ChildFile").append("[").append(i).append("]").toString());
				String childFileSingleItemPath = singleItemPathStatck.getLast();
				Object childFileMiddleWriteObj = singleItemEncoder.getMiddleWriteObjFromArrayObj(childFileSingleItemPath, childFileMiddleWriteArray, i);
				FileListRes.ChildFile childFile = childFileList.get(i);
				singleItemEncoder.putValueToMiddleWriteObj(childFileSingleItemPath, "fileName"
							, 9 // itemTypeID
							, "si pascal string" // itemTypeName
							, childFile.getFileName() // itemValue
							, -1 // itemSize
							, null // itemCharset,
							, charsetOfProject
							, childFileMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(childFileSingleItemPath, "fileSize"
							, 6 // itemTypeID
							, "long" // itemTypeName
							, childFile.getFileSize() // itemValue
							, -1 // itemSize
							, null // itemCharset,
							, charsetOfProject
							, childFileMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(childFileSingleItemPath, "fileType"
							, 0 // itemTypeID
							, "byte" // itemTypeName
							, childFile.getFileType() // itemValue
							, -1 // itemSize
							, null // itemCharset,
							, charsetOfProject
							, childFileMiddleWriteObj);
				singleItemPathStatck.pop();
			}
		}
	}
}