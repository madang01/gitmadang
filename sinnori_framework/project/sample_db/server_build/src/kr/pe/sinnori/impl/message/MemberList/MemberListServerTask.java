package kr.pe.sinnori.impl.message.MemberList;

import java.util.List;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.MemberListResult.MemberListResult;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.SinnoriSqlSessionFactoryIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

import org.apache.ibatis.session.SqlSession;

public class MemberListServerTask extends AbstractServerTask {
	
	
	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager,
			SinnoriSqlSessionFactoryIF sqlSessionFactory,
			LetterSender letterSender,
			AbstractMessage messageFromClient) throws Exception {
		// FIXME!
		// log.info(messageFromClient.toString());
		log.info("serverProjectConfig.getMybatisConfigFileName={}", serverProjectConfig.getMybatisConfigFileName());		
		
		SqlSession session = sqlSessionFactory.openSession();
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
