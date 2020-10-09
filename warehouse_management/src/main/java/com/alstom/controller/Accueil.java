package com.alstom.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.alstom.util.FxmlView;
import com.alstom.util.UserSession;
import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class Accueil extends GenericController implements Initializable {

	@FXML
	private JFXButton btnAccueil;
	@FXML
	private JFXButton btnKits;
	@FXML
	private JFXButton btnPersonnels;
	@FXML
	private JFXButton btnEmplacements;
	@FXML
	private JFXButton btnKPI;
	@FXML
	private JFXButton btnDeconnection;

	@FXML
	void menuAccueil(MouseEvent event) {
		toMenuAccueil();
	}

	@FXML
	void menuKits(MouseEvent event) {
		toMenuKits();
	}

	@FXML
	void menuPersonnels(MouseEvent event) {
		toMenuPersonnels();
	}

	@FXML
	void menuEmplacements(MouseEvent event) {
		toMenuZones();
	}

	@FXML
	void menuKPI(MouseEvent event) {
		toMenuKPI();
	}

	@FXML
	void seDeconncecter(MouseEvent event) {
		UserSession.cleanUserSession();

		Stage currStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		currStage.hide();

		Stage loginStage = new Stage();
		try {
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(FxmlView.LOGIN.getFxmlFile()));

			Scene scene = new Scene(root);

			loginStage.getIcons().add(new Image("icons/warehouse.png"));
			loginStage.setTitle("Coupure");
			loginStage.setScene(scene);
			loginStage.setResizable(false);
			loginStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		changeMenu(FxmlView.MENU);
	}

}
