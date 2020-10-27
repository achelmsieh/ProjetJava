package com.alstom.controller;

import java.io.IOException;

import com.alstom.util.FxmlView;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

public class MenuGenericController {

	@FXML
	protected BorderPane container;

	protected void changeMenu(FxmlView menu) {

		Parent root = null;
		try {
			root = FXMLLoader.load(Accueil.class.getClassLoader().getResource(menu.getFxmlFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		container.setCenter(root);
	}

	@FXML
	void menuAccueil(MouseEvent event) {
		changeMenu(FxmlView.MENU);
	}

	@FXML
	void menuKits(MouseEvent event) {
		changeMenu(FxmlView.KITS);
	}

	@FXML
	void menuOrdres(MouseEvent event) {
		changeMenu(FxmlView.ORDRE_JOUR);
	}

	@FXML
	void menuDashboard(MouseEvent event) {
		changeMenu(FxmlView.DASHBOARD);
	}
	
	@FXML
	void menuEmplacements(MouseEvent event) {
		changeMenu(FxmlView.ZONES);
	}

	@FXML
	void menuPersonnels(MouseEvent event) {
		changeMenu(FxmlView.PERSONNELS);
	}

	@FXML
	void menuRoles(MouseEvent event) {
		changeMenu(FxmlView.PERSONNELS_MANAGEMENT);
	}

	@FXML
	void menuKPI(MouseEvent event) {
		changeMenu(FxmlView.KPI);
	}
}
