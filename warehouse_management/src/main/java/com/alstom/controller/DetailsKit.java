package com.alstom.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import com.alstom.model.Kit;
import com.jfoenix.controls.JFXTextField;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class DetailsKit implements Initializable {

	@FXML
	private JFXTextField ofTF;

	@FXML
	private JFXTextField projetTF;

	@FXML
	private JFXTextField dtrTF;

	@FXML
	private JFXTextField runTimeTF;

	@FXML
	private JFXTextField descriptionTF;

	@FXML
	private JFXTextField nrameTF;

	@FXML
	private JFXTextField indiceCPCTF;

	private Kit displiedKit = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ofTF.setEditable(false);
		displiedKit = Kits.selectedKit;

		if (displiedKit != null) {
			setFieldsEditable(false);
			ofTF.setText(displiedKit.getOF());
			projetTF.setText(displiedKit.getProjet());
			dtrTF.setText(displiedKit.getDTR());
			dtrTF.setText(displiedKit.getDTR());
			dtrTF.setText(displiedKit.getDTR());
			dtrTF.setText(displiedKit.getDTR());
		}
	}

	private void setFieldsEditable(boolean editable) {
		Stream.of(projetTF, dtrTF, runTimeTF, descriptionTF, nrameTF, indiceCPCTF)
				.forEach(tf -> tf.setEditable(editable));
	}

}
