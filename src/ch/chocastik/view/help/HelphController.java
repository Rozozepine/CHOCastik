package ch.chocastik.view.help;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class HelphController {

    @FXML
    private ImageView idImage;

    @FXML
    private Label idIndex;

	private Stage dialogueStage;

	private int stop;
	private int start;
	private int indexEnCour;
	private int taille;
	private int indexAfficher;

    @FXML
    void handleRetour(ActionEvent event) {
        dialogueStage.close();
    }

    @FXML
    void handlerPrecedent(ActionEvent event) {
    	this.indexEnCour--;
    	if(this.indexEnCour < this.start) {
    		this.indexEnCour = this.stop;
    	}
    	this.indexAfficher = indexEnCour-start+1;
      	idIndex.setText(this.indexAfficher+"/"+this.taille);
    	afficherImage();
    }
    @FXML
    void afficherImage() {
    	Image imgTnp = new Image("/ch/chocastik/view/Images/Help/Help"+this.indexEnCour+".jpeg");
    	idImage.setImage(imgTnp);
    }
    @FXML
    void handlerSuivant(ActionEvent event) {
    	this.indexEnCour++;
    	if(this.indexEnCour > this.stop) {
    		this.indexEnCour = this.start;
    	}
    	this.indexAfficher = indexEnCour-start+1;
    	idIndex.setText(this.indexAfficher+"/"+this.taille);
    	afficherImage();
    }
    public void setIndex(int start, int stop) {
    	this.start = start;
    	this.indexEnCour = start;
    	this.stop = stop;
    	this.taille = stop-start+1;
    	this.indexAfficher = indexEnCour-this.start+1;
    	idIndex.setText(this.indexAfficher+"/"+this.taille);
    	afficherImage();
    	
    }
	public void setDialogueStage(Stage dialogueStage) {
		this.dialogueStage = dialogueStage;
	}
}
