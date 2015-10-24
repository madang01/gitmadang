package kr.pe.sinnori.common.config.itemidinfo;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningRegularFile;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;

public abstract class DBCPPartItemIDInfoAdder {
	public static void addAllDBCPPartItemIDInfo(
			DBCPPartItemIDInfoMangerIF dbcpPartItemIDInfoManger)
			throws IllegalArgumentException, SinnoriConfigurationException {
		Logger log = LoggerFactory.getLogger(DBCPPartItemIDInfoAdder.class);
		
		ItemIDInfo<?> itemIDInfo = null;
		String itemID = null;

		boolean isDefaultValueCheck = false;

		/** DBCP start */
		try {
			itemID = ItemIDDefiner.DBCPPartItemIDDefiner.DBCP_CONFIGE_FILE_ITEMID;
			isDefaultValueCheck = false;
			{
				boolean isWritePermissionChecking = false;
				itemIDInfo = new ItemIDInfo<File>(
						ItemIDInfo.ConfigurationPart.DBCP,
						ItemIDInfo.ViewType.FILE,
						itemID,
						"dbcp 설정 파일 경로명",
						"[sinnori installed path]/project/[main project name]/config/[dbcp name].properties",
						isDefaultValueCheck,
						new GeneralConverterReturningRegularFile(
								isWritePermissionChecking));
			}

			dbcpPartItemIDInfoManger.addDBCPPartItemIDInfo(itemIDInfo);
		} catch (SinnoriConfigurationException | IllegalArgumentException e) {
			String errorMessage = new StringBuilder(
					"fail to add dbcp part item identification[")
					.append(itemID).append("] information").toString();

			log.info(errorMessage, e);

			throw new SinnoriConfigurationException(new StringBuilder(errorMessage)
			.append(", errrorMessage=").append(e.getMessage()).toString());
		}

		/** DBCP end */
	}
}
