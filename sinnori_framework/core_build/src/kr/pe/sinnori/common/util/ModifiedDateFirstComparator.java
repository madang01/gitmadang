/**
 * 출처 : http://wwhite103.tistory.com/42
 */
package kr.pe.sinnori.common.util;

import java.io.File;
import java.util.Comparator;

public class ModifiedDateFirstComparator implements Comparator<File> {

	public int compare(File f1, File f2) {

		// 수정날짜의 값이 더크면 -1리턴, -1을 리턴하면 첫번째것이 앞으로 간다.
		if (f1.lastModified() > f2.lastModified())
			return 1;

		// 같으면 0
		if (f1.lastModified() == f2.lastModified())
			return 0;

		// 작으면 1
		return -1;
	}
}