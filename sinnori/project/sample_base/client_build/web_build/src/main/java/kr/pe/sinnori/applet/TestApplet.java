package kr.pe.sinnori.applet;
import java.applet.*;
import java.awt.*;

public class TestApplet extends Applet {
 
  /**
	 * 
	 */
	private static final long serialVersionUID = -8976160365849814146L;
	
	TextField tf=new TextField(30);

  public void init() {
    this.setLayout(new BorderLayout());
    this.add(tf, BorderLayout.CENTER);
  }

  public void appletMethod(String str) {
    tf.setText(str);
  }

}
