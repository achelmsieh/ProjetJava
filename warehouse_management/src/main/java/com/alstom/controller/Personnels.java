package com.alstom.controller;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import com.alstom.model.Kit;
import com.alstom.model.ResProduction;
import com.alstom.service.KitService;
import com.alstom.service.ResProductionService;
import com.jfoenix.controls.JFXComboBox;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class Personnels implements Initializable {

	@FXML
	private VBox VboxAdd;
	@FXML
	private VBox VboxSelect;
	@FXML
	private JFXTextField NomRes;
	@FXML
	private JFXComboBox<ResProduction> Combo;

	@FXML
	private Label IdLabel;
	@FXML
	private Label NomLabel;
	@FXML
	private TableView<Kit> KitTable;
	@FXML
	private TableColumn<Kit, String> OfColumn;
	@FXML
	private TableColumn<Kit, String> ProjetColumn;
	@FXML
	private TableColumn<Kit, String> dateEntreeColumn;
	@FXML
	private TableColumn<Kit, String> dateSortieColumn;

	ResProductionService prs = new ResProductionService();
	KitService kits = new KitService();
	private final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd   hh:mm");

	@FXML
	void AjouterRes(MouseEvent event) {
		if (!VboxAdd.isVisible()) {
			setVisibility(VboxAdd, true);
			setVisibility(VboxSelect, false);
		} else {
			setVisibility(VboxAdd, false);
			setVisibility(VboxSelect, true);
		}
	}

	@FXML
	void AddResProd(ActionEvent event) {
		if (!NomRes.getText().isEmpty()) {

			ResProduction res = new ResProduction();
			res.setNom(NomRes.getText());
			prs.save(res);

			Combo.getItems().addAll(res);
			showAlert(AlertType.INFORMATION, "Responsable Ajout�e");

			setVisibility(VboxAdd, false);
			setVisibility(VboxSelect, true);
		} else {
			showAlert(AlertType.ERROR, "Veuillez Saisir le nom du responsable de production");
		}
	}

	private void showAlert(AlertType type, String msg) {
		Alert alert = new Alert(type);
		alert.setContentText(msg);
		alert.show();
	}

	@FXML
	void FermeAdd(ActionEvent event) {
		setVisibility(VboxAdd, false);
		setVisibility(VboxSelect, true);
	}

	private void setVisibility(Node node, Boolean etat) {
		node.setVisible(etat);
		node.managedProperty().bind(node.visibleProperty());
	}

	@FXML
	void ActionCombo(ActionEvent event) {

		IdLabel.setText("ID : " + Combo.getValue().getId());
		NomLabel.setText("Nom : " + Combo.getValue().getNom());

		List<Kit> list = kits.getKitByIdRes(Combo.getValue());
		ObservableList<Kit> kits = FXCollections.observableArrayList(list);
		KitTable.setItems(kits);
		KitTable.refresh();

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initCombo();
		initKitTableCols();

		setVisibility(VboxAdd, false);
	}

	private void initCombo() {
		List<ResProduction> respos = new ArrayList<>();
		prs.getResProduction().stream().forEach(e -> respos.add(e));
		Combo.getItems().addAll(respos);
	}

	private void initKitTableCols() {
		OfColumn.setCellValueFactory(cellDataFeatures -> new SimpleStringProperty(cellDataFeatures.getValue().getOF()));
		ProjetColumn.setCellValueFactory(
				cellDataFeatures -> new SimpleStringProperty(cellDataFeatures.getValue().getProjet()));

		dateEntreeColumn.setCellValueFactory(cellDataFeatures -> new SimpleStringProperty(
				dateFormat.format(cellDataFeatures.getValue().getDateEntree())));
		dateSortieColumn.setCellValueFactory(cellDataFeatures -> {
			Date ds = cellDataFeatures.getValue().getDateSortie();
			return new SimpleStringProperty(ds == null ? "" : dateFormat.format(ds));
		});
	}
}
