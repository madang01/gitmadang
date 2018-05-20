package kr.pe.codda.common.updownfile;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.config.subset.CommonPartConfiguration;
import kr.pe.codda.common.etc.LimitedLongBitSet;
import kr.pe.codda.common.exception.UpDownFileException;

public abstract class AbstractFileResource {
	protected InternalLogger log = InternalLoggerFactory.getInstance(AbstractFileResource.class);
	
	protected String ownerID = null;
	

	protected int sourceFileID = Integer.MAX_VALUE;
	protected int targetFileID = Integer.MIN_VALUE;

	protected boolean append = false;

	protected String sourceFilePathName = null;
	protected String sourceFileName = null;
	protected long sourceFileSize = -1;

	protected String targetFilePathName = null;
	protected String targetFileName = null;
	protected long targetFileSize = -1;

	protected int fileBlockSize = -1;
	protected long startFileBlockNo = -1;
	protected long endFileBlockNo = -1;
	
	
	/** 주의점 : BitSet 의 크기는 동적으로 자란다. 따라서 해당 비트 인덱스에 대한 엄격한 제한이 필요하다. */
	protected LimitedLongBitSet workedFileBlockBitSet = null;
	
	
	private int fileBlockMaxSize = -1;

	/******* view 와 관련된 모듈 시작 ***************/
	protected FileTranferProcessInformationDialogIF fileTranferProcessInformationDialog = null;

	private void noticeAddedFileData(int fileBlockNo) {
		if (null != fileTranferProcessInformationDialog) {
			FileTransferInformation fileBlockInformation = new FileTransferInformation(
					sourceFileID,			
					targetFileID,
					sourceFileSize,
					targetFileSize,
					startFileBlockNo,
					endFileBlockNo,				
					append, fileBlockSize, fileBlockNo);
			
			fileTranferProcessInformationDialog.noticeAddedFileData(fileBlockInformation.getCurrentLength());
		}
	}
	
	public void setFileTranferProcessInformationDialog(FileTranferProcessInformationDialogIF fileTranferProcessInformationDialog) {
		if (null == fileTranferProcessInformationDialog) {
			throw new IllegalArgumentException("the parameter fileTranferProcessInformationDialog is null");
		}
		this.fileTranferProcessInformationDialog = fileTranferProcessInformationDialog;
	}
	
	
	/**
	 * <pre>
	 * '전송 처리 정보 윈도우' 가 지정되었다면 창을 자동으로 닫는다. 
	 * 단 예외적으로 '전송 완료' 상태인 경우에는 창을 자동으로 닫지 않는다.
	 * 이는 사용자가 최종 전송 완료된 시점의 정보를 확인할 수 있게 해 주는 배려로
	 * 창은 사용자는 OK 버튼 클릭으로 닫히게 된다.
	 * 
	 * <pre>
	 */
	protected void disposeFileTranferProcessInformationDialogIfExistAndNotTransferDone() {		
		if (null != fileTranferProcessInformationDialog) {
			if (! whetherWorkStepIsTransferDoneState()) {
				fileTranferProcessInformationDialog.dispose();
			}
		}
	}
	
	abstract protected boolean whetherWorkStepIsTransferDoneState();
	
	/******* view 와 관련된 모듈 종료 ***************/
	
