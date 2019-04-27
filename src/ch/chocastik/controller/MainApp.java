package ch.chocastik.controller;

import ch.chocastik.model.analyse.objet.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.CameraDevice;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.MarkedPlane;
import org.bytedeco.javacv.Marker;
import org.bytedeco.javacv.MarkerDetector;
import org.bytedeco.javacv.ProjectorSettings;
import org.opencv.core.*;


import ch.chocastik.model.cameras.Camera;
import ch.chocastik.view.accueil.AccueilController;
import ch.chocastik.view.analyse.AddGlisseurController;
import ch.chocastik.view.analyse.AddReferentielleController;
import ch.chocastik.view.analyse.AnalyseController;
import ch.chocastik.view.analyse.EditGlisseurController;
import ch.chocastik.view.calibration.CalibrationController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

public class MainApp extends Application {


	private Stage primaryStage;
	private BorderPane rootLayout;
	private ObservableList<Mobile> mobileData = FXCollections.observableArrayList();
	private Referentiel referentiel = new Referentiel();
	private ArrayList<Tracker> listTraker = new ArrayList<Tracker>();
	private Mesure mesure = new Mesure();
	private Camera cam;
	private ConcurrentLinkedQueue<IplImage> pileImage = new ConcurrentLinkedQueue<IplImage>();
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
	public void showAnalyse(CameraDevice cam) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/ch/chocastik/view/analyse/AnalyseFX.fxml"));
			AnchorPane analyse = (AnchorPane) loader.load();
			rootLayout.setCenter(analyse);
			AnalyseController controller = loader.getController();
			controller.setMainApp(this);
			controller.dsetCameraChoice(cam);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void showCalibration(CameraDevice cam) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/ch/chocastik/view/calibration/CalibrationFX.fxml"));
			AnchorPane calibration = (AnchorPane) loader.load();
			rootLayout.setCenter(calibration);
			CalibrationController controller = loader.getController();
			controller.dsetCameraChoice(cam);
			controller.setMainApp(this);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void showEditGlisseur(Image frame) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/ch/chocastik/view/analyse/EditGlisseurFX.fxml"));
			SplitPane page = (SplitPane) loader.load();
			Stage dialogueStage = new Stage();
			dialogueStage.setTitle("Edit Glisseur");
			dialogueStage.initModality(Modality.WINDOW_MODAL);
			dialogueStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogueStage.setScene(scene);
	        EditGlisseurController controleur = loader.getController();
	        controleur.setMainApp(this);
	        controleur.setFrame(frame);
	        dialogueStage.showAndWait();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Fonction affichant la fenetre de modification et d'ajout d'un mobile
	 * @return boolean
	 */
	public boolean showAddGlisseur(Mobile mobile, Image frame) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/ch/chocastik/view/analyse/AddGlisseurFX.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			Stage dialogueStage = new Stage();
			dialogueStage.setTitle("Add Glisseur");
			dialogueStage.initModality(Modality.WINDOW_MODAL);
			dialogueStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogueStage.setScene(scene);
	        AddGlisseurController controleur = loader.getController();
	        controleur.setDialogueStage(dialogueStage);
	        controleur.setFrame(frame);
	        controleur.setMobile(mobile);
	        dialogueStage.showAndWait();
	        return controleur.isOkClicked();
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * Fonction affichant la fenetre de modification et d'ajout d'un Referentiel
	 * @return boolean
	 */
	public boolean showAddReferentiel(Image frame) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/ch/chocastik/view/analyse/AddReferentielleFX.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			Stage dialogueStage = new Stage();
			dialogueStage.setTitle("Add Referentielle");
			dialogueStage.initModality(Modality.WINDOW_MODAL);
			dialogueStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogueStage.setScene(scene);
	        AddReferentielleController controleur = loader.getController();
	        controleur.setDialogueStage(dialogueStage);
	        controleur.setFrame(frame);
	        controleur.setReferentiel(this.getReferentiel());
	        dialogueStage.showAndWait();
	        return controleur.isOkClicked();
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	// Get et Set 
	public Mesure getMesure() {
		return mesure;
	}
	public void setMesure(Mesure mesure) {
		this.mesure = mesure;
	}
	public Referentiel getReferentiel() {
		return referentiel;
	}
	public void setReferentiel(Referentiel referentiel) {
		this.referentiel = referentiel;
	}
    public Stage getPrimaryStage() {
        return primaryStage;
    }
	public ObservableList<Mobile> getMobileData() {
		return mobileData;
	}

	public Camera getCam() {
		return cam;
	}
	
	public ConcurrentLinkedQueue<IplImage>  getPileImage(){
		return this.pileImage;
	}
	
	/**
	 *  Fonction Main
	 * @param args
	 */
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		 Loader.load(opencv_objdetect.class);
		
		launch(args);
	}
}