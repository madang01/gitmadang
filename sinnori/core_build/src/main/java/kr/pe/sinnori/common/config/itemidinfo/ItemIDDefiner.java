package kr.pe.sinnori.common.config.itemidinfo;

public abstract class ItemIDDefiner {
	
	
	
	public abstract class DBCPPartItemIDDefiner {
		public static final String DBCP_CONFIGE_FILE_ITEMID = "dbcp_confige_file.value";
	}	
	public abstract class CommonPartItemIDDefiner {
		public static final String SERVLET_JSP_JDF_ERROR_MESSAGE_PAGE_ITEMID = "servlet_jsp.jdf_error_message_page.value";
		public static final String SERVLET_JSP_JDF_LOGIN_PAGE_ITEMID = "servlet_jsp.jdf_login_page.value";
		public static final String SERVLET_JSP_JDF_SERVLET_TRACE_ITEMID = "servlet_jsp.jdf_servlet_trace.value";
		public static final String SERVLET_JSP_WEB_LAYOUT_CONTROL_PAGE_ITEMID = "servlet_jsp.web_layout_control_page.value";		
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
		public static final String COMMON_DATA_PACKET_BUFFER_MAX_CNT_PER_MESSAGE_ITEMID = "common.data_packet_buffer_max_cnt_per_message.value";
		public static final String COMMON_DATA_PACKET_BUFFER_SIZE_ITEMID = "common.data_packet_buffer_size.value";
		public static final String COMMON_DATA_PACKET_BUFFER_POOL_SIZE_ITEMID = "common.data_packet_buffer_pool_size.value";
		public static final String COMMON_MESSAGE_ID_FIXED_SIZE_ITEMID = "common.message_id_fixed_size.value";
		public static final String COMMON_MESSAGE_PROTOCOL_TYPE_ITEMID = "common.message_protocol_type.value";
		public static final String COMMON_FIRST_PREFIX_DYNAMIC_CLASS_FULL_NAME_ITEMID = "common.first_prefix_dynamic_class_full_name.value";
		public static final String CLIENT_MONITOR_TIME_INTERVAL_ITEMID = "client.monitor.time_interval.value";
		public static final String CLIENT_MONITOR_RECEPTION_TIMEOUT_ITEMID = "client.monitor.reception_timeout.value";
		public static final String CLIENT_SOCKET_TIMEOUT_ITEMID = "client.socket.timeout.value";
		public static final String CLIENT_CONNECTION_TYPE_ITEMID = "client.connection.type.value";
		public static final String CLIENT_CONNECTION_MAX_COUNT_ITEMID = "client.connection.max_count.value";
		public static final String CLIENT_CONNECTION_COUNT_ITEMID = "client.connection.count.value";
		public static final String CLIENT_CONNECTION_TIMEOUT_ITEMID = "client.connection.timeout.value";
		public static final String CLIENT_DATA_PACKET_BUFFER_CNT_ITEMID = "client.data_packet_buffer_cnt.value";
		
		public static final String CLIENT_ASYN_PIRVATE_MAILBOX_CNT_PER_PUBLIC_CONNECTION_ITEMID = "client.asyn.public.private_mailbox_cnt_per_connection.value";
		
		public static final String CLIENT_ASYN_INPUT_MESSAGE_QUEUE_SIZE_ITEMID 			= "client.asyn.input_message_queue_size.value";
		public static final String CLIENT_ASYN_OUTPUT_MESSAGE_QUEUE_SIZE_ITEMID 	= "client.asyn.output_message_queue_size.value";
		
		
		public static final String CLIENT_ASYN_INPUT_MESSAGE_WRITER_POOL_SIZE_ITEMID 		= "client.asyn.input_message_writer.pool_size.value";
		public static final String CLIENT_ASYN_OUTPUT_MESSAGE_READER_POOL_SIZE_ITEMID 		= "client.asyn.output_message_reader.pool_size.value";
		public static final String CLIENT_ASYN_EXECUTOR_POOL_SIZE_ITEMID = "client.asyn.executor.pool_size.value";
		
		public static final String CLIENT_ASYN_READ_SELECTOR_WAKEUP_INTERVAL_ITEMID 	= "client.asyn.read_selector_wakeup_interval.value";
		
		public static final String SERVER_MONITOR_TIME_INTERVAL_ITEMID = "server.monitor.time_interval.value";
		public static final String SERVER_MONITOR_RECEPTION_TIMEOUT_ITEMID = "server.monitor.reception_timeout.value";
		public static final String SERVER_MAX_CLIENTS_ITEMID = "server.max_clients.value";		
		public static final String SERVER_POOL_ACCEPT_QUEUE_SIZE_ITEMID = "server.pool.accept_queue_size.value";
		public static final String SERVER_POOL_INPUT_MESSAGE_QUEUE_SIZE_ITEMID = "server.pool.input_message_queue_size.value";
		public static final String SERVER_POOL_OUTPUT_MESSAGE_QUEUE_SIZE_ITEMID = "server.pool.output_message_queue_size.value";
		public static final String SERVER_ACCEPT_SELECTOR_TIMEOUT_ITEMID = "server.accept_selector_timeout.value";
		public static final String SERVER_POOL_READ_SELECTOR_WAKEUP_INTERVAL_ITEMID = "server.pool.read_selector_wakeup_interval.value";
		public static final String SERVER_POOL_ACCEPT_PROCESSOR_MAX_SIZE_ITEMID = "server.pool.accept_processor.max_size.value";
		public static final String SERVER_POOL_ACCEPT_PROCESSOR_SIZE_ITEMID = "server.pool.accept_processor.size.value";
		public static final String SERVER_POOL_INPUT_MESSAGE_READER_MAX_SIZE_ITEMID = "server.pool.input_message_reader.max_size.value";
		public static final String SERVER_POOL_INPUT_MESSAGE_READER_SIZE_ITEMID = "server.pool.input_message_reader.size.value";
		public static final String SERVER_POOL_EXECUTOR_PROCESSOR_MAX_SIZE_ITEMID = "server.pool.executor_processor.max_size.value";
		public static final String SERVER_POOL_EXECUTOR_PROCESSOR_SIZE_ITEMID = "server.pool.executor_processor.size.value";
		public static final String SERVER_POOL_OUTPUT_MESSAGE_WRITER_MAX_SIZE_ITEMID = "server.pool.output_message_writer.max_size.value";
		public static final String SERVER_POOL_OUTPUT_MESSAGE_WRITER_SIZE_ITEMID = "server.pool.output_message_writer.size.value";
		public static final String SERVER_MYBATIS_CONFIG_FILE_RELATIVE_PATH_STRING_ITEMID = "server.mybatis.config_file_relative_path.value";
	}
}
