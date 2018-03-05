package kr.pe.sinnori.client.asyn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import kr.pe.sinnori.client.connection.asyn.share.AsynPrivateMailboxMapper;
import kr.pe.sinnori.common.AbstractJunitTest;

public class AsynMailboxMapperTest extends AbstractJunitTest {
	
	@Test
	public void testGetAsynMailbox_theParameterMailBoxID_lessThanOrEqualToZero() {
		
		int totalNumberOfAsynMailbox = 10;
		int socketTimeOut = 1000;
				
		AsynPrivateMailboxMapper asynMailboxMapper = new AsynPrivateMailboxMapper(totalNumberOfAsynMailbox, socketTimeOut);
		
		int mailboxID = 0;
		try {
			asynMailboxMapper.getAsynMailbox(mailboxID);
			
			fail("no IndexOutOfBoundsException");
		} catch (IndexOutOfBoundsException e){
			String errorMessage = e.getMessage();
			String expectedErrorMessage = String.format("the parameter mailboxID[%d] is less than or equal to zero", mailboxID);
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e){
			String errorMessage = "unknown error::"+e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
	}

	@Test
	public void testGetAsynMailbox_theParameterMailBoxID_greaterThanMax() {
		
		int totalNumberOfAsynMailbox = 10;
		int socketTimeOut = 1000;
		
		
		AsynPrivateMailboxMapper asynMailboxManager = new AsynPrivateMailboxMapper(totalNumberOfAsynMailbox, socketTimeOut);
		
		int mailboxID = totalNumberOfAsynMailbox+1;
		try {
			asynMailboxManager.getAsynMailbox(mailboxID);
			
			fail("no IndexOutOfBoundsException");
		} catch (IndexOutOfBoundsException e){
			String errorMessage = e.getMessage();
			String expectedErrorMessage = String.format("the parameter mailboxID[%d] is out of range(1 ~ [%d])", 
					mailboxID, totalNumberOfAsynMailbox);
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e){
			String errorMessage = "unknown error::"+e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
	}
	
	@Test
	public void testGetTotalNumberOfAsynMailbox() {
		
		int totalNumberOfAsynMailbox = 10;
		int socketTimeOut = 1000;
		/*int outputMessageQueueSize = 5;
		
		LinkedBlockingQueue<WrapReadableMiddleObject> outputMessageQueue 
			= new LinkedBlockingQueue<WrapReadableMiddleObject>(outputMessageQueueSize); */
		
		AsynPrivateMailboxMapper asynMailboxManager = new AsynPrivateMailboxMapper(totalNumberOfAsynMailbox, socketTimeOut);		
		
		try {
			int actualTotalNumberOfAsynMailbox = asynMailboxManager.getTotalNumberOfAsynPrivateMailboxs();
			
			assertEquals(totalNumberOfAsynMailbox, actualTotalNumberOfAsynMailbox);
		} catch (Exception e){
			String errorMessage = "unknown error::"+e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
	}
}
