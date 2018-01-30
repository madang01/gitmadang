package kr.pe.sinnori.common.message.builder;

import java.util.List;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.builder.info.AbstractItemInfo;
import kr.pe.sinnori.common.message.builder.info.ArrayInfo;
import kr.pe.sinnori.common.message.builder.info.GroupInfo;
import kr.pe.sinnori.common.message.builder.info.ItemInfoType;
import kr.pe.sinnori.common.message.builder.info.OrderedItemSet;
import kr.pe.sinnori.common.message.builder.info.SingleItemInfo;


public class MessageFileContensBuilder extends AbstractSourceFileBuildre {
	
	/**
	 * 내부 클래스가 있는 경우에 호출
	 * @param depth 깊이
	 * @param itemSet 배열 정보
	 * @return 배열 정보를 바탕으로만들어진 내부 클래스
	 */
	public String buildStringOfClassDefinePart(int depth, String firstUpperItemName, OrderedItemSet itemSet) {
		StringBuilder stringBuilder = new StringBuilder();
		
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		
		
		stringBuilder.append("public static class ");
		
		
		// stringBuilder.append(arrayInfo.getFirstUpperItemName());
		stringBuilder.append(firstUpperItemName);
		stringBuilder.append(" {");
		
		stringBuilder.append(buildStringOfVariableDeclarationPart(depth, itemSet));
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(buildStringOfMethodDefinePart(depth, itemSet));
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(buildStringOfToStringPart(depth, firstUpperItemName, itemSet));
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("}");
		
		return stringBuilder.toString();
	}
	
	
	public String buildStringOfVariableDeclarationPart(int depth, SingleItemInfo singleItemInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\tprivate ");
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
	
	public String buildStringOfVariableDeclarationPart(int depth, ArrayInfo arrayInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(buildStringOfClassDefinePart(depth+1, arrayInfo.getFirstUpperItemName(), arrayInfo.getOrderedItemSet()));
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\tprivate java.util.List<");
		stringBuilder.append(arrayInfo.getFirstUpperItemName());
		stringBuilder.append("> ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("List;");
		return stringBuilder.toString();
	}
	
	
	public String buildStringOfVariableDeclarationPart(int depth, GroupInfo groupInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(buildStringOfClassDefinePart(depth+1, groupInfo.getFirstUpperItemName(), groupInfo.getOrderedItemSet()));
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\tprivate ");
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
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			ItemInfoType itemInfoType = itemInfo.getItemInfoType();
			switch (itemInfoType) {	
				case SINGLE :  {
					SingleItemInfo singleItemInfo = (SingleItemInfo)itemInfo;
					stringBuilder.append(buildStringOfVariableDeclarationPart(depth, singleItemInfo));					
					break;
				}
				case ARRAY :  {
					ArrayInfo arrayInfo = (ArrayInfo)itemInfo;
					stringBuilder.append(buildStringOfVariableDeclarationPart(depth, arrayInfo));
					
					break;
				}
				case GROUP :  {
					GroupInfo groupInfo = (GroupInfo)itemInfo;
					stringBuilder.append(buildStringOfVariableDeclarationPart(depth, groupInfo));					
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
	
	
	public String buildStringOfGetMethodDefinePart(int depth, SingleItemInfo singleItemInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\tpublic ");
		stringBuilder.append(singleItemInfo.getJavaLangTypeOfItemType());
		stringBuilder.append(" get");
		
		stringBuilder.append(singleItemInfo.getFirstUpperItemName());
		stringBuilder.append("() {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\t\treturn ");
		stringBuilder.append(singleItemInfo.getItemName());
		stringBuilder.append(";");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\t}");
		return stringBuilder.toString();
	}
	
	public String buildStringOfSetMethodDefinePart(int depth, SingleItemInfo singleItemInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\tpublic void set");
		stringBuilder.append(singleItemInfo.getFirstUpperItemName());
		stringBuilder.append("(");
		stringBuilder.append(singleItemInfo.getJavaLangTypeOfItemType());
		stringBuilder.append(" ");
		stringBuilder.append(singleItemInfo.getItemName());
		stringBuilder.append(") {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\t\tthis.");
		stringBuilder.append(singleItemInfo.getItemName());
		stringBuilder.append(" = ");
		stringBuilder.append(singleItemInfo.getItemName());
		stringBuilder.append(";");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\t}");
		return stringBuilder.toString();
	}
	
	public String buildStringOfGetMethodDefinePart(int depth, ArrayInfo arrayInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\tpublic java.util.List<");
		stringBuilder.append(arrayInfo.getFirstUpperItemName());
		stringBuilder.append("> get");				
		stringBuilder.append(arrayInfo.getFirstUpperItemName());
		stringBuilder.append("List() {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\t\treturn ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("List;");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\t}");
		return stringBuilder.toString();
	}
	
	public String buildStringOfSetMethodDefinePart(int depth, ArrayInfo arrayInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\tpublic void set");
		stringBuilder.append(arrayInfo.getFirstUpperItemName());
		stringBuilder.append("List(java.util.List<");
		stringBuilder.append(arrayInfo.getFirstUpperItemName());
		stringBuilder.append("> ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("List) {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\t\tthis.");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("List = ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("List;");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\t}");
		return stringBuilder.toString();
	}
	
	public String buildStringOfGetMethodDefinePart(int depth, GroupInfo groupInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\tpublic ");
		stringBuilder.append(groupInfo.getFirstUpperItemName());
		stringBuilder.append(" get");				
		stringBuilder.append(groupInfo.getFirstUpperItemName());
		stringBuilder.append("() {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\t\treturn ");
		stringBuilder.append(groupInfo.getItemName());
		stringBuilder.append(";");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\t}");
		
		return stringBuilder.toString();
	}
	
	public String buildStringOfSetMethodDefinePart(int depth, GroupInfo groupInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\tpublic void set");
		stringBuilder.append(groupInfo.getFirstUpperItemName());
		stringBuilder.append("(");
		stringBuilder.append(groupInfo.getFirstUpperItemName());
		stringBuilder.append(" ");
		stringBuilder.append(groupInfo.getItemName());
		stringBuilder.append(") {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\t\tthis.");
		stringBuilder.append(groupInfo.getItemName());
		stringBuilder.append(" = ");
		stringBuilder.append(groupInfo.getItemName());
		stringBuilder.append(";");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\t}");
		
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
			
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			switch (itemInfoType) {	
				case SINGLE :  {
					SingleItemInfo singleItemInfo = (SingleItemInfo)itemInfo;
					stringBuilder.append(buildStringOfGetMethodDefinePart(depth, singleItemInfo));
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					stringBuilder.append(buildStringOfSetMethodDefinePart(depth, singleItemInfo));
					break;
				}
				case ARRAY :  {
					ArrayInfo arrayInfo = (ArrayInfo)itemInfo;					
					
					stringBuilder.append(buildStringOfGetMethodDefinePart(depth, arrayInfo));
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					stringBuilder.append(buildStringOfSetMethodDefinePart(depth, arrayInfo));
					break;
				}
				case GROUP :  {
					GroupInfo groupInfo = (GroupInfo)itemInfo;
					
					stringBuilder.append(buildStringOfGetMethodDefinePart(depth, groupInfo));
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					stringBuilder.append(buildStringOfSetMethodDefinePart(depth, groupInfo));
					
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
	
	
	public String buildStringOfToStringPart(int depth, boolean isFirstElement, SingleItemInfo singleItemInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		
		if (isFirstElement) {
			stringBuilder.append("\t\tbuilder.append(\"");
		} else {
			stringBuilder.append("\t\tbuilder.append(\", ");			
		}
		
		stringBuilder.append(singleItemInfo.getItemName());
		stringBuilder.append("=\");");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\t\tbuilder.append(");
		
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
	
	public String buildStringOfToStringPart(int depth, boolean isFirstElement, ArrayInfo arrayInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		
		if (isFirstElement) {
			stringBuilder.append("\t\tbuilder.append(\"");
		} else {
			stringBuilder.append("\t\tbuilder.append(\", ");			
		}
		
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("List=\");");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		
		// if (null == memberList) {
		stringBuilder.append("\t\tif (null == ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("List) {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		// builder.append("null");
		stringBuilder.append("\t\t\tbuilder.append(\"null\");");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		// } else {
		stringBuilder.append("\t\t} else {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		
		// int memberListSize = memberList.size();
		stringBuilder.append("\t\t\tint ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("ListSize = ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("List.size();");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		// if (0 == memberListSize) {				
		stringBuilder.append("\t\t\tif (0 == ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("ListSize) {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		// builder.append("empty");
		stringBuilder.append("\t\t\t\tbuilder.append(\"empty\");");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		// } else {
		stringBuilder.append("\t\t\t} else {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		// [
		stringBuilder.append("\t\t\t\tbuilder.append(\"[\");");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		
		// for (int i=0; i < memberListSize; i++) {
		stringBuilder.append("\t\t\t\tfor (int i=0; i < ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("ListSize; i++) {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		// Member member = memberList.get(i);
		stringBuilder.append("\t\t\t\t\t");
		stringBuilder.append(arrayInfo.getFirstUpperItemName());
		stringBuilder.append(" ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append(" = ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("List.get(i);");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		
		
		// builder.append("member=[");
		stringBuilder.append("\t\t\t\t\tif (0 == i) {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		
		stringBuilder.append("\t\t\t\t\t\tbuilder.append(\"");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("[\");");
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\t\t\t\t\t} else {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		
		stringBuilder.append("\t\t\t\t\t\tbuilder.append(\", ");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append("[\");");
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		
		stringBuilder.append("\t\t\t\t\t}");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		
		
		// builder.append(i);
		stringBuilder.append("\t\t\t\t\tbuilder.append(i);");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		// builder.append("]=");
		stringBuilder.append("\t\t\t\t\tbuilder.append(\"]=\");");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		// builder.append(member.toString());
		stringBuilder.append("\t\t\t\t\tbuilder.append(");
		stringBuilder.append(arrayInfo.getItemName());
		stringBuilder.append(".toString());");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		
		// }
		stringBuilder.append("\t\t\t\t}");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		
		// ]
		stringBuilder.append("\t\t\t\tbuilder.append(\"]\");");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		
		// }
		stringBuilder.append("\t\t\t}");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		
		// }
		stringBuilder.append("\t\t}");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		
		return stringBuilder.toString();		
	}
	
	public String buildStringOfToStringPart(int depth, boolean isFirstElement, GroupInfo groupInfo) {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		
		if (isFirstElement) {
			stringBuilder.append("\t\tbuilder.append(\"");
		} else {
			stringBuilder.append("\t\tbuilder.append(\", ");			
		}
		
		stringBuilder.append(groupInfo.getItemName());
		stringBuilder.append("=\");");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\t\tbuilder.append(");		
		stringBuilder.append(groupInfo.getItemName());		
		stringBuilder.append(".toString());");
		
		return stringBuilder.toString();
	}
	
	public String buildStringOfToStringPart(int depth, String firstUpperItemName, OrderedItemSet itemSet) {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\t@Override");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\tpublic String toString() {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\t\tStringBuilder builder = new StringBuilder();");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		
		if (0 == depth) {
			stringBuilder.append("\t\tbuilder.append(\"class ");
		} else {
			stringBuilder.append("\t\tbuilder.append(\"");
		}
		
		// stringBuilder.append(arrayInfo.getFirstUpperItemName());
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
					
					stringBuilder.append(buildStringOfToStringPart(depth, isFirstElement, singleItemInfo));
					break;
				}
				case ARRAY :  {
					ArrayInfo arrayInfo = (ArrayInfo)itemInfo;					
					
					stringBuilder.append(buildStringOfToStringPart(depth, isFirstElement, arrayInfo));					
					break;
				}
				case GROUP :  {
					GroupInfo groupInfo = (GroupInfo)itemInfo;
					
					stringBuilder.append(buildStringOfToStringPart(depth, isFirstElement, groupInfo));
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
			for (int i=0; i < depth; i++) {
				stringBuilder.append("\t");
			}	
			stringBuilder.append("\t\tbuilder.append(\", messageHeaderInfo=\");");
			
			// builder.append(messageHeaderInfo.toString());
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			for (int i=0; i < depth; i++) {
				stringBuilder.append("\t");
			}	
			stringBuilder.append("\t\tbuilder.append(messageHeaderInfo.toString());");
		}
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}	
		stringBuilder.append("\t\tbuilder.append(\"]\");");
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}		
		stringBuilder.append("\t\treturn builder.toString();");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("\t}");
		
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
		
		stringBuilder.append(buildStringOfVariableDeclarationPart(0, messageInfo.getOrderedItemSet()));
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(buildStringOfMethodDefinePart(0, messageInfo.getOrderedItemSet()));
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		stringBuilder.append(buildStringOfToStringPart(0, messageInfo.getFirstLowerMessageID(), messageInfo.getOrderedItemSet()));

		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("}");
		
		return stringBuilder.toString();
	}

}
