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

package kr.pe.codda.impl.message.ChildMenuAddReq;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * ChildMenuAddReq message
 * @author Won Jonghoon
 *
 */
public class ChildMenuAddReq extends AbstractMessage {
	private long parentNo;
	private String menuName;
	private String linkURL;

	public long getParentNo() {
		return parentNo;
	}

	public void setParentNo(long parentNo) {
		this.parentNo = parentNo;
	}
	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public String getLinkURL() {
		return linkURL;
	}

	public void setLinkURL(String linkURL) {
		this.linkURL = linkURL;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("childMenuAddReq[");
		builder.append("parentNo=");
		builder.append(parentNo);
		builder.append(", menuName=");
		builder.append(menuName);
		builder.append(", linkURL=");
		builder.append(linkURL);
		builder.append("]");
		return builder.toString();
	}
}