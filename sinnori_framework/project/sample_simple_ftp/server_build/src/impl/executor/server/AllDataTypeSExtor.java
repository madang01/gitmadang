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
import kr.pe.sinnori.common.message.ArrayData;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.ItemGroupDataIF;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.server.ClientResourceManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerExecutor;
import kr.pe.sinnori.server.executor.LetterSender;

/**
 * 메세지 식별자 AllDataType 비지니스 로직
 * 
 * @author Jonghoon Won
 * 
 */
public final class AllDataTypeSExtor extends AbstractServerExecutor {
	
	@Override
	protected void doTask(ServerProjectConfigIF serverProjectConfig,
			LetterSender letterSender, InputMessage inObj,
			MessageMangerIF messageManger,			
			ClientResourceManagerIF clientResourceManager)
			throws MessageInfoNotFoundException, MessageItemException {

		// FIXME!
		// log.info("inObj=[%s]", inObj.toString());
		
		// InputMessage inObj = inputMessage;
		
		OutputMessage outObj = messageManger.createOutputMessage("AllDataType");

		outObj.setAttribute("byteVar1", inObj.getAttribute("byteVar1"));
		outObj.setAttribute("byteVar2", inObj.getAttribute("byteVar2"));
		outObj.setAttribute("byteVar3", inObj.getAttribute("byteVar3"));

		outObj.setAttribute("unsignedByteVar1",
				inObj.getAttribute("unsignedByteVar1"));
		outObj.setAttribute("unsignedByteVar2",
				inObj.getAttribute("unsignedByteVar2"));
		outObj.setAttribute("unsignedByteVar3",
				inObj.getAttribute("unsignedByteVar3"));

		outObj.setAttribute("shortVar1", inObj.getAttribute("shortVar1"));
		outObj.setAttribute("shortVar2", inObj.getAttribute("shortVar2"));
		outObj.setAttribute("shortVar3", inObj.getAttribute("shortVar3"));

		outObj.setAttribute("unsignedShortVar1",
				inObj.getAttribute("unsignedShortVar1"));
		outObj.setAttribute("unsignedShortVar2",
				inObj.getAttribute("unsignedShortVar2"));
		outObj.setAttribute("unsignedShortVar3",
				inObj.getAttribute("unsignedShortVar3"));

		outObj.setAttribute("intVar1", inObj.getAttribute("intVar1"));
		outObj.setAttribute("intVar2", inObj.getAttribute("intVar2"));
		outObj.setAttribute("intVar3", inObj.getAttribute("intVar3"));

		outObj.setAttribute("unsignedIntVar1",
				inObj.getAttribute("unsignedIntVar1"));
		outObj.setAttribute("unsignedIntVar2",
				inObj.getAttribute("unsignedIntVar2"));
		outObj.setAttribute("unsignedIntVar3",
				inObj.getAttribute("unsignedIntVar3"));

		outObj.setAttribute("longVar1", inObj.getAttribute("longVar1"));
		outObj.setAttribute("longVar2", inObj.getAttribute("longVar2"));
		outObj.setAttribute("longVar3", inObj.getAttribute("longVar3"));

		outObj.setAttribute("strVar1", inObj.getAttribute("strVar1"));
		outObj.setAttribute("strVar2", inObj.getAttribute("strVar2"));
		outObj.setAttribute("strVar3", inObj.getAttribute("strVar3"));

		outObj.setAttribute("bytesVar1", inObj.getAttribute("bytesVar1"));
		outObj.setAttribute("bytesVar2", inObj.getAttribute("bytesVar2"));

		int memberCnt = (Integer) inObj.getAttribute("cnt");
		outObj.setAttribute("cnt", memberCnt);

		ArrayData memberListOfOutObj = (ArrayData) outObj
				.getAttribute("memberList");
		ArrayData memberListOfInObj = (ArrayData) inObj
				.getAttribute("memberList");

		for (int i = 0; i < memberCnt; i++) {
			ItemGroupDataIF memberOfOutObj = memberListOfOutObj.get(i);
			ItemGroupDataIF memberOfInObj = memberListOfInObj.get(i);

			memberOfOutObj.setAttribute("memberID",
					memberOfInObj.getAttribute("memberID"));
			memberOfOutObj.setAttribute("memberName",
					memberOfInObj.getAttribute("memberName"));
			int itemCnt = (int) memberOfInObj.getAttribute("cnt");
			memberOfOutObj.setAttribute("cnt", itemCnt);
			ArrayData itemListOfOutObj = (ArrayData) memberOfOutObj
					.getAttribute("itemList");

			ArrayData itemListOfInObj = (ArrayData) memberOfInObj
					.getAttribute("itemList");
			for (int j = 0; j < itemCnt; j++) {
				ItemGroupDataIF itemOfOutObj = itemListOfOutObj.get(j);
				ItemGroupDataIF itemOfInObj = itemListOfInObj.get(j);

				itemOfOutObj.setAttribute("itemID",
						itemOfInObj.getAttribute("itemID"));
				itemOfOutObj.setAttribute("itemName",
						itemOfInObj.getAttribute("itemName"));
				itemOfOutObj.setAttribute("itemCnt",
						itemOfInObj.getAttribute("itemCnt"));
			}
		}

		letterSender.sendSelf(outObj);
	}	
}
