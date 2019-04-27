package ch.chocastik.view.analyse;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;


import ch.chocastik.controller.MainApp;
import ch.chocastik.model.analyse.objet.Mobile;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class EditGlisseurController {
	private MainApp mainApp;
	@FXML
    private TableView<Mobile> tableMobile;
	@FXML
	private  TableColumn<Mobile, String> colName;
	@FXML
	private  TableColumn<Mobile, String> colCouleur;
	@FXML
	private Label minRadLabel;
	@FXML
	private Label maxRadLabel;
	@FXML
	private Label colorLabel;
	@FXML
	private Label nameLabel;
	@FXML
	private Label tolLabel;
	@FXML
	private Label dilatationLabel;
	@FXML
	private Label erosionLabel;
	private Image frame;
	public EditGlisseurController() {}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		//on ajoute la liste des mobile a la table
		tableMobile.setItems(mainApp.getMobileData());
	}
	/**
	 * on recupere la frame actuelle pour l'ajoute de mobile
	 * @param frame
	 */
	public void setFrame(Image frame) {
		this.frame = frame;
	}
	/**
	 * On initalise le tableau dans cette fonction
	 */
	@FXML
    private void initialize() {
		colName.setCellValueFactory(cellData -> cellData.getValue().nameExportProperty());
		colCouleur.setCellValueFactory(cellData -> cellData.getValue().nameExportProperty());
		showMobileDetail(null);
		tableMobile.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showMobileDetail((newValue)));
	}
	/**
	 * On met à jour la vue pour afficher le mobile selectionner
	 * @param mob
	 */
	private void showMobileDetail(Mobile mob) {
		if (mob != null) {
			minRadLabel.setText(Integer.toString(mob.getMinRad()));
			minRadLabel.setText(Integer.toString(mob.getMinRad()));
			nameLabel.setText(Integer.toString(mob.getMinRad()));
			tolLabel.setText(Integer.toString(mob.getMinRad()));
			dilatationLabel.setText(Integer.toString(mob.getMinRad()));
			erosionLabel.setText(Integer.toString(mob.getMinRad()));
			colorLabel.setText(mob.getColor().toString());
		}else {
			minRadLabel.setText("");
			minRadLabel.setText("");
			nameLabel.setText("");
			tolLabel.setText("");
			dilatationLabel.setText("");
			erosionLabel.setText("");
			colorLabel.setText("");
		}
		
	}
	/**
	 * Action lancé lorsque l'utilisateur clique sur le bouton delete
	 */
	@FXML
	private void handleDeleteMobile() {
		int selectIndex = tableMobile.getSelectionModel().getSelectedIndex();
		tableMobile.getItems().remove(selectIndex);
	}
	/**
	 * Action lancée lorsque l'utilisateur clique sur le bouton Add
	 */
	@FXML
	private void handleNewMobile() {
		Mobile mobile = new Mobile(Color.BLUE, "Tpn"); // mobile prédifinit 
		boolean isOk = mainApp.showAddGlisseur(mobile, frame); // si il n'y pas d'erreur on l'ajoute
		if(isOk) {
			this.mainApp.getMobileData().add(mobile);
		}
	}
	/**
	 *  Action lancée losrque l'utilisateur clique sur le bouton Edit
	 */
	@FXML
	private void handleEditMobile() {
		Mobile selectMobile = tableMobile.getSelectionModel().getSelectedItem();
		// si le mobile selctionnée existe on affiche la fenetre d'ajout avec les parametetre du mobile sinon
		// on affiche un message d'erreur
		if(selectMobile != null) {
			boolean isOk = mainApp.showAddGlisseur(selectMobile, frame); // si il n'y pas d'erreur on l'ajoute
			if(isOk) {
				showMobileDetail(selectMobile);
			}
		}else {
			//rien n'a été selectionnée
			Alert alert = new Alert(AlertType.WARNING);
		    alert.initOwner(mainApp.getPrimaryStage());
		    alert.setTitle("No Selection");
	        alert.setHeaderText("No Mobile Selected"); 
	        alert.setContentText("Please select a Mobile in the table.");
	        alert.showAndWait();
		}
		
	}
}
