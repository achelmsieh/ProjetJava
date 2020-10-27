package com.alstom.controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.alstom.model.Personnel;
import com.alstom.service.LoginService;
import com.alstom.util.FxmlView;
import com.alstom.util.UserSession;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class Login implements Initializable {

	@FXML
	private JFXTextField usernameTF;
	@FXML
	private JFXPasswordField passwordTF;
	@FXML
	private JFXButton btnConnect;

	LoginService LoginService = new LoginService();

	@FXML
	void getConnected(ActionEvent event) throws SQLException {

		if (!verifyFields())
			return;

		Personnel user = LoginService.connecting(usernameTF.getText().trim(), passwordTF.getText().trim());

		if (user != null) {
			UserSession.getInstance(user);
			redirectToHomePage();
		} else
			showErrorAlert("Le nom d'utilisateur ou le mot de passe est incorrect.");

	}

	private Stage getStage() {
		return (Stage) btnConnect.getScene().getWindow();
	}

	private void redirectToHomePage() {
		System.out.println("ok");
		getStage().hide();
		try {
			Stage stage = new Stage();
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(FxmlView.ACCEUIL.getFxmlFile()));
			Scene scene = new Scene(root);

			stage.setScene(scene);
			stage.setMaximized(true);
			stage.getIcons().add(new Image("icons/warehouse.png"));
			stage.setTitle("CoupeAYA");
			stage.setMinWidth(1200);
			stage.setMinHeight(650);
			stage.setResizable(true);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void intialize_enter(ActionEvent event)
	{
		usernameTF.getScene().setOnKeyPressed(e -> {
		    if (e.getCode() == KeyCode.ENTER) {
		        System.out.println("entrer key was pressed");
		    }
		});
	}
	private boolean verifyFields() {
		if (usernameTF == null || usernameTF.getText().isEmpty()) {
			showUsernameError(true);
			showErrorAlert("Veuillez s'il vous plaît saisir votre nom d'utilisateur");
			return false;
		} else {
			showUsernameError(false);
		}

		if (passwordTF == null || passwordTF.getText().isEmpty()) {
			showPasswordError(true);
			showErrorAlert("Veuillez s'il vous plaît saisir votre mot de passe");
			return false;
		} else {
			showPasswordError(false);
		}

		return true;
	}

	private void showErrorAlert(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Erreur de connection");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.show();
	}

	private void showUsernameError(boolean show) {
		if (show)
			usernameTF.setStyle("-jfx-focus-color:red;-jfx-unfocus-color:red;");
		else
			usernameTF.setStyle("-jfx-focus-color:#a4772f;-jfx-unfocus-color:#a4772f;");
	}

	private void showPasswordError(boolean show) {
		if (show)
			passwordTF.setStyle("-jfx-focus-color:red;-jfx-unfocus-color:red;");
		else
			passwordTF.setStyle("-jfx-focus-color:#a4772f;-jfx-unfocus-color:#a4772f;");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	
	}

}
