/*
 * Created by JFormDesigner on Sun Jun 18 22:17:42 KST 2017
 */

package kr.pe.sinnori.gui.syncfileupdown.screen;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Arrays;

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

import kr.pe.sinnori.applib.MainProejctSyncConnectionManager;
import kr.pe.sinnori.applib.ValueChecker;
import kr.pe.sinnori.applib.sessionkey.RSAPublickeyGetterBuilder;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.sessionkey.ClientSessionKeyIF;
import kr.pe.sinnori.common.sessionkey.ClientSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.ClientSymmetricKeyIF;
import kr.pe.sinnori.common.util.CommonStaticUtil;
import kr.pe.sinnori.common.util.PasswordWrapper;
import kr.pe.sinnori.gui.syncfileupdown.lib.ScreenManagerIF;
import kr.pe.sinnori.impl.message.LoginReq.LoginReq;
import kr.pe.sinnori.impl.message.LoginRes.LoginRes;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public final class LoginPanel extends JPanel {
	private Logger log = LoggerFactory.getLogger(LoginPanel.class);

	private Frame mainFrame = null;
	private ScreenManagerIF screenManagerIF = null;

	public LoginPanel(Frame mainFrame, ScreenManagerIF screenManagerIF) {
		super();
		this.mainFrame = mainFrame;
		this.screenManagerIF = screenManagerIF;
		initComponents();
		postInitComponents();
	}
	
	private void postInitComponents() {
		hostTextField.setText("localhost");
		portTextField.setText("9091");
		idTextField.setText("test01");
		pwdPasswordField.setText("test1234!");
	}

	private void showMessageDialog(JComponent component, String message) {
		JOptionPane.showMessageDialog(mainFrame,
				CommonStaticUtil.splitString(message, CommonType.LINE_SEPARATOR_GUBUN.NEWLINE, 100));
		if (null != component) component.requestFocusInWindow();
	}

	private void okButtonActionPerformed(ActionEvent e) {		
		String newServerHost = null;

		try {
			newServerHost = hostTextField.getText();
		} catch (NullPointerException e1) {
			log.warn(e1.getMessage(), e1);
			showMessageDialog(hostTextField, "the host address value is null");
			return;
		}

		newServerHost = newServerHost.trim();
		hostTextField.setText(newServerHost);
		if (0 == newServerHost.length()) {
			showMessageDialog(hostTextField, "the host address value is empty");
			return;
		}

		String portText = null;

		try {
			portText = portTextField.getText();
		} catch (NullPointerException e1) {
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

		int newServerPort = -1;
		try {
			newServerPort = Integer.parseInt(portText);
		} catch (NumberFormatException nfe) {
			showMessageDialog(portTextField, String.format("the port value[%s] is not a integer", portText));
			return;
		}
	
		String id = null;
		try {
			id = idTextField.getText();
		} catch (NullPointerException e1) {
			log.warn(e1.getMessage(), e1);
			showMessageDialog(idTextField, "the id value is null");
			return;
		}

		id = id.trim();
		idTextField.setText(id);
		if (0 == id.length()) {
			showMessageDialog(idTextField, "the id value is empty");
			return;
		}
		
		try {
			ValueChecker.checkValidUserId(id);
		} catch(IllegalArgumentException e1) {
			log.warn(e1.getMessage(), e1);
			showMessageDialog(idTextField, String.format("the id value is bad, errormsg=%s", e1.getMessage()));
			return;
		}

		char[] pwdChars = null;
		try {
			pwdChars = pwdPasswordField.getPassword();
			
		} catch (Exception e1) {
			log.warn(e1.getMessage(), e1);
			showMessageDialog(pwdPasswordField, "the password value is null");
			return;
		} finally {
			pwdPasswordField.setText("");
		}

		if (0 == pwdChars.length) {
			showMessageDialog(pwdPasswordField, "the password value is empty");
			return;
		}

		try {
			ValueChecker.checkValidPwd(pwdChars);
		} catch(IllegalArgumentException e1) {
			log.warn(e1.getMessage(), e1);
			showMessageDialog(pwdPasswordField, String.format("the password value is bad, errormsg=%s", e1.getMessage()));
			return;
		}

		PasswordWrapper passwordWrapper = new PasswordWrapper(pwdChars);
		byte[] passwordBytes = passwordWrapper.toBytes();
		Arrays.fill(pwdChars, '\u0000');		
		try {
			SinnoriConfigurationManager.getInstance().getSinnoriRunningProjectConfiguration().changeServerAddressIfDifferent(newServerHost, newServerPort);
		} catch (IOException e1) {
			log.warn(e1.getMessage(), e1);
			
			String errorMessage =String.format("fail to save the new server address[host:[%s], port:[%d]], errmsg=%s", 
					newServerHost, newServerPort, e1.getMessage());
			showMessageDialog(hostTextField, errorMessage);
			return;
		}		
		
		byte[] idCipherBytes = null;
		byte[] pwdCipherBytes = null;
		byte[] sessionKeyBytes = null;
		byte[] ivBytes = null;
		
		/**
		 * <pre>
		 * 설정 파일을 수정하기도 전에 설정 파일에 저장된 수정전 주소를 갖는 연결 객체가 생성될 수 있다.
		 * 하여 방어 코드 차원에서 연결 객체의 주소도 새롭게 변경한다.
		 * </pre>
		 */
		MainProejctSyncConnectionManager  mainProejctConnectionManager = MainProejctSyncConnectionManager.getInstance();
		
		try {
			mainProejctConnectionManager.changeServerAddress(newServerHost, newServerPort);
		} catch (NotSupportedException e1) {
			mainProejctConnectionManager.closeConnection();
			try {
				mainProejctConnectionManager.changeServerAddress(newServerHost, newServerPort);
			} catch (NotSupportedException e2) {
				/**
				 * 위에서 이전 주소로 접속한 연결을 닫았기때문에 이곳으로 들어와서는 안된다.
				 */
				log.warn(e2.getMessage(), e2);
				showMessageDialog(hostTextField,
						String.format("It failed to change the server address[host:%s, port:%d], errmsg=%s", newServerHost,
								newServerPort, e2.getMessage()));
				System.exit(1);
			}
		}
		
		ClientSessionKeyIF mainProjectClientSessionKey = null;
		try {
			mainProjectClientSessionKey = ClientSessionKeyManager.getInstance()
					.getMainProjectClientSessionKey(RSAPublickeyGetterBuilder.build());
		} catch(InterruptedException e1) {
			log.error(e1.getMessage(), e1);
			System.exit(1);
		} catch (SymmetricException e1) {
			log.warn(e1.getMessage(), e1);
			showMessageDialog(hostTextField,
					String.format("It failed to get the server[%s][%d]' RSA public key bytes, errmsg=%s", newServerHost,
							newServerPort, e1.getMessage()));
			return;
		}		
		
		
		ClientSymmetricKeyIF clientSymmetricKey = mainProjectClientSessionKey.getClientSymmetricKey();	
		
		try {
			pwdCipherBytes = clientSymmetricKey.encrypt(passwordBytes);
		} catch (Exception e1) {
			log.warn(e1.getMessage(), e1);
			showMessageDialog(null, String.format("fail to encrypt the password, errormsg=%s", e1.getMessage()));
			return;		
		} finally {
			Arrays.fill(passwordBytes, (byte)0);	
		}
		
		try {
			idCipherBytes = clientSymmetricKey.encrypt(id.getBytes(CommonStaticFinalVars.SINNORI_CIPHER_CHARSET));
		} catch (Exception e1) {
			log.warn(e1.getMessage(), e1);
			showMessageDialog(null, String.format("fail to encrypt the password, errormsg=%s", e1.getMessage()));
			return;		
		} finally {
			Arrays.fill(passwordBytes, (byte)0);	
		}		
		
		sessionKeyBytes = mainProjectClientSessionKey.getDupSessionKeyBytes();
		ivBytes = mainProjectClientSessionKey.getDupIVBytes();		
		
		LoginReq loginReq = new LoginReq();
		loginReq.setIdEncryptedBytes(idCipherBytes);
		loginReq.setPwdEncryptedBytes(pwdCipherBytes);
		loginReq.setSessionKeyBytes(sessionKeyBytes);
		loginReq.setIvBytes(ivBytes);
		
		LoginRes loginRes = null;
		AbstractMessage outMessage = null;		
		
		try {
			outMessage = mainProejctConnectionManager.sendSyncInputMessage(loginReq);
		} catch (Exception e1) {
			mainProejctConnectionManager.closeConnection();
			log.warn(e1.getMessage(), e1);
			showMessageDialog(null, String.format("fail to encrypt the password, errormsg=%s", e1.getMessage()));
			return;	
		}
		
		if (! outMessage.getMessageID().equals("LoginRes")) {
			String errorMessage = String.format("expected message id 'LoginRes' but returned message id is not 'LoginRes', %s", outMessage.toString());
			log.warn(errorMessage);
			showMessageDialog(null, errorMessage);
			return;
		}
		
		loginRes = (LoginRes)outMessage;
		
		if (! loginRes.getIsSuccess().equals("N") && !loginRes.getIsSuccess().equals("Y")) {
			String errorMessage = String.format("LoginRes message field 'isSuccess' has a bad value, %s", loginRes.toString());
			log.warn(errorMessage);
			showMessageDialog(null, errorMessage);
			return;
		}
		
		if (loginRes.getIsSuccess().equals("N")) {
			showMessageDialog(null, loginRes.getErrorMessage());
			return;
		}
		
		mainProejctConnectionManager.setLoginID(id);
		
		screenManagerIF.goToFileUpDownScreen();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
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

		// ======== this ========
		setMinimumSize(new Dimension(300, 158));
		setPreferredSize(new Dimension(300, 158));
		setLayout(new FormLayout("$ugap, [120dlu,pref]:grow, $ugap", "5*($lgap, default), $lgap"));

		// ======== ipPanel ========
		{
			ipPanel.setLayout(new FormLayout("default, $ugap, ${growing-button}", "default"));

			// ---- hostLabel ----
			hostLabel.setText("host address");
			ipPanel.add(hostLabel, CC.xy(1, 1));
			ipPanel.add(hostTextField, CC.xy(3, 1));
		}
		add(ipPanel, CC.xy(2, 2));

		// ======== portPanel ========
		{
			portPanel.setLayout(new FormLayout("42dlu, $ugap, ${growing-button}", "default"));

			// ---- portLabel ----
			portLabel.setText("port");
			portPanel.add(portLabel, CC.xy(1, 1));
			portPanel.add(portTextField, CC.xy(3, 1));
		}
		add(portPanel, CC.xy(2, 4));

		// ======== idPanel ========
		{
			idPanel.setLayout(new FormLayout("42dlu, $ugap, ${growing-button}", "default"));

			// ---- idLabel ----
			idLabel.setText("ID");
			idPanel.add(idLabel, CC.xywh(1, 1, 3, 1));
			idPanel.add(idTextField, CC.xy(3, 1));
		}
		add(idPanel, CC.xy(2, 6));

		// ======== pwdPanel ========
		{
			pwdPanel.setLayout(new FormLayout("42dlu, $ugap, ${growing-button}", "default"));

			// ---- pwdLabel ----
			pwdLabel.setText("password");
			pwdPanel.add(pwdLabel, CC.xy(1, 1));
			pwdPanel.add(pwdPasswordField, CC.xy(3, 1));
		}
		add(pwdPanel, CC.xy(2, 8));

		// ======== bottomMenuPanel ========
		{
			bottomMenuPanel.setLayout(new FlowLayout());

			// ---- okButton ----
			okButton.setText("OK");
			okButton.addActionListener(e -> okButtonActionPerformed(e));
			bottomMenuPanel.add(okButton);
		}
		add(bottomMenuPanel, CC.xy(2, 10));
		// JFormDesigner - End of component initialization
		// //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
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
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
