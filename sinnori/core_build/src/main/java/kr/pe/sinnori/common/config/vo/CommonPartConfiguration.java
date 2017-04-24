package kr.pe.sinnori.common.config.vo;

import java.io.File;

import kr.pe.sinnori.common.config.itemidinfo.ItemIDDefiner;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;

/**
 * Warning! 비활성한 항목의 값은 쓰레기값이므로 환경 설정 파일을 읽어와서 Value Object 에 저장할때 건너뛰어 초기값인 null 값을 갖게 된다.
 * 따라서 Value Object 에서 값을 넘겨줄때 비활성 항목의 경우 null 검사를 수행하여 ConfigErrorException 을 던져야 한다.
 * 
 * @author Won Jonghoon
 *
 */
public class CommonPartConfiguration {
	// private Logger log = LoggerFactory.getLogger(CommonPartValueObject.class);
	
	
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
	private CommonType.SYMMETRIC_KEY_ENCODING_TYPE symmetricKeyEncodingOfSessionKey = null;	
	
	private Integer localSourceFileResourceCnt=null;
	private Integer localTargetFileResourceCnt=null;
	private Integer fileBlockMaxSize=null;
	
	private Integer cachedObjectMaxSize=null;
	//private Long maxUpdateSeqInterva=5000;	
	
	public CommonPartConfiguration() {
				
	}
	
	public void mapping(String itemKey, Object nativeValue) 
			throws IllegalArgumentException, SinnoriConfigurationException, ClassCastException {
		if (null == itemKey) {
			throw new IllegalArgumentException("the parameter itemKey is null");
		}
		
		if (null == nativeValue) {
			throw new IllegalArgumentException("the parameter nativeValue is null");
		}
		
		String itemID = itemKey;		
		
		if (itemID.equals(ItemIDDefiner.CommonPartItemIDDefiner.SERVLET_JSP_JDF_ERROR_MESSAGE_PAGE_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof String)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(String.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.jdfErrorMessagePage = (String) nativeValue;			
		} else if (itemID.equals(ItemIDDefiner.CommonPartItemIDDefiner.SERVLET_JSP_JDF_LOGIN_PAGE_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof String)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(String.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.jdfLoginPage = (String) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.CommonPartItemIDDefiner.SERVLET_JSP_JDF_SERVLET_TRACE_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof Boolean)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Boolean.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.jdfServletTrace = (Boolean) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.CommonPartItemIDDefiner.SERVLET_JSP_WEB_LAYOUT_CONTROL_PAGE_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof String)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(String.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.webLayoutControlPage = (String) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_KEYPAIR_SOURCE_ITEMID)) {			
			if (null != nativeValue && !(nativeValue instanceof CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.rsaKeypairSourceOfSessionKey = (CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_KEYPAIR_PATH_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof File)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(File.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.rsaKeyPairPathOfSessionKey = (File) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_KEYSIZE_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.rsaKeySizeOfSessionKey = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_SYMMETRIC_KEY_ALGORITHM_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof String)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(String.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.symmetricKeyAlgorithmOfSessionKey = (String) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_SYMMETRIC_KEY_SIZE_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.symmetricKeySizeOfSessionKey = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_IV_SIZE_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.symmetricIVSizeOfSessionKey = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_PRIVATE_KEY_ENCODING_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof CommonType.SYMMETRIC_KEY_ENCODING_TYPE)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(CommonType.SYMMETRIC_KEY_ENCODING_TYPE.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.symmetricKeyEncodingOfSessionKey = (CommonType.SYMMETRIC_KEY_ENCODING_TYPE) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.CommonPartItemIDDefiner.COMMON_UPDOWNFILE_LOCAL_SOURCE_FILE_RESOURCE_CNT_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.localSourceFileResourceCnt = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.CommonPartItemIDDefiner.COMMON_UPDOWNFILE_LOCAL_TARGET_FILE_RESOURCE_CNT_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.localTargetFileResourceCnt = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.CommonPartItemIDDefiner.COMMON_UPDOWNFILE_FILE_BLOCK_MAX_SIZE_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.fileBlockMaxSize = (Integer) nativeValue;		
		} else if (itemID.equals(ItemIDDefiner.CommonPartItemIDDefiner.COMMON_CACHED_OBJECT_MAX_SIZE_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.cachedObjectMaxSize = (Integer) nativeValue;
		} else {
			String errorMessage = new StringBuilder("unknown DBCP part item id(=the parameter itemIDInfo[")
			.append(itemID)
			.append("]), check it").toString();
			throw new SinnoriConfigurationException(errorMessage);
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

	public File getRsaKeyPairPathOfSessionKey() throws SinnoriConfigurationException {
		if (null == rsaKeyPairPathOfSessionKey) {
			throw new SinnoriConfigurationException("no matching or inactive status");
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

	public CommonType.SYMMETRIC_KEY_ENCODING_TYPE getSymmetricKeyEncodingOfSessionKey() {
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
