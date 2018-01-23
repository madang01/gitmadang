package kr.pe.sinnori.common.etc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import kr.pe.sinnori.common.io.WrapBuffer;

public abstract class WrapBufferUtil {
	public static List<WrapBuffer> getDuplicatedAndReadableWrapBufferList(List<WrapBuffer> sourceWrapBufferList) {
		List<WrapBuffer> duplicatedWrapBufferList = new ArrayList<WrapBuffer>();		
		int sourceWrapBufferListSize = sourceWrapBufferList.size();
		for (int i=0; i < sourceWrapBufferListSize; i++) {
			WrapBuffer sourceWrapBuffer = sourceWrapBufferList.get(i);
			ByteBuffer sourceByteBuffer = sourceWrapBuffer.getByteBuffer();
			ByteBuffer duplicatedByteBuffer = sourceByteBuffer.duplicate();			
			duplicatedByteBuffer.flip();
			duplicatedByteBuffer.order(sourceByteBuffer.order());
			WrapBuffer duplicatedWrapBuffer = new WrapBuffer(duplicatedByteBuffer);
			duplicatedWrapBufferList.add(duplicatedWrapBuffer);
		}
		return duplicatedWrapBufferList;
	}
}
