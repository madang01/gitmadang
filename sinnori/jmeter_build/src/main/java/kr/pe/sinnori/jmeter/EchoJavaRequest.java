package kr.pe.sinnori.jmeter;
import java.io.File;
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
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.Echo.Echo;

public class EchoJavaRequest extends AbstractJavaSamplerClient implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6530083948693185629L;
	
	private InternalLogger log = InternalLoggerFactory.getInstance(EchoJavaRequest.class);
// 	private AbstractConnection conn = null;
	
	public void setupTest(JavaSamplerContext context) {
		super.setupTest(context);
		
		String sinnoriRunningProjectName = context.getParameter("sinnori.projectName");
		String sinnoriInstalledPathString = context.getParameter("sinnori.installedPath");
		
		/*String host = context.getParameter("host");
		String nativePort = context.getParameter("port");
		int port=9090;*/
		
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
		
		/*try {
			port = Integer.parseInt(nativePort);
		} catch(NumberFormatException e) {
			log.error("the port[{}] is not a number, change to a default value[9090]", nativePort);
		}
		*/
		
		System
		.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				sinnoriRunningProjectName);
System
		.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				sinnoriInstalledPathString);	


		ConnectionPoolManager connectionPoolManager = ConnectionPoolManager.getInstance();
	
		@SuppressWarnings("unused")
		AnyProjectConnectionPoolIF mainProjectConnectionPool = connectionPoolManager.getMainProjectConnectionPool();
		
		/*int retryCount = 1;
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
			
			retryCount++;
			log.info("1.retry[{}] to connect", retryCount);
			
			try {
				Thread.sleep(retryInterval);
			} catch (InterruptedException e) {
				break;
			}			
		}	*/	
	}

	public Arguments getDefaultParameters() {
		Arguments args = new Arguments();
		args.addArgument("sinnori.installedPath", "d:\\gitsinnori\\sinnori");
		args.addArgument("sinnori.projectName", "sample_base");
		
		args.addArgument("host", "sinnori.pe.kr");
		args.addArgument("port", "9090");
		
		return args;
	}
	
	@Override
	public SampleResult runTest(JavaSamplerContext context) {
		/*String host = context.getParameter("host");
		String nativePort = context.getParameter("port");
		int port=9090;
		try {
			port = Integer.parseInt(nativePort);
		} catch(NumberFormatException e) {
			log.error("the port[{}] is not a number, change to a default value[9090]", nativePort);
		}
		
		
		int maxRetry = 10;		

		// Write your test code here.
		if (null == conn) {
			int retryCount = 0;
			long retryInterval = 5000;
			
			ConnectionPoolManager connectionPoolManager = ConnectionPoolManager.getInstance();
			
			AnyProjectConnectionPoolIF mainProjectConnectionPool = connectionPoolManager.getMainProjectConnectionPool();
			
			
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
					break;
				}
				
			}	
		}		
		
		SampleResult result = new SampleResult();
		long startTime = 0, endTime = 0;
		startTime = System.nanoTime();
		
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
					log.error("2.fail to close the connection", e1);
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
					log.error("3.fail to close the connection", e1);
				}
				
				conn = null;
			}		
		} else {
			result.setSuccessful(false);
			result.setSampleLabel("연결 실패");
			String errorMessage = new StringBuilder("서버 최대[")
					.append(maxRetry)
					.append("] 접속 연결 실패").toString();
			log.warn(errorMessage);
		}
		
		//

		
		result.sampleEnd();
		endTime = System.nanoTime();
		
		log.info("elapsed={}", TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));

		return result;*/

		SampleResult result = new SampleResult();
		result.sampleStart();
		
		ConnectionPoolManager connectionPoolManager = ConnectionPoolManager.getInstance();
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = connectionPoolManager.getMainProjectConnectionPool();
		
		java.util.Random random = new java.util.Random();

		Echo echoInObj = new Echo();
		echoInObj.setRandomInt(random.nextInt());
		echoInObj.setStartTime(new java.util.Date().getTime());

		AbstractMessage messageFromServer = null;
		
		try {
			messageFromServer = mainProjectConnectionPool.sendSyncInputMessage(echoInObj);
			
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
		} catch (Exception e) {
			result.setSuccessful(false);
			result.setSampleLabel("echo 메시지 입력/출력 unknown error");
		}
		
		result.sampleEnd();

		return result;
	}
	
	public void teardownTest(JavaSamplerContext context) {
		super.teardownTest(context);
		/*if (null != conn) {
			try {
				conn.close();
			} catch (IOException e) {
				log.error("fail to close the connection", e);
			}
		}*/
	}
	
}
