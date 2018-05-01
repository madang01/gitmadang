package kr.pe.sinnori.client.connection.asyn.share;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import kr.pe.sinnori.client.AnyProjectConnectionPool;
import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.ConnectionPoolIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.IEOClientThreadPoolSetManagerIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.executor.ClientExecutorIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.InputMessageWriterIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.OutputMessageReaderIF;
import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.ConnectionPoolException;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.type.ConnectionType;
import kr.pe.sinnori.common.type.MessageProtocolType;
import kr.pe.sinnori.common.type.ProjectType;
import kr.pe.sinnori.impl.message.Empty.Empty;
import kr.pe.sinnori.server.AnyProjectServer;

public class AsynPublicConnectionPoolTest extends AbstractJunitTest {
	private ProjectPartConfiguration buildMainProjectPartConfiguration(String projectName,
			String host, int port,
			int numberOfConnection,
			MessageProtocolType messageProtocolType)
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
		final ConnectionType connectionType = ConnectionType.ASYN_PUBLIC;
		long clientSocketTimeout = 5000L;			
		int clientConnectionCount = numberOfConnection;
		int clientConnectionMaxCount = numberOfConnection;
		int clientAsynPirvateMailboxCntPerPublicConnection = 2;
		int clientAsynInputMessageQueueSize = 5;
		int clientAsynOutputMessageQueueSize = 5;
		long clientWakeupIntervalOfSelectorForReadEventOnly = 2L;			
		int clientAsynInputMessageWriterPoolSize = 2;			
		int clientAsynOutputMessageReaderPoolSize = 2;			
		int clientAsynExecutorPoolSize =2;
		long serverMonitorTimeInterval = 5000L;
		int serverMaxClients = 5;
		int serverAcceptQueueSize = 5;
		int serverInputMessageQueueSize = 5;
		int serverOutputMessageQueueSize = 5;
		long serverWakeupIntervalOfSelectorForReadEventOnly = 2L;			
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
	public void testConstructor_연결수검사() {
		String testProjectName = "sample_test";
		ProjectPartConfiguration projectPartConfigurationForTest = null;

		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.DHB;

		String host = null;
		int port;
		
		// host = "172.30.1.16";
		host = "localhost";
		port = 9490;
		
		int numberOfConnection = 2;
		
		try {
			projectPartConfigurationForTest = buildMainProjectPartConfiguration(testProjectName,
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
			
			if (! (connectionPool instanceof AsynPublicConnectionPool)) {
				fail("the var connectionPool is not a instance of AsynPublicConnectionPool class");
			}
			
			AsynPublicConnectionPool asynPublicConnectionPool = (AsynPublicConnectionPool)connectionPool;
			
			assertEquals("연결 폴 크기 점검",  numberOfConnection, asynPublicConnectionPool.getNumberOfConnection());
			assertEquals("연결 폴의 리스트 크기 점검",  numberOfConnection, asynPublicConnectionPool.getPoolSize());
			
		} catch (Exception e) {
			log.warn("error", e);

			String errorMessage = String.format(
					"fail to get a output message::%s",
					e.getMessage());

			fail(errorMessage);
		}
	}
	
	@Test
	public void testConstructor_IEO쓰레드균등할당검사() {
		String testProjectName = "sample_test";
		ProjectPartConfiguration projectPartConfigurationForTest = null;

		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.DHB;

		String host = null;
		int port;
		
		// host = "172.30.1.16";
		host = "localhost";
		port = 9491;
		
		int numberOfConnection = 2;
				
		try {
			projectPartConfigurationForTest = buildMainProjectPartConfiguration(testProjectName,
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
						
			assertEquals("ClientExecutor 쓰레드에 균등 분배 검사", 1, minClientExecutor.getNumberOfConnection());
			assertEquals("InputMessageWriter 쓰레드에 균등 분배 검사", 1, minInputMessageWriter.getNumberOfConnection());
			assertEquals("OutputMessageReader 쓰레드에 균등 분배 검사", 1, minOutputMessageReader.getNumberOfConnection());
			
		} catch (Exception e) {
			log.warn("error", e);

			String errorMessage = String.format(
					"fail to get a output message::%s",
					e.getMessage());

			fail(errorMessage);
		}
	}
	
	
	@Test
	public void testGetConnection_fastRelease() {
		String testProjectName = "sample_test";
		ProjectPartConfiguration projectPartConfigurationForTest = null;

		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.DHB;

		String host = null;
		int port;
		
		// host = "172.30.1.16";
		host = "localhost";
		port = 9492;
		
		int numberOfConnection = 2;
		
		ConnectionPoolIF connectionPoolForTest = null;
		
		AnyProjectServer anyProjectServerForTest = null;
		try {
			projectPartConfigurationForTest = buildMainProjectPartConfiguration(testProjectName,
					host,  port,
					numberOfConnection,
					messageProtocolTypeForTest);
			
			anyProjectServerForTest = new AnyProjectServer(projectPartConfigurationForTest);

			anyProjectServerForTest.startServer();
			
			AnyProjectConnectionPool anyProjectConnectionPoolForTest = new AnyProjectConnectionPool(
					projectPartConfigurationForTest);
			
			connectionPoolForTest = anyProjectConnectionPoolForTest.getconnectionPool();

		} catch (Exception e) {
			log.warn("error", e);

			String errorMessage = String.format(
					"fail to mapping configuration's item value to ProjectPartConfiguration's item value::%s",
					e.getMessage());

			fail(errorMessage);
		}
		
		
		class FastWorker extends Thread {
			private ConnectionPoolIF connectionPool = null;
			private int retryCount;
			
			public FastWorker(ConnectionPoolIF connectionPool, int retryCount) {
				if (null == connectionPool) {
					throw new IllegalArgumentException("the parameter connectionPool is null");
				}
				this.connectionPool = connectionPool;
				this.retryCount = retryCount;
			}
			
			public void run() {
				
				for (int i=0; i < retryCount; i++) {
					AbstractConnection conn = null;
					try {
						conn = connectionPool.getConnection();
						
						//log.info("conn[{}]={}", i, conn.hashCode());
						
					} catch(Exception e) {
						log.warn(i+"::error", e);
						
						//String errorMessage = String.format("error::%s", e.getMessage());
						//fail(errorMessage);
					} finally {
						if (null != conn) {
							try {
								connectionPool.release(conn);
							} catch (ConnectionPoolException e) {
								log.warn("error", e);
								// fail("fail to release connection");
							}
						}
					}
				}
			}
		}
		
		int fastWorkerListSize = 4;
		int retryCount = 10;
		FastWorker[] fastWorkerList = new FastWorker[fastWorkerListSize];
		
		for (int i=0; i < fastWorkerList.length; i++) {
			fastWorkerList[i] = new FastWorker(connectionPoolForTest, retryCount);
			fastWorkerList[i].start();
		}
		
		for (FastWorker fastWorker : fastWorkerList) {
			try {
				fastWorker.join();
			} catch (InterruptedException e) {
			}
		}
	}
	
	@Test
	public void testGetConnection_ramdomRelease() {
		String testProjectName = "sample_test";
		ProjectPartConfiguration projectPartConfigurationForTest = null;

		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.DHB;

		String host = null;
		int port;
		
		// host = "172.30.1.16";
		host = "localhost";
		port = 9493;
		// port = 9090;
		
		int numberOfConnection = 6;
		
		ConnectionPoolIF connectionPoolForTest = null;
		
		AnyProjectServer anyProjectServerForTest = null;
		try {
			projectPartConfigurationForTest = buildMainProjectPartConfiguration(testProjectName,
					host,  port,
					numberOfConnection,
					messageProtocolTypeForTest);
			
			anyProjectServerForTest = new AnyProjectServer(projectPartConfigurationForTest);

			anyProjectServerForTest.startServer();
			
			AnyProjectConnectionPool anyProjectConnectionPoolForTest = new AnyProjectConnectionPool(
					projectPartConfigurationForTest);
			
			connectionPoolForTest = anyProjectConnectionPoolForTest.getconnectionPool();

		} catch (Exception e) {
			log.warn("error", e);

			String errorMessage = String.format(
					"fail to mapping configuration's item value to ProjectPartConfiguration's item value::%s",
					e.getMessage());

			fail(errorMessage);
		}
		
		class RandomWorker extends Thread {
			private ConnectionPoolIF connectionPool = null;
			private int retryCount;
			
			// private Random random = new Random();
			
			public RandomWorker(ConnectionPoolIF connectionPool, int retryCount) {
				if (null == connectionPool) {
					throw new IllegalArgumentException("the parameter connectionPool is null");
				}
				this.connectionPool = connectionPool;
				this.retryCount = retryCount;
			}
			
			public void run() {
				int countOfClosedConnection = 0;
				
				for (int i=0; i < retryCount; i++) {
					AbstractConnection conn = null;
					try {
						conn = connectionPool.getConnection();
						
						log.info("conn[{}]={}, size={}", i, conn.hashCode(), connectionPool.getNumberOfConnection());
						
						
						long sleepTime = ThreadLocalRandom.current().nextLong(0L, 5001L);						
						
						Thread.sleep(sleepTime);
						
						boolean isClosed = ThreadLocalRandom.current().nextBoolean();
						
						// log.info("isClosed={}", isClosed);
						
						if (0 == countOfClosedConnection  && isClosed) {
							conn.close();
							countOfClosedConnection++;
						}						 
					} catch(Exception e) {
						log.warn(i+"::error", e);
						
						//String errorMessage = String.format("error::%s", e.getMessage());
						//fail(errorMessage);
					} finally {
						if (null != conn) {
							try {
								connectionPool.release(conn);
							} catch (ConnectionPoolException e) {
								log.warn("error", e);
								//fail("fail to release connection");
							}
						}
					}
				}
			}
		}
		
		
		int randomWorkerListSize = 4;
		int retryCount = 10;
		RandomWorker[] randomWorkerList = new RandomWorker[randomWorkerListSize];
		
		for (int i=0; i < randomWorkerList.length; i++) {
			randomWorkerList[i] = new RandomWorker(connectionPoolForTest, retryCount);
			randomWorkerList[i].start();
		}
		
		for (RandomWorker randomWorker : randomWorkerList) {
			try {
				randomWorker.join();
			} catch (InterruptedException e) {
			}
		}
	}
	
	@Test
	public void testGetConnection_slowRelease() {
		String testProjectName = "sample_test";
		ProjectPartConfiguration projectPartConfigurationForTest = null;

		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.DHB;

		String host = null;
		int port;
		
		// host = "172.30.1.16";
		host = "localhost";
		port = 9494;
		
		int numberOfConnection = 2;
		
		ConnectionPoolIF connectionPoolForTest = null;
		
		AnyProjectServer anyProjectServerForTest = null;
		try {
			projectPartConfigurationForTest = buildMainProjectPartConfiguration(testProjectName,
					host,  port,
					numberOfConnection,
					messageProtocolTypeForTest);
			
			anyProjectServerForTest = new AnyProjectServer(projectPartConfigurationForTest);

			anyProjectServerForTest.startServer();
			
			AnyProjectConnectionPool anyProjectConnectionPoolForTest = new AnyProjectConnectionPool(
					projectPartConfigurationForTest);
			
			connectionPoolForTest = anyProjectConnectionPoolForTest.getconnectionPool();	

		} catch (Exception e) {
			log.warn("error", e);

			String errorMessage = String.format(
					"fail to mapping configuration's item value to ProjectPartConfiguration's item value::%s",
					e.getMessage());

			fail(errorMessage);
		}
		
		
		class SlowWorker extends Thread {
			private ConnectionPoolIF connectionPool = null;
			private int retryCount;
			
			public SlowWorker(ConnectionPoolIF connectionPool, int retryCount) {
				if (null == connectionPool) {
					throw new IllegalArgumentException("the parameter connectionPool is null");
				}
				this.connectionPool = connectionPool;
				this.retryCount = retryCount;
			}
			
			
			public void run() {
				
				for (int i=0; i < retryCount; i++) {
					AbstractConnection conn = null;
					try {
						conn = connectionPool.getConnection();
						
						log.info("conn[{}]={}", i, conn.hashCode());
						
						long sleepTime = ThreadLocalRandom.current().nextLong(1000L, 5001L); 
						
						Thread.sleep(sleepTime);
					
					} catch(Exception e) {
						log.warn(i + "::error", e);
						
						// String errorMessage = String.format("error::%s", e.getMessage());
						// fail(errorMessage);
					} finally {
						if (null != conn) {
							try {
								connectionPool.release(conn);
							} catch (ConnectionPoolException e) {
								log.warn("error", e);
								// fail("fail to release connection");
							}
						}
					}
				}
			}
		}
		
		int slowWorkerListSize = 4;
		int retryCount = 10;
		SlowWorker[] slowWorkerList = new SlowWorker[slowWorkerListSize];
		
		for (int i=0; i < slowWorkerList.length; i++) {
			slowWorkerList[i] = new SlowWorker(connectionPoolForTest, retryCount);
			slowWorkerList[i].start();
		}
		
		for (SlowWorker fastWorker : slowWorkerList) {			
			try {
				fastWorker.join();
			} catch (InterruptedException e) {
			}
		}
	}
	
	@Test
	public void testGetConnection_Empty() {
		String testProjectName = "sample_test";
		ProjectPartConfiguration projectPartConfigurationForTest = null;

		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.DHB;

		String host = null;
		int port;
		
		// host = "172.30.1.16";
		host = "localhost";
		port = 9495;
		
		int numberOfConnection = 2;
		
		ConnectionPoolIF connectionPoolForTest = null;
		
		AnyProjectServer anyProjectServerForTest = null;
		try {
			projectPartConfigurationForTest = buildMainProjectPartConfiguration(testProjectName,
					host,  port,
					numberOfConnection,
					messageProtocolTypeForTest);
			
			anyProjectServerForTest = new AnyProjectServer(projectPartConfigurationForTest);

			anyProjectServerForTest.startServer();
			
			AnyProjectConnectionPool anyProjectConnectionPoolForTest = new AnyProjectConnectionPool(
					projectPartConfigurationForTest);
			
			connectionPoolForTest = anyProjectConnectionPoolForTest.getconnectionPool();

		} catch (Exception e) {
			log.warn("error", e);

			String errorMessage = String.format(
					"fail to mapping configuration's item value to ProjectPartConfiguration's item value::%s",
					e.getMessage());

			fail(errorMessage);
		}
		
		
		class EmptyWorker extends Thread {
			private ConnectionPoolIF connectionPool = null;
			private int retryCount;
			
			public EmptyWorker(ConnectionPoolIF connectionPool, int retryCount) {
				if (null == connectionPool) {
					throw new IllegalArgumentException("the parameter connectionPool is null");
				}
				this.connectionPool = connectionPool;
				this.retryCount = retryCount;
			}
			
			public void run() {
				
				for (int i=0; i < retryCount; i++) {
					AbstractConnection conn = null;
					try {
						conn = connectionPool.getConnection();
						
						log.info("conn[{}]={}", i, conn.hashCode());
						
						Empty emptyReq = new Empty();
						
						AbstractMessage emptyRes = null;
						
						try {
							emptyRes = conn.sendSyncInputMessage(emptyReq);
						} catch(IOException e) {
							log.info("IO 에러 발생하여 연결[{}] 끊음", conn.hashCode());
							log.info("io error", e);
							conn.close();
							
							throw e;
						}
						
						if (!(emptyRes instanceof Empty)) {
							fail("empty 메시지 수신 실패");
						}

						if (!emptyReq.messageHeaderInfo.equals(emptyRes.messageHeaderInfo)) {
							fail("수신한 empty 메시지의 메시지 헤더가 송신한 empty 메시지의 메시지 헤더와 다릅니다");
						}
						
					} catch(Exception e) {
						log.warn(i+"::error", e);
						
						// String errorMessage = String.format("error::%s", e.getMessage());
						// fail(errorMessage);
					} finally {
						if (null != conn) {
							try {
								connectionPool.release(conn);
							} catch (ConnectionPoolException e) {
								log.warn("error", e);
								// fail("fail to release connection");
							}
						}
					}
				}
			}
		}
		
		int emptyWorkerListSize = 8;
		int retryCount = 10;
		EmptyWorker[] emptyWorkerList = new EmptyWorker[emptyWorkerListSize];
		
		for (int i=0; i < emptyWorkerList.length; i++) {
			emptyWorkerList[i] = new EmptyWorker(connectionPoolForTest, retryCount);
			emptyWorkerList[i].start();
		}
		
		for (EmptyWorker emptyWorker : emptyWorkerList) {
			try {
				emptyWorker.join();
			} catch (InterruptedException e) {
			}
		}
	}
}
