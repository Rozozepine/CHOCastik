package ch.chocastik.view.analyse;

import ch.chocastik.controller.MainApp;
import ch.chocastik.model.analyse.objet.Mesure;
import ch.chocastik.model.analyse.objet.Referentiel;
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

public class AddMesureController {

    @FXML
    private TextField distanceRelle;

    @FXML
    private TextField xStart;

    @FXML
    private TextField yStart;

    @FXML
    private TextField xStop;

    @FXML
    private TextField yStop;

    @FXML
    private AnchorPane conteneur;
    
    @FXML
    private ImageView referentielFrame;

	private MainApp mainApp;

	private Image frame;

	private Stage dialogueStage;

	private boolean okClicked;

	private boolean flagEnd;

	private boolean flageOrigine;

	private Mesure mesure;


    @FXML
    void startHandler(ActionEvent event) {
    	if(flagEnd) {
    		flagEnd = false;
    		flageOrigine = true;
    	}else {
    		flageOrigine = true;
    	}
    }

    @FXML
    void stopHandler(ActionEvent event) {
    	if(flageOrigine) {
    		flagEnd = true;
    		flageOrigine = false;
    	}else {
    		flagEnd = true;
    	}
    }

    @FXML
    void validateHandler(ActionEvent event) {
    	mesure.setPixelXOrigine((int) Double.parseDouble(xStart.getText()));
    	mesure.setPixelYOrigine((int) Double.parseDouble(yStart.getText()));
    	mesure.setMaxPixelX((int) Double.parseDouble(xStop.getText()));
    	mesure.setMaxPixelY((int) Double.parseDouble(yStop.getText()));
    	mesure.setDistanceRelle((float) Float.parseFloat(distanceRelle.getText()));
    	mesure.calculatePixel();
    	this.okClicked = true;
    	dialogueStage.close();
    }
	public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
	}
	/**
	 * recuperation de la frame de travail
	 * @param frame
	 */
	public void setFrame(Image frame) {
		conteneur.setMinWidth(frame.getWidth()); 
		conteneur.setMinHeight(frame.getHeight());		
		this.frame = frame;
		referentielFrame.setImage(frame);
	}
    /**
     * recuperation du Stage Actuelle
     * @param dialogueStage
     */
	public void setDialogueStage(Stage dialogueStage) {
		this.dialogueStage = dialogueStage;
	}
	/**
	 * retourne si l'utilisateur à bien cliqué sur validée
	 * @return
	 */
	public boolean isOkClicked() {
		return okClicked;
	}
	@FXML
	private void initialize() {
		referentielFrame.fitWidthProperty().bind(conteneur.widthProperty());
    	referentielFrame.fitHeightProperty().bind(conteneur.heightProperty());
	}
    @FXML
    void handleMouseEntered(MouseEvent event) {
    	if(flagEnd || flageOrigine) {
    		conteneur.setCursor(Cursor.CROSSHAIR);
    	}
    }
    @FXML
    void handleMouseExited(MouseEvent event) {
    	if(flagEnd || flageOrigine) {
    		conteneur.setCursor(Cursor.DEFAULT);
    	}
    }
    @FXML
    void handleMouseClicked(MouseEvent event) {
    	PixelReader pixelReader = frame.getPixelReader(); 
    	double x = event.getX();
    	double y = event.getY();
    	if(flagEnd) {
    		xStop.setText(Double.toString(x));
    		yStop.setText(Double.toString(conteneur.getHeight() - y));
    	}else if(flageOrigine) {
    		xStart.setText(Double.toString(x));
    		yStart.setText(Double.toString(conteneur.getHeight() - y));
    	}
    }
	public void setMesure(Mesure mesure) {
		this.mesure = mesure;
		this.xStart.setText(Integer.toString(mesure.getPixelXOrigine()));
		this.yStart.setText(Integer.toString(mesure.getPixelYOrigine()));
		this.xStop.setText(Integer.toString(mesure.getMaxPixelX()));
		this.yStop.setText(Integer.toString(mesure.getMaxPixelY()));
		this.distanceRelle.setText(Float.toString(mesure.getDistanceRelle()));	
	}

}
