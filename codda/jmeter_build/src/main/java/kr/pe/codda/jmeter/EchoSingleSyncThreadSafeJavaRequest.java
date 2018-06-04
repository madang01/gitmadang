package kr.pe.codda.jmeter;

import java.io.File;
import java.io.Serializable;
import java.net.SocketTimeoutException;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.Echo.Echo;

public class EchoSingleSyncThreadSafeJavaRequest extends AbstractJavaSamplerClient implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6530083948693185629L;

	private InternalLogger log = InternalLoggerFactory.getInstance(EchoSingleSyncThreadSafeJavaRequest.class);
	private ConnectionIF conn = null;

	public Arguments getDefaultParameters() {
		Arguments args = new Arguments();
		args.addArgument("installedPath", "D:\\gitmadang\\codda");
		args.addArgument("runningProjectName", "sample_base");

		args.addArgument("host", "localhost");
		args.addArgument("port", "9090");

		return args;
	}
	
	public SampleResult buildErrorSampleResult(String errorMessage) {
		SampleResult result = new SampleResult();
		result.sampleStart();
		result.setSuccessful(false);
		result.setSampleLabel(errorMessage);
		result.sampleEnd();
		return result;
	}

	@Override
	public SampleResult runTest(JavaSamplerContext context) {
		if (Thread.currentThread().isInterrupted()) {
			return buildErrorSampleResult("InterruptedException-"+Thread.currentThread().getName());
		}
		
		// log.info("conn is null, thread={}", Thread.currentThread().getName());
		
		if (null == conn) {
			String runningProjectName = context.getParameter("runningProjectName");
			String installedPathString = context.getParameter("installedPath");

			String serverHost = context.getParameter("host");
			String nativePort = context.getParameter("port");
			int serverPort = 9090;

			File sinnoriInstalledPath = new File(installedPathString);

			if (!sinnoriInstalledPath.exists()) {
				log.error("the sinnori installed path doesn't exist");
				System.exit(1);
			}

			if (!sinnoriInstalledPath.isDirectory()) {
				log.error("the sinnori installed path isn't a directory");
				System.exit(1);
			}

			try {
				serverPort = Integer.parseInt(nativePort);
			} catch (NumberFormatException e) {
				log.error("the port[{}] is not a number, change to a default value[9090]", nativePort);
				System.exit(1);
			}

			System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME,
					runningProjectName);
			System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH,
					installedPathString);

			ConnectionPoolManager connectionPoolManager = ConnectionPoolManager.getInstance();

			AnyProjectConnectionPoolIF mainProjectConnectionPool = connectionPoolManager.getMainProjectConnectionPool();
			
			try {
				conn = mainProjectConnectionPool.createSyncThreadSafeConnection(serverHost, serverPort);
			} catch (Exception e) {
				return buildErrorSampleResult("connection timeout");
			}				
			
			log.info("conn[{}] is connected, thread={}", conn.hashCode(), Thread.currentThread().getName());
		}
		
		// log.info("conn is not null, thread={}", Thread.currentThread().getName());

		if (! conn.isConnected()) {
			log.info("conn[{}] is disconencted, thread={}, isInterrupted={}", 
					conn.hashCode(),
					Thread.currentThread().getName(), Thread.currentThread().isInterrupted());			
			return buildErrorSampleResult("conn[" + conn.hashCode() + "] was disconencted, thread="+Thread.currentThread().getName());
		}

		SampleResult result = new SampleResult();
		// long startTime = 0, endTime = 0;
		// startTime = System.nanoTime();

		result.sampleStart();

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
				} else {
					result.setSuccessful(false);
					log.warn("실패::echo 메시지 입력/출력 다름");
				}
			} else {
				result.setSuccessful(false);
				log.warn(new StringBuilder("실패::잘못된 출력 메시지, ").append(messageFromServer.toString()).toString());
			}
		} catch (SocketTimeoutException e) {
			result.setSuccessful(false);
			result.setSampleLabel("echo 메시지 입력/출력 timeout 실패");
		} catch (InterruptedException e) {
			result.setSuccessful(false);
			result.setSampleLabel("echo 메시지 입력/출력 InterruptedException");
		} catch (Exception e) {
			result.setSuccessful(false);
			result.setSampleLabel("echo 메시지 입력/출력 unknown error");

			String errorMessage = new StringBuilder("실패::알수없는 에러 발생, conn=").append(conn.hashCode())
					.append(", thread=")
					.append(Thread.currentThread().getName()).append(", errmsg=")
					.append(e.getMessage()).toString();
			
			
			log.warn(errorMessage, e);
			conn.close();
			conn = null;
		}
		//

		result.sampleEnd();
		// endTime = System.nanoTime();

		// log.info("elapsed={}", TimeUnit.MICROSECONDS.convert((endTime - startTime),
		// TimeUnit.NANOSECONDS));

		return result;

		/*
		 * SampleResult result = new SampleResult(); result.sampleStart();
		 * 
		 * ConnectionPoolManager connectionPoolManager =
		 * ConnectionPoolManager.getInstance();
		 * 
		 * AnyProjectConnectionPoolIF mainProjectConnectionPool =
		 * connectionPoolManager.getMainProjectConnectionPool();
		 * 
		 * java.util.Random random = new java.util.Random();
		 * 
		 * Echo echoInObj = new Echo(); echoInObj.setRandomInt(random.nextInt());
		 * echoInObj.setStartTime(new java.util.Date().getTime());
		 * 
		 * AbstractMessage messageFromServer = null;
		 * 
		 * try { messageFromServer =
		 * mainProjectConnectionPool.sendSyncInputMessage(echoInObj);
		 * 
		 * result.setSampleLabel("echo 메시지 입력/출력 비교");
		 * 
		 * if (messageFromServer instanceof Echo) { Echo echoOutObj = (Echo)
		 * messageFromServer; if ((echoInObj.getRandomInt() ==
		 * echoOutObj.getRandomInt()) && (echoInObj.getStartTime() ==
		 * echoOutObj.getStartTime())) { result.setSuccessful(true); //
		 * log.info(echoOutObj.toString()); // result.setResponseCode("ok"); //
		 * result.setResponseMessage("성공"); } else { result.setSuccessful(false); //
		 * result.setResponseCode("error"); log.warn("실패::echo 메시지 입력/출력 다름"); } } else
		 * { result.setSuccessful(false);
		 * 
		 * // result.setResponseCode("error"); log.warn(new
		 * StringBuilder("실패::잘못된 출력 메시지, ").append(messageFromServer.toString()).
		 * toString()); } } catch (SocketTimeoutException e) {
		 * result.setSuccessful(false);
		 * result.setSampleLabel("echo 메시지 입력/출력 timeout 실패"); } catch (Exception e) {
		 * result.setSuccessful(false);
		 * result.setSampleLabel("echo 메시지 입력/출력 unknown error"); }
		 * 
		 * result.sampleEnd();
		 */

		/*
		 * try { Thread.sleep(1000); } catch (InterruptedException e) { }
		 */

		// return result;
	}

	public void teardownTest(JavaSamplerContext context) {
		super.teardownTest(context);

		if (null != conn) {
			log.info("teardownTest::close connection[{}]", conn.hashCode());

			conn.close();
		} else {
			log.info("teardownTest::connection is null");
		}
	}

}