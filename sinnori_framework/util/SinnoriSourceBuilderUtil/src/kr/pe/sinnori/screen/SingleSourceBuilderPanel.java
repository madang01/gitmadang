package kr.pe.sinnori.screen;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import kr.pe.sinnori.common.message.MessageInfoSAXParser;
import kr.pe.sinnori.source_file_builder.SourceFileBuilderManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;


@SuppressWarnings("serial")
public class SingleSourceBuilderPanel extends JPanel {
	// private JFrame mainFrame = null;
	
	
	/**
	 * Create the panel.
	 */
	public SingleSourceBuilderPanel(final JFrame mainFrame) {
		// this.mainFrame = mainFrame;
		
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(324dlu;pref):grow"),
				FormFactory.UNRELATED_GAP_COLSPEC,},
			new RowSpec[] {
				RowSpec.decode("17dlu:grow"),
				FormFactory.PREF_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("max(100dlu;pref):grow"),
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("max(100dlu;pref):grow"),
				FormFactory.LINE_GAP_ROWSPEC,}));
		
		JPanel menuPanel = new JPanel();
		FlowLayout fl_menuPanel = (FlowLayout) menuPanel.getLayout();
		fl_menuPanel.setAlignment(FlowLayout.LEFT);
		add(menuPanel, "2, 1, fill, center");
		
		final JRadioButton messageRadioButton = new JRadioButton("Message");
		messageRadioButton.setActionCommand("Message");
		messageRadioButton.setSelected(true);
		menuPanel.add(messageRadioButton);
		
		final JRadioButton encoderRadioButton = new JRadioButton("Encoder");
		encoderRadioButton.setActionCommand("Encoder");
		menuPanel.add(encoderRadioButton);
		
		final JRadioButton decoderRadioButton = new JRadioButton("Decoder");
		decoderRadioButton.setActionCommand("Decoder");
		menuPanel.add(decoderRadioButton);
		
		final JRadioButton serverCodecRadioButton = new JRadioButton("ServerCodec");
		serverCodecRadioButton.setActionCommand("ServerCodec");
		menuPanel.add(serverCodecRadioButton);
		
		final JRadioButton clientCodecRadioButton = new JRadioButton("ClientCodec");
		clientCodecRadioButton.setActionCommand("ClientCodec");
		menuPanel.add(clientCodecRadioButton);
		
		
		ButtonGroup sourceTypeRadioGroup = new ButtonGroup();
		sourceTypeRadioGroup.add(messageRadioButton);
		sourceTypeRadioGroup.add(encoderRadioButton);
		sourceTypeRadioGroup.add(decoderRadioButton);
		sourceTypeRadioGroup.add(serverCodecRadioButton);
		sourceTypeRadioGroup.add(clientCodecRadioButton);

		
		JButton createButton = new JButton("생성");
		menuPanel.add(createButton);
		
		JLabel messageInfoLabel = new JLabel("메시지 정보 입력");
		add(messageInfoLabel, "2, 2");
		
		JScrollPane messageInfoScrollPane = new JScrollPane();
		add(messageInfoScrollPane, "2, 3, 1, 2, fill, fill");
		
		final JTextPane messageInfoTextPane = new JTextPane();
		messageInfoScrollPane.setViewportView(messageInfoTextPane);
		
		JLabel resultLabel = new JLabel("생성 결과");
		add(resultLabel, "2, 6");
		
		JScrollPane resultScrollPane = new JScrollPane();
		add(resultScrollPane, "2, 8, fill, fill");
		
		final JTextPane resultTextPane = new JTextPane();
		resultTextPane.setEditable(false);
		resultScrollPane.setViewportView(resultTextPane);
		
		class CreateButtonAction implements ActionListener {
			private SourceFileBuilderManager sourceFileBuilderManager = SourceFileBuilderManager.getInstance();
			
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("CreateButtonAction::"+e.toString());
				
				File messageInfoFile = null;
				FileOutputStream fos = null;
				
				try {
					messageInfoFile = File.createTempFile("sinnoriMessageInfo", null);
					
					fos = new FileOutputStream(messageInfoFile);
					fos.write(messageInfoTextPane.getText().getBytes("UTF-8"));
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(mainFrame, "FileNotFoundException");
					return;
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(mainFrame, "UnsupportedEncodingException");
					return;
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(mainFrame, "IOException");
					return;
				} finally {
					try {
						if (null != fos) fos.close();
					} catch(Exception e1) {
						
					}
				}

				MessageInfoSAXParser messageInfoSAXParser = new MessageInfoSAXParser(messageInfoFile, false);
    			kr.pe.sinnori.common.message.MessageInfo messageInfo = messageInfoSAXParser.parse();
			
    			// ButtonModel selectedObject = sourceTypeRadioGroup.getSelection();
    			// String actionCommand = selectedObject.getActionCommand();
    			
				String result = null;
				if (messageRadioButton.isSelected()) {
					result = sourceFileBuilderManager.getMessageSourceFileBuilder().toString(messageInfo.getMessageID(), "", messageInfo);
				} else if (encoderRadioButton.isSelected()) {
					result = sourceFileBuilderManager.getEncoderSourceFileBuilder().toString(messageInfo.getMessageID(), "", messageInfo);
				} else if (decoderRadioButton.isSelected()) {
					result = sourceFileBuilderManager.getDecoderSourceFileBuilder().toString(messageInfo.getMessageID(), "", messageInfo);
				} else if (serverCodecRadioButton.isSelected()) {
					result = sourceFileBuilderManager.getServerCodecSourceFileBuilder().toString(messageInfo.getDirection(), messageInfo.getMessageID(), "");
				} else if (clientCodecRadioButton.isSelected()) {
					result = sourceFileBuilderManager.getClientCodecSourceFileBuilder().toString(messageInfo.getDirection(), messageInfo.getMessageID(), "");
				} else {
					JOptionPane.showMessageDialog(mainFrame, "알 수 없는 콤포넌트입니다.");
					return;
				}
				
				resultTextPane.setText(result);
				
				if (null != messageInfoFile) messageInfoFile.delete();
			}
		}
		
		createButton.addActionListener(new CreateButtonAction());
		
	}
}