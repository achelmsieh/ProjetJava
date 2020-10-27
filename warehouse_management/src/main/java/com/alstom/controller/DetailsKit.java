package com.alstom.controller;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import com.alstom.model.Kit;
import com.alstom.model.enums.EtatKit;
import com.alstom.service.KitService;
import com.alstom.util.FxmlView;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DetailsKit implements Initializable {

	@FXML
	private JFXTextField ofTF;
	@FXML
	private JFXTextField projetTF;
	@FXML
	private JFXTextField dtrTF;
	@FXML
	private JFXTextField runTimeTF;
	@FXML
	private JFXTextField descriptionTF;
	@FXML
	private JFXTextField nrameTF;
	@FXML
	private JFXTextField indiceCPCTF;
	@FXML
	private JFXDatePicker dateSortiePrevueDP;
	@FXML
	private JFXButton confirmBtn;

	private KitService kitService = new KitService();
	private Kit displiedKit = null;

	private final ZoneId defaultZoneId = ZoneId.systemDefault();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ofTF.setEditable(false);
	}

	public void setData(Kit kit, boolean forEntry) {
		displiedKit = kit;

		if (forEntry) {
			ofTF.setText(displiedKit.getOF());
			setFieldsDisabled(false);

			confirmBtn.setText("Valider");
			confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					fillDispliedKit();
					close();
				}
			});
		} else {
			if (kit != null) {
				setFieldsDisabled(true);
				fillFieldes();
				EtatKit etatKit = kit.getEtat();

				if (etatKit == EtatKit.ENSTOCK) {
					confirmBtn.setText("Livrer Kit");
					confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							showWindow(FxmlView.LIVRER_KIT, "CoupeAYA - Livrer Kit");
						}
					});
				} else if (etatKit == EtatKit.PLANNING) {
					confirmBtn.setText("Supprimer Kit");
					confirmBtn.setStyle("-fx-background-color: red");
					confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							boolean success = kitService.delete(kit);
							if (success) {
								showAlert(AlertType.CONFIRMATION, "Suppression réussi");
								close();
							} else {
								showAlert(AlertType.ERROR, "Échec de Suppression");
							}
						}
					});
				} else {
					confirmBtn.setVisible(false);
					confirmBtn.managedProperty().bind(confirmBtn.visibleProperty());
				}
			}
		}
	}

	private void fillDispliedKit() {
		displiedKit.setProjet(projetTF.getText());
		displiedKit.setDTR(dtrTF.getText());
		displiedKit.setRunTime(Double
				.parseDouble(runTimeTF.getText() == null || runTimeTF.getText().isEmpty() ? "0" : runTimeTF.getText()));
		displiedKit.setDescription(descriptionTF.getText());
		displiedKit.setnRAME(
				Integer.parseInt(nrameTF.getText() == null || nrameTF.getText().isEmpty() ? "0" : nrameTF.getText()));
		displiedKit.setIndiceCPC(indiceCPCTF.getText());

		Date df = Date.from(dateSortiePrevueDP.getValue().atStartOfDay(defaultZoneId).toInstant());
		displiedKit.setDateSortiePrevue(df);
	}

	public Kit getResultKit() {
		return displiedKit;
	}

	@FXML
	void closeWindows(ActionEvent event) {
		close();
	}

	private void close() {
		Stage stage = (Stage) confirmBtn.getScene().getWindow();
		stage.close();
	}

	private void fillFieldes() {
		ofTF.setText(displiedKit.getOF());
		projetTF.setText(displiedKit.getProjet());
		dtrTF.setText(displiedKit.getDTR());
		runTimeTF.setText("" + displiedKit.getRunTime());
		descriptionTF.setText(displiedKit.getDescription());
		nrameTF.setText("" + displiedKit.getnRAME());
		indiceCPCTF.setText(displiedKit.getIndiceCPC());

		Date dsp = displiedKit.getDateSortiePrevue();
		if (dsp != null) {
			dateSortiePrevueDP.setValue(dsp.toInstant().atZone(defaultZoneId).toLocalDate());
		}

	}

	private void setFieldsDisabled(boolean disable) {
		Stream.of(projetTF, dtrTF, runTimeTF, descriptionTF, nrameTF, indiceCPCTF)
				.forEach(tf -> tf.setDisable(disable));
		dateSortiePrevueDP.setDisable(disable);
	}

	private void showWindow(FxmlView window, String title) {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(window.getFxmlFile()));
		BorderPane borderpane;
		try {

			borderpane = loader.load();

			Stage stage = new Stage();
			Object controller = loader.getController();
			if (controller instanceof LivrerKit) {
				((LivrerKit) controller).setStartKit(displiedKit);
				stage.setOnHiding(e -> {
					close();
				});
			}

			Scene scene = new Scene(borderpane);

			stage.setMinWidth(820);
			stage.setMinHeight(550);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.getIcons().add(new Image("icons/warehouse.png"));
			stage.setTitle(title);
			stage.setScene(scene);

			stage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showAlert(AlertType type, String text) {
		Alert alert = new Alert(type);
		alert.setContentText(text);
		alert.show();
	}

}
