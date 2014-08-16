package source_file_builder;

import java.util.ArrayList;

import lib.CommonType;
import message.AbstractItemInfo;
import message.ArrayInfo;
import message.ItemGroupInfoIF;
import message.SingleItemInfo;

public class EncoderSourceFileBuilder extends AbstractSourceFileBuildre {
	
	private String getCountVarName(int depth) {
		StringBuilder countVarNameBuilder = new StringBuilder();
		for (int i=0; i <= depth/2; i++) {
			countVarNameBuilder.append("i");
		}
		return countVarNameBuilder.toString();
	}
	
	public String toBody(int depth, String path, String varName, ItemGroupInfoIF arrayInfo, String middleObjVarName) {
		StringBuilder stringBuilder = new StringBuilder();
		
		ArrayList<AbstractItemInfo> itemInfoList = arrayInfo.getItemInfoList();
		for (AbstractItemInfo itemInfo:itemInfoList) {
			if (itemInfo.getLogicalItemGubun() == CommonType.LOGICAL_ITEM_GUBUN.SINGLE_ITEM) {
				SingleItemInfo singleItemInfo = (SingleItemInfo)itemInfo;
				
				// singleItemEncoder.putValueToMiddleWriteObj(echo.getMessageID(), "randomInt"
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\tsingleItemEncoder.putValueToMiddleWriteObj(");
				stringBuilder.append(varName);
				stringBuilder.append("SingleItemPath, \"");
				stringBuilder.append(singleItemInfo.getItemName());
				stringBuilder.append("\"");
				
				// itemTypeID
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t\t, ");
				stringBuilder.append(singleItemInfo.getItemTypeID());
				stringBuilder.append(" // itemTypeID");	
				
				// itemTypeName
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t\t, \"");
				stringBuilder.append(singleItemInfo.getItemType());
				stringBuilder.append("\" // itemTypeName");
				
				// itemValue
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t\t, ");
				stringBuilder.append(varName);
				stringBuilder.append(".get");
				stringBuilder.append(singleItemInfo.getFirstUpperItemName());
				stringBuilder.append("() // itemValue");
				
				// itemSizeForLang
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t\t, ");
				stringBuilder.append(singleItemInfo.getItemSizeForLang());
				stringBuilder.append(" // itemSizeForLang");
				
				// itemCharsetForLang
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t\t, ");
				/**
				 * <pre>
				 * 타입이 고정 문자열 크기일 경우만 문자셋을 지정할 수 있다.
				 * 문자셋 이름이 null 값으로 넘어가면 
				 * 프로토콜 단일 항목 인코더에서는 파라미터로 넘어가는 프로젝트 문자셋으로 지정되며,
				 * 문자셋 이름이 null 이 아니면 
				 * 프로토콜 단일 항목 인코더에서는 문자셋 객체로 변환된다.
				 * 따라서 메시지 정보를 꾸릴때 문자셋 이름이 있을 경우 반듯이 문자셋 객체로 바꿀수 있는 바른 이름인지 검사를 수행해야 한다.  
				 * </pre>
				 */
				
				if (singleItemInfo.getItemType().equals("fixed length string")) {
					String itemCharset = singleItemInfo.getItemCharset();
					if (null == itemCharset) {
						stringBuilder.append("null");
					} else {
						stringBuilder.append("\"");
						stringBuilder.append(itemCharset);
						stringBuilder.append("\"");
					}
				} else {
					stringBuilder.append("null");
				}
				stringBuilder.append(" // itemCharset,");
				
				// charsetOfProject
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t\t, charsetOfProject");
				
				// middleWriteObj
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t\t, ");
				stringBuilder.append(middleObjVarName);
				stringBuilder.append(");");
			} else {
				ArrayInfo arrayInfoOfChild = (ArrayInfo) itemInfo;
				
				// AllDataType.Member[] memberList = allDataTypeInObj.getMemberList();
				stringBuilder.append(lineSeparator);
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t");
				stringBuilder.append(path);
				stringBuilder.append(".");
				stringBuilder.append(arrayInfoOfChild.getFirstUpperItemName());
				stringBuilder.append("[] ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List = ");
				stringBuilder.append(varName);
				stringBuilder.append(".get");
				stringBuilder.append(arrayInfoOfChild.getFirstUpperItemName());
				stringBuilder.append("List();");
				
				// /** 배열 정보와 배열 크기 일치 검사 */
				stringBuilder.append(lineSeparator);
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t/** 배열 정보와 배열 크기 일치 검사 */");
				
				
				// if (null == memberList) {
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\tif (null == ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List) {");
				
				if (arrayInfoOfChild.getArrayCntType().equals("reference")) {
					// /** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
					stringBuilder.append(lineSeparator);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t\t/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */");
					
					// if (0 != allDataTypeInObj.getCnt()) {
					stringBuilder.append(lineSeparator);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t\tif (0 != ");
					stringBuilder.append(varName);
					stringBuilder.append(".get");
					stringBuilder.append(arrayInfoOfChild.getArrayCntValue().substring(0, 1).toUpperCase());
					stringBuilder.append(arrayInfoOfChild.getArrayCntValue().substring(1));
					stringBuilder.append("()) {");
					
					// String errorMessage = new StringBuilder(allDataTypeInObjSingleItemPath)
					stringBuilder.append(lineSeparator);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t\t\tString errorMessage = new StringBuilder(");
					stringBuilder.append(varName);
					stringBuilder.append("SingleItemPath)");
					
					// .append(".")
					stringBuilder.append(lineSeparator);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t\t\t.append(\".\")");
					
					// .append("memberList")
					stringBuilder.append(lineSeparator);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t\t\t.append(\"");
					stringBuilder.append(arrayInfoOfChild.getItemName());
					stringBuilder.append("List\")");
					
					// .append("is null").toString();
					stringBuilder.append(lineSeparator);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t\t\t.append(\"is null\").toString();");
					
					// throw new BodyFormatException(errorMessage);
					stringBuilder.append(lineSeparator);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t\t\tthrow new BodyFormatException(errorMessage);");
					
					// }
					stringBuilder.append(lineSeparator);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t\t}");
					
				} else {
					// /** 배열 크기 지정 방식이 직접일 경우 배열 값으로 null 을 허용하지 않는다. */
					stringBuilder.append(lineSeparator);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t/** 배열 크기 지정 방식이 직접일 경우 배열 값으로 null 을 허용하지 않는다. */");
					
					// String errorMessage = new StringBuilder(allDataTypeInObjSingleItemPath)
					stringBuilder.append(lineSeparator);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\tString errorMessage = new StringBuilder(");
					stringBuilder.append(varName);
					stringBuilder.append("SingleItemPath)");
					
					// .append(".")
					stringBuilder.append(lineSeparator);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t.append(\".\")");
					
					// .append("memberList")
					stringBuilder.append(lineSeparator);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t.append(\"");
					stringBuilder.append(arrayInfoOfChild.getItemName());
					stringBuilder.append("List\")");
					
					// .append("is null").toString();
					stringBuilder.append(lineSeparator);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\t.append(\"is null\").toString();");
					
					// throw new BodyFormatException(errorMessage);
					stringBuilder.append(lineSeparator);
					for (int i=0; i < depth; i++) {
						stringBuilder.append("\t");
					}
					stringBuilder.append("\t\tthrow new BodyFormatException(errorMessage);");
				}
				
				// } else {
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t} else {");
				
				// /** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */");
				
				
				// if (memberList.length != allDataTypeInObj.getCnt()) {
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\tif (");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List.length != ");
				stringBuilder.append(varName);
				stringBuilder.append(".get");
				stringBuilder.append(arrayInfoOfChild.getArrayCntValue().substring(0, 1).toUpperCase());
				stringBuilder.append(arrayInfoOfChild.getArrayCntValue().substring(1));
				stringBuilder.append("()) {");
				
				// String errorMessage = new StringBuilder(allDataTypeInObjSingleItemPath)
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\tString errorMessage = new StringBuilder(");
				stringBuilder.append(varName);
				stringBuilder.append("SingleItemPath");
				stringBuilder.append(")");
				
				// .append(".")
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t.append(\".\")");
				
				// .append("memberList")
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t.append(\"");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List.length[\")");
				
				// .append(memberList.length)
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t.append(");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List.length)");
				
				// .append("] is not same to ")
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t.append(\"] is not same to \")");
				
				// .append(allDataTypeInObjSingleItemPath)
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t.append(");
				stringBuilder.append(varName);
				stringBuilder.append("SingleItemPath)");
				
				// .append(".")
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t.append(\".\")");
				
				// .append("cnt")
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t.append(\"");
				stringBuilder.append(arrayInfoOfChild.getArrayCntValue());
				stringBuilder.append("[\")");
				
				// .append(allDataTypeInObj.getCnt())
				stringBuilder.append(lineSeparator);
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
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\t.append(\"]\").toString();");
				
				// throw new BodyFormatException(errorMessage);
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\tthrow new BodyFormatException(errorMessage);");
				
				// }
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t}");
				
				// Object memberMiddleWriteArray = singleItemEncoder.createArrayObjFromMiddleWriteObj(allDataTypeInObjSingleItemPath, "member", memberList.length, middleWriteObj);
				stringBuilder.append(lineSeparator);
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\tObject ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("MiddleWriteArray = singleItemEncoder.createArrayObjFromMiddleWriteObj(");
				stringBuilder.append(varName);
				stringBuilder.append("SingleItemPath, \"");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("\", ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List.length, ");
				stringBuilder.append(middleObjVarName);
				stringBuilder.append(");");
				
				// for (int i=0; i < memberList.length; i++) {
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\tfor (int ");
				stringBuilder.append(getCountVarName(depth));
				stringBuilder.append("=0; ");
				stringBuilder.append(getCountVarName(depth));
				stringBuilder.append(" < ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List.length; ");
				stringBuilder.append(getCountVarName(depth));
				stringBuilder.append("++) {");
				
				// singleItemPathStatck.push(new StringBuilder(singleItemPathStatck.getLast()).append(".").append("Member").append("[").append(i).append("]").toString());
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\tsingleItemPathStatck.push(new StringBuilder(singleItemPathStatck.getLast()).append(\".\").append(\"");
				stringBuilder.append(arrayInfoOfChild.getFirstUpperItemName());
				stringBuilder.append("\").append(\"[\").append(");
				stringBuilder.append(getCountVarName(depth));
				stringBuilder.append(").append(\"]\").toString());");
				
				// String memberSingleItemPath = singleItemPathStatck.getLast();
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\tString ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("SingleItemPath = singleItemPathStatck.getLast();");
				
				// Object memberMiddleWriteObj = singleItemEncoder.getMiddleWriteObjFromArrayObj(memberSingleItemPath, memberMiddleWriteArray, i);
				stringBuilder.append(lineSeparator);
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
				
				// AllDataType.Member member = memberList[i];
				stringBuilder.append(lineSeparator);
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
				stringBuilder.append("List[");
				stringBuilder.append(getCountVarName(depth));
				stringBuilder.append("];");
				
				// FIXME!
				stringBuilder.append(toBody(depth+2, path+"."+arrayInfoOfChild.getFirstUpperItemName(), 
						arrayInfoOfChild.getItemName(), arrayInfoOfChild, arrayInfoOfChild.getItemName()+"MiddleWriteObj"));
				
				// singleItemPathStatck.pop();
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t\tsingleItemPathStatck.pop();");
				
				// }
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t}");
				
				// }
				stringBuilder.append(lineSeparator);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t}");
			}
		}
		
		return stringBuilder.toString();
	}
	
	public String toString(String messageID,
			String author,
			message.MessageInfo messageInfo) {
		
		String firstLowerMessageID =  messageID.substring(0, 1).toLowerCase() + messageID.substring(1);
		
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(getLincenseString());
		stringBuilder.append(lineSeparator);
		stringBuilder.append("package ");
		stringBuilder.append(dynamicClassBasePackageName);
		stringBuilder.append(messageID);
		stringBuilder.append(";");
		stringBuilder.append(lineSeparator);
		
		stringBuilder.append(lineSeparator);
		stringBuilder.append("import java.nio.charset.Charset;");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("import java.util.LinkedList;");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("import kr.pe.sinnori.common.exception.BodyFormatException;");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("import kr.pe.sinnori.common.message.AbstractMessage;");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("import kr.pe.sinnori.common.message.codec.MessageEncoder;");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;");
		
		stringBuilder.append(lineSeparator);
		
		stringBuilder.append(lineSeparator);
		stringBuilder.append("/**");
		stringBuilder.append(lineSeparator);
		stringBuilder.append(" * ");
		stringBuilder.append(messageID);
		stringBuilder.append(" 메시지 인코더");
		stringBuilder.append(lineSeparator);
		stringBuilder.append(" * @author ");
		stringBuilder.append(author);
		stringBuilder.append(lineSeparator);
		stringBuilder.append(" *");
		stringBuilder.append(lineSeparator);
		stringBuilder.append(" */");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("public final class ");
		stringBuilder.append(messageID);
		stringBuilder.append("Encoder extends MessageEncoder {");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t@Override");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\tpublic void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj)");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t\t\tthrows Exception {");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t\tif (!(messageObj instanceof ");
		stringBuilder.append(messageID);
		stringBuilder.append(")) {");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t\t\tString errorMessage = String.format(\"메시지 객체 타입[%s]이 ");
		stringBuilder.append(messageID);
		stringBuilder.append(" 이(가) 아닙니다.\", messageObj.getClass().getCanonicalName());");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t\t\tthrow new IllegalArgumentException(errorMessage);");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t\t}");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t\t");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t\t");
		stringBuilder.append(messageID);
		stringBuilder.append(" ");
		stringBuilder.append(firstLowerMessageID);
		stringBuilder.append(" = (");
		stringBuilder.append(messageID);
		stringBuilder.append(") messageObj;");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t\tencodeBody(");
		stringBuilder.append(firstLowerMessageID);
		stringBuilder.append(", singleItemEncoder, charsetOfProject, middleWriteObj);");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t}");
		stringBuilder.append(lineSeparator);
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t/**");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t * <pre>");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t * ");
		stringBuilder.append(messageID);
		stringBuilder.append(" 입력 메시지의 내용을 \"단일항목 인코더\"를 이용하여 \"중간 다리 역활 쓰기 객체\"에 저장한다.");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t * </pre>");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t * @param ");
		stringBuilder.append(firstLowerMessageID);
		stringBuilder.append(" ");
		stringBuilder.append(messageID);
		stringBuilder.append(" 입력 메시지");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t * @param singleItemEncoder 단일항목 인코더");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t * @param charsetOfProject 프로젝트 문자셋");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t * @param middleWriteObj 중간 다리 역활 쓰기 객체");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t * @throws Exception \"입력/출력 메시지\"의 내용을 \"단일항목 인코더\"를 이용하여 \"중간 다리 역활 쓰기 객체\"에 저장할때 에러 발생시 던지는 예외");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t */");
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\tprivate void encodeBody(");
		stringBuilder.append(messageID);
		stringBuilder.append(" ");
		stringBuilder.append(firstLowerMessageID);
		stringBuilder.append(", SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj) throws Exception {");
		
		// String allDataTypeInObjSingleItemPath = "AllDataType";
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t\tString ");
		stringBuilder.append(firstLowerMessageID);
		stringBuilder.append("SingleItemPath = \"");
		stringBuilder.append(messageID);
		stringBuilder.append("\";");
		
		// LinkedList<String> singleItemPathStatck = new LinkedList<String>(); 
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t\tLinkedList<String> singleItemPathStatck = new LinkedList<String>();");
		
		// singleItemPathStatck.push(allDataTypeInObjSingleItemPath);
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t\tsingleItemPathStatck.push(");
		stringBuilder.append(firstLowerMessageID);
		stringBuilder.append("SingleItemPath);");
		stringBuilder.append(lineSeparator);
		
		stringBuilder.append(toBody(0, messageID, firstLowerMessageID, messageInfo, "middleWriteObj"));
		
		stringBuilder.append(lineSeparator);
		stringBuilder.append("\t}");
		
		stringBuilder.append(lineSeparator);
		stringBuilder.append("}");
		// System.out.println(stringBuilder.toString());
		return stringBuilder.toString();
	}
}
