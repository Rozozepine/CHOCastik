package ch.chocastik.view.analyse;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;

import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.*;

import java.io.File;
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
import ch.chocastik.model.analyse.objet.Point;

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
import javafx.scene.control.Label;
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
import javafx.stage.DirectoryChooser;
import ch.chocastik.model.analyse.Analyse;
import ch.chocastik.model.analyse.objet.*;

import static org.bytedeco.javacpp.opencv_imgproc.cvPutText;
import static org.bytedeco.javacpp.opencv_imgproc.cvInitFont;
import static org.bytedeco.javacpp.opencv_imgproc.CV_FONT_HERSHEY_PLAIN;

public class AnalyseController {
	// attribut FXML
 /*   @FXML
    private AnchorPane conteneur; */
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
	@FXML
    private TableView<Mobile> tableMobile;
	@FXML
	private  TableColumn<Mobile, String> colName;
	@FXML
	private  TableColumn<Mobile, String> colCouleur;


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
	private int choiceCam;
	final Java2DFrameConverter converter = new Java2DFrameConverter();
	final OpenCVFrameConverter.ToIplImage converterToIplImage = new OpenCVFrameConverter.ToIplImage();
	private VideoInputFrameGrabber grabber;
	private long startTime;
	
	
	public void messageErreur(String titre, String header, String contenu) {
		Alert alert = new Alert(AlertType.WARNING);
	    alert.initOwner(mainApp.getPrimaryStage());
	    alert.setTitle(titre);
        alert.setHeaderText(header);
        alert.setContentText(contenu);
        alert.showAndWait();
	}
	// Methode JavaFX
    @FXML
    private void initialize() {
    	this.AxeX.setLabel("Axe X");
    	this.AxeY.setLabel("Axe Y");
		colName.setCellValueFactory(cellData -> cellData.getValue().nameExportProperty());
		colCouleur.setCellValueFactory(cellData -> cellData.getValue().nameExportProperty());
    }
    @FXML
    public void startCamera() {
       if(mainApp.getThreadCaptureFlag()) {
    	   mainApp.setThreadCaptureFlag(false);
    	   this.Startvideo.setText("Start Capture");
       }else {
    	   this.threadCam();
    	   mainApp.setThreadCaptureFlag(true);
    	   playThread.start();
    	   this.Startvideo.setText("Stop Capture");
       }
    }
    @FXML 
    void startAnalyse() {
    	if(mainApp.getThreadAnalyseFlag()) {
    		mainApp.setThreadAnalyseFlag(false);
    		this.StartAnalyse.setText("Start Analyse");	
    	}else {
    		
    		if(mainApp.getAnalyseEndFlag()) {
    			if(!mainApp.getMobileData().isEmpty()) {
        			deleteAllDataGraph();
        			creatDataGraph();
        			this.startTime = System.currentTimeMillis();
        			analyseThread = new Thread(new Analyse(mainApp));
        			mainApp.setThreadAnalyseFlag(true);
        			analyseThread.start();
        			this.StartAnalyse.setText("Stop Analyse");
        		}else {
        			messageErreur("No Selection", "No Mobile Selected", "Please select a Mobile in the table.");
        		}
    		}else {
    			messageErreur("Analyse en cour", "Analyse en cour", "Une analyse est deja en cour");
    		}
    		
    	}
    }


	@FXML
	public void handleExportAll() {
		if(mainApp.getAnalyseEndFlag()) {
	        DirectoryChooser directoryChooser = new DirectoryChooser();
	        directoryChooser.setInitialDirectory(new File("C:\\Users\\"));
	        File selectedDirectory = directoryChooser.showDialog(mainApp.getPrimaryStage());
	        System.out.println(selectedDirectory.getAbsolutePath());
			for(Tracker tracker: mainApp.getListTraker())
				tracker.getTrajectoire().exportTrajectoire(mainApp.getMesure(), mainApp.getListTraker(), selectedDirectory);
		}else {
			messageErreur("Analyse en cour", "Analyse en cour", "Une analyse est deja en cour");
		}
		
	}
	/**
	 * Action lanc� lorsque l'utilisateur clique sur le bouton delete Mobile
	 */
	@FXML
	private void handleDeleteMobile() {
		Mobile selectMobile = tableMobile.getSelectionModel().getSelectedItem();
		if(selectMobile != null)
			tableMobile.getItems().remove(selectMobile);
		else
			messageErreur("No Selection", "No Mobile Selected", "Please select a Mobile in the table.");
	}
	/**
	 * Action lanc�e lorsque l'utilisateur clique sur le bouton Add
	 */
	@FXML
	private void handleNewMobile() {
		Mobile mobile = new Mobile(Color.BLUE, "Tpn");
		if(image != null) {
			mainApp.showAddGlisseur(mobile, image); // si il n'y pas d'erreur on l'ajoute
			this.mainApp.getMobileData().add(mobile);
		}else {
			messageErreur("Aucune image disponible", "Aucune image disponssible", "Aucune image disponible veuillez demarer la capture");
		}
		
	}

	/**
	 *  Action lanc�e losrque l'utilisateur clique sur le bouton Edit
	 */
	@FXML
	private void handleEditMobile() {
		Mobile selectMobile = tableMobile.getSelectionModel().getSelectedItem();
		
		if(selectMobile != null && image != null)
			mainApp.showAddGlisseur(selectMobile, image);
		else if(image == null) 
			messageErreur("Aucune image disponible", "Aucune image disponssible", "Aucune image disponible veuillez demarer la capture");
		else if(selectMobile != null)
			messageErreur("No Selection", "No Mobile Selected", "Please select a Mobile in the table.");	
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
	        tableMobile.setItems(mainApp.getMobileData());
	        this.pileFrame = mainApp.getPileImage();
	        this.mainApp.getPrimaryStage().setOnCloseRequest((event)->{
	        	if(mainApp.getThreadCaptureFlag())
	         	   mainApp.setThreadCaptureFlag(false);
	        	
	        });
	}
	public void dsetCameraChoice(int cam) {
		this.choiceCam = cam;
		grabber = new VideoInputFrameGrabber(choiceCam);
		grabber.setFrameRate(60);
		grabber.setImageHeight(1080);
		grabber.setImageWidth(1920);
	}

	
	public void traitement(Frame frame) {
		if(mainApp.getThreadAnalyseFlag()) {
			pileFrame.add(frame);		
		}	
	}
	public void threadCam() {
  	   playThread = new Thread(new Runnable() { public void run() {
  		   try {
  			  ExecutorService executor = Executors.newSingleThreadExecutor();
  			  grabber.start(); // on le demarre 
              while(mainApp.getThreadCaptureFlag()) {
            	 frame = grabber.grab();  
            	 
              	 if(frame == null) {
               		 break;
              	  }else {
              		 frame.timestamp = System.currentTimeMillis()-startTime;         		
              		 traitement(frame);
              		 image = SwingFXUtils.toFXImage(converter.convert(frame), null);
              		 Platform.runLater(() -> RetourCam.setImage(image));
              	   }
              	 Thread.sleep(15);	   
               }
              grabber.stop();
              grabber.release();
              executor.shutdownNow();
              executor.awaitTermination(10, TimeUnit.SECONDS);
  		   } catch (Exception e) {
  			   e.printStackTrace();
  		   } catch (InterruptedException e) {
				e.printStackTrace();
			}
  		 
  	   }});

  }
	
}

