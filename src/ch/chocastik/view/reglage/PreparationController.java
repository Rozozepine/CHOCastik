package ch.chocastik.view.reglage;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.CameraDevice;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.FrameGrabber.Exception;

import ch.chocastik.controller.MainApp;
import ch.chocastik.model.analyse.objet.Referentiel;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class PreparationController {
    @FXML
    private TextField origineX;
    @FXML
    private TextField origineY;
    @FXML
    private TextField endX;
    @FXML
    private TextField endY;
    @FXML
    private ImageView referentielFrame;
    
    // Attribut de l'objet
	private Stage dialogueStage;
	private Referentiel referentiel;
	private Image image;
	private Frame frame = new Frame();
	private boolean flageOrigine = false;
	private boolean flagEnd = false;
	private boolean okClicked;
	private MainApp mainApp;
	private CameraDevice choiceCam;
	final Java2DFrameConverter converter = new Java2DFrameConverter();

	
    /**
     * action lancée lorsque l'utilisateur valide le formulaire
     * @param event
     */
    @FXML
    void handleValider(ActionEvent event) {
    	referentiel.setPixelXOrigine((int) Double.parseDouble(origineX.getText()));
    	referentiel.setPixelYOrigine((int) Double.parseDouble(origineY.getText()));
    	referentiel.setMaxPixelX((int) Double.parseDouble(endX.getText()));
    	referentiel.setMaxPixelY((int) Double.parseDouble(endY.getText()));
    	referentiel.setFrameHeight((float) image.getHeight());
    	this.okClicked = true;
    	dialogueStage.close();
    }
	/**
	 * Action lancée lorsque l'utilisateur clique sur le bouton pour rajouter un fin
	 * @param event
	 */
    @FXML
    void handleAddEnd(ActionEvent event) {
    	// si on était en train de travailler sur l'orgini, on arrète et on change pour travailler sur la fin
    	if(flageOrigine) {
    		flagEnd = true;
    		flageOrigine = false;
    	}else if(flagEnd){
    		flagEnd = true;
    	}
    }
    @FXML
    void handleChoiceColor(ActionEvent event) {
    	// si on était en train de travailler sur l'orgini, on arrète et on change pour travailler sur la fin
    	if(flageOrigine) {
    		flagEnd = true;
    		flageOrigine = false;
    	}else {
    		flagEnd = true;
    	}
    }
    /**
     * Action lancée lorsque l'utilisateur clique sur le boutn pour rajouter une origine
     * @param event
     */
    @FXML
    void handleAddOrigine(ActionEvent event) {
    	// si on était en train de travailler sur la fin, on arrète et on change pour travailler sur l'origine
    	if(flagEnd) {
    		flagEnd = false;
    		flageOrigine = true;
    	}else {
    		flageOrigine = true;
    	}
    }
    @FXML
    void handleMouseEntered(MouseEvent event) {
    	if(flagEnd || flageOrigine) {
    		referentielFrame.setCursor(Cursor.CROSSHAIR);
    	}
    }
    @FXML
    void handleMouseExited(MouseEvent event) {
    	if(flagEnd || flageOrigine) {
    		referentielFrame.setCursor(Cursor.DEFAULT);
    	}
    }
    /**
     * action lancée lorsque l'utilisateur clisque sur l'image
     * @param event
     */
    @FXML
    void handleMouseClicked(MouseEvent event) {
    	PixelReader pixelReader = image.getPixelReader(); 
    	double x = event.getX();
    	double y = event.getY();
    	if(flagEnd) {
    		endX.setText(Double.toString(x));
    		endY.setText(Double.toString(image.getHeight() - y));
    	}else if(flageOrigine) {
    		origineX.setText(Double.toString(x));
    		origineY.setText(Double.toString(image.getHeight() - y));
    	}
    }
	public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
	}
	public void dsetCameraChoice(CameraDevice cam) {
		this.choiceCam = cam;
	}
	
    @FXML
    void captureFrame(ActionEvent event) throws Exception {
		  CameraDevice.Settings setting = (CameraDevice.Settings) choiceCam.getSettings();
		  final FrameGrabber grabber =  FrameGrabber.createDefault(setting.getDeviceNumber()); // on crï¿½e le grabber 
		  final int captureWidth = 1920;
		  final int captureHeight = 1080;
		  frame = grabber.grab();  
		  if(!(frame == null)){
       		 image = SwingFXUtils.toFXImage(converter.convert(frame), null);
       		 Platform.runLater(() -> {
       		    	 referentielFrame.setImage(image);
       		 });	
    }
    }
}

