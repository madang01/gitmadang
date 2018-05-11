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
package kr.pe.codda.common.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.protocol.MessageCodecIF;
import kr.pe.codda.common.util.CommonStaticUtil;

public class SimpleClassLoader extends ClassLoader implements ServerSimpleClassLoaderIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(SimpleClassLoader.class);

	// private final Object monitor = new Object();

	private String classloaderClassPathString = null;
	private String classloaderReousrcesPathString = null;
	private ServerSystemClassLoaderClassManagerIF serverSystemClassLoaderClassManager = null;
	
	private final static ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
	
	private ConcurrentHashMap<String, MessageCodecIF> messageCodecHash = new  ConcurrentHashMap<String, MessageCodecIF>();
	
	
	public SimpleClassLoader(String classloaderClassPathString, String classloaderReousrcesPathString,
			ServerSystemClassLoaderClassManagerIF serverSystemClassLoaderClassManager) {
		super(systemClassLoader);
		
		this.classloaderClassPathString = classloaderClassPathString;
		this.classloaderReousrcesPathString = classloaderReousrcesPathString;
		this.serverSystemClassLoaderClassManager = serverSystemClassLoaderClassManager;		

		log.info("SimpleClassLoader hashCode=[{}] create", this.hashCode());
	}

	/**
	 * 동적으로 로딩할 주어진 클래스 이름을 가지는 클래스 파일 경로를 반환한다.
	 * 
	 * @param classFullName
	 *            클래스 파일 경로를 얻고자 하는 클래스 이름
	 * @return 주어진 클래스 이름을 가지는 클래스 파일 경로
	 */
	public String getClassFileName(String classFullName) {
		String classFileName = new StringBuilder(classloaderClassPathString).append(File.separator)
				.append(classFullName.replace(".", File.separator)).append(".class").toString();
		return classFileName;
	}

	/**
	 * Warning! 효율을 위해서 이 메소드는 thread safe 를 지원하지 않는다. 하여 외부에서 이를 보장해야 한다.
	 */
	@Override
	public Class<?> loadClass(String classFullName) throws ClassNotFoundException {
		Class<?> retClass = null;
		// try {
		// synchronized (monitor) {
		retClass = findLoadedClass(classFullName);
		if (null == retClass) {

			if (-1 == classFullName.indexOf(CommonStaticFinalVars.BASE_DYNAMIC_CLASS_FULL_NAME)) {
				/** 서버 동적 클래스 비 대상 클래스 */
				return systemClassLoader.loadClass(classFullName);
			}

			if (serverSystemClassLoaderClassManager.isSystemClassLoader(classFullName)) {
				return systemClassLoader.loadClass(classFullName);
			}

			String classFileName = getClassFileName(classFullName);

			// log.info("classFileName={}", classFileName);

			File classFileObj = new File(classFileName);
			if (!classFileObj.exists()) {
				String errorMessage = new StringBuilder()
						.append("the class[")
						.append(classFullName)
						.append("] file[")
						.append(classFileName)
						.append("] doesn't exist").toString();

				log.warn(errorMessage);

				throw new ClassNotFoundException(errorMessage);
			}

			if (classFileObj.isDirectory()) {
				String errorMessage = new StringBuilder()
						.append("the class[")
						.append(classFullName)
						.append("] file[")
						.append(classFileName)
						.append("] is a directory").toString();
				log.warn(errorMessage);
				throw new ClassNotFoundException(errorMessage);
			}

			if (! classFileObj.canRead()) {
				String errorMessage = new StringBuilder()
						.append("it can't read the class[")
						.append(classFullName)
						.append("] file[")
						.append(classFileName)
						.append("]").toString();

				log.warn(errorMessage);
				throw new ClassNotFoundException(errorMessage);
			}

			retClass = loadClass(classFullName, classFileObj);
		} else {
			log.info("the class[{}] is already loaded", classFullName);
		}
		

		return retClass;
	}

	
	private Class<?> loadClass(String classFullName, File classFile) throws ClassNotFoundException {		
		Class<?> retClass = null;

		// try {

		/** 서버 비지니스 로직 클래스 */
		try {
			long fileSize = classFile.length();

			if (fileSize > Integer.MAX_VALUE) {
				throw new ClassFormatError("over max size of file");
			}
			
			byte[] dynamicClassfileBytes = Files.readAllBytes(classFile.toPath());			

			retClass = defineClass(classFullName, dynamicClassfileBytes, 0, dynamicClassfileBytes.length);

		} catch (IOException e) {
			String errorMessage = new StringBuilder()
					.append("fail to read the class[")
					.append(classFullName)
					.append("][")
					.append(classFile.getAbsolutePath())
					.append("] in this classloader[")
					.append(this.hashCode())
					.append("]").toString();
			
			log.warn(errorMessage, e);

			throw new ClassFormatError(errorMessage);

		} catch (ClassFormatError e) {
			String errorMessage = new StringBuilder()
					.append("fail to define the class[")
					.append(classFullName)
					.append("][")
					.append(classFile.getAbsolutePath())
					.append("] in this classloader[")
					.append(this.hashCode())
					.append("]").toString();
			
			log.warn(errorMessage, e);

			throw new ClassFormatError(errorMessage);
		}

		
		return retClass;
	}
	
	public InputStream getResourceAsStream(String name) {
		InputStream is = null;

		String realResourceFilePathString = CommonStaticUtil
				.getFilePathStringFromResourcePathAndRelativePathOfFile(classloaderReousrcesPathString, name);
		
		// log.info("realResourceFilePathString=[{}]", realResourceFilePathString);
		
		
		File realResourceFile = new File(realResourceFilePathString);

		if (realResourceFile.exists()) {
			try {
				is = new FileInputStream(realResourceFile);
			} catch (Exception e) {
				String errorMessage = new StringBuilder("fail to get a input stream of the resource[").append(name).append("][")
						.append(realResourceFilePathString).append("]")
						.toString();
				log.warn(errorMessage, e);
				return null;
			}
		} else {
			is = super.getResourceAsStream(name);
		}

		return is;
	}

	/*public URL getResource(String name) {
		URL url = null;

		String realResourceFilePathString = CommonStaticUtil
				.getFilePathStringFromResourcePathAndRelativePathOfFile(classloaderReousrcesPathString, name);
		File realResourceFile = new File(realResourceFilePathString);

		if (realResourceFile.exists()) {
			try {
				url = realResourceFile.toURI().toURL();
			} catch (Exception e) {
				log.warn(new StringBuilder("the resource[").append(name).append("] file[")
						.append(realResourceFilePathString).append("] fail to convert to url").toString(), e);
				return null;
			}
		} else {
			url = super.getResource(name);
		}

		return url;
	}*/
	
	public MessageCodecIF getMessageCodec(String messageID) throws DynamicClassCallException {
		MessageCodecIF messageCodec = messageCodecHash.get(messageID);
		
		if (null == messageCodec) {
			messageCodec = addNewMessageCodec(messageID);
		}		
		
		return messageCodec;
	}
	
	private MessageCodecIF addNewMessageCodec(String messageID) throws DynamicClassCallException {
		String classFullName = serverSystemClassLoaderClassManager.getServerMessageCodecClassFullName(messageID);
		
		Class<?> messageCodecClass = null;
		Object messageCodecInstance = null;
		try {
			messageCodecClass = loadClass(classFullName);
		} catch (ClassNotFoundException e) {
			String errorMessage = new StringBuilder("the parameter messageID[")
					.append(messageID)
					.append("]'s server message codec[")
					.append(classFullName)
					.append("] is not found").toString();
			
			log.warn(errorMessage, e);
			throw new DynamicClassCallException(errorMessage);
		} catch (Exception | Error e) {
			String errorMessage = new StringBuilder("fail to load the parameter messageID[")
					.append(messageID)
					.append("]'s server message codec[")
					.append(classFullName)
					.append("] class, errmsg=")
					.append(e.getMessage()).toString();
			
			log.warn(errorMessage, e);
			throw new DynamicClassCallException(errorMessage);
		}
		
		try {
			messageCodecInstance = messageCodecClass.getDeclaredConstructor().newInstance();
		} catch (Exception | Error e) {
			String errorMessage = new StringBuilder("fail to create a new instance of the parameter messageID[")
					.append(messageID)
					.append("]'s server message codec[")
					.append(classFullName)
					.append("] class").toString();
			
			log.warn(errorMessage, e);
			throw new DynamicClassCallException(errorMessage);
		}
		
		if (! (messageCodecInstance instanceof MessageCodecIF)) {
			String errorMessage = new StringBuilder("the new instance[")
					.append(classFullName)
					.append("] is not a server message codec class").toString();
			
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		}
		
		MessageCodecIF messageCodec = (MessageCodecIF)messageCodecInstance;
		messageCodecHash.put(messageID, messageCodec);
		
		return messageCodec;
	}
	
	

	@Override
	protected void finalize() throws Throwable {
		// FIXME! 메모리 회수 확인용으로 삭제하지 마세요!
		log.info("SimpleClassLoader[{}] destroy", this.hashCode());
	}
}
