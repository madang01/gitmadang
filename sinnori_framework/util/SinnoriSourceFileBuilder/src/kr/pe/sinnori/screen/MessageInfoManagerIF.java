package kr.pe.sinnori.screen;

public interface MessageInfoManagerIF {
	public void readAllMessageInfo();
	
	public void retry(int row, kr.pe.sinnori.message.MessageInfo messageInfo);
	
	public void readMessageInfoWithSearchKeyword();
}
