package ch.chocastik.view.calibration;
import ch.chocastik.controller.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class CalibrationController {

    @FXML
    private ImageView RetourCalib;
    @FXML
    private Button BGo;
    @FXML
    private Button ReglageCalib;
    @FXML
    private Button BhelpCalib;
    private int choiceCam;
	private MainApp mainApp;
	public  CalibrationController() {
		
	}
    @FXML
    void GoStartAnalyse(MouseEvent event) {
    	this.mainApp.showAnalyse(this.choiceCam);
    }
  
    @FXML
    void TakePicture(MouseEvent event) {

    }
	public void setMainApp(MainApp mainApp) {
		   this.mainApp = mainApp;
	}
	public void dsetCameraChoice(int nb) {
		this.choiceCam = nb;
	}
}
		





