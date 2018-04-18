package kr.pe.sinnori.common.config.nativevalueconverter;

import java.io.File;

import kr.pe.sinnori.common.config.AbstractNativeValueConverter;

public class GeneralConverterReturningRegularFile extends
		AbstractNativeValueConverter<File> {

	private boolean isWritePermissionChecking;


	public GeneralConverterReturningRegularFile(
			boolean isWritePermissionChecking) {
		super(File.class);
		this.isWritePermissionChecking = isWritePermissionChecking;
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

		if (! returnValue.exists()) {
			String errorMessage = new StringBuilder(
					"the file that is the parameter itemValue[")
					.append(itemValue).append("] does not exist").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (!returnValue.isFile()) {
			String errorMessage = new StringBuilder(
					"the file that is the parameter itemValue[")
					.append(itemValue).append("] is not a regular file")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (!returnValue.canRead()) {
			String errorMessage = new StringBuilder(
					"the file(=the parameter itemValue[")
					.append(itemValue).append("]) doesn't hava permission to read").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (isWritePermissionChecking && !returnValue.canWrite()) {
			String errorMessage = new StringBuilder(
					"the file(=the parameter itemValue[")
					.append(itemValue).append("]) doesn't hava permission to write")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		return returnValue;
	}
}
