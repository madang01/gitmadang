package kr.pe.sinnori.common.config.valueobject;

import java.util.HashMap;
import java.util.List;

public class AllSubProjectPart {	
	private List<String> subProjectNamelist = null;
	private HashMap<String, ProjectPart> subProjectPartHash = null;
	
	
	public AllSubProjectPart(List<String> subProjectNamelist, HashMap<String, ProjectPart> subProjectPartHash) throws IllegalArgumentException {
		if (null == subProjectNamelist) {
			String errorMessage = "the paramter subProjectNamelist is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == subProjectPartHash) {
			String errorMessage = "the paramter subProjectPartHash is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		this.subProjectNamelist = subProjectNamelist;
		this.subProjectPartHash = subProjectPartHash;
	}
	
	public List<String> getSubProjectNamelist() {
		return subProjectNamelist;
	}
	
	public boolean isRegistedProjectName(String projectName) {		
		return (null != subProjectPartHash.get(projectName));
	}
	
	public ProjectPart getSubProjectPart(String projectName) {
		return subProjectPartHash.get(projectName);
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("AllProjectPart [projectNamelist=");
		builder.append(subProjectNamelist != null ? subProjectNamelist.subList(0,
				Math.min(subProjectNamelist.size(), maxLen)) : null);
		builder.append("]");
		return builder.toString();
	}	
}
