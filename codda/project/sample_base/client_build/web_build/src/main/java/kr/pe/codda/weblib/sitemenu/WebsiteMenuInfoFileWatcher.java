package kr.pe.codda.weblib.sitemenu;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;

/**
 * 사용자 사이트 메뉴 파트 문자열 파일에 대한 모니터 클래스
 * 
 * stackoverflow.com 에서 'timrs2998' 님 2015년 1월 2일 답글중 
 * FileWatcher.java 소스 인용하여 용도에 맞게 수정함
 *  
 * 참고 주소 : https://stackoverflow.com/questions/16251273/can-i-watch-for-single-file-change-with-watchservice-not-the-whole-directory
 * 
 * @author Won Jonghoon
 *
 */
public class WebsiteMenuInfoFileWatcher extends Thread {
	private InternalLogger log = InternalLoggerFactory.getInstance(WebsiteMenuInfoFileWatcher.class);

	private JsonArray rootMenuListJsonArray = null;
	private File watcherFile;
    private AtomicBoolean stop = new AtomicBoolean(false);

    public WebsiteMenuInfoFileWatcher() {    	
    	CoddaConfigurationManager coddaConfigurationManager = CoddaConfigurationManager.getInstance();
    	CoddaConfiguration runningProjectConfiguration = coddaConfigurationManager.getRunningProjectConfiguration();
    	
    	String installedPathString = runningProjectConfiguration.getInstalledPathString();
    	String mainProjectName = runningProjectConfiguration.getMainProjectName();
    	
    	String userWebRootPathString = WebRootBuildSystemPathSupporter.getUserWebRootPathString(installedPathString, mainProjectName);
    	
    	String watcherFilePathString = new StringBuilder().append(userWebRootPathString)
		.append(File.separator)
		.append(WebCommonStaticFinalVars.USER_WEBSITE_MENU_INFO_FILE).toString();    	

    	watcherFile  = new File(watcherFilePathString);
    	
    	if (! watcherFile.exists()) {
    		log.error("the user website menu infomation file[{}] doesn't exist", watcherFile.getAbsoluteFile());
    		System.exit(1);
    	}
    	
    	if (! watcherFile.isFile()) {
    		log.error("the user website menu infomation file[{}] is not a regular file", watcherFile.getAbsoluteFile());
    		System.exit(1);
    	}
    	
        doOnChange();
    }

    public boolean isStopped() { return stop.get(); }
    public void stopThread() { stop.set(true); }

    private void doOnChange() {   	
    	File workingFile = new File(watcherFile.getAbsolutePath());
    	
    	if (workingFile.length() > WebCommonStaticFinalVars.USER_WEBSITE_MENU_INFO_FILE_MAX_SIZE) {    		
    		log.warn("the user website menu infomation file[{}]'s size[{}] is is greater than max size[{}]", 
    				workingFile.getAbsoluteFile(), workingFile.length(), WebCommonStaticFinalVars.USER_WEBSITE_MENU_INFO_FILE_MAX_SIZE);
    		return;
    	}
    	
    	
    	byte[] buffer = null;
    	try {
    		buffer = CommonStaticUtil.readFileToByteArray(workingFile, 10*1024*1024L);
    		
    		log.info("'일반 사용자 웹사이트 메뉴 정보 파일' 읽기 완료");
    	} catch(Exception e) {
    		log.warn("'일반 사용자 웹사이트 메뉴 정보 파일' 읽기 실패", e);
    		return;
    	}
    	
    	String treeSiteMenuResJsonString = new String(buffer, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
    	JsonParser jsonParser = new JsonParser();
    	JsonElement treeSiteMenuResJsonElement = null;
    	
    	try {
    		treeSiteMenuResJsonElement = jsonParser.parse(treeSiteMenuResJsonString);
    	} catch(Exception e) {
    		log.warn("'일반 사용자 웹사이트 메뉴 정보 파일'의 내용 json 파싱 실패", e);
    		return;
    	}
    	if (! treeSiteMenuResJsonElement.isJsonObject()) {
    		log.warn("the var treeSiteMenuResJsonElement is not a JsonObject");
    		return;
    	}
    	JsonObject jsonObject = treeSiteMenuResJsonElement.getAsJsonObject();
		JsonElement rootMenuListJsonElement = jsonObject.get("rootMenuList");
		if (null == rootMenuListJsonElement) {
			log.warn("the rootMenuList JsonElement dosn't exist in the website menu info file");
    		return;
		}
		
		if (! rootMenuListJsonElement.isJsonArray()) {
			log.warn("the rootMenuList JsonElement is not a JsonArray");
			return;
		}
		
		rootMenuListJsonArray = rootMenuListJsonElement.getAsJsonArray();
		
		log.info("'일반 사용자 웹사이트 메뉴 정보 파일' 로 부터 루트 메뉴 목록 JsonArray 추출 완료");
    }
    
    public JsonArray getRootMenuListJsonArray() {
    	return rootMenuListJsonArray;
    }

    @Override
    public void run() {	
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {        	
            Path path = watcherFile.toPath().getParent();
            path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            while (!isStopped()) {
                WatchKey key = null;
                try { key = watcher.take(); }
                catch (InterruptedException e) { 
                	log.info("the WebsiteMenuInfoFileWatcher thread interrupt exist");
                	return;
                }
                if (key == null) { Thread.yield(); continue; }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        Thread.yield();
                        continue;
                    } else if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
                            && filename.toString().equals(watcherFile.getName())) {
                        doOnChange();
                    }
                    boolean valid = key.reset();
                    if (!valid) { break; }
                }
                Thread.yield();
            }
            
            log.info("the WebsiteMenuInfoFileWatcher thread loop exist");
        } catch (Throwable e) {
            // Log or rethrow the error
        	log.warn("the WebsiteMenuInfoFileWatcher thread unknow error exist", e);
        }
    }

}
