package kr.pe.sinnori.impl.message.LoginEcho;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractAuthServerExecutor;
import kr.pe.sinnori.server.executor.LetterSender;

public class LoginEchoServerTask extends AbstractAuthServerExecutor {

	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager, 
			LetterSender letterSender,
			AbstractMessage messageFromClient) throws Exception {
		letterSender.addAsynMessage(messageFromClient);
	}
}
