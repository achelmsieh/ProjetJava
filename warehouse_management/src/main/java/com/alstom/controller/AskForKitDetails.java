package com.alstom.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.alstom.model.Kit;
import com.alstom.util.FileUploader;
import com.alstom.util.FxmlView;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AskForKitDetails implements Initializable {

	@FXML
	private Label kitHintLabel;

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
		showWindow(FxmlView.DETAILS_KITS, "Coupure - Entrer les détails du Kit");
		kitDetailsAction = new KitDetailsAction(KitDetailsActionType.MANUEL, currKit);
		closeStage(event);
	}

	@FXML
	void planningBtnAction(ActionEvent event) {
		System.out.println("Planning Btn");
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		FileUploader.upload(stage);
		kitDetailsAction = new KitDetailsAction(KitDetailsActionType.FILE, currKit);
		closeStage(event);
	}

	private void closeStage(Event e) {
		Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
		s.close();
	}

	private void showWindow(FxmlView window, String title) {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(window.getFxmlFile()));
		BorderPane borderpane;
		try {

			borderpane = loader.load();
			Stage stage = new Stage();

			Object controller = loader.getController();
			if (controller instanceof DetailsKit) {
				((DetailsKit) controller).setDate(currKit, true);
				stage.setOnHiding(e -> {
					currKit = ((DetailsKit) controller).getResultKit();
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
