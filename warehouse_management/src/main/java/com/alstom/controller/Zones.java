package com.alstom.controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.alstom.util.FxmlView;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class Zones implements Initializable {

	@FXML
	private BorderPane container;

	@FXML
	private GridPane gridPane;

	public static String selectedZone = null;
	private Map<String, String> zonesColRows = new HashMap<String, String>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initZoneColsRows();
		initGridEventListener();
	}

	@FXML
	void retour(MouseEvent event) {
		selectedZone = null;
		switchContent();
	}

	private void initGridEventListener() {
		gridPane.getChildren().forEach(item -> {
			item.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					setSelectedZone(gridPane.getRowIndex(item), gridPane.getColumnIndex(item));
					switchContent();
				}

			});
		});
	}

	private void switchContent() {
		if (selectedZone == null) {
			container.setCenter(gridPane);
		} else {
			FXMLLoader loader = new FXMLLoader(
					getClass().getClassLoader().getResource(FxmlView.EMPLACEMENTS.getFxmlFile()));

			Parent root = null;
			try {
				root = loader.load();
			} catch (IOException e) {
				e.printStackTrace();
			}

			container.setCenter(root);
		}
	}

	private void setSelectedZone(Integer row, Integer col) {
		String key = "";
		key += row == null ? "0" : row;
		key += ":";
		key += col == null ? "0" : col;

		selectedZone = zonesColRows.get(key);
	}

	private void initZoneColsRows() {
		zonesColRows.put("0:2", "J");
		zonesColRows.put("0:3", "K");
		zonesColRows.put("0:5", "L");
		zonesColRows.put("0:6", "M");
		zonesColRows.put("0:8", "N");

		zonesColRows.put("2:0", "A");
		zonesColRows.put("2:2", "B");
		zonesColRows.put("2:3", "C");
		zonesColRows.put("2:5", "D");
		zonesColRows.put("2:6", "E");
		zonesColRows.put("2:8", "F");
		zonesColRows.put("2:9", "G");
		zonesColRows.put("2:11", "H");
		zonesColRows.put("2:12", "I");
	}
}
