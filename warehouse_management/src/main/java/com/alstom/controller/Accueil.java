package com.alstom.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.alstom.model.Personnel;
import com.alstom.util.FxmlView;
import com.alstom.util.UserSession;
import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class Accueil extends MenuGenericController implements Initializable {

	@FXML
	private JFXButton btnDashboard;
	@FXML
	private JFXButton btnRoles;
	@FXML
	private JFXButton btnKPI;
	@FXML
	private Label userLabel;

	Personnel personnel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		personnel = UserSession.getUser();
		changeMenu(FxmlView.MENU);

		if (personnel != null) {
			setUserView();
			userLabel.setText(personnel.getFullName());
		}
	}

	private void setUserView() {
		switch (personnel.getRole()) {
		case ADMIN:
			break;

		case RES_STOCK:
			setVisibility(btnDashboard, false);
			setVisibility(btnRoles, false);
			setVisibility(btnKPI, false);
			break;

		default:
			break;
		}
	}

	private void setVisibility(Node node, boolean visible) {
		node.setVisible(visible);
		node.managedProperty().bind(node.visibleProperty());
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

}
