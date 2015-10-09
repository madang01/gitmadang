package kr.pe.sinnori.common.config.valueobject;

import java.io.File;

import kr.pe.sinnori.common.config.AbstractNativeValueConverter;
import kr.pe.sinnori.common.config.itemidinfo.ItemID;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDInfo;
import kr.pe.sinnori.common.etc.CommonType;
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
public class CommonPart {
	private Logger log = LoggerFactory.getLogger(CommonPart.class);
	
	private String jdfErrorMessagePage = null;
	private String jdfLoginPage = null;
	private Boolean jdfServletTrace = null;		
	private String webLayoutControlPage = null;
	
	
	private CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY rsaKeypairSourceOfSessionKey = null;
	private File rsaKeyPairPathOfSessionKey = null;
	private Integer rsaKeySizeOfSessionKey = null;	
	private String symmetricKeyAlgorithmOfSessionKey = null;	
	private Integer symmetricKeySizeOfSessionKey=null;
	private Integer symmetricIVSizeOfSessionKey=null;
	private CommonType.SYMMETRIC_KEY_ENCODING symmetricKeyEncodingOfSessionKey = null;	
	
	private Integer localSourceFileResourceCnt=null;
	private Integer localTargetFileResourceCnt=null;
	private Integer fileBlockMaxSize=null;
	
	private Integer cachedObjectMaxSize=null;
	//private Long maxUpdateSeqInterva=5000;	
	