	public AbstractFileResource(String ownerID, boolean append, String sourceFilePathName, String sourceFileName,
			long sourceFileSize, String targetFilePathName, String targetFileName, long targetFileSize,
			int fileBlockSize) throws IllegalArgumentException, UpDownFileException {
		CoddaConfiguration runningProjectConfiguration = CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();
		CommonPartConfiguration commonPart = runningProjectConfiguration.getCommonPartConfiguration();
		fileBlockMaxSize = commonPart.getFileBlockMaxSize();
		
		if (null == ownerID) {
			throw new IllegalArgumentException("the parameter ownerID is null");
		}
		
		if (null == sourceFilePathName) {			
			throw new IllegalArgumentException("the parameter sourceFilePathName is null");
		}

		sourceFilePathName = sourceFilePathName.trim();

		if (sourceFilePathName.equals("")) {
			throw new IllegalArgumentException("the parameter sourceFilePathName is empty");
		}

		if (null == sourceFileName) {
			throw new IllegalArgumentException("the parameter sourceFileName is null");
		}

		sourceFileName = sourceFileName.trim();

		if (sourceFileName.equals("")) {
			throw new IllegalArgumentException("the parameter sourceFileName is empty");
		}

		if (sourceFileSize <= 0) {
			throw new IllegalArgumentException("the parameter sourceFileSize is less than or equal to zero");
		}

		if (null == targetFilePathName) {
			throw new IllegalArgumentException("the parameter targetFilePathName is null");
		}
		targetFilePathName = targetFilePathName.trim();

		if (targetFilePathName.equals("")) {
			throw new IllegalArgumentException("the parameter targetFilePathName is empty");
		}

		if (null == targetFileName) {
			throw new IllegalArgumentException("the parameter targetFileName is null");
		}

		targetFileName = targetFileName.trim();

		if (targetFileName.equals("")) {
			throw new IllegalArgumentException("the parameter targetFileName is empty");
		}

		if (targetFileSize < 0) {
			throw new IllegalArgumentException("the parameter targetFileSize is less than zero");
		}

		if (fileBlockSize <= 0) {
			throw new IllegalArgumentException("the parameter fileBlockSize is less than or equal to zero");
		}

		if (0 != (fileBlockSize % 1024)) {			
			String errorMessage = String.format(
					"the parameter fileBlockSize[%d] is not a multiple of 1024", fileBlockSize);
			throw new IllegalArgumentException(errorMessage);
		}

		if (fileBlockSize > fileBlockMaxSize) {
			String errorMessage = String.format("the parameter fileBlockSize[%d] is over than max[%d]",
					fileBlockSize, fileBlockMaxSize);
			throw new IllegalArgumentException(errorMessage);
		}

		if (append) {
			/** 이어받기 */
			if (sourceFileSize <= targetFileSize) {
				String errorMessage = String.format("In append mode, the parameter sourceFileSize[%d] must be greater than the parameter targetFileSize[%d]",
						sourceFileSize, targetFileSize);
				throw new IllegalArgumentException(errorMessage);
			}
		}		

		this.ownerID = ownerID;				
		this.append = append;
		this.sourceFilePathName = sourceFilePathName;
		this.sourceFileName = sourceFileName;
		this.sourceFileSize = sourceFileSize;
		this.targetFilePathName = targetFilePathName;
		this.targetFileName = targetFileName;
		this.targetFileSize = targetFileSize;
		this.fileBlockSize = fileBlockSize;

		long sourceFileBlockCount = FileTransferInformation.getFileBlockCountUsingBigDecimal(sourceFileSize, fileBlockSize);
		this.endFileBlockNo = sourceFileBlockCount - 1;
		this.workedFileBlockBitSet = new LimitedLongBitSet(sourceFileBlockCount);

		if (append) {
			/** 이어받기 */
			if (0 == targetFileSize) {
				this.startFileBlockNo = 0;
			} else {
				long targetFileBlockCount = FileTransferInformation.getFileBlockCountUsingBigDecimal(targetFileSize, fileBlockSize);
				this.startFileBlockNo = targetFileBlockCount - 1;			

				/** 이어 받기를 시작하는 위치 전까지 데이터 읽기 여부를 참으로 설정한다. */
				for (int i = 0; i < startFileBlockNo; i++) {
					try {
						this.workedFileBlockBitSet.set(i);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						System.exit(1);
					}
				}
			}
		} else {
			/** 덮어 쓰기 */
			this.startFileBlockNo = 0;			
		}
	}
	
	public String getOwnerID() {
		return ownerID;
	}	

	/**
	 * @return the sourceFileID
	 */
	public int getSourceFileID() {
		return sourceFileID;
	}


	/**
	 * @return the targetFileID
	 */
	public int getTargetFileID() {
		return targetFileID;
	}

	/**
	 * @return the targetFilePathName
	 */
	public String getTargetFilePathName() {
		return targetFilePathName;
	}

	/**
	 * @return the targetFileName
	 */
	public String getTargetFileName() {
		return targetFileName;
	}

	/**
	 * @return the targetFileSize
	 */
	public long getTargetFileSize() {
		return targetFileSize;
	}

	/**
	 * @return the sourceFilePathName
	 */
	public String getSourceFilePathName() {
		return sourceFilePathName;
	}

	/**
	 * @return the sourceFileName
	 */
	public String getSourceFileName() {
		return sourceFileName;
	}

	/**
	 * @return the sourceFileSize
	 */
	public long getSourceFileSize() {
		return sourceFileSize;
	}

	/**
	 * @return the fileBlockSize
	 */
	public long getFileBlockSize() {
		return fileBlockSize;
	}

	/**
	 * @return the startFileBlockNo
	 */
	public int getStartFileBlockNo() {
		return (int) startFileBlockNo;
	}

	/**
	 * @return the endFileBlockNo
	 */
	public int getEndFileBlockNo() {
		return (int) endFileBlockNo;
	}

