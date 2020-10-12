package com.alstom.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import com.alstom.controller.AskForKitDetails.KitDetailsAction;
import com.alstom.model.Emplacement;
import com.alstom.model.EtatKit;
import com.alstom.model.Kit;
import com.alstom.service.EmplacementService;
import com.alstom.service.KitService;
import com.alstom.util.ExcelDataReader;
import com.alstom.util.FxmlView;
import com.alstom.util.UserSession;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
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
					ajouterMultiple(Ajout.Emplacement);
				});
			}
			if (newValue.length() == 8) {
				Platform.runLater(() -> {
					ajouterMultiple(Ajout.Kit);
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
	private String entredEmp = null;

	public void ajouterMultiple(Ajout aj) {
		if (aj == Ajout.Emplacement) {
			entredEmp = Line_texte.getText();
			entredOf.forEach(of -> ajouterKit(of, entredEmp));
		}

		if (aj == Ajout.Kit) {
			if (entredEmp != null)
				entredOf.clear();

			entredOf.add(Line_texte.getText());
			entredEmp = null;
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
			try {
				kit = ExcelDataReader.getKitInfos(ofTxt);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			if (kit == null)
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

		Set<Kit> sk = getValidateRows(null, rows);
		kitService.save(sk);

		fermerFenetre(event);
	}

	private Set<Kit> getValidateRows(HashSet<Kit> validatedSet, ObservableList<Kit> list) {

		if (list == null || list.isEmpty())
			return validatedSet;

		if (validatedSet == null)
			validatedSet = (HashSet<Kit>) list.stream().filter(kit -> kit.getProjet() != null)
					.collect(Collectors.toSet());

		Kit kit2V = list.get(0);

		if (kit2V.getProjet() == null || kit2V.getProjet().isEmpty()) {
			askForInfos(kit2V);
			if (recievedKit == null) {
				list.remove(0);
				return getValidateRows(validatedSet, list);
			}

			kit2V = recievedKit;
		}

		list.remove(0);
		validatedSet.add(kit2V);
		return getValidateRows(validatedSet, list);
	}

	private void askForInfos(Kit kit) {
		showWindow(FxmlView.ASK_KIT_DETAILS, "Coupure - Manque de données", kit);
	}

	private Kit recievedKit = null;

	private void RecievedAction(KitDetailsAction kda) {
		if (kda == null)
			return;

		switch (kda.getActionType()) {
		case FILE:
			System.out.println("Fiiiiiile");
			try {
				Kit sk = kda.getKit(); // searched Kit
				recievedKit = ExcelDataReader.getKitInfos(sk.getOF()); // result kit
				recievedKit.setOF(sk.getOF());
				recievedKit.setDateEntree(new Date());
				recievedKit.setEtat(EtatKit.ENSTOCK);
				recievedKit.setResStock(UserSession.getUser());
				recievedKit.setEmplacements(sk.getEmplacements());

				return;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			break;
		case MANUEL:
			System.out.println("Maaan");
			recievedKit = kda.getKit();
			break;
		case CONTINUE:
			System.out.println("Connn");
			recievedKit = kda.getKit();
			break;
		case RETIRE:
		default:
			recievedKit = null;
			break;
		}

	}

	private void showWindow(FxmlView window, String title, Kit kit) {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(window.getFxmlFile()));
		BorderPane borderpane;
		try {

			borderpane = loader.load();

			AskForKitDetails controller = loader.getController();
			controller.setCurrentKit(kit);

			Stage stage = new Stage();
			Scene scene = new Scene(borderpane);

			stage.setResizable(false);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle(title);
			stage.setScene(scene);
			Kit k = null;
			stage.setOnHiding(e -> {
				RecievedAction(controller.getActionResult());
			});

			stage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
