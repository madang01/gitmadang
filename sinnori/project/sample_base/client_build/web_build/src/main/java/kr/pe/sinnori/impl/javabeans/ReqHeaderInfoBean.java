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

package kr.pe.sinnori.impl.javabeans;

import java.io.Serializable;


/**
 * 
 * @author Won Jonghoon
 *
 */
public class ReqHeaderInfoBean implements Serializable {
	private static final long serialVersionUID = 8322800532312639723L;
	public String title="Not Login Test:this jsp page is called directly";
	public int headerInfoSize=0;
	public class HeaderInfoList {
		public String headerKey;
		public String headerValue;
	}
	
	public HeaderInfoList headerInfoList[] = new HeaderInfoList[0];

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the headerInfoSize
	 */
	public int getHeaderInfoSize() {
		return headerInfoSize;
	}

	/**
	 * @param headerInfoSize the headerInfoSize to set
	 */
	public void setHeaderInfoSize(int headerInfoSize) {
		this.headerInfoSize = headerInfoSize;
	}

	/**
	 * @return the headerInfoList
	 */
	public HeaderInfoList[] getHeaderInfoList() {
		return headerInfoList;
	}

	/**
	 * @param headerInfoList the headerInfoList to set
	 */
	public void setHeaderInfoList(HeaderInfoList[] headerInfoList) {
		this.headerInfoList = headerInfoList;
	}
	
	
}
