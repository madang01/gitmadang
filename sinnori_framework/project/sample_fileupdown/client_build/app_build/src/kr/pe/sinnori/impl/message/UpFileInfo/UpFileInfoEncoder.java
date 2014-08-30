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
package kr.pe.sinnori.impl.message.UpFileInfo;

import java.nio.charset.Charset;
import java.util.LinkedList;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.MessageEncoder;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * UpFileInfo 메시지 인코더
 * @author Jonghoon Won
 *
 */
public final class UpFileInfoEncoder extends MessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj)
			throws Exception {
		if (!(messageObj instanceof UpFileInfo)) {
			String errorMessage = String.format("메시지 객체 타입[%s]이 UpFileInfo 이(가) 아닙니다.", messageObj.getClass().getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
		}
		
		UpFileInfo upFileInfo = (UpFileInfo) messageObj;
		encodeBody(upFileInfo, singleItemEncoder, charsetOfProject, middleWriteObj);
	}

	/**
	 * <pre>
	 * UpFileInfo 입력 메시지의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장한다.
	 * </pre>
	 * @param upFileInfo UpFileInfo 입력 메시지
	 * @param singleItemEncoder 단일항목 인코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleWriteObj 중간 다리 역활 쓰기 객체
	 * @throws Exception "입력/출력 메시지"의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장할때 에러 발생시 던지는 예외
	 */
	private void encodeBody(UpFileInfo upFileInfo, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj) throws Exception {
		String upFileInfoSingleItemPath = "UpFileInfo";
		LinkedList<String> singleItemPathStatck = new LinkedList<String>();
		singleItemPathStatck.push(upFileInfoSingleItemPath);

		singleItemEncoder.putValueToMiddleWriteObj(upFileInfoSingleItemPath, "append"
					, 0 // itemTypeID
					, "byte" // itemTypeName
					, upFileInfo.getAppend() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(upFileInfoSingleItemPath, "clientSourceFileID"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, upFileInfo.getClientSourceFileID() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(upFileInfoSingleItemPath, "localFilePathName"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, upFileInfo.getLocalFilePathName() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(upFileInfoSingleItemPath, "localFileName"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, upFileInfo.getLocalFileName() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(upFileInfoSingleItemPath, "localFileSize"
					, 6 // itemTypeID
					, "long" // itemTypeName
					, upFileInfo.getLocalFileSize() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(upFileInfoSingleItemPath, "remoteFilePathName"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, upFileInfo.getRemoteFilePathName() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(upFileInfoSingleItemPath, "remoteFileName"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, upFileInfo.getRemoteFileName() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(upFileInfoSingleItemPath, "remoteFileSize"
					, 6 // itemTypeID
					, "long" // itemTypeName
					, upFileInfo.getRemoteFileSize() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(upFileInfoSingleItemPath, "fileBlockSize"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, upFileInfo.getFileBlockSize() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
	}
}