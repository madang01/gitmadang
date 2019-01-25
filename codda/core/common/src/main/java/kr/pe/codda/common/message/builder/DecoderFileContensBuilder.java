package kr.pe.codda.common.message.builder;

import java.util.List;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.builder.info.AbstractItemInfo;
import kr.pe.codda.common.message.builder.info.ArrayInfo;
import kr.pe.codda.common.message.builder.info.GroupInfo;
import kr.pe.codda.common.message.builder.info.MessageInfo;
import kr.pe.codda.common.message.builder.info.OrderedItemSet;
import kr.pe.codda.common.message.builder.info.SingleItemInfo;
import kr.pe.codda.common.message.builder.info.SingleItemTypeManger;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;
import kr.pe.codda.common.type.ItemInfoType;
import kr.pe.codda.common.type.SingleItemType;
import kr.pe.codda.common.util.CommonStaticUtil;


public class DecoderFileContensBuilder extends AbstractSourceFileBuildre {

	public String getCountVarName(int depth) {
		StringBuilder countVarNameBuilder = new StringBuilder();
		countVarNameBuilder.append("i");
		countVarNameBuilder.append(depth);
		return countVarNameBuilder.toString();
	}

	public String getArrayMiddleObjVarName(int depth, String arrayName) {
		StringBuilder contetnsStringBuilder = new StringBuilder();
		contetnsStringBuilder.append(arrayName);
		contetnsStringBuilder.append("$");
		contetnsStringBuilder.append(depth);
		contetnsStringBuilder.append("ArrayMiddleObject");
		return contetnsStringBuilder.toString();
	}

	public String getElementObjVarNameOfArrayMiddleObject(int depth, String arrayName) {
		StringBuilder contetnsStringBuilder = new StringBuilder();
		contetnsStringBuilder.append(arrayName);
		contetnsStringBuilder.append("$");
		contetnsStringBuilder.append(depth);
		contetnsStringBuilder.append("MiddleWritableObject");
		return contetnsStringBuilder.toString();
	}	

	public String getArrayVarObjName(int depth, String arrayName) {
		StringBuilder contetnsStringBuilder = new StringBuilder(arrayName);
		contetnsStringBuilder.append("$");
		contetnsStringBuilder.append(depth);
		return contetnsStringBuilder.toString();
	}

	public String getArrayListVarObjName(int depth, String arrayName) {
		StringBuilder contetnsStringBuilder = new StringBuilder(getArrayVarObjName(depth, arrayName));		
		contetnsStringBuilder.append("List");
		return contetnsStringBuilder.toString();
	}

	public String getArrayListSizeVarObjName(int depth, String arrayName) {
		StringBuilder contetnsStringBuilder = new StringBuilder(getArrayListVarObjName(depth, arrayName));
		contetnsStringBuilder.append("Size");
		return contetnsStringBuilder.toString();
	}

	public String getGroupMiddleObjVarName(int depth, String groupName) {
		StringBuilder contetnsStringBuilder = new StringBuilder();
		contetnsStringBuilder.append(groupName);
		contetnsStringBuilder.append("$");
		contetnsStringBuilder.append(depth);
		contetnsStringBuilder.append("WritableMiddleObject");
		return contetnsStringBuilder.toString();
	}
	
	public String getGroupVarObjName(int depth, String groupName) {
		StringBuilder contetnsStringBuilder = new StringBuilder(groupName);
		contetnsStringBuilder.append("$");
		contetnsStringBuilder.append(depth);
		return contetnsStringBuilder.toString();
	}

