package ch.chocastik.view.analyse;

import ch.chocastik.model.analyse.objet.Referentiel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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
	
	// Fonction JavaFX
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
    /**
     * action lancée lorsque l'utilisateur clisque sur l'image
     * @param event
     */
    @FXML
    void handleMouseClicked(MouseEvent event) {

    }
    /**
     * action lancée lorsque l'utilisateur valide le formulaire
     * @param event
     */
    @FXML
    void handleValider(ActionEvent event) {

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
		referentielFrame.setFitWidth(frame.getWidth());
		referentielFrame.setFitHeight(frame.getHeight());
		this.frame = frame;
		referentielFrame.setImage(frame);
		
	}
	/**
	 * retourne si l'utilisateur à bien cliqué sur validée
	 * @return
	 */
	public boolean isOkClicked() {
		return okClicked;
	}
	// Methode de L'objet
	
}
