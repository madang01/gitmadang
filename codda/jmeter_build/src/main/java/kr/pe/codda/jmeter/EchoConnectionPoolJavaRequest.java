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
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.Echo.Echo;

public class EchoConnectionPoolJavaRequest extends AbstractJavaSamplerClient implements Serializable {
	private static final long serialVersionUID = -316690771256109729L;
	private InternalLogger log = InternalLoggerFactory.getInstance(EchoConnectionPoolJavaRequest.class);
	
	private AnyProjectConnectionPoolIF mainProjectConnectionPool = null;
	
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
			return buildErrorSampleResult("InterruptedException-"+Thread.currentThread().getName());		}
		
		if (null == mainProjectConnectionPool) {
			String runningProjectName = context.getParameter("runningProjectName");
			String installedPathString = context.getParameter("installedPath");			

			File sinnoriInstalledPath = new File(installedPathString);

			if (!sinnoriInstalledPath.exists()) {
				log.error("the installed path[{}] doesn't exist", installedPathString);
				System.exit(1);
			}

			if (!sinnoriInstalledPath.isDirectory()) {
				log.error("the installed path[{}] isn't a directory", installedPathString);
				System.exit(1);
			}

			/*String serverHost = context.getParameter("host");
			String nativePort = context.getParameter("port");
			int serverPort = 9090;
			try {
				serverPort = Integer.parseInt(nativePort);
			} catch (NumberFormatException e) {
				log.error("the port[{}] is not a number, change to a default value[9090]", nativePort);
				System.exit(1);
			}*/

			System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME,
					runningProjectName);
			System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH,
					installedPathString);

			ConnectionPoolManager connectionPoolManager = ConnectionPoolManager.getInstance();

			mainProjectConnectionPool = connectionPoolManager.getMainProjectConnectionPool();
		}		
		
		SampleResult result = new SampleResult();
		
		result.sampleStart();

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

			String errorMessage = new StringBuilder("실패::알수없는 에러 발생, thread=")
					.append(Thread.currentThread().getName()).append(", errmsg=")
					.append(e.getMessage()).toString();
			
			log.warn(errorMessage, e);
		}
		//

		result.sampleEnd();
		
		return result;
	}

}