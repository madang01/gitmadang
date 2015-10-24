package kr.pe.sinnori.gui.config.screen;

import java.util.List;


public interface SinnoriInstalledPathWindowManagerIF extends WindowManagerIF {
	public void goMainProjectManagerScreen(String sinnoriInstalledPathString, List<String> mainProjectNameList);
}
