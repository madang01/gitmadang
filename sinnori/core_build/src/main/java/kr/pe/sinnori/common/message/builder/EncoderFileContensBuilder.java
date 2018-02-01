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

public class EncoderFileContensBuilder extends AbstractSourceFileBuildre {

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

	public String getReferenceVariableGetMethodString(String varNameOfSetOwner, String referenceCountVarName) {
		if (referenceCountVarName.length() < 2) {
			String errorMessage = String.format("the character number of the parameter referenceCountVarName[%s] is less than two", referenceCountVarName);
			throw new IllegalArgumentException(errorMessage);
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(varNameOfSetOwner);
		stringBuilder.append(".get");
		stringBuilder.append(referenceCountVarName.substring(0, 1).toUpperCase());
		stringBuilder.append(referenceCountVarName.substring(1));
		stringBuilder.append("()");
		return stringBuilder.toString();
	}

	public String buildStringOfSingleItemInfoPart(int depth, String path, String varNameOfSetOwner, String middleObjVarName,
			SingleItemInfo singleItemInfo) {;
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("singleItemEncoder.putValueToWritableMiddleObject(");
		// the Parameter path
		stringBuilder.append("pathStack.peek()");
		stringBuilder.append(", \"");
		// the parameter itemName
		stringBuilder.append(singleItemInfo.getItemName());
		stringBuilder.append("\"");
		// the parameter singleItemType		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append(", ");
		stringBuilder.append("SingleItemType.");
		stringBuilder
				.append(SingleItemTypeManger.getInstance().getSingleItemType(singleItemInfo.getItemTypeID()).name());
		stringBuilder.append(" // itemType");

		// the parameter itemValue
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append(", ");
		stringBuilder.append(varNameOfSetOwner);
		stringBuilder.append(".get");
		stringBuilder.append(singleItemInfo.getFirstUpperItemName());
		stringBuilder.append("() // itemValue");

		// the parameter itemSize
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append(", ");
		stringBuilder.append(singleItemInfo.getItemSize());
		stringBuilder.append(" // itemSize");

		// the parameter nativeItemCharset
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append(", ");
		String nativeItemCharset = singleItemInfo.getNativeItemCharset();
		if (null == nativeItemCharset) {
			stringBuilder.append("null");
		} else {
			stringBuilder.append("\"");
			stringBuilder.append(nativeItemCharset);
			stringBuilder.append("\"");
		}
		stringBuilder.append(" // nativeItemCharset");
		
		// the parameter writableMiddleObject
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append(", ");
		stringBuilder.append(middleObjVarName);
		stringBuilder.append(");");

		return stringBuilder.toString();
	}
	
	public String buildStringOfPartWhoseListIsNullAtArray(int depth, String varNameOfSetOwner, ArrayInfo arrayInfo) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */");

		if (arrayInfo.getArrayCntType().equals("reference")) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			/**
			 * <pre>
			 * if (0 != <변수명>.get<첫문자자가 대문자인 참조변수명>()) {
			 * </pre>
			 */

			// if (0 != allDataTypeInObj.getCnt()) {
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
			stringBuilder.append("if (0 != ");			
			stringBuilder.append(getReferenceVariableGetMethodString(varNameOfSetOwner, arrayInfo.getArrayCntValue()));
			stringBuilder.append(") {");			
			
			
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			stringBuilder.append(getPrefixWithTabCharacters(depth, 2));			
			stringBuilder.append("String errorMessage = new StringBuilder(\"the var ");
			stringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
			stringBuilder.append(" is null but the value referenced by the array size[");
			stringBuilder.append(getReferenceVariableGetMethodString(varNameOfSetOwner, arrayInfo.getArrayCntValue()));
			stringBuilder.append("][\").append(");
			stringBuilder.append(getReferenceVariableGetMethodString(varNameOfSetOwner, arrayInfo.getArrayCntValue()));
			stringBuilder.append(").append(\"] is not zero\").toString();");

			// throw new BodyFormatException(errorMessage);
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			stringBuilder.append(getPrefixWithTabCharacters(depth, 2));
			stringBuilder.append("throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);");

			// }
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
			stringBuilder.append("}");

		} else {
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
			stringBuilder.append("if (0 != ");
			stringBuilder.append(arrayInfo.getArrayCntValue());
			stringBuilder.append(") {");
			
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			stringBuilder.append(getPrefixWithTabCharacters(depth, 2));
			// String errorMessage = new StringBuilder("the var member$1List is null but the value defined by array size[3] is not zero").toString();
			/*stringBuilder.append("String errorMessage = new StringBuilder(\"the var \").append(\"");
			stringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
			stringBuilder.append("\").append(\"is null but the value defined by array size\").append(\"[");
			stringBuilder.append(arrayInfo.getArrayCntValue());
			stringBuilder.append("] is not zero\").toString();");*/
			
			stringBuilder.append("String errorMessage = new StringBuilder(\"the var ");
			stringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
			stringBuilder.append(" is null but the value defined by array size[");
			stringBuilder.append(arrayInfo.getArrayCntValue());
			stringBuilder.append("] is not zero\").toString();");
			

			// throw new BodyFormatException(errorMessage);
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			stringBuilder.append(getPrefixWithTabCharacters(depth, 2));
			stringBuilder.append("throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);");

			// }
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
			stringBuilder.append("}");
		}

