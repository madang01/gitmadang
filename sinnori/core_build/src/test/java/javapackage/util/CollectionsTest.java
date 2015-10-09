package javapackage.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class CollectionsTest {
	
	
	@Test(expected=java.lang.UnsupportedOperationException.class)
	public void testUnmodifiableList_add() {
		List<String> nameList = new ArrayList<>();
		
		nameList.add("testStr01");
		
		List<String> unmodifiableNameList = Collections.unmodifiableList(nameList);
		
		
		unmodifiableNameList.add("testStr02");
	}
	
	@Test(expected=java.lang.UnsupportedOperationException.class)
	public void testUnmodifiableList_remove() {
		List<String> nameList = new ArrayList<>();
		
		nameList.add("testStr01");
		
		List<String> unmodifiableNameList = Collections.unmodifiableList(nameList);
		
		
		unmodifiableNameList.remove("testStr02");
	}
}
