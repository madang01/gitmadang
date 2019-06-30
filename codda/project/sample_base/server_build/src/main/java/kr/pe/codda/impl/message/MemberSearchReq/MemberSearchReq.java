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

package kr.pe.codda.impl.message.MemberSearchReq;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * MemberSearchReq message
 * @author Won Jonghoon
 *
 */
public class MemberSearchReq extends AbstractMessage {
	private String requestedUserID;
	private byte memberState;
	private String searchID;
	private String fromDateString;
	private String toDateString;
	private int pageNo;
	private int pageSize;

	public String getRequestedUserID() {
		return requestedUserID;
	}

	public void setRequestedUserID(String requestedUserID) {
		this.requestedUserID = requestedUserID;
	}
	public byte getMemberState() {
		return memberState;
	}

	public void setMemberState(byte memberState) {
		this.memberState = memberState;
	}
	public String getSearchID() {
		return searchID;
	}

	public void setSearchID(String searchID) {
		this.searchID = searchID;
	}
	public String getFromDateString() {
		return fromDateString;
	}

	public void setFromDateString(String fromDateString) {
		this.fromDateString = fromDateString;
	}
	public String getToDateString() {
		return toDateString;
	}

	public void setToDateString(String toDateString) {
		this.toDateString = toDateString;
	}
	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("memberSearchReq[");
		builder.append("requestedUserID=");
		builder.append(requestedUserID);
		builder.append(", memberState=");
		builder.append(memberState);
		builder.append(", searchID=");
		builder.append(searchID);
		builder.append(", fromDateString=");
		builder.append(fromDateString);
		builder.append(", toDateString=");
		builder.append(toDateString);
		builder.append(", pageNo=");
		builder.append(pageNo);
		builder.append(", pageSize=");
		builder.append(pageSize);
		builder.append("]");
		return builder.toString();
	}
}