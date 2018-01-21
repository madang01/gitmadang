package kr.pe.sinnori.common.message.builder;

import java.util.ArrayList;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.message.builder.info.AbstractItemInfo;
import kr.pe.sinnori.common.message.builder.info.ArrayInfo;
import kr.pe.sinnori.common.message.builder.info.ItemGroupIF;
import kr.pe.sinnori.common.message.builder.info.SingleItemInfo;


public class MessageFileContensBuilder extends AbstractSourceFileBuildre {
	
	/**
	 * 내부 클래스가 있는 경우에 호출
	 * @param depth 깊이
	 * @param arrayInfo 배열 정보
	 * @return 배열 정보를 바탕으로만들어진 내부 클래스
	 */
	private String getClassPart(int depth, ItemGroupIF arrayInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		
		
		stringBuilder.append("public static class ");
		
		
		stringBuilder.append(arrayInfo.getFirstUpperItemName());
		stringBuilder.append(" {");
		
		stringBuilder.append(getVariableDeclarationPart(depth, arrayInfo));
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getMethodDeclarationPart(depth, arrayInfo));
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getToStringPart(depth, arrayInfo));
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		for (int i=0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("};");
		
		return stringBuilder.toString();
	}
	
	/**
	 * 선언부
	 * @param depth
	 * @param arrayInfo
	 * @return
	 */
	private String getVariableDeclarationPart(int depth, ItemGroupIF arrayInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		
		ArrayList<AbstractItemInfo> itemInfoList = arrayInfo.getItemInfoList();
		for (AbstractItemInfo itemInfo: itemInfoList) {
			if (itemInfo.getMessageItemType() == CommonType.MESSAGE_ITEM_TYPE.SINGLE_ITEM) {
				SingleItemInfo singleItemInfo = (SingleItemInfo)itemInfo;
				
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\tprivate ");
				stringBuilder.append(singleItemInfo.getJavaLangTypeOfItemValueType());
				
				stringBuilder.append(" ");
				stringBuilder.append(singleItemInfo.getItemName());
				
				String defaultValueRightValueString = singleItemInfo.getDefaultValueForVariableDeclarationPart();
				if (null != defaultValueRightValueString) {
					stringBuilder.append(" = ");
					stringBuilder.append(defaultValueRightValueString);
				}
				stringBuilder.append(";");
			} else {
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				ArrayInfo arrayInfoOfChild = (ArrayInfo) itemInfo;
				stringBuilder.append(getClassPart(depth+1, arrayInfoOfChild));
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\tprivate java.util.List<");
				stringBuilder.append(arrayInfoOfChild.getFirstUpperItemName());
				stringBuilder.append("> ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List;");
			}
		}
		
		return stringBuilder.toString();
	}
	
	/**
	 * 메소드 부분
	 * @param depth
	 * @param arrayInfo
	 * @return
	 */
	private String getMethodDeclarationPart(int depth, ItemGroupIF arrayInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		ArrayList<AbstractItemInfo> itemInfoList = arrayInfo.getItemInfoList();
		for (AbstractItemInfo itemInfo:itemInfoList) {
			if (itemInfo.getMessageItemType() == CommonType.MESSAGE_ITEM_TYPE.SINGLE_ITEM) {
				SingleItemInfo singleItemInfo = (SingleItemInfo)itemInfo;
				
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\tpublic ");
				stringBuilder.append(singleItemInfo.getJavaLangTypeOfItemValueType());
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
				
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\tpublic void set");
				stringBuilder.append(singleItemInfo.getFirstUpperItemName());
				stringBuilder.append("(");
				stringBuilder.append(singleItemInfo.getJavaLangTypeOfItemValueType());
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
				
			} else {
				ArrayInfo arrayInfoOfChild = (ArrayInfo) itemInfo;
				
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\tpublic java.util.List<");
				stringBuilder.append(arrayInfoOfChild.getFirstUpperItemName());
				stringBuilder.append("> get");				
				stringBuilder.append(arrayInfoOfChild.getFirstUpperItemName());
				stringBuilder.append("List() {");
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\treturn ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List;");
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t}");
				
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\tpublic void set");
				stringBuilder.append(arrayInfoOfChild.getFirstUpperItemName());
				stringBuilder.append("List(java.util.List<");
				stringBuilder.append(arrayInfoOfChild.getFirstUpperItemName());
				stringBuilder.append("> ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List) {");
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\tthis.");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List = ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List;");
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t}");
			}
		}
		return stringBuilder.toString();
	}
	
	private String getToStringPart(int depth, ItemGroupIF arrayInfo) {
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
		
		stringBuilder.append(arrayInfo.getFirstUpperItemName());
		stringBuilder.append("[\");");
		
		ArrayList<AbstractItemInfo> itemInfoList = arrayInfo.getItemInfoList();
		for (int j=0; j < itemInfoList.size(); j++) {
			AbstractItemInfo itemInfo = itemInfoList.get(j);
			
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			for (int i=0; i < depth; i++) {
				stringBuilder.append("\t");
			}
			
			if (0 == j) {
				stringBuilder.append("\t\tbuilder.append(\"");
			} else {
				stringBuilder.append("\t\tbuilder.append(\", ");
			}
			
			if (itemInfo.getMessageItemType() == CommonType.MESSAGE_ITEM_TYPE.SINGLE_ITEM) {
				SingleItemInfo singleItemInfo = (SingleItemInfo)itemInfo;
				
				stringBuilder.append(singleItemInfo.getItemName());
				stringBuilder.append("=\");");
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\tbuilder.append(");
				
				if (singleItemInfo.getItemValueType().indexOf("length byte[]") >= 0) {
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
				
			} else {
				ArrayInfo arrayInfoOfChild = (ArrayInfo) itemInfo;
				
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List=\");");
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				
				// if (null == memberList) {
				stringBuilder.append("\t\tif (null == ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
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
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("ListSize = ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List.size();");
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				
				// if (0 == memberListSize) {				
				stringBuilder.append("\t\t\tif (0 == ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
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
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("ListSize; i++) {");
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				// Member member = memberList.get(i);
				stringBuilder.append("\t\t\t\t\t");
				stringBuilder.append(arrayInfoOfChild.getFirstUpperItemName());
				stringBuilder.append(" ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append(" = ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
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
				stringBuilder.append(arrayInfoOfChild.getItemName());
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
				stringBuilder.append(arrayInfoOfChild.getItemName());
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
				stringBuilder.append(arrayInfoOfChild.getItemName());
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
	
	public String getFileContents(String messageID, String author, kr.pe.sinnori.common.message.builder.info.MessageInfo messageInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(getLincenseString());
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("package ");
		stringBuilder.append(dynamicClassBasePackageName);
		stringBuilder.append(messageID);
		stringBuilder.append(";");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		stringBuilder.append("import kr.pe.sinnori.common.message.AbstractMessage;");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("/**");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(" * ");
		stringBuilder.append(messageID);
		stringBuilder.append(" 메시지");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(" * @author ");
		stringBuilder.append(author);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(" *");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(" */");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		stringBuilder.append("public class ");
		stringBuilder.append(messageID);
		stringBuilder.append(" extends AbstractMessage {");
		
		stringBuilder.append(getVariableDeclarationPart(0, messageInfo));
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(getMethodDeclarationPart(0, messageInfo));
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		stringBuilder.append(getToStringPart(0, messageInfo));

		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("}");
		
		return stringBuilder.toString();
	}

}
