package kr.pe.codda.client.connection.asyn;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public interface InterestedAsynConnectionIF {
	public SelectionKey register(Selector ioEventSelector, int wantedInterestOps) throws ClosedChannelException;
	public SelectionKey keyFor(Selector ioEventSelector);
	public boolean finishConnect() throws IOException;
	public boolean doConect() throws IOException;
	public void onConnect(SelectionKey selectedKey);
	public void onRead(SelectionKey selectedKey) throws InterruptedException;
	public void onWrite(SelectionKey selectedKey) throws InterruptedException;
	public void close() throws IOException;
	public void releaseResources();
	public int hashCode();
}
