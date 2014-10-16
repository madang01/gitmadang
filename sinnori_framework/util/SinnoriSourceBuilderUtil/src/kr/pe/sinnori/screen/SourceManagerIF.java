package kr.pe.sinnori.screen;

public interface SourceManagerIF {
	public boolean createSourceFile(boolean isSelectedIO, boolean isSelectedDirection, kr.pe.sinnori.common.message.MessageInfo messageInfo);
	public void createAllSourceFiles();
}
