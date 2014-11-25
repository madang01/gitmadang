import java.applet.*;
import java.awt.*;

public class TestApplet extends Applet {
 
  TextField tf=new TextField(30);

  public void init() {
    this.setLayout(new BorderLayout());
    this.add(tf, BorderLayout.CENTER);
  }

  public void appletMethod(String str) {
    tf.setText(str);
  }

}
