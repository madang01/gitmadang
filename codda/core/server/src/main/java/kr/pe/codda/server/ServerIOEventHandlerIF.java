package kr.pe.codda.server;

import java.nio.channels.SelectionKey;

public interface ServerIOEventHandlerIF {
	public void onRead(SelectionKey selectedKey) throws InterruptedException;
	public void onWrite(SelectionKey selectedKey) throws InterruptedException;
	// public boolean canRead();
}
