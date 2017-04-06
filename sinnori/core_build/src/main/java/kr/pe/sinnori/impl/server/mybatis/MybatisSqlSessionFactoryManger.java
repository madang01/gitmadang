package kr.pe.sinnori.impl.server.mybatis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDDefiner;
import kr.pe.sinnori.common.config.vo.AllDBCPPartConfiguration;
import kr.pe.sinnori.common.config.vo.AllSubProjectPartConfiguration;
import kr.pe.sinnori.common.config.vo.ProjectPartConfiguration;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.DBCPManager;
import kr.pe.sinnori.common.etc.LastModifiedFileInfo;
import kr.pe.sinnori.common.exception.DBNotReadyException;
import kr.pe.sinnori.common.exception.NotFoundProjectException;
import kr.pe.sinnori.common.util.CommonStaticUtil;
import kr.pe.sinnori.server.ServerProjectManager;
import kr.pe.sinnori.server.classloader.ServerClassLoader;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <pre>
 * MyBatis SqlSessionFactory 관리자
 * 
 * MyBatis 에서 매핑 클래스란 동적 클래스 로딩된 자바 빈즈이다.
 * MyBatis 는 매핑 클래스를 인스턴스화 할때 필요한 클래스 로더를 자신을 인스턴스한 클래스 로더부터 시스템 클래스 로더까지 훝어서 찾는다.
 * 
 * MyBatis 매핑 클래스들을 개발시 빈번하게 수정되어야 하므로 신놀이 동적 클래스 로더를 통해 적재 되어야 하는 대상으로 정의했다.
 * 따라서 이 요건을 만족하기 위해서는 신놀이 동적 클래스 로더안에서 MyBatis SqlSessionFactory 를 인스턴스해서 사용해야
 * 자신을 인스턴스한 클래스로더로 신놀이 동적 클래스 로더가 지정된다.
 * 이렇게 해야 신놀이 동적 클래스 로더에 적재된 매핑 클래스를 MyBatis 에서 사용하므로 
 * "MyBatis SqlSessionFactory 관리자" 는  반듯이 신놀이 동적 클래스 로더에 적재되어야 한다.
 * </pre>
 * 
 * @author Won Jonghoon
 * 
 */
public class MybatisSqlSessionFactoryManger {
	private final Logger log = LoggerFactory
			.getLogger(MybatisSqlSessionFactoryManger.class);

	private ClassLoader serverClassLoader = this.getClass().getClassLoader();

