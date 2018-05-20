package kr.pe.codda.client.connection.asyn.noshare;

import static org.junit.Assert.fail;

import java.nio.ByteOrder;
import java.nio.charset.Charset;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.CoddaConfigurationException;
import kr.pe.codda.common.type.ConnectionType;
import kr.pe.codda.common.type.MessageProtocolType;
import kr.pe.codda.common.type.ProjectType;

public class AsynNoShareConnectionTest extends AbstractJunitTest {

	private ProjectPartConfiguration buildMainProjectPartConfiguration(String projectName,
			String host, int port,
			int numberOfConnection,
			MessageProtocolType messageProtocolType)
			throws CoddaConfigurationException {		
		 
		
		ProjectPartConfiguration projectPartConfigurationForTest = new ProjectPartConfiguration(ProjectType.MAIN,
				projectName);
		
		ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
		Charset charset = CommonStaticFinalVars.SOURCE_FILE_CHARSET;
		int dataPacketBufferMaxCntPerMessage=50;
		int dataPacketBufferSize=4096;
		int dataPacketBufferPoolSize=1000;
		int messageIDFixedSize=20;
		// MessageProtocolType messageProtocolType;
		String firstPrefixDynamicClassFullName="kr.pe.sinnori.impl.";			
		long clientMonitorTimeInterval = 60*1000*5L;
		final ConnectionType connectionType = ConnectionType.SYNC_PRIVATE;
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
	public void testSendSyncInputMessage() {
		fail("Not yet implemented");
		
		String testProjectName = "sample_test";
		ProjectPartConfiguration projectPartConfigurationForTest = null;

		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.DHB;

		String host = null;
		int port;
		
		// host = "172.30.1.16";
		host = "localhost";
		port = 9290;
		
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
		
		// new AsynNoShareConnection(projectPartConfigurationForTest);
	}
}
