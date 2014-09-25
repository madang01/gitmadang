package kr.pe.sinnori.impl.message.MemberList;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.MemberListResult.MemberListResult;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MemberListServerTask extends AbstractServerTask {
	// private 
	private SqlSessionFactory factory = null;
	
	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager, LetterSender letterSender,
			AbstractMessage messageFromClient) throws Exception {
		// FIXME!
		// log.info(messageFromClient.toString());
		log.info("serverProjectConfig.getMybatisConfigFileName={}", serverProjectConfig.getMybatisConfigFileName());
			
		if (null == factory) {			
			// String res="orm/mybatis/config.xml";
			
			// Resources.setDefaultClassLoader(classLoader);
			
			InputStream is;
			
			/*is = Resources.getResourceAsStream(mybatisConfigFile.getAbsolutePath());
			if (null == is) {
				throw new ServerTaskException("mybatis 설정 파일이 존재하지 않습니다. :: "+serverProjectConfig.getMybatisConfigFileName());
			}*/
			//synchronized (org.apache.ibatis.io.Resources.class) {
				Resources.setDefaultClassLoader(classLoader);
				try {
					is = Resources.getResourceAsStream(serverProjectConfig.getMybatisConfigFileName());
				} catch(IOException e) {
					throw new ServerTaskException("mybatis 설정 파일이 존재하지 않습니다. :: "+serverProjectConfig.getMybatisConfigFileName());
				}						
					
				factory = new SqlSessionFactoryBuilder().build(is); 
			//}
		}
		
		SqlSession session = factory.openSession();
		List<java.util.HashMap<String, Object>> list = null;
        try {
        	list = session.selectList("getHashList");
        } finally {
        	session.close();
        }
                        
        MemberListResult outObj = new MemberListResult();
        
        outObj.setCnt(list.size());        
        
        MemberListResult.Member memberList[] = new MemberListResult.Member[outObj.getCnt()];
        
        for(int i=0; i < memberList.length; i++) {
        	java.util.HashMap<String, Object> memberHash = list.get(i);
        	String id = (String)memberHash.get("id");
        	String pwd = (String)memberHash.get("pwd");
        	String email = (String)memberHash.get("email");
        	String phone = (String)memberHash.get("phone");
        	java.sql.Timestamp regdate = (java.sql.Timestamp)memberHash.get("regdate");
        	
        	memberList[i] = outObj.new Member();
        	memberList[i].setId(id);
        	memberList[i].setPwd(pwd);
        	memberList[i].setEmail(email);
        	memberList[i].setPhone(phone);
        	memberList[i].setRegdate(regdate);        	
        }
        
        outObj.setMemberList(memberList);
        
        letterSender.addSyncMessage(outObj);
	}
}
