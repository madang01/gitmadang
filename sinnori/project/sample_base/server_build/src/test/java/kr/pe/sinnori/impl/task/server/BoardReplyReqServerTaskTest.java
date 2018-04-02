package kr.pe.sinnori.impl.task.server;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.mockito.Mockito;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.impl.message.BoardReplyReq.BoardReplyReq;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.lib.BoardType;
import kr.pe.sinnori.server.lib.MembershipLevel;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class BoardReplyReqServerTaskTest extends AbstractJunitTest {
	@Test
	public void test() {
		String sqlString = new StringBuilder("if ({0} = ")
				.append(MembershipLevel.USER.getValue())
				.append(", '")
				.append(MembershipLevel.USER.getName())
				.append("', if ({0} = ")
				.append(MembershipLevel.ADMIN.getValue())
				.append(", '")
				.append(MembershipLevel.ADMIN.getName())
				.append("', '알수없음'))").toString();
		// "if ({0} = 1, '일반회원', if ({0} = 0, '관리자', '알수없음'))"
		
		System.out.println(sqlString);
	}
	
	
	@Test
	public void testDoTask() {
		PersonalLoginManagerIF personalLoginManagerMock = Mockito.mock(PersonalLoginManagerIF.class);				
		ToLetterCarrier toLetterCarrierMock = Mockito.mock(ToLetterCarrier.class);
				
		BoardReplyReq inObj = new BoardReplyReq();
		inObj.setBoardId(BoardType.FREE.getValue());
		inObj.setParentBoardNo(7);
		inObj.setSubject("테스트 주제02-3");
		inObj.setContent("내용::그림2-3하나를 그리다");
		inObj.setAttachId(0);
		inObj.setUserId("test01");
		inObj.setIp("127.0.0.1");		
		
		BoardReplyReqServerTask boardReplyReqServerTask= new BoardReplyReqServerTask();
		
		try {
			boardReplyReqServerTask.doTask(mainProjectName, 
					personalLoginManagerMock, toLetterCarrierMock, inObj);
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
	}
	
}
