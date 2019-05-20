package ch.chocastik.view.analyse;



import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.naming.InitialContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ch.chocastik.model.analyse.objet.*;

public class AddGlisseurController {
   
    @FXML
    private TextField nameMobile;
    @FXML
    private ColorPicker InitalColor;
    @FXML
    private TextField TFNBreDilatation;
    @FXML
    private TextField TFNbreErosion;
    @FXML
    private TextField tolHue;
    @FXML
    private TextField radiusMin;
    @FXML
    private TextField radiusMax;
    @FXML
    private ImageView ColorFrame;

    private Stage dialogueStage;
    private boolean okClicked = false;
	private Mobile mobile;
	private Color color = new Color(0,0,0.4, 1.0);
	private Image frame;

    public AddGlisseurController() {
    }
    @FXML
    public void AddGlisseur() {
    	mobile.setColor(this.InitalColor.getValue(), Integer.parseInt(tolHue.getText()));
    	mobile.setDilateCount( Integer.parseInt(TFNBreDilatation.getText()));
    	mobile.setErodeCount( Integer.parseInt(TFNbreErosion.getText()));
    	mobile.setMaxRad(Integer.parseInt(radiusMax.getText()));
    	mobile.setMinRad(Integer.parseInt(radiusMin.getText()));
    	mobile.setName(nameMobile.getText());
    	this.okClicked = true;
        dialogueStage.close();
    }
	public void setDialogueStage(Stage dialogueStage) {
		this.dialogueStage = dialogueStage;
	}
	public void setMobile(Mobile mobile) {
		this.mobile = mobile;
		this.nameMobile.setText(mobile.getName());
		this.InitalColor.setValue(mobile.getColor());
		this.TFNBreDilatation.setText(Integer.toString(mobile.getDilateCount()));
		this.TFNbreErosion.setText(Integer.toString(mobile.getErodeCount()));
		this.radiusMax.setText(Integer.toString(mobile.getMaxRad()));
		this.radiusMin.setText(Integer.toString(mobile.getMinRad()));
		this.tolHue.setText(Integer.toString(mobile.getTolHue()));
		
	}
	public void setFrame(Image frame) {
		ColorFrame.setFitWidth(frame.getWidth());
		ColorFrame.setFitHeight(frame.getHeight());
		this.frame = frame;
		ColorFrame.setImage(frame);
		
	}
	public boolean isOkClicked() {
		return okClicked;
	}
    @FXML
    void choiceColor(MouseEvent event) {
    	PixelReader pixelReader = frame.getPixelReader(); 
    	this.InitalColor.setValue(pixelReader.getColor((int)event.getX(), (int) event.getY())); 
    }
}
