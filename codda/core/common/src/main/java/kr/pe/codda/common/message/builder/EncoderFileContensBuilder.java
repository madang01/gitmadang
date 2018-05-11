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
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;
import kr.pe.codda.common.type.ItemInfoType;
import kr.pe.codda.common.type.SingleItemType;
import kr.pe.codda.common.util.CommonStaticUtil;

public class EncoderFileContensBuilder extends AbstractSourceFileBuildre {

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

	public String getReferenceVariableGetMethodString(String varNameOfSetOwner, String referenceCountVarName) {
		if (referenceCountVarName.length() < 2) {
			String errorMessage = String.format("the character number of the parameter referenceCountVarName[%s] is less than two", referenceCountVarName);
			throw new IllegalArgumentException(errorMessage);
		}
		
		StringBuilder contetnsStringBuilder = new StringBuilder();
		contetnsStringBuilder.append(varNameOfSetOwner);
		contetnsStringBuilder.append(".get");
		contetnsStringBuilder.append(referenceCountVarName.substring(0, 1).toUpperCase());
		contetnsStringBuilder.append(referenceCountVarName.substring(1));
		contetnsStringBuilder.append("()");
		return contetnsStringBuilder.toString();
	}

	public void addSingleItemInfoPart(StringBuilder contetnsStringBuilder, int depth, String path, String varNameOfSetOwner, String middleObjVarName,
			SingleItemInfo singleItemInfo) {;
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("singleItemEncoder.putValueToWritableMiddleObject(");
		// the Parameter path
		contetnsStringBuilder.append("pathStack.peek()");
		contetnsStringBuilder.append(", \"");
		// the parameter itemName
		contetnsStringBuilder.append(singleItemInfo.getItemName());
		contetnsStringBuilder.append("\"");
		// the parameter singleItemType		
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append(", ");
		contetnsStringBuilder.append(SingleItemType.class.getName());
		contetnsStringBuilder.append(".");
		contetnsStringBuilder
				.append(SingleItemTypeManger.getInstance().getSingleItemType(singleItemInfo.getItemTypeID()).name());
		contetnsStringBuilder.append(" // itemType");

		// the parameter itemValue
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append(", ");
		contetnsStringBuilder.append(varNameOfSetOwner);
		contetnsStringBuilder.append(".get");
		contetnsStringBuilder.append(singleItemInfo.getFirstUpperItemName());
		contetnsStringBuilder.append("() // itemValue");

		// the parameter itemSize
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append(", ");
		contetnsStringBuilder.append(singleItemInfo.getItemSize());
		contetnsStringBuilder.append(" // itemSize");

		// the parameter nativeItemCharset
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append(", ");
		String nativeItemCharset = singleItemInfo.getNativeItemCharset();
		if (null == nativeItemCharset) {
			contetnsStringBuilder.append("null");
		} else {
			contetnsStringBuilder.append("\"");
			contetnsStringBuilder.append(nativeItemCharset);
			contetnsStringBuilder.append("\"");
		}
		contetnsStringBuilder.append(" // nativeItemCharset");
		
		// the parameter writableMiddleObject
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append(", ");
		contetnsStringBuilder.append(middleObjVarName);
		contetnsStringBuilder.append(");");
	}
	
