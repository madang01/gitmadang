package kr.pe.codda;

import java.sql.Connection;
import java.sql.DriverManager;

public class JooqDBConectionTest {
	public static void main(String[] args) {
		String userName = "madangsoe01";
	    String password = "test1234";
	    String url = "jdbc:mysql://localhost:3306/sb_db";
	
	    
	    try (Connection conn = DriverManager.getConnection(url, userName, password)) {
	      System.out.println("success");
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.out.println("fail");
	    }
	}
}
