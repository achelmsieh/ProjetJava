package com.alstom.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class AgendaExtractor {
	private static XSSFWorkbook wb = null;
	private static XSSFSheet sheet = null;

	public static List<String> getOFs(Date date) {
		initSheet();

		if (sheet == null)
			return null;

		int colIndex = -1;

		colIndex = getColIndex(date);
		if (colIndex >= 0) {
			List<String> kk = getListOfs(++colIndex);
			return kk;
		}

		return null;
	}

	private static void initSheet() {
		File dir = FileManager.getOrdreJoursDir();
		if (dir == null)
			return;

		File currWeekPlanningFile = new File(dir + FileManager.SysSeparator() + "fichier ORDO MAJ 12.10.xlsx");

		if (currWeekPlanningFile == null)
			return;

		wb = getWorkbook(currWeekPlanningFile);
		if (wb == null)
			return;

		sheet = wb.getSheetAt(0);
	}

	private static XSSFWorkbook getWorkbook(File dataFile) {
		FileInputStream fis;

		try {
			fis = new FileInputStream(dataFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		try {
			return new XSSFWorkbook(fis);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static List<String> getListOfs(int colIndex) {
		List<String> ofs = new ArrayList<String>();

		for (Row row : sheet) {
			if (row != null) {
				Cell cell = row.getCell(colIndex);
				if (cell != null) {
					String val = getCellValueAsString(cell);
					if (val != null && val.matches("\\d{8}"))
						ofs.add(val);
				}
			}
		}

		return ofs;
	}

	private static String getCellValueAsString(Cell cell) {
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			return new BigDecimal(cell.getNumericCellValue()).toPlainString();
		default:
			return null;
		}
	}

	private static int getColIndex(Date date) {
		int index;
		for (Row row : sheet) {
			index = 0;
			if (row != null) {
				for (Cell cell : row) {
					if (cell.getCellType() == CellType.NUMERIC && date.equals(cell.getDateCellValue()))
						return index;

					index++;
				}
			}
		}
		return -1;
	}

}
