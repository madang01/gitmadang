package javapackage.sigleton;

import kr.pe.sinnori.common.AbstractJunitTest;

public class sampleMiddle extends AbstractJunitTest {
	public sampleMiddle() {
		new SampleRefSingleton().test();
	}
	
	
	@Override
	protected void finalize() throws Throwable {
		log.info("call sampleMiddle.finalize()");
	}
}
