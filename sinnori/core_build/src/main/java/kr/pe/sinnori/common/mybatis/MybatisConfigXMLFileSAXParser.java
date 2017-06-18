package kr.pe.sinnori.common.mybatis;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.vo.AllDBCPPartConfiguration;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.util.CommonStaticUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class MybatisConfigXMLFileSAXParser extends DefaultHandler {
	private final Logger log = LoggerFactory.getLogger(MybatisConfigXMLFileSAXParser.class);
	
	private SAXParser saxParser = null;
	
	private String rootTag = null;
	private Stack<String> startTagStack = new Stack<String>();
	private Stack<String> tagValueStack = new Stack<String>();
	
	private Set<String> environmentIDSet = new HashSet<String>();
	private List<File> mapperFileList = new ArrayList<File>();
	private String currentEnvironmentID = null;
	 
	
	
	public MybatisConfigXMLFileSAXParser() throws SAXException {
		saxParser = getNewInstanceOfSAXParser();
	}

	private SAXParser getNewInstanceOfSAXParser() throws SAXException {
		SAXParser saxParser = null;
		try {
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setValidating(true);
			saxParserFactory.setNamespaceAware(false);

			/*SchemaFactory schemaFactory = SchemaFactory
					.newInstance("http://www.w3.org/2001/XMLSchema");	
			
			saxParserFactory.setSchema(schemaFactory
					.newSchema(new Source[] { new StreamSource(ItemValueTypeManger.getInstance().getMesgXSLInputSream()) }));
			*/
			saxParser = saxParserFactory.newSAXParser();
		} catch (Exception | Error e) {
			log.warn(e.getMessage(), e);
			throw new SAXException(e.getMessage());
		}
		
		return saxParser;
	}
	
	public FileTypeResourceManager parse(File mybatisConfigFile) 
			throws IllegalArgumentException, SAXException, IOException {
		if (null == mybatisConfigFile) {
			throw new IllegalArgumentException("the parameter mybatisConfigFile is null");
		}
		
		if (! mybatisConfigFile.exists()) {
			String errorMessage = new StringBuilder("the parameter mybatisConfigFile[")
			.append(mybatisConfigFile.getAbsolutePath())
			.append("] does not exist").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (! mybatisConfigFile.isFile()) {
			String errorMessage = new StringBuilder("the file(=the parameter mybatisConfigFile[")
			.append(mybatisConfigFile.getAbsolutePath())
			.append("]) isn't a regular file").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (! mybatisConfigFile.canRead()) {
			String errorMessage = new StringBuilder("the file(=the parameter mybatisConfigFile[")
			.append(mybatisConfigFile.getAbsolutePath())
			.append("]) doesn't hava permission to read").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		// fileTypeResource2FileList.add(xmlFile);
		// mybatisConfigFile = xmlFile;
		
		FileTypeResourceManager fileTypeResourceManger = new FileTypeResourceManager(mybatisConfigFile);
		
		try {
			saxParser.parse(mybatisConfigFile, this);
			/**
			 * Warning! copy list before reset
			 */
			for (File fileTypeResource2File : mapperFileList) {
				fileTypeResourceManger.addMapperFile(fileTypeResource2File);
			}
		} finally {
			reset();	
		}	
		
		return fileTypeResourceManger;
	}
	
	private void reset() throws SAXException {
		this.rootTag = null;
		this.startTagStack.clear();
		this.tagValueStack.clear();
		this.environmentIDSet.clear();
		this.mapperFileList.clear();		
		this.currentEnvironmentID = null;
		
		try {
			saxParser.reset();
		} catch (UnsupportedOperationException e) {
			log.warn("sax 파서가 reset() 메소드를 미 지원하므로 과거 파싱 잔재를 지워 초기 상태로 만들기 위해서 기존 sax 파서를 버리고 새로운 sax 파서 인스턴스로 대체한다.", e);
			
			saxParser  = getNewInstanceOfSAXParser();
		}	
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		String startTag = qName.toLowerCase();

		String prevTag = "";
		
		if (null == rootTag) {
			rootTag = startTag;
			if (!rootTag.equals(CommonStaticFinalVars.MYBATIS_CONFIG_XML_FILE_ROOT_TAG)) {
				/**
				 * this code is dead code and defensive code 
				 * because if xsl engine meets unknown root element then throws exception.
				 */
				String errorMessage = new StringBuilder("this xml file's root tag[")
				.append(rootTag)
				.append("] is differnet from sinnori message information xml file's root tag[")
				.append(CommonStaticFinalVars.MYBATIS_CONFIG_XML_FILE_ROOT_TAG)
				.append("]").toString();
				throw new SAXException(errorMessage);
			}
		}
		
		if (!startTagStack.empty()) {
			prevTag = startTagStack.peek();
		}
		
		startTagStack.push(startTag);
		
		if (startTag.equals("typeAliases")) {
			/*String aliasValue = attributes.getValue("alias");
			String typeValue = attributes.getValue("type");
			
			if (null == aliasValue) {
				String errorMessage = "the typeAlias tag doesn't have the attribute 'alias'";
				throw new SAXException(errorMessage);	
			}
			
			if (null == typeValue) {
				String errorMessage = "the typeAlias tag doesn't have the attribute 'type'";
				throw new SAXException(errorMessage);	
			}
			
			alias2TypeHash.put(aliasValue, typeValue);*/
			/**
			 * 타입에 대한 짧은 별명을 주는것은 별명을 준 당사자한테만 명확할뿐이며 
			 * 타인 입장에서는 한단계 더 추적해야 하는 비용을 지불해야 합니다. 
			 * 하여 이는 유지보수 관점에서 이는 지양해야 합니다.
			 * 신놀이 설계 철학상 맞지 않아 강제적으로 지원하지 않도록 합니다.  
			 */
			String errorMessage = "don't use the typeAliases tag";
			throw new SAXException(errorMessage);
		} else if (startTag.equals("environment")) {
			String idValue = attributes.getValue("id");
			if (null == idValue) {
				String errorMessage = "the environment tag doesn't have the attribute 'id'";
				throw new SAXException(errorMessage);	
			}
			
			currentEnvironmentID = idValue;
			
			SinnoriConfiguration sinnoriConfiguration = 
					SinnoriConfigurationManager.getInstance()
					.getSinnoriRunningProjectConfiguration();
			
			AllDBCPPartConfiguration allDBCPPart = sinnoriConfiguration.getAllDBCPPartConfiguration();
			
			boolean whetherEnviromentExistInDBCPList = allDBCPPart.getDBCPNameList().contains(currentEnvironmentID);
			
			
			// boolean isEnvironment = environmentSet.contains(currentEnvironmentID);			
			
			if (!whetherEnviromentExistInDBCPList) {
				String errorMessage = new StringBuilder("the environment tag's id[").append(currentEnvironmentID)
						.append("] doesn't exist in DBCP name list of Sinnori configration").toString();
				throw new SAXException(errorMessage);	
			}
			
			environmentIDSet.add(currentEnvironmentID);
			
		} else if (startTag.equals("dataSource")) {
			String typeValue = attributes.getValue("type");
			if (null == typeValue) {
				String errorMessage = "the typeAlias tag doesn't have the attribute 'type'";
				throw new SAXException(errorMessage);	
			}
			
			/*String realType = alias2TypeHash.get(typeValue);
			if (null != realType) {
				typeValue = realType;
			}*/
			
			Class<?> dataSourceClass = null;
			try {
				 dataSourceClass = Class.forName(typeValue);
				
			} catch (Exception e) {
				String errorMessage = new StringBuilder("the environment[")
						.append(currentEnvironmentID)
						.append("] resource class[")
						.append(typeValue)
						.append("] cann't be found").toString();
				
				log.warn(errorMessage, e);
						throw new SAXException(errorMessage);
			}

			Object resultObj = null;
			try {
				resultObj = dataSourceClass.newInstance();
			} catch (Exception e) {
				String errorMessage = new StringBuilder("the environment[")
						.append(currentEnvironmentID)
						.append("] resource class[")
						.append(typeValue)
						.append("] failed to create new instance").toString();
				
				log.warn(errorMessage, e);
						throw new SAXException(errorMessage);
			} 

			if (!(resultObj instanceof AbstractUnpooledDataSourceFactory)) {
				String errorMessage = new StringBuilder("the environment[")
						.append(currentEnvironmentID)
						.append("] resource class[")
						.append(typeValue)
						.append("] is not a AbstractUnpooledDataSourceFactory class instance").toString();
				
				log.warn(errorMessage);
						throw new SAXException(errorMessage);
			}
			
			AbstractUnpooledDataSourceFactory dataSourceFactory = (AbstractUnpooledDataSourceFactory)resultObj;
			String dbcpConnectionPoolName = dataSourceFactory.getDBCPConnectionPoolName();
			
			if (! currentEnvironmentID.equals(dbcpConnectionPoolName)) {
				String errorMessage = new StringBuilder("the environment[")
						.append(currentEnvironmentID)
						.append("] resource class[")
						.append(typeValue)
						.append("]'s dbcp connection pool name[")
						.append(dbcpConnectionPoolName)
						.append("is not same to the enviroment id").toString();
				
				log.warn(errorMessage);
						throw new SAXException(errorMessage);
			}
		} else if (startTag.equals("mapper")) {
			String mapperResourceValue = attributes.getValue("resource");
			String mapperURLValue = attributes.getValue("url");
			String classValue = attributes.getValue("class");
			
			SinnoriConfiguration sinnoriConfiguration = 
					SinnoriConfigurationManager.getInstance()
					.getSinnoriRunningProjectConfiguration();
			
			String mainProjectName = sinnoriConfiguration.getMainProjectName();
			String sinnoriInstalledPathString = sinnoriConfiguration.getSinnoriInstalledPathString();
			
			if (null != classValue) {
				return;
			}
			
			if (null != mapperURLValue) {
				URL mapperURL = null;
				try {
					mapperURL = new URL(mapperURLValue);
				} catch(Exception e) {
					String errorMessage = new StringBuilder("the url type mapper[")
							.append(mapperURLValue)
							.append("] failed to convert to URL, errormessage=")
							.append(e.getMessage()).toString();
					
					log.warn(errorMessage, e);
							throw new SAXException(errorMessage);
				}
				
				
				String protocol =  mapperURL.getProtocol();
				if (!protocol.toLowerCase().equals("file")) return;
				
				URI mapperURI = null;
				try {
					mapperURI = mapperURL.toURI();
				} catch (Exception e) {
					String errorMessage = new StringBuilder("the url type mapper[")
							.append(mapperURLValue)
							.append("]'s protocol is file but  it failed to convert to URI, errormessage=")
							.append(e.getMessage()).toString();
					
					log.warn(errorMessage, e);
							throw new SAXException(errorMessage);
				}
				
				File mapperURLFile = null;
				try {
					mapperURLFile = new File(mapperURI);
				} catch(Exception e) {
					return;
				}
				
				if (! mapperURLFile.exists()) {
					String errorMessage = new StringBuilder("the file converted the url[")
							.append(mapperURLValue)
							.append("] whose protocol is file doesn't exist").toString();
					log.warn(errorMessage);
							throw new SAXException(errorMessage);
				}
				
				if (! mapperURLFile.isFile()) {
					String errorMessage = new StringBuilder("the file converted the url[")							
							.append(mapperURLValue)
							.append("] whose protocol is file isn't a regular file").toString();
					
					log.warn(errorMessage);
							throw new SAXException(errorMessage);
				}
				
				mapperFileList.add(mapperURLFile);
				return;
			}
			
			if (null != mapperResourceValue) {
				String mainProjectResourcesPathString = BuildSystemPathSupporter.getProjectResourcesPathString(sinnoriInstalledPathString, mainProjectName);
				
				String mapperResourceFilePathString = CommonStaticUtil.getFilePathStringFromResourcePathAndRelativePathOfFile(mainProjectResourcesPathString, mapperResourceValue);
				
				// FIXME!
				log.info("mapperResourceFilePathString=[{}]", mapperResourceFilePathString);
				
				File mapperResourceFile  = new File(mapperResourceFilePathString);
				
				if (! mapperResourceFile.exists()) {
					String errorMessage = new StringBuilder("the resource type mapper file[")
							.append(mapperResourceFilePathString)
							.append("] doesn't exist").toString();
					
					log.warn(errorMessage);
							throw new SAXException(errorMessage);
				}
				
				if (! mapperResourceFile.isFile()) {
					String errorMessage = new StringBuilder("the resource type mapper file[")							
							.append(mapperResourceFilePathString)
							.append("] isn't a regular file").toString();
					
					log.warn(errorMessage);
							throw new SAXException(errorMessage);
				}
				
				mapperFileList.add(mapperResourceFile);
				return;
			}			
			
			String errorMessage ="the mapper tag must have one attribute(class, url, resource)";
			log.warn("{}, uri=", errorMessage, uri);
			throw new SAXException(errorMessage);
		} else if (startTag.equals("package")) {
						
			// FIXME!
			log.info("when start tag package, prevTag=[{}]", prevTag);			
			
			/**
			 * mybatis-3-config.dtd 에 따라 package tag 가 사용되는 곳은 3군데입니다.
			 * 첫번째 typeAliases
			 * 두번재 mappers
			 * 마지막 세번째 typeHandlers
			 * 
			 * 유지 보수가 쉬울려면 설정파일에 모든것이 있어야 하는데
			 *  package 로 지정하면 설정은 쉬울지 모르지만
			 *  package 에 있는 소스파일 목록을 살펴야 합니다.
			 * 이는 신놀이 설계 철학상 맞지 않아 강제적으로 지원하지 않도록 합니다.  
			 */
			String errorMessage = "don't use the package tag";
			throw new SAXException(errorMessage);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String tagValue = new String(ch, start, length);

		tagValueStack.push(tagValue);
		
		log.info("the tag on top of the start tag stack[{}], tag value=[{}]", startTagStack.peek(), tagValue);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		String endTag = qName.toLowerCase();

		startTagStack.pop();
		
		// FIXME!
		if (endTag.equals("environments")) {
			SinnoriConfiguration sinnoriConfiguration = 
					SinnoriConfigurationManager.getInstance()
					.getSinnoriRunningProjectConfiguration();
			
			AllDBCPPartConfiguration allDBCPPart = sinnoriConfiguration.getAllDBCPPartConfiguration();
			List<String> dbcpConnectionPoolNameList = allDBCPPart.getDBCPNameList();
			
			int dbcpConnectionPoolNameListSize = dbcpConnectionPoolNameList.size();
			
			int environmentCount = environmentIDSet.size();
			
			if (dbcpConnectionPoolNameListSize != environmentCount) {
				String errorMessage = String.format("the count of mybatis config's environment[%d] is " +
						"not same to Sinnori config's dbcp connection pool name list size[%d]", 
						environmentCount, dbcpConnectionPoolNameListSize);
				throw new SAXException(errorMessage);
			}
		}
	}
	
	@Override
	public void warning(SAXParseException e) throws SAXException {
		StringBuilder messageStringBuilder = new StringBuilder("warning::");		
		messageStringBuilder.append(e.toString());	
		throw new SAXException(messageStringBuilder.toString());
	}	
	

	@Override
	public void error(SAXParseException e) throws SAXException {
		StringBuilder messageStringBuilder = new StringBuilder("error::");
		messageStringBuilder.append(e.toString());		
		throw new SAXException(messageStringBuilder.toString());
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		StringBuilder messageStringBuilder = new StringBuilder("fatalError::");
		messageStringBuilder.append(e.toString());		
		throw new SAXException(messageStringBuilder.toString());
	}
}
