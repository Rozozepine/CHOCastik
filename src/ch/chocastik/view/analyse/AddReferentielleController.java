package ch.chocastik.view.analyse;

import java.awt.datatransfer.FlavorTable;

import ch.chocastik.controller.MainApp;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class AddReferentielleController {
	// attribut FXML
    @FXML
    private TextField origineX;
    @FXML
    private TextField origineY;
    @FXML
    private TextField endX;
    @FXML
    private TextField endY;
    @FXML
    private AnchorPane conteneur;
    @FXML
    private ImageView referentielFrame;
    
    // Attribut de l'objet
	private Stage dialogueStage;
	private Referentiel referentiel;
	private Image frame;
	private boolean flageOrigine = false;
	private boolean flagEnd = false;
	private boolean okClicked;
	private MainApp mainApp;
	// Fonction JavaFX
	/**
	 * Action lanc�e lorsque l'utilisateur clique sur le bouton pour rajouter un fin
	 * @param event
	 */
    @FXML
    void handleAddEnd(ActionEvent event) {
    	// si on �tait en train de travailler sur l'orgini, on arr�te et on change pour travailler sur la fin
    	if(flageOrigine) {
    		flagEnd = true;
    		flageOrigine = false;
    	}else {
    		flagEnd = true;
    	}
    }
    /**
     * Action lanc�e lorsque l'utilisateur clique sur le boutn pour rajouter une origine
     * @param event
     */
    @FXML
    void handleAddOrigine(ActionEvent event) {
    	// si on �tait en train de travailler sur la fin, on arr�te et on change pour travailler sur l'origine
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
    		conteneur.setCursor(Cursor.CROSSHAIR);
    	}
    }
    @FXML
    void handleMouseExited(MouseEvent event) {
    	if(flagEnd || flageOrigine) {
    		conteneur.setCursor(Cursor.DEFAULT);
    	}
    }
    /**
     * action lanc�e lorsque l'utilisateur clisque sur l'image
     * @param event
     */
    @FXML
    void handleMouseClicked(MouseEvent event) {
    	PixelReader pixelReader = frame.getPixelReader(); 
    	double x = event.getX();
    	double y = event.getY();
    	if(flagEnd) {
    		endX.setText(Double.toString(x));
    		endY.setText(Double.toString(conteneur.getHeight() - y));
    	}else if(flageOrigine) {
    		origineX.setText(Double.toString(x));
    		origineY.setText(Double.toString(conteneur.getHeight() - y));
    	}
    }
    /**
     * action lanc�e lorsque l'utilisateur valide le formulaire
     * @param event
     */
    @FXML
    void handleValider(ActionEvent event) {
    	referentiel.setPixelXOrigine((int) Double.parseDouble(origineX.getText()));
    	referentiel.setPixelYOrigine((int) Double.parseDouble(origineY.getText()));
    	referentiel.setMaxPixelX((int) Double.parseDouble(endX.getText()));
    	referentiel.setMaxPixelY((int) Double.parseDouble(endY.getText()));
    	referentiel.setFrameHeight((float) conteneur.getHeight());
    	this.okClicked = true;
    	dialogueStage.close();
    }
    //Get et Set de l'objet
    /**
     * recuperation du Stage Actuelle
     * @param dialogueStage
     */
	public void setDialogueStage(Stage dialogueStage) {
		this.dialogueStage = dialogueStage;
	}
	/**
	 * Affichage du Referentiel actuelle;
	 * @param ref
	 */
	public void setReferentiel(Referentiel ref) {
		this.referentiel = ref;
		this.origineX.setText(Integer.toString(ref.getPixelXOrigine()));
		this.origineY.setText(Integer.toString(ref.getPixelYOrigine()));
		this.endX.setText(Integer.toString(ref.getMaxPixelX()));
		this.endY.setText(Integer.toString(ref.getMaxPixelY()));
		
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
	private void drawCross(double x, double y, Color color) {
		Line line1 = new Line();
		line1.setStartX(x-20);
		line1.setStartY(y);
		line1.setEndX(x+20);
		line1.setEndY(y);
		Line line2 = new Line();
		line2.setStartX(x);
		line2.setStartY(y-20);
		line2.setEndX(x);
		line2.setEndY(y+20);
	    line1.setStroke(color);
	    line1.setStrokeWidth(2);
	    line2.setStroke(color);
	    line2.setStrokeWidth(2);
		conteneur.getChildren().addAll(line1, line2);
	}
	/**
	 * retourne si l'utilisateur � bien cliqu� sur valid�e
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
	// Methode de L'objet
	public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
	}
}
