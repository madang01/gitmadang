package kr.pe.sinnori.jmeter;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.SocketTimeoutException;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.client.AnyProjectConnectionPoolIF;
import kr.pe.sinnori.client.ConnectionPoolManager;
import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.Echo.Echo;

public class EchoJavaRequest extends AbstractJavaSamplerClient implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6530083948693185629L;
	
	private InternalLogger log = InternalLoggerFactory.getInstance(EchoJavaRequest.class);
	private AbstractConnection conn = null;
	
	public void setupTest(JavaSamplerContext context) {
		super.setupTest(context);
		
		String sinnoriRunningProjectName = context.getParameter("sinnori.projectName");
		String sinnoriInstalledPathString = context.getParameter("sinnori.installedPath");
		
		File sinnoriInstalledPath = new File(sinnoriInstalledPathString);
		
		if (! sinnoriInstalledPath.exists()) {
			log.error("the sinnori installed path doesn't exist");
			// System.exit(1);
			return;
		}
		
		if (! sinnoriInstalledPath.isDirectory()) {
			log.error("the sinnori installed path isn't a directory");
			// System.exit(1);
			return;
		}
		
		
		System
		.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				sinnoriRunningProjectName);
System
		.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				sinnoriInstalledPathString);	


		ConnectionPoolManager connectionPoolManager = ConnectionPoolManager.getInstance();
	
		AnyProjectConnectionPoolIF mainProjectConnectionPool = connectionPoolManager.getMainProjectConnectionPool();
		
		String host = "172.30.1.15";
		int port=9090;
		
		int retryCount = 1;
		long retryInterval = 5000;
		
		while(null == conn) {
			try {
				conn = mainProjectConnectionPool.createConnection(host, port);			
			} catch (Exception e) {
				log.warn("1.fail to create a connection", e);
				// System.exit(1);
			}
			if (null != conn) {
				break;
			}
			
			try {
				Thread.sleep(retryInterval);
			} catch (InterruptedException e) {
			}
			retryCount++;
			log.info("1.retry[{}] to connect", retryCount);
		}		
	}

	public Arguments getDefaultParameters() {
		Arguments args = new Arguments();
		args.addArgument("sinnori.installedPath", "d:\\gitsinnori\\sinnori");
		args.addArgument("sinnori.projectName", "sample_base");		
		
		return args;
	}
	
	@Override
	public SampleResult runTest(JavaSamplerContext arg0) {
		int maxRetry = 10;		

		// Write your test code here.
		if (null == conn) {
			int retryCount = 0;
			long retryInterval = 5000;
			
			ConnectionPoolManager connectionPoolManager = ConnectionPoolManager.getInstance();
			
			AnyProjectConnectionPoolIF mainProjectConnectionPool = connectionPoolManager.getMainProjectConnectionPool();
			
			String host = "172.30.1.15";
			int port=9090;
			
			while(null == conn && retryCount < maxRetry) {
				retryCount++;
				log.info("2.retry[{}] to connect", retryCount);
				
				try {
					conn = mainProjectConnectionPool.createConnection(host, port);
				} catch (Exception e) {
					log.warn("2.fail to create a connection", e);
					// System.exit(1);
				}
				if (null != conn) {
					break;
				}
				
				try {
					Thread.sleep(retryInterval);
				} catch (InterruptedException e) {
				}
				
			}	
		}		
		
		SampleResult result = new SampleResult();
		result.sampleStart();
		
		if (null != conn) {			
			java.util.Random random = new java.util.Random();

			Echo echoInObj = new Echo();
			echoInObj.setRandomInt(random.nextInt());
			echoInObj.setStartTime(new java.util.Date().getTime());

			AbstractMessage messageFromServer = null;
			try {
				messageFromServer = conn.sendSyncInputMessage(echoInObj);
				
				result.setSampleLabel("echo 메시지 입력/출력 비교");
				
				if (messageFromServer instanceof Echo) {
					Echo echoOutObj = (Echo) messageFromServer;
					if ((echoInObj.getRandomInt() == echoOutObj.getRandomInt())
							&& (echoInObj.getStartTime() == echoOutObj.getStartTime())) {
						result.setSuccessful(true);
						// log.info(echoOutObj.toString());
						// result.setResponseCode("ok");
						// result.setResponseMessage("성공");		
					} else {
						result.setSuccessful(false);
						// result.setResponseCode("error");
						log.warn("실패::echo 메시지 입력/출력 다름");
					}
				} else {
					result.setSuccessful(false);
					
					// result.setResponseCode("error");
					log.warn(new StringBuilder("실패::잘못된 출력 메시지, ").append(messageFromServer.toString()).toString());
				}
			} catch (SocketTimeoutException e) {
				result.setSuccessful(false);
				result.setSampleLabel("echo 메시지 입력/출력 timeout 실패");
				
				log.warn(new StringBuilder("실패::timeout 에러 발생, conn=")
						.append(conn.hashCode())
						.append(", errmsg=")
						.append(e.getMessage()).toString());
				
				try {
					conn.close();
				} catch (IOException e1) {
					log.error("1.fail to close the connection", e1);
				}
				
				conn = null;
			} catch (Exception e) {
				result.setSuccessful(false);
				result.setSampleLabel("echo 메시지 입력/출력 unknown error");
				
				log.warn(new StringBuilder("실패::알수없는 에러 발생, conn=")
						.append(conn.hashCode())
						.append(", errmsg=")
						.append(e.getMessage()).toString(), e);
				
				try {
					conn.close();
				} catch (IOException e1) {
					log.error("2.fail to close the connection", e1);
				}
				
				conn = null;
			}		
		} else {
			result.setSuccessful(false);
			result.setSampleLabel(new StringBuilder("서버 최대[")
					.append(maxRetry)
					.append("] 접속 연결 실패").toString());
		}
		
		//

		result.sampleEnd();

		return result;

	}
	
	public void teardownTest(JavaSamplerContext context) {
		super.teardownTest(context);
		if (null != conn) {
			try {
				conn.close();
			} catch (IOException e) {
				log.error("fail to close the connection", e);
			}
		}
	}
	
}
