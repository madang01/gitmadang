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

package kr.pe.sinnori.gui.screen;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import kr.pe.sinnori.common.configuration.ClientProjectConfig;
import kr.pe.sinnori.common.configuration.SinnoriConfig;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.gui.lib.MainControllerIF;
import kr.pe.sinnori.gui.util.RegexLimitPlainDocume;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * 파일 송수신 서버 접속 화면
 * @author Jonghoon Won
 *
 */
@SuppressWarnings("serial")
public class ConnectionScreen extends JPanel implements CommonRootIF {
	private ClientProjectConfig clientProjectConfig;
	private JFrame mainFrame = null;
	private MainControllerIF mainController = null;
	
	private JTextField hostTextField;
	private JTextField portTextField;
	private JTextField idTextField;
	private JPasswordField passwordField;
	private final Action connectionButtonAction = new ConnectionButtonAction();
	
	
	
	private SinnoriConfig sinnoriConfig = SinnoriConfig.getInstance();
	
	

	/**
	 * 생성자
	 * @param mainFrame 메일 프레임
	 * @param mainController 메인 제어자
	 */
	public ConnectionScreen(ClientProjectConfig clientProjectConfig, final JFrame mainFrame, MainControllerIF mainController) {
		super();
		this.clientProjectConfig = clientProjectConfig;
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
		
		hostTextField = new JTextField();
		hostTextField.setDocument(new RegexLimitPlainDocume(null, 30, null));
		this.add(hostTextField, "4, 4, default, fill");
		hostTextField.setColumns(30);
		
		
		JLabel lblPort = new JLabel("포트");
		this.add(lblPort, "2, 6, right, default");
		
		portTextField = new JTextField();
		portTextField.setDocument(new RegexLimitPlainDocume(null, 4, "^[0-9]*$"));
		this.add(portTextField, "4, 6, fill, default");
		portTextField.setColumns(4);
		
		JLabel lblId = new JLabel("아이디");
		this.add(lblId, "2, 8, right, default");
		
		idTextField = new JTextField();
		idTextField.setDocument(new RegexLimitPlainDocume(null, 20, "^[a-zA-Z0-9]*$"));
		idTextField.setColumns(20);
		idTextField.setText("");
		
		this.add(idTextField, "4, 8, fill, default");
		
		JLabel lblPassword = new JLabel("비밀번호");
		this.add(lblPassword, "2, 10, right, default");
		
		passwordField = new JPasswordField();
		this.add(passwordField, "4, 10, fill, default");
		
		// FIXME!
		hostTextField.setText(clientProjectConfig.getServerHost());
		portTextField.setText(String.valueOf(clientProjectConfig.getServerPort()));
		idTextField.setText("test01");
		passwordField.setText("1234");
		
		
		JButton btnConnect = new JButton("연결");
		
		btnConnect.setToolTipText("신놀이 파일 서버에 접속합니다. ");
		btnConnect.setAction(connectionButtonAction);
		this.add(btnConnect, "4, 12");
		
		JLabel emptyLabel = new JLabel(" ");
		add(emptyLabel, "4, 13");
	}
	
	public void init() {
		// FIXME! 나중에는 비밀번호 초기화를 해 주어야 한다. 지금은 편의를 위해서  강제적으로 입력
		passwordField.setText("1234");
	}
	
	/**
	 * 파일 송수신 서버 접속 이벤트 처리 클래스
	 * @author Jonghoon Won
	 *
	 */
	private class ConnectionButtonAction extends AbstractAction {
		/**
		 * 생성자
		 */
		public ConnectionButtonAction() {
			putValue(NAME, "연결");
			putValue(SHORT_DESCRIPTION, "현재 미 지정");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.printf("call actionPerformed [%d], str len=[%d]", e.getID(), idTextField.getText().length());
			System.out.println("");
			
			String newServerHost = hostTextField.getText();
			String newServerPortStr = portTextField.getText();
			
			int newServerPort = -1;
			try {
				newServerPort = Integer.parseInt(newServerPortStr);
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(mainFrame, "포트 번호에 숫자가 아닌 값을 넣어습니다.");
				portTextField.requestFocus();
				portTextField.grabFocus();
				return;
			}
			
			byte[] binaryPublicKeyBytes = mainController.getBinaryPublicKey(newServerHost, newServerPort);
			
			if (null == binaryPublicKeyBytes) return;
						
			sinnoriConfig.save();
			
			String id = idTextField.getText();
			
			String pwd = new String(passwordField.getPassword());
			
			/** 보안을 위해서 화면에 있는 비밀번호 입력 내용 지우기 */
			passwordField.setText("");
			
			mainController.login(id, pwd);
		}
	}
}