	/**
	 * <pre>
	 * {@link LocalTargetFileResource#writeTargetFileData(int, int, byte[], boolean)} 에서 호출된다.
	 * 따라서 LocalTargetFileResource 에서는 비트셋의 모든 비트가 켜져 있다는 의미는 파일 복사가 완료 되었다는 의미를 갖는다.
	 * 
	 * 반면에 LocalSourceFileResource 에서는 비트셋의 모든 비트가 켜져 있다는 의미는 개발자가 비트셋을 언제 켰느냐에 따라 의미가 달라진다.
	 * 
	 * 신놀이는 파일 블락 저장이 성공했다는 메시지 결과를 받은후 {@link LocalSourceFileResource#turnOnWorkedFileBlockBitSetAt(int))} 를 호출하도록 권장한다. 
	 * </pre>
	 * 
	 * @param fileBlockNo
	 */
	public void turnOnWorkedFileBlockBitSetAt(int fileBlockNo) {
		if (fileBlockNo < 0) {
			String errorMessage = String.format("targetFileID[%d]::parameter fileBlockNo[%d] is less than zero",
					targetFileID, fileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (fileBlockNo < startFileBlockNo) {
			String errorMessage = String.format(
					"targetFileID[%d]::parameter fileBlockNo[%d] is less than startFileBlockNo[%d]", targetFileID,
					fileBlockNo, startFileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (fileBlockNo > endFileBlockNo) {
			String errorMessage = String.format(
					"targetFileID[%d]::parameter fileBlockNo[%d] is greater than endFileBlockNo[%d]", targetFileID,
					fileBlockNo, endFileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		try {
			if (workedFileBlockBitSet.get(fileBlockNo)) {
				/** 파일 조각 중복 도착 */
				String errorMessage = String.format("targetFileID[%d]::파일 조각[%d] 중복 도착", targetFileID, fileBlockNo);
				log.error(errorMessage);
				System.exit(1);
			} else {
				try {
					workedFileBlockBitSet.set(fileBlockNo);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					System.exit(1);
				}
			}
		} catch (NullPointerException e) {
			/**
			 * 심각한 로직 버그
			 */
			String errorMessage = String.format("변수 workedFileBlockBitSet 가 null 입니다, toString=[%s]", toString());
			log.error(errorMessage);
			System.exit(1);
		}
		
		noticeAddedFileData(fileBlockNo);
	}
	
	/**
	 * <pre>
	 * {@link LocalTargetFileResource#writeTargetFileData(int, int, byte[], boolean)} 에서 호출된다.
	 * 따라서 LocalTargetFileResource 에서는 비트셋의 모든 비트가 켜져 있다는 의미는 파일 복사가 완료 되었다는 의미를 갖는다.
	 * 
	 * 반면에 LocalSourceFileResource 에서는 비트셋의 모든 비트가 켜져 있다는 의미는 개발자가 비트셋을 언제 켰느냐에 따라 의미가 달라진다.
	 * 
	 * 신놀이는 파일 블락 저장이 성공했다는 메시지 결과를 받은후 {@link LocalSourceFileResource#turnOnWorkedFileBlockBitSetAt(int))} 를 호출하도록 권장한다. 
	 * </pre>
	 *   
	 * @return 비트셋 모든 비트가 켜져 있는지 여부를 반환한다. 
	 */
	public boolean whetherAllBitOfBitSetIslTrue() {
		boolean isFinished = false;
		try {
			isFinished = (workedFileBlockBitSet.cardinality() == workedFileBlockBitSet.getMaxBitNumber());
		} catch (NullPointerException e) {
			/**
			 * 심각한 로직 버그
			 */
			String errorMessage = String.format("변수 workedFileBlockBitSet 가 null 입니다, toString=[%s]", toString());
			log.error(errorMessage);
			System.exit(1);
		}

		return isFinished;
	}
	
	
	public long getStartOffset() {
		FileTransferInformation fileBlockInformation = new FileTransferInformation(
				sourceFileID,			
				targetFileID,
				sourceFileSize,
				targetFileSize,
				startFileBlockNo,
				endFileBlockNo,				
				append, fileBlockSize, startFileBlockNo);
		return fileBlockInformation.getCurrentStartOffset();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AbstractFileResource [ownerID=");
		builder.append(ownerID);		
		builder.append(", sourceFileID=");
		builder.append(sourceFileID);
		builder.append(", targetFileID=");
		builder.append(targetFileID);
		builder.append(", append=");
		builder.append(append);
		builder.append(", sourceFilePathName=");
		builder.append(sourceFilePathName);
		builder.append(", sourceFileName=");
		builder.append(sourceFileName);
		builder.append(", sourceFileSize=");
		builder.append(sourceFileSize);
		builder.append(", targetFilePathName=");
		builder.append(targetFilePathName);
		builder.append(", targetFileName=");
		builder.append(targetFileName);
		builder.append(", targetFileSize=");
		builder.append(targetFileSize);
		builder.append(", fileBlockSize=");
		builder.append(fileBlockSize);
		builder.append(", startFileBlockNo=");
		builder.append(startFileBlockNo);
		builder.append(", endFileBlockNo=");
		builder.append(endFileBlockNo);
		
		builder.append(", workedFileBlockBitSet.cardinality()=");
		builder.append(workedFileBlockBitSet.cardinality());
		builder.append(", workedFileBlockBitSet.getMaxBitNumber()=");
		builder.append(workedFileBlockBitSet.getMaxBitNumber());
		
		builder.append(", fileBlockMaxSize=");
		builder.append(fileBlockMaxSize);
		builder.append(", fileTranferProcessInformationDialog=");
		builder.append(fileTranferProcessInformationDialog);
		builder.append("]");
		return builder.toString();
	}
}
