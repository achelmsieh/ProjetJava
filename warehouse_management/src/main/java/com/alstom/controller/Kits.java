package com.alstom.controller;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.alstom.model.Emplacement;
import com.alstom.model.EtatKit;
import com.alstom.model.Kit;
import com.alstom.model.ResProduction;
import com.alstom.model.ResStock;
import com.alstom.service.KitService;
import com.alstom.util.FileUploader;
import com.alstom.util.FxmlView;
import com.jfoenix.controls.JFXToggleButton;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Kits implements Initializable {

	@FXML
	TextField search_field;
	ToggleGroup toggleGroup = new ToggleGroup();
	@FXML
	private JFXToggleButton enStockBtn;
	@FXML
	private JFXToggleButton sortieBtn;
	@FXML
	TableView<Kit> table_of;
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
	private TableColumn<Kit, String> col_res_stock;
	@FXML
	private TableColumn<Kit, String> col_run_time;
	@FXML
	private TableColumn<Kit, String> col_dtr;
	@FXML
	private TableColumn<Kit, String> col_description;
	@FXML
	private TableColumn<Kit, String> col_nrame;
	@FXML
	private TableColumn<Kit, String> col_indice_cpc;

	private KitService kms = new KitService();
	private final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd   hh:mm");

	public Kit selectedKit = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initToggleButtons();
		initCols();
		setDoubleClickEvent();
		initSearchField();
	}

	private void initCols() {
		col_of.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getOF()));
		col_etat.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getEtat().toString()));
		col_projet.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getProjet()));
		col_dtr.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getDTR()));
		col_run_time.setCellValueFactory(cdf -> new SimpleStringProperty(String.valueOf(cdf.getValue().getRunTime())));
		col_description.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getDescription()));
		col_nrame.setCellValueFactory(cdf -> new SimpleStringProperty(String.valueOf(cdf.getValue().getnRAME())));
		col_indice_cpc.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getIndiceCPC()));

		col_date_entree.setCellValueFactory(
				cdf -> new SimpleStringProperty(dateFormat.format(cdf.getValue().getDateEntree())));
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

		col_res_stock.setCellValueFactory(cdf -> {
			ResStock rs = cdf.getValue().getResStock();
			return new SimpleStringProperty(rs == null ? "" : rs.getNom());
		});

		col_res_prod.setCellValueFactory(cdf -> {
			ResProduction rp = cdf.getValue().getResProduction();
			return new SimpleStringProperty(rp == null ? "" : rp.getNom());
		});

		setTableItems(kms.getKits());
	}

	private void setDoubleClickEvent() {
		table_of.setRowFactory(tableView -> {
			TableRow<Kit> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					selectedKit = tableView.getSelectionModel().getSelectedItem();
					showWindow(FxmlView.DETAILS_KITS, "Coupure - Détails du Kit");
				}
//				if (event.getClickCount() == 2 && (!row.isEmpty())) {
//					Kit k = tableView.getSelectionModel().getSelectedItem();
//					if (k != null && k.getEtat() != EtatKit.SORTIE) {
//						selectedKit = k;
//						showWindow(FxmlView.LIVRER_KIT, "Coupure - Livrer Kit");
//					}
//					else {
//						Alert alert = new Alert(AlertType.ERROR);
//						alert.setContentText("cette OF est déja livrer");
//						alert.show();
//					}
//				}
			});
			return row;
		});
	}

	private void initSearchField() {
		search_field.textProperty().addListener((observable, oldValue, newValue) -> {
			search(newValue);
		});
	}

	private void search(String value) {
		String val = value.replaceAll("\\s+", "");
		if (val.isEmpty()) {
			setTableItems(kms.getKits());
		} else if (isNumeric(val)) {
			setTableItems(kms.searchKit(value));
		}
	}

	private void setTableItems(List<Kit> list) {
		ObservableList<Kit> kits = FXCollections.observableArrayList(list);
		table_of.setItems(kits);
		table_of.refresh();
	}

	@FXML
	public void ajouterKit() throws IOException {
		showWindow(FxmlView.AJOUTER_KIT, "Coupure - Ajouter Kit");
	}

	@FXML
	void sortirKit(MouseEvent event) {
		showWindow(FxmlView.LIVRER_KIT, "Coupure - Livrer Kit");
	}

	private void initToggleButtons() {
		enStockBtn.setToggleGroup(toggleGroup);
		sortieBtn.setToggleGroup(toggleGroup);
	}

	@FXML
	void filterEnStock(ActionEvent event) {
		filter();
	}

	@FXML
	void filterSortie(ActionEvent event) {
		filter();
	}

	private void filter() {
		if (enStockBtn.isSelected()) {
			setTableItems(kms.filterByEtat(EtatKit.ENSTOCK));
		}

		if (sortieBtn.isSelected()) {
			setTableItems(kms.filterByEtat(EtatKit.SORTIE));
		}

		if ((!enStockBtn.isSelected() && !sortieBtn.isSelected())
				|| (enStockBtn.isSelected() && sortieBtn.isSelected())) {
			search(search_field.getText());
		}
	}

	private void showWindow(FxmlView window, String title) {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(window.getFxmlFile()));
		BorderPane borderpane;
		try {

			borderpane = loader.load();

			Object controller = loader.getController();
			if (controller instanceof DetailsKit)
				((DetailsKit) controller).setDate(selectedKit, false);

			Stage stage = new Stage();
			Scene scene = new Scene(borderpane);

			stage.setMinWidth(820);
			stage.setMinHeight(550);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle(title);
			stage.setScene(scene);

			stage.setOnHiding(e -> {
				search("");
			});

			stage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isNumeric(String strNum) {
		Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

		if (strNum == null) {
			return false;
		}

		return pattern.matcher(strNum).matches();
	}

	@FXML
	void Refresh(MouseEvent event) {
		search("");
	}

	@FXML
	void uploadPlanning(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		FileUploader.upload(stage);
	}
}
