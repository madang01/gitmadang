package kr.pe.sinnori.impl.task.server;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.mockito.Mockito;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.impl.message.BoardListReq.BoardListReq;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.lib.BoardType;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class BoardListReqServerTaskTest extends AbstractJunitTest {
	@Test
	public void testDoTask() {
		PersonalLoginManagerIF personalLoginManagerMock = Mockito.mock(PersonalLoginManagerIF.class);				
		ToLetterCarrier toLetterCarrierMock = Mockito.mock(ToLetterCarrier.class);
		int pageNo = 1;
		int pageSize = 20;
		
		BoardListReq inObj = new BoardListReq();
		inObj.setBoardId(BoardType.FREE.getValue());
		inObj.setPageSize(pageSize);
		inObj.setStartNo((pageNo-1)*pageSize);
		
		BoardListReqServerTask boardListReqServerTask= new BoardListReqServerTask();
		
		try {
			boardListReqServerTask.doTask(mainProjectName, 
					personalLoginManagerMock, toLetterCarrierMock, inObj);
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
	}
}
