package com.alstom.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FileUploader {

	public static boolean upload(Stage stage) {
		FileChooser fileChooser = new FileChooser();

		String separator = System.getProperty("file.separator");
		String initialDirectoryName = System.getProperty("user.home") + separator + "Desktop";

		File initialDirectory = new File(initialDirectoryName);
		if (!initialDirectory.exists()) {
			return false;
		}

		fileChooser.setInitialDirectory(initialDirectory);

		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
		fileChooser.getExtensionFilters().add(extFilter);

//		File selectedFile = fileChooser.showSaveDialog(stage);
		File selectedFile = fileChooser.showOpenDialog(stage);
		File dir = new File(ExcelDataReader.getTargetDirName());

		try {
			copyFiles(selectedFile, dir);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void copyFiles(File sourceFile, File targetLocation) throws IOException {

//		if (sourceLocation.isDirectory()) {
//			if (!targetLocation.exists()) {
//				targetLocation.mkdir();
//			}
//			File[] files = sourceLocation.listFiles();
//			for (File file : files) {
		if (!sourceFile.exists())
			return;
		InputStream in = new FileInputStream(sourceFile);
		OutputStream out = new FileOutputStream(targetLocation + "/" + sourceFile.getName());

		// Copy the bits from input stream to output stream
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
//			}
	}
//	}

}
