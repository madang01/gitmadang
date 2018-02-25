package kr.pe.sinnori.client.connection.asyn.share;

import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailboxMapper;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailboxPool;

public class AsynPrivateMailboxPoolFactory implements AsynPrivateMailboxPoolFactoryIF {

	private int totalNumberOfAsynPrivateMailboxs;
	private long socketTimeOut;
	
	public AsynPrivateMailboxPoolFactory(int totalNumberOfAsynPrivateMailboxs, long socketTimeOut) {
		this.totalNumberOfAsynPrivateMailboxs = totalNumberOfAsynPrivateMailboxs;
		this.socketTimeOut = socketTimeOut;
	}
	

	@Override
	public AsynPrivateMailboxPool makeNewAsynPrivateMailboxPool() {
		AsynPrivateMailboxMapper asynPrivateMailboxMapper = 
				new AsynPrivateMailboxMapper(totalNumberOfAsynPrivateMailboxs, socketTimeOut);
		return new AsynPrivateMailboxPool(asynPrivateMailboxMapper);
	}
}
