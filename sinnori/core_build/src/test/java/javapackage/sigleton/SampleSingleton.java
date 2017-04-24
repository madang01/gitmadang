package javapackage.sigleton;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SampleSingleton {
	private static final class SampleSingletonHolder {
		static final SampleSingleton singleton = new SampleSingleton();
	}

	public static SampleSingleton getInstance() {
		return SampleSingletonHolder.singleton;
	}
	private SampleSingleton() {
		Logger.getGlobal().log(Level.INFO, "call SingletonTest 생성자["+this.hashCode()+"]");
	}
	
	@Override
	protected void finalize() throws Throwable {
		Logger.getGlobal().log(Level.INFO, "call SingletonTest.finalize() ["+this.hashCode()+"]");
	}
	
	public void destroy() {
		Logger.getGlobal().log(Level.INFO, "call SingletonTest.destroy()");
    }
}
