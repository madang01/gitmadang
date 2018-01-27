package kr.pe.sinnori.common.message.builder;

import java.util.ArrayList;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.message.builder.info.AbstractItemInfo;
import kr.pe.sinnori.common.message.builder.info.ArrayInfo;
import kr.pe.sinnori.common.message.builder.info.ItemGroupIF;
import kr.pe.sinnori.common.message.builder.info.SingleItemInfo;
import kr.pe.sinnori.common.message.builder.info.SingleItemTypeManger;


public class EncoderFileContensBuilder extends AbstractSourceFileBuildre {
	
	private String getCountVarName(int depth) {
		StringBuilder countVarNameBuilder = new StringBuilder();
		for (int i=0; i <= depth/2; i++) {
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
				
				// singleItemEncoder.putValueToWritableMiddleObject(echo.getMessageID(), "randomInt"
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\tsingleItemEncoder.putValueToWritableMiddleObject(");
				stringBuilder.append(varName);
				stringBuilder.append("SingleItemPath, \"");
				stringBuilder.append(singleItemInfo.getItemName());
				stringBuilder.append("\"");
				
				/*// itemTypeID
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t\t, ");
				stringBuilder.append(singleItemInfo.getItemTypeID());
				stringBuilder.append(" // itemTypeID");	
				
				// itemTypeName
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t\t, \"");
				stringBuilder.append(singleItemInfo.getItemTypeName());
				stringBuilder.append("\" // itemTypeName");*/
				
				// itemType
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t\t, ");
				stringBuilder.append("SingleItemType.");
				stringBuilder.append(SingleItemTypeManger.getInstance()
						.getSingleItemType(singleItemInfo.getItemTypeID()).name());
				stringBuilder.append(" // itemType");
				// , SingleItemType.UB_PASCAL_STRING
				
				// itemValue
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t\t, ");
				stringBuilder.append(varName);
				stringBuilder.append(".get");
				stringBuilder.append(singleItemInfo.getFirstUpperItemName());
				stringBuilder.append("() // itemValue");
				
				// itemSize
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t\t, ");
				stringBuilder.append(singleItemInfo.getItemSize());
				stringBuilder.append(" // itemSize");
				
				// itemCharset
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t\t, ");
				/*
				 SingleItemType itemType = singleItemInfo.getItemType();
				 if (itemType.equals(SingleItemType.FIXED_LENGTH_STRING) ||
						itemType.equals(SingleItemType.UB_PASCAL_STRING) ||
						itemType.equals(SingleItemType.US_PASCAL_STRING) ||
						itemType.equals(SingleItemType.SI_PASCAL_STRING)) {
				 */
				String nativeItemCharset = singleItemInfo.getNativeItemCharset();
				if (null == nativeItemCharset) {
					stringBuilder.append("null");
				} else {
					stringBuilder.append("\"");
					stringBuilder.append(nativeItemCharset);
					stringBuilder.append("\"");
				}
				stringBuilder.append(" // nativeItemCharset");
				
				
				
				// middleWritableObject
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t\t, ");
				stringBuilder.append(middleObjVarName);
				stringBuilder.append(");");
			} else {
				ArrayInfo arrayInfoOfChild = (ArrayInfo) itemInfo;
				
				// List<AllDataType.Member> memberList = allDataType.getMemberList();	
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
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
				stringBuilder.append("List = ");
				stringBuilder.append(varName);
				stringBuilder.append(".get");
				stringBuilder.append(arrayInfoOfChild.getFirstUpperItemName());
				stringBuilder.append("List();");
				
				// /** 배열 정보와 배열 크기 일치 검사 */
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t/** 배열 정보와 배열 크기 일치 검사 */");
				
				
				// if (null == memberList) {
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\tif (null == ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List) {");
				
				if (arrayInfoOfChild.getArrayCntType().equals("reference")) {
					// /** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t\t/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */");
					
					// if (0 != allDataTypeInObj.getCnt()) {
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t\tif (0 != ");
					stringBuilder.append(varName);
					stringBuilder.append(".get");
					stringBuilder.append(arrayInfoOfChild.getArrayCntValue().substring(0, 1).toUpperCase());
					stringBuilder.append(arrayInfoOfChild.getArrayCntValue().substring(1));
					stringBuilder.append("()) {");
					
					// String errorMessage = new StringBuilder(allDataTypeSingleItemPath)
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t\t\tString errorMessage = new StringBuilder(\"간접 참조 회수[\"");
					stringBuilder.append(")");
					
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);	
					stringBuilder.append("\t\t\t\t.append(");
					stringBuilder.append(varName);
					stringBuilder.append(".get");
					stringBuilder.append(arrayInfoOfChild.getArrayCntValue().substring(0, 1).toUpperCase());
					stringBuilder.append(arrayInfoOfChild.getArrayCntValue().substring(1));
					stringBuilder.append("()");
					stringBuilder.append(")");
					
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);	
					stringBuilder.append("\t\t\t\t.append(");
					stringBuilder.append("\"] is not zero but \")");
					
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);	
					stringBuilder.append("\t\t\t\t.append(");
					stringBuilder.append(varName);
					stringBuilder.append("SingleItemPath");
					stringBuilder.append(")");
					
					// .append(".")
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t\t\t.append(\".\")");
					
					// .append("memberList")
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t\t\t.append(\"");
					stringBuilder.append(arrayInfoOfChild.getItemName());
					stringBuilder.append("List\")");
					
					// .append("is null").toString();
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t\t\t.append(\"is null\").toString();");
					
					// throw new BodyFormatException(errorMessage);
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t\t\tthrow new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);");
					
					// }
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t\t}");
					
				} else {
					// /** 배열 크기 지정 방식이 직접일 경우 배열 값으로 null 을 허용하지 않는다. */
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t/** 배열 크기 지정 방식이 직접일 경우 배열 값으로 null 을 허용하지 않는다. */");
					
					// String errorMessage = new StringBuilder(allDataTypeInObjSingleItemPath)
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\tString errorMessage = new StringBuilder(");
					stringBuilder.append(varName);
					stringBuilder.append("SingleItemPath)");
					
					// .append(".")
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t.append(\".\")");
					
					// .append("memberList")
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t.append(\"");
					stringBuilder.append(arrayInfoOfChild.getItemName());
					stringBuilder.append("List\")");
					
					// .append("is null").toString();
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t.append(\"is null\").toString();");
					
					// throw new BodyFormatException(errorMessage);
					stringBuilder.append(CommonStaticFinalVars.NEWLINE);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\tthrow new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);");
				}
				
