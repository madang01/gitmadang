package kr.pe.codda.gui.helper.iobuilder.screen;

import kr.pe.codda.common.message.builder.info.MessageInfo;

public interface FileFunctionManagerIF {
	// public void buildMessageInfoListFromAllMessageInfoFile();
	
	public void updateRowOfMessageInfoTableAccordingToNewMessageInfoUpdate(int row, MessageInfo newMessageInfo);
	
	// public void buildMessageInfoListFromMessageInfoFilesHavingSearchKeyword();
}