	private Hashtable<String, SqlSessionFactory> connectionPoolName2SqlSessionFactoryHash = new Hashtable<String, SqlSessionFactory>();
	private Hashtable<String, String> alias2typeHash = new Hashtable<String, String>();
	private List<LastModifiedFileInfo> lastModifiedFileInfoList = new ArrayList<LastModifiedFileInfo>();

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 생성자.
	 * 
	 * @throws DBNotReadyException
	 */
	private MybatisSqlSessionFactoryManger() {
		rebuild();
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스
	 */
	private static final class SqlSessionFactoryMangerHolder {
		static final MybatisSqlSessionFactoryManger singleton = new MybatisSqlSessionFactoryManger();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 구현 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static MybatisSqlSessionFactoryManger getInstance() {
		return SqlSessionFactoryMangerHolder.singleton;
	}

	private void rebuild() {
		SinnoriConfiguration sinnoriRunningProjectConfiguration = 
				SinnoriConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();
		
		AllDBCPPartConfiguration allDBCPPart = sinnoriRunningProjectConfiguration.getAllDBCPPartConfiguration();
		AllSubProjectPartConfiguration allSubProjectPart = sinnoriRunningProjectConfiguration
				.getAllSubProjectPartConfiguration();
		ProjectPartConfiguration mainProjetPart = sinnoriRunningProjectConfiguration
				.getMainProjectPartConfiguration();

		// List<String> dbcpConnectionPoolNameList = (List<String>)
		// conf.getResource("dbcp.connection_pool_name_list.value");
		List<String> dbcpConnectionPoolNameList = allDBCPPart.getDBCPNameList();

		String workingProjectName = null;
		if (serverClassLoader instanceof ServerClassLoader) {
			workingProjectName = ((ServerClassLoader) serverClassLoader)
					.getProjectName();
		} else {
			workingProjectName = System
					.getProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME);
		}

		ProjectPartConfiguration workingProjetPartItems = null;		
		
		if (workingProjectName
				.equals(mainProjetPart.getProjectName())) {
			workingProjetPartItems = mainProjetPart;
		} else {
			workingProjetPartItems = allSubProjectPart
					.getSubProjectPartConfiguration(workingProjectName);
			if (null == workingProjetPartItems) {
				log.error("unknown main prject[{}]'s sub project[{}]", 
						CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME, 
						workingProjectName);
				System.exit(1);
			}
		}		
		
		File serverAPPINFPath = null;
		try {
			serverAPPINFPath = ServerProjectManager.getInstance().getRunningMainServerProject().getServerAPPINFPath();
		} catch (NotFoundProjectException e) {
			log.error("");
			System.exit(1);
		}
		
		String serverClassloaderMybatisConfigFileRelativePathString = workingProjetPartItems
				.getServerClassloaderMybatisConfigFileRelativePathString();

		if (serverClassloaderMybatisConfigFileRelativePathString.equals("")) {
			connectionPoolName2SqlSessionFactoryHash.clear();
			alias2typeHash.clear();
			lastModifiedFileInfoList.clear();
			
			String itemKey = null;
			if (workingProjectName.equals(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME)) {
				itemKey = new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_CLASSLOADER_MYBATIS_CONFIG_FILE_RELATIVE_PATH_STRING_ITEMID).toString();
			} else {
				itemKey = new StringBuilder("subproject.")
				.append(workingProjectName)
				.append(".")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_CLASSLOADER_MYBATIS_CONFIG_FILE_RELATIVE_PATH_STRING_ITEMID).toString();
			}
			log.warn(
					"sinnori properties key[{}]'s value is a empty string. that means this project[{}] declared that Mybatis is not used",
					itemKey, workingProjectName
					);
			return;
		}

		String resourcesPathString = new StringBuilder(
				serverAPPINFPath.getAbsolutePath())
				.append(File.separator).append("resources").toString();

		// resources
		String mybatisConfigeFilePathString = CommonStaticUtil
				.getFilePathStringFromResourcePathAndRelativePathOfFile(
						resourcesPathString,
						serverClassloaderMybatisConfigFileRelativePathString);

		File mybatisConfigeFile = new File(mybatisConfigeFilePathString);
		if (!mybatisConfigeFile.exists()) {
			log.error("mybatis config file[{}] does not exist", mybatisConfigeFilePathString);
			System.exit(1);
		}

		connectionPoolName2SqlSessionFactoryHash.clear();
		alias2typeHash.clear();
		lastModifiedFileInfoList.clear();
		{
			LastModifiedFileInfo readFileInfo = new LastModifiedFileInfo(
					mybatisConfigeFile, null);
			lastModifiedFileInfoList.add(readFileInfo);
		}

		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document doc = documentBuilder.parse(mybatisConfigeFile);

			Element rootElement = doc.getDocumentElement();
			rootElement.normalize();

			// FIXME!
			log.info("root element name[{}]", rootElement.getNodeName());

			NodeList mapperNodeList = rootElement
					.getElementsByTagName("mapper");
			int len = mapperNodeList.getLength();

