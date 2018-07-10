package kr.pe.codda.weblib.sitemenu;

import org.junit.Test;

public class AdminSiteMenuMangerTest {

	@Test
	public void test() {
		AdminSiteMenuManger adminSiteMenuManger = AdminSiteMenuManger.getInstance();
		
		System.out.println(adminSiteMenuManger.getSiteNavbarString(true));
	}

}
