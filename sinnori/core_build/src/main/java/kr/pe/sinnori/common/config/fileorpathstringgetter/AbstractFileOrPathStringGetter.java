package kr.pe.sinnori.common.config.fileorpathstringgetter;

public abstract class AbstractFileOrPathStringGetter {
	protected String itemID;
	
	public AbstractFileOrPathStringGetter(String itemID) {
		this.itemID = itemID;
	}
	
	public abstract String getFileOrPathStringDependingOnBuildSystem(
			String mainProjectName, String sinnoriInstalledPathString, String ... etcParamter);
	
	
	
	public String getItemID() {
		return itemID;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FileOrPathStringGetter [itemID=");
		builder.append(itemID);
		builder.append("]");
		return builder.toString();
	}
}
