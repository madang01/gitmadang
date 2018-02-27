package javapackage.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitSupporter;

public class CollectionsTest extends AbstractJunitSupporter {
	
	
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
	/*
	@Test
	public void testTemp() {
		MEMBER_GUBUN memberGubun = MEMBER_GUBUN.valueOf((short)0);
		Logger log = LoggerFactory.getLogger("kr.pe.sinnori");
		log.info("memberGubun=[{}]", memberGubun);
	}*/
}
