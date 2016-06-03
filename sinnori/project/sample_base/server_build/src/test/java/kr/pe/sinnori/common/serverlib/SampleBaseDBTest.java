package kr.pe.sinnori.common.serverlib;

import static org.junit.Assert.*;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class SampleBaseDBTest {
	Logger log = LoggerFactory.getLogger(SampleBaseDBTest.class);
	
	private SampleBaseDB sampleBaseDB = null;
	
	@Before
	public void setup() {
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME, 
				"sample_base");
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH, 
				"/home/madang01/gitsinnori/sinnori");
		
		sampleBaseDB = new SampleBaseDB();
	}
	
	
	@Test
	public void executeTest() {
		sampleBaseDB.execute();
	}
	
	@Test
	public void testCreateAllTables() {
		sampleBaseDB.execute(AbstractDBCommand.EXECUTION_MODE.CREATION);
		
	}
	
	@Test
	public void testDropAllTables() {
		sampleBaseDB.execute(AbstractDBCommand.EXECUTION_MODE.DELETION);
	}
	
	@Test
	public void testGetTableNameFromCreatingTableSql_WithSchema() {
		StringBuffer stringBuilder = new StringBuffer();
		stringBuilder.append("CREATE TABLE IF NOT EXISTS `SB_DB`.`OA_MEMBER_TB` (");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  `id` VARCHAR(30) NOT NULL,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  `pwd` VARCHAR(45) NULL,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  `email` VARCHAR(45) NULL,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  `phone` VARCHAR(45) NULL,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  `regdate` DATETIME NULL,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  PRIMARY KEY (`id`))");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("ENGINE = InnoDB");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("DEFAULT CHARACTER SET = utf8");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("COLLATE = utf8_general_ci");
		stringBuilder.append(System.getProperty("line.separator"));
		
		String creatingTableSql = stringBuilder.toString();
		
		
		String tableName = sampleBaseDB.getTableNameFromCreatingTableSql(creatingTableSql, sampleBaseDB.getSchemaName());
		org.junit.Assert.assertThat("the expected value comparison",
				tableName, org.hamcrest.CoreMatchers.equalTo("OA_MEMBER_TB"));
	}
	
	@Test
	public void testGetTableNameFromCreatingTableSql_WithoutSchema() {
		StringBuffer stringBuilder = new StringBuffer();
		stringBuilder.append("CREATE TABLE `OA_MEMBER_TB` (");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  `id` VARCHAR(30) NOT NULL,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  `pwd` VARCHAR(45) NULL,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  `email` VARCHAR(45) NULL,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  `phone` VARCHAR(45) NULL,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  `regdate` DATETIME NULL,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  PRIMARY KEY (`id`))");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("ENGINE = InnoDB");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("DEFAULT CHARACTER SET = utf8");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("COLLATE = utf8_general_ci");
		stringBuilder.append(System.getProperty("line.separator"));
		
		String creatingTableSql = stringBuilder.toString();
		
		
		String tableName = sampleBaseDB.getTableNameFromCreatingTableSql(creatingTableSql, sampleBaseDB.getSchemaName());
		org.junit.Assert.assertThat("the expected value comparison",
				tableName, org.hamcrest.CoreMatchers.equalTo("OA_MEMBER_TB"));
	}
	
	
	@Test
	public void testGetTableNameFromCreatingTableSql_BadSchema() {
		String badSchemaName = "SB_DB2";
		
		StringBuffer stringBuilder = new StringBuffer();
		stringBuilder.append("CREATE TABLE IF NOT EXISTS `");
		stringBuilder.append(badSchemaName);
		stringBuilder.append("`.`OA_MEMBER_TB` (");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  `id` VARCHAR(30) NOT NULL,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  `pwd` VARCHAR(45) NULL,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  `email` VARCHAR(45) NULL,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  `phone` VARCHAR(45) NULL,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  `regdate` DATETIME NULL,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  PRIMARY KEY (`id`))");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("ENGINE = InnoDB");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("DEFAULT CHARACTER SET = utf8");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("COLLATE = utf8_general_ci");
		stringBuilder.append(System.getProperty("line.separator"));
		
		String creatingTableSql = stringBuilder.toString();
		
		try {		
			@SuppressWarnings("unused")
			String tableName = sampleBaseDB.getTableNameFromCreatingTableSql(creatingTableSql, sampleBaseDB.getSchemaName());
			
			fail("The creating talbe sql's schema name["+badSchemaName+"] is different from dbcp connection pool name["+
					sampleBaseDB.getDBCPConnectionPoolName()+"]'s schema name["+
					sampleBaseDB.getSchemaName()+"] but no error");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			if (errorMessage.indexOf("'s schema["+badSchemaName+"] is different from ths dbcp connection pool name") == -1) {
				fail(errorMessage);
			}
		}
	}
	
	@Test
	public void testAddCreatingTableSql_OverTableName() {
		StringBuffer stringBuilder = new StringBuffer();
		stringBuilder.append("CREATE TABLE `OA_MEMBER_TB` (");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  `id` VARCHAR(30) NOT NULL,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  `pwd` VARCHAR(45) NULL,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  `email` VARCHAR(45) NULL,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  `phone` VARCHAR(45) NULL,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  `regdate` DATETIME NULL,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("  PRIMARY KEY (`id`))");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("ENGINE = InnoDB");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("DEFAULT CHARACTER SET = utf8");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("COLLATE = utf8_general_ci");
		stringBuilder.append(System.getProperty("line.separator"));
		
		String creatingTableSql = stringBuilder.toString();
		
		try {
			sampleBaseDB.addCreatingTableSql(creatingTableSql);
			
			fail("The creating talbe sql's table name is over but no error");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();	
			
			// log.info(errorMessage);
			
			if (errorMessage.indexOf("table[OA_MEMBER_TB] has already been created") == -1) {
				fail(errorMessage);
			}
		}
	}
	
	@Test
	public void testGetTableNameFromInsertingSql_WithSchema() {
		StringBuffer stringBuilder = new StringBuffer();
		stringBuilder.append("INSERT INTO `SB_DB`.`SB_BOARD_INFO_TB`");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("(`board_id`, `board_name`, `board_info`)");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("VALUES");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("(");
		stringBuilder.append(CommonType.BOARD_ID.NOTICE.ordinal());
		stringBuilder.append(", '공지 게시판', '공지를 목적으로 하는 관리자 전용 게시판');");
		String insertingSql = stringBuilder.toString();
		String tableName = sampleBaseDB.getTableNameFromInsertingSql(insertingSql);
		
		org.junit.Assert.assertThat("the expected value comparison",
				tableName, org.hamcrest.CoreMatchers.equalTo("SB_BOARD_INFO_TB"));
	}
	
	@Test
	public void testGetTableNameFromInsertingSql_WithoutSchema() {
		StringBuffer stringBuilder = new StringBuffer();
		stringBuilder.append("INSERT INTO `SB_BOARD_INFO_TB`");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("(`board_id`, `board_name`, `board_info`)");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("VALUES");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("(");
		stringBuilder.append(CommonType.BOARD_ID.NOTICE.ordinal());
		stringBuilder.append(", '공지 게시판', '공지를 목적으로 하는 관리자 전용 게시판');");
		String insertingSql = stringBuilder.toString();
		String tableName = sampleBaseDB.getTableNameFromInsertingSql(insertingSql);
		
		org.junit.Assert.assertThat("the expected value comparison",
				tableName, org.hamcrest.CoreMatchers.equalTo("SB_BOARD_INFO_TB"));
	}
	
	@Test
	public void testGetTableNameFromInsertingSql_BadSchema() {
		String badSchemaName = "SB_DB2";
		
		StringBuffer stringBuilder = new StringBuffer();
		stringBuilder.append("INSERT INTO `"+badSchemaName+"`.`SB_BOARD_INFO_TB`");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("(`board_id`, `board_name`, `board_info`)");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("VALUES");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("(");
		stringBuilder.append(CommonType.BOARD_ID.NOTICE.ordinal());
		stringBuilder.append(", '공지 게시판', '공지를 목적으로 하는 관리자 전용 게시판');");
		String insertingSql = stringBuilder.toString();
		
		try {
			@SuppressWarnings("unused")
			String tableName = sampleBaseDB.getTableNameFromInsertingSql(insertingSql);
			fail("The inserting sql's schema name["+badSchemaName+"] is different from dbcp connection pool name["+
					sampleBaseDB.getDBCPConnectionPoolName()+"]'s schema name["+
					sampleBaseDB.getSchemaName()+"] but no error");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			if (errorMessage.indexOf("'s schema["+badSchemaName+"] is different from ths dbcp connection pool name") == -1) {
				fail(errorMessage);
			}
		}
	}
	
	@Test
	public void testAddInsertingSql_BadTableName() {
		StringBuffer stringBuilder = new StringBuffer();
		stringBuilder.append("INSERT INTO `SB_BOARD_INFO_TB2`");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("(`board_id`, `board_name`, `board_info`)");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("VALUES");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("(");
		stringBuilder.append(CommonType.BOARD_ID.NOTICE.ordinal());
		stringBuilder.append(", '공지 게시판', '공지를 목적으로 하는 관리자 전용 게시판');");
		String insertingSql = stringBuilder.toString();
		
		try {
			sampleBaseDB.addInsertingSql(insertingSql);
			fail("The inserting sql's table name is bad but no error");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			// log.info(errorMessage);
			
			if (errorMessage.indexOf("] has a bad table name[SB_BOARD_INFO_TB2] that doesn't exist") == -1) {
				fail(errorMessage);
			}
		}
	}
}
