package com.alstom.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.alstom.model.Personnel;
import com.alstom.util.UserSession;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class Menu extends MenuGenericController implements Initializable {

	@FXML
	private HBox normalUserAction;
	@FXML
	private HBox adminActions;
	Personnel personnel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		personnel = UserSession.getUser();

		if (personnel != null) {
			setUserView();
		}
	}

	private void setUserView() {
		switch (personnel.getRole()) {
		case ADMIN:
			break;

		case RES_STOCK:
		default:
			setVisibility(adminActions, false);
			break;
		}
	}

	private void setVisibility(Node node, boolean visible) {
		node.setVisible(visible);
		node.managedProperty().bind(node.visibleProperty());
	}

}
