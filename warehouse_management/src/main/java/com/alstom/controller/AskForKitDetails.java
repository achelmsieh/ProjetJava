package com.alstom.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.alstom.model.Kit;
import com.alstom.service.KitService;
import com.alstom.util.FileUploader;
import com.alstom.util.FileUploader.FileType;
import com.alstom.util.FxmlView;
import com.alstom.util.LoadingManipulator;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AskForKitDetails implements Initializable {

	@FXML
	private BorderPane parent;
	@FXML
	private StackPane stackPane;
	@FXML
	private Label kitHintLabel;

	private KitService kitService = new KitService();

	private KitDetailsAction kitDetailsAction = null;
	private Kit currKit = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	public void setCurrentKit(Kit kit) {
		String msg = "";
		if (kit != null) {
			msg = "Le kit identifié par l'OF : " + kit.getOF() + " manque de données";
			currKit = kit;
		} else
			msg = "Il y a une erreur quelque part";

		kitHintLabel.setText(msg);
	}

	public KitDetailsAction getActionResult() {
		return kitDetailsAction;
	}

	public class KitDetailsAction {
		KitDetailsActionType actionType;
		Kit kit;

		public KitDetailsAction(KitDetailsActionType actionType, Kit kit) {
			super();
			this.actionType = actionType;
			this.kit = kit;
		}

		public KitDetailsActionType getActionType() {
			return actionType;
		}

		public void setActionType(KitDetailsActionType actionType) {
			this.actionType = actionType;
		}

		public Kit getKit() {
			return kit;
		}

		public void setKit(Kit kit) {
			this.kit = kit;
		}

		@Override
		public String toString() {
			return "KitDetailsAction [actionType=" + actionType + ", kit=" + kit + "]";
		}

	}

	public enum KitDetailsActionType {
		FILE, MANUEL, CONTINUE, RETIRE
	}

	@FXML
	void continueBtnAction(ActionEvent event) {
		kitDetailsAction = new KitDetailsAction(KitDetailsActionType.CONTINUE, currKit);
		closeStage(event);
	}

	@FXML
	void retireBtnAction(ActionEvent event) {
		kitDetailsAction = new KitDetailsAction(KitDetailsActionType.RETIRE, null);
		closeStage(event);
	}

	@FXML
	void manuelBtnAction(ActionEvent event) {
		System.out.println("Manuel Btn");
		showWindow(FxmlView.DETAILS_KITS, "CoupeAYA - Entrer les détails du Kit");
		kitDetailsAction = new KitDetailsAction(KitDetailsActionType.MANUEL, currKit);
		closeStage(event);
	}

	VBox box;

	void disableContent() {
		ProgressIndicator pi = new ProgressIndicator();
		box = new VBox(pi);
		box.setAlignment(Pos.CENTER);
		stackPane.getChildren().add(box);
		parent.setDisable(true);
	}

	void enableContent() {
		parent.setDisable(false);
		box.setVisible(false);
	}

	@FXML
	void planningBtnAction(ActionEvent event) {
		disableContent();
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		File uploadedFile = FileUploader.upload(stage, FileType.PLANNING);
		LoadingManipulator.manipulate(uploadedFile, kitService);
		enableContent();
		kitDetailsAction = new KitDetailsAction(KitDetailsActionType.FILE, currKit);
		closeStage(event);
	}

	private void closeStage(Event e) {
		Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
		s.close();
	}

	private void showErrorAlert(AlertType type, String text) {
		Alert alert = new Alert(type);
		alert.setContentText(text);
		alert.show();
	}

	private void showWindow(FxmlView window, String title) {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(window.getFxmlFile()));
		BorderPane borderpane;
		try {

			borderpane = loader.load();
			Stage stage = new Stage();

			Object controller = loader.getController();
			if (controller instanceof DetailsKit) {
				((DetailsKit) controller).setData(currKit, true);
				stage.setOnHiding(e -> {
					currKit = ((DetailsKit) controller).getResultKit();
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

}
