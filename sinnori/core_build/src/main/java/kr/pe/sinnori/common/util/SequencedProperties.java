/**
 * 출처 : http://stackoverflow.com/questions/3619796/how-to-read-a-properties-file-in-java-in-the-original-order
 * 저자 : Wayne Johnson
 * 참고 : stackoverflow 사이트에서 "Wayne Johnson" 님 일시 Oct 24 '12 at 15:08 에 답글
 */
package kr.pe.sinnori.common.util;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings("serial")
public class SequencedProperties extends Properties {

    @SuppressWarnings("rawtypes")
	private List keyList = new ArrayList();

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public synchronized Enumeration keys() {
        return Collections.enumeration(keyList);
    }

    @SuppressWarnings("unchecked")
	@Override
    public synchronized Object put(Object key, Object value) {
        if (! containsKey(key)) {
            keyList.add(key);
        }

        return super.put(key, value);
    }

    @Override
    public synchronized Object remove(Object key) {
        keyList.remove(key);

        return super.remove(key);
    }

    @SuppressWarnings("unchecked")
	@Override
    public synchronized void putAll(@SuppressWarnings("rawtypes") Map values) {
        for (Object key : values.keySet()) {
            if (! containsKey(key)) {
                keyList.add(key);
            }
        }

        super.putAll(values);
    }
    
    @Override
    public void store(Writer writer, String title) throws IOException {
    	writer.append("# ");
    	writer.append(title);
    	writer.append(System.getProperty("line.separator"));
    	
    	writer.append("# ");
    	writer.append(new java.util.Date().toString());
    	writer.append(System.getProperty("line.separator"));
    	
    	for (Object key : keyList) {
    		writer.append((String)key);
    		writer.append("=");
    		String tempValue = getProperty((String)key);
    		writer.append(tempValue.replaceAll("\\\\", "\\\\\\\\"));
    		writer.append(System.getProperty("line.separator"));
    	}
    	
    	writer.close();
    }
}