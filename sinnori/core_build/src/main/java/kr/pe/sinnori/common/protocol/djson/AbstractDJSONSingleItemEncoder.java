package kr.pe.sinnori.common.protocol.djson;

import java.nio.charset.Charset;

import org.json.simple.JSONObject;

import kr.pe.sinnori.common.type.SingleItemType;

public abstract class AbstractDJSONSingleItemEncoder {
	abstract public void putValue(String itemName, Object itemValue, int itemSize,
			Charset itemCharset,  JSONObject jsonObjForOutputStream)
			throws Exception;
	
	abstract public SingleItemType getSingleItemType();
}