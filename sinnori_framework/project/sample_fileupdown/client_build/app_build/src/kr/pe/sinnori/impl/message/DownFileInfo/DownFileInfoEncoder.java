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
package kr.pe.sinnori.impl.message.DownFileInfo;

import java.nio.charset.Charset;
import java.util.LinkedList;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.MessageEncoder;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * DownFileInfo 메시지 인코더
 * @author Jonghoon Won
 *
 */
public final class DownFileInfoEncoder extends MessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj)
			throws Exception {
		if (!(messageObj instanceof DownFileInfo)) {
			String errorMessage = String.format("메시지 객체 타입[%s]이 DownFileInfo 이(가) 아닙니다.", messageObj.getClass().getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
		}
		
		DownFileInfo downFileInfo = (DownFileInfo) messageObj;
		encodeBody(downFileInfo, singleItemEncoder, charsetOfProject, middleWriteObj);
	}

	/**
	 * <pre>
	 * DownFileInfo 입력 메시지의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장한다.
	 * </pre>
	 * @param downFileInfo DownFileInfo 입력 메시지
	 * @param singleItemEncoder 단일항목 인코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleWriteObj 중간 다리 역활 쓰기 객체
	 * @throws Exception "입력/출력 메시지"의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장할때 에러 발생시 던지는 예외
	 */
	private void encodeBody(DownFileInfo downFileInfo, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj) throws Exception {
		String downFileInfoSingleItemPath = "DownFileInfo";
		LinkedList<String> singleItemPathStatck = new LinkedList<String>();
		singleItemPathStatck.push(downFileInfoSingleItemPath);

		singleItemEncoder.putValueToMiddleWriteObj(downFileInfoSingleItemPath, "append"
					, 0 // itemTypeID
					, "byte" // itemTypeName
					, downFileInfo.getAppend() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(downFileInfoSingleItemPath, "localFilePathName"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, downFileInfo.getLocalFilePathName() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(downFileInfoSingleItemPath, "localFileName"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, downFileInfo.getLocalFileName() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(downFileInfoSingleItemPath, "localFileSize"
					, 6 // itemTypeID
					, "long" // itemTypeName
					, downFileInfo.getLocalFileSize() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(downFileInfoSingleItemPath, "remoteFilePathName"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, downFileInfo.getRemoteFilePathName() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(downFileInfoSingleItemPath, "remoteFileName"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, downFileInfo.getRemoteFileName() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(downFileInfoSingleItemPath, "remoteFileSize"
					, 6 // itemTypeID
					, "long" // itemTypeName
					, downFileInfo.getRemoteFileSize() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(downFileInfoSingleItemPath, "clientTargetFileID"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, downFileInfo.getClientTargetFileID() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(downFileInfoSingleItemPath, "fileBlockSize"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, downFileInfo.getFileBlockSize() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
	}
}