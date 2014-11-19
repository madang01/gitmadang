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
package kr.pe.sinnori.server.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Hashtable;

import kr.pe.sinnori.common.lib.CommonRootIF;



/**
 * 서버용 동적 클래스들 로딩및 관리를 담당하는 "동적 클래스 로더". @{link ServerObjectManager } 에 종속 된다.
 * 
 * @author Won Jonghoon
 * 
 */
public class ServerClassLoader extends ClassLoader implements CommonRootIF {
	private final Object monitor = new  Object();
	// private ClassLoader parent = null;
	private final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
	// private final static String mybatisPackageName = "kr.pe.sinnori.impl.mybatis.";
	
	
	
	private String appInfBasePath = null;
	private String classLoaderClassPackagePrefixName = null;
	
	private String classPath = null;
	//private String libraryPath = null;
	private String resourcesPath = null;
	
	private Hashtable<String, JarClassInfo> jarClassInfoHash = null;
	
	/**
	 * 생성자
	 * @param parent 부모 클래스 로더
	 * @param classNameRegex 동적 클래스 로딩 대상 클래스 이름 검사용 클래스 이름 정규식
	 */
	public ServerClassLoader(ClassLoader parent, String appInfBasePath, String classLoaderClassPackagePrefixName, Hashtable<String, JarClassInfo> jarClassInfoHash) {
		super(parent);
		
		// this.parent = parent;
		this.appInfBasePath = appInfBasePath;
		this.classLoaderClassPackagePrefixName = classLoaderClassPackagePrefixName;		
		this.jarClassInfoHash = jarClassInfoHash;
		
		classPath = this.appInfBasePath + File.separator + "classes";
		// libraryPath = this.appInfBasePath + File.separator + "lib";
		resourcesPath = this.appInfBasePath + File.separator + "resources";
	
		// Throwable t = new Throwable();
		log.info("SinnoriClassLoader hashCode=[{}] create", this.hashCode());
	}
	
	/**
	 * 동적으로 로딩할 주어진 클래스 이름을 가지는 클래스 파일 경로를 반환한다. 
	 * @param classFullName 클래스 파일 경로를 얻고자 하는 클래스 이름
	 * @return 주어진 클래스 이름을 가지는 클래스 파일 경로
	 */
	public String getClassFileName(String classFullName) {
		String classFileName = new StringBuilder(classPath).append(File.separator).append(classFullName.replace(".", File.separator)).append(".class").toString();
		return classFileName;
	}
	
	/**
	 * 직접적으로 Class<?> loadClass(String classFullName) 를 호출 지향할것.
	 */
	@Override
	public Class<?> loadClass(String classFullName) throws ClassNotFoundException {
		// log.info("SinnoriClassLoader hashCode=[{}], classFullName=[{}]::call loadClass(String)", this.hashCode(), classFullName);

		// log.info("SinnoriClassLoader classLoaderClassPackagePrefixName=[{}]", classLoaderClassPackagePrefixName);
		
		Class<?> retClass = null;
		// try {
			synchronized(monitor) {
				retClass = findLoadedClass(classFullName);
				if (null == retClass) {
					
					
					if (-1 == classFullName.indexOf(classLoaderClassPackagePrefixName)) {
						JarClassInfo jarClassInfo = jarClassInfoHash.get(classFullName);
						
						if (null == jarClassInfo) {
							/** 서버 동적 클래스 비 대상 클래스 */							
							return systemClassLoader.loadClass(classFullName);
						}
						
						/** class in jar libray */
						byte[] classFileBuffer = jarClassInfo.getClassFileBuffer();
						retClass = defineClass(classFullName, classFileBuffer, 0,
								classFileBuffer.length);
						
						return retClass;
					}
	
						
					
					
					if (classFullName.startsWith("kr.pe.sinnori.impl.message.SelfExn.")) {
						/** 시스템 클래스 */
						return systemClassLoader.loadClass(classFullName);
					}
					
					// log.info("SinnoriClassLoader hashCode=[{}], messageID=[{}], classFullName=[{}]::주어진 클래스명은 서버 동적 클래스 대상", this.hashCode(), messageID, classFullName);
					
					// log.info("classFullName 파일 경로 변환 문자열={}", classFullName.replace(".", File.separator));
					
						
					String classFileName = getClassFileName(classFullName);
					
					// log.info("classFileName={}", classFileName);
					
					File classFileObj = new File(classFileName);
					if (!classFileObj.exists()) {
						String errorMessage = String.format("SinnoriClassLoader hashCode=[%d], classFullName=[%s]" +
								"::클래스 파일[%s]이 존재하지 않습니다.", 
								this.hashCode(),classFullName, classFileName);
						
						log.warn(errorMessage);
						
						throw new ClassNotFoundException(errorMessage);
					}
					
					if (classFileObj.isDirectory()) {
						//String errorMessage = String.format("클래스 파일[%s]이 일반 파일이 아닌 경로입니다.", classFileName);
						String errorMessage = String.format("SinnoriClassLoader hashCode=[%d], classFullName=[%s]" +
								"::클래스 파일[%s]이 일반 파일이 아닌 경로입니다.", 
								this.hashCode(), classFullName, classFileName);
						log.warn(errorMessage);
						throw new ClassNotFoundException(errorMessage);
					}
					
					if (!classFileObj.canRead()) {
						// String errorMessage = String.format("클래스 파일[%s]이 일반 파일이 아닌 경로입니다.", classFileName);
						String errorMessage = String.format("SinnoriClassLoader hashCode=[%d], classFullName=[%s]" +
								"::클래스 파일[%s]을 읽을 수 없습니다.", 
								this.hashCode(), classFullName, classFileName);
						
						log.warn(errorMessage);
						throw new ClassNotFoundException(errorMessage);
					}
					
					retClass = loadClass(classFullName, classFileObj);
				} else {
					log.info("retClass[{}] is not null", classFullName);
				}
			}
		/*} finally {
			log.info("SinnoriClassLoader hashCode=[{}], classFullName=[{}]::end loadClass(String)", this.hashCode(), classFullName);
		}*/
		
		return retClass;
	}
	
