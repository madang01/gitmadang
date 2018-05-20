package kr.pe.codda.common.config.subset;

import java.io.File;

import kr.pe.codda.common.config.itemidinfo.ItemIDDefiner;
import kr.pe.codda.common.exception.CoddaConfigurationException;
import kr.pe.codda.common.type.SessionKey;

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
	
	
	private SessionKey.RSAKeypairSourceType rsaKeypairSourceOfSessionKey = null;
	private File rsaPublickeyFileOfSessionKey = null;
	private File rsaPrivatekeyFileOfSessionKey = null;
	private Integer rsaKeySizeOfSessionKey = null;	
	private String symmetricKeyAlgorithmOfSessionKey = null;	
	private Integer symmetricKeySizeOfSessionKey=null;
	private Integer symmetricIVSizeOfSessionKey=null;	
	
	private Integer localSourceFileResourceCnt=null;
	private Integer localTargetFileResourceCnt=null;
	private Integer fileBlockMaxSize=null;
	
	private Integer cachedObjectMaxSize=null;
	//private Long maxUpdateSeqInterva=5000;	
	
	public CommonPartConfiguration() {
				
	}
	
	public void mapping(String itemKey, Object nativeValue) 
			throws IllegalArgumentException, CoddaConfigurationException, ClassCastException {
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
				throw new CoddaConfigurationException(errorMessage);
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
				throw new CoddaConfigurationException(errorMessage);
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
				throw new CoddaConfigurationException(errorMessage);
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
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.webLayoutControlPage = (String) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_KEYPAIR_SOURCE_ITEMID)) {			
			if (null != nativeValue && !(nativeValue instanceof SessionKey.RSAKeypairSourceType)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(SessionKey.RSAKeypairSourceType.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.rsaKeypairSourceOfSessionKey = (SessionKey.RSAKeypairSourceType) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof File)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(File.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.rsaPublickeyFileOfSessionKey = (File) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof File)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(File.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.rsaPrivatekeyFileOfSessionKey = (File) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_KEYSIZE_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
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
				throw new CoddaConfigurationException(errorMessage);
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
				throw new CoddaConfigurationException(errorMessage);
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
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.symmetricIVSizeOfSessionKey = (Integer) nativeValue;		
		} else if (itemID.equals(ItemIDDefiner.CommonPartItemIDDefiner.COMMON_UPDOWNFILE_LOCAL_SOURCE_FILE_RESOURCE_CNT_ITEMID)) {
			if (null != nativeValue && !(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
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
				throw new CoddaConfigurationException(errorMessage);
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
				throw new CoddaConfigurationException(errorMessage);
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
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.cachedObjectMaxSize = (Integer) nativeValue;
		} else {
			String errorMessage = new StringBuilder("unknown DBCP part item id(=the parameter itemIDInfo[")
			.append(itemID)
			.append("]), check it").toString();
			throw new CoddaConfigurationException(errorMessage);
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

	public SessionKey.RSAKeypairSourceType getRsaKeypairSourceOfSessionKey() {
		return rsaKeypairSourceOfSessionKey;
	}

	public File getRSAPublickeyFileOfSessionKey() throws CoddaConfigurationException {
		if (null == rsaPublickeyFileOfSessionKey) {
			throw new CoddaConfigurationException("config's RSA public key file is null because of no matching or inactive status");
		}
		return rsaPublickeyFileOfSessionKey;
	}
	
	public File getRSAPrivatekeyFileOfSessionKey() throws CoddaConfigurationException {
		if (null == rsaPrivatekeyFileOfSessionKey) {
			throw new CoddaConfigurationException("config's RSA private key file is null because of no matching or inactive status");
		}
		return rsaPrivatekeyFileOfSessionKey;
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
		builder.append("CommonPartConfiguration [jdfErrorMessagePage=");
		builder.append(jdfErrorMessagePage);
		builder.append(", jdfLoginPage=");
		builder.append(jdfLoginPage);
		builder.append(", jdfServletTrace=");
		builder.append(jdfServletTrace);
		builder.append(", webLayoutControlPage=");
		builder.append(webLayoutControlPage);
		builder.append(", rsaKeypairSourceOfSessionKey=");
		builder.append(rsaKeypairSourceOfSessionKey);
		builder.append(", rsaPublickeyFileOfSessionKey=");
		builder.append(rsaPublickeyFileOfSessionKey);
		builder.append(", rsaPrivatekeyFileOfSessionKey=");
		builder.append(rsaPrivatekeyFileOfSessionKey);
		builder.append(", rsaKeySizeOfSessionKey=");
		builder.append(rsaKeySizeOfSessionKey);
		builder.append(", symmetricKeyAlgorithmOfSessionKey=");
		builder.append(symmetricKeyAlgorithmOfSessionKey);
		builder.append(", symmetricKeySizeOfSessionKey=");
		builder.append(symmetricKeySizeOfSessionKey);
		builder.append(", symmetricIVSizeOfSessionKey=");
		builder.append(symmetricIVSizeOfSessionKey);
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
