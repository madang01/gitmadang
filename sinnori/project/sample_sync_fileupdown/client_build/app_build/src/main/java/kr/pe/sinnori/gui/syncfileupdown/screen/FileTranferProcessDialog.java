/*
 * Created by JFormDesigner on Sun Aug 06 10:16:07 KST 2017
 */

package kr.pe.sinnori.gui.syncfileupdown.screen;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import kr.pe.sinnori.gui.syncfileupdown.lib.FileTransferTaskIF;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class FileTranferProcessDialog extends JDialog implements FileTranferProcessDialogIF {
	private Logger log = LoggerFactory.getLogger(FileTranferProcessDialog.class);
	
	
	private long fileSize;
	private long totalReceivedDataSize;
	private long startTimeStamp  = 0;
	private FileTransferTaskIF fileTransferTask = null;
	
	
	public FileTranferProcessDialog(Frame owner, String title, long fileSize, long totalReceivedDataSize, FileTransferTaskIF fileTransferTask) {
		super(owner);
		this.fileSize = fileSize;
		this.totalReceivedDataSize = totalReceivedDataSize;
		this.fileTransferTask = fileTransferTask;
		this.setTitle(title);
		
		initComponents();
		postInitComponents();
	}
	
	
	private void postInitComponents() {
		startTimeStamp = System.currentTimeMillis();
	}
	
	
	
	/**
	 * 파일 전송 진행 상태바에 지정한 전송 받은 데이터 크기 만큼 증가 시킨다.
	 * @param receivedDataSize 전송 받은 데이터 크기
	 */
	@Override
	public void noticeAddedFileData(int receivedDataSize) {
		
		
		totalReceivedDataSize += receivedDataSize;
		
		if (totalReceivedDataSize > fileSize) {
			log.error("the totalReceivedDataSize[{}] is greater than fileSize[{}]", totalReceivedDataSize, fileSize);
			System.exit(1);
		}
		
		progressBar.setValue((int)(totalReceivedDataSize*100L/fileSize));
		
		// updateInfoMesg();
		
		if (totalReceivedDataSize == fileSize) {
			messageLabel.setText("파일 전송이 완료되었습니다.");
			okButton.setVisible(true);
			cancelButton.setVisible(false);
		}
		
		updateInfoMesg();
	}
	
	private void updateInfoMesg() {
		DecimalFormat df = new DecimalFormat("#,##0.##");
		
		String reciveLabelTxt = String.format("%s bytes/%s bytes", df.format(totalReceivedDataSize), df.format(fileSize));
		progressRateLabel.setText(reciveLabelTxt);

		long currentTimeStamp = System.currentTimeMillis();
		// 데이터 수신 시간차 단위 ms
		long secondGapTime = (currentTimeStamp - startTimeStamp) / 1000;
		
		if (0 == secondGapTime) return;
		
		// 초당 전송 bytes
		double receivedDataPerSecond = (double)totalReceivedDataSize / secondGapTime;
		
		
		StringBuilder speedLabelTxtBuilder = new StringBuilder();;
		if (receivedDataPerSecond < 1024) {
			// bytes
			speedLabelTxtBuilder.append(df.format(receivedDataPerSecond));
			speedLabelTxtBuilder.append(" bytes/s");
		} else if (receivedDataPerSecond < 1024L*1024L) {
			// Kbytes
			speedLabelTxtBuilder.append(df.format(receivedDataPerSecond/1024L));
			speedLabelTxtBuilder.append(" Kbytes/s");
		} else if (receivedDataPerSecond < 1024L*1024L*1024L) {
			// Mbytes
			speedLabelTxtBuilder.append(df.format(receivedDataPerSecond/(1024L*1024L)));
			speedLabelTxtBuilder.append(" Mbytes/s");
		} else {
			// Gbytes
			speedLabelTxtBuilder.append(df.format(receivedDataPerSecond/(1024L*1024L*1024L)));
			speedLabelTxtBuilder.append(" Gbytes/s");
		}
		
		transmissionSpeedLabel.setText(speedLabelTxtBuilder.toString());
	}
	

	private void cancelButtonActionPerformed(ActionEvent e) {
		if (totalReceivedDataSize != fileSize) {
			/** 미 완료된 경우에만 취소 호출 */
			fileTransferTask.cancelTask();
		}
	}

	private void okButtonActionPerformed(ActionEvent e) {
		dispose();
	}
	
	/*public void dispose() {
		super.dispose();
		isDisposed = true;
	}
	
	public boolean getIsDisposed() {
		return isDisposed;
	}*/

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		messageLabel = new JLabel();
		informationPanel = new JPanel();
		transmissionSpeedLabel = new JLabel();
		informationHSpacer = new JPanel(null);
		progressRateLabel = new JLabel();
		progressBar = new JProgressBar();
		functionPanel = new JPanel();
		okButton = new JButton();
		functionHSpacer = new JPanel(null);
		cancelButton = new JButton();

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(Borders.createEmptyBorder("7dlu, 7dlu, 7dlu, 7dlu"));
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new FormLayout(
					"$ugap, ${growing-button}, $ugap",
					"2*(default, $lgap), fill:43dlu:grow, $lgap, default"));

				//---- messageLabel ----
				messageLabel.setText("\ud30c\uc77c\uc744 \uc804\uc1a1 \ud569\ub2c8\ub2e4.");
				contentPanel.add(messageLabel, CC.xy(2, 1));

				//======== informationPanel ========
				{
					informationPanel.setLayout(new BoxLayout(informationPanel, BoxLayout.X_AXIS));

					//---- transmissionSpeedLabel ----
					transmissionSpeedLabel.setText("999 kbytes/s");
					informationPanel.add(transmissionSpeedLabel);

					//---- informationHSpacer ----
					informationHSpacer.setMaximumSize(new Dimension(20, 32767));
					informationHSpacer.setPreferredSize(new Dimension(20, 10));
					informationPanel.add(informationHSpacer);

					//---- progressRateLabel ----
					progressRateLabel.setText("490,000,000 bytes / 490,000,000,000 bytes");
					informationPanel.add(progressRateLabel);
				}
				contentPanel.add(informationPanel, CC.xy(2, 3));

				//---- progressBar ----
				progressBar.setStringPainted(true);
				contentPanel.add(progressBar, CC.xy(2, 5));

				//======== functionPanel ========
				{
					functionPanel.setLayout(new BoxLayout(functionPanel, BoxLayout.X_AXIS));

					//---- okButton ----
					okButton.setText("OK");
					okButton.setVisible(false);
					okButton.addActionListener(e -> okButtonActionPerformed(e));
					functionPanel.add(okButton);

					//---- functionHSpacer ----
					functionHSpacer.setPreferredSize(new Dimension(20, 10));
					functionHSpacer.setMinimumSize(new Dimension(20, 12));
					functionHSpacer.setMaximumSize(new Dimension(20, 32767));
					functionPanel.add(functionHSpacer);

					//---- cancelButton ----
					cancelButton.setText("Cancel");
					cancelButton.addActionListener(e -> cancelButtonActionPerformed(e));
					functionPanel.add(cancelButton);
				}
				contentPanel.add(functionPanel, CC.xy(2, 7, CC.CENTER, CC.DEFAULT));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JLabel messageLabel;
	private JPanel informationPanel;
	private JLabel transmissionSpeedLabel;
	private JPanel informationHSpacer;
	private JLabel progressRateLabel;
	private JProgressBar progressBar;
	private JPanel functionPanel;
	private JButton okButton;
	private JPanel functionHSpacer;
	private JButton cancelButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	
}
