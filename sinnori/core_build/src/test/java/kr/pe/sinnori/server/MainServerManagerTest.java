package kr.pe.sinnori.server;

import static org.junit.Assert.fail;

import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import kr.pe.sinnori.client.AnyProjectConnectionPool;
import kr.pe.sinnori.client.AnyProjectConnectionPoolIF;
import kr.pe.sinnori.client.ConnectionPoolManager;
import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDDefiner;
import kr.pe.sinnori.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.type.ConnectionType;
import kr.pe.sinnori.common.type.MessageProtocolType;
import kr.pe.sinnori.common.type.ProjectType;
import kr.pe.sinnori.impl.message.Empty.Empty;

public class MainServerManagerTest extends AbstractJunitTest {

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
	public void test2() {
		String testProjectName = "sample_test";
		ProjectPartConfiguration projectPartConfigurationForTest = null;

		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.DHB;
		ConnectionType connectionTypeForTest = ConnectionType.ASYN_PRIVATE;

		String host = null;
		// host = "172.30.1.16";
		host = "localhost";
		
		try {
			projectPartConfigurationForTest = getMainProjectPartConfiguration(testProjectName,
					host,  9091,
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
			
			for (int i = 0; i < 10000; i++) {
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

		// log.info(projectPartConfigurationForTest.toString());
	}

	private ProjectPartConfiguration getMainProjectPartConfiguration(String projectName,
			String host, int port,
			MessageProtocolType messageProtocolTypeForTest, ConnectionType connectionTypeForTest)
			throws SinnoriConfigurationException {
		ProjectPartConfiguration projectPartConfigurationForTest = new ProjectPartConfiguration(ProjectType.MAIN,
				projectName);
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
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_READ_SELECTOR_WAKEUP_INTERVAL_ITEMID)
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
				.toString(), "kr/pe/sinnori/impl/mybatis/mybatisConfig.xml");

		return projectPartConfigurationForTest;
	}
}
