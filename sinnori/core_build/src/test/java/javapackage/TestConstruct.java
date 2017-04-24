package javapackage;

public class TestConstruct extends AbstractParent {
	public TestConstruct() {
		log.info("call TestConstruct1");
	}
	
	public TestConstruct(int t) {
		log.info("call TestConstruct2");
	}
	
	public static void main(String[] args) {
	
		TestConstruct testConstruct = new TestConstruct(1);
	}
}
