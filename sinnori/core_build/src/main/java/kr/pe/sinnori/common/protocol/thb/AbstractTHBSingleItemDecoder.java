package kr.pe.sinnori.common.protocol.thb;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.SinnoriBufferUnderflowException;
import kr.pe.sinnori.common.exception.UnknownItemTypeException;
import kr.pe.sinnori.common.io.BinaryInputStreamIF;
import kr.pe.sinnori.common.message.builder.info.SingleItemTypeManger;
import kr.pe.sinnori.common.type.SingleItemType;

public abstract class AbstractTHBSingleItemDecoder {
	abstract public Object getValue(int itemTypeID, String itemName, int itemSize,
			String nativeItemCharset, BinaryInputStreamIF binaryInputStream) throws Exception;
	
	protected void throwExceptionIfItemTypeIsDifferent(int itemTypeID, String itemName,
			BinaryInputStreamIF binaryInputStream) throws SinnoriBufferUnderflowException, BodyFormatException {
		int receivedItemTypeID = binaryInputStream.getUnsignedByte();
		if (itemTypeID != receivedItemTypeID) {
			
			String itemTypeName = "unknown";
			try {
				itemTypeName = SingleItemTypeManger.getInstance().getItemTypeName(itemTypeID);
			} catch (UnknownItemTypeException e) {
			}
			
			String receivedItemTypeName = "unknown";
			try {
				receivedItemTypeName = SingleItemTypeManger.getInstance().getItemTypeName(receivedItemTypeID);
			} catch (UnknownItemTypeException e) {
			}
			
			String errorMesssage = String.format("this single item type[id:%d, name:%s][%s] is different from the received item type[id:%d, name:%s]", 
					itemTypeID,
					itemTypeName,
					itemName, 
					receivedItemTypeID,
					receivedItemTypeName);
			throw new BodyFormatException(errorMesssage);
		}
	}	
	
	abstract public SingleItemType getSingleItemType();
}
