package kr.pe.sinnori.screen;

public interface SourceManagerIF {
	public void createSourceFile(boolean isSelectedIO, boolean isSelectedDirection, kr.pe.sinnori.common.message.MessageInfo messageInfo);
	public void createAllSourceFiles();
}
