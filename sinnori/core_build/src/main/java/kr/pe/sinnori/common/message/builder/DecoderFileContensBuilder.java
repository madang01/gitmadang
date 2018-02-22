package kr.pe.sinnori.common.message.builder;

import java.util.List;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.builder.info.AbstractItemInfo;
import kr.pe.sinnori.common.message.builder.info.ArrayInfo;
import kr.pe.sinnori.common.message.builder.info.GroupInfo;
import kr.pe.sinnori.common.message.builder.info.OrderedItemSet;
import kr.pe.sinnori.common.message.builder.info.SingleItemInfo;
import kr.pe.sinnori.common.message.builder.info.SingleItemTypeManger;
import kr.pe.sinnori.common.type.ItemInfoType;
import kr.pe.sinnori.common.util.CommonStaticUtil;


public class DecoderFileContensBuilder extends AbstractSourceFileBuildre {

	public String getCountVarName(int depth) {
		StringBuilder countVarNameBuilder = new StringBuilder();
		countVarNameBuilder.append("i");
		countVarNameBuilder.append(depth);
		return countVarNameBuilder.toString();
	}

	public String getArrayMiddleObjVarName(int depth, String arrayName) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(arrayName);
		stringBuilder.append("$");
		stringBuilder.append(depth);
		stringBuilder.append("ArrayMiddleObject");
		return stringBuilder.toString();
	}

	public String getElementObjVarNameOfArrayMiddleObject(int depth, String arrayName) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(arrayName);
		stringBuilder.append("$");
		stringBuilder.append(depth);
		stringBuilder.append("MiddleWritableObject");
		return stringBuilder.toString();
	}	

	public String getArrayVarObjName(int depth, String arrayName) {
		StringBuilder stringBuilder = new StringBuilder(arrayName);
		stringBuilder.append("$");
		stringBuilder.append(depth);
		return stringBuilder.toString();
	}

	public String getArrayListVarObjName(int depth, String arrayName) {
		StringBuilder stringBuilder = new StringBuilder(getArrayVarObjName(depth, arrayName));		
		stringBuilder.append("List");
		return stringBuilder.toString();
	}

	public String getArrayListSizeVarObjName(int depth, String arrayName) {
		StringBuilder stringBuilder = new StringBuilder(getArrayListVarObjName(depth, arrayName));
		stringBuilder.append("Size");
		return stringBuilder.toString();
	}

	public String getGroupMiddleObjVarName(int depth, String groupName) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(groupName);
		stringBuilder.append("$");
		stringBuilder.append(depth);
		stringBuilder.append("WritableMiddleObject");
		return stringBuilder.toString();
	}
	
	public String getGroupVarObjName(int depth, String groupName) {
		StringBuilder stringBuilder = new StringBuilder(groupName);
		stringBuilder.append("$");
		stringBuilder.append(depth);
		return stringBuilder.toString();
	}

	public String buildStringOfSingleItemInfoPart(int depth, String path, String varNameOfSetOwner, String middleObjVarName,
			SingleItemInfo singleItemInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append(varNameOfSetOwner);
		stringBuilder.append(".set");
		stringBuilder.append(singleItemInfo.getFirstUpperItemName());
		stringBuilder.append("((");
		stringBuilder.append(singleItemInfo.getJavaLangClassCastingTypeOfItemType());
		stringBuilder.append(")");
		
		// singleItemDecoder.getValueFromReadableMiddleObject(sigleItemPath0
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()");		
		
		// , "byteVar1" // itemName
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append(", \"");
		stringBuilder.append(singleItemInfo.getItemName());
		stringBuilder.append("\" // itemName");
		
		// itemType
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append(", ");
		stringBuilder.append("kr.pe.sinnori.common.type.SingleItemType.");
		stringBuilder.append(SingleItemTypeManger.getInstance()
				.getSingleItemType(singleItemInfo.getItemTypeID()).name());
		stringBuilder.append(" // itemType");
		// , SingleItemType.UB_PASCAL_STRING
		
		// itemSize
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append(", ");
		stringBuilder.append(singleItemInfo.getItemSize());
		stringBuilder.append(" // itemSize");
		
		// itemCharset
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append(", ");				
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
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append(", ");
		stringBuilder.append(middleObjVarName);
		stringBuilder.append("));");
		
		return stringBuilder.toString();
	}
	
	public String buildStringOfArraySizeVarDeclarationPart(int depth, String varNameOfSetOwner, ArrayInfo arrayInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("int ");
		stringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
		stringBuilder.append(" = ");		
		/** 배열 크기 지정 방식에 따른 배열 크기 지정 */
		if (arrayInfo.getArrayCntType().equals("reference")) {
			stringBuilder.append(varNameOfSetOwner);
			stringBuilder.append(".get");
			stringBuilder.append(arrayInfo.getArrayCntValue().substring(0, 1).toUpperCase());
			stringBuilder.append(arrayInfo.getArrayCntValue().substring(1));
			stringBuilder.append("();");
		} else {
			stringBuilder.append(arrayInfo.getArrayCntValue());
			stringBuilder.append(";");
		}
		return stringBuilder.toString();
	}
	
	public String buildStringOfArrayInfoPart(int depth, String path, 
			String varvarNameOfSetOwner, String middleObjVarName,
			ArrayInfo arrayInfo) {
		String newPath = new StringBuilder(path).append(".").append(arrayInfo.getFirstUpperItemName()).toString();
				
		StringBuilder stringBuilder = new StringBuilder();
		
		/** 배열 크기 변수 선언및 정의 */
		// int memberListSize = allItemTypeReq.getCnt();
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);		
		stringBuilder.append(buildStringOfArraySizeVarDeclarationPart(depth, varvarNameOfSetOwner, arrayInfo));

		// Object memberMiddleReadArray = singleItemDecoder.getArrayObjFromMiddleReadObj(sigleItemPath0, "member", memberListSize, middleReadableObject);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("Object ");
		stringBuilder.append(getArrayMiddleObjVarName(depth, arrayInfo.getItemName()));
		
		stringBuilder.append(" = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(");
		// the parameter path
		stringBuilder.append("pathStack.peek()");
		stringBuilder.append(", ");
		// the parameter arrayName
		stringBuilder.append("\"");
		stringBuilder.append(arrayInfo.getItemName());		
		stringBuilder.append("\"");
		stringBuilder.append(", ");
		// the parameter arrayCntValue		
		stringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));				
		stringBuilder.append(", ");
		// the parameter readableMiddleObject
		stringBuilder.append(middleObjVarName);
		stringBuilder.append(");");
		
		/** 배열 변수 선언및 정의 */
		// List<AllDataType.Member> memberList = new ArrayList<AllDataType.Member>();
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("java.util.List<");
		stringBuilder.append(newPath);
		stringBuilder.append("> ");
		stringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
		stringBuilder.append(" = new java.util.ArrayList<");
		stringBuilder.append(newPath);
		stringBuilder.append(">();");
		
		// for (int i=0; i < memberListSize; i++) {
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("for (int ");
		stringBuilder.append(getCountVarName(depth));
		stringBuilder.append("=0; ");
		stringBuilder.append(getCountVarName(depth));
		stringBuilder.append(" < ");
		stringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
		stringBuilder.append("; ");
		stringBuilder.append(getCountVarName(depth));
		stringBuilder.append("++) {");
		
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("pathStack.push(new StringBuilder(pathStack.peek()).append(\".\").append(\"")
				.append(arrayInfo.getFirstUpperItemName())
				.append("\").append(\"[\").append(")
				.append(getCountVarName(depth))
				.append(").append(\"]\").toString());");
		
		// Object memberMiddleReadObj = singleItemDecoder.getMiddleReadObjFromArrayObj(sigleItemPath1, memberMiddleReadArray, i);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("Object ");
		stringBuilder.append(getElementObjVarNameOfArrayMiddleObject(depth, arrayInfo.getItemName()));
		stringBuilder.append("= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek()");		
		stringBuilder.append(", ");
		stringBuilder.append(getArrayMiddleObjVarName(depth, arrayInfo.getItemName()));
		stringBuilder.append(", ");
		stringBuilder.append(getCountVarName(depth));
		stringBuilder.append(");");
		
		// AllDataType.Member member = new AllDataType.Member();
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append(newPath);
		stringBuilder.append(" ");
		stringBuilder.append(getArrayVarObjName(depth, arrayInfo.getItemName()));
		stringBuilder.append(" = new ");
		stringBuilder.append(newPath);
		stringBuilder.append("();");
		
		// memberList.add(member);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
		stringBuilder.append(".add(");
		stringBuilder.append(getArrayVarObjName(depth, arrayInfo.getItemName()));
		stringBuilder.append(");");				
		
		stringBuilder.append(buildStringOfOrderedItemSetPart(depth+1
				, newPath
				, getArrayVarObjName(depth, arrayInfo.getItemName())
				, getElementObjVarNameOfArrayMiddleObject(depth, arrayInfo.getItemName())
				, arrayInfo.getOrderedItemSet()));	
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("pathStack.pop();");		
						
		// }
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("}");
		
		// allDataType.setMemberList(memberList);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append(varvarNameOfSetOwner);
		stringBuilder.append(".set");
		stringBuilder.append(arrayInfo.getFirstUpperItemName());
		stringBuilder.append("List(");
		stringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
		stringBuilder.append(");");	
		return stringBuilder.toString();
	}
	
	public String buildStringOfGroupInfoPart(int depth, String path, String varNameOfSetOwner, String middleObjVarName,
			GroupInfo groupInfo) {
		String newPath = new StringBuilder(path).append(".").append(groupInfo.getFirstUpperItemName()).toString();
		
		StringBuilder stringBuilder = new StringBuilder();
		/** 그룹 변수 선언  */
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append(newPath);
		stringBuilder.append(" ");
		stringBuilder.append(getGroupVarObjName(depth, groupInfo.getItemName()));
		stringBuilder.append(" = new ");
		stringBuilder.append(newPath);
		stringBuilder.append("();");
		
		/** 그룹 중간 객체 변수 선언 */
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("Object ");
		stringBuilder.append(getGroupMiddleObjVarName(depth, groupInfo.getItemName()));		
		stringBuilder.append(" = singleItemDecoder.getGroupMiddleObjectFromReadableMiddleObject(");
		// the parameter path
		stringBuilder.append("pathStack.peek()");
		stringBuilder.append(", ");
		// the parameter groupName
		stringBuilder.append("\"");
		stringBuilder.append(groupInfo.getItemName());		
		stringBuilder.append("\"");
		stringBuilder.append(", ");		
		// the parameter readableMiddleObject
		stringBuilder.append(middleObjVarName);
		stringBuilder.append(");");
		
		/** path stack push */
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("pathStack.push(new StringBuilder(pathStack.peek()).append(\".\").append(\"")
				.append(groupInfo.getFirstUpperItemName())
				.append("\").toString());");
		
		stringBuilder.append(buildStringOfOrderedItemSetPart(depth
				, newPath
				, getGroupVarObjName(depth, groupInfo.getItemName())
				, getGroupMiddleObjVarName(depth, groupInfo.getItemName())
				, groupInfo.getOrderedItemSet()));
		
		/** path stack pop */
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("pathStack.pop();");
		
		/** allItemTypeReq.setVip(vip$1);  */
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append(varNameOfSetOwner);
		stringBuilder.append(".set");
		stringBuilder.append(groupInfo.getFirstUpperItemName());
		stringBuilder.append("(");
		stringBuilder.append(getGroupVarObjName(depth, groupInfo.getItemName()));
		stringBuilder.append(");");
		
		return stringBuilder.toString();
	}
	
	public String buildStringOfOrderedItemSetPart(int depth, String path, String varNameOfSetOwner, String middleObjVarName, OrderedItemSet orderedItemSet) {
		if (depth < 0) {
			String errorMessage = String.format("the parameter depth[%d] is less than zero", depth);
			throw new IllegalArgumentException(errorMessage);
		}
		if (null == path) {
			String errorMessage = "the parameter path is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == varNameOfSetOwner) {
			String errorMessage = "the parameter varNameOfSetOwner is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == middleObjVarName) {
			String errorMessage = "the parameter middleObjVarName is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == orderedItemSet) {
			String errorMessage = "the parameter orderedItemSet is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		
		List<AbstractItemInfo> itemInfoList = orderedItemSet.getItemInfoList();
		for (AbstractItemInfo itemInfo:itemInfoList) {
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			ItemInfoType itemInfoType = itemInfo.getItemInfoType();
			
			switch (itemInfoType) {
				case SINGLE: {
					SingleItemInfo singleItemInfo = (SingleItemInfo) itemInfo;
					stringBuilder.append(buildStringOfSingleItemInfoPart(depth, path, varNameOfSetOwner, middleObjVarName, singleItemInfo));
					break;
				}
				case ARRAY: {
					ArrayInfo arrayInfo = (ArrayInfo) itemInfo;
					stringBuilder.append(buildStringOfArrayInfoPart(depth, path, varNameOfSetOwner, middleObjVarName, arrayInfo));
					break;
				}
				case GROUP: {
					GroupInfo groupInfo = (GroupInfo) itemInfo;
					stringBuilder.append(buildStringOfGroupInfoPart(depth, path, varNameOfSetOwner, middleObjVarName, groupInfo));
					break;
				}
				default: {
					log.error("unknwon item type[{}]", itemInfoType.toString());
					System.exit(1);
				}
			}
		}
		
		return stringBuilder.toString();
	}
	
	public String buildStringOfFileContents(String author,
			kr.pe.sinnori.common.message.builder.info.MessageInfo messageInfo) {
		
		final String middleObjVarName = "middleReadableObject";
		final int depth = 0;

		String messageID = messageInfo.getMessageID();
		String firstLowerMessageID = messageInfo.getFirstLowerMessageID();		
		
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(buildStringOfLincensePart());
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPackagePartString(messageID));		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				
		String importElements[] = {
				"import kr.pe.sinnori.common.exception.BodyFormatException;",
				"import kr.pe.sinnori.common.message.AbstractMessage;",		
				"import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;",
				"import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;"
		};
		stringBuilder.append(buildStringOfImportPart(importElements));
		
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(buildStringOfFileDescriptionPart(messageID, author, "메시지 디코더"));
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("public final class ");
		stringBuilder.append(messageID);
		stringBuilder.append("Decoder extends AbstractMessageDecoder {");
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("@Override");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws OutOfMemoryError, BodyFormatException {");
		
		// AllDataType allDataType = new AllDataType();
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
		stringBuilder.append(messageID);
		stringBuilder.append(" ");
		stringBuilder.append(firstLowerMessageID);
		stringBuilder.append(" = new ");
		stringBuilder.append(messageID);
		stringBuilder.append("();");
		
		if (! messageInfo.getOrderedItemSet().getItemInfoList().isEmpty()) {
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
			/** java.util.Stack is thread-safe but LinkedList is not thread-safe */
			stringBuilder.append("java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();");

			// pathStack.push("AllItemTypeReq");
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
			stringBuilder.append("pathStack.push(");
			stringBuilder.append("\"");
			stringBuilder.append(messageID);
			stringBuilder.append("\");");
			
			stringBuilder.append(buildStringOfOrderedItemSetPart(2, messageID, firstLowerMessageID, middleObjVarName, messageInfo.getOrderedItemSet()));
			
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);			
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
			stringBuilder.append("pathStack.pop();");
		}		
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		// return allDataType;
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
		stringBuilder.append("return ");
		stringBuilder.append(firstLowerMessageID);
		stringBuilder.append(";");
		
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("}");
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("}");
		
		return stringBuilder.toString();
	}

}
