package kr.pe.codda.common.config.itemidinfo;

public abstract class ItemIDDefiner {
	public abstract class DBCPPartItemIDDefiner {
		public static final String DBCP_CONFIGE_FILE_ITEMID = "dbcp_confige_file.value";
	}	
	public abstract class CommonPartItemIDDefiner {
		public static final String JDF_USER_LOGIN_PAGE_ITEMID = "jdf.user_login_page.value";
		public static final String JDF_ADMIN_LOGIN_PAGE_ITEMID = "jdf.admin_login_page.value";
		public static final String JDF_SESSION_KEY_REDIRECT_PAGE_ITEMID = "jdf.session_key_redirect_page.value";
		public static final String JDF_ERROR_MESSAGE_PAGE_ITEMID = "jdf.error_message_page.value";		
		public static final String JDF_SERVLET_TRACE_ITEMID = "jdf.servlet_trace.value";		
		
		public static final String SESSIONKEY_RSA_KEYPAIR_SOURCE_ITEMID = "sessionkey.rsa.keypair_source.value";		
		public static final String SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID = "sessionkey.rsa.publickey.file.value";
		public static final String SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID = "sessionkey.rsa.privatekey.file.value";		
		public static final String SESSIONKEY_RSA_KEYSIZE_ITEMID = "sessionkey.rsa.keysize.value";
		public static final String SESSIONKEY_SYMMETRIC_KEY_ALGORITHM_ITEMID = "sessionkey.symmetric_key.algorithm.value";
		public static final String SESSIONKEY_SYMMETRIC_KEY_SIZE_ITEMID = "sessionkey.symmetric_key.size.value";
		public static final String SESSIONKEY_IV_SIZE_ITEMID = "sessionkey.iv_size.value";
		
		public static final String COMMON_UPDOWNFILE_LOCAL_SOURCE_FILE_RESOURCE_CNT_ITEMID ="common.updownfile.local_source_file_resource_cnt.value";
		public static final String COMMON_UPDOWNFILE_LOCAL_TARGET_FILE_RESOURCE_CNT_ITEMID = "common.updownfile.local_target_file_resource_cnt.value";
		public static final String COMMON_UPDOWNFILE_FILE_BLOCK_MAX_SIZE_ITEMID ="common.updownfile.file_block_max_size.value";
		public static final String COMMON_CACHED_OBJECT_MAX_SIZE_ITEMID = "common.cached_object.max_size.value";
	}
	
	public abstract class ProjectPartItemIDDefiner {
		public static final String COMMON_HOST_ITEMID = "common.host.value";
		public static final String COMMON_PORT_ITEMID = "common.port.value";
		public static final String COMMON_BYTEORDER_ITEMID = "common.byteorder.value";
		public static final String COMMON_CHARSET_ITEMID = "common.charset.value";
		public static final String COMMON_MESSAGE_PROTOCOL_TYPE_ITEMID = "common.message_protocol_type.value";
		public static final String CLIENT_MONITOR_TIME_INTERVAL_ITEMID = "client.monitor.time_interval.value";
		
		public static final String CLIENT_DATA_PACKET_BUFFER_IS_DIRECT_ITEMID = "client.data_packet_buffer.isdirect.value";
		public static final String CLIENT_DATA_PACKET_BUFFER_MAX_CNT_PER_MESSAGE_ITEMID = "client.data_packet_buffer.max_cnt_per_message.value";
		public static final String CLIENT_DATA_PACKET_BUFFER_SIZE_ITEMID = "client.data_packet_buffer.size.value";
		public static final String CLIENT_DATA_PACKET_BUFFER_POOL_SIZE_ITEMID = "client.data_packet_buffer.pool_size.value";
		
		public static final String CLIENT_SOCKET_TIMEOUT_ITEMID = "client.socket.timeout.value";
		public static final String CLIENT_CONNECTION_TYPE_ITEMID = "client.connection.type.value";
		public static final String CLIENT_CONNECTION_MAX_COUNT_ITEMID = "client.connection.max_count.value";
		public static final String CLIENT_CONNECTION_COUNT_ITEMID = "client.connection.count.value";
		public static final String CLIENT_CONNECTION_POOL_SUPPORTOR_TIME_INTERVAL_ITEMID = "client.connection.pool.supportor.time_interval.value";
		public static final String CLIENT_ASYN_SYNC_MESSAGE_MAILBOX_COUNT_PER_ASYN_NOSHARE_CONNECTION_ITEMID = "client.asyn.sync_message_mailbox_count_per_asyn_noshare_connection.value";
		public static final String CLIENT_ASYN_INPUT_MESSAGE_QUEUE_SIZE_ITEMID 			= "client.asyn.input_message_queue_size.value";
		public static final String CLIENT_ASYN_OUTPUT_MESSAGE_QUEUE_SIZE_ITEMID 	= "client.asyn.output_message_queue_size.value";
		public static final String CLIENT_ASYN_EXECUTOR_POOL_SIZE_ITEMID = "client.asyn.executor.pool_size.value";
		public static final String CLIENT_ASYN_SELECTOR_WAKEUP_INTERVAL_ITEMID 	= "client.asyn.selector.wakeup_interval.value";
		
		public static final String SERVER_MONITOR_TIME_INTERVAL_ITEMID = "server.monitor.time_interval.value";
		public static final String SERVER_DATA_PACKET_BUFFER_IS_DIRECT_ITEMID = "server.data_packet_buffer.isdirect.value";
		public static final String SERVER_DATA_PACKET_BUFFER_MAX_CNT_PER_MESSAGE_ITEMID = "server.data_packet_buffer.max_cnt_per_message.value";
		public static final String SERVER_DATA_PACKET_BUFFER_SIZE_ITEMID = "server.data_packet_buffer.size.value";
		public static final String SERVER_DATA_PACKET_BUFFER_POOL_SIZE_ITEMID = "server.data_packet_buffer.pool_size.value";
		public static final String SERVER_MAX_CLIENTS_ITEMID = "server.max_clients.value";
		public static final String SERVER_POOL_INPUT_MESSAGE_QUEUE_CAPACITY_ITEMID = "server.pool.input_message_queue_size.value";
		public static final String SERVER_POOL_OUTPUT_MESSAGE_QUEUE_CAPACITY_ITEMID = "server.pool.output_message_queue_size.value";
		//public static final String SERVER_POOL_EXECUTOR_MAX_SIZE_ITEMID = "server.pool.executor.max_size.value";
		// public static final String SERVER_POOL_EXECUTOR_SIZE_ITEMID = "server.pool.executor.size.value";
	}
}
