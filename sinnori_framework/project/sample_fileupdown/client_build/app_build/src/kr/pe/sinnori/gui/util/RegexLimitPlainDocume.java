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
package kr.pe.sinnori.gui.util;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * 문자 갯수 혹은 특정 문자셋에서의 길이와 원하는 정규식을 갖는 문자열만 입력 받도록 하는 입력 박스 콤포넌트.
 * 
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class RegexLimitPlainDocume extends PlainDocument {
	private Charset charset = null;
	private int limit=10;
	private String regex = null;

	/**
	 * 생성자
	 * @param charsetName 문자셋,  문자셋이 null 이면 파라미터 {@link #limit}} 는 문자 갯수를 뜻하며 문자셋이 null 이 아니면 파라미터 {@link #limit}} 는 문자셋으로 변환시 바이트 크기를 뜻한다. 
	 * @param limit 크기
	 * @param regex 정규식, 만약 null 이면 정규식 미 적용하며 null 이 아니면 정규식 적용한다. 
	 */
	public RegexLimitPlainDocume(String charsetName, int limit, String regex) {
		super();
		if (limit < 0) {
			IllegalArgumentException e = new IllegalArgumentException("parameter limit is less than zero");
			e.printStackTrace();
			throw e;
		}
		if (null != charsetName) {
			try {
				this.charset = Charset.forName(charsetName);
			} catch (IllegalCharsetNameException e) {
				e.printStackTrace();
				throw e;
			} catch (UnsupportedCharsetException e) {
				e.printStackTrace();
				throw e;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				throw e;			
			}
		}
		this.limit = limit;
		this.regex = regex;
	}
	

	@Override
	public void insertString(int offset, String newAppendStr, AttributeSet attr)
			throws BadLocationException {
		if (newAppendStr == null) return;
		
		
		int lenOfOldStr = getLength();
		String oldStr = getText(0, lenOfOldStr);
		String newStr = new StringBuilder(oldStr).append(newAppendStr).toString();
		
		if (null == charset) {
			if (newStr.length() <= limit) {
				if (null == regex) {
					super.insertString(offset, newAppendStr, attr);
				} else {
					if (newStr.matches(regex)) {
						super.insertString(offset, newAppendStr, attr);
					}
				}
			}
		} else {
			if (newStr.getBytes(charset).length <= limit) {
				if (null == regex) {
					super.insertString(offset, newAppendStr, attr);
				} else {
					if (newStr.toString().matches(regex)) {
						super.insertString(offset, newAppendStr, attr);
					}
				}
			}
		}
	}
}