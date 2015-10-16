package kr.pe.sinnori.gui.lib;

public interface MessageInfoManagerIF {
	public void readAllMessageInfo();
	
	public void retry(int row, kr.pe.sinnori.gui.message.builder.info.MessageInfo messageInfo);
	
	public void readMessageInfoWithSearchKeyword();
}
