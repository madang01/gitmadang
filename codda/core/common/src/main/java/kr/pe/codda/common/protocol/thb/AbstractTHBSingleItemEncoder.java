package kr.pe.codda.common.protocol.thb;

import java.nio.BufferOverflowException;

import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.exception.BufferOverflowExceptionWithMessage;
import kr.pe.codda.common.io.BinaryOutputStreamIF;
import kr.pe.codda.common.type.SingleItemType;

public abstract class AbstractTHBSingleItemEncoder {
	abstract public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
			String nativeItemCharset, BinaryOutputStreamIF binaryOutputStream)
			throws Exception;
	
	protected void writeItemID(int itemTypeID, BinaryOutputStreamIF binaryOutputStream) throws BufferOverflowException, IllegalArgumentException, BufferOverflowExceptionWithMessage, NoMoreDataPacketBufferException {
		binaryOutputStream.putUnsignedByte(itemTypeID);
	}
	
	abstract public SingleItemType getSingleItemType();
}
