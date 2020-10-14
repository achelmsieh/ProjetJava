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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class AjouterKit implements Initializable {
	@FXML
	private TextField of_text;
	@FXML
	private TextField zone_text;
	@FXML
	private TextField Line_texte;
	@FXML
	private JFXButton ValiderButton;
	@FXML
	private VBox Layout_simple;
	@FXML
	private VBox Layout_multi;
	@FXML
	private TableView<Kit> table_of_zone;
	@FXML
	private TableColumn<Kit, String> conlone_of;
	@FXML
	private TableColumn<Kit, String> colone_corodonne;
	@FXML
	private TableColumn<Kit, String> colone_projet;
	@FXML
	private TableColumn<Kit, String> colone_supprimer;
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
		setEditableCols();

		table_of_zone.setItems(rows);

		initCombo();

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

	private void setEditableCols() {
		conlone_of.setCellFactory(TextFieldTableCell.forTableColumn());
		conlone_of.setOnEditCommit(e -> {
			e.getTableView().getItems().get(e.getTablePosition().getRow()).setOF(e.getNewValue());
		});

//		colone_corodonne.setCellFactory(TextFieldTableCell.forTableColumn());
//		colone_corodonne.setOnEditCommit(e -> {
//			Emplacement emp = emplacementService.getEmplacement(e.getNewValue());
//			if(emp != null) {
//				Set<Emplacement> emps = new HashSet();
//				emps.add(emp);
//				e.getTableView().getItems().get(e.getTablePosition().getRow()).setEmplacements(emps);
//			} else {
//				showErrorAlert("Emplacement n'existe pas");
//			}
//		});

//		colone_projet.setCellFactory(TextFieldTableCell.forTableColumn());
//		colone_projet.setOnEditCommit(e -> {
//			e.getTableView().getItems().get(e.getTablePosition().getRow()).setProjet(e.getNewValue());
//		});

		table_of_zone.setEditable(true);
	}

	private void initCombo() {
		ModeCombo.getItems().add(Mode.ModeMultiple);
		ModeCombo.getItems().add(Mode.ModeSimple);
		ModeCombo.setValue(Mode.ModeMultiple);
		display(false);
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
			showErrorAlert("Kit est déja dans le systéme");
			clearFields();
			return;
		}

		Emplacement emp = emplacementService.getEmplacement(empTxt);
		if (emp == null) {
			showErrorAlert("Emplacement n'existe pas");
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

	private void showErrorAlert(String text) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setContentText(text);
		alert.show();
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
