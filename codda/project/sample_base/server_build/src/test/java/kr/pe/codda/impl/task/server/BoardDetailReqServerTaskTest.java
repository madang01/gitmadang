package kr.pe.codda.impl.task.server;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.mockito.Mockito;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.classloader.ServerSimpleClassLoaderIF;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.impl.message.BoardDetailReq.BoardDetailReq;
import kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes;
import kr.pe.codda.server.AcceptedConnection;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.ProjectLoginManagerIF;
import kr.pe.codda.server.lib.BoardType;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardDetailReqServerTaskTest extends AbstractJunitTest {
	
	@Test
	public void testDoTask() {		
		class ToLetterCarrierMock extends ToLetterCarrier {			

			public ToLetterCarrierMock(AcceptedConnection fromAcceptedConnection, AbstractMessage inputMessage,
					ProjectLoginManagerIF projectLoginManager, MessageProtocolIF messageProtocol,
					ServerSimpleClassLoaderIF serverSimpleClassLoader) {
				super(fromAcceptedConnection, inputMessage, projectLoginManager, messageProtocol, serverSimpleClassLoader);
			}

			public void addSyncOutputMessage(AbstractMessage syncOutputMessage) throws InterruptedException {
				if (! (syncOutputMessage instanceof BoardDetailRes)) {
					fail("the parameter syncOutputMessage is not a instance of MessageResultRes class");
				}
				
				
				log.info("success, outObj={}", syncOutputMessage.toString());
			}
		}
		
		PersonalLoginManagerIF personalLoginManagerMock = Mockito.mock(PersonalLoginManagerIF.class);				
		ToLetterCarrier toLetterCarrierMock = new ToLetterCarrierMock(null, null, null, null, null);
				
		BoardDetailReq inObj = new BoardDetailReq();
		inObj.setBoardId(BoardType.FREE.getBoardID());
		inObj.setBoardNo(6);
		
		
		BoardDetailReqServerTask boardDetailReqServerTask= new BoardDetailReqServerTask();
		try {
			boardDetailReqServerTask.doTask(mainProjectName, 
					personalLoginManagerMock, toLetterCarrierMock, inObj);
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
	}
}
