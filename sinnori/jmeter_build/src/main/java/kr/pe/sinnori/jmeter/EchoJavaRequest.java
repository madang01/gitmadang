package kr.pe.sinnori.jmeter;
import java.io.File;
import java.io.Serializable;

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
	private AnyProjectConnectionPoolIF mainProjectConnectionPool = null;
	
	public void setupTest(JavaSamplerContext context) {
		
		String sinnoriRunningProjectName = context.getParameter("sinnori.projectName");
		String sinnoriInstalledPathString = context.getParameter("sinnori.installedPath");
		
		File sinnoriInstalledPath = new File(sinnoriInstalledPathString);
		
		if (! sinnoriInstalledPath.exists()) {
			log.error("the sinnori installed path doesn't exist");
			System.exit(1);
		}
		
		if (! sinnoriInstalledPath.isDirectory()) {
			log.error("the sinnori installed path isn't a directory");
			System.exit(1);
		}
		
		
		System
		.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				sinnoriRunningProjectName);
System
		.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				sinnoriInstalledPathString);	


		mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
	}

	public Arguments getDefaultParameters() {
		Arguments args = new Arguments();
		args.addArgument("sinnori.installedPath", "d:\\gitsinnori\\sinnori");
		args.addArgument("sinnori.projectName", "sample_base");		
		
		return args;
	}
	
	@Override
	public SampleResult runTest(JavaSamplerContext arg0) {
		SampleResult result = new SampleResult();		

		result.sampleStart();

		// Write your test code here.
		result.setSampleLabel("echo 메시지 입력/출력 비교");
		
		
		java.util.Random random = new java.util.Random();

		Echo echoInObj = new Echo();
		echoInObj.setRandomInt(random.nextInt());
		echoInObj.setStartTime(new java.util.Date().getTime());

		AbstractMessage messageFromServer = null;
		try {
			messageFromServer = mainProjectConnectionPool.sendSyncInputMessage(echoInObj);
			
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
		} catch (Exception e) {
			result.setSuccessful(false);
			log.warn(new StringBuilder("실패::알수없는 에러 발생, errtype=")
					.append(e.getClass().getName())
					.append(", errmsg=")
					.append(e.getMessage()).toString(), e);
		}		
		//

		result.sampleEnd();

		return result;

	}
	
}
