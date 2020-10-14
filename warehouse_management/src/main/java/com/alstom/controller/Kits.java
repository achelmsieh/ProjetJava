package com.alstom.controller;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.alstom.model.Emplacement;
import com.alstom.model.EtatKit;
import com.alstom.model.Kit;
import com.alstom.service.KitService;
import com.alstom.util.FxmlView;
import com.jfoenix.controls.JFXToggleButton;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
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
	TableColumn<Kit, String> colone_of;
	@FXML
	TableColumn<Kit, String> colone_projet;
	@FXML
	TableColumn<Kit, String> colone_etats;
	@FXML
	TableColumn<Kit, String> date_entre;
	@FXML
	TableColumn<Kit, String> date_sortie;
	@FXML
	TableColumn<Kit, String> zone_colone;

	private KitService kms = new KitService();
	private final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd   hh:mm");

	public static Kit selectedKit = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initToggleButtons();
		initCols();
		setDoubleClickEvent();
		initSearchField();
	}

	private void initCols() {

		table_of.setColumnResizePolicy((param) -> true);
		Platform.runLater(() -> customResize(table_of));

		colone_of.setCellValueFactory(new PropertyValueFactory<Kit, String>("OF"));
		colone_etats.setCellValueFactory(new PropertyValueFactory<Kit, String>("etat"));
		colone_projet.setCellValueFactory(new PropertyValueFactory<Kit, String>("projet"));

		date_entre.setCellValueFactory(cellDataFeatures -> new SimpleStringProperty(
				dateFormat.format(cellDataFeatures.getValue().getDateEntree())));
		date_sortie.setCellValueFactory(cellDataFeatures -> {
			Date ds = cellDataFeatures.getValue().getDateSortie();
			return new SimpleStringProperty(ds == null ? "" : dateFormat.format(ds));
		});

		zone_colone.setCellValueFactory(cellDataFeatures -> {
			if (cellDataFeatures.getValue().getEmplacements() != null)
				return new SimpleStringProperty(cellDataFeatures.getValue().getEmplacements().stream()
						.map(Emplacement::getCoordonnee).collect(Collectors.joining("\n")));
			else
				return new SimpleStringProperty("");
		});

		setTableItems(kms.getKits());
	}

	public void customResize(TableView<?> view) {

		AtomicLong width = new AtomicLong();
		view.getColumns().forEach(col -> {
			width.addAndGet((long) col.getWidth());
		});
		double tableWidth = view.getWidth();

		if (tableWidth > width.get()) {
			view.getColumns().forEach(col -> {
				col.setPrefWidth(col.getWidth() + ((tableWidth - width.get()) / view.getColumns().size()));
			});
		}
	}

	private void setDoubleClickEvent() {
		table_of.setRowFactory(tableView -> {
			TableRow<Kit> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					Kit k = tableView.getSelectionModel().getSelectedItem();
					if (k != null && k.getEtat() != EtatKit.SORTIE) {
						selectedKit = k;
						showWindow(FxmlView.LIVRER_KIT, "Coupure - Livrer Kit");
					}
//					else {
//						Alert alert = new Alert(AlertType.ERROR);
//						alert.setContentText("cette OF est déja livrer");
//						alert.show();
//					}
				}
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
			Stage stage = new Stage();
			Scene scene = new Scene(borderpane);

			stage.setMinWidth(820);
			stage.setMinHeight(550);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle(title);
			stage.setScene(scene);
			stage.show();

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
}
