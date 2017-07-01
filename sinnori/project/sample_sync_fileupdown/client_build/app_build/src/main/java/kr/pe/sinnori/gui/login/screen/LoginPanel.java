/*
 * Created by JFormDesigner on Sun Jun 18 22:17:42 KST 2017
 */

package kr.pe.sinnori.gui.login.screen;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import kr.pe.sinnori.applib.sessionkey.RSAPublickeyGetterBuilder;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.sessionkey.ClientSessionKeyIF;
import kr.pe.sinnori.common.sessionkey.ClientSessionKeyManager;
import kr.pe.sinnori.common.util.CommonStaticUtil;
import kr.pe.sinnori.gui.helper.ScreenManagerIF;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public final class LoginPanel extends JPanel {
	private Logger log = LoggerFactory
			.getLogger(LoginPanel.class);
	
	
	private Frame mainFrame = null;
	private ScreenManagerIF screenManagerIF = null;
	
	public LoginPanel(Frame mainFrame, ScreenManagerIF screenManagerIF) {
		super();
		this.mainFrame = mainFrame;
		this.screenManagerIF = screenManagerIF;
		initComponents();
	}
	
	private void showMessageDialog(JComponent component, String message) {
		JOptionPane.showMessageDialog(mainFrame,
				CommonStaticUtil.splitString(message, CommonType.LINE_SEPARATOR_GUBUN.NEWLINE, 100));
		component.requestFocusInWindow();
	}

	private void okButtonActionPerformed(ActionEvent e) {
		// TODO add your code here
		String hostText = null;
		
		 try {
			 hostText = hostTextField.getText();
		 } catch(NullPointerException e1) {
			 log.warn(e1.getMessage(), e1);
			 showMessageDialog(hostTextField, "the host address value is null");
				return;
		 }
		 
		 hostText = hostText.trim();
		 hostTextField.setText(hostText);
		 if (0 == hostText.length()) {
			 showMessageDialog(hostTextField, "the host address value is empty");
				return;
		 }		 
		 
		String portText = null;
		
		try {
			portText = portTextField.getText();
		 } catch(NullPointerException e1) {
			 log.warn(e1.getMessage(), e1);
			 showMessageDialog(portTextField, "the port value is null");
				return;
		 }
		
		portText = portText.trim();
		portTextField.setText(portText);
		 if (0 == portText.length()) {
			 showMessageDialog(portTextField, "the port value is empty");
				return;
		 }
		 
		 int port = -1;
		try {
			port = Integer.parseInt(portText);
		} catch (NumberFormatException nfe) {
			showMessageDialog(portTextField, String.format("the port value[%s] is not a integer", portText));
			return;
		}
		
		String idText = null;
		try {
			idText = idTextField.getText();
		 } catch(NullPointerException e1) {
			 log.warn(e1.getMessage(), e1);
			 showMessageDialog(idTextField, "the id value is null");
				return;
		 }
		
		idText = idText.trim();
		idTextField.setText(idText);
		 if (0 == portText.length()) {
			 showMessageDialog(idTextField, "the port value is empty");
				return;
		 }
		 
		
		char[] pwdChars = null;
		try {
			pwdChars = pwdPasswordField.getPassword();
		} catch(NullPointerException e1) {
			 log.warn(e1.getMessage(), e1);
			 showMessageDialog(pwdPasswordField, "the password value is null");
				return;
		 }		
		
		 if (0 == pwdChars.length) {
			 showMessageDialog(pwdPasswordField, "the password value is empty");
				return;
		 }
		
		
		
		
		CharBuffer pwdCharBuffer = CharBuffer.wrap(pwdChars);		
		ByteBuffer pwdByteBuffer = CommonStaticFinalVars.SINNORI_PASSWORD_CHARSET.encode(pwdCharBuffer);		 
		byte[] pwdBytes = pwdByteBuffer.array();
		
		
		

		ClientSessionKeyManager clientSessionKeyManager = ClientSessionKeyManager.getInstance();
		ClientSessionKeyIF mainProjectClientSessionKey = null;
		try {
			mainProjectClientSessionKey = clientSessionKeyManager.getMainProjectClientSessionKey(RSAPublickeyGetterBuilder.build());
		} catch (SymmetricException e1) {
			log.warn(e1.getMessage(), e1);
			showMessageDialog(hostTextField, String.format("It failed to get the server[%s][%d]' RSA public key bytes, errmsg=%s", hostText, port, e1.getMessage()));
			return;
		}
		
		mainProjectClientSessionKey.getClientSymmetricKey();
		mainProjectClientSessionKey.getDupSessionKeyBytes();
		mainProjectClientSessionKey.getDupIVBytes();
		
		
		
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		ipPanel = new JPanel();
		hostLabel = new JLabel();
		hostTextField = new JTextField();
		portPanel = new JPanel();
		portLabel = new JLabel();
		portTextField = new JTextField();
		idPanel = new JPanel();
		idLabel = new JLabel();
		idTextField = new JTextField();
		pwdPanel = new JPanel();
		pwdLabel = new JLabel();
		pwdPasswordField = new JPasswordField();
		bottomMenuPanel = new JPanel();
		okButton = new JButton();

		//======== this ========
		setMinimumSize(new Dimension(300, 158));
		setPreferredSize(new Dimension(300, 158));
		setLayout(new FormLayout(
			"$ugap, [120dlu,pref]:grow, $ugap",
			"5*($lgap, default), $lgap"));

		//======== ipPanel ========
		{
			ipPanel.setLayout(new FormLayout(
				"default, $ugap, ${growing-button}",
				"default"));

			//---- hostLabel ----
			hostLabel.setText("host address");
			ipPanel.add(hostLabel, CC.xy(1, 1));
			ipPanel.add(hostTextField, CC.xy(3, 1));
		}
		add(ipPanel, CC.xy(2, 2));

		//======== portPanel ========
		{
			portPanel.setLayout(new FormLayout(
				"42dlu, $ugap, ${growing-button}",
				"default"));

			//---- portLabel ----
			portLabel.setText("port");
			portPanel.add(portLabel, CC.xy(1, 1));
			portPanel.add(portTextField, CC.xy(3, 1));
		}
		add(portPanel, CC.xy(2, 4));

		//======== idPanel ========
		{
			idPanel.setLayout(new FormLayout(
				"42dlu, $ugap, ${growing-button}",
				"default"));

			//---- idLabel ----
			idLabel.setText("ID");
			idPanel.add(idLabel, CC.xywh(1, 1, 3, 1));
			idPanel.add(idTextField, CC.xy(3, 1));
		}
		add(idPanel, CC.xy(2, 6));

		//======== pwdPanel ========
		{
			pwdPanel.setLayout(new FormLayout(
				"42dlu, $ugap, ${growing-button}",
				"default"));

			//---- pwdLabel ----
			pwdLabel.setText("password");
			pwdPanel.add(pwdLabel, CC.xy(1, 1));
			pwdPanel.add(pwdPasswordField, CC.xy(3, 1));
		}
		add(pwdPanel, CC.xy(2, 8));

		//======== bottomMenuPanel ========
		{
			bottomMenuPanel.setLayout(new FlowLayout());

			//---- okButton ----
			okButton.setText("OK");
			okButton.addActionListener(e -> okButtonActionPerformed(e));
			bottomMenuPanel.add(okButton);
		}
		add(bottomMenuPanel, CC.xy(2, 10));
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel ipPanel;
	private JLabel hostLabel;
	private JTextField hostTextField;
	private JPanel portPanel;
	private JLabel portLabel;
	private JTextField portTextField;
	private JPanel idPanel;
	private JLabel idLabel;
	private JTextField idTextField;
	private JPanel pwdPanel;
	private JLabel pwdLabel;
	private JPasswordField pwdPasswordField;
	private JPanel bottomMenuPanel;
	private JButton okButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
