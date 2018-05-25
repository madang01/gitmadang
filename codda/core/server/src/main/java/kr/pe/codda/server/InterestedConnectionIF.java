package kr.pe.codda.server;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public interface InterestedConnectionIF {
	public SelectionKey keyFor(Selector ioEventSelector);
	public void onRead(SelectionKey selectedKey) throws InterruptedException;
	public void onWrite(SelectionKey selectedKey) throws InterruptedException;
	
	// public void close() throws IOException;
	// public void releaseResources();
	public int hashCode();
}
