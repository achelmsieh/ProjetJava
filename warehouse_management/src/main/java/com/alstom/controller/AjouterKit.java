package com.alstom.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import com.alstom.model.Emplacement;
import com.alstom.model.EtatKit;
import com.alstom.model.Kit;
import com.alstom.service.EmplacementService;
import com.alstom.service.KitService;
import com.alstom.util.UserSession;
import com.jfoenix.controls.JFXButton;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AjouterKit implements Initializable {
	@FXML
	private TextField of_text;
	@FXML
	private TextField Line_texte;
	@FXML
	private JFXButton ValiderButton;
	@FXML
	private VBox Layout_simple;
	@FXML
	private VBox Layout_multi;
	@FXML
	private TextField zone_text;
	@FXML
	private TableView<Kit> table_of_zone;
	@FXML
	private TableColumn<Kit, String> conlone_of;
	@FXML
	private TableColumn<Kit, String> colone_corodonne;
	@FXML
	private TableColumn<Kit, String> colone_projet;
	@FXML
	private JFXComboBox<Mode> ModeCombo;

	enum Mode {
		ModeSimple, ModeMultiple
	};

	enum Ajout {
		Emplacement, Kit
	};

	ObservableList<Kit> rows = FXCollections.observableArrayList();

	EmplacementService emplacementService = new EmplacementService();
	KitService kitService = new KitService();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initCols();

		table_of_zone.setItems(rows);
		ModeCombo.getItems().add(Mode.ModeMultiple);
		ModeCombo.getItems().add(Mode.ModeSimple);
		ModeCombo.setValue(Mode.ModeSimple);
		display(true);
		initFields();

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

	@FXML
	void ActionCombo(ActionEvent event) {
		if (ModeCombo.getValue().equals(Mode.ModeSimple)) {
			display(true);
		}
		if (ModeCombo.getValue().equals(Mode.ModeMultiple)) {
			display(false);
		}
		of_text.clear();
		zone_text.clear();
		Line_texte.clear();
	}

	void display(boolean etat) {
		Layout_simple.setVisible(etat);
		Layout_multi.setVisible(!etat);
		Layout_simple.managedProperty().bind(Layout_simple.visibleProperty());
		Layout_multi.managedProperty().bind(Layout_multi.visibleProperty());

	}

	private void initFields() {
		of_text.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.length() == 8) {
				Platform.runLater(() -> {
					zone_text.requestFocus();
				});
			}
		});

		zone_text.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.length() == 3) {
				Platform.runLater(() -> {
					of_text.requestFocus();
					ajouterKit(of_text.getText(), zone_text.getText());
				});
			}
		});

		Line_texte.textProperty().addListener((observable, oldValue, newValue) -> {

			if (newValue.length() == 3 && isEmplacement(newValue)) {
				Platform.runLater(() -> {
					ajoutermultiple(Ajout.Emplacement);
				});
			}
			if (newValue.length() == 8) {
				Platform.runLater(() -> {
					ajoutermultiple(Ajout.Kit);
				});
			}
//			Line_texte.clear();
		});
	}

	private boolean isEmplacement(String str) {
		if ('A' <= str.charAt(0) && str.charAt(0) <= 'N')
			return (emplacementService.getEmplacement(str) == null) ? false : true;
		return false;
	}

	private void clearFields() {
		zone_text.clear();
		of_text.clear();
	}

	private List<String> entredOf = new ArrayList<>();

	public void ajoutermultiple(Ajout aj) {
		if (aj == Ajout.Emplacement) {
			entredOf.forEach(of -> ajouterKit(of, Line_texte.getText()));

			if (entredOf.size() > 1)
				entredOf.clear();

		}

		if (aj == Ajout.Kit) {
			entredOf.add(Line_texte.getText());
		}

		Line_texte.clear();
	}

	public void ajouterKit(String ofTxt, String empTxt) {

		if (kitService.getKitByOF(ofTxt) != null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Kit est déja dans le systéme");
			alert.show();
			clearFields();
			return;
		}

		Emplacement emp = emplacementService.getEmplacement(empTxt);
		if (emp == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Emplacement n'existe pas");
			alert.show();
			clearFields();
			return;
		}

		Kit kit = rows.stream().filter(e -> ofTxt.equals(e.getOF())).findFirst().orElse(null);
		if (kit == null) {
			kit = new Kit();

			kit.setDateEntree(new Date());
			kit.setEtat(EtatKit.ENSTOCK);
			kit.setOF(ofTxt);
			kit.setResStock(UserSession.getUser());

			Set<Emplacement> list_emp = new HashSet<Emplacement>();
			list_emp.add(emp);
			kit.setEmplacements(list_emp);

			rows.add(kit);
		} else {
			Set<Emplacement> list_emp = kit.getEmplacements();
			list_emp.add(emp);
			kit.setEmplacements(list_emp);

			table_of_zone.refresh();
		}
		clearFields();
	}

	@FXML
	void fermerFenetre(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	void validerSelection(ActionEvent event) {
		rows = table_of_zone.getItems();
		Set<Kit> list = new HashSet<Kit>();
		rows.stream().forEach(e -> list.add(e));
		kitService.save(list);

		fermerFenetre(event);
	}

}
