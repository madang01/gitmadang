/**
 * 
 * 참고 주소 : http://www.rgagnon.com/javadetails/java-0614.html
 */
package kr.pe.codda.common.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

@SuppressWarnings("serial")
public class SortedProperties extends Properties {
	/**
	 * Overrides, called by the store method.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized Enumeration keys() {
	     Enumeration keysEnum = super.keys();
	     Vector keyList = new Vector();
	     while(keysEnum.hasMoreElements()){
	       keyList.add(keysEnum.nextElement());
	     }
	     Collections.sort(keyList);
	     return keyList.elements();
	  }
	  
	  /**
	   * Demo
	   */
	  public static void main(String[] args) throws Exception {
	    // regular Properties
	    Properties p = new Properties();
	    p.put("B", "value B");
	    p.put("C", "value C");
	    p.put("A", "value A");
	    p.put("D", "value D");
	    java.io.FileOutputStream fos = new java.io.FileOutputStream("/temp/p.props");
	    p.store(fos, "regular props");
	    /*
	      #regular props
	      #Thu Jul 31 22:21:51 EDT 2008
	      A=value A
	      D=value D
	      C=value C
	      B=value B
	    */
	    // same data but with sorted Properties
	    SortedProperties sp = new SortedProperties();
	    sp.put("B", "value B");
	    sp.put("C", "value C");
	    sp.put("A", "value A");
	    sp.put("D", "value D");
	    fos = new java.io.FileOutputStream("/temp/sp.props");
	    sp.store(fos, "sorted props");
	    /*
	      #sorted props
	      #Thu Jul 31 22:34:06 EDT 2008
	      A=value A
	      B=value B
	      C=value C
	      D=value D
	    */
	  }
}
