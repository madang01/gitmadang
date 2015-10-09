package kr.pe.sinnori.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SequencedPropertiesUtil {
	private static Logger log = LoggerFactory.getLogger(SequencedPropertiesUtil.class);
	
	
	public static SequencedProperties getSequencedPropertiesFromFile(
			String sourcePropertiesFilePathString) throws FileNotFoundException,  IOException {
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(sourcePropertiesFilePathString);
			sourceSequencedProperties.load(fis);
		} catch (FileNotFoundException e) {
			String errorMessage = String.format("the source properties file(=the parameter sourcePropertiesFilePathString[%s]) is not found",
					sourcePropertiesFilePathString);
			throw new FileNotFoundException(errorMessage);
		} catch (IOException e) {
			String errorMessage = String.format("fail to load the source properties file(=the parameter sourcePropertiesFilePathString[%s]), errormessage=%s",
					sourcePropertiesFilePathString, e.getMessage());
			throw new IOException(errorMessage);
		} finally {
			if (null != fis) {
				try {
					fis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return sourceSequencedProperties;
	}

	public static void saveSequencedPropertiesToFile(
			SequencedProperties sourceProperties, String sourcePropertiesTitle,
			String sourcePropertiesFilePathString,
			Charset sourcePropertiesFileCharset) throws FileNotFoundException,  IOException {
		

		File sourcePropertiesFile = new File(sourcePropertiesFilePathString);
		
		if (!sourcePropertiesFile.exists()) {
			try {
				boolean isSuccess = sourcePropertiesFile.createNewFile();
				if (! isSuccess) {
					String errorMessage = String.format("fail to create the new source properties file(=the parameter sourcePropertiesFilePathString[%s])",
							sourcePropertiesFilePathString);
					throw new IOException(errorMessage);
				}
			} catch (IOException e) {
				String errorMessage = String.format("fail to create the new source properties file(=the parameter sourcePropertiesFilePathString[%s])",
						sourcePropertiesFilePathString);
				
				log.warn(errorMessage, e);
				throw new IOException(errorMessage);
			}
			
		}

		if (!sourcePropertiesFile.isFile()) {
			String errorMessage = String.format("the source properties file(=the parameter sourcePropertiesFilePathString[%s]) is not a regular file",
					sourcePropertiesFilePathString);
			throw new IOException(errorMessage);
		}

		if (!sourcePropertiesFile.canWrite()) {
			String errorMessage = String.format("the source properties file(=the parameter sourcePropertiesFilePathString[%s]) doesn't hava permission to write",
					sourcePropertiesFilePathString);
			throw new IOException(errorMessage);
		}

		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		try {
			fos = new FileOutputStream(sourcePropertiesFile);
			osw = new OutputStreamWriter(fos, sourcePropertiesFileCharset);
			sourceProperties.store(osw, sourcePropertiesTitle);
		} catch (FileNotFoundException e) {
			/** expected dead code */
			String errorMessage = String.format("the source properties file(=the parameter sourcePropertiesFilePathString[%s]) doesn't exist",
					sourcePropertiesFilePathString);
			
			log.warn(errorMessage, e);			
			throw new FileNotFoundException(errorMessage);
		} catch (IOException e) {
			String errorMessage = String.format("fail to write the source properties file(=the parameter sourcePropertiesFilePathString[%s])",
					sourcePropertiesFilePathString);
			log.warn(errorMessage, e);			
			throw new IOException(errorMessage);
		} finally {
			if (osw != null) {
				try {
					osw.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		}
	}
}
