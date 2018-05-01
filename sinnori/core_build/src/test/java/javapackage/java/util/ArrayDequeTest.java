package javapackage.java.util;

import java.util.ArrayDeque;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;

public class ArrayDequeTest extends AbstractJunitTest {

	@Test
	public void test() {
		ArrayDeque<String> tempQueue = new ArrayDeque<String>();
		tempQueue.add("t1");
		tempQueue.add("t2");
		tempQueue.add("t3");
		
		for (String tempString: tempQueue) {
			log.info(tempString);
		}
		
		log.info("tempQueue.size={}", tempQueue.size());
	}
}
