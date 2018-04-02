package kr.pe.sinnori.impl.task.server;

import static org.junit.Assert.fail;

import java.nio.channels.SocketChannel;

import org.junit.Test;
import org.mockito.Mockito;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.impl.message.BoardDetailReq.BoardDetailReq;
import kr.pe.sinnori.impl.message.BoardDetailRes.BoardDetailRes;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.SocketResourceManagerIF;
import kr.pe.sinnori.server.lib.BoardType;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class BoardDetailReqServerTaskTest extends AbstractJunitTest {
	@Test
	public void testDoTask() {
		class ToLetterCarrierMock extends ToLetterCarrier {

			public ToLetterCarrierMock(SocketChannel fromSC, AbstractMessage inputMessage,
					SocketResourceManagerIF socketResourceManager, PersonalLoginManagerIF personalMemberManager,
					MessageProtocolIF messageProtocol, ClassLoader classLoaderOfServerTask,
					ServerObjectCacheManagerIF serverObjectCacheManager) {
				super(fromSC, inputMessage, socketResourceManager, personalMemberManager, messageProtocol, classLoaderOfServerTask,
						serverObjectCacheManager);
			}

			public void addSyncOutputMessage(AbstractMessage syncOutputMessage) throws InterruptedException {
				if (! (syncOutputMessage instanceof BoardDetailRes)) {
					fail("the parameter syncOutputMessage is not a instance of MessageResultRes class");
				}
				
				
				log.info("success, outObj={}", syncOutputMessage.toString());
			}
		}
		
		PersonalLoginManagerIF personalLoginManagerMock = Mockito.mock(PersonalLoginManagerIF.class);				
		ToLetterCarrier toLetterCarrierMock = new ToLetterCarrierMock(null, null, null, null, null, null, null);
				
		BoardDetailReq inObj = new BoardDetailReq();
		inObj.setBoardId(BoardType.FREE.getValue());
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
