package ch.chocastik.view.analyse;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;

import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.*;


import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.text.GapContent;

import org.opencv.core.Mat;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_imgproc.CvFont;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.BufferRing.ReleasableBuffer;
import org.bytedeco.javacv.FrameGrabber.Exception;

import ch.chocastik.controller.MainApp;


import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import ch.chocastik.model.analyse.Analyse;
import ch.chocastik.model.analyse.objet.*;
import ch.chocastik.model.analyse.objet.Point;
import static org.bytedeco.javacpp.opencv_imgproc.cvPutText;
import static org.bytedeco.javacpp.opencv_imgproc.cvInitFont;
import static org.bytedeco.javacpp.opencv_imgproc.CV_FONT_HERSHEY_PLAIN;

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
    @FXML 
    private TabPane tabPane;
    
    // Attribut de l'Objet
    private static volatile Thread playThread;
    private static volatile Thread analyseThread;
    private Image image;
    private Frame frame = new Frame();
	private boolean videoIsActive = false;
	private boolean analyseIsActive = false;
	private Timer timer;
	private  Iterator it;
	private ConcurrentLinkedQueue<Frame> pileFrame;
	private CameraDevice choiceCam;
	final Java2DFrameConverter converter = new Java2DFrameConverter();
	final OpenCVFrameConverter.ToIplImage converterToIplImage = new OpenCVFrameConverter.ToIplImage();
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
       if(mainApp.getThreadCaptureFlag()) {
    	   mainApp.setThreadCaptureFlag(false);
    	   this.Startvideo.setText("Start");
       }else {
    	   this.threadCam();
    	   mainApp.setThreadCaptureFlag(true);
    	   playThread.start();
    	   this.Startvideo.setText("Stop");
 
       }
    }
    @FXML 
    void startAnalyse() {
    	if(mainApp.getThreadAnalyseFlag()) {
    		mainApp.setThreadAnalyseFlag(false);
    		this.setMenuDisable(false);
    		this.StartAnalyse.setText("Start Analyse");
    		
    	}else {
    		if(!mainApp.getMobileData().isEmpty() &&  mainApp.getReferentiel() != null &&  mainApp.getMesure() != null) {
    			deleteAllDataGraph();
    			creatDataGraph();
    			analyseThread = new Thread(new Analyse(mainApp));
    			analyseThread.start();
    			this.setMenuDisable(true);
    			mainApp.setThreadAnalyseFlag(true);
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

	@FXML
	public void handleExportAll() {
		if(mainApp.getPileImage().isEmpty()) {
			for(Tracker tracker: mainApp.getListTraker()) 
				tracker.getTrajectoire().exportTrajectoire(mainApp.getMesure(), mainApp.getListTraker());
		}
		
	}
	
	public void showFrame(Point point) {
		if(!analyseIsActive) {
			final CvFont font = new CvFont(); 
		    if(mainApp.getThreadCaptureFlag()) {
		    	   mainApp.setThreadCaptureFlag(false);
		    	   this.videoIsActive= false;
		    	   this.Startvideo.setText("Start");
		    }
        	IplImage dessin = cvCloneImage(converterToIplImage.convert(frame));
        	cvInitFont(font, CV_FONT_HERSHEY_PLAIN,0.5, 0.5);
        	mainApp.getReferentiel().transformToNoneRelatif(point);
        	mainApp.getReferentiel().transformToNoneNaturalReferentiel(point);
        	cvPutText(dessin, "Mobile", cvPoint((int) point.getX(),(int) point.getY()), font, CvScalar.GREEN); 
        	mainApp.getReferentiel().transformToNaturalReferentiel(point);
        	mainApp.getReferentiel().transformToRelatif(point);
        	Frame frame = converterToIplImage.convert(dessin);
    		Image image = SwingFXUtils.toFXImage(converter.convert(frame), null);
        	RetourCam.setImage(image);
     		
		}
	}
	private void deleteAllDataGraph() {

		tabPane.getTabs().removeAll(tabPane.getTabs());
		graphiquePoint.getData().removeAll(graphiquePoint.getData());			
	}

	private void creatDataGraph() {
		for(Mobile mob: mainApp.getMobileData()) {	
			XYChart.Series<Number, Number> series = new XYChart.Series<>();
			series.setName(mob.getName()); // TODO Regler probl�me ajout de donn�es
			graphiquePoint.getData().add(series);
			Tab tab = new Tab();
			tab.setText(mob.getName());
		
			Tracker tracker = new Tracker(mob, mainApp.getReferentiel(), series, mainApp.getMesure());
			TableView<Point> table = new TableView<Point>();
			TableColumn<Point,Float> xCol = new TableColumn<Point, Float>("X");
			TableColumn<Point,Float> yCol = new TableColumn<Point,Float>("Y");
			TableColumn<Point,Long> timeCol = new TableColumn<Point,Long>("timestamp");
			table.setItems(tracker.getTrajectoire().getListOfPoint());
			xCol.setCellValueFactory(cellData -> cellData.getValue().xProperty().asObject());
			yCol.setCellValueFactory(cellData -> cellData.getValue().yProperty().asObject());
			timeCol.setCellValueFactory(cellData -> cellData.getValue().timeProperty().asObject());
			table.getColumns().addAll(timeCol, xCol, yCol);
			table.getSelectionModel().selectedItemProperty().addListener(
		            (observable, oldValue, newValue) -> showFrame(newValue));
			
			tab.setContent(table);
			tabPane.getTabs().add(tab);	
			mainApp.getListTraker().add(tracker);
			
		}
	}
    //Get et Set
	/**
	 * Permet de mettre en place la mainApp
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
	        this.mainApp = mainApp;

	        this.pileFrame = mainApp.getPileImage();
	}
	public void dsetCameraChoice(CameraDevice cam) {
		this.choiceCam = cam;
	}
	/**
	 * bloque ou debloque tout les menu
	 */
	private void setMenuDisable(boolean value) {
		this.itemReferentiel.setDisable(value);
		this.itemGlisseur.setDisable(value);
	}
	// Methode de l'objet
	/**
	 * Transmet � thread s'occupant des calculs les frame
	 * @param frame
	 */
	public void traitement(Frame frame) {
		if(mainApp.getThreadAnalyseFlag()) {
			pileFrame.add(frame.clone());		
		}	
	}
	/**
	 * Cr�e le Thread s'occupant de la camera 
	 */
	public void threadCam() {
  	   playThread = new Thread(new Runnable() { public void run() {
  		   try {
  			  CameraDevice.Settings setting = (CameraDevice.Settings) choiceCam.getSettings();
  			
  			  final  OpenCVFrameGrabber grabber =   OpenCVFrameGrabber.createDefault(setting.getDeviceNumber()); // on cr�e le grabber 
  			  grabber.setFrameRate(60);
  		
  			  ExecutorService executor = Executors.newSingleThreadExecutor();
  			  grabber.start(); // on le d�marre 
  			  conteneur.setMinWidth(grabber.getImageWidth()); 
  			  conteneur.setMinHeight(grabber.getImageHeight());
  			  long startTime = System.currentTimeMillis();
  			  long videoTS;
              while(mainApp.getThreadCaptureFlag()) {
            	 frame = grabber.grab();  
            	 videoTS = System.currentTimeMillis() - startTime;
              	 if(frame == null) {
               		 break;
              	  }else {
              		 IplImage grabbedImage = converterToIplImage.convert(frame);
              		 frame = converterToIplImage.convert(grabbedImage);
              		 frame.timestamp = System.currentTimeMillis();              		
              		 traitement(frame);
              		 image = SwingFXUtils.toFXImage(converter.convert(frame), null);
              		 Platform.runLater(() -> {
              		    	 RetourCam.setImage(image);
              		 });
              	   }
              	 Thread.sleep(15);	   
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

