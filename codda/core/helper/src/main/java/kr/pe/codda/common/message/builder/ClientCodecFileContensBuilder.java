package kr.pe.codda.common.message.builder;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageCodecIF;
import kr.pe.codda.common.type.MessageTransferDirectionType;
import kr.pe.codda.common.util.CommonStaticUtil;

public class ClientCodecFileContensBuilder extends AbstractSourceFileBuildre {

	public String buildStringOfFileContents(MessageTransferDirectionType messageTransferDirectionType, String messageID,
			String author) {
		final int depth = 0;

		StringBuilder contetnsStringBuilder = new StringBuilder();
		addLincensePart(contetnsStringBuilder);

		// pachage
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		addPackageDeclarationPart(contetnsStringBuilder, messageID);

		// import
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		Class<?> importClazzes[] = { DynamicClassCallException.class, AbstractMessageDecoder.class,
				AbstractMessageEncoder.class, MessageCodecIF.class };
		addImportDeclarationsPart(contetnsStringBuilder, importClazzes);

		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		addSourceFileDescriptionPart(contetnsStringBuilder, messageID, author, "client codec");

		// public final class EchoClientCodec implements MessageCodecIF {
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("public final class ");
		contetnsStringBuilder.append(messageID);
		contetnsStringBuilder.append("ClientCodec implements MessageCodecIF {");
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);

		switch (messageTransferDirectionType) {
			case FROM_ALL_TO_ALL:
			case FROM_SERVER_TO_CLINET: {
				/** 디코더가 필요한 경우 */
				// private AbstractMessageDecoder messageDecoder = new EmptyDecoder();
				contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
				contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
				contetnsStringBuilder.append("private AbstractMessageDecoder messageDecoder = new ");
				contetnsStringBuilder.append(messageID);
				contetnsStringBuilder.append("Decoder();");
				break;
			}
			default: {
				/** 디코더가 필요 없는 경우 */
				break;
			}
		}
		
		switch (messageTransferDirectionType) {
			case FROM_ALL_TO_ALL:
			case FROM_CLIENT_TO_SERVER: {
				/** 인코더가 필요한 경우 */
				// private AbstractMessageDecoder messageDecoder = new EmptyDecoder();
				contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
				contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
				contetnsStringBuilder.append("private AbstractMessageEncoder messageEncoder = new ");
				contetnsStringBuilder.append(messageID);
				contetnsStringBuilder.append("Encoder();");
				break;
			}
			default: {
				/** 인코더가 필요 없는 경우 */
				break;
			}
		}

		// @Override
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append("@Override");

		// public MessageDecoder getMessageDecoder() throws
		// DynamicClassCallException {
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder
				.append("public AbstractMessageDecoder getMessageDecoder() throws DynamicClassCallException {");

		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));

		switch (messageTransferDirectionType) {
		case FROM_ALL_TO_ALL:
		case FROM_SERVER_TO_CLINET: {
			/** 디코더가 필요한 경우 */
			contetnsStringBuilder.append("return messageDecoder;");
			break;
		}
		default: {
			/** 디코더가 필요 없는 경우 */
			contetnsStringBuilder.append(
					"throw new DynamicClassCallException(\"the client don't need a message decoder because it is a message[");
			contetnsStringBuilder.append(messageID);
			contetnsStringBuilder.append("] that is not sent from server to client\");");
			break;
		}
		}

		// }
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append("}");

		// @Override
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append("@Override");

		// public MessageEncoder getMessageEncoder() throws
		// DynamicClassCallException {
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder
				.append("public AbstractMessageEncoder getMessageEncoder() throws DynamicClassCallException {");

		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));

		switch (messageTransferDirectionType) {
		case FROM_ALL_TO_ALL:
		case FROM_CLIENT_TO_SERVER: {
			/** 인코더가 필요한 경우 */
			contetnsStringBuilder.append("return messageEncoder;");
			break;
		}
		default: {
			/** 인코더가 필요 없는 경우 */
			contetnsStringBuilder.append(
					"throw new DynamicClassCallException(\"the client don't need a message encoder because it is a message[");
			contetnsStringBuilder.append(messageID);
			contetnsStringBuilder.append("] that is not sent from client to server\");");
			break;
		}
		}

		// }
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append("}");

		// }
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("}");

		return contetnsStringBuilder.toString();
	}
}
