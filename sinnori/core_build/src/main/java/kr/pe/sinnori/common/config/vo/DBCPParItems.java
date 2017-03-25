package kr.pe.sinnori.common.config.vo;

import java.io.File;

import kr.pe.sinnori.common.config.itemidinfo.ItemIDDefiner;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;

/**
 * Warning! 비활성한 항목의 값은 쓰레기값이므로 
 * 환경 설정 파일을 읽어와서 Value Object 에 저장할때 건너뛰어 초기값인 null 값을 갖게 된다.
 * 따라서 Value Object 에서 값을 넘겨줄때 비활성 항목의 경우 
 * null 검사를 수행하여 ConfigErrorException 을 던져야 한다.
 * 
 * @author Won Jonghoon
 *
 */
public class DBCPParItems {
	// private Logger log = LoggerFactory.getLogger(DBCPPartValueObject.class);
	
	private String dbcpName = null;
	private File dbcpConfigFile = null;
	
	private String prefexOfItemID = null;
	
	public DBCPParItems(String dbcpName) {
		this.dbcpName = dbcpName;
		
		prefexOfItemID = new StringBuilder("dbcp.").append(dbcpName)
				.append(".").toString();
	}
	
	public String getDBCPName() {
		return dbcpName;
	}
	
	public File getDBCPConfigFile() {
		return dbcpConfigFile;
	}
	
	public void mapping(String itemKey, Object nativeValue) 
			throws IllegalArgumentException, SinnoriConfigurationException, ClassCastException {
		if (null == itemKey) {
			throw new IllegalArgumentException("the parameter itemKey is null");
		}
		
		if (null == nativeValue) {
			throw new IllegalArgumentException("the parameter nativeValue is null");
		}
		
		if (! itemKey.startsWith(prefexOfItemID)) {
			String errorMessage = new StringBuilder("the parameter itemKey[")
			.append("] doesn't start with prefix[")
			.append(prefexOfItemID).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		/**
		 * no IndexOutOfBoundsException because the variable itemKey starts with the variable prefexOfItemID
		 */
		String itemID = itemKey.substring(prefexOfItemID.length());		
		
		
		if (itemID.equals(ItemIDDefiner.DBCPPartItemIDDefiner.DBCP_CONFIGE_FILE_ITEMID)) {			
			if (null != nativeValue && !(nativeValue instanceof File)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(File.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.dbcpConfigFile = (File) nativeValue;			
		} else {
			String errorMessage = new StringBuilder("unknown DBCP part item id(=the parameter itemIDInfo[")
			.append(itemID)
			.append("]), check it").toString();
			throw new SinnoriConfigurationException(errorMessage);
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
