package kr.pe.sinnori.common.config.part;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllSubProjectPartConfiguration {
	private List<String> subProjectNamelist = new ArrayList<String>();
	private HashMap<String, ProjectPartConfiguration> subProjectPartValueObjectHash = 
			new HashMap<String, ProjectPartConfiguration>();

	public void clear() {
		subProjectNamelist.clear();
		subProjectPartValueObjectHash.clear();
	}
	public void addSubProjectPartValueObject(ProjectPartConfiguration subProjectPartValueObject) {
		if (null == subProjectPartValueObject) {
			throw new IllegalArgumentException("the paramter subProjectPartValueObject is null");
		}
		
		String subProjectName = subProjectPartValueObject.getProjectName();
		subProjectNamelist.add(subProjectName);
		subProjectPartValueObjectHash.put(subProjectName, subProjectPartValueObject);
	}
	
	public List<String> getSubProjectNamelist() {
		return subProjectNamelist;
	}

	public boolean isRegistedProjectName(String projectName) {
		return (null != subProjectPartValueObjectHash.get(projectName));
	}

	public ProjectPartConfiguration getSubProjectPart(String projectName) {
		return subProjectPartValueObjectHash.get(projectName);
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("AllProjectPart [projectNamelist=");
		builder.append(subProjectNamelist != null ? subProjectNamelist.subList(
				0, Math.min(subProjectNamelist.size(), maxLen)) : null);
		builder.append("]");
		return builder.toString();
	}
}