	public void addSingleItemInfoPart(StringBuilder contetnsStringBuilder, int depth, String path, String varNameOfSetOwner, String middleObjVarName,
			SingleItemInfo singleItemInfo) {		
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append(varNameOfSetOwner);
		contetnsStringBuilder.append(".set");
		contetnsStringBuilder.append(singleItemInfo.getFirstUpperItemName());
		contetnsStringBuilder.append("((");
		contetnsStringBuilder.append(singleItemInfo.getJavaLangClassCastingTypeOfItemType());
		contetnsStringBuilder.append(")");
		
		// singleItemDecoder.getValueFromReadableMiddleObject(sigleItemPath0
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()");		
		
		// , "byteVar1" // itemName
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append(", \"");
		contetnsStringBuilder.append(singleItemInfo.getItemName());
		contetnsStringBuilder.append("\" // itemName");
		
		// itemType
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append(", ");
		contetnsStringBuilder.append(SingleItemType.class.getName());
		contetnsStringBuilder.append(".");
		
		contetnsStringBuilder.append(SingleItemTypeManger.getInstance()
				.getSingleItemType(singleItemInfo.getItemTypeID()).name());
		contetnsStringBuilder.append(" // itemType");
		// , SingleItemType.UB_PASCAL_STRING
		
		// itemSize
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append(", ");
		contetnsStringBuilder.append(singleItemInfo.getItemSize());
		contetnsStringBuilder.append(" // itemSize");
		
		// itemCharset
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append(", ");				
		/*SingleItemType itemType = singleItemInfo.getItemType();
		if (itemType.equals(SingleItemType.FIXED_LENGTH_STRING) ||
				itemType.equals(SingleItemType.UB_PASCAL_STRING) ||
				itemType.equals(SingleItemType.US_PASCAL_STRING) ||
				itemType.equals(SingleItemType.SI_PASCAL_STRING)) {*/
		String nativeItemCharset = singleItemInfo.getNativeItemCharset();
		if (null == nativeItemCharset) {
			contetnsStringBuilder.append("null");
		} else {
			contetnsStringBuilder.append("\"");
			contetnsStringBuilder.append(nativeItemCharset);
			contetnsStringBuilder.append("\"");
		}				
		contetnsStringBuilder.append(" // nativeItemCharset");
		
		
		// , middleReadableObject));
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append(", ");
		contetnsStringBuilder.append(middleObjVarName);
		contetnsStringBuilder.append("));");
	}
	
	public void addArraySizeVarDeclarationPart(StringBuilder contetnsStringBuilder, int depth, String varNameOfSetOwner, ArrayInfo arrayInfo) {		
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("int ");
		contetnsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
		contetnsStringBuilder.append(" = ");		
		/** 배열 크기 지정 방식에 따른 배열 크기 지정 */
		if (arrayInfo.getArrayCntType().equals("reference")) {
			contetnsStringBuilder.append(varNameOfSetOwner);
			contetnsStringBuilder.append(".get");
			contetnsStringBuilder.append(arrayInfo.getArrayCntValue().substring(0, 1).toUpperCase());
			contetnsStringBuilder.append(arrayInfo.getArrayCntValue().substring(1));
			contetnsStringBuilder.append("();");
		} else {
			contetnsStringBuilder.append(arrayInfo.getArrayCntValue());
			contetnsStringBuilder.append(";");
		}
	}
	
