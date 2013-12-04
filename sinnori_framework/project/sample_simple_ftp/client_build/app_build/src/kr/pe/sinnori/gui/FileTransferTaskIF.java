package kr.pe.sinnori.gui;

public interface FileTransferTaskIF {
	public void doTask();
	public void setFileTranferProcessDialog(FileTranferProcessDialog fileTranferProcessDialog);
	public void cancelTask();
}
