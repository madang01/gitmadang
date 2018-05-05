package kr.pe.codda.common.util;

import java.io.File;
import java.util.Comparator;

/**
 * <pre>
 * 파일 최근 수정일 역순 출력을 위한 비교자 클래스.
 * 
 * === code sample start ===
 * File sortWantedFiles[] = dir.listFiles(new XMLFileFilter(); 
 * Arrays.sort(sortWantedFiles, new FileLastModifiedComparator());
 * === code sample end ===
 * </pre>
 * @author Won Jonghoon
 *
 */
public class FileLastModifiedComparator implements Comparator<File> {

	@Override
	public int compare(File o1, File o2) {
		return Long.compare(o2.lastModified(), o1.lastModified());
	}
}
