package impl.executor.client;

import java.net.SocketTimeoutException;

import kr.pe.sinnori.client.ClientProjectIF;
import kr.pe.sinnori.client.io.LetterFromServer;
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

public class TestNetEcoCExtor extends AbstractClientExecutor {

	@Override
	protected void doTask(MessageMangerIF messageManger, ClientProjectIF clientProject) throws SocketTimeoutException,
			ServerNotReadyException, DynamicClassCallException,
			NoMoreDataPacketBufferException, BodyFormatException,
			MessageInfoNotFoundException, NoMatchOutputMessage, 
			InterruptedException, MessageItemException, 
			ServerExcecutorUnknownException, NotLoginException {
		
		// AbstractConnectionPool sinnoriConnectionPool = SinnoriClientManager.getInstance().getConnectionPool("sinnori");
		// Thread.sleep(2000);
		
		
		java.util.Random random = new java.util.Random();

		InputMessage echoInObj = null;
		
		try {
			echoInObj = messageManger.createInputMessage("Echo");
		} catch (MessageInfoNotFoundException e1) {
			e1.printStackTrace();
			System.exit(1);
		}

		echoInObj.messageHeaderInfo.mailboxID = CommonStaticFinal.SERVER_MAILBOX_ID;
		echoInObj.messageHeaderInfo.mailID = Integer.MIN_VALUE;

		echoInObj.setAttribute("mRandomInt", random.nextInt());
		echoInObj.setAttribute("mStartTime", new java.util.Date().getTime());

		LetterFromServer letterFromServer = clientProject
				.sendInputMessage(echoInObj);

		if (null == letterFromServer) {
			log.warn(String.format("input message[%s] letterFromServer is null", echoInObj.getMessageID()));
			return;
		}		

		OutputMessage echoOutObj = letterFromServer.getOutputMessage("Echo");

		// log.info(String.format("echoOutObj=[%s]", echoOutObj.toString()));

		if (((int) echoOutObj.getAttribute("mRandomInt") == (int) echoInObj
				.getAttribute("mRandomInt"))
				&& ((long) echoOutObj.getAttribute("mStartTime") == (long) echoInObj
						.getAttribute("mStartTime"))) {
			// isSame = true;
			log.info("성공::echo 메시지 입력/출력 동일함");
		} else {
			log.info("실패::echo 메시지 입력/출력 다름");
		}
	}
}
