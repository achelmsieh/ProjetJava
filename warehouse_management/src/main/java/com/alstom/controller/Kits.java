package com.alstom.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.alstom.model.Emplacement;
import com.alstom.model.Kit;
import com.alstom.model.Personnel;
import com.alstom.model.enums.EtatKit;
import com.alstom.service.KitService;
import com.alstom.util.FileUploader;
import com.alstom.util.FileUploader.FileType;
import com.alstom.util.FxmlView;
import com.alstom.util.LoadingManipulator;
import com.jfoenix.controls.JFXToggleButton;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Kits extends KitsGenericController implements Initializable {

	@FXML
	private BorderPane parent;
	@FXML
	private StackPane stackPaneTable;
	ToggleGroup toggleGroup = new ToggleGroup();
	@FXML
	TextField search_field;
	@FXML
	private Label countLabel;
	@FXML
	private JFXToggleButton enStockBtn;
	@FXML
	private JFXToggleButton sortieBtn;
	@FXML
	private JFXToggleButton planningBtn;
	@FXML
	TableView<Kit> table_of;
	@FXML
	private TableColumn<Kit, String> col_of;
	@FXML
	private TableColumn<Kit, String> col_emplacement;
	@FXML
	private TableColumn<Kit, String> col_projet;
	@FXML
	private TableColumn<Kit, String> col_etat;
	@FXML
	private TableColumn<Kit, String> col_date_entree;
	@FXML
	private TableColumn<Kit, String> col_date_sortie;
	@FXML
	private TableColumn<Kit, String> col_res_prod;
	@FXML
	private TableColumn<Kit, String> col_res_stock;
	@FXML
	private TableColumn<Kit, String> col_run_time;
	@FXML
	private TableColumn<Kit, String> col_dtr;
	@FXML
	private TableColumn<Kit, String> col_description;
	@FXML
	private TableColumn<Kit, String> col_nrame;
	@FXML
	private TableColumn<Kit, String> col_indice_cpc;

	private KitService kitService = new KitService();
	private final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd   hh:mm");

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initToggleButtons();
		initSearchField();
		initCols();
		setTableItems(kitService.getKits());

		setDoubleClickEvent(table_of);
	}

	private void initToggleButtons() {
		enStockBtn.setToggleGroup(toggleGroup);
		sortieBtn.setToggleGroup(toggleGroup);
		planningBtn.setToggleGroup(toggleGroup);
	}

	private void initSearchField() {
		search_field.textProperty().addListener((observable, oldValue, newValue) -> {
			search(newValue);
		});
	}

	private void initCols() {
		col_of.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getOF()));
		col_etat.setCellValueFactory(cdf -> {
			EtatKit etat = cdf.getValue().getEtat();
			return new SimpleStringProperty(etat == null ? "" : etat.toString());
		});
		col_projet.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getProjet()));
		col_dtr.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getDTR()));
		col_run_time.setCellValueFactory(cdf -> new SimpleStringProperty(String.valueOf(cdf.getValue().getRunTime())));
		col_description.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getDescription()));
		col_nrame.setCellValueFactory(cdf -> new SimpleStringProperty(String.valueOf(cdf.getValue().getnRAME())));
		col_indice_cpc.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getIndiceCPC()));

		col_date_entree.setCellValueFactory(cdf -> {
			Date de = cdf.getValue().getDateEntree();
			return new SimpleStringProperty(de == null ? "" : dateFormat.format(de));
		});
		col_date_sortie.setCellValueFactory(cdf -> {
			Date ds = cdf.getValue().getDateSortie();
			return new SimpleStringProperty(ds == null ? "" : dateFormat.format(ds));
		});

		col_emplacement.setCellValueFactory(cdf -> {
			if (cdf.getValue().getEmplacements() != null)
				return new SimpleStringProperty(cdf.getValue().getEmplacements().stream()
						.map(Emplacement::getCoordonnee).collect(Collectors.joining("\n")));
			else
				return new SimpleStringProperty("");
		});

		col_res_stock.setCellValueFactory(cdf -> {
			Personnel rs = cdf.getValue().getResStock();
			return new SimpleStringProperty(rs == null ? "" : rs.getNom() + " " + rs.getPrenom());
		});

		col_res_prod.setCellValueFactory(cdf -> {
			Personnel rp = cdf.getValue().getResProduction();
			return new SimpleStringProperty(rp == null ? "" : rp.getNom() + " " + rp.getPrenom());
		});
	}

	private void setTableItems(List<Kit> list) {
		ObservableList<Kit> kits = null;
		if (list != null)
			kits = FXCollections.observableArrayList(list);
		table_of.setItems(kits);
		table_of.refresh();

		long countSorties = 0, countEnStocks = 0, countPlannings = 0, countTotal = 0;

		if (kits != null) {
			countTotal = kits.size();
			countSorties = kits.stream().filter(Kit::isSortie).count();
			countEnStocks = kits.stream().filter(Kit::isEnStock).count();
			countPlannings = kits.stream().filter(Kit::isPlanning).count();
		}

		countLabel.setText("En Stocks : " + countEnStocks + "\t\t Sorties : " + countSorties + "\t\t Plannings : "
				+ countPlannings + "\t\t Total : " + countTotal);
	}

	@Override
	protected void search(String value) {
		String val = value.replaceAll("\\s+", "");
		if (val.isEmpty()) {
			setTableItems(kitService.getKits());
		} else if (isNumeric(val)) {
			setTableItems(kitService.searchKit(value));
		}
	}

	@FXML
	public void ajouterKit() throws IOException {
		showWindow(FxmlView.AJOUTER_KIT);
	}

	@FXML
	public void sortirKit(MouseEvent event) {
		showWindow(FxmlView.LIVRER_KIT);
	}

	@FXML
	private void filter() {
		if (enStockBtn.isSelected()) {
			setTableItems(kitService.getKits(EtatKit.ENSTOCK));
		}

		if (sortieBtn.isSelected()) {
			setTableItems(kitService.getKits(EtatKit.SORTIE));
		}

		if (planningBtn.isSelected()) {
			setTableItems(kitService.getKits(EtatKit.PLANNING));
		}

		if ((!enStockBtn.isSelected() && !sortieBtn.isSelected() && !planningBtn.isSelected())
				|| (enStockBtn.isSelected() && sortieBtn.isSelected() && planningBtn.isSelected())) {
			search(search_field.getText());
		}
	}

	@FXML
	void Refresh(MouseEvent event) {
		search("");
	}

	public boolean isNumeric(String strNum) {
		Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

		if (strNum == null) {
			return false;
		}

		return pattern.matcher(strNum).matches();
	}

	VBox box;

	void disableContent() {
		ProgressIndicator pi = new ProgressIndicator();
		box = new VBox(pi);
		box.setAlignment(Pos.CENTER);
		stackPaneTable.getChildren().add(box);
		parent.setDisable(true);
	}

	void enableContent() {
		parent.setDisable(false);
		box.setVisible(false);
	}

	@FXML
	void uploadPlanning(ActionEvent event) {
		disableContent();
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		File uploadedFile = FileUploader.upload(stage, FileType.PLANNING);
		boolean sucess = LoadingManipulator.manipulate(uploadedFile, kitService);
		if (sucess)
			search("");
		enableContent();
	}

}
