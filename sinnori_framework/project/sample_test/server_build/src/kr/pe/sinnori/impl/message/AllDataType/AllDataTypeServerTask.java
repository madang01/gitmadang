package kr.pe.sinnori.impl.message.AllDataType;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.SinnoriSqlSessionFactoryIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

public class AllDataTypeServerTask extends AbstractServerTask {
	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager,
			SinnoriSqlSessionFactoryIF sqlSessionFactory,
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
