package com.alstom.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.alstom.model.Emplacement;
import com.alstom.model.EtatKit;
import com.alstom.model.Kit;
import com.alstom.model.ResProduction;
import com.alstom.service.KitService;
import com.alstom.service.ResProductionService;
import com.jfoenix.controls.JFXComboBox;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LivrerKit implements Initializable {

	@FXML
	private TextField of_text;
	@FXML
	private TableView<Kit> table_of_zone;
	@FXML
	private JFXComboBox<ResProduction> Combo;
	@FXML
	private TableColumn<Kit, String> conlone_of;
	@FXML
	private TableColumn<Kit, String> colone_corodonne;
	@FXML
	private TableColumn<Kit, String> colone_projet;

	ObservableList<Kit> rows = FXCollections.observableArrayList();
	KitService kitService = new KitService();

	ResProductionService prs = new ResProductionService();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initCols();

		table_of_zone.setItems(rows);

		of_text.requestFocus();
		of_text.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.length() == 8)
				Platform.runLater(() -> search());
		});

		List<ResProduction> c = new ArrayList<>();
		prs.getResProduction().stream().forEach(e -> c.add(e));
		Combo.getItems().addAll(c);
	}

	private void initCols() {

		conlone_of
				.setCellValueFactory(cellDataFeatures -> new SimpleStringProperty(cellDataFeatures.getValue().getOF()));
		colone_projet.setCellValueFactory(
				cellDataFeatures -> new SimpleStringProperty(cellDataFeatures.getValue().getProjet()));

		colone_corodonne.setCellValueFactory(cellDataFeatures -> {
			if (cellDataFeatures.getValue().getEmplacements() != null)
				return new SimpleStringProperty(cellDataFeatures.getValue().getEmplacements().stream()
						.map(Emplacement::getCoordonnee).collect(Collectors.joining("\n")));
			else
				return new SimpleStringProperty("");
		});
	}

	void search() {
		Kit kit = kitService.getKitByOF(of_text.getText());
		if (kit == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("OF: " + of_text.getText() + " n'existe pas");
			alert.show();
		} else {
			if (kit.getEtat() == EtatKit.SORTIE) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("OF : " + of_text.getText() + " est déja livrer");
				alert.show();
			} else {
				rows.add(kit);
				table_of_zone.setItems(rows);
			}
		}
		of_text.clear();
	}

	@FXML
	void validerSelection(ActionEvent event) {
		if (Combo.getValue() != null) {
			rows.stream().forEach(kit -> {
				kit.setResProduction(Combo.getValue());
				kitService.livrerKit(kit);
			});
			fermerFenetre(event);
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Veuillez selectionner un Responsable de production");
			alert.show();

		}
	}

	@FXML
	void fermerFenetre(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

}