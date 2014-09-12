package kr.pe.sinnori.gui.lib;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.LoginEcho.LoginEcho;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;

public interface AsynMainControllerIF extends MainControllerIF {
	
	/**
	 * 파일 다운로드 준비된 서버측에 다운로드하고자 하는 파일 전체를 요구하는 메시지를 보낸다.
	 * @return 가상적으로 만들어진 파일 다운로드 요청 출력 메시지 
	 */
	public MessageResult doDownloadFileAll();

	
	/**
	 * 서버에서 보내는 비동기 출력 메시지 처리
	 * @param outObj 서버에서 보내는 비동기 출력 메시지
	 */
	public void doAsynOutputMessageTask(AbstractMessage outObj);
	
	public LoginEcho doLoginEcho();
	
	public boolean doUploadFile(int serverTargetFileID, int fileBlockNo, byte[] fileData);
}
