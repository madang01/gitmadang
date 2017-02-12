package kr.pe.sinnori.gui.helper.iobuilder.screen;

import kr.pe.sinnori.common.message.builder.info.MessageInfo;

public interface FileFunctionManagerIF {
	// public void buildMessageInfoListFromAllMessageInfoFile();
	
	public void updateRowOfMessageInfoTableAccordingToNewMessageInfoUpdate(int row, MessageInfo newMessageInfo);
	
	// public void buildMessageInfoListFromMessageInfoFilesHavingSearchKeyword();
}
