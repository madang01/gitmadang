package kr.pe.codda.common.message.builder;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.type.MessageTransferDirectionType;
import kr.pe.codda.common.util.CommonStaticUtil;

public class ClientCodecFileContensBuilder extends AbstractSourceFileBuildre {

	public String buildStringOfFileContents(
			MessageTransferDirectionType messageTransferDirectionType,
			String messageID, String author) {
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
		stringBuilder.append(buildStringOfFileDescriptionPart(messageID, author, "클라이언트 코덱"));

		// public final class EchoClientCodec implements MessageCodecIF {
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("public final class ");
		stringBuilder.append(messageID);
		stringBuilder.append("ClientCodec implements MessageCodecIF {");

		// @Override
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("@Override");

		// public MessageDecoder getMessageDecoder() throws
		// DynamicClassCallException {
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder
				.append("public AbstractMessageDecoder getMessageDecoder() throws DynamicClassCallException {");

		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));	
		
		switch (messageTransferDirectionType) {
			case FROM_ALL_TO_ALL : 
			case FROM_SERVER_TO_CLINET : {
				/** 디코더가 필요한 경우 */
				stringBuilder.append("return new ");
				stringBuilder.append(messageID);
				stringBuilder.append("Decoder();");
				break;
			}
			default: {
				/** 디코더가 필요 없는 경우 */
				stringBuilder.append("throw new DynamicClassCallException(\"the client don't need a message decoder because it is a message[");
				stringBuilder.append(messageID);
				stringBuilder.append("] that is not sent from server to client\");");
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

		// public MessageEncoder getMessageEncoder() throws
		// DynamicClassCallException {
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder
				.append("public AbstractMessageEncoder getMessageEncoder() throws DynamicClassCallException {");

		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
		
		switch (messageTransferDirectionType) {
			case FROM_ALL_TO_ALL : 
			case FROM_CLIENT_TO_SERVER : {
				/** 인코더가 필요한 경우 */
				stringBuilder.append("return new ");
				stringBuilder.append(messageID);
				stringBuilder.append("Encoder();");
				break;
			}
			default: {
				/** 디코더가 필요 없는 경우 */
				stringBuilder.append("throw new DynamicClassCallException(\"the client don't need a message encoder because it is a message[");
				stringBuilder.append(messageID);
				stringBuilder.append("] that is not sent from client to server\");");
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
