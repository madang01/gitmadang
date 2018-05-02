package kr.pe.sinnori.client.connection;

import static org.junit.Assert.fail;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Set;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.type.ConnectionType;
import kr.pe.sinnori.common.type.MessageProtocolType;
import kr.pe.sinnori.common.type.ProjectType;
import kr.pe.sinnori.server.AnyProjectServer;

public class AsynConnectionTest  extends AbstractJunitTest {
	
	private ProjectPartConfiguration buildMainProjectPartConfiguration(String projectName,
			String host, int port,
			int numberOfConnection,
			MessageProtocolType messageProtocolType,
			ConnectionType connectionType)
			throws SinnoriConfigurationException {		
		 
		
		ProjectPartConfiguration projectPartConfigurationForTest = new ProjectPartConfiguration(ProjectType.MAIN,
				projectName);
		
		ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
		Charset charset = CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET;
		int dataPacketBufferMaxCntPerMessage=50;
		int dataPacketBufferSize=4096;
		int dataPacketBufferPoolSize=1000;
		int messageIDFixedSize=20;
		// MessageProtocolType messageProtocolType;
		String firstPrefixDynamicClassFullName="kr.pe.sinnori.impl.";			
		long clientMonitorTimeInterval = 60*1000*5L;
		// final ConnectionType connectionType = ConnectionType.SYNC_PRIVATE;
		long clientSocketTimeout = 5000L;			
		int clientConnectionCount = numberOfConnection;
		int clientConnectionMaxCount = numberOfConnection;
		int clientAsynPirvateMailboxCntPerPublicConnection = 2;
		int clientAsynInputMessageQueueSize = 5;
		int clientAsynOutputMessageQueueSize = 5;
		long clientWakeupIntervalOfSelectorForReadEventOnly = 10L;			
		int clientAsynInputMessageWriterPoolSize = 2;			
		int clientAsynOutputMessageReaderPoolSize = 2;			
		int clientAsynExecutorPoolSize =2;
		long serverMonitorTimeInterval = 5000L;
		int serverMaxClients = numberOfConnection*2;
		int serverAcceptQueueSize = 5;
		int serverInputMessageQueueSize = 5;
		int serverOutputMessageQueueSize = 5;
		long serverWakeupIntervalOfSelectorForReadEventOnly = 10L;			
		int serverAcceptProcessorSize = 2; 
		int serverAcceptProcessorMaxSize = serverAcceptProcessorSize;			
		int serverInputMessageReaderPoolSize = 2;
		int serverInputMessageReaderPoolMaxSize = serverInputMessageReaderPoolSize;
		int serverExecutorPoolSize = 2;
		int serverExecutorPoolMaxSize = serverExecutorPoolSize;
		int serverOutputMessageWriterPoolSize = 2;
		int serverOutputMessageWriterPoolMaxSize = serverOutputMessageWriterPoolSize;	
		String serverMybatisConfigFileRelativePathString = "kr/pe/sinnori/impl/mybatis/mybatisConfig.xml";
		
		projectPartConfigurationForTest.build(host, port, 
				byteOrder, 
				charset, 
				dataPacketBufferMaxCntPerMessage, 
				dataPacketBufferSize, 
				dataPacketBufferPoolSize, 
				messageIDFixedSize, 
				messageProtocolType, 
				firstPrefixDynamicClassFullName, 
				clientMonitorTimeInterval, 
				connectionType, 
				clientSocketTimeout, 
				clientConnectionCount, 
				clientConnectionMaxCount, 
				clientAsynPirvateMailboxCntPerPublicConnection, 
				clientAsynInputMessageQueueSize, 
				clientAsynOutputMessageQueueSize, 
				clientWakeupIntervalOfSelectorForReadEventOnly, 
				clientAsynInputMessageWriterPoolSize, 
				clientAsynOutputMessageReaderPoolSize, 
				clientAsynExecutorPoolSize, 
				serverMonitorTimeInterval, 
				serverMaxClients, 
				serverAcceptQueueSize, 
				serverInputMessageQueueSize, 
				serverOutputMessageQueueSize, 
				serverWakeupIntervalOfSelectorForReadEventOnly, 
				serverAcceptProcessorSize, 
				serverAcceptProcessorMaxSize, 
				serverInputMessageReaderPoolSize, 
				serverInputMessageReaderPoolMaxSize, 
				serverExecutorPoolSize, 
				serverExecutorPoolMaxSize, 
				serverOutputMessageWriterPoolSize, 
				serverOutputMessageWriterPoolMaxSize, 
				serverMybatisConfigFileRelativePathString);

		return projectPartConfigurationForTest;
	}

	@Test
	public void test연결테스트() {
		String testProjectName = "sample_test";
		ProjectPartConfiguration projectPartConfigurationForTest = null;

		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.DHB;

		String host = null;
		int port;
		
		// host = "172.30.1.16";
		host = "localhost";
		port = 9291;
		
		int numberOfConnection = 2;
		
		try {
			projectPartConfigurationForTest = buildMainProjectPartConfiguration(testProjectName,
					host,  port,
					numberOfConnection,
					messageProtocolTypeForTest,
					ConnectionType.SYNC_PRIVATE);

		} catch (Exception e) {
			log.warn("error", e);

			String errorMessage = String.format(
					"fail to mapping configuration's item value to ProjectPartConfiguration's item value::%s",
					e.getMessage());

			fail(errorMessage);
		}
		
		try {
			AnyProjectServer anyProjectServerForTest = new AnyProjectServer(projectPartConfigurationForTest);

			anyProjectServerForTest.startServer();
			
			Selector sel = Selector.open(); // Create the Selector
			SocketChannel sc = SocketChannel.open(); // Create a SocketChannel
			sc.configureBlocking(false); // ... non blocking
			sc.setOption(StandardSocketOptions.SO_KEEPALIVE, true); // ... set some options

			// Register the Channel to the Selector for wake-up on CONNECT event and use some description as an attachement
			sc.register(sel, SelectionKey.OP_CONNECT, "Connection to sinnori.pe.kr"); // Returns a SelectionKey: the association between the SocketChannel and the Selector

			System.out.println("Initiating connection");
			if (sc.connect(new InetSocketAddress(host, port)))
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
			                    
			                    int newInterestOps = k.interestOps() & ~SelectionKey.OP_CONNECT;
			                    
			                    log.info("newInterestOps={}", newInterestOps);
			                    
			                    k.interestOps(newInterestOps); // We are already connected: remove interest in CONNECT event
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