		return stringBuilder.toString();
	}

	/** 배열 크기가 메시지 정보에서 정의한 배열 크기와 같은지 검사 */
	public String buildStringOfPartCheckingListSizeIsValid(int depth, String varNameOfSetOwner, ArrayInfo arrayInfo) {
		StringBuilder stringBuilder = new StringBuilder();

		// if (memberListSize != allDataTypeInObj.getCnt()) {
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("if (");		
		if (arrayInfo.getArrayCntType().equals("reference")) {
			stringBuilder.append(getReferenceVariableGetMethodString(varNameOfSetOwner, arrayInfo.getArrayCntValue()));			
		} else {
			stringBuilder.append(arrayInfo.getArrayCntValue());
		}		
		stringBuilder.append(" != ");
		stringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getArrayName()));
		stringBuilder.append(") {");

		// String errorMessage = new StringBuilder(allDataTypeInObjSingleItemPath)
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 2));
		
		if (arrayInfo.getArrayCntType().equals("reference")) {
			// String errorMessage = new StringBuilder("the var member$1ListSize[").append(member$1ListSize).append("] is not same to the value referenced by the array size[allItemTypeReq.getCnt()][").append(allItemTypeReq.getCnt()).append("]").toString();			
			stringBuilder.append("String errorMessage = new StringBuilder(\"the var ");
			stringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
			stringBuilder.append("[\").append(");
			stringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
			stringBuilder.append(").append(\"] is not same to the value referenced by the array size[");
			stringBuilder.append(getReferenceVariableGetMethodString(varNameOfSetOwner, arrayInfo.getArrayCntValue()));
			stringBuilder.append("][\").append(");
			stringBuilder.append(getReferenceVariableGetMethodString(varNameOfSetOwner, arrayInfo.getArrayCntValue()));
			stringBuilder.append(").append(\"]\").toString();");
			
		} else {
			// String errorMessage = new StringBuilder("the var member$1ListSize[").append(member$1ListSize).append("] is not same to the value defined by array size[3]").toString();
			stringBuilder.append("String errorMessage = new StringBuilder(\"the var ");
			stringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
			stringBuilder.append("[\").append(");
			stringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
			stringBuilder.append(").append(\"] is not same to the value defined by array size[");
			stringBuilder.append(arrayInfo.getArrayCntValue());
			stringBuilder.append("]\").toString();");
			
		}

		// throw new BodyFormatException(errorMessage);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 2));
		stringBuilder.append("throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);");

		// }
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("}");

		return stringBuilder.toString();
	}

	public String buildStringOfPartWhoseListIsNotNullAtArray(int depth, String path, String nameOfSetOwner,
			String middleObjVarName, ArrayInfo arrayInfo) {
		String newPath = new StringBuilder(path).append(".").append(arrayInfo.getFirstUpperItemName()).toString();

		StringBuilder stringBuilder = new StringBuilder();

		/** 배열 변수 선언및 정의 */
		// int memberListSize = memberList.size();
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("int ");
		stringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
		stringBuilder.append(" = ");
		stringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
		stringBuilder.append(".size();");

		/** 배열 값이 null 이 아닐때에는 배열 크기가 메시지 정보에서 정의한 배열 크기와 같은지 검사 */
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */");

		/** 배열 크기가 메시지 정보에서 정의한 배열 크기와 같은지 검사 */
		stringBuilder.append(buildStringOfPartCheckingListSizeIsValid(depth, nameOfSetOwner, arrayInfo));

		stringBuilder.append(CommonStaticFinalVars.NEWLINE);

		/** 이 배열을 위한 중간 객체 가져오기 */
		// Object memberArrayMiddleObject =
		// singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(allDataTypeInObjSingleItemPath,
		// "member", memberList.length, middleWritableObject);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("Object ");
		/*
		 * stringBuilder.append(arrayInfo.getItemName());
		 * stringBuilder.append("MiddleWriteArray");
		 */
		stringBuilder.append(getArrayMiddleObjVarName(depth, arrayInfo.getItemName()));
		stringBuilder.append(" = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(");		
		// the parameter path
		stringBuilder.append("pathStack.peek()");
		stringBuilder.append(", ");
		// the parameter arrayName
		stringBuilder.append("\"");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("\", ");
		// the parameter arrayCntValue
		stringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
		stringBuilder.append(", ");
		// the parameter writableMiddleObject
		stringBuilder.append(middleObjVarName);
		stringBuilder.append(");");

		// for (int i=0; i < memberListSize; i++) {
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("for (int ");
		stringBuilder.append(getCountVarName(depth));
		stringBuilder.append("=0; ");
		stringBuilder.append(getCountVarName(depth));
		stringBuilder.append(" < ");
		stringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
		stringBuilder.append("; ");
		stringBuilder.append(getCountVarName(depth));
		stringBuilder.append("++) {");

		
		// pathStack.push(newStringBuilder(pathStack.peek()).append(".").append("Member").append("[").append(i).append("]").toString());
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 2));
		stringBuilder.append("pathStack.push(new StringBuilder(pathStack.peek()).append(\".\").append(\"")
				.append(arrayInfo.getFirstUpperItemName())
				.append("\").append(\"[\").append(")
				.append(getCountVarName(depth))
				.append(").append(\"]\").toString());");

		// Object memberMiddleWriteObj = singleItemEncoder.getMiddleWriteObjFromArrayObj(memberSingleItemPath, memberMiddleWriteArray, i);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 2));
		stringBuilder.append("Object ");
		stringBuilder.append(getElementObjVarNameOfArrayMiddleObject(depth, arrayInfo.getItemName()));
		stringBuilder.append(" = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(");	
		// the parameter path
		stringBuilder.append("pathStack.peek(), ");
		// the parameter arrayObj
		stringBuilder.append(getArrayMiddleObjVarName(depth, arrayInfo.getItemName()));
		stringBuilder.append(", ");
		// the parameter inx
		stringBuilder.append(getCountVarName(depth));
		stringBuilder.append(");");

		// AllDataType.Member member = memberList.get(i);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 2));
		/*
		 * stringBuilder.append(path); stringBuilder.append(".");
		 * stringBuilder.append(arrayInfo.getFirstUpperItemName());
		 */
		stringBuilder.append(newPath);
		stringBuilder.append(" ");
		stringBuilder.append(getArrayVarObjName(depth, arrayInfo.getItemName()));
		stringBuilder.append(" = ");
		stringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
		stringBuilder.append(".get(");
		stringBuilder.append(getCountVarName(depth));
		stringBuilder.append(");");

		stringBuilder.append(buildStringOfOrderedItemSetPart(depth + 2
				, newPath
				, getArrayVarObjName(depth, arrayInfo.getItemName())
				, getElementObjVarNameOfArrayMiddleObject(depth, arrayInfo.getItemName())
				, arrayInfo.getOrderedItemSet()));

		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		// pathStack.pop();
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 2));
		stringBuilder.append("pathStack.pop();");

		// }
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("}");
		
		return stringBuilder.toString();
	}

	public String buildStringOfArrayInfoPart(int depth, String path, String varNameOfSetOwner, String middleObjVarName,
			ArrayInfo arrayInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		
		/** 배열 변수 선언및 정의 */
		// java.util.List<AllItemTypeReq.Member> member$0List =allItemTypeReq.getMemberList();
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("java.util.List<");
		stringBuilder.append(path);
		stringBuilder.append(".");
		stringBuilder.append(arrayInfo.getFirstUpperItemName());
		stringBuilder.append("> ");
		stringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
		stringBuilder.append(" = ");
		stringBuilder.append(varNameOfSetOwner);
		stringBuilder.append(".get");
		stringBuilder.append(arrayInfo.getFirstUpperItemName());
		stringBuilder.append("List();");

		/** 배열 정보와 배열 크기 일치 검사 */
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);

		/** 주석 */
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("/** 배열 정보와 배열 크기 일치 검사 */");

		/** if (null == memberList) { */
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("if (null == ");
		stringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
		stringBuilder.append(") {");

		stringBuilder.append(buildStringOfPartWhoseListIsNullAtArray(depth, varNameOfSetOwner, arrayInfo));

		// } else {
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("} else {");

		// buildStringOfPartWhoseListIsNotNullAtArray
		stringBuilder.append(buildStringOfPartWhoseListIsNotNullAtArray(depth, path, varNameOfSetOwner,
				middleObjVarName, arrayInfo));				

		// }
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("}");

		return stringBuilder.toString();
	}
	
	public String buildStringOfGroupInfoPart(int depth, String path, String varNameOfSetOwner, String middleObjVarName,
			GroupInfo groupInfo) {

		String newPath = new StringBuilder(path).append(".").append(groupInfo.getFirstUpperItemName()).toString();

		StringBuilder stringBuilder = new StringBuilder();		

		/** 변수 선언 */
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 0));		
		stringBuilder.append(newPath);
		stringBuilder.append(" ");
		stringBuilder.append(getGroupVarObjName(depth, groupInfo.getItemName()));
		stringBuilder.append(" = ");
		stringBuilder.append(varNameOfSetOwner);
		stringBuilder.append(".get");
		stringBuilder.append(groupInfo.getFirstUpperItemName());
		stringBuilder.append("();");
		
		/** if (null == group1$2) { */
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("if (null == ");
		stringBuilder.append(getGroupVarObjName(depth, groupInfo.getItemName()));
		stringBuilder.append(") {");		
		/** 	String errorMessage = "the var group1$1 is null"; */
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("String errorMessage = \"the var ");
		stringBuilder.append(getGroupVarObjName(depth, groupInfo.getItemName()));
		stringBuilder.append(" is null\";");		
		/** 	throw new BodyFormatException(errorMessage); */
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 1));		 
		stringBuilder.append("throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);");
		/** } */
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("}");		
		
		/** group 쓰기 가능한 중간 객체 얻기 */
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("Object ");
		stringBuilder.append(getGroupMiddleObjVarName(depth, groupInfo.getItemName()));
		stringBuilder.append(" = singleItemEncoder.getGroupMiddleObjectFromWritableMiddleObject(");
		// the parameter path
		stringBuilder.append("pathStack.peek(), ");
		// the parameter groupName
		stringBuilder.append("\"");
		stringBuilder.append(groupInfo.getItemName());
		stringBuilder.append("\", ");
		// the parameter writableMiddleObject
		stringBuilder.append(middleObjVarName);
		stringBuilder.append(");");
		
		/** path stack push */
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("pathStack.push(new StringBuilder(pathStack.peek()).append(\".\").append(\"");
		stringBuilder.append(groupInfo.getFirstUpperItemName());
		stringBuilder.append("\").toString());");
		
		stringBuilder.append(buildStringOfOrderedItemSetPart(depth, newPath, getGroupVarObjName(depth, groupInfo.getItemName()),
				getGroupMiddleObjVarName(depth, groupInfo.getItemName()), groupInfo.getOrderedItemSet()));
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		/** pathStack.pop(); */
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("pathStack.pop();");		

		return stringBuilder.toString();
	}


	public String buildStringOfOrderedItemSetPart(int depth, String path, String varNameOfSetOwner,
			String middleObjVarName, OrderedItemSet orderedItemSet) {
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
		for (AbstractItemInfo itemInfo : itemInfoList) {
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);

			ItemInfoType itemInfoType = itemInfo.getItemInfoType();
			switch (itemInfoType) {
			case SINGLE: {
				SingleItemInfo singleItemInfo = (SingleItemInfo) itemInfo;
				stringBuilder
						.append(buildStringOfSingleItemInfoPart(depth, path, varNameOfSetOwner, middleObjVarName, singleItemInfo));
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

	/**
	 * @param messageID
	 * @param firstLowerMessageID
	 * @return encode(AbstractMessage, SingleItemEncoderIF, Object) 메소드 파트 문자열
	 */
	public String buildStringOfEncodeMethodPart(String messageID, String firstLowerMessageID) {
		StringBuilder stringBuilder = new StringBuilder();

		final int depth = 1;
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append(
				"public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {");

		// AllItemTypeReq allItemTypeReq = (AllItemTypeReq)messageObj;
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append(messageID);
		stringBuilder.append(" ");
		stringBuilder.append(firstLowerMessageID);
		stringBuilder.append(" = (");
		stringBuilder.append(messageID);
		stringBuilder.append(")messageObj;");
		// encodeBody(allItemTypeReq, singleItemEncoder, writableMiddleObject);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("encodeBody(");
		stringBuilder.append(firstLowerMessageID);
		stringBuilder.append(", singleItemEncoder, writableMiddleObject);");
		// }
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("}");

		return stringBuilder.toString();
	}

	public String buildStringOfEncodeBodyMethodPart(String messageID, String firstLowerMessageID,
			String middleObjVarName, kr.pe.sinnori.common.message.builder.info.MessageInfo messageInfo) {
		StringBuilder stringBuilder = new StringBuilder();

		int depth = 1;
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("private void encodeBody(");
		stringBuilder.append(messageID);
		stringBuilder.append(" ");
		stringBuilder.append(firstLowerMessageID);
		stringBuilder.append(", SingleItemEncoderIF singleItemEncoder, Object ");
		stringBuilder.append(middleObjVarName);
		stringBuilder.append(") throws Exception {");

		if (! messageInfo.getOrderedItemSet().getItemInfoList().isEmpty()) {
			// Stack<String> pathStack = new Stack<String>();
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
			/** java.util.Stack is thread-safe but LinkedList is not thread-safe */
			stringBuilder.append("LinkedList<String> pathStack = new LinkedList<String>();");

			// pathStack.push("AllItemTypeReq");
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
			stringBuilder.append("pathStack.push(");
			stringBuilder.append("\"");
			stringBuilder.append(messageID);
			stringBuilder.append("\");");
			
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			stringBuilder.append(buildStringOfOrderedItemSetPart(depth+1, messageID, firstLowerMessageID, middleObjVarName,
					messageInfo.getOrderedItemSet()));
			
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
			stringBuilder.append("pathStack.pop();");
		}		
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("}");

		/*stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("}");*/

		return stringBuilder.toString();
	}

	public String buildStringOfFileContents(String author,
			kr.pe.sinnori.common.message.builder.info.MessageInfo messageInfo) {

		final String middleObjVarName = "middleWritableObject";
		final int depth = 0;

		String messageID = messageInfo.getMessageID();
		String firstLowerMessageID = messageInfo.getFirstLowerMessageID();

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(buildStringOfLincensePart());
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPackagePartString(messageID));
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);

		stringBuilder.append(CommonStaticFinalVars.NEWLINE);

		String importElements[] = { "import java.util.LinkedList;",
				"import kr.pe.sinnori.common.message.AbstractMessage;",
				"import kr.pe.sinnori.common.type.SingleItemType;",
				"import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;",
				"import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;" };
		stringBuilder.append(buildStringOfImportPart(importElements));

		stringBuilder.append(CommonStaticFinalVars.NEWLINE);

		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(buildStringOfFileDescriptionPart(messageID, author, "메시지 인코더"));

		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("public final class ");
		stringBuilder.append(messageID);
		stringBuilder.append("Encoder extends AbstractMessageEncoder {");

		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("@Override");
		// encode(AbstractMessage, SingleItemEncoderIF, Object) 메소드 파트 문자열
		stringBuilder.append(buildStringOfEncodeMethodPart(messageID, firstLowerMessageID));

		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(
				buildStringOfEncodeBodyMethodPart(messageID, firstLowerMessageID, middleObjVarName, messageInfo));

		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("}");
		return stringBuilder.toString();
	}
}
