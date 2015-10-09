package kr.pe.sinnori.gui.config.buildsystem;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.util.SequencedProperties;

public abstract class AntPropertiesUtilOfBuildSystem {
	

	public static SequencedProperties getNewBuildSystemProperties(
			boolean isWebClient, String servletSystemLibrayPathString) {

		SequencedProperties buildSystemProperties = new SequencedProperties();

		setBuildSystemProperties(buildSystemProperties, isWebClient,
				servletSystemLibrayPathString);

		return buildSystemProperties;
	}

	public static void setBuildSystemProperties(SequencedProperties buildSystemProperties,
			boolean isWebClient, String servletSystemLibrayPathString) {

		buildSystemProperties.put(CommonStaticFinalVars.IS_WEB_CLIENT_KEY,
				String.valueOf(isWebClient));
		buildSystemProperties.put(CommonStaticFinalVars.SERVLET_SYSTEM_LIBIARY_PATH_KEY,
				servletSystemLibrayPathString);
	}
}
