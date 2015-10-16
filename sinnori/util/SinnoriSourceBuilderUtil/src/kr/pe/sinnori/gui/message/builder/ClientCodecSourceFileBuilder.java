package kr.pe.sinnori.gui.message.builder;

import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.lib.CommonType;

public class ClientCodecSourceFileBuilder extends AbstractSourceFileBuildre {

	public String toString(
			CommonType.MESSAGE_TRANSFER_DIRECTION connectionDirectionMode,
			String messageID, String author) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(getLincenseString());

		// pachage
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("package ");
		stringBuilder.append(dynamicClassBasePackageName);
		stringBuilder.append(messageID);
		stringBuilder.append(";");

		// import
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder
				.append("import kr.pe.sinnori.common.exception.DynamicClassCallException;");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder
				.append("import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder
				.append("import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder
				.append("import kr.pe.sinnori.common.protocol.MessageCodecIF;");

		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("/**");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(" * ");
		stringBuilder.append(messageID);
		stringBuilder.append(" 클라이언트 코덱");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(" * @author ");
		stringBuilder.append(author);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(" *");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(" */");

		// public final class EchoClientCodec implements MessageCodecIF {
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("public final class ");
		stringBuilder.append(messageID);
		stringBuilder.append("ClientCodec implements MessageCodecIF {");

		// @Override
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t@Override");

		// public MessageDecoder getMessageDecoder() throws
		// DynamicClassCallException {
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder
				.append("\tpublic AbstractMessageDecoder getMessageDecoder() throws DynamicClassCallException {");

		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		if ((connectionDirectionMode == CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_ALL_TO_ALL)
				|| (connectionDirectionMode == CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_SERVER_TO_CLINET)) {
			/** 디코더가 필요한 경우 */
			stringBuilder.append("\t\treturn new ");
			stringBuilder.append(messageID);
			stringBuilder.append("Decoder();");
		} else {
			/** 디코더가 필요 없는 경우 */
			// throw new
			// DynamicClassCallException("SelfExn 메시지는 클라이언트에서 서버로 전달하지 않는 메시지 입니다.");
			stringBuilder.append("\t\tthrow new DynamicClassCallException(\"");
			stringBuilder.append(messageID);
			stringBuilder.append(" 메시지는 서버에서 클라이언트로 전달하지 않는 메시지 입니다.\");");
		}

		// }
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t}");

		// @Override
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t@Override");

		// public MessageEncoder getMessageEncoder() throws
		// DynamicClassCallException {
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder
				.append("\tpublic AbstractMessageEncoder getMessageEncoder() throws DynamicClassCallException {");

		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		if ((connectionDirectionMode == CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_ALL_TO_ALL)
				|| (connectionDirectionMode == CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_CLIENT_TO_SERVER)) {
			/** 인코더가 필요한 경우 */
			stringBuilder.append("\t\treturn new ");
			stringBuilder.append(messageID);
			stringBuilder.append("Encoder();");
		} else {
			/** 인코더가 필요 없는 경우 */
			// throw new
			// DynamicClassCallException("SelfExn 메시지는 클라이언트에서 서버로 전달하지 않는 메시지 입니다.");
			stringBuilder.append("\t\tthrow new DynamicClassCallException(\"");
			stringBuilder.append(messageID);
			stringBuilder.append("메시지는 클라이언트에서 서버로 전달하지 않는 메시지 입니다.\");");
		}
		// }
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t}");

		// }
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("}");

		return stringBuilder.toString();
	}
}
