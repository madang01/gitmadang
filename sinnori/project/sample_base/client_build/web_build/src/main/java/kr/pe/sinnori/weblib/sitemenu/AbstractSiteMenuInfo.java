package kr.pe.sinnori.weblib.sitemenu;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSiteMenuInfo {
	private Logger log = LoggerFactory.getLogger(AbstractSiteMenuInfo.class);
	
	private final List<SiteTopMenuInfo > siteTopMenuInfoList =	new ArrayList<SiteTopMenuInfo>();	
	
	public AbstractSiteMenuInfo() {
		build();
	}
	
	protected void add(String siteTopMenuName, String bodyURL) throws IllegalStateException {
		int topMenuIndex = siteTopMenuInfoList.size();
		SiteTopMenuType siteTopMenuType = SiteTopMenuType.match(topMenuIndex);
		if (siteTopMenuType.getTopMenuIndex() != topMenuIndex) {
			log.warn("the expected top menu index[{}] is not same to the actual top menu index[{}]", 
					topMenuIndex, siteTopMenuType.toString());
			
			throw new IllegalStateException("unmatch top menu index");
		}
		
		siteTopMenuInfoList.add(new SiteTopMenuInfo(siteTopMenuType, siteTopMenuName, bodyURL)); 
	}
	
	public List<SiteTopMenuInfo> getSiteTopMenuInfoList() {
		return siteTopMenuInfoList;
	}
	
	/**
	 * @param targetSiteTopMenuType
	 * @return 탑 메뉴 타입과 일치하는 사이트 탑 메뉴 정보, 단 불일치시 null 을 리턴한다.  
	 */
	public SiteTopMenuInfo match(SiteTopMenuType targetSiteTopMenuType) {
		for (SiteTopMenuInfo siteTopMenuInfo : siteTopMenuInfoList) {
			if (siteTopMenuInfo.getSiteTopMenuType().equals(targetSiteTopMenuType)) {
				return siteTopMenuInfo;
			}
		}
		return null;
	}	 
	
	protected abstract void build();
}
