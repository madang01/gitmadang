package kr.pe.sinnori.gui.lib;

import kr.pe.sinnori.impl.message.DownFileDataResult.DownFileDataResult;
import kr.pe.sinnori.impl.message.UpFileDataResult.UpFileDataResult;

public interface SyncMainControllerIF extends MainControllerIF {
	/**
	 * 업로드 준비된 서버측에 업로드할 로컬 파일 조각을 담음 메시지를 보낸다. 
	 * @param serverTargetFileID 서버 송수신 자원 파일 식별자 
	 * @param fileBlockNo 파일 조각 번호
	 * @param fileData 파일 조각 데이터
	 * @return 업로드 조각 전송 출력 메시지
	 */
	public UpFileDataResult doUploadFile(int serverTargetFileID, int fileBlockNo, byte[] fileData);
	
	/**
	 * 다운로드 준비된 서버측에 다운로드하고자 하는 개별 파일 조각을 요구하는 메시지를 보낸다.
	 * @param serverSourceFileID 클라이언트 송수신 자원 파일 식별자
	 * @param fileBlockNo 파일 조각 번호
	 * @return 파일 다운로드 요청 출력 메시지 
	 */
	public DownFileDataResult doDownloadFile(int serverSourceFileID, int fileBlockNo);
}
