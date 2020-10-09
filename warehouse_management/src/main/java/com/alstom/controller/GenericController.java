package com.alstom.controller;

import java.io.IOException;

import com.alstom.util.FxmlView;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class GenericController {

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

	protected void toMenuAccueil() {
		changeMenu(FxmlView.MENU);
	}

	protected void toMenuKits() {
		changeMenu(FxmlView.KITS);
	}

	protected void toMenuPersonnels() {
		changeMenu(FxmlView.PERSONNELS);
	}

	protected void toMenuZones() {
		changeMenu(FxmlView.ZONES);
	}

	protected void toMenuKPI() {
		changeMenu(FxmlView.KPI);
	}
}
