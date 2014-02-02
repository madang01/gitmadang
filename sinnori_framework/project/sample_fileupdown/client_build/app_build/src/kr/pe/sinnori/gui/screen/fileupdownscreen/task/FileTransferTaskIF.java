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

package kr.pe.sinnori.gui.screen.fileupdownscreen.task;

import kr.pe.sinnori.gui.screen.FileTranferProcessDialog;

/**
 * <pre>
 * 파일 송수신 상태를 보여주는 모달 윈도우에서 호출하는 사용자 정의 비지니스 로직 인터페이스.
 *  
 * 단 사용자 정의 비지니스 로직은 파일 송수신 로직을 직접 수행후 
 * 파일 송수신 상태를 보여주는 모달 윈도우에도 이를 알려 파일 진행상태를 보여주는것으로 제한한다.
 * </pre>
 * @author Jonghoon Won
 *
 */
public interface FileTransferTaskIF {
	/**
	 * <pre>
	 * 사용자가 정의해야할 비지니스 로직. 파일 송수신 로직을 직접 수행후 
	 * 파일 송수신 상태를 보여주는 모달 윈도우에도 이를 알려 파일 진행상태를 보여준다.
	 * </pre>
	 */
	public void doTask();
	
	/**
	 * 파일 송수신 상태를 보여주는 모달 윈도우를 설정한다.
	 * @param fileTranferProcessDialog 파일 송수신 상태를 보여주는 모달 윈도우
	 */
	public void setFileTranferProcessDialog(FileTranferProcessDialog fileTranferProcessDialog);

	/**
	 * 파일 송수신 작업 취소를 요청한다. 
	 */
	public void cancelTask();
	
	/**
	 * 파일 송수신 작업이 끝났을 경우 호출되는 메소드
	 */
	public void endTask();
}
