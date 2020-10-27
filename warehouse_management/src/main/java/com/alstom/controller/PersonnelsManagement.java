package com.alstom.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

import com.alstom.model.Personnel;
import com.alstom.model.enums.PersonnelRole;
import com.alstom.service.PersonnelService;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class PersonnelsManagement implements Initializable {

	@FXML
	private HBox HboxAjout;

	@FXML
	private HBox HboxModif;

	PersonnelService ps = new PersonnelService();
	@FXML
	private VBox VboxAdd;

	@FXML
	private RadioButton ResStockRadio;

	@FXML
	private ToggleGroup R1;

	@FXML
	private RadioButton ResProdRadio;
	@FXML
	private RadioButton AdminRadio;

	@FXML
	private JFXTextField MatRes;

	@FXML
	private JFXTextField NomRes;

	@FXML
	private JFXTextField PreNomRes;

	@FXML
	private JFXPasswordField PasRes;

	@FXML
	private TableView<Personnel> PersonnelTable;

	@FXML
	private TableColumn<Personnel, String> MatriculeColumn;

	@FXML
	private TableColumn<Personnel, String> NomColumn;

	@FXML
	private TableColumn<Personnel, String> PrenomColumn;

	@FXML
	private TableColumn<Personnel, String> FonctionColumn;

	@FXML
	private TableColumn<Personnel, String> MDPColumn;

	@FXML
	private ImageView AddImage;

	@FXML
	private ImageView ModifImage;

	@FXML
	private ImageView RemoveImage;

	public Personnel personne = null;

	@FXML
	private void ModifyPer() {
		if (checkinput()) {
			personne = PersonnelTable.getSelectionModel().getSelectedItem();
			personne.setNom(NomRes.getText());
			personne.setPrenom(PreNomRes.getText());
			personne.setMatricule(MatRes.getText());
			if (AdminRadio.isSelected()) {
				personne.setMotDePasse(PasRes.getText());
				personne.setRole(PersonnelRole.ADMIN);
			} else if (ResStockRadio.isSelected()) {
				personne.setMotDePasse(PasRes.getText());
				personne.setRole(PersonnelRole.RES_STOCK);
			} else
				personne.setRole(PersonnelRole.RES_PRODUCTION);
			ps.save(personne);
			showAlert(AlertType.INFORMATION, "Responsable de " + personne.getRole().toString() + " : "
					+ personne.getMatricule() + " est modifié.");

			setVisibility(VboxAdd, false);
			setTableItems(ps.getPersonnels());
			ClearInput();
		}

	}

	@FXML
	void AddPer(ActionEvent event) {
		// s'assurer que les champs tout sont remplis
		if (checkinput()) {
			Personnel res = new Personnel();
			res.setNom(NomRes.getText());
			res.setPrenom(PreNomRes.getText());
			res.setMatricule(MatRes.getText());
			if (AdminRadio.isSelected()) {
				res.setMotDePasse(PasRes.getText());
				res.setRole(PersonnelRole.ADMIN);
			}
			if (ResStockRadio.isSelected()) {
				res.setMotDePasse(PasRes.getText());
				res.setRole(PersonnelRole.RES_STOCK);
			} else
				res.setRole(PersonnelRole.RES_PRODUCTION);
			ps.save(res);
			showAlert(AlertType.INFORMATION, "Responsable " + res.getRole().toString() + "Ajoutée");

			setVisibility(VboxAdd, false);
			setTableItems(ps.getPersonnels());
			ClearInput();
		}

	}

	private void ClearInput() {
		PasRes.clear();
		NomRes.clear();
		PreNomRes.clear();
		MatRes.clear();
	}

	private boolean checkinput() {
		String erreur = "";
		if ((ResStockRadio.isSelected() || AdminRadio.isSelected()) && PasRes.getText().isEmpty())
			erreur += " Mot de passe";
		if (NomRes.getText().isEmpty())
			erreur += " Nom";
		if (PreNomRes.getText().isEmpty())
			erreur += " Prenom";
		if (MatRes.getText().isEmpty())
			erreur += " Matricule";
		if (erreur.isEmpty())
			return true;
		showAlert(AlertType.ERROR, "Veuillez Saisir les information suivant :" + erreur);
		return false;
	}

	private void showAlert(AlertType type, String msg) {
		Alert alert = new Alert(type);
		alert.setContentText(msg);
		alert.show();
	}

	private void setVisibility(Node node, Boolean etat) {
		node.setVisible(etat);
		node.managedProperty().bind(node.visibleProperty());
	}

	@FXML
	void FermerForm(ActionEvent event) {
		setVisibility(VboxAdd, false);

	}

	@FXML
	void HidPass(ActionEvent event) {
		HidPasse_etat(true);
	}

	void HidPasse_etat(Boolean etat) {
		PasRes.setVisible(!etat);
		PasRes.managedProperty().bind(PasRes.visibleProperty());
	}

	@FXML
	void ShowPass(ActionEvent event) {
		PasRes.setVisible(true);
		PasRes.managedProperty().bind(PasRes.visibleProperty());

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initCols();
		setDoubleClickEvent();

	}

	private void initCols() {
		MatriculeColumn.setCellValueFactory(
				cellDataFeatures -> new SimpleStringProperty(cellDataFeatures.getValue().getMatricule()));

		NomColumn.setCellValueFactory(
				cellDataFeatures -> new SimpleStringProperty(cellDataFeatures.getValue().getNom()));
		PrenomColumn.setCellValueFactory(
				cellDataFeatures -> new SimpleStringProperty(cellDataFeatures.getValue().getPrenom()));
		FonctionColumn.setCellValueFactory(
				cellDataFeatures -> new SimpleStringProperty(cellDataFeatures.getValue().getRole().toString()));
		MDPColumn.setCellValueFactory(
				cellDataFeatures -> new SimpleStringProperty(cellDataFeatures.getValue().getMotDePasse()));
//		ps.save(new Personnel("gu", "acrah", "acrauuh", PersonnelRole.RES_STOCK, "aciirah"));
//		ps.save(new Personnel("acrah", "acruuah", "acuurah", PersonnelRole.Res_production, "aiicrah"));
//		ps.save(new Personnel("aciuiurah", "acuurah", "acrah", Personnel.Role.Res_stock, "acuurah"));
//		ps.save(new Personnel("acrah", "acrhuah", "acrah", Personnel.Role.Res_production, "acjjrah"));
//		ps.save(new Personnel("acuhuurah", "acrah", "aciirah", Personnel.Role.Res_stock, "auucrah"));
		setTableItems(ps.getPersonnels());
	}

	private void setDoubleClickEvent() {
		PersonnelTable.setRowFactory(tableView -> {
			TableRow<Personnel> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 1 && (!row.isEmpty())) {
					setVisibility(VboxAdd, true);
					setVisibility(HboxAjout, false);
					setVisibility(HboxModif, true);
					personne = tableView.getSelectionModel().getSelectedItem();
					MatRes.setText(personne.getMatricule());
					NomRes.setText(personne.getNom());
					PreNomRes.setText(personne.getPrenom());
					PasRes.setText(personne.getMotDePasse());
					if (personne.getRole() == PersonnelRole.ADMIN) {
						HidPasse_etat(false);
						AdminRadio.setSelected(true);
					} else if (personne.getRole() == PersonnelRole.RES_PRODUCTION) {
						HidPasse_etat(true);
						ResProdRadio.setSelected(true);
					} else {
						HidPasse_etat(false);
						ResStockRadio.setSelected(true);
					}

				}

			});
			return row;
		});
	}

	private void setTableItems(List<Personnel> list) {
		ObservableList<Personnel> personnel = FXCollections.observableArrayList(list);
		PersonnelTable.setItems(personnel);
		PersonnelTable.refresh();
	}

	@FXML
	void ModifPer(MouseEvent event) {
		modifyplatform();
	}

	@FXML
	void RemovePer(MouseEvent event) {
		if (personne == null)
			showAlert(AlertType.INFORMATION, "Veuillez selection un Employé");
		else {
			ps.remove(personne);
			setTableItems(ps.getPersonnels());
			ClearInput();
			setVisibility(VboxAdd, false);
			personne = null;

		}

	}

	@FXML
	void AjouterPer(MouseEvent event) {

		Addplatform();
	}

	void modifyplatform() {
		if (!HboxModif.isVisible()) {
			ClearInput();
			setVisibility(VboxAdd, true);
			setVisibility(HboxAjout, false);
			setVisibility(HboxModif, true);

		} else {
			setVisibility(VboxAdd, false);
			setVisibility(HboxModif, false);
		}
	}

	void Addplatform() {
		if (!HboxAjout.isVisible()) {
//			AddImage.scaleXProperty();
//			AddImage.scaleYProperty()
			ClearInput();
			setVisibility(VboxAdd, true);
			setVisibility(HboxAjout, true);
			setVisibility(HboxModif, false);

		}

		else {
			setVisibility(VboxAdd, false);
			setVisibility(HboxAjout, false);
		}

	}

	private Stage getStage() {
		return (Stage) PersonnelTable.getScene().getWindow();
	}

	private File getTargetFile(Stage stage, String name) {
		FileChooser fileChooser = new FileChooser();

		String initialDirectoryName = System.getProperty("user.home");
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

	private Document make_doc(Document document, PdfWriter writer) throws Exception {
		document.open();

		setDocHeader(document);
		space(document);
		setDocTitle(document, "Badge Approvisionneur".toUpperCase());
		space(document);
		String name = NomRes.getText().toUpperCase() + " " + PreNomRes.getText();
		setDocName(document, name);
		SetBody(document, writer);

		document.close();
		return document;
	}

	private void setDocHeader(Document document) throws Exception {
		PdfPTable table = new PdfPTable(3);

		Path logoPath = Paths.get(ClassLoader.getSystemResource("icons/logo.png").toURI());
		Image logo = Image.getInstance(logoPath.toAbsolutePath().toString());
		logo.scalePercent(12);

		PdfPCell logoCell = new PdfPCell(logo);
		logoCell.setBorder(0);
		logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		logoCell.setPaddingTop(5);
		table.addCell(logoCell);

		PdfPCell blankCell = new PdfPCell();
		blankCell.setBorder(0);
		table.addCell(blankCell);

		Path alstomPath = Paths.get(ClassLoader.getSystemResource("icons/Alstom.png").toURI());
		Image alstom = Image.getInstance(alstomPath.toAbsolutePath().toString());
		alstom.scalePercent(5);

		PdfPCell alstomCell = new PdfPCell(alstom);
		alstomCell.setBorder(0);
		alstomCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		alstomCell.setPaddingTop(10);
		table.addCell(alstomCell);

		document.add(table);
	}
	
	private void space(Document document) throws DocumentException {
		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);
	}

	private void setDocTitle(Document document, String title) throws DocumentException {
		Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK);
		font.setStyle("underline");
		Paragraph p = new Paragraph(title, font);
		p.setAlignment(Element.ALIGN_CENTER);
		document.add(p);
	}

	private void setDocName(Document document, String title) throws DocumentException {
		Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, BaseColor.BLACK);
		font.setStyle("normal");
		Paragraph p = new Paragraph(title, font);
		p.setAlignment(Element.ALIGN_CENTER);
		document.add(p);
	}

	void SetBody(Document document, PdfWriter writer) {
//		 PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));

		PdfContentByte canvas = writer.getDirectContent();
		Barcode128 code128 = new Barcode128();
		code128.setCode(MatRes.getText());
		code128.setCodeType(Barcode128.CODE128);
		code128.setX(2);
		PdfTemplate template = code128.createTemplateWithBarcode(canvas, BaseColor.BLACK, BaseColor.BLACK);
//		float x = 36;
//		float y = 750;
		float w = template.getWidth();
		float h = template.getHeight();
		float pw = document.getPageSize().getWidth();
		float ph = document.getPageSize().getHeight();
		float x = pw / 2 - w / 2;
		float y = 30;
		canvas.saveState();
		canvas.setColorFill(BaseColor.WHITE);
		canvas.rectangle(x, y, w, h);
		canvas.fill();
		canvas.restoreState();
		canvas.addTemplate(template, x, y);

	}

	@FXML
	void ImprimerMat(MouseEvent event) {
		if (!MatRes.getText().isEmpty()) {
			String docTitle = "Matricule_" + MatRes.getText();
			File targetFile = getTargetFile(getStage(), docTitle);

			if (targetFile == null)
				return;

			float dw = 3.375f * 72, dh = 2.125f * 72;
			Rectangle pagesize = new Rectangle(dw, dh);
			Document document = new Document(pagesize);
			document.setMargins(0, 0, 0, 0);
			try {
				PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(targetFile));
				make_doc(document, writer);
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
