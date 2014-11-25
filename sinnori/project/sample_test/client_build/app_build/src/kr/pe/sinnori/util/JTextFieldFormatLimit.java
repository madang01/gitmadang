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


package kr.pe.sinnori.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import kr.pe.sinnori.common.lib.CommonRootIF;

/**
 * 길이와 원하는 정규식을 갖는 문자열만 입력 받도록 하는 입력 박스 콤포넌트.
 * 
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class JTextFieldFormatLimit extends PlainDocument implements CommonRootIF {
	private int limit;
	private String regex = null;

	/**
	 * 생성자
	 * @param limit 입력 가능한 문자열 최대 길이
	 * @param regex 입력값을 제한하고자 하는 정규식 표현 문자열
	 */
	public JTextFieldFormatLimit(int limit, String regex) {
		super();
		this.limit = limit;
		this.regex = regex;
	}

	public void insertString(int offset, String newStr, AttributeSet attr)
			throws BadLocationException {
		if (newStr == null) return;

		int lenOfOldStr = getLength();
		if ((lenOfOldStr + newStr.length()) <= limit) {
			if (null == regex) {
				super.insertString(offset, newStr, attr);
			} else {
				String oldStr = getText(0, lenOfOldStr);
				StringBuilder finalStrBuilder = new StringBuilder(oldStr);
				finalStrBuilder.append(newStr);
				if (finalStrBuilder.toString().matches(regex)) {
					super.insertString(offset, newStr, attr);
				}
			}
		}
	}
}