				// } else {
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t} else {");
				
				// int memberListSize = memberList.size();
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\tint ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("ListSize = ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List.size();");
				
				// /** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */");
				
				
				// if (memberListSize != allDataTypeInObj.getCnt()) {
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\tif (");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("ListSize != ");
				
				if (arrayInfoOfChild.getArrayCntType().equals("reference")) {
					stringBuilder.append(varName);
					stringBuilder.append(".get");
					stringBuilder.append(arrayInfoOfChild.getArrayCntValue().substring(0, 1).toUpperCase());
					stringBuilder.append(arrayInfoOfChild.getArrayCntValue().substring(1));
					stringBuilder.append("()");
				} else {
					stringBuilder.append(arrayInfoOfChild.getArrayCntValue());
				}				
				
				stringBuilder.append(") {");
				
				// String errorMessage = new StringBuilder(allDataTypeInObjSingleItemPath)
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\tString errorMessage = new StringBuilder(");
				stringBuilder.append(varName);
				stringBuilder.append("SingleItemPath");
				stringBuilder.append(")");
				
				// .append(".")
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t.append(\".\")");
				
				// .append("memberList")
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t.append(\"");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List.length[\")");
				
				// .append(memberListSize)
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t.append(");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("ListSize)");
				
				// .append("] is not same to ")
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t.append(\"] is not same to \")");
				
				// .append(allDataTypeInObjSingleItemPath)
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t.append(");
				stringBuilder.append(varName);
				stringBuilder.append("SingleItemPath)");
				
				// .append(".")
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t.append(\".\")");
				
				// .append("cnt")
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t.append(\"");
				stringBuilder.append(arrayInfoOfChild.getArrayCntValue());
				stringBuilder.append("[\")");
				
				// .append(allDataTypeInObj.getCnt())
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t.append(");
				stringBuilder.append(varName);
				stringBuilder.append(".get");
				stringBuilder.append(arrayInfoOfChild.getArrayCntValue().substring(0, 1).toUpperCase());
				stringBuilder.append(arrayInfoOfChild.getArrayCntValue().substring(1));
				stringBuilder.append("())");
				
				// .append("]").toString();
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t.append(\"]\").toString();");
				
				// throw new BodyFormatException(errorMessage);
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\tthrow new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);");
				
				// }
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t}");
				
				// Object memberMiddleWriteArray = singleItemEncoder.getArrayObjFromMiddleWriteObj(allDataTypeInObjSingleItemPath, "member", memberList.length, middleWritableObject);
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\tObject ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("MiddleWriteArray = singleItemEncoder.getArrayObjFromMiddleWriteObj(");
				stringBuilder.append(varName);
				stringBuilder.append("SingleItemPath, \"");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("\", ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("ListSize, ");
				stringBuilder.append(middleObjVarName);
				stringBuilder.append(");");
				
				// for (int i=0; i < memberListSize; i++) {
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\tfor (int ");
				stringBuilder.append(getCountVarName(depth));
				stringBuilder.append("=0; ");
				stringBuilder.append(getCountVarName(depth));
				stringBuilder.append(" < ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("ListSize; ");
				stringBuilder.append(getCountVarName(depth));
				stringBuilder.append("++) {");
				
				// singleItemPathStatck.push(new StringBuilder(singleItemPathStatck.getLast()).append(".").append("Member").append("[").append(i).append("]").toString());
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\tsingleItemPathStatck.push(new StringBuilder(singleItemPathStatck.getLast()).append(\".\").append(\"");
				stringBuilder.append(arrayInfoOfChild.getFirstUpperItemName());
				stringBuilder.append("\").append(\"[\").append(");
				stringBuilder.append(getCountVarName(depth));
				stringBuilder.append(").append(\"]\").toString());");
				
				// String memberSingleItemPath = singleItemPathStatck.getLast();
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\tString ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("SingleItemPath = singleItemPathStatck.getLast();");
				
				// Object memberMiddleWriteObj = singleItemEncoder.getMiddleWriteObjFromArrayObj(memberSingleItemPath, memberMiddleWriteArray, i);
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\tObject ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("MiddleWriteObj = singleItemEncoder.getMiddleWriteObjFromArrayObj(");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("SingleItemPath, ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("MiddleWriteArray, ");
				stringBuilder.append(getCountVarName(depth));
				stringBuilder.append(");");
				
				// AllDataType.Member member = memberList.get(i);
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t");
				stringBuilder.append(path);
				stringBuilder.append(".");
				stringBuilder.append(arrayInfoOfChild.getFirstUpperItemName());
				stringBuilder.append(" ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append(" = ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List.get(");
				stringBuilder.append(getCountVarName(depth));
				stringBuilder.append(");");
				
				// FIXME!
				stringBuilder.append(toBody(depth+2, path+"."+arrayInfoOfChild.getFirstUpperItemName(), 
						arrayInfoOfChild.getItemName(), arrayInfoOfChild, arrayInfoOfChild.getItemName()+"MiddleWriteObj"));
				
				// singleItemPathStatck.pop();
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\tsingleItemPathStatck.pop();");
				
				// }
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t}");
				
				// }
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t}");
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
				"import java.util.LinkedList;",
				"import kr.pe.sinnori.common.message.AbstractMessage;",
				"import kr.pe.sinnori.common.message.builder.info.SingleItemType;",
				"import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;",
				"import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;"
		};
		stringBuilder.append(getImportPartString(importElements));
		
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("/**");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(" * ");
		stringBuilder.append(messageID);
		stringBuilder.append(" 메시지 인코더");
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
		stringBuilder.append("Encoder extends AbstractMessageEncoder {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t@Override");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\tpublic void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject)");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t\t\tthrows Exception {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t\tif (!(messageObj instanceof ");
		stringBuilder.append(messageID);
		stringBuilder.append(")) {");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t\t\tString errorMessage = String.format(\"메시지 객체 타입[%s]이 ");
		stringBuilder.append(messageID);
		stringBuilder.append(" 이(가) 아닙니다.\", messageObj.getClass().getCanonicalName());");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t\t\tthrow new IllegalArgumentException(errorMessage);");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t\t}");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t\t");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t\t");
		stringBuilder.append(messageID);
		stringBuilder.append(" ");
		stringBuilder.append(firstLowerMessageID);
		stringBuilder.append(" = (");
		stringBuilder.append(messageID);
		stringBuilder.append(") messageObj;");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t\tencodeBody(");
		stringBuilder.append(firstLowerMessageID);
		stringBuilder.append(", singleItemEncoder, middleWritableObject);");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t}");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t/**");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t * <pre>");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t * ");
		stringBuilder.append(messageID);
		stringBuilder.append(" 입력 메시지의 내용을 \"단일항목 인코더\"를 이용하여 \"중간 다리 역활 쓰기 객체\"에 저장한다.");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t * </pre>");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t * @param ");
		stringBuilder.append(firstLowerMessageID);
		stringBuilder.append(" ");
		stringBuilder.append(messageID);
		stringBuilder.append(" 입력 메시지");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t * @param singleItemEncoder 단일항목 인코더");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);		
		stringBuilder.append("\t * @param middleWritableObject 중간 다리 역활 쓰기 객체");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t * @throws Exception \"입력/출력 메시지\"의 내용을 \"단일항목 인코더\"를 이용하여 \"중간 다리 역활 쓰기 객체\"에 저장할때 에러 발생시 던지는 예외");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t */");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\tprivate void encodeBody(");
		stringBuilder.append(messageID);
		stringBuilder.append(" ");
		stringBuilder.append(firstLowerMessageID);
		stringBuilder.append(", SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {");
		
		// String allDataTypeInObjSingleItemPath = "AllDataType";
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t\tString ");
		stringBuilder.append(firstLowerMessageID);
		stringBuilder.append("SingleItemPath = \"");
		stringBuilder.append(messageID);
		stringBuilder.append("\";");
		
		// LinkedList<String> singleItemPathStatck = new LinkedList<String>(); 
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t\tLinkedList<String> singleItemPathStatck = new LinkedList<String>();");
		
		// singleItemPathStatck.push(allDataTypeInObjSingleItemPath);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t\tsingleItemPathStatck.push(");
		stringBuilder.append(firstLowerMessageID);
		stringBuilder.append("SingleItemPath);");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		stringBuilder.append(toBody(0, messageID, firstLowerMessageID, messageInfo, "middleWritableObject"));
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t}");
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("}");
		return stringBuilder.toString();
	}
}
