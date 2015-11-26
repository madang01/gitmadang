package kr.pe.sinnori.common.config.fileorpathstringgetter;

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

	/**
	 * 주어진 메인 프로젝트, 신놀이 설치 경로 그리고 기타 정보들에 맞는 파일 혹은 경로 문자열을 반환한다.
	 * 
	 * @param mainProjectName
	 *            메인 프로젝트 이름
	 * @param sinnoriInstalledPathString
	 *            신놀이 설치 경로
	 * @param etcParamter
	 *            문자열 타입 자바 가변 변수 '기타 정보들', 참고) dbcp 파트의 'dbcp 설정 파일' 항목의 값은 '기타정보들'로 'dbcp 이름' 이 사용된다.
	 * @return 주어진 메인 프로젝트, 신놀이 설치 경로 그리고 기타 정보들에 맞는 파일 혹은 경로 문자열
	 */
	public abstract String getFileOrPathStringDependingOnSinnoriInstalledPath(
			String mainProjectName, String sinnoriInstalledPathString,
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