	public CommonPart() {
				
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
		
		if (! itemIDInfo.getConfigurationPart().equals(ItemIDInfo.ConfigurationPart.COMMON)) {
			String errorMessage = new StringBuilder("the parameter itemIDInfo[")
			.append(itemID).append("]'s configuration part is not common part").toString();
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
		
		
		if (itemID.equals(ItemID.CommonPartItemID.SERVLET_JSP_JDF_ERROR_MESSAGE_PAGE_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof String)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValueConverterTypeName)
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(String.class.getName())
				.append("]").toString();
				throw new ConfigErrorException(errorMessage);
			}
			
			this.jdfErrorMessagePage = (String) nativeValue;			
		} else if (itemID.equals(ItemID.CommonPartItemID.SERVLET_JSP_JDF_LOGIN_PAGE_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof String)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValueConverterTypeName)
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(String.class.getName())
				.append("]").toString();
				throw new ConfigErrorException(errorMessage);
			}
			
			this.jdfLoginPage = (String) nativeValue;
		} else if (itemID.equals(ItemID.CommonPartItemID.SERVLET_JSP_JDF_SERVLET_TRACE_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof Boolean)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValueConverterTypeName)
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Boolean.class.getName())
				.append("]").toString();
				throw new ConfigErrorException(errorMessage);
			}
			
			this.jdfServletTrace = (Boolean) nativeValue;
		} else if (itemID.equals(ItemID.CommonPartItemID.SERVLET_JSP_WEB_LAYOUT_CONTROL_PAGE_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof String)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValueConverterTypeName)
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(String.class.getName())
				.append("]").toString();
				throw new ConfigErrorException(errorMessage);
			}
			
			this.webLayoutControlPage = (String) nativeValue;
		} else if (itemID.equals(ItemID.CommonPartItemID.SESSIONKEY_RSA_KEYPAIR_SOURCE_ITEMID)) {			
			if (null != nativeValue && !(nativeValue instanceof CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValueConverterTypeName)
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.class.getName())
				.append("]").toString();
				throw new ConfigErrorException(errorMessage);
			}
			
			this.rsaKeypairSourceOfSessionKey = (CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY) nativeValue;
		} else if (itemID.equals(ItemID.CommonPartItemID.SESSIONKEY_RSA_KEYPAIR_PATH_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof File)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValueConverterTypeName)
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(File.class.getName())
				.append("]").toString();
				throw new ConfigErrorException(errorMessage);
			}
			
			this.rsaKeyPairPathOfSessionKey = (File) nativeValue;
		} else if (itemID.equals(ItemID.CommonPartItemID.SESSIONKEY_RSA_KEYSIZE_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValueConverterTypeName)
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new ConfigErrorException(errorMessage);
			}
			
			this.rsaKeySizeOfSessionKey = (Integer) nativeValue;
		} else if (itemID.equals(ItemID.CommonPartItemID.SESSIONKEY_SYMMETRIC_KEY_ALGORITHM_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof String)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValueConverterTypeName)
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(String.class.getName())
				.append("]").toString();
				throw new ConfigErrorException(errorMessage);
			}
			
			this.symmetricKeyAlgorithmOfSessionKey = (String) nativeValue;
		} else if (itemID.equals(ItemID.CommonPartItemID.SESSIONKEY_SYMMETRIC_KEY_SIZE_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValueConverterTypeName)
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new ConfigErrorException(errorMessage);
			}
			
			this.symmetricKeySizeOfSessionKey = (Integer) nativeValue;
		} else if (itemID.equals(ItemID.CommonPartItemID.SESSIONKEY_IV_SIZE_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValueConverterTypeName)
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new ConfigErrorException(errorMessage);
			}
			
			this.symmetricIVSizeOfSessionKey = (Integer) nativeValue;
		} else if (itemID.equals(ItemID.CommonPartItemID.SESSIONKEY_PRIVATE_KEY_ENCODING_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof CommonType.SYMMETRIC_KEY_ENCODING)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValueConverterTypeName)
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(CommonType.SYMMETRIC_KEY_ENCODING.class.getName())
				.append("]").toString();
				throw new ConfigErrorException(errorMessage);
			}
			
			this.symmetricKeyEncodingOfSessionKey = (CommonType.SYMMETRIC_KEY_ENCODING) nativeValue;
		} else if (itemID.equals(ItemID.CommonPartItemID.COMMON_UPDOWNFILE_LOCAL_SOURCE_FILE_RESOURCE_CNT_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValueConverterTypeName)
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new ConfigErrorException(errorMessage);
			}
			
			this.localSourceFileResourceCnt = (Integer) nativeValue;
		} else if (itemID.equals(ItemID.CommonPartItemID.COMMON_UPDOWNFILE_LOCAL_TARGET_FILE_RESOURCE_CNT_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValueConverterTypeName)
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new ConfigErrorException(errorMessage);
			}
			
			this.localTargetFileResourceCnt = (Integer) nativeValue;
		} else if (itemID.equals(ItemID.CommonPartItemID.COMMON_UPDOWNFILE_FILE_BLOCK_MAX_SIZE_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValueConverterTypeName)
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new ConfigErrorException(errorMessage);
			}
			
			this.fileBlockMaxSize = (Integer) nativeValue;		
		} else if (itemID.equals(ItemID.CommonPartItemID.COMMON_CACHED_OBJECT_MAX_SIZE_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValueConverterTypeName)
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new ConfigErrorException(errorMessage);
			}
			
			this.cachedObjectMaxSize = (Integer) nativeValue;
		} else {
			String errorMessage = new StringBuilder("unknown DBCP part item id(=the parameter itemIDInfo[")
			.append(itemID)
			.append("]), check it").toString();
			throw new ConfigErrorException(errorMessage);
		}
	}
	
	/*

	public long getMaxUpdateSeqInterva() {
		return maxUpdateSeqInterva;
	}*/

	public String getJdfErrorMessagePage() {
		return jdfErrorMessagePage;
	}

	public String getJdfLoginPage() {
		return jdfLoginPage;
	}

	public Boolean getJdfServletTrace() {
		return jdfServletTrace;
	}

	public String getWebLayoutControlPage() {
		return webLayoutControlPage;
	}

	public CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY getRsaKeypairSourceOfSessionKey() {
		return rsaKeypairSourceOfSessionKey;
	}

	public File getRsaKeyPairPathOfSessionKey() throws ConfigErrorException {
		if (null == rsaKeyPairPathOfSessionKey) {
			throw new ConfigErrorException("no matching or inactive status");
		}
		return rsaKeyPairPathOfSessionKey;
	}

	public Integer getRsaKeySizeOfSessionKey() {
		return rsaKeySizeOfSessionKey;
	}

	public String getSymmetricKeyAlgorithmOfSessionKey() {
		return symmetricKeyAlgorithmOfSessionKey;
	}

	public Integer getSymmetricKeySizeOfSessionKey() {
		return symmetricKeySizeOfSessionKey;
	}

	public Integer getSymmetricIVSizeOfSessionKey() {
		return symmetricIVSizeOfSessionKey;
	}

	public CommonType.SYMMETRIC_KEY_ENCODING getSymmetricKeyEncodingOfSessionKey() {
		return symmetricKeyEncodingOfSessionKey;
	}

	public Integer getLocalSourceFileResourceCnt() {
		return localSourceFileResourceCnt;
	}

	public Integer getLocalTargetFileResourceCnt() {
		return localTargetFileResourceCnt;
	}

	public Integer getFileBlockMaxSize() {
		return fileBlockMaxSize;
	}

	public Integer getCachedObjectMaxSize() {
		return cachedObjectMaxSize;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CommonPart [jdfErrorMessagePage=");
		builder.append(jdfErrorMessagePage);
		builder.append(", jdfLoginPage=");
		builder.append(jdfLoginPage);
		builder.append(", jdfServletTrace=");
		builder.append(jdfServletTrace);
		builder.append(", webLayoutControlPage=");
		builder.append(webLayoutControlPage);
		builder.append(", rsaKeypairSourceOfSessionKey=");
		builder.append(rsaKeypairSourceOfSessionKey);
		builder.append(", rsaKeyPairPathOfSessionKey=");
		builder.append(rsaKeyPairPathOfSessionKey);
		builder.append(", rsaKeySizeOfSessionKey=");
		builder.append(rsaKeySizeOfSessionKey);
		builder.append(", symmetricKeyAlgorithmOfSessionKey=");
		builder.append(symmetricKeyAlgorithmOfSessionKey);
		builder.append(", symmetricKeySizeOfSessionKey=");
		builder.append(symmetricKeySizeOfSessionKey);
		builder.append(", symmetricIVSizeOfSessionKey=");
		builder.append(symmetricIVSizeOfSessionKey);
		builder.append(", symmetricKeyEncodingOfSessionKey=");
		builder.append(symmetricKeyEncodingOfSessionKey);
		builder.append(", localSourceFileResourceCnt=");
		builder.append(localSourceFileResourceCnt);
		builder.append(", localTargetFileResourceCnt=");
		builder.append(localTargetFileResourceCnt);
		builder.append(", fileBlockMaxSize=");
		builder.append(fileBlockMaxSize);		
		builder.append(", cachedObjectMaxSize=");
		builder.append(cachedObjectMaxSize);
		builder.append("]");
		return builder.toString();
	}	
}
