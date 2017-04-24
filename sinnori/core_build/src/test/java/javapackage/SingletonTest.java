package javapackage;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class SingletonTest {
	private static final class SingletonTestHolder {
		static final SingletonTest singleton = new SingletonTest();
	}

	public static SingletonTest getInstance() {
		return SingletonTestHolder.singleton;
	}
	private SingletonTest() {
		Logger.getGlobal().log(Level.INFO, "call SingletonTest 생성자2");
	}
	
	@Override
	protected void finalize() throws Throwable {
		Logger.getGlobal().log(Level.INFO, "call SingletonTest.finalize()");
	}
	
	public void destroy() {
		Logger.getGlobal().log(Level.INFO, "call SingletonTest.destroy()");
    }


}
