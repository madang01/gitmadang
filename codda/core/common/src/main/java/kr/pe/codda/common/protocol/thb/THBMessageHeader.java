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

import java.nio.BufferOverflowException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.exception.CharsetEncoderException;
import kr.pe.codda.common.exception.HeaderFormatException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.exception.BufferOverflowExceptionWithMessage;
import kr.pe.codda.common.io.BinaryInputStreamIF;
import kr.pe.codda.common.io.BinaryOutputStreamIF;

/**
 * @author Won Jonghoon
 *
 */
public class THBMessageHeader {
	private InternalLogger log = InternalLoggerFactory.getInstance(THBMessageHeader.class);	
	
	public long bodySize= -1;
	
	
	public void toOutputStream(BinaryOutputStreamIF headerOutputStream, Charset headerCharset) throws IllegalArgumentException, CharsetEncoderException, BufferOverflowException, BufferOverflowExceptionWithMessage, NoMoreDataPacketBufferException {
		if (null == headerOutputStream) {
			throw new IllegalArgumentException("the parameter headerOutputStream is null");
		}
		
		if (null == headerCharset) {
			throw new IllegalArgumentException("the parameter headerCharset is null");
		}
		
		headerOutputStream.putLong(bodySize);
		
	}
	
	
	public void fromInputStream(BinaryInputStreamIF headerInputStream, CharsetDecoder headerCharsetDecoder) throws HeaderFormatException {		
		try {
			this.bodySize = headerInputStream.getLong();
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.warn(errorMessage, e);
			throw new HeaderFormatException(errorMessage);
		}
	}
	

	@Override
	public String toString() {
		StringBuffer headerInfo = new StringBuffer();
		headerInfo.append("THBMessageHeader={body data size=[");
		headerInfo.append(bodySize);
		headerInfo.append("]");		
		headerInfo.append("}");

		return headerInfo.toString();
	}
}
