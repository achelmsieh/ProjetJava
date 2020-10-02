package com.alstom.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.alstom.model.Emplacement;
import com.alstom.model.Kit;
import com.alstom.service.EmplacementService;
import com.alstom.service.KitService;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Emplacements implements Initializable {

	@FXML
	private GridPane gridPane;

	private final List<Integer> colAllowed = Arrays.asList(0, 1, 3, 4, 6, 7);
	EmplacementService emplacementService = new EmplacementService();
	KitService ks = new KitService();

	Integer row = 0, col = 0;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		row = col = 0;
		initCells();
	}


	private void initCells() {
//		List<Emplacement> emps = emplacementService.getAllZoneEmplacements(Zones.selectedZone);
//		List<Emplacement> emps = emplacementService.getEmplacements().stream()
//				.filter(emp -> emp.getCoordonnee().substring(0, 1).equals(Zones.selectedZone))
//				.collect(Collectors.toList());

		List<Emplacement> emplacements = new ArrayList<Emplacement>();

		List<Kit> kits = ks.getKitsByZone(Zones.selectedZone);
		for (int i = 1; i < 25; i++) {
			String etiquette = new StringBuilder(Zones.selectedZone).append(i < 10 ? "0" + i : i).toString();

			final Emplacement emp = new Emplacement(etiquette);
			Kit k = kits
					.stream()
					.filter(kit -> kit.getEmplacements() != null && kit.getEmplacements().contains(emp))
					.findFirst().orElse(null);

			if (k != null) {
				emplacements.add(k.getEmplacements().stream().filter(e -> e.getCoordonnee().equals(etiquette))
						.findFirst().orElse(null));
			} else
				emplacements.add(emp);
		}

		List<Emplacement> emps = emplacements.stream().sorted(Comparator.comparing(Emplacement::getCoordonnee))
				.collect(Collectors.toList());

		emps.stream().forEach(emp -> {
			if (!colAllowed.contains(col))
				col++;
			if (col >= 8) {
				row += 2;
				col = 0;
			}

			addRectangel(emp);
			addZoneLabel(emp);

			if (emp.getKits() != null && emp.getKits().size() > 0) {
				addOfLabel(emp);
			}

			col++;
		});
	}

	private void addRectangel(Emplacement emp) {
		Color recColor = Color.GREEN;
		if (emp.getKits() != null && emp.getKits().size() > 0)
			recColor = Color.RED;

		Rectangle rec = new Rectangle(165, 100);
		rec.setFill(recColor);
		rec.setStrokeWidth(0);
		gridPane.add(rec, col, row);
	}

	private void addZoneLabel(Emplacement emp) {
		Label zoneLabel = new Label(emp.getCoordonnee());
		zoneLabel.setAlignment(Pos.CENTER);
		zoneLabel.setTextFill(Color.WHITE);
		gridPane.setHalignment(zoneLabel, HPos.CENTER);
		gridPane.setValignment(zoneLabel, VPos.BOTTOM);
		gridPane.add(zoneLabel, col, row);
	}

	private void addOfLabel(Emplacement emp) {
		String OFs = emp.getKits().stream().map(Kit::getOF).collect(Collectors.joining("\n"));
		Label ofLabel = new Label(OFs);
		ofLabel.setAlignment(Pos.CENTER);
		ofLabel.setTextFill(Color.WHITE);
		gridPane.setHalignment(ofLabel, HPos.CENTER);
		gridPane.add(ofLabel, col, row);
	}
}
