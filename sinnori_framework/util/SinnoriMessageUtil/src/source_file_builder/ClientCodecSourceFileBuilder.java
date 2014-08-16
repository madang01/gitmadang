package source_file_builder;

import lib.CommonType;


public class ClientCodecSourceFileBuilder extends AbstractSourceFileBuildre {
	
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
		stringBuilder.append(" 클라이언트 코덱");
		stringBuilder.append(lineSeparator);
		stringBuilder.append(" * @author ");
		stringBuilder.append(author);
		stringBuilder.append(lineSeparator);
		stringBuilder.append(" *");
		stringBuilder.append(lineSeparator);
		stringBuilder.append(" */");
		
		// public final class EchoClientCodec implements MessageCodecIF {
		stringBuilder.append(lineSeparator);
		stringBuilder.append("public final class ");
		stringBuilder.append(messageID);
		stringBuilder.append("ClientCodec implements MessageCodecIF {");
		
		// @Override
		stringBuilder.append(lineSeparator);
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t@Override");
		
		// public MessageDecoder getMessageDecoder() throws NotSupportedException {
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\tpublic MessageDecoder getMessageDecoder() throws NotSupportedException {");
		
		stringBuilder.append(lineSeparator);
		if ((connectionDirectionMode == CommonType.CONNECTION_DIRECTION_MODE.FROM_ALL_TO_ALL) ||
				(connectionDirectionMode == CommonType.CONNECTION_DIRECTION_MODE.FROM_SERVER_TO_CLINET)) {
			/** 디코더가 필요한 경우 */
			stringBuilder.append("\t\treturn new ");
			stringBuilder.append(messageID);
			stringBuilder.append("Decoder();");
		} else {
			/** 디코더가 필요 없는 경우 */
			// throw new NotSupportedException("SelfExn 메시지는 클라이언트에서 서버로 전달하지 않는 메시지 입니다.");
			stringBuilder.append("\t\tthrow new NotSupportedException(\"");
			stringBuilder.append(messageID);
			stringBuilder.append("메시지는 서버에서 클라이언트로 전달하지 않는 메시지 입니다.\");");
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
				(connectionDirectionMode == CommonType.CONNECTION_DIRECTION_MODE.FROM_CLIENT_TO_SERVER)) {
			/** 인코더가 필요한 경우 */
			stringBuilder.append("\t\treturn new ");
			stringBuilder.append(messageID);
			stringBuilder.append("Encoder();");
		} else {
			/** 인코더가 필요 없는 경우 */
			// throw new NotSupportedException("SelfExn 메시지는 클라이언트에서 서버로 전달하지 않는 메시지 입니다.");
			stringBuilder.append("\t\tthrow new NotSupportedException(\"");
			stringBuilder.append(messageID);
			stringBuilder.append("메시지는 클라이언트에서 서버로 전달하지 않는 메시지 입니다.\");");
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
