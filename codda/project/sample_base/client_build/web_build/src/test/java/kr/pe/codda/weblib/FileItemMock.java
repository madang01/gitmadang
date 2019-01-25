package kr.pe.codda.weblib;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;

import kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.io.FileUtils;

public class FileItemMock implements FileItem {	
	private static final long serialVersionUID = -9136132597604217176L;
	
	private InternalLogger log = InternalLoggerFactory.getInstance(FileItemMock.class);
	
	private File temporaryUploadFile = null;
	private String uploadFileName = null;
	private String fieldName = null;
	private long uploadFileSize = 0L;
	private String contentTypeOfUploadFile = null;
	private byte[] contentsOfUploadFile = null;	
	
	public FileItemMock(File uploadSourceFile, String fieldName) throws InterruptedException, IOException {
		this.uploadFileName = uploadSourceFile.getName();
		this.uploadFileSize = uploadSourceFile.length();
		this.fieldName = fieldName;
		
		CoddaConfiguration runningProjectConfiguration = CoddaConfigurationManager
				.getInstance().getRunningProjectConfiguration();
		String mainProjectName = runningProjectConfiguration
				.getMainProjectName();
		String  installedPathString = runningProjectConfiguration
				.getInstalledPathString();
		
		String userWebTempPathString = WebRootBuildSystemPathSupporter
				.getUserWebTempPathString(installedPathString, mainProjectName);					
		
		while (null == temporaryUploadFile) {
			String temporaryUploadFilePathString = new StringBuilder()
			.append(userWebTempPathString)
			.append(File.separator)
			.append("uploadTemporaryFile")
			.append(new java.util.Date().getTime())
			.append(".tmp").toString();
			
			temporaryUploadFile = new File(temporaryUploadFilePathString);
			
			if (temporaryUploadFile.exists()) {
				temporaryUploadFile = null;
				
				Thread.sleep(100);
			}
		}
		
		try {
			FileUtils.copyFile(uploadSourceFile, temporaryUploadFile);
		} catch(IOException e) {
			log.warn("입출력 에러로 '첨부 파일 원본 파일'을 '임시 첨부 파일'로 복사 실패, errmsg=", e.getMessage());
			throw e;
		}
		
		// uploadTemporaryFile.deleteOnExit();
		contentsOfUploadFile = FileUtils.readFileToByteArray(temporaryUploadFile);
		
		InputStream attachedFileInputStream = getInputStream();
		try {
			contentTypeOfUploadFile = URLConnection
					.guessContentTypeFromStream(attachedFileInputStream);
			if (null == contentTypeOfUploadFile) {
				throw new IllegalArgumentException("the upload file's content type is unknown");
			}

		} finally {
			try {
				attachedFileInputStream.close();
			} catch (IOException e) {
			}
		}
	}
	
	@Override
	public boolean isFormField() {
		return false;
	}
	
	@Override
	public void delete() {
		if (temporaryUploadFile.exists()) {
			boolean result = temporaryUploadFile.delete();
			if (! result) {
				log.warn("fail to delete the upload temporary file[{}]", temporaryUploadFile.getAbsolutePath());
			}
		}
	}
	
	@Override
	public String getContentType() {
		return contentTypeOfUploadFile;
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(contentsOfUploadFile);
	}
	
	@Override
	public String getName() {
		return uploadFileName;
	}
	@Override
	public long getSize() {
		return uploadFileSize;
	}
	
	@Override
	public void write(File destFile) throws Exception {
		FileUtils.moveFile(temporaryUploadFile, destFile);
	}
	
	@Override
	public String getFieldName() {
		return fieldName;
	}

	@Override
	public FileItemHeaders getHeaders() {
		return null;
	}

	@Override
	public void setHeaders(FileItemHeaders arg0) {					
	}

	@Override
	public byte[] get() {
		return null;
	}

					

	@Override
	public OutputStream getOutputStream() throws IOException {
		return null;
	}

	@Override
	public String getString() {
		return null;
	}

	@Override
	public String getString(String arg0)
			throws UnsupportedEncodingException {
		return null;
	}

	@Override
	public boolean isInMemory() {
		return false;
	}

	@Override
	public void setFieldName(String arg0) {
	}

	@Override
	public void setFormField(boolean arg0) {					
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FileItemMock [uploadFileName=");
		builder.append(uploadFileName);
		builder.append(", fieldName=");
		builder.append(fieldName);
		builder.append(", uploadFileSize=");
		builder.append(uploadFileSize);
		builder.append(", contentTypeOfUploadFile=");
		builder.append(contentTypeOfUploadFile);
		builder.append("]");
		return builder.toString();
	}
}

