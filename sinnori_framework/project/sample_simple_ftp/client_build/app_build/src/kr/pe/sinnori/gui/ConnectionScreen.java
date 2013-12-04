/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.pe.sinnori.gui;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.text.MaskFormatter;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import kr.pe.sinnori.common.configuration.ProjectConfig;
import kr.pe.sinnori.common.configuration.SinnoriConfig;

/**
 * 
 * @author Jonghoon Won
 *
 */
@SuppressWarnings("serial")
public class ConnectionScreen extends JPanel {
	private JFrame mainFrame = null;
	private MainControllerIF mainController = null;
	
	private JFormattedTextField hostTextField;
	private JFormattedTextField portTextField;
	private JFormattedTextField idTextField;
	private JPasswordField passwordField;
	private final Action connectionButtonAction = new ConnectionButtonAction();
	
	
	private final String projectName = "sample_simple_ftp";
	private SinnoriConfig sinnoriConfig = SinnoriConfig.getInstance();
	private ProjectConfig projectConfig = sinnoriConfig.getProjectConfig(projectName);
	

	/**
	 * Create the frame.
	 */
	public ConnectionScreen(final JFrame mainFrame, MainControllerIF mainController) {
		super();
		
		this.mainFrame = mainFrame;
		this.mainController = mainController;
		initialize();
	}

	
	/**
	 * Initialize the contents of the this.
	 */
	private void initialize() {
		// Container thisContainer = this.getContentPane();
		
		setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("9dlu"),
				ColumnSpec.decode("center:default"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				ColumnSpec.decode("10dlu"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		
		JLabel lblHost = new JLabel("호스트");
		this.add(lblHost, "2, 4, right, default");
		
		hostTextField = new JFormattedTextField();
		this.add(hostTextField, "4, 4, default, fill");
		hostTextField.setColumns(30);
		
		
		JLabel lblPort = new JLabel("포트");
		this.add(lblPort, "2, 6, right, default");
		
		portTextField = new JFormattedTextField(createFormatter("####"));
		this.add(portTextField, "4, 6, fill, default");
		portTextField.setColumns(4);
		
		JLabel lblId = new JLabel("아이디");
		this.add(lblId, "2, 8, right, default");
		
		idTextField = new JFormattedTextField();
		idTextField.setColumns(20);
		idTextField.setValue("");
		
		this.add(idTextField, "4, 8, fill, default");
		
		JLabel lblPassword = new JLabel("비밀번호");
		this.add(lblPassword, "2, 10, right, default");
		
		passwordField = new JPasswordField();
		this.add(passwordField, "4, 10, fill, default");
		
		// FIXME!
		hostTextField.setText(projectConfig.getServerHost());
		portTextField.setText(String.valueOf(projectConfig.getServerPort()));
		idTextField.setText("test01");
		passwordField.setText("1234");
		
		
		JButton btnConnect = new JButton("연결");
		
		btnConnect.setToolTipText("신놀이 파일 서버에 접속합니다. ");
		btnConnect.setAction(connectionButtonAction);
		this.add(btnConnect, "4, 12");
		
		JLabel emptyLabel = new JLabel(" ");
		add(emptyLabel, "4, 13");
	}
	
	private class ConnectionButtonAction extends AbstractAction {
		public ConnectionButtonAction() {
			putValue(NAME, "연결");
			putValue(SHORT_DESCRIPTION, "현재 미 지정");
		}
		public void actionPerformed(ActionEvent e) {
			System.out.printf("call actionPerformed [%d], str len=[%d]", e.getID(), idTextField.getText().length());
			System.out.println("");
			
			String newServerHost = hostTextField.getText();
			String newServerPortStr = portTextField.getText();
			
			int newServerPort = -1;
			try {
				newServerPort = Integer.parseInt(newServerPortStr);
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(mainFrame, "선택된 디렉토리가 존재하지 않습니다");				
				return;
			}
			
			byte[] binaryPublicKeyBytes = mainController.connectServer(newServerHost, newServerPort);
			
			if (null == binaryPublicKeyBytes) return;
			
			projectConfig.changeServerHostInfo(newServerHost, newServerPort);
			sinnoriConfig.save();
			
			String id = idTextField.getText();
			
			String pwd = new String(passwordField.getPassword());
			
			
			boolean loginResult = mainController.login(id, pwd);
			
			if (loginResult) {
				mainController.loginOK();
			}
		}
	}
	
	protected MaskFormatter createFormatter(String s) {
	    MaskFormatter formatter = null;
	    try {
	        formatter = new MaskFormatter(s);
	        
	        // formatter.setAllowsInvalid(false);
	        // formatter.setInvalidCharacters("\u0127\u003A");
	        
	    } catch (java.text.ParseException exc) {
	        System.err.println("formatter is bad: " + exc.getMessage());
	        System.exit(1);
	    }
	    return formatter;
	}

}
