package kr.pe.sinnori.common.config.configvo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllSubProjectPartConfigurationVO {
	private List<String> subProjectNamelist = new ArrayList<String>();
	private HashMap<String, ProjectPartConfigurationVO> subProjectPartValueObjectHash = 
			new HashMap<String, ProjectPartConfigurationVO>();

	public void clear() {
		subProjectNamelist.clear();
		subProjectPartValueObjectHash.clear();
	}
	public void addSubProjectPartValueObject(ProjectPartConfigurationVO subProjectPartValueObject) {
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

	public ProjectPartConfigurationVO getSubProjectPart(String projectName) {
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
