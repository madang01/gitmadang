package kr.pe.sinnori.client.connection.asyn.share;

public class AsynPrivateMailboxPoolFactory implements AsynPrivateMailboxPoolFactoryIF {

	private int totalNumberOfAsynPrivateMailboxs;
	private long socketTimeOut;
	
	public AsynPrivateMailboxPoolFactory(int totalNumberOfAsynPrivateMailboxs, long socketTimeOut) {
		this.totalNumberOfAsynPrivateMailboxs = totalNumberOfAsynPrivateMailboxs;
		this.socketTimeOut = socketTimeOut;
	}
	

	@Override
	public AsynPrivateMailboxPoolIF makeNewAsynPrivateMailboxPool() {
		AsynPrivateMailboxMapper asynPrivateMailboxMapper = 
				new AsynPrivateMailboxMapper(totalNumberOfAsynPrivateMailboxs, socketTimeOut);
		return new AsynPrivateMailboxPool(asynPrivateMailboxMapper);
	}
}
