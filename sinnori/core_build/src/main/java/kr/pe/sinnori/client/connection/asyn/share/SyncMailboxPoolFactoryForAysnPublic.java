package kr.pe.sinnori.client.connection.asyn.share;

public class SyncMailboxPoolFactoryForAysnPublic implements SyncMailboxPoolFactoryForAsynPublicIF {

	private int totalNumberOfAsynPrivateMailboxs;
	private long socketTimeOut;
	
	public SyncMailboxPoolFactoryForAysnPublic(int totalNumberOfAsynPrivateMailboxs, long socketTimeOut) {
		this.totalNumberOfAsynPrivateMailboxs = totalNumberOfAsynPrivateMailboxs;
		this.socketTimeOut = socketTimeOut;
	}
	

	@Override
	public SyncMailboxPoolForAsynPublicIF makeNewAsynPrivateMailboxPool() {
		SyncMailboxMapperForAsynPublic asynPrivateMailboxMapper = 
				new SyncMailboxMapperForAsynPublic(totalNumberOfAsynPrivateMailboxs, socketTimeOut);
		return new SyncMailboxPoolForAsynPublic(asynPrivateMailboxMapper);
	}
}
