package kr.pe.codda.impl.task.server;

import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.BoardListReq.BoardListReq;
import kr.pe.codda.impl.message.BoardListRes.BoardListRes;
import kr.pe.codda.server.lib.BoardType;
import kr.pe.codda.server.lib.ServerDBUtil;

public class BoardListReqServerTaskTest extends AbstractJunitTest {	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();		
		
		ServerDBUtil.initializeDBEnvoroment("testAdmin");
	}
	
	@Test
	public void testDoService_ok() {		
		int pageNo = 1;
		int pageLength = 20;
		
		BoardListReq boardListReq = new BoardListReq();
		boardListReq.setBoardID(BoardType.FREE.getBoardID());
		boardListReq.setPageLength(pageLength);		
		boardListReq.setPageOffset((pageNo-1)*pageLength);
		
		BoardListReqServerTask boardListReqServerTask= new BoardListReqServerTask();
		
		try {
			BoardListRes boardListRes = boardListReqServerTask.doService(boardListReq);
			log.info(boardListRes.toString());
		} catch(ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
}
