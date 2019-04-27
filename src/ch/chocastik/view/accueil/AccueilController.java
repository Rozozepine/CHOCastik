package ch.chocastik.view.accueil;




import java.beans.PropertyVetoException;
import java.net.URL;
import java.util.ResourceBundle;

import org.bytedeco.javacpp.videoInputLib.videoInput;
import org.bytedeco.javacv.CameraDevice;
import org.bytedeco.javacv.CameraSettings;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.ProjectiveDevice.Exception;

import ch.chocastik.controller.MainApp;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.input.MouseEvent;

public class AccueilController {
	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;
	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;
	@FXML // fx:id="SelectionCam"
	private SplitMenuButton SelectionCam; // Value injected by FXMLLoader
	@FXML // fx:id="BStartAcc"
	private Button BStartAcc; // Value injected by FXMLLoader
	
	private MainApp mainApp;
	private int choice;
	static CameraSettings cameraSettings = new CameraSettings();
	static CameraDevice cameraDevices = null;

	
	public void AccueilController() {}
	@FXML
	public void OpenCalib(MouseEvent event) throws Exception, PropertyVetoException {
		CameraDevice.Settings[] cs= cameraSettings.toArray();
		cs[this.choice].setFrameGrabber(FrameGrabber.getDefault());
   	 	cameraDevices = new CameraDevice(cs[this.choice]);    	 
		this.mainApp.showCalibration(cameraDevices);
	}
		
	public void setMainApp(MainApp mainApp) {
	   this.mainApp = mainApp;
	}
    @FXML
    private void initialize() throws PropertyVetoException{
    	int n = videoInput.listDevices();
    	cameraSettings.setQuantity(n);  
		for (int i = 0; i < n; i++) {
			MenuItem menuItem = new MenuItem("Device "+i+" : " +videoInput.getDeviceName(i).getString());
			menuItem.setId(Integer.toString(i));
			menuItem.setOnAction(createChoiceHandler(i));
			SelectionCam.getItems().add(menuItem);
		}
    }
    private EventHandler<ActionEvent> createChoiceHandler(int index) {
        return event -> setChoice(index);
    }
    private void setChoice(int index) {
    	this.choice = index;
    	
    }

}

