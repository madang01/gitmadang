import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class TestCharset {
	public static void main(String[] args) throws IOException {
		String enc = new java.io.OutputStreamWriter(System.out).getEncoding();
	    System.out.println("default encoding = " + enc);

	 

	    File t = File.createTempFile("temp", ".tmp");
	    
	    FileReader fr    = new FileReader(t);
	    System.out.println("FileReader.getEncoding() = "+fr.getEncoding());

	    System.out.println("file.encoding = " + System.getProperty("file.encoding"));
	}

}
