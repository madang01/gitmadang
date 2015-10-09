package kr.pe.sinnori.common.config.itemidinfo;

public interface ProjectPartItemIDInfoMangerIF {
	/**
	 * 프로젝트 파트 소속 '항목 식별자 정보'를 등록한다. 
	 * 단 '항목 식별자 정보'가 프로젝트 파트 소속이 아닐 경우 IllegalArgumentException 을 던진다.
	 * 그리고 싱글턴인 신놀이 항목 식별자 정보 관리자 생성자 작업을 마친후에는 호출할 수 없다.
	 * 만약 호출시 UnsupportedOperationException 예외를 던진다.
	 * 
	 * @param itemIDInfo '항목 식별자 정보'
	 * @throws IllegalArgumentException 파라미터 '항목 식별자 정보'의 값이 잘못되었을 경우 던지는 예외, ex) 프로젝트 파트 소속이 아닌 '항목 식별자 정보'
	 * @throws UnsupportedOperationException 싱글턴인 신놀이 항목 식별자 정보 관리자의 생성자 작업이 끝난 이후 호출시 던지는 예외
	 */
	public void addProjectPartItemIDInfo(ItemIDInfo<?> itemIDInfo) 
			throws IllegalArgumentException, UnsupportedOperationException;
}
