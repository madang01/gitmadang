package javapackage.sigleton;

import kr.pe.sinnori.common.AbstractJunitSupporter;

public class SampleRefSingleton extends AbstractJunitSupporter {

	// private SampleSingleton sampleSingleton = SampleSingleton.getInstance();
	
	public void test() {
		SampleSingleton.getInstance();
	}
	
	@Override
	protected void finalize() throws Throwable {
		log.info("call SampleRefSingleton.finalize()");
	}
}