	public void addArraySizeCheckPart(StringBuilder contetnsStringBuilder, int depth, String varNameOfSetOwner, ArrayInfo arrayInfo) {	
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("if (");
		contetnsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
		contetnsStringBuilder.append(" < 0) {");
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));	
		contetnsStringBuilder.append("String errorMessage = new StringBuilder(\"the var ");
		contetnsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
		contetnsStringBuilder.append(" is less than zero\").toString();");

		// throw new BodyFormatException(errorMessage);
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append("throw new ");
		contetnsStringBuilder.append(BodyFormatException.class.getName());
		contetnsStringBuilder.append("(errorMessage);");
		
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("}");
	}
	
	public void addArrayInfoPart(StringBuilder contetnsStringBuilder, int depth, String path, 
			String varvarNameOfSetOwner, String middleObjVarName,
			ArrayInfo arrayInfo) {
		String newPath = new StringBuilder(path).append(".").append(arrayInfo.getFirstUpperItemName()).toString();
		
		/** 배열 크기 변수 선언및 정의 */
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);		
		addArraySizeVarDeclarationPart(contetnsStringBuilder, depth, varvarNameOfSetOwner, arrayInfo);
		
		/** 양수인 배열 크기 검사 */
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		addArraySizeCheckPart(contetnsStringBuilder, depth, varvarNameOfSetOwner, arrayInfo);
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		// Object memberMiddleReadArray = singleItemDecoder.getArrayObjFromMiddleReadObj(sigleItemPath0, "member", memberListSize, middleReadableObject);
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("Object ");
		contetnsStringBuilder.append(getArrayMiddleObjVarName(depth, arrayInfo.getItemName()));
		
		contetnsStringBuilder.append(" = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(");
		// the parameter path
		contetnsStringBuilder.append("pathStack.peek()");
		contetnsStringBuilder.append(", ");
		// the parameter arrayName
		contetnsStringBuilder.append("\"");
		contetnsStringBuilder.append(arrayInfo.getItemName());		
		contetnsStringBuilder.append("\"");
		contetnsStringBuilder.append(", ");
		// the parameter arrayCntValue		
		contetnsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));				
		contetnsStringBuilder.append(", ");
		// the parameter readableMiddleObject
		contetnsStringBuilder.append(middleObjVarName);
		contetnsStringBuilder.append(");");
		
		/** 배열 변수 선언및 정의 */
		// List<AllDataType.Member> memberList = new ArrayList<AllDataType.Member>();
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("java.util.List<");
		contetnsStringBuilder.append(newPath);
		contetnsStringBuilder.append("> ");
		contetnsStringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
		contetnsStringBuilder.append(" = new java.util.ArrayList<");
		contetnsStringBuilder.append(newPath);
		contetnsStringBuilder.append(">();");
		
		// for (int i=0; i < memberListSize; i++) {
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("for (int ");
		contetnsStringBuilder.append(getCountVarName(depth));
		contetnsStringBuilder.append("=0; ");
		contetnsStringBuilder.append(getCountVarName(depth));
		contetnsStringBuilder.append(" < ");
		contetnsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
		contetnsStringBuilder.append("; ");
		contetnsStringBuilder.append(getCountVarName(depth));
		contetnsStringBuilder.append("++) {");
		
		
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append("pathStack.push(new StringBuilder(pathStack.peek()).append(\".\").append(\"")
				.append(arrayInfo.getFirstUpperItemName())
				.append("\").append(\"[\").append(")
				.append(getCountVarName(depth))
				.append(").append(\"]\").toString());");
		
		// Object memberMiddleReadObj = singleItemDecoder.getMiddleReadObjFromArrayObj(sigleItemPath1, memberMiddleReadArray, i);
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append("Object ");
		contetnsStringBuilder.append(getElementObjVarNameOfArrayMiddleObject(depth, arrayInfo.getItemName()));
		contetnsStringBuilder.append("= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek()");		
		contetnsStringBuilder.append(", ");
		contetnsStringBuilder.append(getArrayMiddleObjVarName(depth, arrayInfo.getItemName()));
		contetnsStringBuilder.append(", ");
		contetnsStringBuilder.append(getCountVarName(depth));
		contetnsStringBuilder.append(");");
		
		// AllDataType.Member member = new AllDataType.Member();
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append(newPath);
		contetnsStringBuilder.append(" ");
		contetnsStringBuilder.append(getArrayVarObjName(depth, arrayInfo.getItemName()));
		contetnsStringBuilder.append(" = new ");
		contetnsStringBuilder.append(newPath);
		contetnsStringBuilder.append("();");
		
		// memberList.add(member);
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
		contetnsStringBuilder.append(".add(");
		contetnsStringBuilder.append(getArrayVarObjName(depth, arrayInfo.getItemName()));
		contetnsStringBuilder.append(");");				
		
		addOrderedItemSetPart(contetnsStringBuilder, depth+1
				, newPath
				, getArrayVarObjName(depth, arrayInfo.getItemName())
				, getElementObjVarNameOfArrayMiddleObject(depth, arrayInfo.getItemName())
				, arrayInfo.getOrderedItemSet());	
		
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append("pathStack.pop();");		
						
		// }
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("}");
		
		// allDataType.setMemberList(memberList);
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append(varvarNameOfSetOwner);
		contetnsStringBuilder.append(".set");
		contetnsStringBuilder.append(arrayInfo.getFirstUpperItemName());
		contetnsStringBuilder.append("List(");
		contetnsStringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
		contetnsStringBuilder.append(");");
	}
	
	public void addGroupInfoPart(StringBuilder contetnsStringBuilder, int depth, String path, String varNameOfSetOwner, String middleObjVarName,
			GroupInfo groupInfo) {
		String newPath = new StringBuilder(path).append(".").append(groupInfo.getFirstUpperItemName()).toString();
		
		/** 그룹 변수 선언  */
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append(newPath);
		contetnsStringBuilder.append(" ");
		contetnsStringBuilder.append(getGroupVarObjName(depth, groupInfo.getItemName()));
		contetnsStringBuilder.append(" = new ");
		contetnsStringBuilder.append(newPath);
		contetnsStringBuilder.append("();");
		
		/** 그룹 중간 객체 변수 선언 */
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("Object ");
		contetnsStringBuilder.append(getGroupMiddleObjVarName(depth, groupInfo.getItemName()));		
		contetnsStringBuilder.append(" = singleItemDecoder.getGroupMiddleObjectFromReadableMiddleObject(");
		// the parameter path
		contetnsStringBuilder.append("pathStack.peek()");
		contetnsStringBuilder.append(", ");
		// the parameter groupName
		contetnsStringBuilder.append("\"");
		contetnsStringBuilder.append(groupInfo.getItemName());		
		contetnsStringBuilder.append("\"");
		contetnsStringBuilder.append(", ");		
		// the parameter readableMiddleObject
		contetnsStringBuilder.append(middleObjVarName);
		contetnsStringBuilder.append(");");
		
		/** path stack push */
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("pathStack.push(new StringBuilder(pathStack.peek()).append(\".\").append(\"")
				.append(groupInfo.getFirstUpperItemName())
				.append("\").toString());");
		
		addOrderedItemSetPart(contetnsStringBuilder, depth
				, newPath
				, getGroupVarObjName(depth, groupInfo.getItemName())
				, getGroupMiddleObjVarName(depth, groupInfo.getItemName())
				, groupInfo.getOrderedItemSet());
		
		/** path stack pop */
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("pathStack.pop();");
		
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append(varNameOfSetOwner);
		contetnsStringBuilder.append(".set");
		contetnsStringBuilder.append(groupInfo.getFirstUpperItemName());
		contetnsStringBuilder.append("(");
		contetnsStringBuilder.append(getGroupVarObjName(depth, groupInfo.getItemName()));
		contetnsStringBuilder.append(");");
	}
	
	public void addOrderedItemSetPart(StringBuilder contetnsStringBuilder, int depth, String path, String varNameOfSetOwner, String middleObjVarName, OrderedItemSet orderedItemSet) {
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
		
		
		List<AbstractItemInfo> itemInfoList = orderedItemSet.getItemInfoList();
		for (AbstractItemInfo itemInfo:itemInfoList) {
			contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			ItemInfoType itemInfoType = itemInfo.getItemInfoType();
			
			switch (itemInfoType) {
				case SINGLE: {
					SingleItemInfo singleItemInfo = (SingleItemInfo) itemInfo;
					addSingleItemInfoPart(contetnsStringBuilder, depth, path, varNameOfSetOwner, middleObjVarName, singleItemInfo);
					break;
				}
				case ARRAY: {
					ArrayInfo arrayInfo = (ArrayInfo) itemInfo;
					addArrayInfoPart(contetnsStringBuilder, depth, path, varNameOfSetOwner, middleObjVarName, arrayInfo);
					break;
				}
				case GROUP: {
					GroupInfo groupInfo = (GroupInfo) itemInfo;
					addGroupInfoPart(contetnsStringBuilder, depth, path, varNameOfSetOwner, middleObjVarName, groupInfo);
					break;
				}
				default: {
					log.error("unknwon item type[{}]", itemInfoType.toString());
					System.exit(1);
				}
			}
		}
	}
	
	public String buildStringOfFileContents(String author,
			MessageInfo messageInfo) {
		
		final String middleObjVarName = "middleReadableObject";
		final int depth = 0;

		String messageID = messageInfo.getMessageID();
		String firstLowerMessageID = messageInfo.getFirstLowerMessageID();		
		
		StringBuilder contetnsStringBuilder = new StringBuilder();
		
		// contetnsStringBuilder.append(buildStringOfLincensePart());
		addLincensePart(contetnsStringBuilder);
		
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		addPackageDeclarationPart(contetnsStringBuilder, messageID);		
		
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);		
		Class<?> importClazzes[] = {
				BodyFormatException.class,
				AbstractMessage.class,
				AbstractMessageDecoder.class,
				SingleItemDecoderIF.class
		};
		addImportDeclarationsPart(contetnsStringBuilder, importClazzes);		
		
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		addSourceFileDescriptionPart(contetnsStringBuilder, messageID, author, "message decoder");
		
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append("public final class ");
		contetnsStringBuilder.append(messageID);
		contetnsStringBuilder.append("Decoder extends AbstractMessageDecoder {");
		
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append("@Override");
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append("protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {");
		
		// AllDataType allDataType = new AllDataType();
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
		contetnsStringBuilder.append(messageID);
		contetnsStringBuilder.append(" ");
		contetnsStringBuilder.append(firstLowerMessageID);
		contetnsStringBuilder.append(" = new ");
		contetnsStringBuilder.append(messageID);
		contetnsStringBuilder.append("();");
		
		if (! messageInfo.getOrderedItemSet().getItemInfoList().isEmpty()) {
			contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
			/** java.util.Stack is thread-safe but LinkedList is not thread-safe */
			contetnsStringBuilder.append("java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();");

			contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
			contetnsStringBuilder.append("pathStack.push(");
			contetnsStringBuilder.append("\"");
			contetnsStringBuilder.append(messageID);
			contetnsStringBuilder.append("\");");
			
			addOrderedItemSetPart(contetnsStringBuilder, 2, messageID, firstLowerMessageID, middleObjVarName, messageInfo.getOrderedItemSet());
			
			contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);			
			contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
			contetnsStringBuilder.append("pathStack.pop();");
		}		
		
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		// return allDataType;
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
		contetnsStringBuilder.append("return ");
		contetnsStringBuilder.append(firstLowerMessageID);
		contetnsStringBuilder.append(";");
		
		
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append("}");
		
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("}");
		
		return contetnsStringBuilder.toString();
	}

}
