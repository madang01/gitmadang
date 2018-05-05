package kr.pe.codda.common.config;


import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public abstract class AbstractNativeValueConverter<T> {	
	protected InternalLogger log = InternalLoggerFactory.getInstance(AbstractNativeValueConverter.class);
	
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