			for (int i = 0; i < len; i++) {
				org.w3c.dom.Node mapperNode = mapperNodeList.item(i);
				// FIXME!
				log.info("mapperNode[{}][{}]", mapperNode.getNodeName(),
						mapperNode.getNodeType());

				if (mapperNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					Element mapperElement = (Element) mapperNode;

					if (mapperElement.getNodeName().equals("mapper")) {
						String mapperResourceValue = mapperElement
								.getAttribute("resource");

						if (null == mapperResourceValue)
							mapperResourceValue = "";
						mapperResourceValue = mapperResourceValue.trim();

						File mapperFile = null;

						if (mapperResourceValue.equals("")) {
							mapperResourceValue = mapperElement
									.getAttribute("url");
							if (null == mapperResourceValue)
								mapperResourceValue = "";
							mapperResourceValue = mapperResourceValue.trim();

							if (mapperResourceValue.equals("")) {
								/** mapper interface class */

								mapperResourceValue = mapperElement
										.getAttribute("class");
								if (null == mapperResourceValue)
									mapperResourceValue = "";
								mapperResourceValue = mapperResourceValue
										.trim();
								if (mapperResourceValue.equals("")) {
									log.warn(
											"bad mybatis config file error::bad mapper resource[{}], "
													+ "you need to choose one of 3 attributes(resource, url, class)",
											mapperResourceValue);
									return;
								}

								try {
									serverClassLoader
											.loadClass(mapperResourceValue);
								} catch (ClassNotFoundException e) {
									log.warn(
											"ClassNotFoundException::mapperResourceValue={}",
											mapperResourceValue);
									return;
								}

								continue;
							} else {
								/** url fully qualified path */
								URL url = null;
								try {
									url = new URL(mapperResourceValue);
								} catch (MalformedURLException e) {
									log.warn(
											"bad mybatis config file error::bad mapper resource[{}], MalformedURLException={}",
											mapperResourceValue, e.getMessage());
									return;
								}
								URI uri = null;
								try {
									uri = url.toURI();
								} catch (URISyntaxException e) {
									log.warn(
											"bad mybatis config file error::bad mapper resource[{}], URISyntaxException={}",
											mapperResourceValue, e.getMessage());
									return;
								}

								mapperFile = new File(uri);
							}
						} else {
							/** classpath relative resource */
							/*
							 * String mapperFilePathString = new StringBuilder(
							 * classLoaderAPPINFPathString)
							 * .append(File.separator) .append("resources")
							 * .append(File.separator)
							 * .append(mapperResourceValue.replaceAll("/",
							 * File.separator)).toString();
							 * 
							 * mapperFile = new File(mapperFilePathString);
							 */

							try {
								URL mapperFileURL = serverClassLoader
										.getResource(mapperResourceValue);

								if (null == mapperFileURL) {
									log.warn(new StringBuilder(
											"the mapper file resource value[")
											.append(mapperResourceValue)
											.append("] cannot be found")
											.toString());
									return;
								}

								mapperFile = new File(mapperFileURL.toURI());
							} catch (Exception e) {
								log.warn(
										new StringBuilder(
												"the mapper file resource value[")
												.append(mapperResourceValue)
												.append("] fail to convert to file")
												.toString(), e);
								return;
							}
						}

						if (!mapperFile.exists()) {
							log.warn(
									"bad mybatis config file error::bad mapper resource[{}] file[{}] is not found",
									mapperResourceValue,
									mapperFile.getAbsolutePath());
							return;
						}

						if (!mapperFile.isFile()) {
							log.warn(
									"bad mybatis config file error::bad mapper resource[{}] file[{}] is not a normal file",
									mapperResourceValue,
									mapperFile.getAbsolutePath());
							return;
						}

						if (!mapperFile.canRead()) {
							log.warn(
									"bad mybatis config file error::bad mapper resource[{}] file[{}] cannot be read",
									mapperResourceValue,
									mapperFile.getAbsolutePath());
							return;
						}

						if (!mapperFile.canWrite()) {
							log.warn(
									"bad mybatis config file error::bad mapper resource[{}] file[{}] cannot be written",
									mapperResourceValue,
									mapperFile.getAbsolutePath());
							return;
						}

						LastModifiedFileInfo readFileInfo = new LastModifiedFileInfo(
								mapperFile, null);

						lastModifiedFileInfoList.add(readFileInfo);

						/**
						 * FIXME! 파일 수정 여부를 판단할 수 있는 정보를 담은 객체
						 * 목록(lastModifiedFileInfoList)에 매핑 파일을 추가한다. mybatis 재
						 * 로딩 여부는 파일 수정 여부를 판단할 수 있는 정보를 담은 객체 목록에 등록된 파일들의 수정
						 * 여부로 판단한다.
						 */
						log.info("변경 여부 점검할 매핑 파일 추가, 매핑 파일 경로={}",
								mapperFile.getAbsolutePath());
					}
				}
			}

