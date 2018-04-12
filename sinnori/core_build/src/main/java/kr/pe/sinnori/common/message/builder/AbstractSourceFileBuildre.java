package kr.pe.sinnori.common.message.builder;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;

public abstract class AbstractSourceFileBuildre {
	protected final InternalLogger log = InternalLoggerFactory.getInstance(AbstractSourceFileBuildre.class);
	
	public static final String PACKAGE_FULL_NAME_PREFIX = "kr.pe.sinnori.impl.message.";
	
	public String buildStringOfLincensePart() {
		StringBuilder licenseBuilder = new StringBuilder();
		
		licenseBuilder.append("/*");
		licenseBuilder.append(CommonStaticFinalVars.NEWLINE);
		licenseBuilder.append(" * Licensed to the Apache Software Foundation (ASF) under one or more");
		licenseBuilder.append(CommonStaticFinalVars.NEWLINE);
		licenseBuilder.append(" * contributor license agreements.  See the NOTICE file distributed with");
		licenseBuilder.append(CommonStaticFinalVars.NEWLINE);
		licenseBuilder.append(" * this work for additional information regarding copyright ownership.");
		licenseBuilder.append(CommonStaticFinalVars.NEWLINE);
		licenseBuilder.append(" * The ASF licenses this file to You under the Apache License, Version 2.0");
		licenseBuilder.append(CommonStaticFinalVars.NEWLINE);
		licenseBuilder.append(" * (the \"License\"); you may not use this file except in compliance with");
		licenseBuilder.append(CommonStaticFinalVars.NEWLINE);
		licenseBuilder.append(" * the License.  You may obtain a copy of the License at");
		licenseBuilder.append(CommonStaticFinalVars.NEWLINE);
		licenseBuilder.append(" * ");
		licenseBuilder.append(CommonStaticFinalVars.NEWLINE);
		licenseBuilder.append(" *      http://www.apache.org/licenses/LICENSE-2.0");
		licenseBuilder.append(CommonStaticFinalVars.NEWLINE);
		licenseBuilder.append(" * ");
		licenseBuilder.append(CommonStaticFinalVars.NEWLINE);
		licenseBuilder.append(" * Unless required by applicable law or agreed to in writing, software");
		licenseBuilder.append(CommonStaticFinalVars.NEWLINE);
		licenseBuilder.append(" * distributed under the License is distributed on an \"AS IS\" BASIS,");
		licenseBuilder.append(CommonStaticFinalVars.NEWLINE);
		licenseBuilder.append(" * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.");
		licenseBuilder.append(CommonStaticFinalVars.NEWLINE);
		licenseBuilder.append(" * See the License for the specific language governing permissions and");
		licenseBuilder.append(CommonStaticFinalVars.NEWLINE);
		licenseBuilder.append(" * limitations under the License.");
		licenseBuilder.append(CommonStaticFinalVars.NEWLINE);
		licenseBuilder.append(" */");
		
		return licenseBuilder.toString();
	}
	
	public String getPackagePartString(String messageID) {
		StringBuilder packagePartStringBuilder = new StringBuilder();
		
		packagePartStringBuilder.append("package ");
		packagePartStringBuilder.append(PACKAGE_FULL_NAME_PREFIX);
		packagePartStringBuilder.append(messageID);
		packagePartStringBuilder.append(";");
		
		return packagePartStringBuilder.toString();
	}
	
	public String buildStringOfImportPart(String importElements[]) {
		if (null == importElements) {
			throw new IllegalArgumentException("the parameter importElements is null");
		}
		
		// int importElementsSize = importElements.length;
		if (0 == importElements.length) {
			return "";
		}
		
		StringBuilder ImportPartBuilder = new StringBuilder();
		ImportPartBuilder.append(importElements[0]);
		
		for (int i=1; i < importElements.length; i++) {
			ImportPartBuilder.append(CommonStaticFinalVars.NEWLINE);
			ImportPartBuilder.append(importElements[i]);
		}
		
		return ImportPartBuilder.toString();
	}
	
	public String buildStringOfFileDescriptionPart(String messageID, String author, String fileDescription) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("/**");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(" * ");
		stringBuilder.append(messageID);
		stringBuilder.append(" ");
		stringBuilder.append(fileDescription);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(" * @author ");
		stringBuilder.append(author);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(" *");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(" */");
		
		return stringBuilder.toString();
	}	
}
