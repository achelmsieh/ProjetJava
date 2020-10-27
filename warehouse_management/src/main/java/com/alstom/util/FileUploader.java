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

	public enum FileType {
		PLANNING, ORDER
	}

	public static File upload(Stage stage, FileType fileType) {
		FileChooser fileChooser = new FileChooser();

		String separator = System.getProperty("file.separator");
		String initialDirectoryName = System.getProperty("user.home") + separator + "Desktop";

		File initialDirectory = new File(initialDirectoryName);
		if (initialDirectory.exists()) {
			fileChooser.setInitialDirectory(initialDirectory);
		}

		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
		fileChooser.getExtensionFilters().add(extFilter);

		File selectedFile = fileChooser.showOpenDialog(stage);
		File dir;
		if (fileType == FileType.PLANNING)
			dir = FileManager.getPlanningsDir();
		else
			dir = FileManager.getOrdreJoursDir();

		try {
			return copyFiles(selectedFile, dir);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static File copyFiles(File sourceFile, File targetLocation) throws IOException {

		if (sourceFile == null || !sourceFile.exists())
			return null;

		InputStream in = new FileInputStream(sourceFile);
		File outputFile = new File(targetLocation + FileManager.SysSeparator() + sourceFile.getName());
		OutputStream out = new FileOutputStream(outputFile);

		// Copy the bits from input stream to output stream
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();

		return outputFile;
	}

}
