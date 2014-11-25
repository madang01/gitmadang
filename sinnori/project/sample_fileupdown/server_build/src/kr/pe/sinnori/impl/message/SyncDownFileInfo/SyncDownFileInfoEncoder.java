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
package kr.pe.sinnori.impl.message.SyncDownFileInfo;

import java.nio.charset.Charset;
import java.util.LinkedList;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.MessageEncoder;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * SyncDownFileInfo 메시지 인코더
 * @author Won Jonghoon
 *
 */
public final class SyncDownFileInfoEncoder extends MessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj)
			throws Exception {
		if (!(messageObj instanceof SyncDownFileInfo)) {
			String errorMessage = String.format("메시지 객체 타입[%s]이 SyncDownFileInfo 이(가) 아닙니다.", messageObj.getClass().getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
		}
		
		SyncDownFileInfo syncDownFileInfo = (SyncDownFileInfo) messageObj;
		encodeBody(syncDownFileInfo, singleItemEncoder, charsetOfProject, middleWriteObj);
	}

	/**
	 * <pre>
	 * SyncDownFileInfo 입력 메시지의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장한다.
	 * </pre>
	 * @param syncDownFileInfo SyncDownFileInfo 입력 메시지
	 * @param singleItemEncoder 단일항목 인코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleWriteObj 중간 다리 역활 쓰기 객체
	 * @throws Exception "입력/출력 메시지"의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장할때 에러 발생시 던지는 예외
	 */
	private void encodeBody(SyncDownFileInfo syncDownFileInfo, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj) throws Exception {
		String syncDownFileInfoSingleItemPath = "SyncDownFileInfo";
		LinkedList<String> singleItemPathStatck = new LinkedList<String>();
		singleItemPathStatck.push(syncDownFileInfoSingleItemPath);

		singleItemEncoder.putValueToMiddleWriteObj(syncDownFileInfoSingleItemPath, "append"
					, 0 // itemTypeID
					, "byte" // itemTypeName
					, syncDownFileInfo.getAppend() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(syncDownFileInfoSingleItemPath, "localFilePathName"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, syncDownFileInfo.getLocalFilePathName() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(syncDownFileInfoSingleItemPath, "localFileName"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, syncDownFileInfo.getLocalFileName() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(syncDownFileInfoSingleItemPath, "localFileSize"
					, 6 // itemTypeID
					, "long" // itemTypeName
					, syncDownFileInfo.getLocalFileSize() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(syncDownFileInfoSingleItemPath, "remoteFilePathName"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, syncDownFileInfo.getRemoteFilePathName() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(syncDownFileInfoSingleItemPath, "remoteFileName"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, syncDownFileInfo.getRemoteFileName() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(syncDownFileInfoSingleItemPath, "remoteFileSize"
					, 6 // itemTypeID
					, "long" // itemTypeName
					, syncDownFileInfo.getRemoteFileSize() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(syncDownFileInfoSingleItemPath, "clientTargetFileID"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, syncDownFileInfo.getClientTargetFileID() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(syncDownFileInfoSingleItemPath, "fileBlockSize"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, syncDownFileInfo.getFileBlockSize() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
	}
}