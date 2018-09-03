package kr.pe.codda.impl.task.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.config.subset.AllDBCPPartConfiguration;
import kr.pe.codda.common.config.subset.DBCPParConfiguration;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.BoardListReq.BoardListReq;
import kr.pe.codda.impl.message.BoardListRes.BoardListRes;
import kr.pe.codda.server.lib.BoardType;
import kr.pe.codda.server.lib.MemberType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;

public class BoardListReqServerTaskTest extends AbstractJunitTest {	
	// final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME;;
	final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;;
	//final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.LOAD_TEST_DBCP_NAME;;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();		
		
		ServerDBUtil.initializeDBEnvoroment(TEST_DBCP_NAME);
		
		String userID = "test01";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "단위테스터용아이디1";
		String pwdHint = "힌트 그것이 알고싶다";
		String pwdAnswer = "힌트답변 말이여 방구여";
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberType.USER, userID, nickname, pwdHint, pwdAnswer, passwordBytes);
		} catch (ServerServiceException e) {
			String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[")
					.append(userID)
					.append("] 입니다").toString();
			String actualErrorMessag = e.getMessage();
			
			log.warn(actualErrorMessag, e);
			
			assertEquals(expectedErrorMessage, actualErrorMessag);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
	}
	
	@Test
	public void testDBCPConfig() {
		CoddaConfiguration runningProjectConfiguration = 
				CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();
		
		AllDBCPPartConfiguration allDBCPPart =  runningProjectConfiguration.getAllDBCPPartConfiguration();
		
		List<String> dbcpNameList = allDBCPPart.getDBCPNameList();
		for (String dbcpName : dbcpNameList) {
			DBCPParConfiguration dbcpPart = allDBCPPart.getDBCPPartConfiguration(dbcpName);
			
			File dbcpConfigFile = dbcpPart.getDBCPConfigFile();	
			
			log.info("dbcpName[{}] DBCPConfigFile[{}]", dbcpName, dbcpConfigFile.getName());
		}
	}
	
	@Test
	public void testDoService_ok() {		
		int pageNo = 1;
		int pageSize = 20;
		
		BoardListReq boardListReq = new BoardListReq();
		boardListReq.setRequestUserID("guest");
		boardListReq.setBoardID(BoardType.FREE.getBoardID());
		boardListReq.setPageNo(pageNo);
		boardListReq.setPageSize(pageSize);
		
		BoardListReqServerTask boardListReqServerTask= new BoardListReqServerTask();
		
		try {
			BoardListRes boardListRes = boardListReqServerTask.doWork(TEST_DBCP_NAME, boardListReq);
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
