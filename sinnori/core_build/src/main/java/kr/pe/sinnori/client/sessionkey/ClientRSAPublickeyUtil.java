package kr.pe.sinnori.client.sessionkey;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.client.AnyProjectClient;
import kr.pe.sinnori.client.ProjectClientManager;
import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.vo.CommonPartConfiguration;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotFoundProjectException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.BinaryPublicKey.BinaryPublicKey;

public abstract class ClientRSAPublickeyUtil {
	public static byte[] getPublickeyFromMainProjectServer() throws SymmetricException {
		Logger log = LoggerFactory.getLogger(ClientRSAPublickeyUtil.class);
		
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
	}
	
	public static byte[] getPublickeyFromSubProjectServer(
			String subProjectName) throws SymmetricException {
		Logger log = LoggerFactory.getLogger(ClientRSAPublickeyUtil.class);
		
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
	}
	
	public static byte[] getPublickeyFromFile() throws SymmetricException {
		Logger log = LoggerFactory.getLogger(ClientRSAPublickeyUtil.class);
		
		byte[] publicKeyBytes = null;

		SinnoriConfiguration sinnoriRunningProjectConfiguration = SinnoriConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();
		CommonPartConfiguration commonPart = sinnoriRunningProjectConfiguration.getCommonPartConfiguration();

		File rsaPublickeyFile = null;
		FileInputStream rsaPublicKeyFIS = null;
		try {
			rsaPublickeyFile = commonPart.getRSAPublickeyFileOfSessionKey();
			rsaPublicKeyFIS = new FileInputStream(rsaPublickeyFile);

			long size = rsaPublickeyFile.length();
			if (size > CommonStaticFinalVars.MAX_KEY_FILE_SIZE) {
				String errorMessage = String.format("check rsa public key file size[%d]", size);
				throw new SymmetricException(errorMessage);
			}
			publicKeyBytes = new byte[(int) size];
			rsaPublicKeyFIS.read(publicKeyBytes);

		} catch (SinnoriConfigurationException e) {
			String errorMessage = e.getMessage();
			throw new SymmetricException(errorMessage);
		} catch (IOException e) {
			String errorMessage = String.format("fail to read RSA public key file[%s], errormessage=[%s]",
					rsaPublickeyFile.getAbsolutePath(), e.getMessage());
			throw new SymmetricException(errorMessage);
		} finally {
			if (null != rsaPublicKeyFIS) {
				try {
					rsaPublicKeyFIS.close();
				} catch (IOException e) {
					log.warn("fail to close the input stream of rsa public key file", e);
				}
			}
		}

		return publicKeyBytes;
	}
	
	private static byte[] getPublickeyFromServer(AnyProjectClient clientProject)
			throws SymmetricException, SocketTimeoutException, ServerNotReadyException, NoMoreDataPacketBufferException,
			BodyFormatException, DynamicClassCallException, ServerTaskException, NotLoginException {
		BinaryPublicKey inObj = new BinaryPublicKey();
		inObj.setPublicKeyBytes(new byte[0]);
		;
		AbstractMessage outObj = clientProject.sendSyncInputMessage(inObj);
		BinaryPublicKey binaryPublicKeyObj = (BinaryPublicKey) outObj;

		return binaryPublicKeyObj.getPublicKeyBytes();
	}
}
