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
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import kr.pe.sinnori.gui.lib.FileTransferTaskIF;

/**
 * 파일 송수신 전송 상태 모달 윈도우
 * @author Jonghoon Won
 *
 */
@SuppressWarnings("serial")
public class FileTranferProcessDialog extends JDialog  implements ActionListener {
	// private final Object monitor = new Object();
	// private MainControllerIF mainController = null;

	
	private long fileSize = 0;
	private long totalReceivedDataSize = 0;
	private long startTimeStamp  = 0;
	
	private FileTransferTaskThread fileTransferTaskThread = null;
	private FileTransferTaskIF fileTransferTask = null;
	
	private JLabel speedLabel = new JLabel("999 kbytes/s");
	private JLabel mesgLabel = new JLabel("파일을 전송합니다.");
	private JLabel reciveLabel = new JLabel("490,000,000,000 bytes / 490,000,000,000 bytes");
	private JProgressBar progressBar = new JProgressBar();
	
	/**
	 * 생성자
	 * @param mainFrame 메인 프레임
	 * @param mesg 메시지
	 * @param fileSize 전송할 파일 크기
	 * @param fileTransferTask 파일 송수신 전송 상태 모달 윈도우에서 호출한 사용자 정의 비지니스 로직
	 */
	public FileTranferProcessDialog(final JFrame mainFrame, String mesg, long fileSize, FileTransferTaskIF fileTransferTask) {
		super(mainFrame, "파일 전송 현황 보고 창" , true);
		this.mesgLabel.setText(mesg);
		this.fileSize = fileSize;
		// this.mainController = mainController;
		this.fileTransferTask = fileTransferTask;
		this.fileTransferTask.setFileTranferProcessDialog(this);
		
		
		setBounds(100, 100, 500, 156);
		Container innerContainer = getContentPane();
		innerContainer.setLayout(new BorderLayout(10, 5));
		
		
		// setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		JLabel leftEmptyLabel = new JLabel(" ");
		JLabel rightEmptyLabel = new JLabel(" ");
		
		innerContainer.add(leftEmptyLabel, BorderLayout.LINE_START);
		innerContainer.add(rightEmptyLabel, BorderLayout.LINE_END);
		
		{
			JPanel topPanel = new JPanel();
			innerContainer.add(topPanel, BorderLayout.PAGE_START);
			topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
			{
				JPanel mesgPanel = new JPanel();
				FlowLayout flowLayout = (FlowLayout) mesgPanel.getLayout();
				flowLayout.setAlignment(FlowLayout.LEFT);
				topPanel.add(mesgPanel);
				{
					
					mesgPanel.add(mesgLabel);
				}
			}
			{
				JPanel infoPanel = new JPanel();
				topPanel.add(infoPanel);
				infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
				{
					JPanel speedPanel = new JPanel();
					infoPanel.add(speedPanel);
					FlowLayout fl_speedPanel = (FlowLayout) speedPanel.getLayout();
					fl_speedPanel.setAlignment(FlowLayout.RIGHT);
					{
						
						speedPanel.add(speedLabel);
						speedLabel.setHorizontalAlignment(SwingConstants.LEFT);
					}
				}
				{
					JPanel receivePanel = new JPanel();
					infoPanel.add(receivePanel);
					FlowLayout fl_receivePanel = (FlowLayout) receivePanel.getLayout();
					fl_receivePanel.setAlignment(FlowLayout.RIGHT);
					{	
						receivePanel.add(reciveLabel);
						reciveLabel.setHorizontalAlignment(SwingConstants.RIGHT);
						reciveLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
					}
				}
			}
		}
		{
			
			progressBar.setStringPainted(true);
			progressBar.setMinimum(0);
			progressBar.setMaximum(100);
			progressBar.setValue(0);
			innerContainer.add(progressBar, BorderLayout.CENTER);
		}
		{
			JPanel bottomPanel = new JPanel();
			bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			innerContainer.add(bottomPanel, BorderLayout.PAGE_END);
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(this);
				bottomPanel.add(cancelButton);
			}
		}
		
		fileTransferTaskThread = new FileTransferTaskThread(this);
		
		this.startTimeStamp = System.currentTimeMillis();
		
		fileTransferTaskThread.start();
	}
	
	/**
	 * 파일 전송 진행 상태바에 지정한 전송 받은 데이터 크기 만큼 증가 시킨다.
	 * @param receivedDataSize 전송 받은 데이터 크기
	 */
	public void noticeAddingFileData(int receivedDataSize) {
		
		// synchronized(monitor) {
			totalReceivedDataSize += receivedDataSize;
			// FIXME! 받은 데이터가 많을 경우 강제적으로 파일 크기로 설정
			if (receivedDataSize > fileSize) totalReceivedDataSize = fileSize;
		// }
		
		
		progressBar.setValue((int)(totalReceivedDataSize*100L/fileSize));
		
		// updateInfoMesg();
		
		if (receivedDataSize == fileSize) {
			mesgLabel.setText("파일 전송이 완료되었습니다.");
			// this.dispose();
		}
		
		updateInfoMesg();
	}

	/** 
	 * 파일 전송 작업 취소 시킨다. 결과적으로 취소 작업이 완료되면 윈도우는 닫힌다.
	 */
	public void cancelTask() {
		fileTransferTask.cancelTask();
	}

	/**
	 * 파일 송수신 전송 상태 모달 윈도우에서 기동하는 쓰레드로 사용자 정의 비지니스 로직을 호출한다.
	 * 비지니스 로직이 끝나면 파일 송수신 전송 상태 모달 윈도우를 닫는다.
	 * @author Jonghoon Won
	 *
	 */
	class FileTransferTaskThread extends Thread {
		private FileTranferProcessDialog fileTranferProcessDialog = null;
		
		/**
		 * 생성자
		 * @param fileTranferProcessDialog 파일 송수신 전송 상태 모달 윈도우
		 */
		public FileTransferTaskThread(FileTranferProcessDialog fileTranferProcessDialog) {
			this.fileTranferProcessDialog = fileTranferProcessDialog;
		}
		
		public void run() {
            // compute primes larger than minPrime
			try {
				fileTransferTask.doTask();
			} finally {
				fileTranferProcessDialog.dispose();
			}
        }
	}
	
	/**
	 * 송수신 받은 데이터에 맞추어 메시지를 갱신한다.
	 */
	public void updateInfoMesg() {
		
		
		String reciveLabelTxt = String.format("%d bytes/%d bytes", totalReceivedDataSize, fileSize);
		reciveLabel.setText(reciveLabelTxt);

		long currentTimeStamp = System.currentTimeMillis();
		// 데이터 수신 시간차 단위 ms
		long secondGapTime = (currentTimeStamp - startTimeStamp) / 1000;
		
		if (0 == secondGapTime) return;
		
		// 초당 전송 bytes
		double receivedDataPerSecond = (double)totalReceivedDataSize / secondGapTime;
		
		DecimalFormat df = new DecimalFormat("#,##0.##");
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
		
		speedLabel.setText(speedLabelTxtBuilder.toString());
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
			
		// boolean cancelResult = mainControllerIF.cancelFileTransferProcessDialog();
		// if (cancelResult) finishWindow();
		
		// mainController.turnOnFileTransferCanceledFlag();
		fileTransferTask.cancelTask();
	}
}
