package javapackage.sigleton;

import java.util.logging.Level;
import java.util.logging.Logger;

public class sampleMiddle {
	public sampleMiddle() {
		new SampleRefSingleton().test();
	}
	
	
	@Override
	protected void finalize() throws Throwable {
		Logger.getGlobal().log(Level.INFO, "call sampleMiddle.finalize()");
	}
}
