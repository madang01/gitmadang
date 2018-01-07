package kr.pe.sinnori.common.updownfile;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileBlockInformation {
	private Logger log = LoggerFactory.getLogger(FileBlockInformation.class);
	
	private int sourceFileID;			
	private int targetFileID;
	
	private long currentStartOffset;
	private int currentLength;
	
	public FileBlockInformation(
			int sourceFileID,			
			int targetFileID,
			long sourceFileSize,
			long targetFileSize,
			long startFileBlockNo,
			long endFileBlockNo,
			
			boolean append, int fileBlockSize, long fileBlockNo) {
		if (fileBlockNo < 0) {
			String errorMessage = String.format("sourceFileID[%d]::parameter fileBlockNo[%d] is less than zero",
					sourceFileID, fileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (fileBlockNo < startFileBlockNo) {
			String errorMessage = String.format(
					"sourceFileID[%d]::parameter fileBlockNo[%d] is less than startFileBlockNo[%d]", sourceFileID,
					fileBlockNo, startFileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (fileBlockNo > endFileBlockNo) {
			String errorMessage = String.format(
					"sourceFileID[%d]::parameter fileBlockNo[%d] is greater than endFileBlockNo[%d]", sourceFileID,
					fileBlockNo, endFileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		this.sourceFileID = sourceFileID;
		this.targetFileID = targetFileID;
		this.currentStartOffset = fileBlockNo*fileBlockSize;
		long currentEndOffset = this.currentStartOffset + fileBlockSize;
		
		if (append && fileBlockSize == startFileBlockNo) {
			this.currentStartOffset = targetFileSize;
		}
		
		if (currentEndOffset > sourceFileSize) {
			currentEndOffset = sourceFileSize;
		}
		
		
		this.currentLength = (int)(currentEndOffset - currentStartOffset);
	}

	public long getCurrentStartOffset() {
		return currentStartOffset;
	}

	

	public int getCurrentLength() {
		return currentLength;
	}
	
	/**
	 * Warning! 파일 블락 갯수로 1이상을 반환하기 위해서는 파일 크기는 0 보다 커야 한다.
	 */
	public static long getFileBlockCountUsingBigDecimal(long fileSize, int fileBlockSize) {
		if (fileSize <= 0) {			
			throw new IllegalArgumentException("the parameter fileSize is less than or equal to zero");
		}
		
		if (fileBlockSize <= 0) {			
			throw new IllegalArgumentException("the parameter fileBlockSize is less than or equal to zero");
		}
				
		/**
		 * fileBlockCount = (fileSize + fileBlockSize - 1) / fileBlockSize
		 */
		BigDecimal bigDecimalForEndFileBlockSize = new BigDecimal(fileBlockSize);
		
		
		BigDecimal bigDecimalForEndFileBlockNo = new BigDecimal(fileSize)
				.add(bigDecimalForEndFileBlockSize)
				.subtract(BigDecimal.ONE).divide(bigDecimalForEndFileBlockSize, 0, RoundingMode.DOWN);
		
		long fileBlockCount = bigDecimalForEndFileBlockNo.longValue();
		return fileBlockCount;
	}
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FileBlockInformation [sourceFileID=");
		builder.append(sourceFileID);
		builder.append(", targetFileID=");
		builder.append(targetFileID);
		builder.append(", currentStartOffset=");
		builder.append(currentStartOffset);
		builder.append(", currentLength=");
		builder.append(currentLength);
		builder.append("]");
		return builder.toString();
	}
	
}
