package kr.pe.codda.common.sessionkey;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.config.Configuration;
import kr.pe.codda.common.config.ConfigurationManager;
import kr.pe.codda.common.config.itemvalue.CommonPartConfiguration;
import kr.pe.codda.common.exception.ConfigurationException;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.type.SessionKey;

public abstract class AbstractRSAPublickeyGetter {
	
	public final byte[] getMainProjectPublickeyBytes() throws SymmetricException, InterruptedException {
		byte[] publicKeyBytes = null;
		Configuration sinnoriRunningProjectConfiguration = ConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();
		CommonPartConfiguration commonPart = sinnoriRunningProjectConfiguration.getCommonPartConfiguration();

		SessionKey.RSAKeypairSourceType rsaKeyPairSoureOfSessionkey = commonPart
				.getRsaKeypairSourceOfSessionKey();
		
		if (rsaKeyPairSoureOfSessionkey.equals(SessionKey.RSAKeypairSourceType.SERVER)) {
			publicKeyBytes = getPublickeyBytesFromMainProjectServer();
		} else if (rsaKeyPairSoureOfSessionkey.equals(SessionKey.RSAKeypairSourceType.FILE)) {
			publicKeyBytes = getPublickeyBytesFromFile();
		} else {
			throw new SymmetricException(new StringBuilder("unknown rsa keypair source[")
					.append(rsaKeyPairSoureOfSessionkey.toString()).append("]").toString());
		}
		
		return publicKeyBytes;
	}
	
	abstract protected byte[] getPublickeyBytesFromMainProjectServer() throws SymmetricException, InterruptedException;
	
	/*private static final byte[] getPublickeyBytesFromMainProjectServer() throws SymmetricException {
		Logger log = LoggerFactory.getLogger(ClientRSAPublickeyGetter.class);
		
		AnyProjectClient mainClientProject = ProjectClientManager.getInstance().getMainProjectClient();

		byte[] publicKeyBytes = null;
		try {
			publicKeyBytes = getPublickeyFromServer(mainClientProject);
		} catch (SocketTimeoutException | ServerNotReadyException | NoMoreDataPacketBufferException
				| BodyFormatException | DynamicClassCallException | ServerTaskException | NotLoginException e) {
			String errorMessage = e.getMessage();
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		return publicKeyBytes;
	}*/
	
	/**
	 * return public key bytes getting from sub project server
	 * 
	 * @param subProjectName The subproject name from which to obtain the bytes of the public key.
	 * @return public key bytes getting from sub project server
	 * @throws SymmetricException
	 */
	abstract public byte[] getSubProjectPublickeyBytes(
			String subProjectName) throws SymmetricException;
	
	/*public byte[] getSubProjectPublickeyBytes(
			String subProjectName) throws SymmetricException {
		Logger log = LoggerFactory.getLogger(ClientRSAPublickeyGetter.class);
		
		AnyProjectClient subClientProject = null;
		try {
			subClientProject = ProjectClientManager.getInstance().getSubProjectClient(subProjectName);
		} catch (NotFoundProjectException e) {
			String errorMessage = e.getMessage();
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		byte[] publicKeyBytes = null;
		try {
			publicKeyBytes = getPublickeyFromServer(subClientProject);
		} catch (SocketTimeoutException | ServerNotReadyException | NoMoreDataPacketBufferException
				| BodyFormatException | DynamicClassCallException | ServerTaskException | NotLoginException e) {
			String errorMessage = e.getMessage();
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		return publicKeyBytes;
	}*/
	
	private final byte[] getPublickeyBytesFromFile() throws SymmetricException {
		InternalLogger log = InternalLoggerFactory.getInstance(AbstractRSAPublickeyGetter.class);
		
		byte[] publicKeyBytes = null;

		Configuration sinnoriRunningProjectConfiguration = ConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();
		CommonPartConfiguration commonPart = sinnoriRunningProjectConfiguration.getCommonPartConfiguration();

		File rsaPublickeyFile = null;
	
		try {
			rsaPublickeyFile = commonPart.getRSAPublickeyFileOfSessionKey();
			
			publicKeyBytes =FileUtils.readFileToByteArray(rsaPublickeyFile);

		} catch (ConfigurationException e) {
			String errorMessage = String.format("fail to get RSA public key file from Sinnnori configuration, errormessage=[%s]",
					 e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		} catch (IOException e) {
			String errorMessage = String.format("fail to read RSA public key file[%s], errormessage=[%s]",
					rsaPublickeyFile.getAbsolutePath(), e.getMessage());
			
			log.warn(errorMessage, e);
			
			throw new SymmetricException(errorMessage);
		}

		return publicKeyBytes;
	}
	
	/*public byte[] getPublickeyFromServer(AnyProjectClient clientProject)
			throws SymmetricException, SocketTimeoutException, ServerNotReadyException, NoMoreDataPacketBufferException,
			BodyFormatException, DynamicClassCallException, ServerTaskException, NotLoginException {
		BinaryPublicKey inObj = new BinaryPublicKey();
		inObj.setPublicKeyBytes(new byte[0]);
		
		AbstractMessage outObj = clientProject.sendSyncInputMessage(inObj);
		BinaryPublicKey binaryPublicKeyObj = (BinaryPublicKey) outObj;

		return binaryPublicKeyObj.getPublicKeyBytes();
	}*/
}
