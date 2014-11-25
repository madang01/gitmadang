package kr.pe.sinnori.source_file_builder;

import kr.pe.sinnori.common.lib.CommonStaticFinalVars;

public abstract class AbstractSourceFileBuildre {
	protected String dynamicClassBasePackageName = "kr.pe.sinnori.impl.message.";
	
	public String getLincenseString() {
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

}
