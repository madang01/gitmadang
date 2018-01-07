package junitlib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JunitUtil {
/** 사용법 : 
	public class Test {
		private String concatString(String a, String b) {
	    	return (a+b);
		}	
	}
	
	Test t = new Test();
    String str = (String) genericInvokMethod(t, "concatString", 2, "Hello", "Mr.x");
*/
	
	public static Object genericInvokMethod(Object obj, String methodName, int paramCount, Object... params)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		// Logger log = LoggerFactory.getLogger(JunitUtil.class);

		Method method;
		Object returnedValueObject = null;
		Object[] parameters = new Object[paramCount];
		Class<?>[] classArray = new Class<?>[paramCount];
		for (int i = 0; i < paramCount; i++) {
			parameters[i] = params[i];
			classArray[i] = params[i].getClass();
		}

		method = obj.getClass().getDeclaredMethod(methodName, classArray);
		method.setAccessible(true);
		
		
		returnedValueObject = method.invoke(obj, params);

		return returnedValueObject;
	}
	
	
}
