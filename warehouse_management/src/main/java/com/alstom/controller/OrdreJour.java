package com.alstom.controller;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.alstom.model.Emplacement;
import com.alstom.model.Kit;
import com.alstom.model.Personnel;
import com.alstom.util.FileUploader;
import com.alstom.util.FileUploader.FileType;
import com.alstom.util.OrderPDFGenerator;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.jfoenix.controls.JFXDatePicker;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class OrdreJour extends KitsGenericController implements Initializable {
	Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK);

	@FXML
	private TableView<Kit> table_ordre;
	@FXML
	private TableColumn<Kit, String> col_of;
	@FXML
	private TableColumn<Kit, String> col_emplacement;
	@FXML
	private TableColumn<Kit, String> col_projet;
	@FXML
	private TableColumn<Kit, String> col_etat;
	@FXML
	private TableColumn<Kit, String> col_date_entree;
	@FXML
	private TableColumn<Kit, String> col_date_sortie;
	@FXML
	private TableColumn<Kit, String> col_res_prod;
	@FXML
	private TableView<Kit> table_kits_manquants;
	@FXML
	private TableColumn<Kit, String> tkm_col_ofs;
	@FXML
	private TableView<Kit> table_kits_bloques;
	@FXML
	private TableColumn<Kit, String> tkb_col_ofs;
	@FXML
	private TextField search_field;
	@FXML
	private JFXDatePicker select_date;

	@FXML
	private Label manquantsLabel;
	@FXML
	private Label ordresLabel;
	@FXML
	private Label bloquesLabel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initTablesCols();
		initDataList(toDate(LocalDate.now()));

		select_date.setValue(LocalDate.now());
		select_date.setOnAction(event -> {
			Date df = toDate(select_date.getValue());
			initDataList(df);
		});

		initDoubleClickEvent();
	}

	@Override
	protected void search(String value) {
		super.search(value);
		Date df = toDate(select_date.getValue());
		initDataList(df);
	}

	private void initDoubleClickEvent() {
		setDoubleClickEvent(table_ordre);
		setDoubleClickEvent(table_kits_manquants);
		setDoubleClickEvent(table_kits_bloques);
	}

	private void initTablesCols() {
		initTableOrdreCols();
		initTableManquantCols();
		initTableBloquantCols();
	}

	private void initTableOrdreCols() {
		col_of.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getOF()));
		col_etat.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getEtat().toString()));
		col_projet.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getProjet()));

		col_date_entree.setCellValueFactory(cdf -> {
			Date de = cdf.getValue().getDateEntree();
			return new SimpleStringProperty(de == null ? "" : dateFormat.format(de));
		});
		col_date_sortie.setCellValueFactory(cdf -> {
			Date ds = cdf.getValue().getDateSortie();
			return new SimpleStringProperty(ds == null ? "" : dateFormat.format(ds));
		});

		col_emplacement.setCellValueFactory(cdf -> {
			if (cdf.getValue().getEmplacements() != null)
				return new SimpleStringProperty(cdf.getValue().getEmplacements().stream()
						.map(Emplacement::getCoordonnee).collect(Collectors.joining("\n")));
			else
				return new SimpleStringProperty("");
		});

		col_res_prod.setCellValueFactory(cdf -> {
			Personnel rp = cdf.getValue().getResProduction();
			return new SimpleStringProperty(rp == null ? "" : rp.getNom());
		});
	}

	private void initTableManquantCols() {
		tkm_col_ofs.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getOF()));
	}

	private void initTableBloquantCols() {
		tkb_col_ofs.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getOF()));
	}

	private void initDataList(Date date) {
		Date ceJour = toDate(LocalDate.now());

		List<Kit> ordres = kitService.getKitsOrder(date);

		long manquantsCount = 0, sortiesCount = 0, enStocksCount = 0, ordresCount = 0, bloquesCount = 0;
		if (ordres != null) {
			ordres = ordres.stream().sorted(Comparator.comparing(Kit::getEtat)).collect(Collectors.toList());
			ordresCount = ordres.size();
			enStocksCount = ordres.stream().filter(Kit::isEnStock).count();
			sortiesCount = ordres.stream().filter(Kit::isSortie).count();
		}

		List<Kit> manquants = kitService.getKitsManquants(ceJour);
		if (manquants != null)
			manquantsCount = manquants.size();
		List<Kit> bloques = kitService.getKitsBloques(ceJour);
		if (bloques != null)
			bloquesCount = bloques.size();

		setTableItems(table_ordre, ordres);
		setTableItems(table_kits_manquants, manquants);
		setTableItems(table_kits_bloques, bloques);

		manquantsLabel.setText("Total : " + manquantsCount);
		ordresLabel.setText(
				"En Stock : " + enStocksCount + "\t\tSorties : " + sortiesCount + "\t\tTotal : " + ordresCount);
		bloquesLabel.setText("Total : " + bloquesCount);
	}

	private void setTableItems(TableView<Kit> table, List<Kit> list) {
		ObservableList<Kit> kits = FXCollections.observableArrayList(list);

//		if (table.getId() == table_ordre.getId()) {
		SortedList<Kit> sortedData = new SortedList<Kit>(filterData(kits));
		sortedData.comparatorProperty().bind(table.comparatorProperty());
		table.setItems(sortedData);
//			return;
//		}

//		table.setItems(kits);
		table.refresh();
	}

	private FilteredList<Kit> filterData(ObservableList<Kit> dataList) {
		FilteredList<Kit> filteredData = new FilteredList<Kit>(dataList, b -> true);

		search_field.textProperty().addListener((observable, oldVal, newVal) -> {
			filteredData.setPredicate(kit -> {
				if (newVal == null || newVal.isEmpty())
					return true;

				String lowerCaseFilter = newVal.toLowerCase();

				if (kit.getOF() != null && kit.getOF().toLowerCase().indexOf(lowerCaseFilter) != -1)
					return true;
				else if (kit.getEmplacements() != null && kit.getEmplacements().stream()
						.anyMatch(em -> em.getCoordonnee().toLowerCase().indexOf(lowerCaseFilter) != -1))
					return true;
				else if (kit.getEtat() != null && kit.getEtat().toString().toLowerCase().indexOf(lowerCaseFilter) != -1)
					return true;

				return false;
			});
		});

		return filteredData;
	}

	@FXML
	void uploadFile(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

		File uploadedFile = FileUploader.upload(stage, FileType.ORDER);
		if (uploadedFile != null) {
			showAlert(AlertType.CONFIRMATION, "Chargement complete avec success !");
		} else {
			showAlert(AlertType.ERROR, "Erreur de chargement !");
		}
	}

	private Stage getStage() {
		return (Stage) table_ordre.getScene().getWindow();
	}

	@FXML
	void downloadOrder(ActionEvent event) {
		String title = "Ordre du Jour " + formatDate(toDate(select_date.getValue()));
		boolean success = OrderPDFGenerator.generate(getStage(), title, table_ordre.getItems());
		if (success)
			showAlert(AlertType.CONFIRMATION, "Téléchargement réussi");
		else
			showAlert(AlertType.CONFIRMATION, "Échec du téléchargement");
	}

	private static String formatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		try {
			return sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void showAlert(AlertType type, String text) {
		Alert alert = new Alert(type);
		alert.setContentText(text);
		alert.show();
	}

}
