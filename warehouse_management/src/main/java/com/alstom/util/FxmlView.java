package com.alstom.util;

public enum FxmlView {
	LOGIN {
		@Override
		public String getFxmlFile() {
			return rootPath + "Login.fxml";
		}
	},
	ACCEUIL {

		@Override
		public String getFxmlFile() {
			return rootPath + "Accueil.fxml";
		}
	},
	MENU {

		@Override
		public String getFxmlFile() {
			return rootPath + "Menu.fxml";
		}
	},
	KITS {

		@Override
		public String getFxmlFile() {
			return rootPath + "Kits.fxml";
		}
	},
	DETAILS_KITS {

		@Override
		public String getFxmlFile() {
			return rootPath + "DetailsKit.fxml";
		}
	},
	PERSONNELS {

		@Override
		public String getFxmlFile() {
			return rootPath + "personnels.fxml";
		}
	},
	ZONES {

		@Override
		public String getFxmlFile() {
			return rootPath + "Zones.fxml";
		}
	},
	EMPLACEMENTS {

		@Override
		public String getFxmlFile() {
			return rootPath + "Emplacements.fxml";
		}
	},
	KPI {

		@Override
		public String getFxmlFile() {
			return rootPath + "KPI.fxml";
		}
	},
	AJOUTER_KIT {

		@Override
		public String getFxmlFile() {
			return rootPath + "AjouterKit.fxml";
		}
	},
	LIVRER_KIT {

		@Override
		public String getFxmlFile() {
			return rootPath + "LivrerKit.fxml";
		}
	};

	protected final String rootPath = "fxml/";

	public abstract String getFxmlFile();
}
