package com.alstom.controller;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class Menu extends GenericController {

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
}
