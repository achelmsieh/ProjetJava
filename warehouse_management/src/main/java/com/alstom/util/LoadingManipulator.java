package com.alstom.util;

import java.io.File;
import java.util.List;

import com.alstom.model.Kit;
import com.alstom.service.KitService;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class LoadingManipulator {

	public static boolean manipulate(File uploadedFile, KitService kitService) {
		if (uploadedFile != null) {
			int kitsErrorCount = 0, insertedKitsCount = 0, alreadyExistedKits = 0;
			List<Kit> kits = PlanningDataExtrator.getKits(uploadedFile);

			if (kits != null) {
				System.out.println(kits.size());
				for (Kit kit : kits) {
					if (kitService.getKitByOF(kit.getOF()) == null) {
						kitService.save(kit);
						insertedKitsCount++;
					} else {
						alreadyExistedKits++;
					}
				}
				kitsErrorCount = kits.size() - insertedKitsCount - alreadyExistedKits;
			}
			String msg = "Chargement complete avec success !\n\t- " + insertedKitsCount + " Kits sont insérés\n\t- "
					+ alreadyExistedKits + " Kits sont déjà existants dans la BD";
			msg += kitsErrorCount <= 0 ? "" : "\n\t- " + kitsErrorCount + " Erronés quelque part";

			showAlert(AlertType.CONFIRMATION, msg);
			return true;
		} else {
			showAlert(AlertType.ERROR, "Erreur de chargement !");
			return false;
		}
	}

	private static void showAlert(AlertType type, String text) {
		Alert alert = new Alert(type);
		alert.setContentText(text);
		alert.show();
	}
}
