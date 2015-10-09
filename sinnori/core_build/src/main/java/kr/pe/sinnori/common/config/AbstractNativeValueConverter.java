package kr.pe.sinnori.common.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractNativeValueConverter<T> {	
	protected Logger log = LoggerFactory.getLogger(AbstractNativeValueConverter.class);
	
	public abstract T valueOf(String itemValue) throws IllegalArgumentException;
	
	
	private Class<T> genericType = null;
	
	public AbstractNativeValueConverter(Class<T> genericType) {
		if (null == genericType) {
			String errorMessage = new StringBuilder("parameter genericType is null")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		this.genericType = genericType;
	}
	
		
	public Class<T> getGenericType() {
		return genericType;
	}
}
