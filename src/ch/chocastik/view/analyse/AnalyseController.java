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
import javafx.beans.property.ObjectProperty;
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
	private  TableColumn<Mobile, Color> colCouleur;


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
	IplImage iplImage;
	
	 // ============ Methode Utilitaire ================ //
	/**
	 * Affiche un fenetre d'alerte
	 * @param titre titre du message
	 * @param header header du message
	 * @param contenu contenu du message
	 */
	public void messageErreur(String titre, String header, String contenu) {
		Alert alert = new Alert(AlertType.WARNING);
	    alert.initOwner(mainApp.getPrimaryStage());
	    alert.setTitle(titre);
        alert.setHeaderText(header);
        alert.setContentText(contenu);
        alert.showAndWait();
	}
	/***
	 * Met en place les differents graphes necessaire 
	 */
	private void creatDataGraph() {
		for(Mobile mob: mainApp.getMobileData()) {	
			// pour chaque mobile
			XYChart.Series<Number, Number> series = new XYChart.Series<>(); //on commence par cree un nouveau graphe
			series.setName(mob.getName()); //on lui donne le nom du mobile
			graphiquePoint.getData().add(series); // on l'ajoute au graphe deja present
			 // on cree un nouveau traker et onl'ajoute a la liste des trackers
			Tracker tracker = new Tracker(mob, mainApp.getReferentiel(), series, mainApp.getMesure());
			mainApp.getListTraker().add(tracker);
			Tab tab = new Tab(); // on cree un nouvel onglet
			tab.setText(mob.getName()); // on lui donne le nom du mobile en cours
			// on ajoute un tableau suivant l'ajout des points concerenant le mobile en cours
			TableView<Point> table = new TableView<Point>();
			TableColumn<Point,Float> xCol = new TableColumn<Point, Float>("X");
			TableColumn<Point,Float> yCol = new TableColumn<Point,Float>("Y");
			TableColumn<Point,Long> timeCol = new TableColumn<Point,Long>("timestamp");
			table.setItems(tracker.getTrajectoire().getListOfPoint());
			xCol.setCellValueFactory(cellData -> cellData.getValue().xProperty().asObject());
			yCol.setCellValueFactory(cellData -> cellData.getValue().yProperty().asObject());
			timeCol.setCellValueFactory(cellData -> cellData.getValue().timeProperty().asObject());
			table.getColumns().addAll(timeCol, xCol, yCol);	
			//on ajoute le nouvel onglet a la table des onglets
			tab.setContent(table);
			tabPane.getTabs().add(tab);				
		}
	}
	/**
	 * permet de reinitialiser toutes les donnees contenues dans les graphes
	 */
	private void deleteAllDataGraph() {
		tabPane.getTabs().removeAll(tabPane.getTabs());
		graphiquePoint.getData().removeAll(graphiquePoint.getData());			
	}
	
    @FXML
    private void initialize() {
    	this.AxeX.setLabel("Axe X");
    	this.AxeY.setLabel("Axe Y");
		colName.setCellValueFactory(cellData -> cellData.getValue().nameExportProperty());
		colCouleur.setCellValueFactory(cellData -> cellData.getValue().colorProperty());
    }
    
	@FXML
	public void handleExportAll() {
		if(mainApp.getAnalyseEndFlag()) {
			// si l'analyse est fini on authorise l'export
			// on cree une nouvelle fenetre de selection de dossier d'export
	        DirectoryChooser directoryChooser = new DirectoryChooser();
	        directoryChooser.setInitialDirectory(new File("C:\\Users\\"));
	        File selectedDirectory = directoryChooser.showDialog(mainApp.getPrimaryStage());
	        System.out.println(selectedDirectory.getAbsolutePath());
			for(Tracker tracker: mainApp.getListTraker()) {
				boolean ok = tracker.getTrajectoire().exportTrajectoire(mainApp.getMesure(), mainApp.getListTraker(), selectedDirectory);
				if(ok) {
					
				}else {
					
				}
			}
		}else {
			messageErreur("Analyse en cours", "Analyse en cours", "Une analyse est deja en cours");
		}
		
	}
	
    // ============ Methode Camera ================ //
    
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
	              	 Thread.sleep(14);	   
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
	 // ============ Methode Analyse  ================ //
	
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
        			messageErreur("Pas de sélection", "Pas de mobile sélectionné", "Veuillez sélectionner un mobile sur la table.");
        		}
    		}else {
    			messageErreur("Analyse en cours", "Analyse en cours", "Une analyse est deja en cours");
    		}
    		
    	}
    }

	public void traitement(Frame frame) {
		if(mainApp.getThreadAnalyseFlag()) {
			pileFrame.add(frame);		
		}	
	}
	
	// ============ Methode Mobile  ================ //
	/**
	 * Action lancee lorsque l'utilisateur clique sur le bouton supprimer Mobile
	 */
	@FXML
	private void handleDeleteMobile() {
		Mobile selectMobile = tableMobile.getSelectionModel().getSelectedItem();
		if(selectMobile != null)
			tableMobile.getItems().remove(selectMobile);
		else
			messageErreur("Pas de sélection", "Pas de mobile sélectionné", "Veuillez sélectionner un mobile sur la table.");
	}
	/**
	 * Action lancee lorsque l'utilisateur clique sur le bouton Ajouter
	 */
	@FXML
	private void handleNewMobile() {
		Mobile mobile = new Mobile(Color.BLUE, "Tpn");
		if(image != null) {
			mainApp.showAddGlisseur(mobile, image); // si il n'y pas d'erreur on l'ajoute
			this.mainApp.getMobileData().add(mobile);
		}else {
			messageErreur("Aucune image disponible", "Aucune image disponible", "Aucune image disponible, veuillez demarrer la capture");
		}
		
	}

	/**
	 *  Action lancee losrque l'utilisateur clique sur le bouton modifier
	 */
	@FXML
	private void handleEditMobile() {
		Mobile selectMobile = tableMobile.getSelectionModel().getSelectedItem();
		
		if(selectMobile != null && image != null)
			mainApp.showAddGlisseur(selectMobile, image);
		else if(image == null) 
			messageErreur("Aucune image disponible", "Aucune image disponible", "Aucune image disponible, veuillez démarrer la capture");
		else if(selectMobile != null)
			messageErreur("Pas de sélection", "Pas de mobile sélectionné", "Veuillez sélectionner un mobile sur la table.");	
	}

  
	 // ============ Get et Set ================ //
	
	/**
	 * Permet de mettre en place la mainApp
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
	        this.mainApp = mainApp;
	        tableMobile.setItems(mainApp.getMobileData()); // on ajoute les mobiles deja present dans la liste
	        this.pileFrame = mainApp.getPileImage(); // on recupere une reference a la pile d'echange
	        // on ajoute la fermeture de la capture lors de la fermeture de la camera
	        this.mainApp.getPrimaryStage().setOnCloseRequest((event)->{
	        	if(mainApp.getThreadCaptureFlag())
	         	   mainApp.setThreadCaptureFlag(false);
	        });
	}
	/**
	 * Permet de mettre en place la camera ainsi que le grabber
	 * @param cam
	 */
	public void dsetCameraChoice(int cam) {
		this.choiceCam = cam;
		grabber = new VideoInputFrameGrabber(choiceCam);
		grabber.setFrameRate(60); // fréquence de trame
		grabber.setImageHeight(1080); // Largeur
		grabber.setImageWidth(1920); // Hauteur
		//grabber.setOption("transpose","cclock_flip");
	}	
}

