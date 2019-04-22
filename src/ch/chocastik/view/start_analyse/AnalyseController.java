package ch.chocastik.view.start_analyse;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.FrameGrabber.Exception;

import ch.chocastik.controller.MainApp;
import ch.chocastik.model.Mobile.Mobile;
import ch.chocastik.model.cameras.Camera;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class AnalyseController {
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private AnchorPane StartAnalyse;
    @FXML
    private AnchorPane conteneur;
    @FXML
    private ImageView RetourCam;
    @FXML
    private Button AjouterGlisseur;
    @FXML
    private TableView<Mobile> TableGlisseurs;
    @FXML
    private TableColumn<Mobile, String> nameColunm;
    @FXML
    private TableColumn<Mobile, Color> colorColumn;
    @FXML
    private Button Startvideo;
    @FXML
	private MainApp mainApp;

    private static volatile Thread playThread;
    private Image image;
    private Camera cam;
    private Mat frame = new Mat();
	private boolean videoIsActive = false;
	private Timer timer;
	private  Iterator it;
	private int choiceCam;
    public AnalyseController() {
    	
    }
    @FXML
    private void initialize() {
    	this.threadCam();
    	RetourCam.fitWidthProperty().bind(conteneur.widthProperty());
    	RetourCam.fitHeightProperty().bind(conteneur.heightProperty());
    	nameColunm.setCellValueFactory(cellData->cellData.getValue().nameExportProperty());
    	colorColumn.setCellValueFactory(cellData->cellData.getValue().colorProperty());
    }
    @FXML
    public void startCamera() {
    		if(!playThread.isInterrupted()) {
    			this.Startvideo.setText("Stop");
    			threadCam();
    			playThread.start();	
    		}else {
    			this.Startvideo.setText("Start");
    		}	
    }
    public void threadCam() {
    	   playThread = new Thread(() -> {
    		   try {
    			   FrameGrabber grabber = FrameGrabber.createDefault(this.choiceCam);
    			   grabber.start();
    			   conteneur.setMinWidth(grabber.getImageWidth());
    			   conteneur.setMinHeight(grabber.getImageHeight());
    			   Java2DFrameConverter converter = new Java2DFrameConverter();
                   ExecutorService executor = Executors.newSingleThreadExecutor();
                   
                   while(!Thread.interrupted()) {
                	   Frame frame = grabber.grab();
                	   if(frame == null) {
                 		   break;
                	   }else {
                		   //System.out.println(mainApp.getMobileData().size());
           		    	   if(mainApp.getMobileData().size() >0) {
        		    		   for(Mobile mob: mainApp.getMobileData()) {
               					frame = mob.detectCircle(frame);
        		    		   	}
        		    	   }
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	   });
    }
 
	public void setMainApp(MainApp mainApp) {
	        this.mainApp = mainApp;
	        TableGlisseurs.setItems(this.mainApp.getMobileData());
	        this.it = mainApp.getMobileData().iterator();
	        this.cam = this.mainApp.getCam();
	}
    @FXML
    void AjoutMobile(ActionEvent event) {
    	Mobile mobile = new Mobile();
    	boolean ok = this.mainApp.showEditGlisseur(mobile, this.image);
    	if(ok) {
    		this.mainApp.getMobileData().add(mobile);
    	}
    }
    @FXML
    void EditMobile(ActionEvent event) {
    	Mobile mobile  = TableGlisseurs.getSelectionModel().getSelectedItem();	
    	if(mobile != null) {
    		this.mainApp.showEditGlisseur(mobile, this.image);	
    	}else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Mobile Selected");
            alert.setContentText("Please select a monbile in the table.");
            alert.showAndWait();
    	}
    }
    @FXML
    void SupprimeMobile() {
    	int index = TableGlisseurs.getSelectionModel().getFocusedIndex();
  
    	if(index >= 0) {
    		TableGlisseurs.getItems().remove(index);
    	}else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Mobile Selected");
            alert.setContentText("Please select a monbile in the table.");
            alert.showAndWait();
    	}
    }
	public void dsetCameraChoice(int nb) {
		this.choiceCam = nb;
	}

}

