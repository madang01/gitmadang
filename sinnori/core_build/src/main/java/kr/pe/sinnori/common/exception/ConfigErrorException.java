package kr.pe.sinnori.common.exception;

/**
 * 신놀이 환경 변수들을 처리하기 위한 사전 작업시 에러를 만날때 던지는 예외
 * @author "Won Jonghoon"
 *
 */
@SuppressWarnings("serial")
public class ConfigErrorException extends Exception {
	/**
	 * <pre>
	 * 신놀이 환경 변수들을 처리하기 위한 사전 작업시 에러를 만날때 던지는 예외 생성자
	 * >> 신놀이 환경 변수의 값을 검사하기 위한 사전 작업 <<
	 * - 신놀이 환경 변수를 담은 내용 중 아래와 같은 경우 에러 - 
	 * (1) 프로젝트 목록을 지정하는 키가 없을 경우
	 * (2) 프로젝트 목록 값으로 부터 프로젝트 목록을 추출할 수 없는 경우
	 * (3) 프로젝트 목록에 지정된 메인 프로젝트에 대한 정보가 없는 경우
	 * (4) 프로젝트 목록의 프로젝트들중 이름들중 중복된 것이 있는 경우
	 * (5) 알 수 없는 키가 존재할 경우
	 * - 환경 변수 값을 검사하기 위한 정보 클래스 객체 생성시 아래와 같은 경우 에러  -
	 * (1) 디폴트 값 검사 수행시 디폴트 값이 잘못된 경우
	 * (2) 잘못된 파라미터 전달시
	 * </pre>
	 * @param errorMessage 에러 내용
	 */
	public ConfigErrorException(String errorMessage) {
		super(errorMessage);
	}
}
