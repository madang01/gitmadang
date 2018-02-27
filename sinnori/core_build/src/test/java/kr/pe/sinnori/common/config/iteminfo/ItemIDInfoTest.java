package kr.pe.sinnori.common.config.iteminfo;


import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitSupporter;
import kr.pe.sinnori.common.config.AbstractNativeValueConverter;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDInfo;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDInfo.ViewType;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningNoTrimString;

public class ItemIDInfoTest extends AbstractJunitSupporter {
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_NullParameter_configPart() throws Exception {
		try {
			ItemIDInfo.ConfigurationPart configPart = null;
			ViewType configItemViewType = ItemIDInfo.ViewType.TEXT;
			String itemID = "servlet_jsp.jdf_error_message_page.value";
			String description = "JDF framework 에서 에러 발생시 에러 내용을 보여주는 사용자 친화적인 화면을 전담할 jsp";
			String defaultValue = "/errorMessagePage.jsp";			
			boolean isDefaultValueCheck = true;
			AbstractNativeValueConverter<String> nativeValueConverter =
					new GeneralConverterReturningNoTrimString();
			
			new ItemIDInfo<String>(configPart,
					configItemViewType, itemID,
					description,
					defaultValue, isDefaultValueCheck,
					nativeValueConverter);
		} catch (IllegalArgumentException e) {
			log.info("the parameter configPart is null, errormessage={}", e.getMessage());
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_NullParameter_configItemViewType() throws Exception {
		try {
			ItemIDInfo.ConfigurationPart configPart = ItemIDInfo.ConfigurationPart.COMMON;
			ViewType configItemViewType = null;
			String itemID = "servlet_jsp.jdf_error_message_page.value";
			String description = "JDF framework 에서 에러 발생시 에러 내용을 보여주는 사용자 친화적인 화면을 전담할 jsp";
			String defaultValue = "/errorMessagePage.jsp";			
			boolean isDefaultValueCheck = true;
			AbstractNativeValueConverter<String> nativeValueConverter =
					new GeneralConverterReturningNoTrimString();
			
			new ItemIDInfo<String>(configPart,
					configItemViewType, itemID,
					description,
					defaultValue, isDefaultValueCheck,
					nativeValueConverter);
		} catch (IllegalArgumentException e) {
			log.info("the parameter configItemViewType is null, errormessage={}", e.getMessage());
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_NullParameter_itemID() throws Exception {
		try {
			ItemIDInfo.ConfigurationPart configPart = ItemIDInfo.ConfigurationPart.COMMON;
			ViewType configItemViewType = ItemIDInfo.ViewType.TEXT;
			String itemID = null;
			String description = "JDF framework 에서 에러 발생시 에러 내용을 보여주는 사용자 친화적인 화면을 전담할 jsp";
			String defaultValue = "/errorMessagePage.jsp";	
			boolean isDefaultValueCheck = true;
			AbstractNativeValueConverter<String> nativeValueConverter =
					new GeneralConverterReturningNoTrimString();
			
			new ItemIDInfo<String>(configPart,
					configItemViewType, itemID,
					description,
					defaultValue, isDefaultValueCheck,
					nativeValueConverter);
		} catch (IllegalArgumentException e) {
			log.info("the parameter itemID is null, errormessage={}", e.getMessage());
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_NullParameter_description() throws Exception {
		try {
			ItemIDInfo.ConfigurationPart configPart = ItemIDInfo.ConfigurationPart.COMMON;
			ViewType configItemViewType = ItemIDInfo.ViewType.TEXT;
			String itemID = "servlet_jsp.jdf_error_message_page.value";
			String description = null;
			String defaultValue = "/errorMessagePage.jsp";			
			boolean isDefaultValueCheck = true;
			AbstractNativeValueConverter<String> nativeValueConverter =
					new GeneralConverterReturningNoTrimString();
			
			new ItemIDInfo<String>(configPart,
					configItemViewType, itemID,
					description,
					defaultValue, isDefaultValueCheck,
					nativeValueConverter);
		} catch (IllegalArgumentException e) {
			log.info("the parameter description is null, errormessage={}", e.getMessage());
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_NullParameter_defaultValue() throws Exception {
		try {
			ItemIDInfo.ConfigurationPart configPart = ItemIDInfo.ConfigurationPart.COMMON;
			ViewType configItemViewType = ItemIDInfo.ViewType.TEXT;
			String itemID = "servlet_jsp.jdf_error_message_page.value";
			String description = "JDF framework 에서 에러 발생시 에러 내용을 보여주는 사용자 친화적인 화면을 전담할 jsp";
			String defaultValue = null;
			boolean isDefaultValueCheck = true;
			AbstractNativeValueConverter<String> nativeValueConverter =
					new GeneralConverterReturningNoTrimString();
			
			new ItemIDInfo<String>(configPart,
					configItemViewType, itemID,
					description,
					defaultValue, isDefaultValueCheck,
					nativeValueConverter);
		} catch (IllegalArgumentException e) {
			log.info("the parameter defaultValue is null, errormessage={}", e.getMessage());
			throw e;
		}
	}	
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_NullParameter_nativeValueConverter() throws Exception {
		try {
			ItemIDInfo.ConfigurationPart configPart = ItemIDInfo.ConfigurationPart.COMMON;
			ViewType configItemViewType = ItemIDInfo.ViewType.TEXT;
			String itemID = "servlet_jsp.jdf_error_message_page.value";
			String description = "JDF framework 에서 에러 발생시 에러 내용을 보여주는 사용자 친화적인 화면을 전담할 jsp";
			String defaultValue = "/errorMessagePage.jsp";			
			boolean isDefaultValueCheck = true;
			AbstractNativeValueConverter<String> nativeValueConverter = null;
			
			new ItemIDInfo<String>(configPart,
					configItemViewType, itemID,
					description,
					defaultValue, isDefaultValueCheck,
					nativeValueConverter);
		} catch (IllegalArgumentException e) {
			log.info("the parameter nativeValueConverter is null, errormessage={}", e.getMessage());
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_EmptyParameter_itemID() throws Exception {
		try {
			ItemIDInfo.ConfigurationPart configPart = ItemIDInfo.ConfigurationPart.COMMON;
			ViewType configItemViewType = ItemIDInfo.ViewType.TEXT;
			String itemID = "";
			String description = "JDF framework 에서 에러 발생시 에러 내용을 보여주는 사용자 친화적인 화면을 전담할 jsp";
			String defaultValue = "/errorMessagePage.jsp";			
			boolean isDefaultValueCheck = true;
			AbstractNativeValueConverter<String> nativeValueConverter =
					new GeneralConverterReturningNoTrimString();
			
			new ItemIDInfo<String>(configPart,
					configItemViewType, itemID,
					description,
					defaultValue, isDefaultValueCheck,
					nativeValueConverter);
		} catch (IllegalArgumentException e) {
			log.info("the parameter itemID is a empty string, errormessage={}", e.getMessage());
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_EmptyParameter_description() throws Exception {
		try {
			ItemIDInfo.ConfigurationPart configPart = ItemIDInfo.ConfigurationPart.COMMON;
			ViewType configItemViewType = ItemIDInfo.ViewType.TEXT;
			String itemID = "servlet_jsp.jdf_error_message_page.value";
			String description = "";
			String defaultValue = "/errorMessagePage.jsp";			
			boolean isDefaultValueCheck = true;
			AbstractNativeValueConverter<String> nativeValueConverter =
					new GeneralConverterReturningNoTrimString();
			
			new ItemIDInfo<String>(configPart,
					configItemViewType, itemID,
					description,
					defaultValue, isDefaultValueCheck,
					nativeValueConverter);
		} catch (IllegalArgumentException e) {
			log.info("the parameter description is a empty string, errormessage={}", e.getMessage());
			throw e;
		}
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_ValidButBadParameter_defaultValue() throws Exception {
		try {
			ItemIDInfo.ConfigurationPart configPart = ItemIDInfo.ConfigurationPart.COMMON;
			ViewType configItemViewType = ItemIDInfo.ViewType.TEXT;
			String itemID = "servlet_jsp.jdf_error_message_page.value";
			String description = "JDF framework 에서 에러 발생시 에러 내용을 보여주는 사용자 친화적인 화면을 전담할 jsp";
			String defaultValue = "/tmp.jsp ";
			boolean isDefaultValueCheck = true;
			AbstractNativeValueConverter<String> nativeValueConverter =
					new GeneralConverterReturningNoTrimString();
			
			new ItemIDInfo<String>(configPart,
					configItemViewType, itemID,
					description,
					defaultValue, isDefaultValueCheck,
					nativeValueConverter);
			
		} catch (IllegalArgumentException e) {
			log.info("the parameter defaultValue has leading or tailing white space, errormessage={}", e.getMessage());
			throw e;
		}
	}
	
}
