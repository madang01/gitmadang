package kr.pe.sinnori.common.config.valueobject;

import java.io.File;

import kr.pe.sinnori.common.config.AbstractNativeValueConverter;
import kr.pe.sinnori.common.config.itemidinfo.ItemID;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDInfo;
import kr.pe.sinnori.common.exception.ConfigErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Warning! 비활성한 항목의 값은 쓰레기값이므로 환경 설정 파일을 읽어와서 Value Object 에 저장할때 건너뛰어 초기값인 null 값을 갖게 된다.
 * 따라서 Value Object 에서 값을 넘겨줄때 비활성 항목의 경우 null 검사를 수행하여 ConfigErrorException 을 던져야 한다.
 * 
 * @author Won Jonghoon
 *
 */
public class DBCPPart {
	private Logger log = LoggerFactory.getLogger(DBCPPart.class);
	
	private String dbcpName = null;
	private File dbcpConfigFile = null;
	
	public DBCPPart(String dbcpName) {
		this.dbcpName = dbcpName;
	}
	
	public String getDBCPName() {
		return dbcpName;
	}
	
	public File getDBCPConfigFile() {
		return dbcpConfigFile;
	}
	
	public void mapping(ItemIDInfo<?> itemIDInfo, String itemValue) 
			throws IllegalArgumentException, ConfigErrorException, ClassCastException {
		if (null == itemIDInfo) {
			String errorMessage = new StringBuilder("the parameter itemIDInfo is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == itemValue) {
			String errorMessage = new StringBuilder("the parameter itemValue is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}		
		
		String itemID = itemIDInfo.getItemID();
		
		if (! itemIDInfo.getConfigurationPart().equals(ItemIDInfo.ConfigurationPart.DBCP)) {
			String errorMessage = new StringBuilder("the parameter itemIDInfo[")
			.append(itemID).append("]'s configuration part is not dbcp part").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		AbstractNativeValueConverter<?> nativeValueConveter = itemIDInfo.getItemValueConverter();
		String nativeValueConverterTypeName = nativeValueConveter.getGenericType().getName();
		Object nativeValue = null;
		try {
			nativeValue = nativeValueConveter.valueOf(itemValue);				
		} catch(IllegalArgumentException e) {
			String errorMessage = new StringBuilder("fail to get a native value of the parameter itemValue[")
			.append(itemValue)
			.append("] from the parameter itemIDInfo[")
			.append(itemID).append("]'s native value converter").toString();
			
			log.warn("errorMessage", e);
			
			throw new ConfigErrorException(errorMessage);
		}	
		
		if (itemID.equals(ItemID.DBCPPartItemID.DBCP_CONFIGE_FILE_ITEMID)) {			
			if (null != nativeValue && !(nativeValue instanceof File)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValueConverterTypeName)
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(File.class.getName())
				.append("]").toString();
				throw new ConfigErrorException(errorMessage);
			}
			
			this.dbcpConfigFile = (File) nativeValue;			
		} else {
			String errorMessage = new StringBuilder("unknown DBCP part item id(=the parameter itemIDInfo[")
			.append(itemID)
			.append("]), check it").toString();
			throw new ConfigErrorException(errorMessage);
		}
		
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DBCPPart [dbcpName=");
		builder.append(dbcpName);
		builder.append(", dbcpConfigFile=");
		builder.append(dbcpConfigFile);
		builder.append("]");
		return builder.toString();
	}	
}
