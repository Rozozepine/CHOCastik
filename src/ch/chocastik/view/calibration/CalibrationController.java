package ch.chocastik.view.calibration;

import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.FrameGrabber.ImageMode;
import org.bytedeco.javacv.*;
import ch.chocastik.controller.MainApp;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;

public class CalibrationController {

    @FXML
    private ImageView RetourCalib;
    @FXML
    private Button boutonStart;
    @FXML
    private Button boutonContinue;
    @FXML
    private Button ReglageCalib;
    @FXML
    private AnchorPane conteneur;
    @FXML
    private Text nombreImage;
    
    private boolean videoIsActive = false;
    private boolean done = false;
    private long lastAddedTime = -1;
	private MainApp mainApp;
	private Thread calibrationThread;
    private Marker[][] markers;
    private MarkedPlane boardPlane;
    private CameraDevice.Settings cameraSetting;
    private Marker.ArraySettings markerSettings = new Marker.ArraySettings();
    private  MarkerDetector.Settings markerDetectorSettings =  new MarkerDetector.Settings();
    private GeometricSettings geometricCalibratorSettings = new GeometricSettings();
    private CameraDevice cameraDevices = null;
    private GeometricCalibrator geometricCalibrators = null;
	
    private IplImage colorImages;
    private FrameGrabber frameGrabbers;
	private OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
	private Java2DFrameConverter converter2 = new Java2DFrameConverter();
    
    public static class GeometricSettings extends ProCamGeometricCalibrator.Settings {
        boolean enabled = true;
        boolean useMarkerCenters = true;
        int imagesInTotal = 3;
        long shotTimeInterval = 2000;

        public boolean isEnabled() {
            return enabled;
        }
        public void setEnabled(boolean enabled) {
            firePropertyChange("enabled", this.enabled, this.enabled = enabled);
        }

        public boolean isUseMarkerCenters() {
            return useMarkerCenters;
        }

        public int getImagesInTotal() {
            return imagesInTotal;
        }
        public void setImagesInTotal(int imagesInTotal) {
            imagesInTotal = Math.max(3, imagesInTotal);
            this.imagesInTotal = imagesInTotal;
        }

        public long getShotTimeInterval() {
            return shotTimeInterval;
        }
        public void setShotTimeInterval(long shotTimeInterval) {
            this.shotTimeInterval = shotTimeInterval;
        }
    }
    // METHODE FXML
    @FXML
    private void initialize() throws Exception {
    	RetourCalib.fitWidthProperty().bind(conteneur.widthProperty());
    	RetourCalib.fitHeightProperty().bind(conteneur.heightProperty());
    }
    @FXML
	public void handlerContinue() throws Exception {
		if(this.done) {
    	calibrationThread.interrupt();
	 		frameGrabbers.stop();
            frameGrabbers.release();
			mainApp.showAnalyse(cameraDevices);
    }
    	
    	mainApp.showAnalyse(cameraDevices);
	}
	@FXML
	public void handleImageMire() {
		
	}
	
    /**
     * Cette méthode permet de lancer ou stopper l'analyse vidéo lorsque l'utilisateur
     *  clique sur le bouton Start
     */
    @FXML
    private void startCalibration() {
    	// On vérifie si l'analyse n'est pas deja en cours, si c'est le cas
    	// on la stoppe, sinon on la relance
        if(this.videoIsActive) {
     	   calibrationThread.interrupt();
     	   this.videoIsActive = false;
     	   this.boutonStart.setText("Start");
        }else {
     	   this.threadCalibration();
     	   calibrationThread.start();
     	   this.videoIsActive = true;
     	   this.boutonStart.setText("Stop");
        }
    }
    
