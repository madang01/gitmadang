package kr.pe.sinnori.server;

import static org.junit.Assert.fail;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import kr.pe.sinnori.client.AnyProjectConnectionPool;
import kr.pe.sinnori.client.AnyProjectConnectionPoolIF;
import kr.pe.sinnori.client.ConnectionPoolManager;
import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.type.ConnectionType;
import kr.pe.sinnori.common.type.MessageProtocolType;
import kr.pe.sinnori.common.type.ProjectType;
import kr.pe.sinnori.impl.message.Empty.Empty;

public class MainServerManagerTest extends AbstractJunitTest {
	private ProjectPartConfiguration getMainProjectPartConfiguration(String projectName,
			String host, int port,
			MessageProtocolType messageProtocolType, ConnectionType connectionType)
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
		// final ConnectionType connectionType = ConnectionType.ASYN_PRIVATE;
		long clientSocketTimeout = 6000L;			
		int clientConnectionCount = 1;
		int clientConnectionMaxCount = 1;
		int clientAsynPirvateMailboxCntPerPublicConnection = 1;
		int clientAsynInputMessageQueueSize = 5;
		int clientAsynOutputMessageQueueSize = 5;
		long clientWakeupIntervalOfSelectorForReadEventOnly = 10L;			
		int clientAsynInputMessageWriterPoolSize = 2;			
		int clientAsynOutputMessageReaderPoolSize = 2;			
		int clientAsynExecutorPoolSize =2;
		long serverMonitorTimeInterval = 5000L;
		int serverMaxClients = clientConnectionCount*2;
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
		/*
		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_HOST_ITEMID).toString(), host);
		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_PORT_ITEMID).toString(), port);
		projectPartConfigurationForTest
				.mapping(
						new StringBuilder("mainproject.")
								.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_BYTEORDER_ITEMID).toString(),
						ByteOrder.BIG_ENDIAN);
		projectPartConfigurationForTest
				.mapping(
						new StringBuilder("mainproject.")
								.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_CHARSET_ITEMID).toString(),
						CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_DATA_PACKET_BUFFER_MAX_CNT_PER_MESSAGE_ITEMID)
				.toString(), 1000);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_DATA_PACKET_BUFFER_SIZE_ITEMID).toString(), 4096);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_DATA_PACKET_BUFFER_POOL_SIZE_ITEMID).toString(),
				1000);

		projectPartConfigurationForTest.mapping(
				new StringBuilder("mainproject.")
						.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_MESSAGE_ID_FIXED_SIZE_ITEMID).toString(),
				20);

		projectPartConfigurationForTest.mapping(
				new StringBuilder("mainproject.")
						.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_MESSAGE_PROTOCOL_TYPE_ITEMID).toString(),
				messageProtocolTypeForTest);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_FIRST_PREFIX_DYNAMIC_CLASS_FULL_NAME_ITEMID)
				.toString(), "kr.pe.sinnori.impl.");

		projectPartConfigurationForTest.mapping(
				new StringBuilder("mainproject.")
						.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_MONITOR_TIME_INTERVAL_ITEMID).toString(),
				1000L);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_SOCKET_TIMEOUT_ITEMID).toString(), 5000L);

		projectPartConfigurationForTest.mapping(
				new StringBuilder("mainproject.")
						.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_TYPE_ITEMID).toString(),
				connectionTypeForTest);

		projectPartConfigurationForTest.mapping(
				new StringBuilder("mainproject.")
						.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_MAX_COUNT_ITEMID).toString(),
				3);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_COUNT_ITEMID).toString(), 2);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.").append(
				ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_PIRVATE_MAILBOX_CNT_PER_PUBLIC_CONNECTION_ITEMID)
				.toString(), 2);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_INPUT_MESSAGE_QUEUE_SIZE_ITEMID).toString(),
				10);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_OUTPUT_MESSAGE_QUEUE_SIZE_ITEMID).toString(),
				10);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_INPUT_MESSAGE_WRITER_POOL_SIZE_ITEMID)
				.toString(), 2);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_OUTPUT_MESSAGE_READER_POOL_SIZE_ITEMID)
				.toString(), 2);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_EXECUTOR_POOL_SIZE_ITEMID).toString(), 2);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_READ_ONLY_SELECTOR_WAKEUP_INTERVAL_ITEMID)
				.toString(), 5000L);

		projectPartConfigurationForTest.mapping(
				new StringBuilder("mainproject.")
						.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_MONITOR_TIME_INTERVAL_ITEMID).toString(),
				1000L);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_MAX_CLIENTS_ITEMID).toString(), 5);

		projectPartConfigurationForTest.mapping(
				new StringBuilder("mainproject.")
						.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_ACCEPT_QUEUE_SIZE_ITEMID).toString(),
				5);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_INPUT_MESSAGE_QUEUE_SIZE_ITEMID).toString(),
				5);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_OUTPUT_MESSAGE_QUEUE_SIZE_ITEMID).toString(),
				5);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_READ_ONLY_SELECTOR_WAKEUP_INTERVAL_ITEMID)
				.toString(), 10L);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_ACCEPT_PROCESSOR_MAX_SIZE_ITEMID).toString(),
				2);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_ACCEPT_PROCESSOR_SIZE_ITEMID).toString(), 1);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_INPUT_MESSAGE_READER_MAX_SIZE_ITEMID)
				.toString(), 3);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_INPUT_MESSAGE_READER_SIZE_ITEMID).toString(),
				2);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_EXECUTOR_MAX_SIZE_ITEMID)
				.toString(), 3);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_EXECUTOR_SIZE_ITEMID).toString(),
				2);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_OUTPUT_MESSAGE_WRITER_MAX_SIZE_ITEMID)
				.toString(), 3);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_OUTPUT_MESSAGE_WRITER_SIZE_ITEMID)
				.toString(), 2);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_MYBATIS_CONFIG_FILE_RELATIVE_PATH_STRING_ITEMID)
				.toString(), "kr/pe/sinnori/impl/mybatis/mybatisConfig.xml");*/

