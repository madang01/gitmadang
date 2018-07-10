package gson;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;

import junitlib.AbstractJunitTest;
import kr.pe.codda.impl.message.MenuListRes.MenuListRes;

public class GSonTest extends AbstractJunitTest {
	
	@Test
	public void test() {
		MenuListRes menuListRes = new MenuListRes();
		
		
		List<MenuListRes.Menu> menuList = new ArrayList<MenuListRes.Menu>();
		MenuListRes.Menu menu = new MenuListRes.Menu();
		menu.setMenuNo(1);
		menu.setParentNo(0);
		menu.setOrderSeq((short)0);
		menu.setDepth((short)0);
		menu.setMenuName("테스트메뉴01");
		menu.setLinkURL("/test01");
		
		menuList.add(menu);
		
		menuListRes.setCnt(menuList.size());
		menuListRes.setMenuList(menuList);
		
		String menuListResJson = new Gson().toJson(menuListRes);
		
		
		log.info("menuListResJson={}", menuListResJson);
		
	}
}
