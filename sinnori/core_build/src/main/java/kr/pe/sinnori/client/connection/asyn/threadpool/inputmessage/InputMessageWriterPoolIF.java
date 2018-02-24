package kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage;

import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.handler.InputMessageWriterIF;

public interface InputMessageWriterPoolIF {
	public InputMessageWriterIF getInputMessageWriterWithMinimumNumberOfConnetion();
}
