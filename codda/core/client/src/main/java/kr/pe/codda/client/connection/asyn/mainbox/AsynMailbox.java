package kr.pe.codda.client.connection.asyn.mainbox;

import kr.pe.codda.common.etc.CommonStaticFinalVars;

public abstract class AsynMailbox {

	private static int mailID = Integer.MIN_VALUE;

	public static int getMailboxID() {
		return CommonStaticFinalVars.ASYN_MAILBOX_ID;
	}

	public synchronized static int getNextMailID() {
		if (Integer.MAX_VALUE == mailID) {
			mailID = Integer.MIN_VALUE;
		} else {
			mailID++;
		}
		return mailID;
	}	
}
