package kr.pe.sinnori.common.message.builder.info;

public class GroupInfo extends AbstractItemInfo {	
	private OrderedItemSet orderedItemSet = new OrderedItemSet();
	private String groupName = null;
	private String groupFirstUpperName = null;
	
	public GroupInfo(String groupName) {
		if (null == groupName) {
			throw new IllegalArgumentException("the parameter groupName is null");
		}
		
		if (groupName.length() < 2) {
			String errorMessage = String.format("the number[%d] of character of the parameter groupName is less than two", groupName.length());
			throw new IllegalArgumentException(errorMessage);
		}
		
		this.groupName = groupName;
		this.groupFirstUpperName = groupName.substring(0, 1).toUpperCase() + groupName.substring(1);
	}
	
	public OrderedItemSet getOrderedItemSet() {
		return orderedItemSet;
	}

	@Override
	public String getItemName() {
		return groupName;
	}

	@Override
	public String getFirstUpperItemName() {
		return groupFirstUpperName;
	}

	@Override
	public ItemInfoType getItemInfoType() {
		return ItemInfoType.GROUP;
	}

	
	
}
