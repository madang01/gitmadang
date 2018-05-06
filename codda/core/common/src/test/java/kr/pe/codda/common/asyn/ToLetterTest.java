package kr.pe.codda.common.asyn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.io.WrapBuffer;

public class ToLetterTest extends AbstractJunitTest {
	

	@Test
	public void testConstructor_theParameterToSCIsNull() {
		SocketChannel toSC=null;
		String messageID=null;
		int mailboxID=0;
		int mailID=0;
		ArrayDeque<WrapBuffer> wrapBufferList = null;
		
		try {
			new ToLetter(toSC, messageID, mailboxID, mailID, wrapBufferList);
			
			fail("not IllegalArgumentException");
			
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = "the parameter toSC is null";
			
			assertEquals(errorMessage, expectedMessage);
		} catch(Exception e) {
			fail("error");
		}
	}

	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_theParameterMessageIDIsNull() {
		SocketChannel toSC = null;
		try {
			toSC = SocketChannel.open();
		} catch (IOException e1) {
			fail("fail to open socekt");
		}
		String messageID=null;
		int mailboxID=0;
		int mailID=0;
		ArrayDeque<WrapBuffer> wrapBufferList = null;
		
		try {
			new ToLetter(toSC, messageID, mailboxID, mailID, wrapBufferList);
			
			fail("not IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			log.info(e.getMessage());
			throw e;
		} catch(Exception e) {
			log.info("unknown error", e);
			fail("error");
		}
	}
}
