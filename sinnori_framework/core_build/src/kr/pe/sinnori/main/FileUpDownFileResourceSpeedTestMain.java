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

package kr.pe.sinnori.main;

import java.io.File;
import java.io.IOException;

import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResource;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResourceManager;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResource;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResourceManager;

/**
 * {@link kr.pe.sinnori.common.updownfile.LocalTargetFileResource } 와
 * {@link kr.pe.sinnori.common.updownfile.LocalSourceFileResource} 2개 속도 테스트
 * 
 * @author Jonghoon won
 * 
 */
public class FileUpDownFileResourceSpeedTestMain implements CommonRootIF {
	private String testSourceFilePath = null;
	private String testSourceFileName = null;
	private long testSourceFileSize = 0L;
	
	private String testTargetFilePath = null;
	private String testTargetFileName = null;
	private long testTargetFileSize = 0L;

	public FileUpDownFileResourceSpeedTestMain(String testSourceFileFullName, String testTargetFileFullName) {
		File sourceFileObj = new File(testSourceFileFullName);
		if (!sourceFileObj.exists()) {
			log.fatal(String.format("testSourceFileFullName[%s] not exist",
					testSourceFileFullName));
			System.exit(1);
		}

		if (!sourceFileObj.isFile()) {
			log.fatal(String.format(
					"testSourceFileFullName[%s] is not a normal file",
					testSourceFileFullName));
			System.exit(1);
		}
		
		testSourceFileSize = sourceFileObj.length();

		File canonicalSourceFileObj = null;
		try {
			canonicalSourceFileObj = sourceFileObj.getCanonicalFile();
		} catch (IOException e) {
			log.fatal(String.format(
					"IOException::File.getCanonicalFile::file[%s]",
					testSourceFileFullName), e);
			System.exit(1);
		}

		File parentSourceFileObj = canonicalSourceFileObj.getParentFile();
		testSourceFilePath = parentSourceFileObj.getAbsolutePath();
		testSourceFileName = canonicalSourceFileObj.getName();
		
		
		File targetFileObj = new File(testTargetFileFullName);
		if (!targetFileObj.exists()) {
			log.fatal(String.format("testTargetFileFullName[%s] not exist",
					testTargetFileFullName));
			System.exit(1);
		}

		if (!targetFileObj.isFile()) {
			log.fatal(String.format(
					"testFileFullName[%s] is not a normal file",
					testTargetFileFullName));
			System.exit(1);
		}
		
		File canonicaltargetFileObj = null;
		try {
			canonicaltargetFileObj = targetFileObj.getCanonicalFile();
		} catch (IOException e) {
			log.fatal(String.format(
					"IOException::File.getCanonicalFile::file[%s]",
					testSourceFileFullName), e);
			System.exit(1);
		}
		
		File parentTargetFileObj = canonicaltargetFileObj.getParentFile();
		testTargetFilePath = parentTargetFileObj.getAbsolutePath();
		testTargetFileName = canonicaltargetFileObj.getName();
		testTargetFileSize = parentTargetFileObj.length();

		log.info(String.format("testSourceFilePath[%s], testSourceFileName=[%s]",
				testSourceFilePath, testSourceFileName));
		
		log.info(String.format("testTargetFilePath[%s], testTargetFileName=[%s]",
				testTargetFilePath, testTargetFileName));
	}

