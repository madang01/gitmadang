package kr.pe.sinnori.client.connection.asyn.noshare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteOrder;

import org.junit.Test;

import kr.pe.sinnori.client.AnyProjectConnectionPool;
import kr.pe.sinnori.client.connection.ConnectionPoolIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.IEOClientThreadPoolSetManagerIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.executor.ClientExecutor;
import kr.pe.sinnori.client.connection.asyn.threadpool.executor.ClientExecutorIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.InputMessageWriter;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.InputMessageWriterIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.OutputMessageReader;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.OutputMessageReaderIF;
import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDDefiner;
import kr.pe.sinnori.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.type.ConnectionType;
import kr.pe.sinnori.common.type.MessageProtocolType;
import kr.pe.sinnori.common.type.ProjectType;
import kr.pe.sinnori.server.AnyProjectServer;

public class AsynPrivateConnectionPoolTest extends AbstractJunitTest {
	/**
	 * 지정한 연결 수 만큼 클라이언트 IEO 담당 쓰레드 수를 일치시킨 프로젝트 부분 환경 설정을 반환한다 
	 * 
	 * @param projectName 프로젝트 이름
	 * @param host 호스트 주소
	 * @param port 포트 번호
	 * @param numberOfConnection 연결수
	 * @param messageProtocolType 프로토콜
	 * @return 지정한 연결 수 만큼 클라이언트 IEO 담당 쓰레드 수를 일치시킨 프로젝트 부분 환경 설정
	 * @throws SinnoriConfigurationException
	 */
	private ProjectPartConfiguration getMainProjectPartConfiguration(String projectName,
			String host, int port,
			int numberOfConnection,
			MessageProtocolType messageProtocolType)
			throws SinnoriConfigurationException {		
		final ConnectionType connectionType = ConnectionType.ASYN_PRIVATE; 
		
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
				messageProtocolType);

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
						connectionType);

		projectPartConfigurationForTest.mapping(
				new StringBuilder("mainproject.")
						.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_MAX_COUNT_ITEMID).toString(),
				3);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_COUNT_ITEMID).toString(), numberOfConnection);

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
				.toString(), numberOfConnection);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_OUTPUT_MESSAGE_READER_POOL_SIZE_ITEMID)
				.toString(), numberOfConnection);

		projectPartConfigurationForTest.mapping(new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_EXECUTOR_POOL_SIZE_ITEMID).toString(), numberOfConnection);

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
				.toString(), "kr/pe/sinnori/impl/mybatis/mybatisConfig.xml");

		return projectPartConfigurationForTest;
	}
	
	
	@Test
	public void testConstructor_연결수검사() {
		String testProjectName = "sample_test";
		ProjectPartConfiguration projectPartConfigurationForTest = null;

		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.DHB;

		String host = null;
		int port;
		
		// host = "172.30.1.16";
		host = "localhost";
		port = 9092;
		
		int numberOfConnection = 2;
		
		try {
			projectPartConfigurationForTest = getMainProjectPartConfiguration(testProjectName,
					host,  port,
					numberOfConnection,
					messageProtocolTypeForTest);

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
			
			ConnectionPoolIF connectionPool = anyProjectConnectionPoolForTest.getconnectionPool();
			
			if (! (connectionPool instanceof AsynPrivateConnectionPool)) {
				fail("the var connectionPool is not a instance of AsynPrivateConnectionPool class");
			}
			
			AsynPrivateConnectionPool asynPrivateConnectionPool = (AsynPrivateConnectionPool)connectionPool;

			asynPrivateConnectionPool.size();
			asynPrivateConnectionPool.getQueueSize();
			
			assertEquals("연결 폴 크기 점검",  numberOfConnection, asynPrivateConnectionPool.size());
			assertEquals("연결 폴의 큐 크기 점검",  numberOfConnection, asynPrivateConnectionPool.getQueueSize());
			
			IEOClientThreadPoolSetManagerIF ieoClientThreadPoolSetManager = anyProjectConnectionPoolForTest
				.getIEOClientThreadPoolSetManager();
						
			ieoClientThreadPoolSetManager.getClientExecutorWithMinimumNumberOfConnetion();
			
		} catch (Exception e) {
			log.warn("error", e);

			String errorMessage = String.format(
					"fail to get a output message::%s",
					e.getMessage());

			fail(errorMessage);
		}
	}
	
	/**
	 * <pre>
	 * 이 테스트는 비동기+비공유 연결 폴의 연결들이 클라이언트 IEO 담당 쓰레드에 각 1개씩 균등하게 받는것을  검사한다.
	 *   
	 * 참고1) IEO 담당 쓰레드 목록
	 *       {@link ClientExecutor}
	 *       {@link InputMessageWriter}
	 *       {@link OutputMessageReader}
	 * 
	 * 참고2) 테스트 환경 구축을 위한 메소드
	 * {@link #getMainProjectPartConfiguration(String, String, int, int, MessageProtocolType) }  
	 * </pre>
	 */
	@Test
	public void testConstructor_IEO쓰레드균등할당검사() {
		String testProjectName = "sample_test";
		ProjectPartConfiguration projectPartConfigurationForTest = null;

		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.DHB;

		String host = null;
		int port;
		
		// host = "172.30.1.16";
		host = "localhost";
		port = 9093;
		
		int numberOfConnection = 2;
				
		try {
			projectPartConfigurationForTest = getMainProjectPartConfiguration(testProjectName,
					host,  port,
					numberOfConnection,
					messageProtocolTypeForTest);

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
						
			
			IEOClientThreadPoolSetManagerIF ieoClientThreadPoolSetManager = anyProjectConnectionPoolForTest
				.getIEOClientThreadPoolSetManager();
						
			ClientExecutorIF minClientExecutor = ieoClientThreadPoolSetManager.getClientExecutorWithMinimumNumberOfConnetion();
			InputMessageWriterIF minInputMessageWriter = ieoClientThreadPoolSetManager.getInputMessageWriterWithMinimumNumberOfConnetion();
			OutputMessageReaderIF minOutputMessageReader = ieoClientThreadPoolSetManager.getOutputMessageReaderWithMinimumNumberOfConnetion();
						
			assertEquals("ClientExecutor 쓰레드에 균등 분배 검사", 1, minClientExecutor.getNumberOfAsynConnection());
			assertEquals("InputMessageWriter 쓰레드에 균등 분배 검사", 1, minInputMessageWriter.getNumberOfAsynConnection());
			assertEquals("OutputMessageReader 쓰레드에 균등 분배 검사", 1, minOutputMessageReader.getNumberOfAsynConnection());
			
		} catch (Exception e) {
			log.warn("error", e);

			String errorMessage = String.format(
					"fail to get a output message::%s",
					e.getMessage());

			fail(errorMessage);
		}
	}
}
