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
package kr.pe.sinnori.common.message.builder;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.type.MessageTransferDirectionType;
import kr.pe.sinnori.common.util.CommonStaticUtil;

/**
 * 서버 코덱 자바 소스 파일 빌더
 * @author "Won Jonghoon"
 *
 */
public class ServerCodecFileContensBuilder extends AbstractSourceFileBuildre {
	
	public String buildStringOfFileContents(MessageTransferDirectionType messageTransferDirectionType, String messageID, String author) {
		final int depth = 0;
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(buildStringOfLincensePart());
		
		// pachage
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("package ");
		stringBuilder.append(PACKAGE_FULL_NAME_PREFIX);
		stringBuilder.append(messageID);
		stringBuilder.append(";");
		
		// import
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		String importElements[] = {
				"import kr.pe.sinnori.common.exception.DynamicClassCallException;",
				"import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;",
				"import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;",
				"import kr.pe.sinnori.common.protocol.MessageCodecIF;"
		};
		stringBuilder.append(buildStringOfImportPart(importElements));		
		
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(buildStringOfFileDescriptionPart(messageID, author, "서버 코덱"));
		
		// public final class EchoServerCodec implements MessageCodecIF {
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("public final class ");
		stringBuilder.append(messageID);
		stringBuilder.append("ServerCodec implements MessageCodecIF {");
		
		// @Override
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("@Override");
		
		// public MessageDecoder getMessageDecoder() throws DynamicClassCallException {
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("public AbstractMessageDecoder getMessageDecoder() throws DynamicClassCallException {");
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
		switch(messageTransferDirectionType) {
			case FROM_ALL_TO_ALL :
			case FROM_CLIENT_TO_SERVER : {
				/** 디코더가 필요한 경우 */
				stringBuilder.append("return new ");
				stringBuilder.append(messageID);
				stringBuilder.append("Decoder();");
				break;
			}
			default : {
				/** 디코더가 필요 없는 경우 */
				stringBuilder.append("throw new DynamicClassCallException(\"the server don't need a message decoder because it is a message[");
				stringBuilder.append(messageID);
				stringBuilder.append("] that is not sent from client to server\");");
				break;
			}
		}
		
		
		// }
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("}");
		
		
		// @Override
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("@Override");
		
		// public MessageEncoder getMessageEncoder() throws DynamicClassCallException {
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("public AbstractMessageEncoder getMessageEncoder() throws DynamicClassCallException {");
		
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
		
		switch(messageTransferDirectionType) {
			case FROM_ALL_TO_ALL :
			case FROM_SERVER_TO_CLINET : {
				/** 인코더가 필요한 경우 */
				stringBuilder.append("return new ");
				stringBuilder.append(messageID);
				stringBuilder.append("Encoder();");
				break;
			}
			default : {
				/** 디코더가 필요 없는 경우 */
				stringBuilder.append("throw new DynamicClassCallException(\"the server don't need a message encoder because it is a message[");
				stringBuilder.append(messageID);
				stringBuilder.append("] that is not sent from server to client\");");
				break;
			}
		}
		
		
		// }
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("}");
		
		// }
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("}");
		
		return stringBuilder.toString();
	}

}
