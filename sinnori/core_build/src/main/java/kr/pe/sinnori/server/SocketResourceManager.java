package kr.pe.sinnori.server;

import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetDecoder;
import java.util.concurrent.ConcurrentHashMap;

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolManagerIF;
import kr.pe.sinnori.common.io.SocketOutputStream;

public class SocketResourceManager implements SocketResourceManagerIF {
	private final Object monitor = new Object();
	
	
	private CharsetDecoder streamCharsetDecoder = null;
	private int dataPacketBufferMaxCntPerMessage = 0;
	private DataPacketBufferPoolManagerIF dataPacketBufferPoolManager = null;
	private ProjectLoginManagerIF projectLoginManager = null;
	
	
	private ConcurrentHashMap<SocketChannel, SocketResource> socketChannel2SocketResourceHash 
		= new ConcurrentHashMap<SocketChannel, SocketResource>(); 
	
	private SocketResourceManager( 
			CharsetDecoder streamCharsetDecoder,
			int dataPacketBufferMaxCntPerMessage,
			DataPacketBufferPoolManagerIF dataPacketBufferPoolManager,
			ProjectLoginManagerIF projectLoginManager) {
		this.streamCharsetDecoder = streamCharsetDecoder;
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
		this.dataPacketBufferPoolManager = dataPacketBufferPoolManager;
	}
	
	public static class Builder {
		public static SocketResourceManager build(CharsetDecoder streamCharsetDecoder,
				int dataPacketBufferMaxCntPerMessage,
				DataPacketBufferPoolManagerIF dataPacketBufferPoolManager,
				ProjectLoginManagerIF projectLoginManager) {
            return new SocketResourceManager(
            		streamCharsetDecoder,
            		dataPacketBufferMaxCntPerMessage,
            		dataPacketBufferPoolManager,
            		projectLoginManager);
        }
	}

	@Override
	public void addNewSocketChannel(SocketChannel newSC) throws NoMoreDataPacketBufferException {
		synchronized (monitor) {
			SocketOutputStream socketOutputStream = 
					new SocketOutputStream(streamCharsetDecoder, 
							dataPacketBufferMaxCntPerMessage, 
							dataPacketBufferPoolManager);
			
			PersonalLoginManager personalLoginManager = 
					new PersonalLoginManager(newSC, projectLoginManager);
			
			SocketResource socketResource = new SocketResource(newSC, socketOutputStream, personalLoginManager);
			socketChannel2SocketResourceHash.put(newSC, socketResource);
		}
	}

	@Override
	public void remove(SocketChannel ownerSC) {
		synchronized (monitor) {
			SocketResource socketResource = socketChannel2SocketResourceHash.get(ownerSC);
			if (null != socketResource) {
				socketResource.close();
				socketChannel2SocketResourceHash.remove(ownerSC);
				projectLoginManager.removeLoginUser(ownerSC);
			}
		}
	}

	@Override
	public SocketResource getClientResource(SocketChannel ownerSC) {
		return socketChannel2SocketResourceHash.get(ownerSC);
	}

	@Override
	public int getNumberOfSocketResources() {
		return socketChannel2SocketResourceHash.size();
	}
	
	
}
