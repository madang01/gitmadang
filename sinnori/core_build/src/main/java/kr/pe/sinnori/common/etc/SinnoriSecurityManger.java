package kr.pe.sinnori.common.etc;

import java.lang.reflect.ReflectPermission;
import java.security.Permission;

public class SinnoriSecurityManger extends SecurityManager {
	@Override
    public void checkPermission(Permission perm) {
    	
    	System.out.println(perm.toString());
    	
        if (perm instanceof ReflectPermission) {	                    
            throw new SecurityException();
        }
    }
}
