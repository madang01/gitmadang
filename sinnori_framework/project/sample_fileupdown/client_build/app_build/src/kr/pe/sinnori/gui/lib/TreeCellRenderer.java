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

package kr.pe.sinnori.gui.lib;
import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import kr.pe.sinnori.common.lib.CommonRootIF;

/**
 * 파일 목록을 보여주는 트리 노드의 렌더러 클래스
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class TreeCellRenderer extends DefaultTreeCellRenderer  implements CommonRootIF {


	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		
		setToolTipText(null); // no tool tip
		
		if (leaf ) {
			if (value instanceof AbstractFileTreeNode) {
				AbstractFileTreeNode node = (AbstractFileTreeNode) value;
				
				if (node.isDirectory()) {
					setIcon(getDefaultClosedIcon());
				} else {
					setIcon(getDefaultLeafIcon());
					long fileSize = node.getFileSize();
					
					StringBuilder toolTipTextBuilder = new  StringBuilder();
					DecimalFormat df = new DecimalFormat("#,##0.##");
					
					if (fileSize < 1024L) {
						// bytes
						toolTipTextBuilder.append(df.format(fileSize));
						toolTipTextBuilder.append(" Byte(s) ");
					} else if (fileSize < 1024L*1024L) {
						// Kbytes
						toolTipTextBuilder.append(df.format((double)fileSize/1024L));
						toolTipTextBuilder.append(" Kbyte(s) ");
					} else if (fileSize < 1024L*1024L*1024L) {
						// Mbytes
						toolTipTextBuilder.append(df.format((double)fileSize/(1024L*1024L)));
						toolTipTextBuilder.append(" Mbyte(s) ");
					} else {
						// Gbytes
						toolTipTextBuilder.append(df.format((double)fileSize/(1024L*1024L*1024L)));
						toolTipTextBuilder.append(" Gbyte(s) ");
					}
					
					toolTipTextBuilder.append(node.getFileName());
					
					setToolTipText(toolTipTextBuilder.toString());
				}
			} else {
				log.error("unkown FileTreeNode class, [{}]", value.getClass().getName());
				System.exit(1);
			}
		}

		return this;
	}
}
