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
		System.out.println("Accueil");
		changeMenu(FxmlView.MENU);
	}

	protected void toMenuKits() {
		System.out.println("kitService");
		changeMenu(FxmlView.KITS);
	}

	protected void toMenuPersonnels() {
		System.out.println("persons");
		changeMenu(FxmlView.PERSONNELS);
	}

	protected void toMenuZones() {
		System.out.println("zones");
		changeMenu(FxmlView.ZONES);
	}

	protected void toMenuKPI() {
		System.out.println("kpi");
		changeMenu(FxmlView.KPI);
	}
}
