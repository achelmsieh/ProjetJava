package com.alstom.controller;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.alstom.model.EtatKit;
import com.alstom.service.EmplacementService;
import com.alstom.service.KPIService;
import com.alstom.service.KitService;

import eu.hansolo.medusa.FGauge;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.TickLabelOrientation;
import eu.hansolo.medusa.skins.ModernSkin;
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

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		long countAllEmplacement = empls.countAllEmplacement();
		long countOccupeEmplacement = empls.countOccupedEmplacement();
		final long freeEmplacements = countAllEmplacement - countOccupeEmplacement;
		double percentfree = (double) freeEmplacements / (double) countAllEmplacement;
		double percentocuppe = (double) countOccupeEmplacement / (double) countAllEmplacement;
		ObservableList<PieChart.Data> pieChartData1 = FXCollections.observableArrayList(
				new PieChart.Data("Free", percentfree), new PieChart.Data("Ocuppé", percentocuppe));
		ObservableList<PieChart.Data> pieChartData2 = FXCollections.observableArrayList(
				new PieChart.Data("Projet3", 60), new PieChart.Data("Projet2", 25), new PieChart.Data("Projet1", 15));
		nombre_stock.setText("  " + Long.toString(kpis.get_nombre_stock()) + " OF.");
		nombre_sortie.setText("  " + Long.toString(kpis.get_all() - kpis.get_nombre_stock()) + " OF.");

//		chart_stock.setData(pieChartData1);
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
		
		chart_projet.setData(pieChartData2);

		// barchart
		System.out.println("\n\n\n" + kits.getAllDateEntre());

		Map<Integer, Long> dd = kits.getAllDateEntre().stream().collect(Collectors
				.groupingBy(d -> Integer.valueOf(new SimpleDateFormat("w").format(d)), Collectors.counting()));

		TreeMap<Integer, Long> Maptriee = new TreeMap<>(dd);

//		Maptriee.forEach((k, v) -> serie.getData().add(new XYChart.Data<String,Long>(Integer.toString(k), v)));

		for (int i = Maptriee.firstKey(); i < Maptriee.lastKey(); i++) {
			if (Maptriee.get(i) == null) {
				Maptriee.put(i, 0L);
			}
		}
		Maptriee.forEach((k, v) -> serie.getData().add(new XYChart.Data<String, Long>(Integer.toString(k), v)));
		Barchart.getData().add(serie);
		double SejourMoyen;
		SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
		kits.getKits().stream().forEach(a -> {
			try {
				if (a.getDateSortie() != null) {
					long diff = SDF.parse(a.getDateSortie().toString()).getTime()
							- SDF.parse(a.getDateEntree().toString()).getTime();

					somme_jour += TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		});
		System.out.println(somme_jour);
		StockMoyen.setText(": " + Double.toString(somme_jour / kits.filterByEtat(EtatKit.SORTIE).size()) + " Jours");

	}

}
