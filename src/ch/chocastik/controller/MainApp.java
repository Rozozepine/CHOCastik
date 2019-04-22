package ch.chocastik.controller;


import java.io.IOException;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.MarkedPlane;
import org.bytedeco.javacv.Marker;
import org.bytedeco.javacv.MarkerDetector;
import org.bytedeco.javacv.ProjectorSettings;
import org.opencv.core.*;

import ch.chocastik.model.Mobile.Mobile;
import ch.chocastik.model.cameras.Camera;
import ch.chocastik.view.accueil.AccueilController;
import ch.chocastik.view.calibration.CalibrationController;
import ch.chocastik.view.start_analyse.AnalyseController;
import ch.chocastik.view.start_analyse.EditGlisseurController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

public class MainApp extends Application {


	private Stage primaryStage;
	private BorderPane rootLayout;
	private ObservableList<Mobile> mobileData = FXCollections.observableArrayList();
	private Camera cam;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
	    this.primaryStage.setTitle("CHOCastik");
	    initRoot();
	    showAcceuil(); 
	}
	/**
	 * Affichage du layout root
	 */
	public void initRoot() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/ch/chocastik/view/rootFX.fxml"));
			rootLayout = (BorderPane) loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
		}catch (Exception e) {
			 e.printStackTrace();
		}
	}
	public void showAcceuil() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/ch/chocastik/view/accueil/AccueilFX.fxml"));
			AnchorPane acceuil = (AnchorPane) loader.load();
			rootLayout.setCenter(acceuil);
	        AccueilController controller = loader.getController();
	        controller.setMainApp(this);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void showAnalyse(int indexCam) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/ch/chocastik/view/start_analyse/AnalyseFX.fxml"));
			AnchorPane analyse = (AnchorPane) loader.load();
			rootLayout.setCenter(analyse);
			AnalyseController controller = loader.getController();
			controller.setMainApp(this);
			controller.dsetCameraChoice(indexCam);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void showCalibration(int indexCam) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/ch/chocastik/view/calibration/CalibrationFX.fxml"));
			AnchorPane calibration = (AnchorPane) loader.load();
			rootLayout.setCenter(calibration);
			CalibrationController controller = loader.getController();
			controller.dsetCameraChoice(indexCam);
			controller.setMainApp(this);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public boolean showEditGlisseur(Mobile mobile, Image frame) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/ch/chocastik/view/start_analyse/EditGlisseurFX.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			Stage dialogueStage = new Stage();
			dialogueStage.setTitle("Edit Glisseur");
			dialogueStage.initModality(Modality.WINDOW_MODAL);
			dialogueStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogueStage.setScene(scene);
	        EditGlisseurController controller = loader.getController();
	        controller.setDialogueStage(dialogueStage);
	        controller.setMobile(mobile);
	        controller.setFrame(frame);
	        dialogueStage.showAndWait();
	        return controller.isOkClicked();
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
    public Stage getPrimaryStage() {
        return primaryStage;
    }
	public ObservableList<Mobile> getMobileData() {
		return mobileData;
	}
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		 Loader.load(opencv_objdetect.class);
		
		launch(args);
	}
	public Camera getCam() {
		return cam;
	}
}