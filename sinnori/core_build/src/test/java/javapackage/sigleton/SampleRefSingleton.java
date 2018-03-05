package javapackage.sigleton;

import kr.pe.sinnori.common.AbstractJunitTest;

public class SampleRefSingleton extends AbstractJunitTest {

	// private SampleSingleton sampleSingleton = SampleSingleton.getInstance();
	
	public void test() {
		SampleSingleton.getInstance();
	}
	
	@Override
	protected void finalize() throws Throwable {
		log.info("call SampleRefSingleton.finalize()");
	}
}