	/**
	 * 지정된 클래스명와 클래스 파일로 부터 얻어진 클래스 객체를 반환한다.
	 * @param className 클래스명
	 * @param classFile 클래스 파일
	 * @return 지정된 클래스명와 클래스 파일로 부터 얻어진 클래스 객체
	 * @throws ClassNotFoundException 클래스를 발견하지 못했을때 던지는 예외, 상단 검사로 나올 수 없는 예외
	 */
	private Class<?> loadClass(String classFullName, File classFile) throws ClassNotFoundException {
		// log.info("SinnoriClassLoader hashCode=[{}], messageID=[{}], classFullName=[{}], classFile=[{}]::call loadClass(String, File)", this.hashCode(), messageID, classFullName, classFile.getAbsolutePath());
		
		/**
		 * <pre> 
		 * 상속 받은 loadClass 는 파라미터로 문자열만 받기 때문에, 
		 * 클래스명 대응 클래스 파일을 외부에서 지정하기 위한 편법으로 아래와 같이 코딩하였다.
		 * 
		 * </pre>
		 */
		/*if (null == classFullName) {
			String errorMessage = "파라미터 클래스명이 널입니다.";
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}		*/
		
		Class<?> retClass = null;
		
		// try {
			
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
					

					fc.read(fileBuffer);
				} finally {
					if (null != fis)
						fis.close();
				}

				retClass = defineClass(classFullName, fileBuffer.array(), 0,
						fileBuffer.capacity());

				
				/*Message2ClassFileInfo messageClassGroupInfo = messageID2InfoHash.get(messageID);
				if (null == messageClassGroupInfo) {
					messageClassGroupInfo = new Message2ClassFileInfo(messageID);
				}
				
				messageClassGroupInfo.addClassFile(classFullName, classFile);*/
				
			} catch (IOException e) {
				String errorMessage = String.format("SinnoriClassLoader hashCode=[%d], classFullName=[%s], classFile=[{}]" +
						"::IOException %s", 
						this.hashCode(), classFullName, classFile.getAbsolutePath(), e.getMessage());
				
				log.warn(errorMessage, e);

				throw new ClassFormatError(errorMessage);

			} catch (ClassFormatError e) {
				String errorMessage = String.format("SinnoriClassLoader hashCode=[%d], classFullName=[%s], classFile=[{}]" +
						"::ClassFormatError %s", 
						this.hashCode(), classFullName, classFile.getAbsolutePath(), e.getMessage());
				
				log.warn(errorMessage, e);

				throw new ClassFormatError(errorMessage);
			}
			
		/*} finally {
			log.info("SinnoriClassLoader hashCode=[{}], messageID=[{}], classFullName=[{}], classFile=[{}]::end loadClass(String, File)", this.hashCode(), messageID, classFullName, classFile.getAbsolutePath());
		}*/

		return retClass;
	}
	
	/**
	 * <pre>
	 * kr/pe/sinnori/impl/mybatis/memberMapper.xml 로 시작되는 mybatis 리소스 파일의 InputStream 을 반환한다.
	 * </pre>
	 */
	@Override
	public InputStream getResourceAsStream(String name) {
		FileInputStream fis = null;
		
		/*int inx = name.indexOf(mybatisPackageName);
		if (0 != inx) {
			log.info("not mybatis resource, name={}", name);
			
			return super.getResourceAsStream(name);
		}*/

		/*String fileName = name.substring(mybatisPackageName.length());
		
		String resourceFileName = new StringBuilder(dynamicClassBinaryBasePath).append(File.separator).append(mybatisPackageName.replace("/", File.separator)).append(fileName).toString();*/
		
		String resourceFileName = new StringBuilder(resourcesPath).append(File.separator).append(name.replace("/", File.separator)).toString();
		
		// FIXME!
		// log.info("name={}, packageName={}, fileName={}", name, mybatisPackageName, fileName);
		log.info("name={}, resourceFileName={}", name, resourceFileName);
		
		try {
			fis = new FileInputStream(resourceFileName);
		} catch (FileNotFoundException e) {
			return null;
		}
		return fis;
	}
	
	@Override
    protected void finalize() throws Throwable{
		// FIXME! 메모리 회수 확인용으로 삭제하지 마세요!
		log.info("서버 클래스 로더[{}] 소멸", this.hashCode());
    }
}

