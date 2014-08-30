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
package kr.pe.sinnori.impl.message.FileListResult;

import kr.pe.sinnori.common.message.AbstractMessage;
/**
 * FileListResult 메시지
 * @author Jonghoon Won
 *
 */
public final class FileListResult extends AbstractMessage {
	private String requestDirectory;
	private String pathSeperator;
	private String taskResult;
	private String resultMessage;
	private int cntOfDriver;
	public class DriverList {
		private String driverName;

		public String getDriverName() {
			return driverName;
		}

		public void setDriverName(String driverName) {
			this.driverName = driverName;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("DriverList[");
			builder.append("driverName=");
			builder.append(driverName);
			builder.append("]");
			return builder.toString();
		}
	};
	private DriverList[] driverListList;
	private int cntOfFile;
	public class FileList {
		private String fileName;
		private long fileSize;
		private byte fileType;

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public long getFileSize() {
			return fileSize;
		}

		public void setFileSize(long fileSize) {
			this.fileSize = fileSize;
		}
		public byte getFileType() {
			return fileType;
		}

		public void setFileType(byte fileType) {
			this.fileType = fileType;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("FileList[");
			builder.append("fileName=");
			builder.append(fileName);
			builder.append(", fileSize=");
			builder.append(fileSize);
			builder.append(", fileType=");
			builder.append(fileType);
			builder.append("]");
			return builder.toString();
		}
	};
	private FileList[] fileListList;

	public String getRequestDirectory() {
		return requestDirectory;
	}

	public void setRequestDirectory(String requestDirectory) {
		this.requestDirectory = requestDirectory;
	}
	public String getPathSeperator() {
		return pathSeperator;
	}

	public void setPathSeperator(String pathSeperator) {
		this.pathSeperator = pathSeperator;
	}
	public String getTaskResult() {
		return taskResult;
	}

	public void setTaskResult(String taskResult) {
		this.taskResult = taskResult;
	}
	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
	public int getCntOfDriver() {
		return cntOfDriver;
	}

	public void setCntOfDriver(int cntOfDriver) {
		this.cntOfDriver = cntOfDriver;
	}

	public DriverList[] getDriverListList() {
		return driverListList;
	}

	public void setDriverListList(DriverList[] driverListList) {
		this.driverListList = driverListList;
	}
	public int getCntOfFile() {
		return cntOfFile;
	}

	public void setCntOfFile(int cntOfFile) {
		this.cntOfFile = cntOfFile;
	}

	public FileList[] getFileListList() {
		return fileListList;
	}

	public void setFileListList(FileList[] fileListList) {
		this.fileListList = fileListList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("class FileListResult[");
		builder.append("requestDirectory=");
		builder.append(requestDirectory);
		builder.append(", pathSeperator=");
		builder.append(pathSeperator);
		builder.append(", taskResult=");
		builder.append(taskResult);
		builder.append(", resultMessage=");
		builder.append(resultMessage);
		builder.append(", cntOfDriver=");
		builder.append(cntOfDriver);
		builder.append(", driverListList=");
		if (null == driverListList) {
			builder.append("null");
		} else {
			if (0 == driverListList.length) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < driverListList.length; i++) {
					DriverList driverList = driverListList[i];
					if (0 == i) {
						builder.append("driverList[");
					} else {
						builder.append(", driverList[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(driverList.toString());
				}
				builder.append("]");
			}
		}

		builder.append(", cntOfFile=");
		builder.append(cntOfFile);
		builder.append(", fileListList=");
		if (null == fileListList) {
			builder.append("null");
		} else {
			if (0 == fileListList.length) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < fileListList.length; i++) {
					FileList fileList = fileListList[i];
					if (0 == i) {
						builder.append("fileList[");
					} else {
						builder.append(", fileList[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(fileList.toString());
				}
				builder.append("]");
			}
		}

		builder.append(", messageHeaderInfo=");
		builder.append(messageHeaderInfo.toString());
		builder.append("]");
		return builder.toString();
	}
}