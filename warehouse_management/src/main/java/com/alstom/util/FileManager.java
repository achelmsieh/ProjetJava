package com.alstom.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileManager {

	public static String SysSeparator() {
		return System.getProperty("file.separator");
	}

	private static File getDocDir() {
		String docDirName = System.getProperty("user.home") + SysSeparator() + "Documents" + SysSeparator()
				+ "ALSTOM_COUPURE";

		File docDir = new File(docDirName);

		if (!docDir.exists() || !docDir.isDirectory()) {
			docDir.mkdir();
		}

		return docDir;
	}

	private static File getDocSubDir(String dirName) {
		File subDir = new File(getDocDir() + SysSeparator() + dirName);

		if (!subDir.exists() || !subDir.isDirectory()) {
			subDir.mkdir();
		}

		return subDir;
	}

	public static File getPlanningsDir() {
		return getDocSubDir("plannings");
	}

	public static File getOrdreJoursDir() {
		return getDocSubDir("ordres");
	}

	public static File currWeekPlanningFile() {
		SimpleDateFormat sdf = new SimpleDateFormat("w");
		String semaine = sdf.format(new Date());
		return new File(getPlanningsDir() + SysSeparator() + "Planning S43.xlsx");
//		return new File(getPlanningsDir() + SysSeparator() + "Planning S" + semaine + ".xlsx");
	}

}
