package kr.pe.sinnori.common.util;

import java.io.File;
import java.util.Comparator;

public class DirectoryFirstComparator implements Comparator<File> {
	public int compare(File f1, File f2) {

		// 디렉토리와 파일일 경우 
		if (f1.isFile() && f2.isDirectory())
			return 1;

		// 같으면 0
		if ((f1.isDirectory() && f2.isDirectory()) || (f1.isFile() && f2.isFile()))
			return 0;

		// 작으면 1
		return -1;
	}
}
