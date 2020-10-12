package com.alstom.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import com.alstom.model.EtatKit;
import com.alstom.model.Kit;
import com.alstom.util.FxmlView;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
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
	private JFXButton confirmBtn;

	private Kit displiedKit = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ofTF.setEditable(false);
	}

	public void setDate(Kit kit, boolean forEntry) {
		displiedKit = kit;

		if (forEntry) {
			ofTF.setText(displiedKit.getOF());
			setFieldsDisabled(false);

			confirmBtn.setText("Valider");
			confirmBtn.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					System.out.println("update kit");
					fillDispliedKit();
					close();
				}
			});
		} else if (kit != null && kit.getEtat() != EtatKit.SORTIE) {
			setFieldsDisabled(true);
			fillFieldes();

			confirmBtn.setText("Livrer Kit");
			confirmBtn.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					System.out.println("Livrer kit");
					showWindow(FxmlView.LIVRER_KIT, "Coupure - Livrer Kit");
				}
			});
		} else {
			setFieldsDisabled(true);
			fillFieldes();
			confirmBtn.setVisible(false);
			confirmBtn.managedProperty().bind(confirmBtn.visibleProperty());
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
	}

	private void setFieldsDisabled(boolean disabel) {
		Stream.of(projetTF, dtrTF, runTimeTF, descriptionTF, nrameTF, indiceCPCTF)
				.forEach(tf -> tf.setDisable(disabel));
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
			stage.setTitle(title);
			stage.setScene(scene);

			stage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
