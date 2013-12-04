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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;



/**
 * 신놀이 동적 클래스 로더 클래스
 * 
 * @author Jonghoon Won
 * 
 */
public class SinnoriClassLoader extends ClassLoader implements CommonRootIF {
	private String classNameRegex;
	
	
	/**
	 * 생성자
	 * @param parent 부모 클래스 로더
	 * @param classNameRegex 동적 클래스 로딩 대상 클래스 이름 검사용 클래스 이름 정규식
	 */
	public SinnoriClassLoader(ClassLoader parent, String classNameRegex) {
		super(parent);
		this.classNameRegex = classNameRegex;
	}

	/**
	 * 지정된 클래스명와 클래스 파일로 부터 얻어진 클래스 객체를 반환한다.
	 * @param className 클래스명
	 * @param classFile 클래스 파일
	 * @return 지정된 클래스명와 클래스 파일로 부터 얻어진 클래스 객체
	 * @throws ClassNotFoundException 클래스를 발견하지 못했을때 던지는 예외, 상단 검사로 나올 수 없는 예외
	 */
	public Class<?> loadClass(String className, File classFile) throws ClassNotFoundException {
		
		
		/**
		 * <pre> 
		 * 상속 받은 loadClass 는 파라미터로 문자열만 받기 때문에, 
		 * 클래스명 대응 클래스 파일을 외부에서 지정하기 위한 편법으로 아래와 같이 코딩하였다.
		 * 
		 * </pre>
		 */
		if (null == className) {
			String errorMessage = "파라미터 클래스명이 널입니다.";
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}		
		
		
		Class<?> retClass = null;
		
		if (className.matches(classNameRegex)) {
			/** 서버 비지니스 로직 클래스 */
			try {
				long fileSize = classFile.length();

				
				if (fileSize > Integer.MAX_VALUE) {
					throw new ClassFormatError("over max size of file");
				}

				ByteBuffer fileBuffer = ByteBuffer.allocate((int) fileSize);
				// byte[] classData = new byte[(int)fileSize];
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(classFile);
					FileChannel fc = fis.getChannel();
					/*
					 * MappedByteBuffer m = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					 * fileSize); System.out.printf("m.hasArray=[%s]",
					 * m.hasArray()); System.out.println();
					 */

					fc.read(fileBuffer);
				} finally {
					if (null != fis)
						fis.close();
				}

				retClass = defineClass(className, fileBuffer.array(), 0,
						fileBuffer.capacity());

			} catch (IOException e) {
				// e.printStackTrace();
				String errorMessage = String.format("IOException::className=[%s]",
						className);
				log.warn(errorMessage, e);

				throw new ClassFormatError(errorMessage);

			} catch (ClassFormatError e) {
				String errorMessage = String.format(
						"ClassFormatError::className=[%s]", className);
				log.warn(errorMessage, e);

				throw new ClassFormatError(errorMessage);
			}
		} else {
			/** 서버 비지니스 로직외 클래스 */
			retClass = super.loadClass(className);
		}

		return retClass;
	}
}
