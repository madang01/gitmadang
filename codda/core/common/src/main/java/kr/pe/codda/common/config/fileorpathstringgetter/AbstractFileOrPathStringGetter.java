package kr.pe.codda.common.config.fileorpathstringgetter;

/**
 * 신놀이 설치 경로에 의존하는 파일이나 경 타입 항목의 값을 정의하는 최상위 추상화 클래스.
 * 
 * @author Won Jonghoon
 * 
 */
public abstract class AbstractFileOrPathStringGetter {
	protected String itemID;

	/**
	 * 생성자
	 * 
	 * @param itemID
	 *            항목 식별자
	 */
	public AbstractFileOrPathStringGetter(String itemID) {
		this.itemID = itemID;
	}

	
	public abstract String getFileOrPathStringDependingOninstalledPath(
			String installedPathString, String mainProjectName, 
			String... etcParamter);

	/**
	 * @return 항목 식별자
	 */
	public String getItemID() {
		return itemID;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AbstractFileOrPathStringGetter [itemID=");
		builder.append(itemID);
		builder.append("]");
		return builder.toString();
	}
}
