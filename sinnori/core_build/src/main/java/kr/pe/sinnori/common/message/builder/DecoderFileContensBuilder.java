package kr.pe.sinnori.common.message.builder;

import java.util.ArrayList;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.message.builder.info.AbstractItemInfo;
import kr.pe.sinnori.common.message.builder.info.ArrayInfo;
import kr.pe.sinnori.common.message.builder.info.ItemGroupIF;
import kr.pe.sinnori.common.message.builder.info.SingleItemInfo;
import kr.pe.sinnori.common.message.builder.info.SingleItemTypeManger;


public class DecoderFileContensBuilder extends AbstractSourceFileBuildre {

	private String getCountVarName(int depth) {
		StringBuilder countVarNameBuilder = new StringBuilder();
		for (int i=0; i <= depth; i++) {
			countVarNameBuilder.append("i");
		}
		return countVarNameBuilder.toString();
	}
	
	private String toBody(int depth, String path, String varName, ItemGroupIF arrayInfo, String middleObjVarName) {
		StringBuilder stringBuilder = new StringBuilder();
		
		ArrayList<AbstractItemInfo> itemInfoList = arrayInfo.getItemInfoList();
		for (AbstractItemInfo itemInfo:itemInfoList) {
			if (itemInfo.getMessageItemType() == CommonType.MESSAGE_ITEM_TYPE.SINGLE_ITEM) {
				SingleItemInfo singleItemInfo = (SingleItemInfo)itemInfo;
				
				// allDataType.setByteVar1((Byte)
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t");
				stringBuilder.append(varName);
				stringBuilder.append(".set");
				stringBuilder.append(singleItemInfo.getFirstUpperItemName());
				stringBuilder.append("((");
				stringBuilder.append(singleItemInfo.getJavaLangClassCastingTypeOfItemType());
				stringBuilder.append(")");
				
				// singleItemDecoder.getValueFromReadableMiddleObject(sigleItemPath0
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\tsingleItemDecoder.getValueFromReadableMiddleObject(sigleItemPath");
				stringBuilder.append(depth);
				
				// , "byteVar1" // itemName
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t, \"");
				stringBuilder.append(singleItemInfo.getItemName());
				stringBuilder.append("\" // itemName");
				
				// itemType
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t, ");
				stringBuilder.append("SingleItemType.");
				stringBuilder.append(SingleItemTypeManger.getInstance()
						.getSingleItemType(singleItemInfo.getItemTypeID()).name());
				stringBuilder.append(" // itemType");
				// , SingleItemType.UB_PASCAL_STRING
				
				// itemSize
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t, ");
				stringBuilder.append(singleItemInfo.getItemSize());
				stringBuilder.append(" // itemSize");
				
				// itemCharset
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t, ");				
				/*SingleItemType itemType = singleItemInfo.getItemType();
				if (itemType.equals(SingleItemType.FIXED_LENGTH_STRING) ||
						itemType.equals(SingleItemType.UB_PASCAL_STRING) ||
						itemType.equals(SingleItemType.US_PASCAL_STRING) ||
						itemType.equals(SingleItemType.SI_PASCAL_STRING)) {*/
				String nativeItemCharset = singleItemInfo.getNativeItemCharset();
				if (null == nativeItemCharset) {
					stringBuilder.append("null");
				} else {
					stringBuilder.append("\"");
					stringBuilder.append(nativeItemCharset);
					stringBuilder.append("\"");
				}				
				stringBuilder.append(" // nativeItemCharset");
				
				
				// , middleReadableObject));
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t, ");
				stringBuilder.append(middleObjVarName);
				stringBuilder.append("));");
				
			} else {
				ArrayInfo arrayInfoOfChild = (ArrayInfo) itemInfo;
				
				// int memberListSize=allDataType.getCnt();
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\tint ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("ListSize = ");
				
				/** 배열 크기 지정 방식에 따른 배열 크기 지정 */
				if (arrayInfoOfChild.getArrayCntType().equals("reference")) {
					stringBuilder.append(varName);
					stringBuilder.append(".get");
					stringBuilder.append(arrayInfoOfChild.getArrayCntValue().substring(0, 1).toUpperCase());
					stringBuilder.append(arrayInfoOfChild.getArrayCntValue().substring(1));
					stringBuilder.append("();");
				} else {
					stringBuilder.append(arrayInfoOfChild.getArrayCntValue());
					stringBuilder.append(";");
				}
								
				// Object memberMiddleReadArray = singleItemDecoder.getArrayObjFromMiddleReadObj(sigleItemPath0, "member", memberListSize, middleReadableObject);
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\tObject ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("MiddleReadArray = singleItemDecoder.getArrayObjFromMiddleReadObj(sigleItemPath");
				stringBuilder.append(depth);
				stringBuilder.append(", \"");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("\", ");
				stringBuilder.append(arrayInfoOfChild.getItemName());				
				stringBuilder.append("ListSize, ");
				stringBuilder.append(middleObjVarName);
				stringBuilder.append(");");
				
				// List<AllDataType.Member> memberList = new ArrayList<AllDataType.Member>();
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\tjava.util.List<");
				stringBuilder.append(path);
				stringBuilder.append(".");
				stringBuilder.append(arrayInfoOfChild.getFirstUpperItemName());
				stringBuilder.append("> ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List = new java.util.ArrayList<");
				stringBuilder.append(path);
				stringBuilder.append(".");
				stringBuilder.append(arrayInfoOfChild.getFirstUpperItemName());
				stringBuilder.append(">();");
				
				// for (int i=0; i < memberListSize; i++) {
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\tfor (int ");
				stringBuilder.append(getCountVarName(depth));
				stringBuilder.append("=0; ");
				stringBuilder.append(getCountVarName(depth));
				stringBuilder.append(" < ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("ListSize; ");
				stringBuilder.append(getCountVarName(depth));
				stringBuilder.append("++) {");
				
				// String sigleItemPath1 = new StringBuilder(sigleItemPath0).append(".").append("Member[").append(i).append("]").toString();
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\tString sigleItemPath");
				stringBuilder.append(depth+1);
				stringBuilder.append(" = new StringBuilder(sigleItemPath");
				stringBuilder.append(depth);
				stringBuilder.append(").append(\".\").append(\"");
				stringBuilder.append(arrayInfoOfChild.getFirstUpperItemName());
				stringBuilder.append("[\").append(");
				stringBuilder.append(getCountVarName(depth));
				stringBuilder.append(").append(\"]\").toString();");
				
				// Object memberMiddleReadObj = singleItemDecoder.getMiddleReadObjFromArrayObj(sigleItemPath1, memberMiddleReadArray, i);
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\tObject ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("MiddleReadObj = singleItemDecoder.getMiddleReadObjFromArrayObj(sigleItemPath");
				stringBuilder.append(depth+1);
				stringBuilder.append(", ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("MiddleReadArray, ");
				stringBuilder.append(getCountVarName(depth));
				stringBuilder.append(");");
				
				// AllDataType.Member member = new AllDataType.Member();
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t");
				stringBuilder.append(path);
				stringBuilder.append(".");
				stringBuilder.append(arrayInfoOfChild.getFirstUpperItemName());
				stringBuilder.append(" ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append(" = new ");
				stringBuilder.append(path);
				stringBuilder.append(".");
				stringBuilder.append(arrayInfoOfChild.getFirstUpperItemName());
				stringBuilder.append("();");
				
				// memberList.add(member);
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				stringBuilder.append("\t\t\t");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List.add(");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append(");");				
				
				
				stringBuilder.append(toBody(depth+1, path+"."+arrayInfoOfChild.getFirstUpperItemName(), 
						arrayInfoOfChild.getItemName(), 
						arrayInfoOfChild, arrayInfoOfChild.getItemName()+ "MiddleReadObj"));						
								
				// }
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t}");
				
				// allDataType.setMemberList(memberList);
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t");
				stringBuilder.append(varName);
				stringBuilder.append(".set");
				stringBuilder.append(arrayInfoOfChild.getFirstUpperItemName());
				stringBuilder.append("List(");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List);");	
			}
		}
		
		return stringBuilder.toString();
	}
	
	public String getFileContents(String messageID,
			String author,
			kr.pe.sinnori.common.message.builder.info.MessageInfo messageInfo) {
		String firstLowerMessageID =  messageID.substring(0, 1).toLowerCase() + messageID.substring(1);
		
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(getLincenseString());
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("package ");
		stringBuilder.append(dynamicClassBasePackageName);
		stringBuilder.append(messageID);
		stringBuilder.append(";");
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				
		String importElements[] = {
				"import kr.pe.sinnori.common.exception.BodyFormatException;",
				"import kr.pe.sinnori.common.message.AbstractMessage;",
				"import kr.pe.sinnori.common.message.builder.info.SingleItemType;",				
				"import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;",
				"import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;"
		};
		stringBuilder.append(getImportPartString(importElements));
		
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("/**");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(" * ");
		stringBuilder.append(messageID);
		stringBuilder.append(" 메시지 디코더");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(" * @author ");
		stringBuilder.append(author);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(" *");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(" */");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("public final class ");
		stringBuilder.append(messageID);
		stringBuilder.append("Decoder extends AbstractMessageDecoder {");
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t/**");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t * <pre>");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t * ");
		// "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 Echo 메시지를 반환한다.
		stringBuilder.append(" \"단일항목 디코더\"를 이용하여 \"중간 다리 역활 읽기 객체\" 에서 추출된 ");
		stringBuilder.append(messageID);
		stringBuilder.append(" 메시지를 반환한다.");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t * </pre>");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t * @param singleItemDecoder 단일항목 디코더");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);		
		stringBuilder.append("\t * @param middleReadableObject 중간 다리 역활 읽기 객체");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t * @return \"단일항목 디코더\"를 이용하여 \"중간 다리 역활 읽기 객체\" 에서 추출된 ");
		stringBuilder.append(messageID);
		stringBuilder.append(" 메시지");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t * @throws OutOfMemoryError 메모리 확보 실패시 던지는 예외");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t * @throws BodyFormatException 바디 디코딩 실패시 던지는 예외");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t */");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t@Override");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\tprotected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws OutOfMemoryError, BodyFormatException {");
		
		// AllDataType allDataType = new AllDataType();
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t\t");
		stringBuilder.append(messageID);
		stringBuilder.append(" ");
		stringBuilder.append(firstLowerMessageID);
		stringBuilder.append(" = new ");
		stringBuilder.append(messageID);
		stringBuilder.append("();");
		
		if (messageInfo.getItemInfoList().size() > 0) {
			// String sigleItemPath0 = "AllDataType";
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			stringBuilder.append("\t\tString sigleItemPath0 = \"");
			stringBuilder.append(messageID);
			stringBuilder.append("\";");
			
			stringBuilder.append(toBody(0, messageID, firstLowerMessageID, messageInfo, "middleReadableObject"));
		}
		
		
		// return allDataType;
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t\treturn ");
		stringBuilder.append(firstLowerMessageID);
		stringBuilder.append(";");
		
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t}");
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("}");
		
		return stringBuilder.toString();
	}

}
