package javapackage.sigleton;

import kr.pe.sinnori.common.AbstractJunitSupporter;

public class sampleMiddle extends AbstractJunitSupporter {
	public sampleMiddle() {
		new SampleRefSingleton().test();
	}
	
	
	@Override
	protected void finalize() throws Throwable {
		log.info("call sampleMiddle.finalize()");
	}
}
