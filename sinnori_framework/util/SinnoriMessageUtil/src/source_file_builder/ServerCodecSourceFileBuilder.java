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
package source_file_builder;

import lib.CommonType;

/**
 * 서버 코덱 자바 소스 파일 빌더
 * @author "Jonghoon Won"
 *
 */
public class ServerCodecSourceFileBuilder extends AbstractSourceFileBuildre {
	
	public String toString(CommonType.CONNECTION_DIRECTION_MODE connectionDirectionMode, String messageID, String author) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(getLincenseString());
		
		// pachage
		stringBuilder.append(lineSeparator);
		stringBuilder.append("package ");
		stringBuilder.append(dynamicClassBasePackageName);
		stringBuilder.append(messageID);
		stringBuilder.append(";");
		
		// import
		stringBuilder.append(lineSeparator);
		stringBuilder.append(lineSeparator);
		stringBuilder.append("import kr.pe.sinnori.common.exception.NotSupportedException;");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("import kr.pe.sinnori.common.message.codec.MessageDecoder;");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("import kr.pe.sinnori.common.message.codec.MessageEncoder;");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("import kr.pe.sinnori.common.protocol.MessageCodecIF;");
		
		stringBuilder.append(lineSeparator);
		stringBuilder.append(lineSeparator);
		stringBuilder.append("/**");
		stringBuilder.append(lineSeparator);
		stringBuilder.append(" * ");
		stringBuilder.append(messageID);
		stringBuilder.append(" 서버 코덱");
		stringBuilder.append(lineSeparator);
		stringBuilder.append(" * @author ");
		stringBuilder.append(author);
		stringBuilder.append(lineSeparator);
		stringBuilder.append(" *");
		stringBuilder.append(lineSeparator);
		stringBuilder.append(" */");
		
		// public final class EchoServerCodec implements MessageCodecIF {
		stringBuilder.append(lineSeparator);
		stringBuilder.append("public final class ");
		stringBuilder.append(messageID);
		stringBuilder.append("ServerCodec implements MessageCodecIF {");
		
		// @Override
		stringBuilder.append(lineSeparator);
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t@Override");
		
		// public MessageDecoder getMessageDecoder() throws NotSupportedException {
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\tpublic MessageDecoder getMessageDecoder() throws NotSupportedException {");
		
		stringBuilder.append(lineSeparator);
		if ((connectionDirectionMode == CommonType.CONNECTION_DIRECTION_MODE.FROM_ALL_TO_ALL) ||
				(connectionDirectionMode == CommonType.CONNECTION_DIRECTION_MODE.FROM_CLIENT_TO_SERVER)) {
			/** 디코더가 필요한 경우 */
			stringBuilder.append("\t\treturn new ");
			stringBuilder.append(messageID);
			stringBuilder.append("Decoder();");
		} else {
			/** 디코더가 필요 없는 경우 */
			// throw new NotSupportedException("SelfExn 메시지는 클라이언트에서 서버로 전달하지 않는 메시지 입니다.");
			stringBuilder.append("\t\tthrow new NotSupportedException(\"");
			stringBuilder.append(messageID);
			stringBuilder.append("메시지는 클라이언트에서 서버로 전달하지 않는 메시지 입니다.\");");
		}
		
		
		// }
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t}");
		
		
		// @Override
		stringBuilder.append(lineSeparator);
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t@Override");
		
		// public MessageEncoder getMessageEncoder() throws NotSupportedException {
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\tpublic MessageEncoder getMessageEncoder() throws NotSupportedException {");
		
		
		stringBuilder.append(lineSeparator);
		if ((connectionDirectionMode == CommonType.CONNECTION_DIRECTION_MODE.FROM_ALL_TO_ALL) ||
				(connectionDirectionMode == CommonType.CONNECTION_DIRECTION_MODE.FROM_SERVER_TO_CLINET)) {
			/** 인코더가 필요한 경우 */
			stringBuilder.append("\t\treturn new ");
			stringBuilder.append(messageID);
			stringBuilder.append("Encoder();");
		} else {
			/** 인코더가 필요 없는 경우 */
			// throw new NotSupportedException("SelfExn 메시지는 클라이언트에서 서버로 전달하지 않는 메시지 입니다.");
			stringBuilder.append("\t\tthrow new NotSupportedException(\"");
			stringBuilder.append(messageID);
			stringBuilder.append("메시지는 서버에서 클라이언트로 전달하지 않는 메시지 입니다.\");");
		}
		
		// }
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t}");
		
		// }
		stringBuilder.append(lineSeparator);
		stringBuilder.append("}");
		
		return stringBuilder.toString();
	}

}
