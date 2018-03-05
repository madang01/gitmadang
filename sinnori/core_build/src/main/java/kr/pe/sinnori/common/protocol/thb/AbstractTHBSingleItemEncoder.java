package kr.pe.sinnori.common.protocol.thb;

import java.nio.BufferOverflowException;

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriBufferOverflowException;
import kr.pe.sinnori.common.io.BinaryOutputStreamIF;
import kr.pe.sinnori.common.type.SingleItemType;

public abstract class AbstractTHBSingleItemEncoder {
	abstract public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
			String nativeItemCharset, BinaryOutputStreamIF binaryOutputStream)
			throws Exception;
	
	protected void writeItemID(int itemTypeID, BinaryOutputStreamIF binaryOutputStream) throws BufferOverflowException, IllegalArgumentException, SinnoriBufferOverflowException, NoMoreDataPacketBufferException {
		binaryOutputStream.putUnsignedByte(itemTypeID);
	}
	
	abstract public SingleItemType getSingleItemType();
}
