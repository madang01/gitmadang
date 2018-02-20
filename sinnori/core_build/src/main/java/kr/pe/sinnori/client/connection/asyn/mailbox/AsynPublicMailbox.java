package kr.pe.sinnori.client.connection.asyn.mailbox;

public abstract class AsynPublicMailbox {
	// private Logger log = LoggerFactory.getLogger(AsynPublicMailbox.class);

	// private final static Object monitor = new Object();

	private static final int mailboxID = 0;
	// private LinkedBlockingQueue<WrapReadableMiddleObject> outputMessageQueue =
	// null;
	// private long socketTimeOut;

	private static int mailID = Integer.MIN_VALUE;

	/*
	 * public AsynPublicMailbox() { if (socketTimeOut < 0) { String errorMessage =
	 * String.format("the parameter socketTimeOut[%d] is less than zero",
	 * socketTimeOut); throw new IllegalArgumentException(errorMessage); }
	 * 
	 * if (null == outputMessageQueue) { throw new
	 * IllegalArgumentException("the parameter outputMessageQueue is null"); }
	 * 
	 * this.socketTimeOut = socketTimeOut; this.outputMessageQueue =
	 * outputMessageQueue; }
	 */

	public static int getMailboxID() {
		return mailboxID;
	}

	public synchronized static int getNextMailID() {
		if (Integer.MAX_VALUE == mailID) {
			mailID = Integer.MIN_VALUE;
		} else {
			mailID++;
		}
		return mailID;
	}

	/*
	 * 
	 * 
	 * public void putToSyncOutputMessageQueue(WrapReadableMiddleObject
	 * wrapReadableMiddleObject) throws InterruptedException { int fromMailboxID =
	 * wrapReadableMiddleObject.getMailboxID(); if (mailboxID != fromMailboxID) {
	 * log.
	 * error("the mailbox id of the received message[{}] is not same to this mail box id[%{}]"
	 * , wrapReadableMiddleObject.toString(), mailboxID); System.exit(1); }
	 * 
	 * boolean result = outputMessageQueue.offer(wrapReadableMiddleObject,
	 * socketTimeOut, TimeUnit.MILLISECONDS);
	 * 
	 * if (! result) { log.warn("지정한 시간[{}]에 출력 메시지[{}]를 큐 넣기 실패하여 무한 대기로 큐 넣기",
	 * socketTimeOut, wrapReadableMiddleObject.toString());
	 * 
	 * outputMessageQueue.put(wrapReadableMiddleObject); } }
	 * 
	 * public WrapReadableMiddleObject getSyncOutputMessage() throws
	 * SocketTimeoutException, InterruptedException { WrapReadableMiddleObject
	 * wrapReadableMiddleObject = outputMessageQueue.take();
	 * 
	 * return wrapReadableMiddleObject; }
	 * 
	 * public int hashCode() { return monitor.hashCode(); }
	 * 
	 * @Override public String toString() { StringBuilder builder = new
	 * StringBuilder(); builder.append("AsynPublicMailbox [mailboxID=");
	 * builder.append(mailboxID); builder.append(", mailID=");
	 * builder.append(mailID); builder.append(", socketTimeOut=");
	 * builder.append(socketTimeOut); builder.append("]"); return
	 * builder.toString(); }
	 */
}
