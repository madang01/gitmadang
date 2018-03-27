package kr.pe.sinnori.impl.mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.classloader.SimpleClassLoader;
import kr.pe.sinnori.common.exception.MybatisException;
import kr.pe.sinnori.common.mybatis.MybatisSqlSession;

/**
 * MyBatis SqlSessionFactory 관리자
 *  
 * @author Won Jonghoon
 * 
 */
public class MybatisSqlSessionFactoryManger {
	private final Logger log = LoggerFactory.getLogger(MybatisSqlSessionFactoryManger.class);
	
	// private final Object monitor = new Object();
	
	private MybatisSqlSession mybatisSqlSession = null;	

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 생성자.
	 * 
	 * @throws MybatisException
	 */
	private MybatisSqlSessionFactoryManger() {
		ClassLoader contextClassloader =  MybatisSqlSessionFactoryManger.class.getClassLoader();
		
		if (!(contextClassloader instanceof SimpleClassLoader)) {
			log.error("the var contextClassloader is not a instance of SimpleClassLoader");
			System.exit(1);
		}
		
		synchronized (Resources.class) {
			Resources.setDefaultClassLoader(contextClassloader);
			mybatisSqlSession = new MybatisSqlSession();
		}
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

	
	/**
	 * SqlSessionFactory 객체를 반환한다.
	 * 
	 * @param enviromentID
	 * @return
	 * @throws MybatisException
	 */
	public SqlSessionFactory getSqlSessionFactory(String enviromentID)
			throws MybatisException {
		
		if (null == enviromentID) {
			throw new IllegalArgumentException("the parameter enviromentID is null");
		}
		
		SqlSessionFactory sqlSessionFactory = mybatisSqlSession.getSqlSessionFactory(enviromentID);
		
		return sqlSessionFactory;
	}
}
