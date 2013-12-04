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

package kr.pe.sinnori.common.lib;

//import java.util.logging.Level;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * client의 문자셋에 관련 encoder와 decoder를 쉽게 만들어 주는 클래스<br/>
 * 사용자가 지정한 문자셋으로 encoder, decoder를 생성한다. <br/>
 * 단 지정한것이 없을시에는 서버설정파일에서 지정한 문자셋을 따르며 <br/>
 * 그거 마자 실패시에는 UTF-8로 세팅된다.
 * 
 * @author Jonghoon Won
 */
public class CharsetUtil implements CommonRootIF {

	/**
	 * 사용자가 원하는 문자셋 이름으로 문자셋을 생성 반환한다. 단, 문자셋 지정 실패시 설정값에서 정하는 문자셋을 가지는 @see
	 * #CONFIG_CHARSET 로 설정된다.
	 * 
	 * @param charsetName
	 *            사용자가 원하는 문자셋
	 */
	public static Charset getCharset(String charsetName) throws IllegalArgumentException {
		if (charsetName == null || charsetName.equals("")) {
			String errorMessage = String.format("파라미터 값[%s]이 지정되지 않았습니다.", charsetName);
			throw new IllegalArgumentException(errorMessage);
		}

		Charset charset = null;
		try {
			charset = Charset.forName(charsetName);
		} catch (Exception e) {
			String errorMessage = String.format("잘못된 문자셋[%s]을 지정하였습니다.", charsetName);
			throw new IllegalArgumentException(errorMessage);
		}
		return charset;
	}
	
	/** 
	 * <pre>
	 * 주의사항) CharsetDecoder Not Thread Safe
	 * 참고) 신놀이 프레임 워크에서 네트워크에서 메시지 교환시 
	 * 문자열을 다룰때 문자셋 인코딩/디코딩시하는 중에 에러 발생시 
	 * 기본으로 설정된 문자로 대치를 해서 처리를 한다. 
	 * 이는 메시지안에 포함된 문자열 항목에 다소 문제가 있어도 메시지를 받게 하기 위함이다.
	 * 자바에서 문자셋을 다룰때 2가지 에러 상황에 대해서 사용자가 에러에 대한 동작을 지시 할 수 있다. 
	 * 첫번째 문자셋 규약을 어긴 경우 발생시 대처 방법을 지정하는 onMalformedInput 가 있다.
	 * 마지막 두번째 문자셋에 없는 문자를 발견한 경우 대처 방법을 지정하는 onUnmappableCharacter 가 있다.
	 * </pre>
	 * @param thisCharset 문자셋
	 * @return (1) 문자셋 규약을 어긴 경우 그리고 (2) 문자셋에 없는 문자가 있는 경우 기본으로 설정된 문자로 대치하는 디코더
	 */
	public static CharsetDecoder createCharsetDecoder(Charset thisCharset) {
		CharsetDecoder thisCharsetDecoder = thisCharset.newDecoder();
		
		thisCharsetDecoder
				.onMalformedInput(java.nio.charset.CodingErrorAction.REPLACE);
		thisCharsetDecoder
				.onUnmappableCharacter(java.nio.charset.CodingErrorAction.REPLACE);
		return thisCharsetDecoder;
	}

	/**
	 * <pre>
	 * 주의사항) CharsetEncoder Not Thread Safe
	 * 참고) 신놀이 프레임 워크에서 네트워크에서 메시지 교환시 
	 * 문자열을 다룰때 문자셋 인코딩/디코딩시하는 중에 에러 발생시 
	 * 기본으로 설정된 문자로 대치를 해서 처리를 한다. 
	 * 이는 메시지안에 포함된 문자열 항목에 다소 문제가 있어도 메시지를 받게 하기 위함이다.
	 * 자바에서 문자셋을 다룰때 2가지 에러 상황에 대해서 사용자가 에러에 대한 동작을 지시 할 수 있다. 
	 * 첫번째 문자셋 규약을 어긴 경우 발생시 대처 방법을 지정하는 onMalformedInput 가 있다.
	 * 마지막 두번째 문자셋에 없는 문자를 발견한 경우 대처 방법을 지정하는 onUnmappableCharacter 가 있다.
	 * </pre>
	 * @param thisCharset 문자셋
	 * @return (1) 문자셋 규약을 어긴 경우 그리고 (2) 문자셋에 없는 문자가 있는 경우 기본으로 설정된 문자로 대치하는 인코더
	 */
	public static CharsetEncoder createCharsetEncoder(Charset thisCharset) {
		CharsetEncoder thisCharsetEncoder = thisCharset.newEncoder();
		thisCharsetEncoder
				.onMalformedInput(java.nio.charset.CodingErrorAction.REPLACE);
		thisCharsetEncoder
				.onUnmappableCharacter(java.nio.charset.CodingErrorAction.REPLACE);
		return thisCharsetEncoder;
	}
}
