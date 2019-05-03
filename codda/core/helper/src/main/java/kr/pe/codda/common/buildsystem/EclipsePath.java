package kr.pe.codda.common.buildsystem;

public class EclipsePath {
	private String pathName;
	private String relativePathString;
	
	public EclipsePath(String pathName, String relativePath) {
		this.pathName = pathName;
		this.relativePathString = relativePath;
	}

	public String getPathName() {
		return pathName;
	}

	public String getRelativePath() {
		return relativePathString;
	}
	
	
}
