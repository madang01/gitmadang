package kr.pe.codda.server;

import java.nio.channels.SelectionKey;

public interface ServerIOEvenetControllerIF {	
	public void cancel(SelectionKey selectedKey);
}
