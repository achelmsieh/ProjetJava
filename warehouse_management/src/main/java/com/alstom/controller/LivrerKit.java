package com.alstom.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.alstom.model.Emplacement;
import com.alstom.model.Kit;
import com.alstom.model.Personnel;
import com.alstom.model.enums.EtatKit;
import com.alstom.model.enums.PersonnelRole;
import com.alstom.service.KitService;
import com.alstom.service.PersonnelService;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;

public class LivrerKit implements Initializable {

	@FXML
	private TextField of_text;
	@FXML
	private TextField matricule_text;
	@FXML
	private TableView<Kit> table_of_zone;
	@FXML
	private JFXComboBox<Personnel> Combo;
	@FXML
	private TableColumn<Kit, String> conlone_of;
	@FXML
	private TableColumn<Kit, String> colone_corodonne;
	@FXML
	private TableColumn<Kit, String> colone_projet;
	@FXML
	private TableColumn<Kit, String> colone_supprimer;

	ObservableList<Kit> rows = FXCollections.observableArrayList();
	KitService kitService = new KitService();

	PersonnelService personnelService = new PersonnelService();

	private Kit startKit = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initCols();

		table_of_zone.setItems(rows);

		initFields();
		initSearchField();

		Combo.getItems().addAll(personnelService.getPersonnels(PersonnelRole.RES_PRODUCTION));
	}

	private void initFields() {
		Platform.runLater(() -> of_text.requestFocus());
		of_text.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.length() == 8)
				Platform.runLater(() -> search());
		});
	}

	public void setStartKit(Kit kit) {
		startKit = kit;
		if (startKit != null)
			rows.add(startKit);
		table_of_zone.refresh();
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

		Callback<TableColumn<Kit, String>, TableCell<Kit, String>> cellFactory = new Callback<TableColumn<Kit, String>, TableCell<Kit, String>>() {
			@Override
			public TableCell<Kit, String> call(TableColumn<Kit, String> param) {
				final TableCell<Kit, String> cell = new TableCell<Kit, String>() {

					@Override
					protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							ImageView img = new ImageView("icons/trash.png");
							img.setFitWidth(35);
							img.setFitHeight(35);
							JFXButton btn = new JFXButton();
							btn.setGraphic(img);

							btn.setOnAction(event -> {
								Kit kit = getTableView().getItems().get(getIndex());
								rows.remove(kit);
							});
							setGraphic(btn);
							setText(null);
						}
					}
				};
				return cell;
			}
		};

		colone_supprimer.setCellFactory(cellFactory);
	}

	private void search() {
		// search for this kit in table rows
		Kit k = rows.stream().filter(item -> item.getOF().equals(of_text.getText())).findFirst().orElse(null);
		if (k != null) {
			showErrorAlert("OF: " + of_text.getText() + " est déjà dans la liste");
			of_text.clear();
			return;
		}

		// search if this kit in database
		Kit kit = kitService.getKitByOF(of_text.getText());
		if (kit == null) {
			showErrorAlert("OF: " + of_text.getText() + " n'existe pas");
		} else {
			if (kit.getEtat() == EtatKit.SORTIE) {
				showErrorAlert("OF : " + of_text.getText() + " est déja livrer");
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
			showErrorAlert("Veuillez selectionner un Responsable de production");
		}
	}

	private void showErrorAlert(String text) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setContentText(text);
		alert.show();
	}

	@FXML
	void fermerFenetre(ActionEvent event) {
		startKit = null;
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	private void initSearchField() {
		matricule_text.textProperty().addListener((observable, oldValue, newValue) -> {
			search(newValue);
		});
	}

	private void search(String value) {
		String val = value.replaceAll("\\s+", "");
		if (val.isEmpty()) {
		} else if (isNumeric(val)) {
			List<Personnel> personnels = Combo.getItems();
			Personnel personnel = personnels.stream().filter(e -> e.getMatricule().equals(matricule_text.getText()))
					.findFirst().orElse(null);
			if (personnel != null) {
				Combo.setValue(personnel);
			} else {
				Combo.setValue(null);
			}
		}
	}

	public boolean isNumeric(String strNum) {
		Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

		if (strNum == null) {
			return false;
		}

		return pattern.matcher(strNum).matches();
	}

	void RequestFocus() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				of_text.requestFocus();
			}
		});
	}

}