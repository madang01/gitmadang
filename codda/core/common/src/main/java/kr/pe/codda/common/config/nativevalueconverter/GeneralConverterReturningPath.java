package kr.pe.codda.common.config.nativevalueconverter;

import java.io.File;

import kr.pe.codda.common.config.AbstractNativeValueConverter;

public class GeneralConverterReturningPath extends AbstractNativeValueConverter<File> {	
	public GeneralConverterReturningPath() {
		super(File.class);
	}

	@Override
	public File valueOf(String itemValue) throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (itemValue.equals("")) {
			String errorMessage = "parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}
		
		File returnValue = new File(itemValue);
		
		if (!returnValue.exists()) {
			String errorMessage = new StringBuilder("the path(=the parameter itemValue[")
			.append(itemValue)
			.append("]) does not exist").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (!returnValue.isDirectory()) {
			String errorMessage = new StringBuilder("the path(=the parameter itemValue[")
			.append(itemValue)
			.append("]) is not a directory").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (!returnValue.canRead()) {
			String errorMessage = new StringBuilder("the path(=the parameter itemValue[")
			.append(itemValue)
			.append("]) doesn't hava permission to read").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		return returnValue;
	}
}
