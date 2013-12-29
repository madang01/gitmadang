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

package impl.executor.client;

import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kr.pe.sinnori.client.ClientProjectIF;
import kr.pe.sinnori.common.configuration.ClientProjectConfigIF;
import kr.pe.sinnori.common.configuration.ProjectConfig;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.io.dhb.header.DHBMessageHeader;
import kr.pe.sinnori.common.lib.CharsetUtil;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.util.AbstractClientExecutor;

/**
 * 원본 메시지 헤더를 버퍼에 저장한후 그 버퍼 내용을 복사본 메시지 헤더로 읽어와서 비교한다.
 * @author Jonghoon Won
 *
 */
public class TestMessageHeaderCExtor extends AbstractClientExecutor {

	@Override
	protected void doTask(ClientProjectConfigIF clientProjectConfig, MessageMangerIF messageManger,
			ClientProjectIF clientProject) throws SocketTimeoutException,
			ServerNotReadyException, DynamicClassCallException,
			NoMoreDataPacketBufferException, BodyFormatException,
			MessageInfoNotFoundException, InterruptedException {
		ProjectConfig projectInfo = null;
		
		try {
			projectInfo = (ProjectConfig)conf.getResource(clientProjectConfig.getProjectName());
		} catch(RuntimeException e) {
			log.fatal(String.format("%s 프로젝트 정보가 존재하지 않습니다.", clientProjectConfig.getProjectName()));
			System.exit(1);
		}
		
		int messageIDFixedSize = projectInfo.getMessageIDFixedSize();
		
		
		
		CharsetEncoder charsetOfProjectEncoder = CharsetUtil.createCharsetEncoder(clientProjectConfig.getCharset());
		CharsetDecoder clinetCharsetDecoder = CharsetUtil.createCharsetDecoder(clientProjectConfig.getCharset());
		
		java.util.Random random = new java.util.Random();
		
		java.security.MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		ByteBuffer workBuffer = ByteBuffer.allocate(clientProjectConfig.getDataPacketBufferSize());
		workBuffer.order(clientProjectConfig.getByteOrder());
		
		DHBMessageHeader orgMessageHeader = new DHBMessageHeader(messageIDFixedSize);
		orgMessageHeader.messageID = "Echo";
		orgMessageHeader.mailboxID = 1;
		orgMessageHeader.mailID = 2;
		orgMessageHeader.bodySize = 1024;
		orgMessageHeader.bodyMD5 = new byte[DHBMessageHeader.MD5_BYTESIZE];
		random.nextBytes(orgMessageHeader.bodyMD5);
		
		orgMessageHeader.writeMessageHeader(workBuffer, clientProjectConfig.getCharset(), charsetOfProjectEncoder, md5);
		workBuffer.flip();
		
		// log.info(workBuffer.toString());
		// log.info(orgMessageHeader.toString());
		
		DHBMessageHeader dupMessageHeader = new DHBMessageHeader(messageIDFixedSize);
		
		try {
			dupMessageHeader.readMessageHeader(workBuffer, md5, clinetCharsetDecoder);
			workBuffer.flip();
			// log.info(workBuffer.toString());
		
			String orgMessageHeaderStr = orgMessageHeader.toString();
			String dupMessageHeaderStr = dupMessageHeader.toString();
			
			// log.info(dupMessageHeader.toString());
			
			log.info(String.format("원본 메시지 헤더와 복사본 메시지 헤더 비교 결과=[%s]", orgMessageHeaderStr.equals(dupMessageHeaderStr)));
		} catch (HeaderFormatException e) {
			e.printStackTrace();
		}
	}
}