	public void testReadFileSpeed() {
		LocalSourceFileResourceManager localSourceFileResourceManager = LocalSourceFileResourceManager
				.getInstance();

		LocalSourceFileResource localSourceFileResource = null;

		long startTime = 0L;
		long endTime = 0L;
		try {
			localSourceFileResource = localSourceFileResourceManager
					.pollLocalSourceFileResource(false, testSourceFilePath, testSourceFileName,
							testSourceFileSize, testTargetFilePath, testTargetFileName, testTargetFileSize,
							getfileBlockSize());
			
			startTime = System.currentTimeMillis();
			
			int startFileBlockNo = 0;
			int endFileBlockNo = localSourceFileResource.getEndFileBlockNo();
			
			for (; startFileBlockNo <= endFileBlockNo; startFileBlockNo++) {
				byte fileData[] = localSourceFileResource
						.getByteArrayOfFileBlockNo(startFileBlockNo);
				localSourceFileResource.readSourceFileData(startFileBlockNo,
						fileData, true);
			}
			
		} catch (IllegalArgumentException e) {
			log.fatal(String.format("source file[%s%s%s], %s", testSourceFilePath,
					File.separator, testSourceFileName, e.getMessage()), e);
			System.exit(1);
		} catch (UpDownFileException e) {
			log.fatal(String.format("source file[%s%s%s], %s", testSourceFilePath,
					File.separator, testSourceFileName, e.getMessage()), e);
			System.exit(1);
		} finally {
			endTime = System.currentTimeMillis();
			log.info(String.format("elapsed=[%d ms]", (endTime - startTime)));
			
			localSourceFileResourceManager.putLocalSourceFileResource(localSourceFileResource);
		}
	}
	
	public void testWriteFileSpeed() {
		LocalSourceFileResourceManager localSourceFileResourceManager = LocalSourceFileResourceManager
				.getInstance();

		LocalSourceFileResource localSourceFileResource = null;
		
		LocalTargetFileResourceManager localTargetFileResourceManager = LocalTargetFileResourceManager.getInstance();
		
		LocalTargetFileResource localTargetFileResource = null;
		long startTime = 0L;
		long endTime = 0L;
		
		try {
			localSourceFileResource = localSourceFileResourceManager
					.pollLocalSourceFileResource(false, testSourceFilePath, testSourceFileName,
							testSourceFileSize, testTargetFilePath, testTargetFileName, testTargetFileSize,
							getfileBlockSize());
			
			localTargetFileResource = localTargetFileResourceManager.pollLocalTargetFileResource(false, testSourceFilePath, testSourceFileName,
							testSourceFileSize, testTargetFilePath, testTargetFileName, testTargetFileSize, 
							getfileBlockSize());
			
			startTime = System.currentTimeMillis();
			
			int startFileBlockNo = 0;
			int endFileBlockNo = localSourceFileResource.getEndFileBlockNo();
			
			for (; startFileBlockNo <= endFileBlockNo; startFileBlockNo++) {
				byte fileData[] = localSourceFileResource
						.getByteArrayOfFileBlockNo(startFileBlockNo);
				localSourceFileResource.readSourceFileData(startFileBlockNo, fileData, true);
				
				localTargetFileResource.writeTargetFileData(startFileBlockNo, fileData, true);
			}
			
		} catch (IllegalArgumentException e) {
			log.fatal(String.format("target file[%s%s%s], %s", testTargetFilePath,
					File.separator, testTargetFileName, e.getMessage()), e);
			System.exit(1);
		} catch (UpDownFileException e) {
			log.fatal(String.format("target file[%s%s%s], %s", testTargetFilePath,
					File.separator, testTargetFileName, e.getMessage()), e);
			System.exit(1);
		} finally {
			endTime = System.currentTimeMillis();
			log.info(String.format("elapsed=[%d ms]", (endTime - startTime)));
			
			localSourceFileResourceManager.putLocalSourceFileResource(localSourceFileResource);
			localTargetFileResourceManager.putLocalTargetFileResource(localTargetFileResource);
		}
	}

	public int getfileBlockSize() {
		return (1024 * 30);
	}

	public static void main(String[] args) throws InterruptedException {
		FileUpDownFileResourceSpeedTestMain testObj = new FileUpDownFileResourceSpeedTestMain(
				"/home/madang01/temp3/집에서 나홀로.wmv", "/home/madang01/temp3/집에서 나홀로 (사본).wmv");
		//testObj.testReadFileSpeed();
		testObj.testWriteFileSpeed();
	}
}