	public void addNullCaseArrayValidationPart(StringBuilder contetnsStringBuilder, int depth, String varNameOfSetOwner, ArrayInfo arrayInfo) {
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append("/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */");

		if (arrayInfo.getArrayCntType().equals("reference")) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			/**
			 * <pre>
			 * if (0 != <변수명>.get<첫문자자가 대문자인 참조변수명>()) {
			 * </pre>
			 */

			// if (0 != allDataTypeInObj.getCnt()) {
			contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
			contetnsStringBuilder.append("if (0 != ");			
			contetnsStringBuilder.append(getReferenceVariableGetMethodString(varNameOfSetOwner, arrayInfo.getArrayCntValue()));
			contetnsStringBuilder.append(") {");			
			
			
			contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));			
			contetnsStringBuilder.append("String errorMessage = new StringBuilder(\"the var ");
			contetnsStringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
			contetnsStringBuilder.append(" is null but the value referenced by the array size[");
			contetnsStringBuilder.append(getReferenceVariableGetMethodString(varNameOfSetOwner, arrayInfo.getArrayCntValue()));
			contetnsStringBuilder.append("][\").append(");
			contetnsStringBuilder.append(getReferenceVariableGetMethodString(varNameOfSetOwner, arrayInfo.getArrayCntValue()));
			contetnsStringBuilder.append(").append(\"] is not zero\").toString();");

			// throw new BodyFormatException(errorMessage);
			contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
			contetnsStringBuilder.append("throw new ");
			contetnsStringBuilder.append(BodyFormatException.class.getName());
			contetnsStringBuilder.append("(errorMessage);");

			// }
			contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
			contetnsStringBuilder.append("}");

		} else {
			contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
			contetnsStringBuilder.append("if (0 != ");
			contetnsStringBuilder.append(arrayInfo.getArrayCntValue());
			contetnsStringBuilder.append(") {");
			
			contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
			// String errorMessage = new StringBuilder("the var member$1List is null but the value defined by array size[3] is not zero").toString();
			/*contetnsStringBuilder.append("String errorMessage = new StringBuilder(\"the var \").append(\"");
			contetnsStringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
			contetnsStringBuilder.append("\").append(\"is null but the value defined by array size\").append(\"[");
			contetnsStringBuilder.append(arrayInfo.getArrayCntValue());
			contetnsStringBuilder.append("] is not zero\").toString();");*/
			
			contetnsStringBuilder.append("String errorMessage = new StringBuilder(\"the var ");
			contetnsStringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
			contetnsStringBuilder.append(" is null but the value defined by array size[");
			contetnsStringBuilder.append(arrayInfo.getArrayCntValue());
			contetnsStringBuilder.append("] is not zero\").toString();");
			

			// throw new BodyFormatException(errorMessage);
			contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
			contetnsStringBuilder.append("throw new ");
			contetnsStringBuilder.append(BodyFormatException.class.getName());
			contetnsStringBuilder.append("(errorMessage);");

			// }
			contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
			contetnsStringBuilder.append("}");
		}
	}

	/** 배열 크기가 메시지 정보에서 정의한 배열 크기와 같은지 검사 */
	public void addPartCheckingListSizeIsValid(StringBuilder contetnsStringBuilder, int depth, String varNameOfSetOwner, ArrayInfo arrayInfo) {

		// if (memberListSize != allDataTypeInObj.getCnt()) {
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append("if (");		
		if (arrayInfo.getArrayCntType().equals("reference")) {
			contetnsStringBuilder.append(getReferenceVariableGetMethodString(varNameOfSetOwner, arrayInfo.getArrayCntValue()));			
		} else {
			contetnsStringBuilder.append(arrayInfo.getArrayCntValue());
		}		
		contetnsStringBuilder.append(" != ");
		contetnsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getArrayName()));
		contetnsStringBuilder.append(") {");

		// String errorMessage = new StringBuilder(allDataTypeInObjSingleItemPath)
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
		
		if (arrayInfo.getArrayCntType().equals("reference")) {			
			contetnsStringBuilder.append("String errorMessage = new StringBuilder(\"the var ");
			contetnsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
			contetnsStringBuilder.append("[\").append(");
			contetnsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
			contetnsStringBuilder.append(").append(\"] is not same to the value referenced by the array size[");
			contetnsStringBuilder.append(getReferenceVariableGetMethodString(varNameOfSetOwner, arrayInfo.getArrayCntValue()));
			contetnsStringBuilder.append("][\").append(");
			contetnsStringBuilder.append(getReferenceVariableGetMethodString(varNameOfSetOwner, arrayInfo.getArrayCntValue()));
			contetnsStringBuilder.append(").append(\"]\").toString();");
			
		} else {
			// String errorMessage = new StringBuilder("the var member$1ListSize[").append(member$1ListSize).append("] is not same to the value defined by array size[3]").toString();
			contetnsStringBuilder.append("String errorMessage = new StringBuilder(\"the var ");
			contetnsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
			contetnsStringBuilder.append("[\").append(");
			contetnsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
			contetnsStringBuilder.append(").append(\"] is not same to the value defined by array size[");
			contetnsStringBuilder.append(arrayInfo.getArrayCntValue());
			contetnsStringBuilder.append("]\").toString();");
			
		}

		// throw new BodyFormatException(errorMessage);
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
		contetnsStringBuilder.append("throw new ");
		contetnsStringBuilder.append(BodyFormatException.class.getName());
		contetnsStringBuilder.append("(errorMessage);");

		// }
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append("}");
	}

	public void addNotNullCaseArrayValidationPart(StringBuilder contetnsStringBuilder, int depth, String path, String nameOfSetOwner,
			String middleObjVarName, ArrayInfo arrayInfo) {
		String newPath = new StringBuilder(path).append(".").append(arrayInfo.getFirstUpperItemName()).toString();

		/** 배열 변수 선언및 정의 */
		// int memberListSize = memberList.size();
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append("int ");
		contetnsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
		contetnsStringBuilder.append(" = ");
		contetnsStringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
		contetnsStringBuilder.append(".size();");

		/** 배열 값이 null 이 아닐때에는 배열 크기가 메시지 정보에서 정의한 배열 크기와 같은지 검사 */
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append("/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */");

		/** 배열 크기가 메시지 정보에서 정의한 배열 크기와 같은지 검사 */
		addPartCheckingListSizeIsValid(contetnsStringBuilder, depth, nameOfSetOwner, arrayInfo);

		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);

		/** 이 배열을 위한 중간 객체 가져오기 */
		// Object memberArrayMiddleObject =
		// singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(allDataTypeInObjSingleItemPath,
		// "member", memberList.length, middleWritableObject);
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append("Object ");
		/*
		 * contetnsStringBuilder.append(arrayInfo.getItemName());
		 * contetnsStringBuilder.append("MiddleWriteArray");
		 */
		contetnsStringBuilder.append(getArrayMiddleObjVarName(depth, arrayInfo.getItemName()));
		contetnsStringBuilder.append(" = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(");		
		// the parameter path
		contetnsStringBuilder.append("pathStack.peek()");
		contetnsStringBuilder.append(", ");
		// the parameter arrayName
		contetnsStringBuilder.append("\"");
		contetnsStringBuilder.append(arrayInfo.getItemName());
		contetnsStringBuilder.append("\", ");
		// the parameter arrayCntValue
		contetnsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
		contetnsStringBuilder.append(", ");
		// the parameter writableMiddleObject
		contetnsStringBuilder.append(middleObjVarName);
		contetnsStringBuilder.append(");");

		// for (int i=0; i < memberListSize; i++) {
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append("for (int ");
		contetnsStringBuilder.append(getCountVarName(depth));
		contetnsStringBuilder.append("=0; ");
		contetnsStringBuilder.append(getCountVarName(depth));
		contetnsStringBuilder.append(" < ");
		contetnsStringBuilder.append(getArrayListSizeVarObjName(depth, arrayInfo.getItemName()));
		contetnsStringBuilder.append("; ");
		contetnsStringBuilder.append(getCountVarName(depth));
		contetnsStringBuilder.append("++) {");

		
		// pathStack.push(newStringBuilder(pathStack.peek()).append(".").append("Member").append("[").append(i).append("]").toString());
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
		contetnsStringBuilder.append("pathStack.push(new StringBuilder(pathStack.peek()).append(\".\").append(\"")
				.append(arrayInfo.getFirstUpperItemName())
				.append("\").append(\"[\").append(")
				.append(getCountVarName(depth))
				.append(").append(\"]\").toString());");

		// Object memberMiddleWriteObj = singleItemEncoder.getMiddleWriteObjFromArrayObj(memberSingleItemPath, memberMiddleWriteArray, i);
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
		contetnsStringBuilder.append("Object ");
		contetnsStringBuilder.append(getElementObjVarNameOfArrayMiddleObject(depth, arrayInfo.getItemName()));
		contetnsStringBuilder.append(" = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(");	
		// the parameter path
		contetnsStringBuilder.append("pathStack.peek(), ");
		// the parameter arrayObj
		contetnsStringBuilder.append(getArrayMiddleObjVarName(depth, arrayInfo.getItemName()));
		contetnsStringBuilder.append(", ");
		// the parameter inx
		contetnsStringBuilder.append(getCountVarName(depth));
		contetnsStringBuilder.append(");");

		// AllDataType.Member member = memberList.get(i);
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
		/*
		 * contetnsStringBuilder.append(path); contetnsStringBuilder.append(".");
		 * contetnsStringBuilder.append(arrayInfo.getFirstUpperItemName());
		 */
		contetnsStringBuilder.append(newPath);
		contetnsStringBuilder.append(" ");
		contetnsStringBuilder.append(getArrayVarObjName(depth, arrayInfo.getItemName()));
		contetnsStringBuilder.append(" = ");
		contetnsStringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
		contetnsStringBuilder.append(".get(");
		contetnsStringBuilder.append(getCountVarName(depth));
		contetnsStringBuilder.append(");");

		addOrderedItemSetPart(contetnsStringBuilder, depth + 2
				, newPath
				, getArrayVarObjName(depth, arrayInfo.getItemName())
				, getElementObjVarNameOfArrayMiddleObject(depth, arrayInfo.getItemName())
				, arrayInfo.getOrderedItemSet());

		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		// pathStack.pop();
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
		contetnsStringBuilder.append("pathStack.pop();");

		// }
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append("}");
	}

	public void addArrayInfoPart(StringBuilder contetnsStringBuilder, int depth, String path, String varNameOfSetOwner, String middleObjVarName,
			ArrayInfo arrayInfo) {		
		/** 배열 변수 선언및 정의 */
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("java.util.List<");
		contetnsStringBuilder.append(path);
		contetnsStringBuilder.append(".");
		contetnsStringBuilder.append(arrayInfo.getFirstUpperItemName());
		contetnsStringBuilder.append("> ");
		contetnsStringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
		contetnsStringBuilder.append(" = ");
		contetnsStringBuilder.append(varNameOfSetOwner);
		contetnsStringBuilder.append(".get");
		contetnsStringBuilder.append(arrayInfo.getFirstUpperItemName());
		contetnsStringBuilder.append("List();");

		/** 배열 정보와 배열 크기 일치 검사 */
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);

		/** 주석 */
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("/** 배열 정보와 배열 크기 일치 검사 */");

		/** if (null == memberList) { */
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("if (null == ");
		contetnsStringBuilder.append(getArrayListVarObjName(depth, arrayInfo.getItemName()));
		contetnsStringBuilder.append(") {");

		addNullCaseArrayValidationPart(contetnsStringBuilder, depth, varNameOfSetOwner, arrayInfo);

		// } else {
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("} else {");

		// buildStringOfPartWhoseListIsNotNullAtArray
		addNotNullCaseArrayValidationPart(contetnsStringBuilder, depth, path, varNameOfSetOwner,
				middleObjVarName, arrayInfo);				

		// }
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("}");
	}
	
	public void addGroupInfoPart(StringBuilder contetnsStringBuilder, int depth, String path, String varNameOfSetOwner, String middleObjVarName,
			GroupInfo groupInfo) {
		String newPath = new StringBuilder(path).append(".").append(groupInfo.getFirstUpperItemName()).toString();	

		/** 변수 선언 */
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));		
		contetnsStringBuilder.append(newPath);
		contetnsStringBuilder.append(" ");
		contetnsStringBuilder.append(getGroupVarObjName(depth, groupInfo.getItemName()));
		contetnsStringBuilder.append(" = ");
		contetnsStringBuilder.append(varNameOfSetOwner);
		contetnsStringBuilder.append(".get");
		contetnsStringBuilder.append(groupInfo.getFirstUpperItemName());
		contetnsStringBuilder.append("();");
		
		/** if (null == group1$2) { */
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("if (null == ");
		contetnsStringBuilder.append(getGroupVarObjName(depth, groupInfo.getItemName()));
		contetnsStringBuilder.append(") {");		
		/** 	String errorMessage = "the var group1$1 is null"; */
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append("String errorMessage = \"the var ");
		contetnsStringBuilder.append(getGroupVarObjName(depth, groupInfo.getItemName()));
		contetnsStringBuilder.append(" is null\";");		
		/** 	throw new BodyFormatException(errorMessage); */
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));		 
		contetnsStringBuilder.append("throw new ");
		contetnsStringBuilder.append(BodyFormatException.class.getName());
		contetnsStringBuilder.append("(errorMessage);");
		/** } */
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("}");		
		
		/** group 쓰기 가능한 중간 객체 얻기 */
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("Object ");
		contetnsStringBuilder.append(getGroupMiddleObjVarName(depth, groupInfo.getItemName()));
		contetnsStringBuilder.append(" = singleItemEncoder.getGroupMiddleObjectFromWritableMiddleObject(");
		// the parameter path
		contetnsStringBuilder.append("pathStack.peek(), ");
		// the parameter groupName
		contetnsStringBuilder.append("\"");
		contetnsStringBuilder.append(groupInfo.getItemName());
		contetnsStringBuilder.append("\", ");
		// the parameter writableMiddleObject
		contetnsStringBuilder.append(middleObjVarName);
		contetnsStringBuilder.append(");");
		
		/** path stack push */
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("pathStack.push(new StringBuilder(pathStack.peek()).append(\".\").append(\"");
		contetnsStringBuilder.append(groupInfo.getFirstUpperItemName());
		contetnsStringBuilder.append("\").toString());");
		
		addOrderedItemSetPart(contetnsStringBuilder, depth, newPath, getGroupVarObjName(depth, groupInfo.getItemName()),
				getGroupMiddleObjVarName(depth, groupInfo.getItemName()), groupInfo.getOrderedItemSet());
		
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		/** pathStack.pop(); */
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("pathStack.pop();");
	}


	public void addOrderedItemSetPart(StringBuilder contetnsStringBuilder, int depth, String path, String varNameOfSetOwner,
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

		List<AbstractItemInfo> itemInfoList = orderedItemSet.getItemInfoList();
		for (AbstractItemInfo itemInfo : itemInfoList) {
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

	
	public void addEncodeMethodPart(StringBuilder contetnsStringBuilder, String messageID, String firstLowerMessageID) {
		final int depth = 1;
		
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append(
				"public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {");

		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append(messageID);
		contetnsStringBuilder.append(" ");
		contetnsStringBuilder.append(firstLowerMessageID);
		contetnsStringBuilder.append(" = (");
		contetnsStringBuilder.append(messageID);
		contetnsStringBuilder.append(")messageObj;");
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append("encodeBody(");
		contetnsStringBuilder.append(firstLowerMessageID);
		contetnsStringBuilder.append(", singleItemEncoder, writableMiddleObject);");
		// }
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("}");
	}

	public void addEncodeBodyMethodPart(StringBuilder contetnsStringBuilder, String messageID, String firstLowerMessageID,
			String middleObjVarName, MessageInfo messageInfo) {
		int depth = 1;
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("private void encodeBody(");
		contetnsStringBuilder.append(messageID);
		contetnsStringBuilder.append(" ");
		contetnsStringBuilder.append(firstLowerMessageID);
		contetnsStringBuilder.append(", SingleItemEncoderIF singleItemEncoder, Object ");
		contetnsStringBuilder.append(middleObjVarName);
		contetnsStringBuilder.append(") throws Exception {");

		if (! messageInfo.getOrderedItemSet().getItemInfoList().isEmpty()) {
			// Stack<String> pathStack = new Stack<String>();
			contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
			/** java.util.Stack is thread-safe but LinkedList is not thread-safe */
			contetnsStringBuilder.append("java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();");

			contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
			contetnsStringBuilder.append("pathStack.push(");
			contetnsStringBuilder.append("\"");
			contetnsStringBuilder.append(messageID);
			contetnsStringBuilder.append("\");");
			
			contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			addOrderedItemSetPart(contetnsStringBuilder, depth+1, messageID, firstLowerMessageID, middleObjVarName,
					messageInfo.getOrderedItemSet());
			
			contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
			contetnsStringBuilder.append("pathStack.pop();");
		}		
		
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contetnsStringBuilder.append("}");
	}

	public String buildStringOfFileContents(String author,
			MessageInfo messageInfo) {

		final String middleObjVarName = "middleWritableObject";
		final int depth = 0;

		String messageID = messageInfo.getMessageID();
		String firstLowerMessageID = messageInfo.getFirstLowerMessageID();

		StringBuilder contetnsStringBuilder = new StringBuilder();

		addLincensePart(contetnsStringBuilder);
		
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);		
		addPackageDeclarationPart(contetnsStringBuilder, messageID);
		
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		Class<?> importClazzes[] = {
				AbstractMessage.class,
				AbstractMessageEncoder.class,
				SingleItemEncoderIF.class 
		};
		addImportDeclarationsPart(contetnsStringBuilder, importClazzes);

		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		addSourceFileDescriptionPart(contetnsStringBuilder, messageID, author, "message encoder");

		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append("public final class ");
		contetnsStringBuilder.append(messageID);
		contetnsStringBuilder.append("Encoder extends AbstractMessageEncoder {");

		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contetnsStringBuilder.append("@Override");
		// encode(AbstractMessage, SingleItemEncoderIF, Object) 메소드 파트 문자열
		addEncodeMethodPart(contetnsStringBuilder, messageID, firstLowerMessageID);

		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		addEncodeBodyMethodPart(contetnsStringBuilder, messageID, firstLowerMessageID, middleObjVarName, messageInfo);

		contetnsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contetnsStringBuilder.append("}");
		return contetnsStringBuilder.toString();
	}
}
