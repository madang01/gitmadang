import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;


@SuppressWarnings("serial")
public class PathSwingAction extends AbstractAction {
	private JFrame mainFrame = null;
	private JTextField targetTextField = null;
	private JFileChooser chooser = null;
	
	public PathSwingAction(JFrame mainFrame, JFileChooser chooser, JTextField targetTextField) {
		this.mainFrame= mainFrame;
		this.chooser = chooser;
		this.targetTextField = targetTextField;
		putValue(NAME, "경로선택");
		putValue(SHORT_DESCRIPTION, "경로 선택 박스");
	}
	public void actionPerformed(ActionEvent e) {
		int returnVal = chooser.showOpenDialog(mainFrame);
		if (JFileChooser.APPROVE_OPTION == returnVal) {
			File selectedFile = chooser.getSelectedFile();
			targetTextField.setText(selectedFile.getAbsolutePath());
		}
	}
}
