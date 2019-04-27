package ch.chocastik.view.calibration;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_SIGN;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.bytedeco.javacpp.opencv_core.IplImage;

import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.FrameGrabber.ImageMode;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacv.*;

import ch.chocastik.controller.MainApp;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacv.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
public class CalibrationController {

    @FXML
    private ImageView RetourCalib;
    @FXML
    private ImageView mire;
    @FXML
    private Button BGo;
    @FXML
    private Button ReglageCalib;
    @FXML
    private AnchorPane conteneur;
    private boolean done = false;
	private MainApp mainApp;
	private Thread calibrationThread;
    private Marker[][] markers;
    private MarkedPlane boardPlane;
    private Marker.ArraySettings markerSettings = new Marker.ArraySettings();
    private  MarkerDetector.Settings markerDetectorSettings =  new MarkerDetector.Settings(); // possiblement à redifinr
    private GeometricSettings geometricCalibratorSettings = new GeometricSettings();

    private CameraDevice cameraDevices = null;
    private FrameGrabber frameGrabbers = null;
    private GeometricCalibrator geometricCalibrators = null;
	private boolean videoIsActive = false;
    
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
        public void setUseMarkerCenters(boolean useMarkerCenters) {
            this.useMarkerCenters = useMarkerCenters;
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

    @FXML
    private void initialize() throws Exception {
    	RetourCalib.fitWidthProperty().bind(conteneur.widthProperty());
    	RetourCalib.fitHeightProperty().bind(conteneur.heightProperty());
    }
    @FXML
    private void startCalibration() throws Exception {
        if(this.videoIsActive) {
     	   calibrationThread.interrupt();
     	   this.videoIsActive = false;
     	   this.BGo.setText("Start");
        }else {
     	   this.threadCalibration();
     	   this.videoIsActive = true;
     	   this.BGo.setText("Stop");
     	 
        	 calibrationThread.start();
        }
    }
	public void threadCalibration() {
		 calibrationThread = new Thread(new Runnable() { public void run() {
			try {
				 ExecutorService executor = Executors.newSingleThreadExecutor();
				 geometricCalibrators = new GeometricCalibrator(geometricCalibratorSettings,markerDetectorSettings, boardPlane, cameraDevices); 
				
	  			 CameraDevice.Settings setting = (CameraDevice.Settings) cameraDevices.getSettings();
	  			 final FrameGrabber frameGrabbers = new OpenCVFrameGrabber(setting.getDeviceNumber()); // on crée le grabber 
				 if(frameGrabbers.getImageMode() != ImageMode.GRAY) {
					 frameGrabbers.setImageMode(ImageMode.GRAY);
				 }
				 frameGrabbers.start();
				 conteneur.setMinWidth(frameGrabbers.getImageWidth());
	  			 conteneur.setMinHeight(frameGrabbers.getImageHeight());
				 final IplImage[] colorImages = new IplImage[1];

	             
				 final  OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
				 final Java2DFrameConverter converter2 = new Java2DFrameConverter();
				 done = false;
				 long lastAddedTime = -1;
				 while (!done && !Thread.interrupted()) {
					 final Frame grabbedFrames = frameGrabbers.grab();
					 final IplImage grabbedImages = converter.convert(grabbedFrames);	
					 if (colorImages[0] == null) {
		                    colorImages[0] = IplImage.create(grabbedImages.width(), grabbedImages.height(), IPL_DEPTH_8U, 3);
					 }
					 cvCvtColor(grabbedImages, colorImages[0], CV_GRAY2RGB);
		      
					 boolean hasDetectedMarkers = geometricCalibrators.processImage(grabbedImages) != null;
					 geometricCalibrators.drawMarkers(colorImages[0]);
					 Image image = SwingFXUtils.toFXImage(converter2.convert(converter.convert(colorImages[0])), null);
					 Platform.runLater(() -> {
						 RetourCalib.setImage(image);
					 });
					 long time = System.currentTimeMillis();
					 if (hasDetectedMarkers && time-lastAddedTime > geometricCalibratorSettings.shotTimeInterval) {
						 lastAddedTime = time;
		        		// the calibrators have decided to save these markers, make a little flash effect
						 geometricCalibrators.addMarkers();
						 System.out.println("Prout "+geometricCalibrators.getImageCount());
						 if (geometricCalibrators.getImageCount() >= geometricCalibratorSettings.imagesInTotal) 
							 done = true;
					 	}
				 	}
				GeometricCalibrator calibratorAtOrigin = geometricCalibrators;
			    geometricCalibrators.calibrate(geometricCalibratorSettings.useMarkerCenters);
		 		executor.shutdownNow();	
		 		executor.awaitTermination(10, TimeUnit.SECONDS);
		 		frameGrabbers.stop();
	            frameGrabbers.release();
		   } catch (Exception | InterruptedException e) {
			   e.printStackTrace();
		   }
		   }});
	}
	
	public void setMainApp(MainApp mainApp) {
		   this.mainApp = mainApp;
	}
	public void dsetCameraChoice(CameraDevice cam) throws Exception {
		this.cameraDevices = cam;
        double marginX = Math.max(0.0, (markerSettings.getSpacingX()-markerSettings.getSizeX())/2);
        double marginY = Math.max(0.0, (markerSettings.getSpacingY()-markerSettings.getSizeY())/2);
        markers = Marker.createArray(markerSettings, marginX, marginY);
        double width = (markerSettings.getColumns()-1)*markerSettings.getSpacingX() + markerSettings.getSizeX() + 2*marginX;
        double height = (markerSettings.getRows()-1)*markerSettings.getSpacingY() + markerSettings.getSizeY() + 2*marginY;
        boardPlane = new MarkedPlane((int)Math.ceil(width), (int)Math.ceil(height), markers[0], 1);
        cvSaveImage("test.jpg", boardPlane.getImage());
        
	}
	@FXML
	public void handlerContinue() {
		if(this.done) {
			mainApp.showAnalyse(cameraDevices);
		}
	}
}
		





