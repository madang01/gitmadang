package kr.pe.sinnori.weblib.sitemenu;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SiteMenuMangerTest {
	private Logger log = LoggerFactory.getLogger(SiteMenuMangerTest.class);

	@Test
	public void testGetTopMenuPartString() {
		SiteMenuManger siteMenuManger = SiteMenuManger.getInstance();
		
		String topMenuPartString = siteMenuManger.getTopMenuPartString(SiteTopMenuType.INTRODUCE);
		
		log.info("topMenuPartString={}", topMenuPartString);
	}
	
	@Test
	public void testGetBodyURL() {
		SiteMenuManger siteMenuManger = SiteMenuManger.getInstance();
		
		String bodyURL = siteMenuManger.getBodyURL(SiteTopMenuType.COMMUNITY);
		
		log.info("bodyURL={}", bodyURL);
	}
	
	@Test
	public void testGetLeftMenuPartString() {
		SiteMenuManger siteMenuManger = SiteMenuManger.getInstance();
		
		String leftMenuPartString = siteMenuManger.getLeftMenuPartString(SiteTopMenuType.TEST_EXAMPLE, "/servlet/JDFLoginTest");
		
		log.info("leftMenuPartString={}", leftMenuPartString);
	}
	
	
}
