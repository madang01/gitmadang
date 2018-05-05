package kr.pe.codda.common.util;

import java.io.File;
import java.util.Comparator;

public class NameFirstComparator implements Comparator<File> {
	public int compare(File f1, File f2) {		
		return f1.getName().compareTo(f2.getName());
	}
}
