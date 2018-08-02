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

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;

/**
 * TreeSiteMenuRes message
 * @author Won Jonghoon
 *
 */
public class TreeSiteMenuRes extends AbstractMessage {
	private int rootMenuListSize;

	public static class Menu {
		private long menuNo;
		private long parentNo;
		private short depth;
		private short orderSeq;
		private String menuName;
		private String linkURL;
		private int childMenuListSize;
		private java.util.List<Menu> childMenuList;

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
		public int getChildMenuListSize() {
			return childMenuListSize;
		}

		public void setChildMenuListSize(int childMenuListSize) {
			this.childMenuListSize = childMenuListSize;
		}
		
		public java.util.List<Menu> getChildMenuList() {
			return childMenuList;
		}

		public void setChildMenuList(java.util.List<Menu> childMenuList) {
			this.childMenuList = childMenuList;
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
			builder.append(", childMenuListSize=");
			builder.append(childMenuListSize);
			builder.append(", childMenuList=");
			if (null == childMenuList) {
				builder.append("null");
			} else {
				if (childMenuListSize != childMenuList.size()) {
					String errorMessage = new StringBuilder()
							.append("the var childMenuListSize[")
							.append(childMenuListSize)
							.append("] is different from the size[")
							.append(childMenuList.size())
							.append("] of the array var childMenuList").toString();
					throw new IllegalArgumentException(errorMessage);
				}
				
				if (0 == childMenuListSize) {
					builder.append("empty");
				} else {
					builder.append("[");
					for (int i=0; i < childMenuListSize; i++) {
						Menu menu = childMenuList.get(i);
						if (0 == i) {
							builder.append("menu[");
						} else {
							builder.append(CommonStaticFinalVars.NEWLINE);
							builder.append(", menu[");
						}
						builder.append(i);
						builder.append("]=");
						builder.append(menu.toString());
						
					}
				}
			}
			builder.append("]");
			return builder.toString();
		}
	}

	private java.util.List<Menu> rootMenuList;

	public int getRootMenuListSize() {
		return rootMenuListSize;
	}

	public void setRootMenuListSize(int rootMenuListSize) {
		this.rootMenuListSize = rootMenuListSize;
	}
	public java.util.List<Menu> getRootMenuList() {
		return rootMenuList;
	}

	public void setRootMenuList(java.util.List<Menu> rootMenuList) {
		this.rootMenuList = rootMenuList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("treeSiteMenuRes[");
		builder.append("childMenuListSize=");
		builder.append(rootMenuListSize);

		builder.append(", rootMenuList=");
		if (null == rootMenuList) {
			builder.append("null");
		} else {
			if (rootMenuListSize != rootMenuList.size()) {
				String errorMessage = new StringBuilder()
						.append("the var rootMenuListSize[")
						.append(rootMenuListSize)
						.append("] is different from the size[")
						.append(rootMenuList.size())
						.append("] of the array var rootMenuList").toString();
				throw new IllegalArgumentException(errorMessage);
			}
			
			if (0 == rootMenuListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < rootMenuListSize; i++) {
					Menu menu = rootMenuList.get(i);
					if (0 == i) {
						builder.append("menu[");
					} else {
						builder.append(CommonStaticFinalVars.NEWLINE);
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