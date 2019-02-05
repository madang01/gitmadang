package codda_common;

import org.junit.Test;

import kr.pe.codda.common.classloader.SimpleClassLoader;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.util.HexUtil;
import kr.pe.codda.impl.message.Empty.Empty;

public class TestClassName {
	
	
	@Test
	public void showClassName() {
		System.out.println(HexUtil.class.getName());
		
		System.out.println(new StringBuilder()
				.append("import ")
				.append(AbstractMessage.class.getName())
				.append(";").toString());
		
		System.out.println(SimpleClassLoader.class.getSimpleName());
		
		System.out.println(Empty.class.getName());
		
		// SimpleClassLoader.class.getSimpleName()
	}
	
	@Test
	public void showErrorMessageString() {
		String classFileName = "kr/pe/codda/impl/message/Empty.class";
		String classFullName = "kr.pe.codda.impl.message.Empty";
		
		String errorMessage = new StringBuilder()
				.append("the class file[")
				.append(classFileName)
				.append("] doesn't exist, ")
				.append(SimpleClassLoader.class.getSimpleName())
				.append(" hashCode=[")
				.append(this.hashCode())
				.append("], classFullName=[")
				.append(classFullName)
				.append("]").toString();
		System.out.println(errorMessage);
	}
}
