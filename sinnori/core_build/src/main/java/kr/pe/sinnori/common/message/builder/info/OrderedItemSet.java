package kr.pe.sinnori.common.message.builder.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;

public class OrderedItemSet {
	private ArrayList<AbstractItemInfo> itemInfoList = new ArrayList<AbstractItemInfo>();
	private HashMap<String, AbstractItemInfo> itemInfoHash = new HashMap<String, AbstractItemInfo>();

	public void addItemInfo(AbstractItemInfo itemInfo) {
		if (null ==  itemInfo) {
			throw new IllegalArgumentException("the parameter itemInfo is null");
		}
		
		if (isRegisted(itemInfo.getItemName())) {
			String errorMessage = String.format("the parameter itemInfo[%s] was registed", itemInfo.toString());
			throw new IllegalArgumentException(errorMessage);
		}

		itemInfoList.add(itemInfo);
		itemInfoHash.put(itemInfo.getItemName(), itemInfo);
	}
	
	public boolean isRegisted(String itemIName) {
		if (null ==  itemIName) {
			throw new IllegalArgumentException("the parameter itemIName is null");
		}
		return itemInfoHash.containsKey(itemIName);
	}
	
	public AbstractItemInfo getItemInfo(String itemIName) {
		if (null ==  itemIName) {
			throw new IllegalArgumentException("the parameter itemIName is null");
		}
		return itemInfoHash.get(itemIName);
	}
	
	public List<AbstractItemInfo> getItemInfoList() {
		return itemInfoList;
	}
	
	public String toString() {
		StringBuffer toStringStringBuffer = new StringBuffer();
		
		Iterator<AbstractItemInfo> itemInfoIterator = itemInfoList.iterator();
		
		if (itemInfoIterator.hasNext()) {
			AbstractItemInfo itemInfo = itemInfoIterator.next();
			toStringStringBuffer.append(CommonStaticFinalVars.NEWLINE);
			toStringStringBuffer.append(itemInfo.toString());
		}
		while (itemInfoIterator.hasNext()) {
			AbstractItemInfo itemInfo = itemInfoIterator.next();
			toStringStringBuffer.append(", ");
			toStringStringBuffer.append(CommonStaticFinalVars.NEWLINE);			
			toStringStringBuffer.append(itemInfo.toString());
		}
		return toStringStringBuffer.toString();
	}
}
