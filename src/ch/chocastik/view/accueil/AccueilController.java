package ch.chocastik.view.accueil;



import java.net.URL;
import java.util.ResourceBundle;

import org.bytedeco.javacpp.videoInputLib.videoInput;

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
	public void AccueilController() {}
	@FXML
	public void OpenCalib(MouseEvent event) {
		this.mainApp.showCalibration(choice);
	}
		
	public void setMainApp(MainApp mainApp) {
	   this.mainApp = mainApp;
	}
    @FXML
    private void initialize() {
    	int n = videoInput.listDevices();
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
    	System.out.println(index);
    }

}

