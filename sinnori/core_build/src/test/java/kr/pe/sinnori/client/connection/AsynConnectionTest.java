package kr.pe.sinnori.client.connection;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;

public class AsynConnectionTest  extends AbstractJunitTest {

	@Test
	public void test() {
		try {
			Selector sel = Selector.open(); // Create the Selector
			SocketChannel sc = SocketChannel.open(); // Create a SocketChannel
			sc.configureBlocking(false); // ... non blocking
			sc.setOption(StandardSocketOptions.SO_KEEPALIVE, true); // ... set some options

			// Register the Channel to the Selector for wake-up on CONNECT event and use some description as an attachement
			sc.register(sel, SelectionKey.OP_CONNECT, "Connection to sinnori.pe.kr"); // Returns a SelectionKey: the association between the SocketChannel and the Selector

			System.out.println("Initiating connection");
			if (sc.connect(new InetSocketAddress("www.sinnori.pe.kr", 80)))
			    System.out.println("Connected"); // Connected right-away: nothing else to do
			else {
			    boolean exit = false;
			    while (!exit) {
			    	// log.info("11111111111");
			    	
			        if (sel.select(100) == 0) // Did something happen on some registered Channels during the last 100ms?
			            continue; // No, wait some more
			        
			        // sc.keyFor(sel).cancel();
			        
			        // Something happened...
			        Set<SelectionKey> keys = sel.selectedKeys(); // List of SelectionKeys on which some registered operation was triggered
			        for (SelectionKey k : keys) {
			            System.out.println("Checking "+k.attachment());
			            if (k.isConnectable()) { // CONNECT event
			                System.out.print("Connected through select() on "+k.channel()+" -> ");
			                if (sc.finishConnect()) { // Finish connection process
			                    System.out.println("done!");
			                    k.interestOps(k.interestOps() & ~SelectionKey.OP_CONNECT); // We are already connected: remove interest in CONNECT event
			                    exit = true;
			                } else
			                    System.out.println("unfinished...");
			            }
			            // TODO: else if (k.isReadable()) { ...
			        }
			        keys.clear(); // Have to clear the selected keys set once processed!
			    }
			}
			System.out.print("Disconnecting ... ");
			sc.shutdownOutput(); // Initiate graceful disconnection
			// TODO: emtpy receive buffer
			sc.close();
			System.out.println("done");
		} catch(Exception e) {
			log.warn("unknown error", e);
		}
	}
}
