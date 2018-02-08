package kr.pe.sinnori.common.message.builder;

import java.util.List;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.builder.info.AbstractItemInfo;
import kr.pe.sinnori.common.message.builder.info.ArrayInfo;
import kr.pe.sinnori.common.message.builder.info.GroupInfo;
import kr.pe.sinnori.common.message.builder.info.OrderedItemSet;
import kr.pe.sinnori.common.message.builder.info.SingleItemInfo;
import kr.pe.sinnori.common.type.ItemInfoType;
import kr.pe.sinnori.common.util.CommonStaticUtil;


public class MessageFileContensBuilder extends AbstractSourceFileBuildre {
	
	/**
	 * 내부 클래스가 있는 경우에 호출
	 * @param depth 깊이
	 * @param itemSet 배열 정보
	 * @return 배열 정보를 바탕으로만들어진 내부 클래스
	 */
	public String buildStringOfClassDefinePart(int depth, String firstUpperItemName, OrderedItemSet itemSet) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));		
		stringBuilder.append("public static class ");
		stringBuilder.append(firstUpperItemName);
		stringBuilder.append(" {");		
		stringBuilder.append(buildStringOfVariableDeclarationPart(depth+1, itemSet));
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(buildStringOfMethodDefinePart(depth+1, itemSet));
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(buildStringOfToStringPart(depth+1, firstUpperItemName, itemSet));		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("}");
		
		return stringBuilder.toString();
	}
	
	
	public String buildStringOfVariableDeclarationPartForSingleItemInfo(int depth, SingleItemInfo singleItemInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("private ");
		stringBuilder.append(singleItemInfo.getJavaLangTypeOfItemType());
		
		stringBuilder.append(" ");
		stringBuilder.append(singleItemInfo.getItemName());
		
		String defaultValueRightValueString = singleItemInfo.getDefaultValueForVariableDeclarationPart();
		if (null != defaultValueRightValueString) {
			stringBuilder.append(" = ");
			stringBuilder.append(defaultValueRightValueString);
		}
		stringBuilder.append(";");
		return stringBuilder.toString();
	}
	
	public String buildStringOfVariableDeclarationPartForArrayInfo(int depth, ArrayInfo arrayInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(buildStringOfClassDefinePart(depth, arrayInfo.getFirstUpperItemName(), arrayInfo.getOrderedItemSet()));
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("private java.util.List<");
		stringBuilder.append(arrayInfo.getFirstUpperItemName());
		stringBuilder.append("> ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("List;");
		return stringBuilder.toString();
	}
	
	
	public String buildStringOfVariableDeclarationPartForGroupInfo(int depth, GroupInfo groupInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(buildStringOfClassDefinePart(depth+1, groupInfo.getFirstUpperItemName(), groupInfo.getOrderedItemSet()));
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("private ");
		stringBuilder.append(groupInfo.getFirstUpperItemName());
		stringBuilder.append(" ");
		stringBuilder.append(groupInfo.getItemName());
		stringBuilder.append(";");
		return stringBuilder.toString();
	}
	
	
	public String buildStringOfVariableDeclarationPart(int depth, OrderedItemSet itemSet) {
		StringBuilder stringBuilder = new StringBuilder();
		
		List<AbstractItemInfo> itemInfoList = itemSet.getItemInfoList();
		for (AbstractItemInfo itemInfo: itemInfoList) {
			ItemInfoType itemInfoType = itemInfo.getItemInfoType();
			switch (itemInfoType) {	
				case SINGLE :  {
					SingleItemInfo singleItemInfo = (SingleItemInfo)itemInfo;
					stringBuilder.append(buildStringOfVariableDeclarationPartForSingleItemInfo(depth, singleItemInfo));					
					break;
				}
				case ARRAY :  {
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					ArrayInfo arrayInfo = (ArrayInfo)itemInfo;
					stringBuilder.append(buildStringOfVariableDeclarationPartForArrayInfo(depth, arrayInfo));
					
					break;
				}
				case GROUP :  {
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					GroupInfo groupInfo = (GroupInfo)itemInfo;
					stringBuilder.append(buildStringOfVariableDeclarationPartForGroupInfo(depth, groupInfo));					
					break;
				}
				default: {
					log.error("unknwon item type[{}]", itemInfoType.toString());
					System.exit(1);
					break;
				}
			}
		}
		
		return stringBuilder.toString();
	}
	
	
	public String buildStringOfGetMethodDefinePartForSingleItemInfo(int depth, SingleItemInfo singleItemInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("public ");
		stringBuilder.append(singleItemInfo.getJavaLangTypeOfItemType());
		stringBuilder.append(" get");
		
		stringBuilder.append(singleItemInfo.getFirstUpperItemName());
		stringBuilder.append("() {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("return ");
		stringBuilder.append(singleItemInfo.getItemName());
		stringBuilder.append(";");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("}");
		return stringBuilder.toString();
	}
	
	public String buildStringOfSetMethodDefinePartForSingleItemInfo(int depth, SingleItemInfo singleItemInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("public void set");
		stringBuilder.append(singleItemInfo.getFirstUpperItemName());
		stringBuilder.append("(");
		stringBuilder.append(singleItemInfo.getJavaLangTypeOfItemType());
		stringBuilder.append(" ");
		stringBuilder.append(singleItemInfo.getItemName());
		stringBuilder.append(") {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("this.");
		stringBuilder.append(singleItemInfo.getItemName());
		stringBuilder.append(" = ");
		stringBuilder.append(singleItemInfo.getItemName());
		stringBuilder.append(";");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("}");
		return stringBuilder.toString();
	}
	
	public String buildStringOfGetMethodDefinePartForArrayInfo(int depth, ArrayInfo arrayInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("public java.util.List<");
		stringBuilder.append(arrayInfo.getFirstUpperItemName());
		stringBuilder.append("> get");				
		stringBuilder.append(arrayInfo.getFirstUpperItemName());
		stringBuilder.append("List() {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("return ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("List;");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("}");
		return stringBuilder.toString();
	}
	
	public String buildStringOfSetMethodDefinePartForArrayInfo(int depth, ArrayInfo arrayInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("public void set");
		stringBuilder.append(arrayInfo.getFirstUpperItemName());
		stringBuilder.append("List(java.util.List<");
		stringBuilder.append(arrayInfo.getFirstUpperItemName());
		stringBuilder.append("> ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("List) {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("this.");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("List = ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("List;");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("}");
		return stringBuilder.toString();
	}
	
	public String buildStringOfGetMethodDefinePartForGroupInfo(int depth, GroupInfo groupInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("public ");
		stringBuilder.append(groupInfo.getFirstUpperItemName());
		stringBuilder.append(" get");				
		stringBuilder.append(groupInfo.getFirstUpperItemName());
		stringBuilder.append("() {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("return ");
		stringBuilder.append(groupInfo.getItemName());
		stringBuilder.append(";");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("}");
		
		return stringBuilder.toString();
	}
	
	public String buildStringOfSetMethodDefinePartForGroupInfo(int depth, GroupInfo groupInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("public void set");
		stringBuilder.append(groupInfo.getFirstUpperItemName());
		stringBuilder.append("(");
		stringBuilder.append(groupInfo.getFirstUpperItemName());
		stringBuilder.append(" ");
		stringBuilder.append(groupInfo.getItemName());
		stringBuilder.append(") {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("this.");
		stringBuilder.append(groupInfo.getItemName());
		stringBuilder.append(" = ");
		stringBuilder.append(groupInfo.getItemName());
		stringBuilder.append(";");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("}");
		
		return stringBuilder.toString();
	}
	
	/**
	 * 메소드 부분
	 * @param depth
	 * @param itemSet
	 * @return
	 */
	public String buildStringOfMethodDefinePart(int depth, OrderedItemSet itemSet) {
		StringBuilder stringBuilder = new StringBuilder();
		List<AbstractItemInfo> itemInfoList = itemSet.getItemInfoList();
		for (AbstractItemInfo itemInfo:itemInfoList) {
			
			ItemInfoType itemInfoType = itemInfo.getItemInfoType();			
			switch (itemInfoType) {	
				case SINGLE :  {
					SingleItemInfo singleItemInfo = (SingleItemInfo)itemInfo;
					stringBuilder.append(buildStringOfGetMethodDefinePartForSingleItemInfo(depth, singleItemInfo));
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					stringBuilder.append(buildStringOfSetMethodDefinePartForSingleItemInfo(depth, singleItemInfo));
					break;
				}
				case ARRAY :  {
					ArrayInfo arrayInfo = (ArrayInfo)itemInfo;
					stringBuilder.append(buildStringOfGetMethodDefinePartForArrayInfo(depth, arrayInfo));
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					stringBuilder.append(buildStringOfSetMethodDefinePartForArrayInfo(depth, arrayInfo));
					break;
				}
				case GROUP :  {					
					GroupInfo groupInfo = (GroupInfo)itemInfo;					
					stringBuilder.append(buildStringOfGetMethodDefinePartForGroupInfo(depth, groupInfo));
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					stringBuilder.append(buildStringOfSetMethodDefinePartForGroupInfo(depth, groupInfo));
					
					break;
				}
				default : {
					log.error("unknwon item type[{}]", itemInfoType.toString());
					System.exit(1);
					break;
				}
			}
		}
		return stringBuilder.toString();
	}
	
	
	public String buildStringOfToStringPartForSingleItemInfo(int depth, boolean isFirstElement, SingleItemInfo singleItemInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		
		if (isFirstElement) {
			stringBuilder.append("builder.append(\"");
		} else {
			stringBuilder.append("builder.append(\", ");			
		}
		
		stringBuilder.append(singleItemInfo.getItemName());
		stringBuilder.append("=\");");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("builder.append(");
		
		if (singleItemInfo.getItemTypeName().indexOf("length byte[]") >= 0) {
			// builder.append(kr.pe.sinnori.common.util.HexUtil.getHexStringFromByteArray(bytesVar1, 0, Math.max(bytesVar1.length, 7)));
			stringBuilder.append("kr.pe.sinnori.common.util.HexUtil.getHexStringFromByteArray(");
			stringBuilder.append(singleItemInfo.getItemName());
			stringBuilder.append(", 0, Math.min(");
			stringBuilder.append(singleItemInfo.getItemName());
			stringBuilder.append(".length, 7))");
		} else {
			// builder.append(longVar1);
			stringBuilder.append(singleItemInfo.getItemName());
		}
		
		stringBuilder.append(");");
		
		return stringBuilder.toString();
		
	}
	
	public String buildStringOfToStringPartForArrayInfo(int depth, boolean isFirstElement, ArrayInfo arrayInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		
		if (isFirstElement) {
			stringBuilder.append("builder.append(\"");
		} else {
			stringBuilder.append("builder.append(\", ");			
		}
		
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("List=\");");
		
		
		// if (null == memberList) {
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("if (null == ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("List) {");
		
		// builder.append("null");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("builder.append(\"null\");");		
		// } else {
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("} else {");		
		// int memberListSize = memberList.size();
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("int ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("ListSize = ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("List.size();");		
		// if (0 == memberListSize) {		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("if (0 == ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("ListSize) {");		
		// builder.append("empty");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
		stringBuilder.append("builder.append(\"empty\");");		
		// } else {
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("} else {");		
		// builder.append("[");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
		stringBuilder.append("builder.append(\"[\");");
		// for (int i=0; i < memberListSize; i++) {
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
		stringBuilder.append("for (int i=0; i < ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("ListSize; i++) {");
		// Member member = memberList.get(i);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 3));
		stringBuilder.append(arrayInfo.getFirstUpperItemName());
		stringBuilder.append(" ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append(" = ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("List.get(i);");
		// builder.append("member=[");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 3));
		stringBuilder.append("if (0 == i) {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 4));		
		stringBuilder.append("builder.append(\"");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("[\");");
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 3));
		stringBuilder.append("} else {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 4));		
		stringBuilder.append("builder.append(\", ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("[\");");		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 3));
		stringBuilder.append("}");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 3));
		// builder.append(i);
		stringBuilder.append("builder.append(i);");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 3));
		// builder.append("]=");
		stringBuilder.append("builder.append(\"]=\");");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 3));
		// builder.append(member.toString());
		stringBuilder.append("builder.append(");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append(".toString());");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
		// }
		stringBuilder.append("}");				
		// builder.append("]=");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
		stringBuilder.append("builder.append(\"]\");");
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
	
	public String buildStringOfToStringPartForGroupInfo(int depth, boolean isFirstElement, GroupInfo groupInfo) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));		
		if (isFirstElement) {
			stringBuilder.append("builder.append(\"");
		} else {
			stringBuilder.append("builder.append(\", ");			
		}
		
		stringBuilder.append(groupInfo.getItemName());
		stringBuilder.append("=\");");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("builder.append(");		
		stringBuilder.append(groupInfo.getItemName());		
		stringBuilder.append(".toString());");
		
		return stringBuilder.toString();
	}
	
	public String buildStringOfToStringPart(int depth, String firstUpperItemName, OrderedItemSet itemSet) {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("@Override");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("public String toString() {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("StringBuilder builder = new StringBuilder();");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		if (0 == depth) {
			stringBuilder.append("builder.append(\"class ");
		} else {
			stringBuilder.append("builder.append(\"");
		}
		stringBuilder.append(firstUpperItemName);
		stringBuilder.append("[\");");
		
		List<AbstractItemInfo> itemInfoList = itemSet.getItemInfoList();
		for (int j=0; j < itemInfoList.size(); j++) {
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			boolean isFirstElement = (0 == j);
			
			AbstractItemInfo itemInfo = itemInfoList.get(j);
			
			ItemInfoType itemInfoType = itemInfo.getItemInfoType();
			
			switch (itemInfoType) {	
				case SINGLE :  {
					SingleItemInfo singleItemInfo = (SingleItemInfo)itemInfo;
					
					stringBuilder.append(buildStringOfToStringPartForSingleItemInfo(depth+1, isFirstElement, singleItemInfo));
					break;
				}
				case ARRAY :  {
					ArrayInfo arrayInfo = (ArrayInfo)itemInfo;					
					
					stringBuilder.append(buildStringOfToStringPartForArrayInfo(depth+1, isFirstElement, arrayInfo));					
					break;
				}
				case GROUP :  {
					GroupInfo groupInfo = (GroupInfo)itemInfo;
					
					stringBuilder.append(buildStringOfToStringPartForGroupInfo(depth+1, isFirstElement, groupInfo));
					break;
				}
				default : {
					log.error("unknwon item type[{}]", itemInfoType.toString());
					System.exit(1);
				}
			}
		}
		
		if (0 == depth) {
			// builder.append(", messageHeaderInfo=");
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
			stringBuilder.append("builder.append(\", messageHeaderInfo=\");");
			
			// builder.append(messageHeaderInfo.toString());
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));	
			stringBuilder.append("builder.append(messageHeaderInfo.toString());");
		}
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));	
		stringBuilder.append("builder.append(\"]\");");
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		stringBuilder.append("return builder.toString();");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		stringBuilder.append("}");
		
		return stringBuilder.toString();
	}
	
	public String buildStringOfFileContents(String messageID, String author, kr.pe.sinnori.common.message.builder.info.MessageInfo messageInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(buildStringOfLincensePart());
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getPackagePartString(messageID));
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		// stringBuilder.append("import kr.pe.sinnori.common.message.AbstractMessage;");		
		String importElements[] = {
				"import kr.pe.sinnori.common.message.AbstractMessage;"
		};
		stringBuilder.append(buildStringOfImportPart(importElements));
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(buildStringOfFileDescriptionPart(messageID, author, "메시지"));
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		stringBuilder.append("public class ");
		stringBuilder.append(messageID);
		stringBuilder.append(" extends AbstractMessage {");
		
		stringBuilder.append(buildStringOfVariableDeclarationPart(1, messageInfo.getOrderedItemSet()));
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(buildStringOfMethodDefinePart(1, messageInfo.getOrderedItemSet()));
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		stringBuilder.append(buildStringOfToStringPart(1, messageInfo.getFirstLowerMessageID(), messageInfo.getOrderedItemSet()));

		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("}");
		
		return stringBuilder.toString();
	}

}
