package kr.pe.codda.client.connection.asyn;

import java.nio.channels.Selector;

public class SelectorAlarm extends Thread {
	private long selectorWakeupInterval;
	private Selector ioEventSelector = null;
	// private ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(10); 
	
	// private boolean isStop = false;
	
	public SelectorAlarm(Selector ioEventSelector, long selectorWakeupInterval) {
		this.ioEventSelector = ioEventSelector;
		this.selectorWakeupInterval = selectorWakeupInterval;
	}
	
	@Override
	public void run() {	
		try {
			while (isInterrupted()) {				
				ioEventSelector.wakeup();			
				Thread.sleep(selectorWakeupInterval);
			}
		
		} catch (InterruptedException e) {
		}
	}
}
