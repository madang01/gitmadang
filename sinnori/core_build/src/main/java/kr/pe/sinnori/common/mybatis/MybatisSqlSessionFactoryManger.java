package kr.pe.sinnori.common.mybatis;

import java.util.HashMap;

import org.apache.ibatis.session.SqlSessionFactory;

import kr.pe.sinnori.common.classloader.SimpleClassLoader;
import kr.pe.sinnori.common.exception.MybatisException;

/**
 * MyBatis SqlSessionFactory 관리자
 *  
 * @author Won Jonghoon
 * 
 */
public class MybatisSqlSessionFactoryManger {
	// private final Logger log = LoggerFactory.getLogger(MybatisSqlSessionFactoryManger.class);
	
	private final Object monitor = new Object();
	
	private HashMap<SimpleClassLoader, MybatisSqlSession> simpleClassLoader2MybatisSqlSessionHash = new HashMap<SimpleClassLoader, MybatisSqlSession>();

	

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 생성자.
	 * 
	 * @throws MybatisException
	 */
	private MybatisSqlSessionFactoryManger() {
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
	public SqlSessionFactory getSqlSessionFactory(final ClassLoader targetClassLoader, String enviromentID)
			throws MybatisException {
		if (null == targetClassLoader) {
			throw new IllegalArgumentException("the parameter targetClassLoader is null");
		}
		
		if (! (targetClassLoader instanceof SimpleClassLoader)) {
			throw new IllegalArgumentException("the parameter targetClassLoader is not a instance of SimpleClassLoader class");
		}
		
		
		
		if (null == enviromentID) {
			throw new IllegalArgumentException("the parameter enviromentID is null");
		}
		
		SimpleClassLoader simpleClassLoader = (SimpleClassLoader)targetClassLoader;
		
		SqlSessionFactory sqlSessionFactory = null;
		synchronized (monitor) {
			MybatisSqlSession mybatisSqlSession = simpleClassLoader2MybatisSqlSessionHash.get(simpleClassLoader);
			
			if (null == mybatisSqlSession) {
				
				mybatisSqlSession = new MybatisSqlSession(simpleClassLoader);
				simpleClassLoader2MybatisSqlSessionHash.put(simpleClassLoader, mybatisSqlSession);
			}
			
			sqlSessionFactory = mybatisSqlSession.getSqlSessionFactory(enviromentID);
		}
		
		return sqlSessionFactory;
	}
}
