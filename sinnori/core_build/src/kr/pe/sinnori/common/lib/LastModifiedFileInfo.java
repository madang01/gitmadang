/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.pe.sinnori.common.lib;

import java.io.File;


/**
 * <pre>
 * 수정 여부 추적 대상 파일 정보 클래스
 * </pre> 
 * @author Won Jonghoon
 * 
 */
public class LastModifiedFileInfo {
	/**
	 * <pre>
	 * 수정 여부 추적 대상 파일의 마지막 수정 일자
	 * 처음 생성시 수정 여부 추적 대상 파일의 마지막 수정 일자로 설정되며,
	 * 응용 프로그램의 추적 여부 판단후 {@link #update()} 호출을 통해서 재 갱신된다.
	 * </pre>
	 */
	private Long lastModified;
	
	/** 수정 여부 추적 대상 파일 */
	private File lastModifiedFile = null;
	
	/** 수정 여부 추적 대상 파일과 더불어 보존해야할 객체 */
	private Object userObject = null;

	/**
	 * 수정 여부 추적 대상 파일 정보 클래스 생성자.
	 * @param lastModifiedFile 수정 여부 추적 대상 파일
	 * @param userObject 수정 여부 추적 대상 파일과 더불어 보존해야할 객체
	 */
	public LastModifiedFileInfo(File lastModifiedFile, Object userObject) {
		this.lastModifiedFile = lastModifiedFile;
		this.lastModified = lastModifiedFile.lastModified();
		this.userObject = userObject;
	}	
		
	public Long getLastModified() {
		return lastModified;
	}

	public boolean isModified() {
		if (! lastModifiedFile.exists()) {
			return true;
		}
		
		return (lastModified != lastModifiedFile.lastModified());
	}
	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {		
		this.userObject = userObject;
	}
	
	public void update() {
		this.lastModified = lastModifiedFile.lastModified();
	}
}
