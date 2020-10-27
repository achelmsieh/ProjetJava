package com.alstom.controller;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.alstom.model.enums.EtatKit;
import com.alstom.service.EmplacementService;
import com.alstom.service.KPIService;
import com.alstom.service.KitService;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.TickLabelOrientation;
import eu.hansolo.medusa.skins.SlimSkin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class KPI implements Initializable {
//	@FXML
//	PieChart chart_stock;
//	
	@FXML
	Gauge chart_stock2;
	@FXML
	PieChart chart_projet;

	@FXML
	Label nombre_sortie;
	@FXML
	Label nombre_planing;

	@FXML
	Label nombre_stock;
	@FXML
	Label StockMoyen;

	@FXML
	Label nombre_entree;

	@FXML
	CategoryAxis Semaineaxis;

	@FXML
	NumberAxis OFaxis;

	@FXML
	BarChart<String, Long> Barchart;
	double somme_jour = 0;
	KPIService kpis = new KPIService();
	EmplacementService empls = new EmplacementService();
	KitService kits = new KitService();
	XYChart.Series serie = new Series<>();

	Long somme_stock, somme_kit, somme_planing, somme_sortie, somme_bloque;
	private final ZoneId defaultZoneId = ZoneId.systemDefault();

	@FXML
	private void PlaningPie() {
		setPie(EtatKit.PLANNING);
	}

	@FXML
	private void StockPie() {
		setPie(EtatKit.ENSTOCK);
	}

	public void setPie(EtatKit etat) {

		Map<String, Long> mapProj = kits.getKits(etat).stream().collect(
				Collectors.groupingBy(k -> k.getProjet() != null ? k.getProjet() : "autres", Collectors.counting()));
		ObservableList<PieChart.Data> pieChartData2 = FXCollections.observableArrayList();
		mapProj.forEach((k, v) -> pieChartData2.add(new PieChart.Data(k + " [" + v + "]", v)));
		chart_projet.setData(pieChartData2);
	}

	public void settext() {
		somme_stock = kpis.getKitsCount(EtatKit.ENSTOCK);
		somme_kit = kpis.getKitsCount();
		somme_planing = kpis.getKitsCount(EtatKit.PLANNING);
		Date ceJour = Date.from(LocalDate.now().atStartOfDay(defaultZoneId).toInstant());
		somme_bloque = Long.valueOf(kits.getKitsBloques(ceJour).size());
		somme_sortie = somme_kit - somme_stock - somme_planing;
		nombre_stock.setText("  " + somme_stock + " OF(" + somme_bloque + " Bloque).");
		nombre_sortie.setText("  " + somme_sortie + " OF.");
		nombre_planing.setText("  " + somme_planing + " OF.");
	}

	public void setNiveauStock() {
		long countAllEmplacement = empls.countAllEmplacement();
		long countOccupeEmplacement = empls.countOccupedEmplacement();
		double percentocuppe = (double) countOccupeEmplacement / (double) countAllEmplacement;
		chart_stock2.setSkin(new SlimSkin(chart_stock2));
		chart_stock2.setTitle("Niveau de Stock");
		chart_stock2.setUnit("%");
		chart_stock2.setDecimals(1);
		chart_stock2.setValue(percentocuppe);

		chart_stock2.setValueColor(Color.BLACK);
		chart_stock2.setTitleColor(Color.BLACK);
		chart_stock2.setSubTitleColor(Color.RED);

		chart_stock2.setBarColor(Color.RED);
		chart_stock2.setNeedleColor(Color.BLACK);
		chart_stock2.setThresholdColor(Color.BLACK);
		chart_stock2.setTickLabelColor(Color.BLACK);
		chart_stock2.setTickMarkColor(Color.BLACK);
		chart_stock2.setTickLabelOrientation(TickLabelOrientation.ORTHOGONAL);

	}

	public void setStockMoyen() {
		Map<Integer, Long> dd = kits.getAllDateEntre().stream().collect(Collectors
				.groupingBy(d -> Integer.valueOf(new SimpleDateFormat("w").format(d)), Collectors.counting()));

		TreeMap<Integer, Long> Maptriee = new TreeMap<>(dd);

		if (!Maptriee.isEmpty()) {
			for (int i = Maptriee.firstKey(); i < Maptriee.lastKey(); i++) {
				if (Maptriee.get(i) == null) {
					Maptriee.put(i, 0L);
				}
			}
		}
		Maptriee.forEach((k, v) -> serie.getData().add(new XYChart.Data<String, Long>(Integer.toString(k), v)));
		Barchart.getData().add(serie);
	}

	public void setPocession() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		kits.getKits().stream().forEach(a -> {
			try {
				if (a.getDateSortie() != null) {
					long diff = sdf.parse(a.getDateSortie().toString()).getTime()
							- sdf.parse(a.getDateEntree().toString()).getTime();

					somme_jour += TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		});

		if (kits.getKits(EtatKit.SORTIE).size() != 0) {
			String result = String.format("%.2f", somme_jour / kits.getKits(EtatKit.SORTIE).size());
			StockMoyen.setText(": " + result + " Jours");
		} else
			StockMoyen.setText(": 0 Jours");
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		setPocession();
		setPie(EtatKit.ENSTOCK);
		settext();
		setNiveauStock();
		setStockMoyen();

	}

}
