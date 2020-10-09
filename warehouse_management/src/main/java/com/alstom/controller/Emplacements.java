package com.alstom.controller;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
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
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class Emplacements implements Initializable {

	@FXML
	private GridPane gridPane;

	private final List<Integer> colAllowed = Arrays.asList(0, 1, 3, 4, 6, 7);
	EmplacementService emplacementService = new EmplacementService();
	KitService ks = new KitService();

	Integer row = 0, col = 0;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initCells();
	}

	private void initCells() {
		getOccupedEmpsMap().forEach((emp, OFs) -> {
			if (!colAllowed.contains(col))
				col++;
			if (col >= 8) {
				row += 2;
				col = 0;
			}

			if (OFs != null && !OFs.isEmpty()) {
				addRectangel("FF0000");
				addOfLabel(OFs);
			} else {
				addRectangel("008000");
			}

			addZoneLabel(emp);
			col++;
		});
	}

	private Map<String, List<String>> getOccupedEmpsMap() {
		Map<String, List<String>> resltMap = new HashMap<String, List<String>>();
		List<Kit> Kits = ks.getKitsByZone(Zones.selectedZone);

		for (int i = 1; i < 25; i++) {
			String etiquette = new StringBuilder(Zones.selectedZone).append(i < 10 ? "0" + i : i).toString();

			List<String> OFs = getOFsForEtiquette(Kits, etiquette);

			resltMap.put(etiquette, OFs);
		}

		TreeMap<String, List<String>> sorted = new TreeMap<>(resltMap);
		return sorted;
	}

	private List<String> getOFsForEtiquette(List<Kit> kits, String etiquette) {
		return kits.stream()
				.filter(kit -> kit.getEmplacements() != null
						&& kit.getEmplacements().contains(new Emplacement(etiquette)))
				.distinct().map(Kit::getOF).collect(Collectors.toList());
	}

	private void addRectangel(String couleur) {
		HBox rec = new HBox();
		rec.setFillHeight(true);
		rec.setStyle("-fx-background-color: #" + couleur);

		gridPane.add(rec, col, row);
	}

	private void addZoneLabel(String empLabel) {
		Label zoneLabel = new Label(empLabel);
		zoneLabel.setAlignment(Pos.CENTER);
		zoneLabel.setTextFill(Color.WHITE);
		gridPane.setHalignment(zoneLabel, HPos.CENTER);
		gridPane.setValignment(zoneLabel, VPos.BOTTOM);
		gridPane.add(zoneLabel, col, row);
	}

	private void addOfLabel(List<String> OFs) {
		String OFString = OFs.stream().collect(Collectors.joining("\n"));
		Label ofLabel = new Label(OFString);
		ofLabel.setAlignment(Pos.CENTER);
		ofLabel.setTextFill(Color.WHITE);
		gridPane.setHalignment(ofLabel, HPos.CENTER);
		gridPane.add(ofLabel, col, row);
	}
}
