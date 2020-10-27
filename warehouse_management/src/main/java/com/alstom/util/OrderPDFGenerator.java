package com.alstom.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.alstom.model.Kit;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class OrderPDFGenerator {
	private static Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK);

	public static boolean generate(Stage stage, String docTitle, List<Kit> kits) {
		File targetFile = getTargetFile(stage, docTitle);

		if (targetFile == null)
			return false;

		Document document = new Document();
		try {
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(targetFile));
			makeDoc(document, docTitle, kits);
			writer.close();
			return true;
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	private static Document makeDoc(Document document, String docTitle, List<Kit> kits) throws Exception {
		document.open();

		setDocHeader(document);
		espace(document);

		setDocTitle(document, docTitle);
		espace(document);

		SetCorp(kits, document);
		espace(document);

		SetButtom(document);

		document.close();
		return document;
	}

	private static void setDocHeader(Document document) throws Exception {
		PdfPTable table = new PdfPTable(3);

		Path logoPath = Paths.get(ClassLoader.getSystemResource("icons/logo.png").toURI());
		Image logo = Image.getInstance(logoPath.toAbsolutePath().toString());
		logo.scalePercent(20);

		PdfPCell logoCell = new PdfPCell(logo);
		logoCell.setBorder(0);
		logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(logoCell);

		PdfPCell blankCell = new PdfPCell();
		blankCell.setBorder(0);
		table.addCell(blankCell);

		Path alstomPath = Paths.get(ClassLoader.getSystemResource("icons/Alstom.png").toURI());
		Image alstom = Image.getInstance(alstomPath.toAbsolutePath().toString());
		alstom.scalePercent(10);

		PdfPCell alstomCell = new PdfPCell(alstom);
		alstomCell.setBorder(0);
		alstomCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(alstomCell);

		document.add(table);
	}

	private static void espace(Document document) throws DocumentException {
		document.add(Chunk.NEWLINE);
	}

	private static void setDocTitle(Document document, String title) throws DocumentException {
		font.setSize(18);
		font.setStyle("normal");
		Paragraph p = new Paragraph(title, font);
		p.setAlignment(Element.ALIGN_CENTER);
		document.add(p);
	}

	private static void SetCorp(List<Kit> list, Document document) throws DocumentException {

		PdfPTable table = new PdfPTable(3);

		PdfPCell pdfCell = new PdfPCell(new Phrase("OF", font));
		pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pdfCell.setVerticalAlignment(Element.ALIGN_CENTER);
		pdfCell.setPadding(5);
		table.addCell(pdfCell);

		pdfCell.setPhrase(new Phrase("Projet", font));
		table.addCell(pdfCell);

		pdfCell.setPhrase(new Phrase("Signature", font));
		table.addCell(pdfCell);

		font.setSize(16);
		font.setStyle("normal");
		list.stream().forEach(i -> {
			PdfPCell pdfCell_1 = new PdfPCell(new Phrase(i.getOF(), font));
			pdfCell_1.setHorizontalAlignment(Element.ALIGN_CENTER);
			pdfCell_1.setVerticalAlignment(Element.ALIGN_CENTER);
			pdfCell_1.setPadding(8);
			table.addCell(pdfCell_1);

			pdfCell_1.setPhrase(new Phrase(i.getProjet(), font));
			table.addCell(pdfCell_1);

			pdfCell_1.setPhrase(new Phrase("", font));
			table.addCell(pdfCell_1);
		});
		document.add(table);
	}

	private static void SetButtom(Document document) throws DocumentException {
		Font font2 = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK);
		font2.setSize(15);
		font2.setColor(BaseColor.GRAY);
		Paragraph ph = new Paragraph("Zone de coupe", font2);
		ph.setAlignment(Element.ALIGN_CENTER);
		document.add(ph);
	}

	private static File getTargetFile(Stage stage, String name) {
		FileChooser fileChooser = new FileChooser();

		String separator = System.getProperty("file.separator");
		String initialDirectoryName = System.getProperty("user.home") + separator + "Desktop";
		File initialDirectory = new File(initialDirectoryName);
		if (!initialDirectory.exists()) {
			initialDirectory.mkdir();
		}
		fileChooser.setInitialDirectory(initialDirectory);
		fileChooser.setInitialFileName(name);

		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
		fileChooser.getExtensionFilters().add(extFilter);

		File selectedFile = fileChooser.showSaveDialog(stage);

		return selectedFile;
	}

}
