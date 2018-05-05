package kr.pe.codda.common.config;

import java.util.HashSet;
import java.util.Set;






public abstract class AbstractSetTypeNativeValueConverter<E> extends AbstractNativeValueConverter<E> {
	protected Set<String> itemValueSet = new HashSet<String>();
	// protected HashMap<String, E> nativeValueHash = new HashMap<String, E>();
	
	public AbstractSetTypeNativeValueConverter(Class<E> genericTypeClass) {
		super(genericTypeClass);
		initItemValueSet();
	}	
	
	abstract protected void initItemValueSet();
	
	abstract public String getSetName();
	
	
	
	public Set<String> getItemValueSet() {
		return itemValueSet;
	}

	/*public HashMap<String, E> getNativeValueHash() {
		return nativeValueHash;
	}*/

	public String getStringFromSet() {		
		return itemValueSet.toString();
	}
}
