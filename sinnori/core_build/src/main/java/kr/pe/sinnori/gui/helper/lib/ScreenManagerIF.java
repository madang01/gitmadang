package kr.pe.sinnori.gui.helper.lib;

import kr.pe.sinnori.common.buildsystem.MainProjectBuildSystemState;

public interface ScreenManagerIF {
	/**
	 * 화면 흐름도
	 *  
	 *  >> 첫화면
	 *  1. 신놀이 설치 경로 화면
	 *  2. 입출력 파일 셋 생성기 화면
	 *  
	 *  >> 입출력 파일 셋 생성기 화면
	 *  1. 입출력 종류별 생성기 화면
	 *  
	 *   >> 입출력 종류별 생성기 화면
	 *   1. 입출력 파일 셋 생성기 화면
	 *      
	 *  >> 신놀이 설치 경로 화면 
	 *  1.전체 메인 프로젝트 화면
	 *  
	 *  >> 전체 메인 프로젝트 화면
	 * 1. 개별적인 메인 프로젝트 편집 화면
	 * 
	 * >> 개별적인 메인 프로젝트 편집 화면
	 * 1. DBCP 편집 팝업 화면
	 * 2. 자식 프로젝트 편집 팝업 화면
	 * 3. 프로젝트 종속 입출력 관리자 팝업 화면
	 *  
	 *  >> 입출력 관리자 화면
	 */
	
	/** 첫화면으로 전환 */
	public void moveToFirstScreen();
	
	/** 신놀이 설치 화면으로 전환 */
	public void moveToSinnoriInstalledPathScreen();
	
	/** 전체 주 프로젝트 편집 화면으로 전환  */
	public void moveToAllMainProjectManagerScreen(String sinnoriInstalledPathString);
	
	/** 개별적인 주 프로젝트 편집 화면으로 전환 */
	public void moveToMainProjectEditScreen(MainProjectBuildSystemState mainProjectBuildSystemState);
	
	/** 입출력 파일 셋 생성기 화면으로 전환 */
	public void moveToIOFileSetBuilderScreen();
	
	
	/** 입출력 종류별 생성기 화면으로 전환 */
	public void moveToEachIOFileTypeBuilderScreen();
		
}
