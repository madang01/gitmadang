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

/**
 * 메일 제어기 인터페이스
 * @author Jonghoon Won
 *
 */
public interface MainControllerIF {
	/**
	 * 2단계 화면 이동
	 * @param sinnoriInstallPathName 신놀이 프레임 워크 설치 경로
	 */
	public void nextStep2Screen(String sinnoriInstallPathName);
	/**
	 * 3단계 화면 이동
	 */
	public void nextStep3Screen();
	/**
	 * 완료 처리
	 */
	public void finish();
}
