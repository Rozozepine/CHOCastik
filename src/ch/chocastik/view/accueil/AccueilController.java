package ch.chocastik.view.accueil;




import java.beans.PropertyVetoException;
import java.net.URL;
import java.util.ResourceBundle;

import org.bytedeco.javacpp.videoInputLib.videoInput;
import org.bytedeco.javacv.CameraDevice;
import org.bytedeco.javacv.CameraSettings;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.VideoInputFrameGrabber;
import org.bytedeco.javacv.ProjectiveDevice.Exception;

import ch.chocastik.controller.MainApp;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;

public class AccueilController {
	// Attribut FXML
	@FXML 
	private SplitMenuButton SelectionCam;
	@FXML
	private Button BStartAcc; 
	
	// Attribut de l'objet
	private MainApp mainApp;
	private int choice = -1;

	
	 // ============ Methode FXML ================ //
    
	
	/**
	 * Lors du clic sur le bouton Réglage on lance la fenetre de Préparation
	 */
	@FXML
	public void OpenReglage() {
		// si l'utilisateur n'a choisit aucune source vidéo on affiche un message d'erreur
		if(choice == -1) {
			Alert alert = new Alert(AlertType.WARNING);
		    alert.initOwner(mainApp.getPrimaryStage());
		    alert.setTitle("Pas de source vidéo");
	        alert.setHeaderText("Pas de source vidéo selctionnées"); 
	        alert.setContentText("Veuillez choisir une source vidéo correct");
	        alert.showAndWait();
	     // sinon on passe à la suite
		}else {
			this.mainApp.showPreparation(choice);
		}
		
		
	}
    @FXML
    private void initialize() throws org.bytedeco.javacv.FrameGrabber.Exception{
    	int n = VideoInputFrameGrabber.getDeviceDescriptions().length;
    	SelectionCam.setText("Aucune caméras");
		for (int i = 0; i < n; i++) {
			MenuItem menuItem = new MenuItem("Device "+i+" : " +VideoInputFrameGrabber.getDeviceDescriptions()[i]);
			menuItem.setOnAction(createChoiceHandler(i));
			SelectionCam.getItems().add(menuItem);
		}
    }
    
    
    // ============ Methode pour la création et la gestion du menu ================ //
    
    
    /**
     * Pour chaque menuItem on lui lie un handler qui permet de set le choix de l'utilisateur
     */
    private EventHandler<ActionEvent> createChoiceHandler(int index) {
        return event -> {
			try {
				setChoice(index);
			} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
				e.printStackTrace();
			}
		};
    }
    /**
     * 
     */
    private void setChoice(int index) throws org.bytedeco.javacv.FrameGrabber.Exception {
    	//On affiche pour chaque device détectés le nom dans le menu
    	SelectionCam.setText("Device "+index+" : " + VideoInputFrameGrabber.getDeviceDescriptions()[index]);
    	this.choice = index;
    	
    }
    
    // ============ Get et Set ================ //
    
    
	public void setMainApp(MainApp mainApp) {
		   this.mainApp = mainApp;   
	}

}

