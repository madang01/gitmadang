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
package kr.pe.codda.common.protocol.thb;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.BinaryOutputStreamIF;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;
import kr.pe.codda.common.type.SingleItemType;

/**
 * THB 단일 항목 인코더
 * @author "Won Jonghoon"
 *
 */
public class THBSingleItemEncoder implements SingleItemEncoderIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(THBSingleItemEncoder.class);
	
	private THBSingleItemEncoderMatcherIF thbSingleItemEncoderMatcher = null;
	
	public THBSingleItemEncoder(THBSingleItemEncoderMatcherIF thbSingleItemEncoderMatcher) {
		if (null == thbSingleItemEncoderMatcher) {
			throw new IllegalArgumentException("the parameter thbSingleItemEncoderMatcher is null");
		}
		
		this.thbSingleItemEncoderMatcher = thbSingleItemEncoderMatcher;
	}
	

	@Override
	public void putValueToWritableMiddleObject(String path, String itemName,
			SingleItemType singleItemType, Object nativeItemValue,
			int itemSize, String nativeItemCharset, Object writableMiddleObject) throws Exception {
		if (null == path) {
			throw new IllegalArgumentException("the parameter path is null");
		}
		if (null == itemName) {
			throw new IllegalArgumentException("the parameter itemName is null");
		}
		if (null == singleItemType) {
			throw new IllegalArgumentException("the parameter singleItemType is null");
		}
		
		if (null == nativeItemValue) {
			throw new IllegalArgumentException("the parameter nativeItemValue is null");
		}
		
		if (null == writableMiddleObject) {
			throw new IllegalArgumentException("the parameter writableMiddleObject is null");
		}
		
		if (! (writableMiddleObject instanceof BinaryOutputStreamIF)) {
			String errorMessage = new StringBuilder("the parameter writableMiddleObject's class[")
					.append(writableMiddleObject.getClass().getCanonicalName())
					.append("] is not a BinaryOutputStreamIF class").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		int itemTypeID = singleItemType.getItemTypeID();
		String itemTypeName = singleItemType.getItemTypeName();
		
		BinaryOutputStreamIF binaryOutputStream = (BinaryOutputStreamIF)writableMiddleObject;		
		try {
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			
			thbSingleItemEncoder.putValue(itemTypeID, itemName, nativeItemValue, itemSize, nativeItemCharset, binaryOutputStream);
		} catch(IllegalArgumentException e) {
			throw e;
		} catch(NoMoreDataPacketBufferException e) {
			throw e;
		} catch(Exception | OutOfMemoryError e) {
			String errorMessage = new StringBuilder("fail to encode a single item value::")
					.append("{ path=[")
					.append(path)
					.append("], itemName=[")
					.append(itemName)
					.append("], itemType=[")
					.append(itemTypeName)
					.append("], itemSize=[")
					.append(itemSize)
					.append("], itemCharset=[")
					.append(nativeItemCharset)
					.append("] }, errmsg=[")
					.append(e.getMessage())
					.append("]").toString();
			
			log.warn(errorMessage, e);
			throw new BodyFormatException(errorMessage);
		}
	}

	@Override
	public Object getWritableMiddleObjectjFromArrayMiddleObject(String path, Object arrayObj, int inx) throws BodyFormatException {
		return arrayObj;
	}
	
	@Override
	public Object getArrayMiddleObjectFromWritableMiddleObject(String path, String arrayName,
			int arrayCntValue, Object writableMiddleObject) throws BodyFormatException {
		return writableMiddleObject;
	}
	
	@Override
	public Object getGroupMiddleObjectFromWritableMiddleObject(String path, String groupName, Object writableMiddleObject)
			throws BodyFormatException {
		return writableMiddleObject;
	}
}
