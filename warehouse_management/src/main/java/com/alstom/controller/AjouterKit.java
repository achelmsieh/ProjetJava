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
import com.alstom.model.Kit;
import com.alstom.model.enums.EtatKit;
import com.alstom.service.EmplacementService;
import com.alstom.service.KitService;
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
import javafx.scene.image.Image;
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
	private JFXComboBox<ModeAjout> ModeCombo;

	private enum ModeAjout {
		ModeSimple, ModeMultiple
	};

	private enum EntiteAjoute {
		Emplacement, Kit
	};

	private ObservableList<Kit> rows = FXCollections.observableArrayList();

	private EmplacementService emplacementService = new EmplacementService();
	private KitService kitService = new KitService();

	private List<String> entredOf = new ArrayList<>();
	private String entredEmp = null;

	private KitDetailsAction recievedKitDetailsAction = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initCombo();
		initFields();

		initCols();
		setEditableCols();

		table_of_zone.setItems(rows);
	}

	private void initCombo() {
		ModeCombo.getItems().add(ModeAjout.ModeMultiple);
		ModeCombo.getItems().add(ModeAjout.ModeSimple);
		ModeCombo.setValue(ModeAjout.ModeMultiple);
		display(false);
	}

	private void display(boolean etat) {
		Layout_simple.setVisible(etat);
		Layout_multi.setVisible(!etat);
		Layout_simple.managedProperty().bind(Layout_simple.visibleProperty());
		Layout_multi.managedProperty().bind(Layout_multi.visibleProperty());
	}

	private void initFields() {
		Platform.runLater(() -> Line_texte.requestFocus());

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
					ajouterMultiple(EntiteAjoute.Emplacement);
				});
			}
			if (newValue.length() == 8) {
				Platform.runLater(() -> {
					ajouterMultiple(EntiteAjoute.Kit);
				});
			}
		});
	}

	private boolean isEmplacement(String str) {
		if ('A' <= str.charAt(0) && str.charAt(0) <= 'N')
			return (emplacementService.getEmplacement(str) == null) ? false : true;
		return false;
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

		table_of_zone.setEditable(true);
	}

	@FXML
	void ActionCombo(ActionEvent event) {
		if (ModeCombo.getValue().equals(ModeAjout.ModeSimple)) {
			display(true);
			Platform.runLater(() -> of_text.requestFocus());
		}

		if (ModeCombo.getValue().equals(ModeAjout.ModeMultiple)) {
			Platform.runLater(() -> Line_texte.requestFocus());
			display(false);
		}

		clearFields();
	}

	private void clearFields() {
		zone_text.clear();
		of_text.clear();
		Line_texte.clear();
	}

	public void ajouterMultiple(EntiteAjoute entity) {
		if (entity == EntiteAjoute.Emplacement) {
			entredEmp = Line_texte.getText();
			entredOf.forEach(of -> ajouterKit(of, entredEmp));
		}

		if (entity == EntiteAjoute.Kit) {
			if (entredEmp != null)
				entredOf.clear();

			entredOf.add(Line_texte.getText());
			entredEmp = null;
		}

		Line_texte.clear();
	}

	public void ajouterKit(String ofTxt, String empTxt) {

		Emplacement emp = emplacementService.getEmplacement(empTxt);
		if (emp == null) {
			showErrorAlert("Emplacement n'existe pas");
			clearFields();
			return;
		}

		Kit kit = rows.stream().filter(e -> ofTxt.equals(e.getOF())).findFirst().orElse(null);
		if (kit != null) {
			Set<Emplacement> list_emp = kit.getEmplacements();
			list_emp.add(emp);
			kit.setEmplacements(list_emp);

			table_of_zone.refresh();
		} else {
			Kit existingKit = kitService.getKitByOF(ofTxt);
			if (existingKit != null) {
				if (existingKit.getEtat() == EtatKit.PLANNING) {
					rows.add(kitToAdd(existingKit, emp));
				} else {
					showErrorAlert("Kit est déja dans le systéme");
					clearFields();
					return;
				}
			} else {
//				showErrorAlert("Unknown Error");
				rows.add(kitToAdd(new Kit(ofTxt), emp));
			}
		}
		clearFields();
	}

	private Kit kitToAdd(Kit kit, Emplacement emp) {
		Kit rsltKit = new Kit();
		rsltKit.setOF(kit.getOF());
		rsltKit.setDateEntree(new Date());
		rsltKit.setProjet(kit.getProjet());

		if (emp != null) {
			if (kit.getEmplacements() != null) {
				Set<Emplacement> list_emp = new HashSet<Emplacement>();
				kit.getEmplacements().forEach(e -> list_emp.add(e));
				list_emp.add(emp);
				rsltKit.setEmplacements(list_emp);
			} else {
				Set<Emplacement> list_emp = new HashSet<Emplacement>();
				list_emp.add(emp);
				rsltKit.setEmplacements(list_emp);
			}
		}

		return rsltKit;
	}

	@FXML
	void validerSelection(ActionEvent event) {

//		List<Kit> sk = table_of_zone.getItems();
		List<Kit> sk = getValidateRows(table_of_zone.getItems());

		if (sk == null) {
			showErrorAlert("Aucune ligne n'est valide");
			return;
		}

		sk.forEach(k -> {
			Kit kit = kitService.getKitByOF(k.getOF());
			kit.setEtat(EtatKit.ENSTOCK);
			kit.setDateEntree(k.getDateEntree());
			kit.setResStock(UserSession.getUser());
			kit.setEmplacements(k.getEmplacements());
			kitService.update(kit);
		});

		fermerFenetre(event);
	}

	private List<Kit> getValidateRows(ObservableList<Kit> list) {
		List<Kit> validatedList = new ArrayList<Kit>();
		if (list == null || list.isEmpty())
			return validatedList;

		validatedList = list.stream().filter(kit -> kit.getProjet() != null).collect(Collectors.toList());
		validatedList.forEach(kit -> list.remove(kit));

		if (!list.isEmpty()) {
			for (Kit kit : list) {
				showWindow(kit);

				if (recievedKitDetailsAction == null)
					continue;

				Kit rk = recievedKitDetailsAction.getKit();
				switch (recievedKitDetailsAction.getActionType()) {
				case FILE:
					Kit existingKit = kitService.getKitByOF(rk.getOF());
					if (existingKit != null && existingKit.getEtat() == EtatKit.PLANNING) {
						Kit kkk = kitToAdd(existingKit, null);
						kkk.setEmplacements(rk.getEmplacements());
						validatedList.add(kkk);
					} else {
						showErrorAlert("L'OF " + rk.getOF()
								+ " n'est pas dans le Planning chargé! Veuillez choisir une autre action");
					}
					continue;
				case MANUEL:
				case CONTINUE:
					kitService.save(rk);
					validatedList.add(rk);
				case RETIRE:
				default:
				}
			}
		}

		return validatedList;
	}

	private Kit askForInfos(Kit kit) {
		showWindow(kit);

		return treatAction();
	}

	private Kit treatAction() {
		if (recievedKitDetailsAction == null)
			return null;

		Kit rk = recievedKitDetailsAction.getKit();
		switch (recievedKitDetailsAction.getActionType()) {
		case FILE:
			Kit existingKit = kitService.getKitByOF(rk.getOF());
			if (existingKit != null && existingKit.getEtat() == EtatKit.PLANNING) {
				Kit kkk = kitToAdd(existingKit, null);
				kkk.setEmplacements(rk.getEmplacements());
				return kkk;
			} else {
				showErrorAlert(
						"L'OF " + rk.getOF() + " n'est pas dans le Planning chargé! Veuillez choisir une autre action");
				return askForInfos(rk);
			}
		case MANUEL:
		case CONTINUE:
			kitService.save(rk);
			return rk;
		case RETIRE:
		default:
			return null;
		}
	}

	private void showWindow(Kit kit) {
		FXMLLoader loader = new FXMLLoader(
				getClass().getClassLoader().getResource(FxmlView.ASK_KIT_DETAILS.getFxmlFile()));
		BorderPane borderpane;
		try {

			borderpane = loader.load();

			AskForKitDetails controller = loader.getController();
			controller.setCurrentKit(kit);

			Stage stage = new Stage();
			Scene scene = new Scene(borderpane);

			stage.setResizable(false);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.getIcons().add(new Image("icons/warehouse.png"));
			stage.setTitle("CoupeAYA - Manque de données");
			stage.setScene(scene);
			stage.setOnHiding(e -> {
				recievedKitDetailsAction = controller.getActionResult();
			});

			stage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
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

}
