package javapackage.java.util;

import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;

public class LinkedListTest extends AbstractJunitTest {
	
	@Test
	public void test() {
		LinkedList<String> list = new LinkedList<String>(); 
		list.add("hello1");
		list.add("hello2");
		list.add("hello3");
	
		Iterator<String> iter = list.iterator();
		
		while(iter.hasNext()) {
			iter.next();
			iter.remove();
			break;
		}
		
		log.info("list size={}", list.size());
	}
}
