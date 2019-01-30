package kr.pe.codda.common.message.builder;

import java.util.List;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.builder.info.AbstractItemInfo;
import kr.pe.codda.common.message.builder.info.ArrayInfo;
import kr.pe.codda.common.message.builder.info.GroupInfo;
import kr.pe.codda.common.message.builder.info.MessageInfo;
import kr.pe.codda.common.message.builder.info.OrderedItemSet;
import kr.pe.codda.common.message.builder.info.SingleItemInfo;
import kr.pe.codda.common.type.ItemInfoType;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.HexUtil;


public class MessageFileContensBuilder extends AbstractSourceFileBuildre {
	
	/**
	 * 내부 클래스가 있는 경우에 호출
	 * @param depth 깊이
	 * @param itemSet 배열 정보
	 * @return 배열 정보를 바탕으로만들어진 내부 클래스
	 */
	public void addClassDefinePart(StringBuilder contentsStringBuilder, int depth, String firstUpperItemName, OrderedItemSet itemSet) {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));		
		contentsStringBuilder.append("public static class ");
		contentsStringBuilder.append(firstUpperItemName);
		contentsStringBuilder.append(" {");		
		addVariableDeclarationPart(contentsStringBuilder, depth+1, itemSet);
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		addMethodDefinePart(contentsStringBuilder, depth+1, itemSet);
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		addToStringPart(contentsStringBuilder, depth+1, firstUpperItemName, itemSet);		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("}");
	}
	
	
	public void addVariableDeclarationPartForSingleItemInfo(StringBuilder contentsStringBuilder, int depth, SingleItemInfo singleItemInfo) {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("private ");
		contentsStringBuilder.append(singleItemInfo.getJavaLangTypeOfItemType());
		
		contentsStringBuilder.append(" ");
		contentsStringBuilder.append(singleItemInfo.getItemName());
		
		String defaultValueRightValueString = singleItemInfo.getDefaultValueForVariableDeclarationPart();
		if (null != defaultValueRightValueString) {
			contentsStringBuilder.append(" = ");
			contentsStringBuilder.append(defaultValueRightValueString);
		}
		contentsStringBuilder.append(";");
	}
	
	public void addVariableDeclarationPartForArrayInfo(StringBuilder contentsStringBuilder, int depth, ArrayInfo arrayInfo) {
		addClassDefinePart(contentsStringBuilder, depth, arrayInfo.getFirstUpperItemName(), arrayInfo.getOrderedItemSet());
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("private java.util.List<");
		contentsStringBuilder.append(arrayInfo.getFirstUpperItemName());
		contentsStringBuilder.append("> ");
		contentsStringBuilder.append(arrayInfo.getItemName());
		contentsStringBuilder.append("List;");
	}
	
	
	public void addVariableDeclarationPartForGroupInfo(StringBuilder contentsStringBuilder, int depth, GroupInfo groupInfo) {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		addClassDefinePart(contentsStringBuilder, depth+1, groupInfo.getFirstUpperItemName(), groupInfo.getOrderedItemSet());
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("private ");
		contentsStringBuilder.append(groupInfo.getFirstUpperItemName());
		contentsStringBuilder.append(" ");
		contentsStringBuilder.append(groupInfo.getItemName());
		contentsStringBuilder.append(";");
	}
	
	
	public void addVariableDeclarationPart(StringBuilder contentsStringBuilder, int depth, OrderedItemSet itemSet) {
		List<AbstractItemInfo> itemInfoList = itemSet.getItemInfoList();
		for (AbstractItemInfo itemInfo: itemInfoList) {
			ItemInfoType itemInfoType = itemInfo.getItemInfoType();
			switch (itemInfoType) {	
				case SINGLE :  {
					SingleItemInfo singleItemInfo = (SingleItemInfo)itemInfo;
					addVariableDeclarationPartForSingleItemInfo(contentsStringBuilder, depth, singleItemInfo);					
					break;
				}
				case ARRAY :  {
					contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
					ArrayInfo arrayInfo = (ArrayInfo)itemInfo;
					addVariableDeclarationPartForArrayInfo(contentsStringBuilder, depth, arrayInfo);
					
					break;
				}
				case GROUP :  {
					contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
					GroupInfo groupInfo = (GroupInfo)itemInfo;
					addVariableDeclarationPartForGroupInfo(contentsStringBuilder, depth, groupInfo);					
					break;
				}
				default: {
					log.error("unknwon item type[{}]", itemInfoType.toString());
					System.exit(1);
					break;
				}
			}
		}
	}
	
	
	public void addGetMethodDefinePartForSingleItemInfo(StringBuilder contentsStringBuilder, int depth, SingleItemInfo singleItemInfo) {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("public ");
		contentsStringBuilder.append(singleItemInfo.getJavaLangTypeOfItemType());
		contentsStringBuilder.append(" get");
		
		contentsStringBuilder.append(singleItemInfo.getFirstUpperItemName());
		contentsStringBuilder.append("() {");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contentsStringBuilder.append("return ");
		contentsStringBuilder.append(singleItemInfo.getItemName());
		contentsStringBuilder.append(";");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("}");
	}
	
	public void addSetMethodDefinePartForSingleItemInfo(StringBuilder contentsStringBuilder, int depth, SingleItemInfo singleItemInfo) {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("public void set");
		contentsStringBuilder.append(singleItemInfo.getFirstUpperItemName());
		contentsStringBuilder.append("(");
		contentsStringBuilder.append(singleItemInfo.getJavaLangTypeOfItemType());
		contentsStringBuilder.append(" ");
		contentsStringBuilder.append(singleItemInfo.getItemName());
		contentsStringBuilder.append(") {");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contentsStringBuilder.append("this.");
		contentsStringBuilder.append(singleItemInfo.getItemName());
		contentsStringBuilder.append(" = ");
		contentsStringBuilder.append(singleItemInfo.getItemName());
		contentsStringBuilder.append(";");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("}");
	}
	
	public void addGetMethodDefinePartForArrayInfo(StringBuilder contentsStringBuilder, int depth, ArrayInfo arrayInfo) {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("public java.util.List<");
		contentsStringBuilder.append(arrayInfo.getFirstUpperItemName());
		contentsStringBuilder.append("> get");				
		contentsStringBuilder.append(arrayInfo.getFirstUpperItemName());
		contentsStringBuilder.append("List() {");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contentsStringBuilder.append("return ");
		contentsStringBuilder.append(arrayInfo.getItemName());
		contentsStringBuilder.append("List;");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("}");
	}
	
	public void addSetMethodDefinePartForArrayInfo(StringBuilder contentsStringBuilder, int depth, ArrayInfo arrayInfo) {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("public void set");
		contentsStringBuilder.append(arrayInfo.getFirstUpperItemName());
		contentsStringBuilder.append("List(java.util.List<");
		contentsStringBuilder.append(arrayInfo.getFirstUpperItemName());
		contentsStringBuilder.append("> ");
		contentsStringBuilder.append(arrayInfo.getItemName());
		contentsStringBuilder.append("List) {");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contentsStringBuilder.append("this.");
		contentsStringBuilder.append(arrayInfo.getItemName());
		contentsStringBuilder.append("List = ");
		contentsStringBuilder.append(arrayInfo.getItemName());
		contentsStringBuilder.append("List;");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("}");
	}
	
	public void addGetMethodDefinePartForGroupInfo(StringBuilder contentsStringBuilder, int depth, GroupInfo groupInfo) {
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("public ");
		contentsStringBuilder.append(groupInfo.getFirstUpperItemName());
		contentsStringBuilder.append(" get");				
		contentsStringBuilder.append(groupInfo.getFirstUpperItemName());
		contentsStringBuilder.append("() {");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contentsStringBuilder.append("return ");
		contentsStringBuilder.append(groupInfo.getItemName());
		contentsStringBuilder.append(";");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("}");
	}
	
	public void addSetMethodDefinePartForGroupInfo(StringBuilder contentsStringBuilder, int depth, GroupInfo groupInfo) {		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("public void set");
		contentsStringBuilder.append(groupInfo.getFirstUpperItemName());
		contentsStringBuilder.append("(");
		contentsStringBuilder.append(groupInfo.getFirstUpperItemName());
		contentsStringBuilder.append(" ");
		contentsStringBuilder.append(groupInfo.getItemName());
		contentsStringBuilder.append(") {");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contentsStringBuilder.append("this.");
		contentsStringBuilder.append(groupInfo.getItemName());
		contentsStringBuilder.append(" = ");
		contentsStringBuilder.append(groupInfo.getItemName());
		contentsStringBuilder.append(";");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("}");
	}
	
	/**
	 * 메소드 부분
	 * @param depth
	 * @param itemSet
	 * @return
	 */
	public void addMethodDefinePart(StringBuilder contentsStringBuilder, int depth, OrderedItemSet itemSet) {
		List<AbstractItemInfo> itemInfoList = itemSet.getItemInfoList();
		for (AbstractItemInfo itemInfo:itemInfoList) {
			
			ItemInfoType itemInfoType = itemInfo.getItemInfoType();			
			switch (itemInfoType) {	
				case SINGLE :  {
					SingleItemInfo singleItemInfo = (SingleItemInfo)itemInfo;
					addGetMethodDefinePartForSingleItemInfo(contentsStringBuilder, depth, singleItemInfo);
					contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
					addSetMethodDefinePartForSingleItemInfo(contentsStringBuilder, depth, singleItemInfo);
					break;
				}
				case ARRAY :  {
					ArrayInfo arrayInfo = (ArrayInfo)itemInfo;
					addGetMethodDefinePartForArrayInfo(contentsStringBuilder, depth, arrayInfo);
					contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
					addSetMethodDefinePartForArrayInfo(contentsStringBuilder, depth, arrayInfo);
					break;
				}
				case GROUP :  {					
					GroupInfo groupInfo = (GroupInfo)itemInfo;					
					addGetMethodDefinePartForGroupInfo(contentsStringBuilder, depth, groupInfo);
					contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
					addSetMethodDefinePartForGroupInfo(contentsStringBuilder, depth, groupInfo);
					
					break;
				}
				default : {
					log.error("unknwon item type[{}]", itemInfoType.toString());
					System.exit(1);
					break;
				}
			}
		}
	}
	
	
	public void addToStringPartForSingleItemInfo(StringBuilder contentsStringBuilder, int depth, boolean isFirstElement, SingleItemInfo singleItemInfo) {		
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		
		if (isFirstElement) {
			contentsStringBuilder.append("builder.append(\"");
		} else {
			contentsStringBuilder.append("builder.append(\", ");			
		}
		
		contentsStringBuilder.append(singleItemInfo.getItemName());
		contentsStringBuilder.append("=\");");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("builder.append(");
		
		if (singleItemInfo.getItemTypeName().indexOf("length byte[]") >= 0) {
			contentsStringBuilder.append(HexUtil.class.getName());
			contentsStringBuilder.append(".getHexStringFromByteArray(");
			contentsStringBuilder.append(singleItemInfo.getItemName());
			contentsStringBuilder.append(", 0, Math.min(");
			contentsStringBuilder.append(singleItemInfo.getItemName());
			contentsStringBuilder.append(".length, 7))");
		} else {
			// builder.append(longVar1);
			contentsStringBuilder.append(singleItemInfo.getItemName());
		}
		
		contentsStringBuilder.append(");");		
	}
	
	public void addToStringPartForArrayInfo(StringBuilder contentsStringBuilder, int depth, boolean isFirstElement, ArrayInfo arrayInfo) {		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		
		if (isFirstElement) {
			contentsStringBuilder.append("builder.append(\"");
		} else {
			contentsStringBuilder.append("builder.append(\", ");			
		}
		
		contentsStringBuilder.append(arrayInfo.getItemName());
		contentsStringBuilder.append("List=\");");
		
		
		// if (null == memberList) {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("if (null == ");
		contentsStringBuilder.append(arrayInfo.getItemName());
		contentsStringBuilder.append("List) {");
		
		// builder.append("null");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contentsStringBuilder.append("builder.append(\"null\");");		
		// } else {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("} else {");		
		// int memberListSize = memberList.size();
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contentsStringBuilder.append("int ");
		contentsStringBuilder.append(arrayInfo.getItemName());
		contentsStringBuilder.append("ListSize = ");
		contentsStringBuilder.append(arrayInfo.getItemName());
		contentsStringBuilder.append("List.size();");		
		// if (0 == memberListSize) {		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contentsStringBuilder.append("if (0 == ");
		contentsStringBuilder.append(arrayInfo.getItemName());
		contentsStringBuilder.append("ListSize) {");		
		// builder.append("empty");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
		contentsStringBuilder.append("builder.append(\"empty\");");		
		// } else {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contentsStringBuilder.append("} else {");		
		// builder.append("[");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
		contentsStringBuilder.append("builder.append(\"[\");");
		// for (int i=0; i < memberListSize; i++) {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
		contentsStringBuilder.append("for (int i=0; i < ");
		contentsStringBuilder.append(arrayInfo.getItemName());
		contentsStringBuilder.append("ListSize; i++) {");
		// Member member = memberList.get(i);
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 3));
		contentsStringBuilder.append(arrayInfo.getFirstUpperItemName());
		contentsStringBuilder.append(" ");
		contentsStringBuilder.append(arrayInfo.getItemName());
		contentsStringBuilder.append(" = ");
		contentsStringBuilder.append(arrayInfo.getItemName());
		contentsStringBuilder.append("List.get(i);");
		// builder.append("member=[");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 3));
		contentsStringBuilder.append("if (0 == i) {");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 4));		
		contentsStringBuilder.append("builder.append(\"");
		contentsStringBuilder.append(arrayInfo.getItemName());
		contentsStringBuilder.append("[\");");
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 3));
		contentsStringBuilder.append("} else {");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 4));		
		contentsStringBuilder.append("builder.append(\", ");
		contentsStringBuilder.append(arrayInfo.getItemName());
		contentsStringBuilder.append("[\");");		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 3));
		contentsStringBuilder.append("}");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 3));
		// builder.append(i);
		contentsStringBuilder.append("builder.append(i);");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 3));
		// builder.append("]=");
		contentsStringBuilder.append("builder.append(\"]=\");");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 3));
		// builder.append(member.toString());
		contentsStringBuilder.append("builder.append(");
		contentsStringBuilder.append(arrayInfo.getItemName());
		contentsStringBuilder.append(".toString());");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
		// }
		contentsStringBuilder.append("}");				
		// builder.append("]=");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 2));
		contentsStringBuilder.append("builder.append(\"]\");");
		// }
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));		
		contentsStringBuilder.append("}");
		// }
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("}");		
	}
	
	public void addToStringPartForGroupInfo(StringBuilder contentsStringBuilder, int depth, boolean isFirstElement, GroupInfo groupInfo) {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));		
		if (isFirstElement) {
			contentsStringBuilder.append("builder.append(\"");
		} else {
			contentsStringBuilder.append("builder.append(\", ");			
		}
		
		contentsStringBuilder.append(groupInfo.getItemName());
		contentsStringBuilder.append("=\");");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("builder.append(");		
		contentsStringBuilder.append(groupInfo.getItemName());		
		contentsStringBuilder.append(".toString());");
	}
	
	public void addToStringPart(StringBuilder contentsStringBuilder, int depth, String firstUpperItemName, OrderedItemSet itemSet) {		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("@Override");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("public String toString() {");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contentsStringBuilder.append("StringBuilder builder = new StringBuilder();");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		if (0 == depth) {
			contentsStringBuilder.append("builder.append(\"class ");
		} else {
			contentsStringBuilder.append("builder.append(\"");
		}
		contentsStringBuilder.append(firstUpperItemName);
		contentsStringBuilder.append("[\");");
		
		List<AbstractItemInfo> itemInfoList = itemSet.getItemInfoList();
		for (int j=0; j < itemInfoList.size(); j++) {
			contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			boolean isFirstElement = (0 == j);
			
			AbstractItemInfo itemInfo = itemInfoList.get(j);
			
			ItemInfoType itemInfoType = itemInfo.getItemInfoType();
			
			switch (itemInfoType) {	
				case SINGLE :  {
					SingleItemInfo singleItemInfo = (SingleItemInfo)itemInfo;
					
					addToStringPartForSingleItemInfo(contentsStringBuilder, depth+1, isFirstElement, singleItemInfo);
					break;
				}
				case ARRAY :  {
					ArrayInfo arrayInfo = (ArrayInfo)itemInfo;					
					
					addToStringPartForArrayInfo(contentsStringBuilder, depth+1, isFirstElement, arrayInfo);					
					break;
				}
				case GROUP :  {
					GroupInfo groupInfo = (GroupInfo)itemInfo;
					
					addToStringPartForGroupInfo(contentsStringBuilder, depth+1, isFirstElement, groupInfo);
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
			contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
			contentsStringBuilder.append("builder.append(\", messageHeaderInfo=\");");
			
			// builder.append(messageHeaderInfo.toString());
			contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));	
			contentsStringBuilder.append("builder.append(messageHeaderInfo.toString());");
		}
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));	
		contentsStringBuilder.append("builder.append(\"]\");");
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 1));
		contentsStringBuilder.append("return builder.toString();");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(CommonStaticUtil.getPrefixWithTabCharacters(depth, 0));
		contentsStringBuilder.append("}");
	}
	
	public String buildStringOfFileContents(String messageID, String author, MessageInfo messageInfo) {
		StringBuilder contentsStringBuilder = new StringBuilder();
		
		addLincensePart(contentsStringBuilder);
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		addPackageDeclarationPart(contentsStringBuilder, messageID);

		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);		
		Class<?> importClazzes[] = {
				AbstractMessage.class
				/*new StringBuilder()
				.append("import ")
				.append(AbstractMessage.class.getName())
				.append(";").toString()*/
		};
		addImportDeclarationsPart(contentsStringBuilder, importClazzes);
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		addSourceFileDescriptionPart(contentsStringBuilder, messageID, author, "message");
		
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		contentsStringBuilder.append("public class ");
		contentsStringBuilder.append(messageID);
		contentsStringBuilder.append(" extends AbstractMessage {");
		
		addVariableDeclarationPart(contentsStringBuilder, 1, messageInfo.getOrderedItemSet());
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		addMethodDefinePart(contentsStringBuilder, 1, messageInfo.getOrderedItemSet());
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		addToStringPart(contentsStringBuilder, 1, messageInfo.getFirstLowerMessageID(), messageInfo.getOrderedItemSet());

		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append("}");
		
		return contentsStringBuilder.toString();
	}

}