    // METHODE DE L'OBJET
    /**
     * Cette méthode permet de créer le thread s'occuppant d'analyser la vidéo a la recherche de marqueur
     * et quand il à suffisament de marqueur elle lance la calibration
     */
	public void threadCalibration() {
		
		 calibrationThread = new Thread(new Runnable() { public void run() {
			try {
				ExecutorService executor = Executors.newSingleThreadExecutor();
				frameGrabbers.start();
				while (!Thread.interrupted()) {
					final IplImage grabbedImages = converter.convert(frameGrabbers.grab());
					// on copie l'image dans une version RGB
					cvCvtColor(grabbedImages, colorImages, CV_GRAY2RGB);
					// on vérifie si on à trouvé des marqueurs
					boolean hasDetectedMarkers = geometricCalibrators.processImage(grabbedImages) != null;
					// on affiche ceux detectées
					geometricCalibrators.drawMarkers(colorImages);
					Image image = SwingFXUtils.toFXImage(converter2.convert(converter.convert(colorImages)), null);
					// on projete l'image sur l'image view
					Platform.runLater(() -> {
						RetourCalib.setImage(image);
					});
					long time = System.currentTimeMillis();
					// si on à detecté des marqueur, et que le temps depuis la derniére capture est suffisament important
					// on les ajoute
					if (hasDetectedMarkers && time-lastAddedTime > geometricCalibratorSettings.shotTimeInterval) {
						lastAddedTime = time;
						geometricCalibrators.addMarkers();
						// on affiche le nombre d'image restant à capturer
						Platform.runLater(() -> {
							nombreImage.setText("Il vous reste "+(geometricCalibratorSettings.getImagesInTotal()-geometricCalibrators.getImageCount()) + " Image à prendre");
						});
						// si on à suffisament de marqueur on lance la calibration
						if (geometricCalibrators.getImageCount() >= geometricCalibratorSettings.imagesInTotal) {
							 GeometricCalibrator calibratorAtOrigin = geometricCalibrators;
							 geometricCalibrators.calibrate(geometricCalibratorSettings.useMarkerCenters);
							 // on authorise l'utilisateur à continuer
							 boutonContinue.setDisable(false);			 
							 done = true;
						}				
					}
				}
				// on stop de maniére propre les différent objet
		 		executor.shutdownNow();	
		 		executor.awaitTermination(10, TimeUnit.SECONDS);
		 		frameGrabbers.stop();
	            frameGrabbers.release();
		   } catch (Exception | InterruptedException e) {
			   e.printStackTrace();
		   }
		   }});
	}
	/**
	 * Cette méthode permet d'initaliser différente variable nessecaire au fonctionnement de l'analyse
	 * @throws Exception
	 */
    public void initVariable() throws Exception {
    	// on initialise le marqueur
    	double marginX = Math.max(0.0, (markerSettings.getSpacingX()-markerSettings.getSizeX())/2);
        double marginY = Math.max(0.0, (markerSettings.getSpacingY()-markerSettings.getSizeY())/2);
        double width = (markerSettings.getColumns()-1)*markerSettings.getSpacingX() + markerSettings.getSizeX() + 2*marginX;
        double height = (markerSettings.getRows()-1)*markerSettings.getSpacingY() + markerSettings.getSizeY() + 2*marginY;
        markers = Marker.createArray(markerSettings, marginX, marginY);
        boardPlane = new MarkedPlane((int)Math.ceil(width), (int)Math.ceil(height), markers[0], 1);
    	// on intialise le calibrateur
		geometricCalibrators = new GeometricCalibrator(geometricCalibratorSettings,markerDetectorSettings, boardPlane, cameraDevices); 
		// on initialise la paramêtre de la caméra 
		cameraSetting = (CameraDevice.Settings) cameraDevices.getSettings();
		// on initialise le frameGrabber
		frameGrabbers =  FrameGrabber.createDefault(cameraSetting.getDeviceNumber()); 
		
		// on démarre le Grabber pour configurer différent paramêtre
		frameGrabbers.start();
		frameGrabbers.setFrameRate(60.0);
		// on vérife si le mode de capture est en noir/blanc
		if(frameGrabbers.getImageMode() != ImageMode.GRAY) {
			frameGrabbers.setImageMode(ImageMode.GRAY);
		}
		// on ajuste la taille de l'image
		conteneur.setMinWidth(frameGrabbers.getImageWidth());
		conteneur.setMinHeight(frameGrabbers.getImageHeight());
		// on crée l'image qui va affiche les marqueur en couleur et le reste en noir/blanc
		colorImages = new IplImage();
		colorImages = IplImage.create(frameGrabbers.getImageWidth(), frameGrabbers.getImageHeight(), IPL_DEPTH_8U, 3);
		// on stoppe le Grabber
		frameGrabbers.stop();
		frameGrabbers.release();	
		// on sauvgarde une image de la mire
		cvSaveImage("test.jpg", boardPlane.getImage());
		// on empeche l'utilisateur de continuer
		this.boutonContinue.setDisable(true);
		// on affiche le nombre d'image à prendre
		nombreImage.setText("Il vous reste "+geometricCalibratorSettings.getImagesInTotal() + " Image à prendre");
	}

	// GET ET SET
	public void setMainApp(MainApp mainApp) {
		   this.mainApp = mainApp;
	}
	public void setCameraChoice(CameraDevice cam) throws Exception {
		this.cameraDevices = cam;
	}
	
}
		





