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

package kr.pe.codda.impl.message.TreeSiteMenuRes;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * TreeSiteMenuRes message
 * @author Won Jonghoon
 *
 */
public class TreeSiteMenuRes extends AbstractMessage {
	private int cnt;

	public static class Menu {
		private long menuNo;
		private long parentNo;
		private short depth;
		private short orderSeq;
		private String menuName;
		private String linkURL;
		private int cnt;

		public long getMenuNo() {
			return menuNo;
		}

		public void setMenuNo(long menuNo) {
			this.menuNo = menuNo;
		}
		public long getParentNo() {
			return parentNo;
		}

		public void setParentNo(long parentNo) {
			this.parentNo = parentNo;
		}
		public short getDepth() {
			return depth;
		}

		public void setDepth(short depth) {
			this.depth = depth;
		}
		public short getOrderSeq() {
			return orderSeq;
		}

		public void setOrderSeq(short orderSeq) {
			this.orderSeq = orderSeq;
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
		public int getCnt() {
			return cnt;
		}

		public void setCnt(int cnt) {
			this.cnt = cnt;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Menu[");
			builder.append("menuNo=");
			builder.append(menuNo);
			builder.append(", parentNo=");
			builder.append(parentNo);
			builder.append(", depth=");
			builder.append(depth);
			builder.append(", orderSeq=");
			builder.append(orderSeq);
			builder.append(", menuName=");
			builder.append(menuName);
			builder.append(", linkURL=");
			builder.append(linkURL);
			builder.append(", cnt=");
			builder.append(cnt);
			builder.append("]");
			return builder.toString();
		}
	}

	private java.util.List<Menu> menuList;

	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public java.util.List<Menu> getMenuList() {
		return menuList;
	}

	public void setMenuList(java.util.List<Menu> menuList) {
		this.menuList = menuList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("treeSiteMenuRes[");
		builder.append("cnt=");
		builder.append(cnt);

		builder.append(", menuList=");
		if (null == menuList) {
			builder.append("null");
		} else {
			int menuListSize = menuList.size();
			if (0 == menuListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < menuListSize; i++) {
					Menu menu = menuList.get(i);
					if (0 == i) {
						builder.append("menu[");
					} else {
						builder.append(", menu[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(menu.toString());
				}
				builder.append("]");
			}
		}
		builder.append("]");
		return builder.toString();
	}
}