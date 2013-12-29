package impl.executor.client;

import java.net.SocketTimeoutException;

import kr.pe.sinnori.client.ClientProjectIF;
import kr.pe.sinnori.client.io.LetterFromServer;
import kr.pe.sinnori.common.configuration.ClientProjectConfigIF;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.exception.NoMatchOutputMessage;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.ServerExcecutorUnknownException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.lib.CommonStaticFinal;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.util.AbstractClientExecutor;

public class TestLoginServiceCExtor extends AbstractClientExecutor {
	@Override
	protected void doTask(ClientProjectConfigIF clientProjectConfig, MessageMangerIF messageManger, ClientProjectIF clientProject) throws SocketTimeoutException,
			ServerNotReadyException, DynamicClassCallException,
			NoMoreDataPacketBufferException, BodyFormatException,
			MessageInfoNotFoundException, NoMatchOutputMessage, 
			InterruptedException, MessageItemException, 
			ServerExcecutorUnknownException, NotLoginException {
		
		InputMessage fileListInObj = null;
		
		try {
			fileListInObj = messageManger.createInputMessage("FileListRequest");
		} catch (MessageInfoNotFoundException e1) {
			e1.printStackTrace();
			System.exit(1);
		}

		fileListInObj.messageHeaderInfo.mailboxID = CommonStaticFinal.SERVER_MAILBOX_ID;
		fileListInObj.messageHeaderInfo.mailID = Integer.MIN_VALUE;

		fileListInObj.setAttribute("requestDirectory", ".");
		

		LetterFromServer letterFromServer = clientProject
				.sendSyncInputMessage(fileListInObj);

		if (null == letterFromServer) {
			log.warn(String.format("input message[%s] letterFromServer is null", fileListInObj.getMessageID()));
			return;
		}		

		OutputMessage fileListResultOutObj = letterFromServer.getOutputMessage("FileListResult");
		log.info(fileListResultOutObj.toString());
		
	}
}
