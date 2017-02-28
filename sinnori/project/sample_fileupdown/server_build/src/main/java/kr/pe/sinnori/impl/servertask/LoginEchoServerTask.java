package kr.pe.sinnori.impl.servertask;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.LoginEcho.LoginEcho;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractAuthServerExecutor;
import kr.pe.sinnori.server.executor.LetterSender;

public class LoginEchoServerTask extends AbstractAuthServerExecutor {

	@Override
	public void doTask(String projectName,
			LoginManagerIF loginManager,
			LetterSender letterSender, AbstractMessage inObj)
			throws Exception {
		doWork(projectName, letterSender, (LoginEcho)inObj);
	}
	
	private void doWork(String projectName,
			LetterSender letterSender, LoginEcho inObj)
			throws Exception {
		letterSender.addAsynMessage(inObj);
	}
}
