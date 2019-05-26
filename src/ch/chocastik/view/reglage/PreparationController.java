package ch.chocastik.view.reglage;



import static org.bytedeco.javacpp.opencv_core.*;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.FrameGrabber.Exception;

import ch.chocastik.controller.MainApp;
import ch.chocastik.model.analyse.objet.Mobile;
import ch.chocastik.model.analyse.objet.Referentiel;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;


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
    @FXML
    private ColorPicker InitalColor;
	@FXML
    private TableView<Mobile> tableMobile;
	@FXML
	private  TableColumn<Mobile, String> colName;
	@FXML
	private  TableColumn<Mobile, String> colCouleur;
    @FXML
    private TextField nameMobile;
    
    // Attribut de l'objet
	private Image image;
	private Frame frame = new Frame();
	
	private boolean flageOrigine = false;
	private boolean flagEnd = false;
	private boolean flagColor = false;
	private MainApp mainApp;
	private int choiceCam;
	
	private Referentiel referentiel;
	final OpenCVFrameConverter.ToIplImage converterToIplImage = new OpenCVFrameConverter.ToIplImage();
	final Java2DFrameConverter converter = new Java2DFrameConverter();
	double ratioX = 1920/640; 
    double ratioY = 1080/360;
	private IplImage iplImage;
	
    @FXML
    private void initialize() {
		colName.setCellValueFactory(cellData -> cellData.getValue().nameExportProperty());
		colCouleur.setCellValueFactory(cellData -> cellData.getValue().nameExportProperty());
    }
	
    /**
     * action lancée lorsque l'utilisateur valide le formulaire
     * @param event
     */
    @FXML
    void handleValider(ActionEvent event) {
    	
    	// on met à jour le referentiel
    	referentiel.setPixelXOrigine((int) Double.parseDouble(origineX.getText()));
    	referentiel.setPixelYOrigine((int) Double.parseDouble(origineY.getText()));
    	referentiel.setMaxPixelX((int) Double.parseDouble(endX.getText()));
    	referentiel.setMaxPixelY((int) Double.parseDouble(endY.getText()));

    	// on affiche la suite
    	this.mainApp.showAnalyse(choiceCam);
    }

    
    // ============ Methode pour la gestion de la souris ================ //
    
    @FXML
    void handleMouseEntered(MouseEvent event) {
    	if(flagEnd || flageOrigine || flagColor) {
    		referentielFrame.setCursor(Cursor.CROSSHAIR);
    	}
    }
    @FXML
    void handleMouseExited(MouseEvent event) {
    	if(flagEnd || flageOrigine || flagColor) {
    		referentielFrame.setCursor(Cursor.DEFAULT);
    	}
    }
	
    // ============ Methode pour l'affichage de l'image ================ //
    public Frame flip(Frame frame) {
    	this.iplImage = converterToIplImage.convert(frame);
    	cvFlip(this.iplImage, this.iplImage, 1);
    	return converterToIplImage.convert(this.iplImage);
    }
    @FXML
    void captureFrame(ActionEvent event) throws Exception {
    	  Thread threadCapture = new Thread(new Runnable() { public void run() {
     		   try {
     	  			FrameGrabber grabber = FrameGrabber.createDefault(choiceCam);
     	  			  grabber.setImageHeight(1080);
     	  			  grabber.setImageWidth(1920);
     	  			  grabber.start(); // on le demarre      	          
     	  			  frame = flip(grabber.grab());  
     	  			
     	              image = SwingFXUtils.toFXImage(converter.convert(frame), null);      
     	              Platform.runLater(() -> {
     	              		    	 referentielFrame.setImage(image);
     	              });
     	                grabber.stop();
     	                grabber.release();
     	              		   
     	  		   } catch (Exception e) {
     	  			   e.printStackTrace();
     	  		   }
     		   
    	  }});
    	  threadCapture.start();
    }
    
    /**
     * action lancée lorsque l'utilisateur clisque sur l'image
     * @param event
     */
    @FXML
    void handleMouseClicked(MouseEvent event) {
    	double x = event.getX()*ratioX;
    	double y = (360-event.getY())*ratioY;
    	
    	if(flagEnd) {
    		endX.setText(Double.toString((int) x));
    		endY.setText(Double.toString((int) y));
    	}else if(flageOrigine) {
    		origineX.setText(Double.toString((int) x));
    		origineY.setText(Double.toString((int) y));
    	}else if(flagColor) {
 
    		PixelReader pixelReader = image.getPixelReader(); 
        	this.InitalColor.setValue(pixelReader.getColor((int) x, 1080 - (int) y)); 
        
    	}
    }
	// ============ Methode pour la gestion du referentiel ================ //
    
	/**
	 * Action lancée lorsque l'utilisateur clique sur le bouton pour rajouter un fin
	 * @param event
	 */
    @FXML
    void handleAddEnd(ActionEvent event) {
    	// si on était en train de travailler sur l'orgini, on arrète et on change pour travailler sur la fin
    	if(flageOrigine) {
    		flagEnd = true;
    		flagColor = false;
    		flageOrigine = false;
    	}else if(flagEnd){
    		flagEnd = true;
    	}
    }
    /**
     * Action lancée lorsque l'utilisateur clique sur le bouton pour rajouter une origine
     * @param event
     */
    @FXML
    void handleAddOrigine(ActionEvent event) {
    	// si on était en train de travailler sur la fin, on arrète et on change pour travailler sur l'origine
    	if(flagEnd) {
    		flagEnd = false;
    		flagColor = false;
    		flageOrigine = true;
    	}else {
    		flageOrigine = true;
    	}
    }
    
	// ============ Methode pour la gestion des mobiles ================ //
	/**
	 * Action lancé lorsque l'utilisateur clique sur le bouton delete Mobile
	 */
	@FXML
	private void handleDeleteMobile() {
		int selectIndex = tableMobile.getSelectionModel().getSelectedIndex();
		tableMobile.getItems().remove(selectIndex);
	}
	/**
	 * Action lancée lorsque l'utilisateur clique sur le bouton Add
	 */
	@FXML
	private void handleNewMobile() {
		Mobile mobile = new Mobile(InitalColor.getValue(), nameMobile.getText()); // mobile prédifinit 
    	mobile.setColor(this.InitalColor.getValue(), 10);
    	mobile.setDilateCount(3);
    	mobile.setErodeCount(3);
    	mobile.setMaxRad(18);
    	mobile.setMinRad(12);
    	mobile.setName(nameMobile.getText());
    	
		this.mainApp.getMobileData().add(mobile);
		InitalColor.setValue(Color.WHITE);
		nameMobile.setText("");
	}

	/**
	 *  Action lancée losrque l'utilisateur clique sur le bouton Edit
	 */
	@FXML
	private void handleEditMobile() {
		Mobile selectMobile = tableMobile.getSelectionModel().getSelectedItem();
		// si le mobile selctionnée existe on affiche la fenetre d'ajout avec les parametetre du mobile sinon
		// on affiche un message d'erreur
		if(selectMobile != null) {
			mainApp.showAddGlisseur(selectMobile, image); // si il n'y pas d'erreur on l'ajoute
		}else {
			//rien n'a été selectionnée
			Alert alert = new Alert(AlertType.WARNING);
		    alert.initOwner(mainApp.getPrimaryStage());
		    alert.setTitle("No Selection");
	        alert.setHeaderText("No Mobile Selected"); 
	        alert.setContentText("Please select a Mobile in the table.");
	        alert.showAndWait();
		}
		
	}
	
    /**
     * Action lancée lorsque l'utilisateur clique sur le bouton pour capturer la couleur
     * @param event
     */
    @FXML
    void handleChoiceColor(ActionEvent event) {
    	// si on était en train de travailler sur l'orginie ou la fin, on arrète et on change pour travailler sur la couleur
    	if(flageOrigine || flagEnd) {
    		flagEnd = false;
    		flageOrigine = false;
    		flagColor = true;
    		
    	}else {
    		flagColor = true;
    	}
    }
	
    // ============ Get et Set ================ //
    
	public void setReferentiel(Referentiel ref) {	
		this.referentiel = ref;
		this.origineX.setText(Integer.toString(ref.getPixelXOrigine()));
		this.origineY.setText(Integer.toString(ref.getPixelYOrigine()));
		this.endX.setText(Integer.toString(ref.getMaxPixelX()));
		this.endY.setText(Integer.toString(ref.getMaxPixelY()));
		
	}
	public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        tableMobile.setItems(mainApp.getMobileData());
	}
	
	public void dsetCameraChoice(int cam) {
		this.choiceCam = cam;
	}
	
}

