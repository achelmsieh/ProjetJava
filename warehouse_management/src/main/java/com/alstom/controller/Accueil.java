package com.alstom.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.alstom.util.FxmlView;
import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;

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
		System.out.println("log out");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		changeMenu(FxmlView.MENU);
	}

}
