package com.alstom.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import com.alstom.model.Kit;
import com.jfoenix.controls.JFXDatePicker;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class DashboardKit extends KitsGenericController implements Initializable {

	@FXML
	private TableView<Kit> BloqueTable;
	@FXML
	private TableColumn<Kit, String> OfBColumn;
	@FXML
	private TableColumn<Kit, String> ProjetBColumn;
	@FXML
	private TableColumn<Kit, String> NombreBColumn;
	@FXML
	private TableView<Kit> AnticpeTable;
	@FXML
	private TableColumn<Kit, String> OfAColumn;
	@FXML
	private TableColumn<Kit, String> ProjetAColumn;
	@FXML
	private TableColumn<Kit, String> NombreAColumn;
	@FXML
	private Label SommeBloque;
	@FXML
	private Label SommeAnticipe;
	@FXML
	private JFXDatePicker Date1;
	@FXML
	private JFXDatePicker Date2;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initDatePickers();
		initCells();

		initDoubleClickEvent();
	}

	private void initDoubleClickEvent() {
		setDoubleClickEvent(AnticpeTable);
		setDoubleClickEvent(BloqueTable);
	}

	private void initCells() {
		OfBColumn.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getOF()));
		ProjetBColumn.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getProjet()));
		OfAColumn.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getOF()));
		ProjetAColumn.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getProjet()));

		Callback<TableColumn<Kit, String>, TableCell<Kit, String>> cellFactory = new Callback<TableColumn<Kit, String>, TableCell<Kit, String>>() {

			@Override
			public TableCell<Kit, String> call(TableColumn<Kit, String> param) {
				final TableCell<Kit, String> cell = new TableCell<Kit, String>() {

					@Override
					protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							setGraphic(null);
							Kit kit = getTableView().getItems().get(getIndex());
							String val = null;
							if (kit != null) {
								Date ds = kit.getDateSortie() == null ? new Date() : kit.getDateSortie();
								Date dsp = kit.getDateSortiePrevue();
								if (ds != null && dsp != null)
									val = "" + Math.abs(ChronoUnit.DAYS.between(toLocalDate(dsp), toLocalDate(ds)));
							}
							setText(val);
						}
					}
				};
				return cell;
			}

		};

		NombreBColumn.setCellFactory(cellFactory);
		NombreAColumn.setCellFactory(cellFactory);
	}

	private void initDatePickers() {
		LocalDate initial = LocalDate.now();
		LocalDate start = initial.withDayOfMonth(1);
		LocalDate end = initial.withDayOfMonth(initial.lengthOfMonth());

		Date1.setValue(start);
		Date2.setValue(end);

		Date1.setOnAction(event -> updateList());
		Date2.setOnAction(event -> updateList());

		displayList(toDate(start), toDate(end));
	}

	private void updateList() {
		Date date1 = toDate(Date1.getValue());
		Date date2 = toDate(Date2.getValue());

		displayList(date1, date2);
	}

	private void displayList(Date dateStart, Date dateEnd) {
		List<Kit> kitsBloques = kitService.getKitsBloques(dateStart, dateEnd);
		List<Kit> kitsAnticipes = kitService.getKitsAnticipe(dateStart, dateEnd);

		setTableItems(BloqueTable, kitsBloques);
		setTableItems(AnticpeTable, kitsAnticipes);

		int kitsBloquesCount = 0, kitsAnticipeCount = 0;
		if (kitsBloques != null)
			kitsBloquesCount = kitsBloques.size();
		if (kitsAnticipes != null)
			kitsAnticipeCount = kitsAnticipes.size();

		SommeBloque.setText("Total : " + kitsBloquesCount);
		SommeAnticipe.setText("Tatal : " + kitsAnticipeCount);
	}

	void setTableItems(TableView<Kit> table, List<Kit> listkit) {
		ObservableList<Kit> kits = FXCollections.observableArrayList(listkit);
		table.setItems(kits);
		table.refresh();
	}

}
