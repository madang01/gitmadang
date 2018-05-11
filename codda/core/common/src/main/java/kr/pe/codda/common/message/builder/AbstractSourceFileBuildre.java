package kr.pe.codda.common.message.builder;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.etc.CommonStaticFinalVars;

public abstract class AbstractSourceFileBuildre {
	protected final InternalLogger log = InternalLoggerFactory.getInstance(AbstractSourceFileBuildre.class);

	public void addLincensePart(StringBuilder contentsStringBuilder) {
		contentsStringBuilder.append("/*");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(" * Licensed to the Apache Software Foundation (ASF) under one or more");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(" * contributor license agreements.  See the NOTICE file distributed with");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(" * this work for additional information regarding copyright ownership.");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(" * The ASF licenses this file to You under the Apache License, Version 2.0");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(" * (the \"License\"); you may not use this file except in compliance with");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(" * the License.  You may obtain a copy of the License at");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(" * ");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(" *      http://www.apache.org/licenses/LICENSE-2.0");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(" * ");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(" * Unless required by applicable law or agreed to in writing, software");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(" * distributed under the License is distributed on an \"AS IS\" BASIS,");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(" * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(" * See the License for the specific language governing permissions and");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(" * limitations under the License.");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(" */");
	}
	
	public void addPackageDeclarationPart(StringBuilder contentsStringBuilder, String messageID) {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append("package ");		
		contentsStringBuilder.append(CommonStaticFinalVars.BASE_MESSAGE_CLASS_FULL_NAME);
		contentsStringBuilder.append(".");
		contentsStringBuilder.append(messageID);
		contentsStringBuilder.append(";");
	}
	
	public void addImportDeclarationPart(StringBuilder contentsStringBuilder, Class<?> importClazz) {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append("import ");		
		contentsStringBuilder.append(importClazz.getName());
		contentsStringBuilder.append(";");
	}
	
	public void addImportDeclarationsPart(StringBuilder contentsStringBuilder, Class<?> importClazzes[]) {
		if (null == importClazzes) {
			throw new IllegalArgumentException("the parameter importClazzes is null");
		}
		
		for (Class<?> importClazz : importClazzes) {			
			addImportDeclarationPart(contentsStringBuilder, importClazz);
		}
	}
	
	public void addSourceFileDescriptionPart(StringBuilder contentsStringBuilder, String messageID, String author, String fileDescription) {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append("/**");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(" * ");
		contentsStringBuilder.append(messageID);
		contentsStringBuilder.append(" ");
		contentsStringBuilder.append(fileDescription);
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(" * @author ");
		contentsStringBuilder.append(author);
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(" *");
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		contentsStringBuilder.append(" */");
	}	
}
