package kr.pe.codda.common.etc;

import java.lang.reflect.ReflectPermission;
import java.security.Permission;

/**
 * <pre>
 * this class is intended to prevent Java reflection permission.
 * 
 * if you want to prevent Java reflection permission
 * then give this class name to the java virtual machine argument 'java.security.manager'
 * ex) -Djava.security.manager=kr.pe.codda.common.etc.CoddaSecurityManger  
 * </pre>
 * 
 * @author Won Jonghoon
 *
 */
public class CoddaSecurityManger extends SecurityManager {
	@Override
    public void checkPermission(Permission perm) {
    	
    	System.out.println(perm.toString());
    	
        if (perm instanceof ReflectPermission) {	                    
            throw new SecurityException();
        }
    }
}
