package kr.pe.sinnori.server.lib;

public enum DeleteFlag {
	YES("Y"), NO("N");

	private String deleteFlagValue;
	
	private DeleteFlag(String deleteFlagValue) {
		this.deleteFlagValue = deleteFlagValue;
	}
	
	public String getValue() {
		return deleteFlagValue;
	}
	
	public static DeleteFlag valueOf(String deleteFlagValue, boolean isOrigial) {
		if (null == deleteFlagValue) {
			throw new IllegalArgumentException("the parameter deleteFlagValue is null");
		}
		
		if (isOrigial) {
			return valueOf(deleteFlagValue);
		}
		
		DeleteFlag[] deleteFlags = DeleteFlag.values();
		for (DeleteFlag deleteFlag : deleteFlags) {
			if (deleteFlag.getValue().equals(deleteFlagValue)) {
				return deleteFlag;
			}
		}
		
		throw new IllegalArgumentException("the parameter deleteFlagValue["+deleteFlagValue+"] is a element of BoardType set");
	}
}