		return projectPartConfigurationForTest;
	}
	

	@Test
	public void testEmpty메시지송수신() {
		AnyProjectServer anyProjectServer = null;

		try {
			anyProjectServer = MainServerManager.getInstance().getMainProjectServer();
		} catch (IllegalStateException e) {
			fail(e.getMessage());
		}

		anyProjectServer.startServer();

		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance()
				.getMainProjectConnectionPool();

		long startTime = System.nanoTime();

		try {
			for (int i = 0; i < 1; i++) {
				Empty emptyReq = new Empty();
				AbstractMessage emptyRes = mainProjectConnectionPool.sendSyncInputMessage(emptyReq);

				if (!(emptyRes instanceof Empty)) {
					fail("empty 메시지 수신 실패");
				}

				if (!emptyReq.messageHeaderInfo.equals(emptyRes.messageHeaderInfo)) {
					fail("수신한 empty 메시지의 메시지 헤더가 송신한 empty 메시지의 메시지 헤더와 다릅니다");
				}

				// log.info("[{}] 번째 메시지 완료", i);
			}

		} catch (Exception e) {
			log.warn("error", e);

			String errorMessage = String.format(
					"fail to get a output message::%s",
					e.getMessage());
			
			fail(errorMessage);
		}

		long endTime = System.nanoTime();
		log.info("시간차[{}]", TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));

	}

	/*
	 * public void test3() {
	 * 
	 * AnyProjectServer anyProjectServer = null;
	 * 
	 * try { anyProjectServer =
	 * MainServerManager.getInstance().getMainProjectServer(); } catch
	 * (IllegalStateException e) { fail(e.getMessage()); }
	 * 
	 * anyProjectServer.startServer();
	 * 
	 * String host = "localhost"; int port = 9090;
	 * 
	 * try { Selector selector = Selector.open();
	 * 
	 * InetSocketAddress remoteAddr = new InetSocketAddress(host, port);
	 * 
	 * SocketChannel serverSC = SocketChannel.open(); SelectableChannel
	 * serverSelectableChannel = serverSC.configureBlocking(true);
	 * serverSC.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
	 * serverSC.setOption(StandardSocketOptions.TCP_NODELAY, true);
	 * serverSC.setOption(StandardSocketOptions.SO_LINGER, 0);
	 * 
	 * if (! serverSC.connect(remoteAddr)) { String errorMessage =
	 * String.format("fail to connect the remote address[host:{}, post:{}]", host,
	 * port); throw new IOException(errorMessage); }
	 * 
	 * serverSC.register(selector, SelectionKey.OP_WRITE);
	 * 
	 * selector.close();
	 * 
	 * } catch (Exception e) { log.warn("Exception", e);
	 * fail("error::"+e.getMessage()); } }
	 */
	
	

	@Test
	public void test_프로토콜종류별_그리고_연결종류별검사() {
		String testProjectName = "sample_test";
		ProjectPartConfiguration projectPartConfigurationForTest = null;

		MessageProtocolType[] MessageProtocolTypeList = {
				MessageProtocolType.DHB,
				MessageProtocolType.THB
		};
		
		ConnectionType[] ConnectionTypeList = {
				ConnectionType.ASYN_PRIVATE,
				ConnectionType.ASYN_PUBLIC,
				ConnectionType.SYNC_PRIVATE
		};
		
		String host = null;
		int port;
		
		// host = "172.30.1.16";
		host = "localhost";
		port = 9090;
		
		
		for (MessageProtocolType messageProtocolTypeForTest : MessageProtocolTypeList) {
			for (ConnectionType connectionTypeForTest : ConnectionTypeList) {
				port++;
				
				try {
					projectPartConfigurationForTest = getMainProjectPartConfiguration(testProjectName,
							host,  port,
							messageProtocolTypeForTest, connectionTypeForTest);

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
					
					AnyProjectConnectionPool anyProjectConnectionPoolForTest = new AnyProjectConnectionPool(
							projectPartConfigurationForTest);

					long startTime = System.nanoTime();
					
					for (int i = 0; i < 2; i++) {
						Empty emptyReq = new Empty();
						AbstractMessage emptyRes = anyProjectConnectionPoolForTest.sendSyncInputMessage(emptyReq);

						if (!(emptyRes instanceof Empty)) {
							fail("empty 메시지 수신 실패");
						}

						if (!emptyReq.messageHeaderInfo.equals(emptyRes.messageHeaderInfo)) {
							fail("수신한 empty 메시지의 메시지 헤더가 송신한 empty 메시지의 메시지 헤더와 다릅니다");
						}
					}

					long endTime = System.nanoTime();
					log.info("시간차[{}]", TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));
				} catch (Exception e) {
					log.warn("error", e);

					String errorMessage = String.format(
							"fail to get a output message::%s",
							e.getMessage());

					fail(errorMessage);
				}
				
				log.info("프로토콜 {} 에서 연결 {} 테스트 이상무", 
						messageProtocolTypeForTest.toString(),
						connectionTypeForTest.toString());
			}
		}
	}	
}
