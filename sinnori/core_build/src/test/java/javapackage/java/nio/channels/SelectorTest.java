package javapackage.java.nio.channels;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectorTest {
	protected Logger log = LoggerFactory.getLogger(SelectorTest.class);
	
	@Test 
	public void test_open할수있는Selctor최대갯수() {
		List<Selector> selectorList = new LinkedList<Selector>();
		try {
			for (int i=0; i < Integer.MAX_VALUE; i++) {
				Selector tempSelector = Selector.open();
				selectorList.add(tempSelector);
			}
			
			fail("no Exception");
		} catch(Exception e) {
			log.warn("error", e);			
		} finally {
			log.info("selectorList.size={}", selectorList.size());
			for (Selector tempSelector : selectorList) {
				try {
					tempSelector.close();
				} catch (IOException e) {
				}
			}
		}		
	}
}
