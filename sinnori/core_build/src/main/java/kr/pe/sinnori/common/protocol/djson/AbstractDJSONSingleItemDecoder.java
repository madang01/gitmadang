package kr.pe.sinnori.common.protocol.djson;

import java.nio.charset.Charset;

import org.json.simple.JSONObject;

import kr.pe.sinnori.common.type.SingleItemType;

public abstract class AbstractDJSONSingleItemDecoder {
	abstract public Object getValue(String itemName, int itemSize,
			Charset itemCharset, JSONObject jsonObjForInputStream) throws Exception;
	
	abstract public SingleItemType getSingleItemType();
}