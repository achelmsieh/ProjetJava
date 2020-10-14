package com.alstom.application;

import com.alstom.connection.EntityManagerInitializer;
import com.alstom.service.AppInitializer;
import com.alstom.util.FxmlView;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	public void start(Stage primaryStage) throws Exception {
		EntityManagerInitializer.init();
//		AppInitializer.init();
//		AppInitializer.fetchAllData();

		try {
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(FxmlView.LOGIN.getFxmlFile()));

			Scene scene = new Scene(root);

			primaryStage.getIcons().add(new Image("icons/warehouse.png"));
			primaryStage.setTitle("Coupure");
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
