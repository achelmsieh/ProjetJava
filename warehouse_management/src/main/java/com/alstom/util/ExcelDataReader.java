package com.alstom.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alstom.model.Kit;

public class ExcelDataReader {
	private static XSSFWorkbook wb = null;
	private static XSSFSheet sheet = null;

	public static Kit getKitInfos(String of) throws Exception {
		File dir = new File(getTargetDirName());
		if (dir == null)
			return null;

		File currWeekPlanningFile = new File(dir + SysSeparator() + currWeekPlanningFileName());
		int rowIndex = -1;

		if (currWeekPlanningFile != null) {
			rowIndex = getRowIndex(currWeekPlanningFile, of);
		}

		if (rowIndex >= 0)
			return makeKit(sheet, rowIndex);

		List<File> directoryFiles = listOfFiles();
		if (directoryFiles != null) {
			for (File childFile : directoryFiles) {
				rowIndex = getRowIndex(childFile, of);

				if (rowIndex >= 0)
					return makeKit(sheet, rowIndex);
			}
		}

		return null;
	}

	public static List<File> listOfFiles() {
		return Stream.of(new File(getTargetDirName()).listFiles()).filter(file -> !file.isDirectory())
				.collect(Collectors.toList());
	}

	private static int getRowIndex(File file, String of) {
		int rowIndex = -1;
		wb = getWorkbook(file);
		if (wb != null) {
			sheet = wb.getSheetAt(0);
			if (sheet != null)
				rowIndex = findRow(sheet, Integer.valueOf(of));
		}

		return rowIndex;
	}

	private static XSSFWorkbook getWorkbook(File dataFile) {
		FileInputStream fis;

		try {
			fis = new FileInputStream(dataFile);
		} catch (FileNotFoundException e) {
//			e.printStackTrace();
			return null;
		}

		try {
			return new XSSFWorkbook(fis);
		} catch (IOException e) {
//			e.printStackTrace();
			return null;
		}
	}

	private static int findRow(XSSFSheet sheet, int cellContent) {
		for (Row row : sheet) {
			for (Cell cell : row) {
				if (cell.getCellType() == CellType.NUMERIC) {
					if (cell.getNumericCellValue() == cellContent) {
						return row.getRowNum();
					}
				}
			}
		}
		return -1;
	}

	private static Kit makeKit(XSSFSheet sheet, int rowIndex) {
		Kit kit = new Kit();
		kit.setDTR(getStringCellValue(sheet, rowIndex, "DTR"));
		kit.setRunTime(getDoubleCellValue(sheet, rowIndex, "RUN TIME"));
		kit.setProjet(getStringCellValue(sheet, rowIndex, "PROJET"));
		kit.setDescription(getStringCellValue(sheet, rowIndex, "Description"));
		kit.setnRAME((int) getDoubleCellValue(sheet, rowIndex, "N° de rame"));
		kit.setIndiceCPC(getStringCellValue(sheet, rowIndex, "Indice CPC"));

		return kit;
	}

	private static String getStringCellValue(XSSFSheet sheet, int rowIndex, String colName) {
		Row firsRow = sheet.getRow(0);
		Row row = sheet.getRow(rowIndex);

		if (firsRow == null || row == null)
			return null;

		int colIndex = getColIndex(firsRow, colName);

		if (colIndex < 0)
			return null;
		return row.getCell(colIndex).getStringCellValue();
	}

	private static double getDoubleCellValue(XSSFSheet sheet, int rowIndex, String colName) {
		Row firsRow = sheet.getRow(0);
		Row row = sheet.getRow(rowIndex);

		if (firsRow == null || row == null)
			return 0;

		int colIndex = getColIndex(firsRow, colName);

		if (colIndex < 0)
			return 0;
		return row.getCell(colIndex).getNumericCellValue();
	}

	private static int getColIndex(Row row, String cellName) {
		int index = 0;
		if (row != null) {
			for (Cell cell : row) {
				if (cellName.equalsIgnoreCase(cell.getStringCellValue().trim()))
					return index;

				index++;
			}
		}
		return -1;
	}

	private static String currWeekPlanningFileName() {
		SimpleDateFormat sdf = new SimpleDateFormat("w");
		String semaine = sdf.format(new Date());
		return "Planning S" + semaine + ".xlsx";
	}

	private static String SysSeparator() {
		return System.getProperty("file.separator");
	}

	public static String getTargetDirName() {
		return System.getProperty("user.home") + SysSeparator() + "Documents" + SysSeparator() + "ALSTOM_COUPURE";
	}

//	private static void displayData(XSSFSheet sheet) {
//		Iterator<Row> itr = sheet.iterator();
//		while (itr.hasNext()) {
//			Row row = itr.next();
//			Iterator<Cell> cellIterator = row.cellIterator();
//
//			while (cellIterator.hasNext()) {
//				Cell cell = cellIterator.next();
//				switch (cell.getCellType()) {
//				case STRING:
//					System.out.print(cell.getStringCellValue() + "\t\t\t");
//					break;
//				case NUMERIC:
//					System.out.print(cell.getNumericCellValue() + "\t\t\t");
//					break;
//
//				default:
//					break;
//				}
//			}
//			System.out.println();
//		}
//	}
}
