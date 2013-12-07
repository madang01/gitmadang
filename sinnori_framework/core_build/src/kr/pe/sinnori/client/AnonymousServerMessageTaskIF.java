package kr.pe.sinnori.client;

import kr.pe.sinnori.common.message.OutputMessage;

public interface AnonymousServerMessageTaskIF {
	public void doTask(String projectName, OutputMessage outObj);
}
