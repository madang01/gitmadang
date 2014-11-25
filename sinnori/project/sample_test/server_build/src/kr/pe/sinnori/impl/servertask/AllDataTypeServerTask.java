package kr.pe.sinnori.impl.servertask;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.AllDataType.AllDataType;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

public class AllDataTypeServerTask extends AbstractServerTask {
	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager,
			LetterSender letterSender, AbstractMessage messageFromClient)
			throws Exception {
		doWork(serverProjectConfig, letterSender, (AllDataType)messageFromClient);
	}
	
	private void doWork(ServerProjectConfig serverProjectConfig,
			LetterSender letterSender, AllDataType allDataTypeInObj)
			throws Exception {
		letterSender.addSyncMessage(allDataTypeInObj);
	}
}
