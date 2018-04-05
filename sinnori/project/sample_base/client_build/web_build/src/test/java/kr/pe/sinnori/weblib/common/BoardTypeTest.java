package kr.pe.sinnori.weblib.common;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoardTypeTest {
	private Logger log = LoggerFactory
			.getLogger(BoardTypeTest.class);
	
	@Test
	public void test() {
		log.info("{}", BoardType.getSetString());
	}
}
