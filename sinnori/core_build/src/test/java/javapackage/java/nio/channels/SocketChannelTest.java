package javapackage.java.nio.channels;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class SocketChannelTest {
protected InternalLogger log = InternalLoggerFactory.getInstance(SocketChannelTest.class);
	
	@Test 
	public void test_open할수있는SocketChannel최대갯수() {
		List<SocketChannel> socketChannelList = new LinkedList<SocketChannel>();
		try {
			for (int i=0; i < Integer.MAX_VALUE; i++) {
				SocketChannel tempSocketChannel = SocketChannel.open();
				socketChannelList.add(tempSocketChannel);
			}
			
			fail("no Exception");
		} catch(Exception e) {
			log.warn("error", e);			
		} finally {
			log.info("socketChannelList.size={}", socketChannelList.size());
			for (SocketChannel tempSocketChannel : socketChannelList) {
				try {
					tempSocketChannel.close();
				} catch (IOException e) {
				}
			}
		}		
	}
}
