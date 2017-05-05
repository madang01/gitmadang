package kr.pe.sinnori.server.mysql;

public class MepperInfo {
	public enum RESOURCE_TYPE {
		RESOURCE, CLASS, URL 
	};
	
	private MepperInfo.RESOURCE_TYPE resourceType = null;
	private Object resourceValue = null;
	
	public MepperInfo(MepperInfo.RESOURCE_TYPE resourceType, Object resourceValue) {
		this.resourceType = resourceType;
		this.resourceValue = resourceValue;
	}

	public MepperInfo.RESOURCE_TYPE getResourceType() {
		return resourceType;
	}

	public Object getResourceValue() {
		return resourceValue;
	}	
}
