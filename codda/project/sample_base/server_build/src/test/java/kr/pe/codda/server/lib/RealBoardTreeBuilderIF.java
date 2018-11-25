package kr.pe.codda.server.lib;


public interface RealBoardTreeBuilderIF {
	
	/**
	 * 실 DB 작업이 이루어진 게시판 트리를 반환한다.
	 * @param workingDBName 작업중인 DB 이름
	 * @param virtualBoardTreeBuilder 테스트할 게시판 트리를 만들어주는 빌더
	 * @param boardType 게시판 종류
	 * @return 실 DB 작업이 이루어진 게시판 트리
	 */
	public BoardTree build(String workingDBName, 
			VirtualBoardTreeBuilderIF  virtualBoardTreeBuilder, BoardType boardType);
}