			NodeList typeAliasNodeList = rootElement
					.getElementsByTagName("typeAlias");
			len = typeAliasNodeList.getLength();
			for (int i = 0; i < len; i++) {
				org.w3c.dom.Node typeAliasNode = typeAliasNodeList.item(i);
				if (typeAliasNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					Element typeAliaseElement = (Element) typeAliasNode;

					String alias = typeAliaseElement.getAttribute("alias");
					String type = typeAliaseElement.getAttribute("type");

					// FIXME!
					log.info("node name={}, alias={}, type={}",
							typeAliasNode.getNodeName(), alias, type);

					alias2typeHash.put(alias, type);
				}
			}

			NodeList environmentNodeList = rootElement
					.getElementsByTagName("environment");
			len = environmentNodeList.getLength();
			for (int i = 0; i < len; i++) {
				org.w3c.dom.Node environmentNode = environmentNodeList.item(i);
				if (environmentNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					Element environmentElement = (Element) environmentNode;

					String environmentID = environmentElement
							.getAttribute("id");

					// FIXME!
					log.info("node name={}, environmentID={}",
							environmentNode.getNodeName(), environmentID);

					if (!dbcpConnectionPoolNameList.contains(environmentID)) {
						log.warn(
								"bad mybatis config file error::environment id[{}] is not same to dbcp conneection pool name[{}]",
								environmentID,
								dbcpConnectionPoolNameList.toString());
						return;
					}

					NodeList dataSourceNodeList = environmentElement
							.getElementsByTagName("dataSource");

					Element dataSourceElement = (Element) dataSourceNodeList
							.item(0);

					String dataSourceClassName = dataSourceElement
							.getAttribute("type");
					String type = alias2typeHash.get(dataSourceClassName);
					if (null != type) {
						// FIXME!
						log.info(
								"environmentID[{}]'s dataSourceClassName[{}] is alias so change to value[{}]",
								environmentID, dataSourceClassName, type);

						dataSourceClassName = type;
					}
					Class<?> dataSourceClass = null;
					try {
						// dataSourceClass = Class.forName(dataSourceClassName);
						dataSourceClass = serverClassLoader
								.loadClass(dataSourceClassName);
					} catch (ClassNotFoundException e1) {
						log.warn(
								"bad mybatis config file error::environmentID[{}]'s data source class not found[{}]",
								environmentID, dataSourceClassName);
						return;
					}

					Object resultObj = null;
					try {
						resultObj = dataSourceClass.newInstance();
					} catch (InstantiationException e1) {
						log.warn(
								"bad mybatis config file error::fail to get environmentID[{}]'s data source class[{}] instance",
								environmentID, dataSourceClassName);
						return;
					} catch (IllegalAccessException e1) {
						log.warn(
								"bad mybatis config file error::fail to get environmentID[{}]'s data source class[{}] "
										+ "instance because of illegal access",
								environmentID, dataSourceClassName);
						return;
					}

					if (!(resultObj instanceof UnpooledDataSourceFactory)) {
						log.warn(
								"bad mybatis config file error::environmentID[{}]'s data source class[{}] is not a type of UnpooledDataSourceFactory class",
								environmentID, dataSourceClassName);
						return;
					}

					UnpooledDataSourceFactory unpooledDataSourceFactory = (UnpooledDataSourceFactory) resultObj;

					String dbcpConnectionPoolName = DBCPManager.getInstance()
							.getDBCPConnectionPoolName(
									unpooledDataSourceFactory.getDataSource());
					if (null == dbcpConnectionPoolName) {
						log.warn(
								"bad mybatis config file error::unknown environmentID[{}]'s data soruce",
								environmentID);
						return;
					}

					if (!dbcpConnectionPoolName.equals(environmentID)) {
						log.warn(
								"bad mybatis config file error::environmentID[{}]'s data soruce's dbcp connection pool name[{}] is bad, "
										+ "the environment name must be a dbcp connection pool name",
								environmentID, dbcpConnectionPoolName);
						return;
					}

					SqlSessionFactory sqlSessionFactory = null;

					InputStream is = null;
					try {
						is = new FileInputStream(mybatisConfigeFile);

						try {
							sqlSessionFactory = new SqlSessionFactoryBuilder()
									.build(is, environmentID);
						} catch (Exception e) {
							e.printStackTrace();
							log.warn(
									"fail to get a SqlSessionFactory object from mybatis config file[{}] having environment[{}]",
									mybatisConfigeFile.getAbsolutePath(),
									environmentID);
							return;
						}
					} catch (Exception e) {
						log.warn(
								"mybatis config file[{}] fail to get the object of FileInputStream class",
								mybatisConfigeFile.getAbsolutePath());
						return;
					} finally {
						if (null != is) {
							try {
								is.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

					// FIXME!
					log.info("environmentID[{}] SqlSessionFactory registered",
							environmentID);

					// SqlSessionFactoryInfo sqlSessionFactoryInfo = new
					// SqlSessionFactoryInfo(sqlSessionFactory, type);
					connectionPoolName2SqlSessionFactoryHash.put(environmentID,
							sqlSessionFactory);
				}
			}
		} catch (ParserConfigurationException e1) {
			log.warn("ParserConfigurationException::mybatis config file[{}]",
					mybatisConfigeFile.getAbsolutePath());
		} catch (SAXException e) {
			log.warn("SAXException::mybatis config file[{}]",
					mybatisConfigeFile.getAbsolutePath());
		} catch (IOException e) {
			log.warn("IOException::mybatis config file[{}]",
					mybatisConfigeFile.getAbsolutePath());
			log.warn("IOException", e);
		}
	}

	/**
	 * SqlSessionFactory 객체를 반환한다.
	 * 
	 * @param connectionPoolName
	 * @return
	 * @throws DBNotReadyException
	 */
	public SqlSessionFactory getSqlSessionFactory(String connectionPoolName)
			throws DBNotReadyException {
		synchronized (connectionPoolName2SqlSessionFactoryHash) {
			SqlSessionFactory sqlSessionFactory = connectionPoolName2SqlSessionFactoryHash
					.get(connectionPoolName);
			if (null == sqlSessionFactory) {
				String errorMessage = String.format(
						"dbcp connection pool[%s] is not found",
						connectionPoolName);
				log.warn(errorMessage);
				throw new DBNotReadyException(errorMessage);
			}

			if (isModified()) {
				rebuild();
				sqlSessionFactory = connectionPoolName2SqlSessionFactoryHash
						.get(connectionPoolName);
				if (null == sqlSessionFactory) {
					String errorMessage = String.format(
							"dbcp connection pool[%s] is not found",
							connectionPoolName);
					log.warn(errorMessage);
					throw new DBNotReadyException(errorMessage);
				}
			}

			return sqlSessionFactory;
		}
	}

	private boolean isModified() {
		for (LastModifiedFileInfo readFileInfo : lastModifiedFileInfoList) {
			return readFileInfo.isModified();
		}
		return false;
	}
}
