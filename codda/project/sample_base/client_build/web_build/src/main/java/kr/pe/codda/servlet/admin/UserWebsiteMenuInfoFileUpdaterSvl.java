package kr.pe.codda.servlet.admin;

import java.io.File;
import java.io.FileOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.message.TreeSiteMenuReq.TreeSiteMenuReq;
import kr.pe.codda.impl.message.TreeSiteMenuRes.TreeSiteMenuRes;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractAdminLoginServlet;

import com.google.gson.Gson;

public class UserWebsiteMenuInfoFileUpdaterSvl extends
		AbstractAdminLoginServlet {
	private static final long serialVersionUID = 654417668900237779L;
	
	/*private String getTabStrings(int tapStep) {
		StringBuilder tapStringBuilder = new StringBuilder();
		for (int i=0; i < tapStep; i++) {
			tapStringBuilder.append("\t");
		}
		return tapStringBuilder.toString();
	}
	
	private String makeWebsiteMenuPartStringUsingMenuList(List<TreeSiteMenuRes.Menu> menuList, int tapStep) {
		StringBuilder websiteMenuPartStringBuilder = new StringBuilder();
		for (TreeSiteMenuRes.Menu menu : menuList) {
			List<TreeSiteMenuRes.Menu> childMenuList = menu.getChildMenuList();
			
			if (null == childMenuList || childMenuList.isEmpty()) {
				websiteMenuPartStringBuilder.append(getTabStrings(tapStep));
				
				if (menuGroupURL.equals(menu.getLinkURL())) {
					siteNavbarStringBuilder.append("<li class=\"active\">");
				} else {
					siteNavbarStringBuilder.append("<li>");
				}
				websiteMenuPartStringBuilder.append("<li>");
				
				websiteMenuPartStringBuilder.append("<a href=\"");
				websiteMenuPartStringBuilder.append(menu.getLinkURL());
				websiteMenuPartStringBuilder.append("\">");
				websiteMenuPartStringBuilder.append(menu.getMenuName());
				websiteMenuPartStringBuilder.append("</a></li>");
				websiteMenuPartStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			} else {
				websiteMenuPartStringBuilder.append(getTabStrings(tapStep));
				websiteMenuPartStringBuilder.append("<li class=\"dropdown\">");
				websiteMenuPartStringBuilder.append(CommonStaticFinalVars.NEWLINE);
				websiteMenuPartStringBuilder.append(getTabStrings(tapStep+1));
				websiteMenuPartStringBuilder.append("<a class=\"dropdown-toggle\" data-toggle=\"dropdown\" href=\"");
				websiteMenuPartStringBuilder.append(menu.getLinkURL());
				websiteMenuPartStringBuilder.append("\">");
				websiteMenuPartStringBuilder.append(menu.getMenuName());
				websiteMenuPartStringBuilder.append("<span class=\"caret\"></span></a>");
				websiteMenuPartStringBuilder.append(CommonStaticFinalVars.NEWLINE);
				
				websiteMenuPartStringBuilder.append(getTabStrings(tapStep+1));
				websiteMenuPartStringBuilder.append("<ul class=\"dropdown-menu\">");
				websiteMenuPartStringBuilder.append(CommonStaticFinalVars.NEWLINE);
				
				websiteMenuPartStringBuilder.append(makeWebsiteMenuPartStringUsingMenuList(childMenuList, tapStep+2));
				//siteNavbarStringBuilder.append(CommonStaticFinalVars.NEWLINE);
				
				websiteMenuPartStringBuilder.append(getTabStrings(tapStep+1));
				websiteMenuPartStringBuilder.append("</ul>");
				websiteMenuPartStringBuilder.append(CommonStaticFinalVars.NEWLINE);
				
				websiteMenuPartStringBuilder.append(getTabStrings(tapStep));
				websiteMenuPartStringBuilder.append("</li>");
				websiteMenuPartStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			}
		}
		return websiteMenuPartStringBuilder.toString();
	}*/

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		CoddaConfigurationManager coddaConfigurationManager = CoddaConfigurationManager
				.getInstance();
		CoddaConfiguration runningProjectConfiguration = coddaConfigurationManager
				.getRunningProjectConfiguration();

		String installedPathString = runningProjectConfiguration
				.getInstalledPathString();
		String mainProjectName = runningProjectConfiguration
				.getMainProjectName();

		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager
				.getInstance().getMainProjectConnectionPool();
		
		TreeSiteMenuReq treeSiteMenuReq = new TreeSiteMenuReq();
		
		AccessedUserInformation accessedUserformation = getAccessedUserInformationFromSession(req);
		treeSiteMenuReq.setRequestedUserID(accessedUserformation.getUserID());
		
		TreeSiteMenuRes treeSiteMenuRes = null;

		AbstractMessage outputMessage = mainProjectConnectionPool
				.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), treeSiteMenuReq);

		if (!(outputMessage instanceof TreeSiteMenuRes)) {

			if ((outputMessage instanceof MessageResultRes)) {
				MessageResultRes messageResultRes = (MessageResultRes) outputMessage;
				String errorMessage = "트리형 메뉴 목록 조회가 실패하였습니다";
				String debugMessage = messageResultRes.toString();
				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			} else {
				String errorMessage = "트리형 메뉴 목록 조회가 실패했습니다";
				String debugMessage = new StringBuilder("입력 메시지[")
						.append(treeSiteMenuReq.getMessageID())
						.append("]에 대한 비 정상 출력 메시지[")
						.append(outputMessage.toString()).append("] 도착")
						.toString();

				log.error(debugMessage);

				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			}

		}

		treeSiteMenuRes = (TreeSiteMenuRes) outputMessage;
		
		/*
		final int tapStep = 5;
		String websiteMenuPartString = makeWebsiteMenuPartStringUsingMenuList(treeSiteMenuRes.getRootMenuList(), tapStep);
		*/
		
		Gson gson = new Gson();
		String treeSiteMenuResJsonString = gson.toJson(treeSiteMenuRes);
		
		String userWebRootPathString = WebRootBuildSystemPathSupporter.getUserWebRootPathString(installedPathString, mainProjectName);
		File watcherFile  = new File(new StringBuilder().append(userWebRootPathString)
    			.append(File.separator)
    			.append(WebCommonStaticFinalVars.USER_WEBSITE_MENU_INFO_FILE).toString());
    	
    	if ( watcherFile.exists()) {
    		if (! watcherFile.isFile()) {
        		String errorMessage = new StringBuilder().append("the sitemenu part string file[")
        				.append(watcherFile.getAbsoluteFile())
        				.append("] is not a regular file").toString();
    			String debugMessage = null;
    			
    			log.warn(errorMessage);
    			
    			printErrorMessagePage(req, res, errorMessage, debugMessage);
    			return;
        	}
    	}
    	
    	FileOutputStream fos = null;
    	
    	try {
    		fos = new FileOutputStream(watcherFile, false);
    		fos.write(treeSiteMenuResJsonString.getBytes(CommonStaticFinalVars.SOURCE_FILE_CHARSET));
    	} catch(Exception e) {
    		log.warn("'웹사이트 메뉴 파트 문자열 파일' 쓰기 실패", e);
    		
    		String errorMessage = "'웹사이트 메뉴 파트 문자열 파일' 쓰기 실패";
			String debugMessage = e.getMessage();
			
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
    	} finally {
    		if (null != fos) {
    			try {
    				fos.close();
    			} catch(Exception e1) {
    			}
    		}
    	} 
    	
    	printJspPage(req, res, "/jsp/menu/UserWebsiteMenuInfoFileUpdater.jsp");
	}
}
