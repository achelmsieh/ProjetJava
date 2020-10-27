package com.alstom.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alstom.model.Kit;
import com.alstom.model.enums.EtatKit;

public class PlanningDataExtrator {

	private static XSSFWorkbook wb = null;
	private static XSSFSheet sheet = null;
	private static final Map<String, Date> DSP = OrderDataExtrator.getKitsDSP();

	public static List<Kit> getKits(File file) {
		if (file == null)
			return null;

		initSheet(file);
		if (sheet == null)
			return null;

		initColsIndexes();
		List<Kit> rsltKits = new ArrayList<Kit>();
		for (Row row : sheet) {
			if (row != null) {
				String OF = getCellValueAsString(row, "ORDER");
				if (isOF(OF)) {
					rsltKits.add(makeKit(row));
				}
			}
		}

		return rsltKits;
	}

	private static Kit makeKit(Row row) {
		Kit kit = new Kit();
		kit.setOF(getCellValueAsString(row, "ORDER"));
		kit.setDTR(getCellValueAsString(row, "DTR"));
		kit.setRunTime(getDoubleCellValue(row, "RUN TIME"));
		kit.setProjet(getCellValueAsString(row, "PROJET"));
		kit.setDescription(getCellValueAsString(row, "Description"));
		kit.setnRAME((int) getDoubleCellValue(row, "N° de rame"));
		kit.setIndiceCPC(getCellValueAsString(row, "Indice CPC"));
		kit.setEtat(EtatKit.PLANNING);
		kit.setDateSortiePrevue(DSP.get(kit.getOF()));

		return kit;
	}

	private static boolean isOF(String OF) {
		return OF != null && OF.matches("\\d{8}");
	}

	private static void initSheet(File file) {
		wb = getWorkbook(file);
		if (wb == null)
			return;

		sheet = wb.getSheetAt(0);
	}

	private static XSSFWorkbook getWorkbook(File dataFile) {
		FileInputStream fis;

		try {
			fis = new FileInputStream(dataFile);
		} catch (FileNotFoundException e) {
			return null;
		}

		try {
			return new XSSFWorkbook(fis);
		} catch (IOException e) {
			return null;
		}
	}

	private static Map<String, Integer> initColsIndexes() {
		Map<String, Integer> colsIndexes = new HashMap<String, Integer>();

		colsIndexes.put("ORDER", getColIndex("ORDER"));
		colsIndexes.put("RUN TIME", getColIndex("RUN TIME"));
		colsIndexes.put("DTR", getColIndex("DTR"));
		colsIndexes.put("PROJET", getColIndex("PROJET"));
		colsIndexes.put("Description", getColIndex("Description"));
		colsIndexes.put("N° de rame", getColIndex("N° de rame"));
		colsIndexes.put("Indice CPC", getColIndex("Indice CPC"));

		return colsIndexes;
	}

	private static int getColIndex(String cellName) {
		Row firsRow = sheet.getRow(0);
		int index = 0;
		if (firsRow != null) {
			for (Cell cell : firsRow) {
				if (cellName.equalsIgnoreCase(cell.getStringCellValue().trim()))
					return index;

				index++;
			}
		}
		return -1;
	}

	private static String getStringCellValue(Row row, String colName) {
		int colIndex = getColIndex(colName);

		if (colIndex < 0)
			return null;
		if (row.getCell(colIndex) == null)
			return null;
		return row.getCell(colIndex).getStringCellValue();
	}

	private static double getDoubleCellValue(Row row, String colName) {
		int colIndex = getColIndex(colName);

		if (colIndex < 0)
			return 0;
		if (row.getCell(colIndex) == null)
			return 0;

		return row.getCell(colIndex).getNumericCellValue();
	}

	private static String getCellValueAsString(Row row, String colName) {
		int colIndex = getColIndex(colName);

		if (colIndex < 0)
			return null;

		Cell cell = row.getCell(colIndex);
		if (cell == null)
			return null;

		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			return new BigDecimal(cell.getNumericCellValue()).toPlainString();
		default:
			return null;
		}
	}
}
