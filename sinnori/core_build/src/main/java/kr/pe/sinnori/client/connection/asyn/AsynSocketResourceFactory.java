package kr.pe.sinnori.client.connection.asyn;

import kr.pe.sinnori.client.connection.asyn.threadpool.IEOClientThreadPoolSetManagerIF;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.SocketOutputStreamFactoryIF;

public class AsynSocketResourceFactory implements AsynSocketResourceFactoryIF {
	private SocketOutputStreamFactoryIF socketOutputStreamFactory = null;
	private IEOClientThreadPoolSetManagerIF ieoClientThreadPoolSetManager = null;
	
	public AsynSocketResourceFactory(SocketOutputStreamFactoryIF socketOutputStreamFactory,
			IEOClientThreadPoolSetManagerIF ieoClientThreadPoolSetManager) {
		if (null == socketOutputStreamFactory) {
			throw new IllegalArgumentException("the parameter socketOutputStreamFactory is null");
		}
		
		if (null == ieoClientThreadPoolSetManager) {
			throw new IllegalArgumentException("the parameter ieoClientThreadPoolSetManager is null");
		}
		
		this.socketOutputStreamFactory = socketOutputStreamFactory;
		this.ieoClientThreadPoolSetManager = ieoClientThreadPoolSetManager;
	}
	

	@Override
	public AsynSocketResourceIF makeNewAsynSocketResource() throws NoMoreDataPacketBufferException {
		SocketOutputStream socketOutputStream = socketOutputStreamFactory.makeNewSocketOutputStream();
		
		return new AsynSocketResource(socketOutputStream, ieoClientThreadPoolSetManager);
	}	
}
