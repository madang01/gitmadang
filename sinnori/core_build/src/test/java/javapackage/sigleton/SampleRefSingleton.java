package javapackage.sigleton;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SampleRefSingleton {

	// private SampleSingleton sampleSingleton = SampleSingleton.getInstance();
	
	public void test() {
		SampleSingleton.getInstance();
	}
	
	@Override
	protected void finalize() throws Throwable {
		Logger.getGlobal().log(Level.INFO, "call SampleRefSingleton.finalize()");
	}
}
