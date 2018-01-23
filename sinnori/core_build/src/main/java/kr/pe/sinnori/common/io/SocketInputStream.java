package kr.pe.sinnori.common.io;

import java.nio.charset.CharsetDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.SinnoriBufferUnderflowException;

public class SocketInputStream extends FreeSizeInputStream {

	public SocketInputStream(int dataPacketBufferMaxCount, List<WrapBuffer> dataPacketBufferList,
			CharsetDecoder streamCharsetDecoder, DataPacketBufferPoolManagerIF dataPacketBufferQueueManager) {
		super(dataPacketBufferMaxCount, dataPacketBufferList, streamCharsetDecoder, dataPacketBufferQueueManager);
	}
	
	/**
	 * nothing
	 */
	public void close() {
		
	}	
	
	public byte[] getMD5(long size, int blockSize)
			throws IllegalArgumentException, SinnoriBufferUnderflowException {
		if (size < 0) {
			String errorMessage = new StringBuilder("the parameter size[").append(size).append("] is less than zero")
					.toString();
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (size > available()) {
			String errorMessage = String.format("the parameter size'[%d] is greater than the numbfer of bytes[%d] remaing in this input stream", size,
					available());
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		
		if (blockSize <= 0) {
			String errorMessage = new StringBuilder("the parameter blockSize[").append(blockSize).append("] is less than or equal to zero")
					.toString();
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (0 == size) {
			byte md5Bytes[] = new byte[CommonStaticFinalVars.MD5_BYTESIZE];
			Arrays.fill(md5Bytes, CommonStaticFinalVars.ZERO_BYTE);
			return md5Bytes;
		}
		
		java.security.MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}

		byte md5Bytes[] = null;
		
		byte dataBytes[] = new byte[blockSize];
		
		do {
			if (size > dataBytes.length) {
				getBytes(dataBytes);
				md5.update(dataBytes);
				size -= dataBytes.length;
			} else  {				
				getBytes(dataBytes, 0, (int)size);
				md5.update(dataBytes, 0, (int)size);
				size = 0;
			}
			
			/*byte value = this.getByte();
			
			md5.update(value);
			size--;*/
		} while (size > 0);
		
		md5Bytes = md5.digest();		
		return md5Bytes;
	}
	
	
	/*public SocketInputStream getDuplicatedStream() {
		// List<WrapBuffer> dataPacketBufferList;
		// indexOfWorkBuffer
		
		int dataPacketBufferListSize = dataPacketBufferList.size();
		// int duplicatedListSize = dataPacketBufferListSize - indexOfWorkBuffer;
		
		List<WrapBuffer> duplicatedList = new ArrayList<WrapBuffer>();
		for (int i=getIndexOfWorkBuffer(); i < dataPacketBufferListSize; i++) {
			WrapBuffer originalWrapBuffer = dataPacketBufferList.get(i);
			ByteBuffer originalByteBuffer = originalWrapBuffer.getByteBuffer();
			ByteBuffer duplicatedByteBuffer = originalByteBuffer.duplicate();
			duplicatedByteBuffer.order(originalByteBuffer.order());
			WrapBuffer duplicatedWrapBuffer = new WrapBuffer(duplicatedByteBuffer);
			duplicatedList.add(duplicatedWrapBuffer);
		}
		
		
		return new SocketInputStream(dataPacketBufferMaxCount, duplicatedList, streamCharsetDecoder, dataPacketBufferQueueManager);
	}*/
	
}
