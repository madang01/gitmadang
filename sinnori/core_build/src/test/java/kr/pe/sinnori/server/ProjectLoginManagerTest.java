package kr.pe.sinnori.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;

public class ProjectLoginManagerTest extends AbstractJunitTest {

	public static class LoginTest extends Thread {
		private ProjectLoginManagerIF projectLoginManager;
		public LoginTest(ProjectLoginManagerIF projectLoginManager) {
			this.projectLoginManager = projectLoginManager;
		}
		
		public void run() {
			Random random = new Random();
			for (int i=0; i < 10; i++) {
				projectLoginManager.isLogin("hello");
				
				try {
					Thread.sleep(random.nextInt(100));
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}
	
	@Test
	public void test_synchronized블락에로그를넣어섞이는지테스트_로그수동추가및삭제필요함() {
		ProjectLoginManagerIF projectLoginManager = new ProjectLoginManager();
		
		List<LoginTest> LoginTestList = new ArrayList<LoginTest>();
		for (int i=0; i < 3; i++) {
			LoginTest LoginTest = new LoginTest(projectLoginManager);
			LoginTestList.add(LoginTest);
			LoginTest.start();
		}
		
		for (LoginTest LoginTest : LoginTestList) {
			try {
				LoginTest.join();
			} catch (InterruptedException e) {
			}
		}
	}
}
