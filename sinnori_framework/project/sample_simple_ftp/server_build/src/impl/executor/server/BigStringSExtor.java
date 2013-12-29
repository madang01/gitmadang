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

package impl.executor.server;

import kr.pe.sinnori.common.configuration.ServerProjectConfigIF;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.server.ClientResourceManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerExecutor;
import kr.pe.sinnori.server.executor.LetterSender;

/**
 * 메세지 식별자 BigString 비지니스 로직
 * 
 * @author Jonghoon Won
 * 
 */
public final class BigStringSExtor extends AbstractServerExecutor {

	@Override
	protected void doTask(ServerProjectConfigIF serverProjectConfig,
			LetterSender letterSender, InputMessage inObj,
			MessageMangerIF messageManger,			
			ClientResourceManagerIF clientResourceManager)
			throws MessageInfoNotFoundException, MessageItemException {
		// InputMessage inObj = (BigStringInputMessage) inputMessage;

		OutputMessage outObj = messageManger.createOutputMessage("BigString");

		/*
		 * outObj.filler1Size = inObj.filler1Size; outObj.filler1 =
		 * inObj.filler1; outObj.value1 = inObj.value1; outObj.value2 =
		 * inObj.value2; outObj.value3 = inObj.value3; outObj.filler2Size =
		 * inObj.filler2Size; outObj.filler2 = inObj.filler2; outObj.value4 =
		 * inObj.value4;
		 */

		outObj.setAttribute("filler1", (byte[]) inObj.getAttribute("filler1"));
		outObj.setAttribute("value1", (String) inObj.getAttribute("value1"));
		outObj.setAttribute("value2", (String) inObj.getAttribute("value2"));
		outObj.setAttribute("value3", (String) inObj.getAttribute("value3"));
		outObj.setAttribute("filler2", (byte[]) inObj.getAttribute("filler2"));
		outObj.setAttribute("value4", (String) inObj.getAttribute("value4"));

		letterSender.sendSelf(outObj);
	}
}
