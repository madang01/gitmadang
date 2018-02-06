package kr.pe.sinnori.client.asyn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;

import kr.pe.sinnori.client.connection.asyn.mailbox.AsynMailboxMapper;
import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public class AsynMailboxMapperTest extends AbstractJunitTest {
	
	@Test
	public void testGetAsynMailbox_theParameterMailBoxID_lessThanZero() {
		
		int totalNumberOfAsynMailbox = 10;
		int socketTimeOut = 1000;
		int outputMessageQueueSize = 5;
		
		LinkedBlockingQueue<WrapReadableMiddleObject> outputMessageQueue 
			= new LinkedBlockingQueue<WrapReadableMiddleObject>(outputMessageQueueSize); 
		
		AsynMailboxMapper asynMailboxMapper = new AsynMailboxMapper(totalNumberOfAsynMailbox, socketTimeOut, outputMessageQueue);
		
		int mailboxID = -1;
		try {
			asynMailboxMapper.getAsynMailbox(mailboxID);
			
			fail("no IndexOutOfBoundsException");
		} catch (IndexOutOfBoundsException e){
			String errorMessage = e.getMessage();
			String expectedErrorMessage = String.format("the parameter mailboxID[%d] is less than zero", mailboxID);
			
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
		int outputMessageQueueSize = 5;
		
		LinkedBlockingQueue<WrapReadableMiddleObject> outputMessageQueue 
			= new LinkedBlockingQueue<WrapReadableMiddleObject>(outputMessageQueueSize); 
		
		AsynMailboxMapper asynMailboxManager = new AsynMailboxMapper(totalNumberOfAsynMailbox, socketTimeOut, outputMessageQueue);
		
		int mailboxID = totalNumberOfAsynMailbox;
		try {
			asynMailboxManager.getAsynMailbox(mailboxID);
			
			fail("no IndexOutOfBoundsException");
		} catch (IndexOutOfBoundsException e){
			String errorMessage = e.getMessage();
			String expectedErrorMessage = String.format("the parameter mailboxID[%d] is out of range(0 ~ [%d])", 
					mailboxID, (totalNumberOfAsynMailbox - 1));
			
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
		int outputMessageQueueSize = 5;
		
		LinkedBlockingQueue<WrapReadableMiddleObject> outputMessageQueue 
			= new LinkedBlockingQueue<WrapReadableMiddleObject>(outputMessageQueueSize); 
		
		AsynMailboxMapper asynMailboxManager = new AsynMailboxMapper(totalNumberOfAsynMailbox, socketTimeOut, outputMessageQueue);		
		
		try {
			int actualTotalNumberOfAsynMailbox = asynMailboxManager.getTotalNumberOfAsynMailbox();
			
			assertEquals(totalNumberOfAsynMailbox, actualTotalNumberOfAsynMailbox);
		} catch (Exception e){
			String errorMessage = "unknown error::"+e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
	}
}
