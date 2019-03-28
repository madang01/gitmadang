package kr.pe.codda.server.lib;

import org.junit.Test;

import junitlib.AbstractJunitTest;

public class UserActivityTypeTest extends AbstractJunitTest {

	@Test
	public void test() {
		
		for (MemberActivityType userActivityType : MemberActivityType.values()) {
			log.info("userActivityType {}={}::{}", userActivityType.getName(), userActivityType.getValue(), (char)userActivityType.getValue());
		}
	}

}
