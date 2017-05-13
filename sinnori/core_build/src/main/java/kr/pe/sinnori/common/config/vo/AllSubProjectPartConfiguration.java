package kr.pe.sinnori.common.config.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllSubProjectPartConfiguration {
	private List<String> subProjectNamelist = new ArrayList<String>();
	private HashMap<String, ProjectPartConfiguration> subProjectPartConfigurationHash = 
			new HashMap<String, ProjectPartConfiguration>();

	public void clear() {
		subProjectNamelist.clear();
		subProjectPartConfigurationHash.clear();
	}
	public void addSubProjectPartValueObject(ProjectPartConfiguration subProjectPartValueObject) {
		if (null == subProjectPartValueObject) {
			throw new IllegalArgumentException("the paramter subProjectPartValueObject is null");
		}
		
		String subProjectName = subProjectPartValueObject.getProjectName();
		subProjectNamelist.add(subProjectName);
		subProjectPartConfigurationHash.put(subProjectName, subProjectPartValueObject);
	}
	
	public List<String> getSubProjectNamelist() {
		return subProjectNamelist;
	}

	public boolean isRegistedSubProjectName(String subProjectName) {
		return (null != subProjectPartConfigurationHash.get(subProjectName));
	}

	public ProjectPartConfiguration getSubProjectPartConfiguration(String projectName) {
		return subProjectPartConfigurationHash.get(projectName);
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("AllSubProjectPartValueObject [subProjectNamelist=");
		builder.append(subProjectNamelist != null ? subProjectNamelist.subList(
				0, Math.min(subProjectNamelist.size(), maxLen)) : null);
		builder.append("]");
		return builder.toString();
	}
}
