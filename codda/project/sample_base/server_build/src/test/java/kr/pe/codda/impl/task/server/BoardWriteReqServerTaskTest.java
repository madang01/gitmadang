package kr.pe.codda.impl.task.server;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.mockito.Mockito;

import junitlib.AbstractJunitTest;
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.lib.BoardType;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardWriteReqServerTaskTest extends AbstractJunitTest {	
	@Test
	public void testDoTask() {
		PersonalLoginManagerIF personalLoginManagerMock = Mockito.mock(PersonalLoginManagerIF.class);				
		ToLetterCarrier toLetterCarrierMock = Mockito.mock(ToLetterCarrier.class);
				
		BoardWriteReq inObj = new BoardWriteReq();
		inObj.setBoardId(BoardType.FREE.getBoardID());
		inObj.setSubject("테스트 주제06");
		inObj.setContent("내용::그림2하나를 그리다");
		inObj.setAttachId(0);
		inObj.setUserId("test01");
		inObj.setIp("127.0.0.1");		
		
		BoardWriteReqServerTask boardWriteReqServerTask= new BoardWriteReqServerTask();
		
		try {
			boardWriteReqServerTask.doTask(mainProjectName, 
					personalLoginManagerMock, toLetterCarrierMock, inObj);
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
	}
}
