package kr.pe.codda.weblib.jdf;

import javax.servlet.http.HttpServletRequest;

import kr.pe.codda.weblib.sitemenu.AdminSiteMenuManger;

public abstract class AbstractUserJSP extends AbstractJSP {

	private static final long serialVersionUID = 6189100903301427726L;

	private AdminSiteMenuManger adminSiteMenuManger = AdminSiteMenuManger.getInstance();

	@Override
	public String getSiteNavbarString(HttpServletRequest request) {
		return adminSiteMenuManger.getSiteNavbarString(getGroupRequestURL(request), isUserLogin(request));
	}
}
