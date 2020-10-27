package com.alstom.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.alstom.model.Kit;
import com.alstom.service.KitService;
import com.alstom.util.FxmlView;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class KitsGenericController {

	protected KitService kitService = new KitService();
	protected final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd   hh:mm");
	protected final ZoneId defaultZoneId = ZoneId.systemDefault();

	private Kit selectedKit = null;

	private final String DETAILS_KIT_TITLE = "CoupeAYA - Détails du Kit";
	private final String AJOUTER_KIT_TITLE = "CoupeAYA - Ajouter Kit";
	private final String LIVRER_KIT_TITLE = "CoupeAYA - Livrer Kit";

	protected void setDoubleClickEvent(TableView<Kit> table) {
		table.setRowFactory(tableView -> {
			TableRow<Kit> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					selectedKit = tableView.getSelectionModel().getSelectedItem();
					showWindow(FxmlView.DETAILS_KITS);
				}
			});
			return row;
		});
	}

	protected void search(String value) {

	}

	protected void showWindow(FxmlView window) {
		switch (window) {
		case AJOUTER_KIT:
			showWindow(window, AJOUTER_KIT_TITLE);
			break;
		case LIVRER_KIT:
			showWindow(window, LIVRER_KIT_TITLE);
			break;
		case DETAILS_KITS:
			showWindow(window, DETAILS_KIT_TITLE);
			break;
		default:
			break;
		}
	}

	private void showWindow(FxmlView window, String title) {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(window.getFxmlFile()));
		BorderPane borderpane;
		try {

			borderpane = loader.load();

			Object controller = loader.getController();
			if (controller instanceof DetailsKit)
				((DetailsKit) controller).setData(selectedKit, false);

			Stage stage = new Stage();
			Scene scene = new Scene(borderpane);

			stage.setMinWidth(820);
			stage.setMinHeight(550);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.getIcons().add(new Image("icons/warehouse.png"));
			stage.setTitle(title);
			stage.setScene(scene);

			stage.setOnHiding(e -> {
				search("");
			});

			stage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected LocalDate toLocalDate(Date date) {
		return date.toInstant().atZone(defaultZoneId).toLocalDate();
	}

	protected Date toDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
	}
}
