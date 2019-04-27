package ch.chocastik.view.analyse;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import org.bytedeco.javacpp.*;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.BufferRing.ReleasableBuffer;
import org.bytedeco.javacv.FrameGrabber.Exception;

import ch.chocastik.controller.MainApp;

import ch.chocastik.model.cameras.Camera;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import ch.chocastik.model.analyse.Analyse;
import ch.chocastik.model.analyse.objet.*;
public class AnalyseController {
	// attribut FXML
    @FXML
    private AnchorPane conteneur;
    @FXML
    private ImageView RetourCam;
    @FXML
    private Button AjouterGlisseur;
    @FXML
    private Button Startvideo;
    @FXML
    private Button StartAnalyse;
    @FXML
	private MainApp mainApp;
    @FXML
    private MenuItem itemGlisseur;
    @FXML
    private MenuItem itemReferentiel;
    @FXML
    private MenuItem itemMesure;
    @FXML
    private ScatterChart<Number,Number> graphiquePoint;
    @FXML
    private NumberAxis AxeX;
    @FXML
    private NumberAxis AxeY;
    
    // Attribut de l'Objet
    private static volatile Thread playThread;
    private static volatile Thread analyseThread;
    private Image image;
    private Camera cam;
    private Mat frame = new Mat();
	private boolean videoIsActive = false;
	private boolean analyseIsActive = false;
	private Timer timer;
	private  Iterator it;
	private ConcurrentLinkedQueue<IplImage> pileFrame;
	private int choiceCam;
   // Methode JavaFX
    @FXML
    private void initialize() {
    	this.AxeX.setLabel("Axe X");
    	this.AxeY.setLabel("Axe Y");
    	RetourCam.fitWidthProperty().bind(conteneur.widthProperty());
    	RetourCam.fitHeightProperty().bind(conteneur.heightProperty());
    }
    @FXML
    public void startCamera() {
       if(this.videoIsActive) {
    	   playThread.interrupt();
    	   this.videoIsActive= false;
    	   this.Startvideo.setText("Start");
       }else {
    	   this.threadCam();
    	   this.videoIsActive = true;
    	   this.Startvideo.setText("Stop");
       	   playThread.start();
       }
    }
    @FXML 
    void startAnalyse() {
    	if(this.analyseIsActive) {
    		analyseThread.interrupt();
    		this.setMenuDisable(false);
    		this.analyseIsActive = false;
    		this.StartAnalyse.setText("Start Analyse");
    		
    	}else {
    		if(!mainApp.getMobileData().isEmpty() &&  mainApp.getReferentiel() != null &&  mainApp.getMesure() != null) {
    			analyseThread = new Thread(new Analyse(mainApp, this.graphiquePoint));
    			analyseThread.start();
    			this.setMenuDisable(true);
    			this.analyseIsActive = true;
    			this.StartAnalyse.setText("Stop Analyse");
    		}else {
    			Alert alert = new Alert(AlertType.WARNING);
    		    alert.initOwner(mainApp.getPrimaryStage());
    		    alert.setTitle("No Selection");
    	        alert.setHeaderText("Need all of Data");
    	        alert.setContentText("Please add Date in the table.");
    	        alert.showAndWait();
    		}
    	}
    }
	@FXML
	public void handleGlisseur() {
		mainApp.showEditGlisseur(image);
	}
	@FXML
	public void handleReferentiel() {
		mainApp.showAddReferentiel(image);
	}
    
    //Get et Set
	/**
	 * Permet de mettre en place la mainApp
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
	        this.mainApp = mainApp;
	        this.cam = this.mainApp.getCam();
	        this.pileFrame = mainApp.getPileImage();
	}
	public void dsetCameraChoice(int nb) {
		this.choiceCam = nb;
	}
	/**
	 * bloque ou debloque tout les menu 
	 */
	private void setMenuDisable(boolean value) {
		this.itemMesure.setDisable(value);
		this.itemReferentiel.setDisable(value);
		this.itemGlisseur.setDisable(value);
	}
	// Methode de l'objet
	/**
	 * Transmet à thread s'occupant des calculs les frame
	 * @param frame
	 */
	public void traitement(IplImage frame) {
		if(analyseIsActive) {
			pileFrame.add(frame.clone());		
		}	
	}
	/**
	 * Crée le Thread s'occupant de la camera 
	 */
	public void threadCam() {
  	   playThread = new Thread(new Runnable() { public void run() {
  		   try {
  			   
  			  final FrameGrabber grabber = new OpenCVFrameGrabber(choiceCam); // on crée le grabber 
  			  final Java2DFrameConverter converter = new Java2DFrameConverter();
  			  final OpenCVFrameConverter.ToIplImage converterToIplImage = new OpenCVFrameConverter.ToIplImage();
  			  Frame frame = new Frame();
  			  ExecutorService executor = Executors.newSingleThreadExecutor();
  			  grabber.start(); // on le démarre 
  			  conteneur.setMinWidth(grabber.getImageWidth());
  			  conteneur.setMinHeight(grabber.getImageHeight());

              while(!Thread.interrupted()) {
            	 frame = grabber.grab();  
              	 if(frame == null) {
               		 break;
              	  }else {
              		 IplImage grabbedImage = converterToIplImage.convert(frame);
              		 traitement(grabbedImage);
              		 image = SwingFXUtils.toFXImage(converter.convert(frame), null);
              		 Platform.runLater(() -> {
              		    	 RetourCam.setImage(image);
              		 });
              	   }
              	   
               }
               executor.shutdownNow();
               executor.awaitTermination(10, TimeUnit.SECONDS);
               grabber.stop();
               grabber.release();				
  		   } catch (Exception e) {
  			   e.printStackTrace();
  		   } catch (InterruptedException e) {
				e.printStackTrace();
			}
  	   }});

  }
	
}

