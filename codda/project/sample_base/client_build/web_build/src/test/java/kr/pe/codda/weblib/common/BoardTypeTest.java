package kr.pe.codda.weblib.common;

import org.junit.Test;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class BoardTypeTest {
	private InternalLogger log = InternalLoggerFactory.getInstance(BoardTypeTest.class);
	
	@Test
	public void test() {
		log.info("{}", BoardType.getSetString());
	}
}
