package kr.pe.sinnori.server;

import static org.junit.Assert.fail;

import java.nio.ByteOrder;

import org.junit.Test;

import kr.pe.sinnori.client.AnyProjectConnectionPoolIF;
import kr.pe.sinnori.client.ConnectionPoolManager;
import kr.pe.sinnori.common.AbstractJunitSupporter;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDDefiner;
import kr.pe.sinnori.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.type.ConnectionType;
import kr.pe.sinnori.common.type.MessageProtocolType;
import kr.pe.sinnori.common.type.ProjectType;
import kr.pe.sinnori.impl.message.Empty.Empty;

public class MainServerManagerTest extends AbstractJunitSupporter {
	
	@Test
	public void testEmpty메시지송수신() {
		
		AnyProjectServer anyProjectServer = null;
		
		try {
			anyProjectServer = MainServerManager.getInstance().getMainProjectServer();
		} catch (IllegalStateException e) {
			fail(e.getMessage());
		}
		
		anyProjectServer.startServer();
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		
		Empty emptyReq = new Empty();
		AbstractMessage emptyRes = null; 
		try {
			emptyRes = mainProjectConnectionPool.sendSyncInputMessage(emptyReq);
		} catch (Exception e) {
			log.warn("error", e);
			fail(e.getMessage());
		}
		
		if (! (emptyRes instanceof Empty)) {
			fail("empty 메시지 수신 실패");
		}
		
		log.info(emptyRes.toString());
	}
	
	@Test
	public void test2() {
		String testProjectName = "sample_test";
		ProjectPartConfiguration projectPartConfiguration = new ProjectPartConfiguration(ProjectType.MAIN, testProjectName);
		
		
		try {
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_HOST_ITEMID).toString(),
					"localhost");
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_PORT_ITEMID).toString(), 
					9090);
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_BYTEORDER_ITEMID).toString(), 
					ByteOrder.BIG_ENDIAN);
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_CHARSET_ITEMID).toString(), 
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_DATA_PACKET_BUFFER_MAX_CNT_PER_MESSAGE_ITEMID).toString(), 
					1000);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_DATA_PACKET_BUFFER_SIZE_ITEMID).toString(), 
					4096);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_DATA_PACKET_BUFFER_POOL_SIZE_ITEMID).toString(), 
					1000);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_MESSAGE_ID_FIXED_SIZE_ITEMID).toString(), 
					50);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_MESSAGE_PROTOCOL_TYPE_ITEMID).toString(), 
					MessageProtocolType.THB);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_FIRST_PREFIX_DYNAMIC_CLASS_FULL_NAME_ITEMID).toString(), 
					"kr.pe.sinnori.impl.");
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_MONITOR_TIME_INTERVAL_ITEMID).toString(), 
					1000L);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_SOCKET_TIMEOUT_ITEMID).toString(), 
					5000L);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_TYPE_ITEMID).toString(), 
					ConnectionType.SYNC_PRIVATE);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_MAX_COUNT_ITEMID).toString(), 
					3);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_COUNT_ITEMID).toString(), 
					1);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_PIRVATE_MAILBOX_CNT_PER_PUBLIC_CONNECTION_ITEMID).toString(), 
					2);
			
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_INPUT_MESSAGE_QUEUE_SIZE_ITEMID).toString(), 
					10);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_OUTPUT_MESSAGE_QUEUE_SIZE_ITEMID).toString(), 
					10);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_INPUT_MESSAGE_WRITER_POOL_SIZE_ITEMID).toString(), 
					2);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_OUTPUT_MESSAGE_READER_POOL_SIZE_ITEMID).toString(), 
					2);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_EXECUTOR_POOL_SIZE_ITEMID).toString(), 
					2);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_READ_SELECTOR_WAKEUP_INTERVAL_ITEMID).toString(), 
					5000L);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_MONITOR_TIME_INTERVAL_ITEMID).toString(), 
					1000L);
			
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_MAX_CLIENTS_ITEMID).toString(), 
					5);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_ACCEPT_QUEUE_SIZE_ITEMID).toString(), 
					5);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_INPUT_MESSAGE_QUEUE_SIZE_ITEMID).toString(), 
					5);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_OUTPUT_MESSAGE_QUEUE_SIZE_ITEMID).toString(), 
					5);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_READ_SELECTOR_WAKEUP_INTERVAL_ITEMID).toString(), 
					10L);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_ACCEPT_PROCESSOR_MAX_SIZE_ITEMID).toString(), 
					2);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_ACCEPT_PROCESSOR_SIZE_ITEMID).toString(), 
					1);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_INPUT_MESSAGE_READER_MAX_SIZE_ITEMID).toString(), 
					3);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_INPUT_MESSAGE_READER_SIZE_ITEMID).toString(), 
					2);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_EXECUTOR_PROCESSOR_MAX_SIZE_ITEMID).toString(), 
					3);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_EXECUTOR_PROCESSOR_SIZE_ITEMID).toString(), 
					2);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_OUTPUT_MESSAGE_WRITER_MAX_SIZE_ITEMID).toString(), 
					3);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_OUTPUT_MESSAGE_WRITER_SIZE_ITEMID).toString(), 
					2);
			
			projectPartConfiguration.mapping(new StringBuilder("mainproject.")
					.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_MYBATIS_CONFIG_FILE_RELATIVE_PATH_STRING_ITEMID).toString(), 
					"kr/pe/sinnori/impl/mybatis/mybatisConfig.xml");
			
		} catch (Exception e) {
			log.warn("error", e);
			
			String errorMessage = String.format("fail to mapping configuration's item value to ProjectPartConfiguration's item value::%s", e.getMessage());			
		
			fail(errorMessage);
		}
		
		log.info(projectPartConfiguration.toString());
	}
}
