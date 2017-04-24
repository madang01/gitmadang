package javapackage.sigleton;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SingletonTestMain {
	
	public void call(int id) {
		Logger.getGlobal().log(Level.INFO, "call ["+id+"] sampleMiddle");
		new sampleMiddle();
	}

	public static void main(String[] args) {
		
		SingletonTestMain singletonTestMain = new SingletonTestMain();
		
		singletonTestMain.call(1);
		System.gc();
		
		singletonTestMain.call(2);
		System.gc();
		
		
	}

}
