package ch.chocastik.model.analyse.objet;

import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvInRangeS;
import static org.bytedeco.javacpp.opencv_core.cvPointFrom32f;
import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_AA;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2HSV;
import static org.bytedeco.javacpp.opencv_imgproc.CV_HOUGH_GRADIENT;
import static org.bytedeco.javacpp.opencv_imgproc.CV_MEDIAN;
import static org.bytedeco.javacpp.opencv_imgproc.cvCircle;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvDilate;
import static org.bytedeco.javacpp.opencv_imgproc.cvErode;
import static org.bytedeco.javacpp.opencv_imgproc.cvHoughCircles;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;

import java.util.ArrayList;
import java.util.Iterator;

import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvPoint2D32f;
import org.bytedeco.javacpp.opencv_core.CvPoint3D32f;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;

import ch.chocastik.controller.MainApp;
import javafx.application.Platform;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;

public class Tracker {
	private OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
	private Mobile mobile;
	private Trajectoire trajectoire;
	private Referentiel referentiel;
	private  XYChart.Series<Number, Number> series;
	private Mesure mesure;
	public Tracker(Mobile mobile, Referentiel referentiel, XYChart.Series<Number, Number> series, Mesure mesure) {
		this.mobile = mobile;
		this.referentiel = referentiel;
		this.series = series;
		this.mesure = mesure;
		this.trajectoire = new Trajectoire(referentiel, mobile, series);
	}
	
	public void detectCircle(IplImage imgSrc, long timecode) {
		CvMemStorage mem = CvMemStorage.create();
		// on commence par extraire les tache de la couleur donnée
		IplImage detectThrs = getThresholdImage(imgSrc);
		IplImage WorkingImage = cvCreateImage(detectThrs.asCvMat().cvSize(), IPL_DEPTH_8U, 1);   
		// on érode et dilate ensuite l'image en noir et blanc contenant les zone detectée pour supprimer les point isolée et 
		// augmenter les grande zone
        cvErode(detectThrs, WorkingImage, null, mobile.getErodeCount());    
        cvDilate(WorkingImage, WorkingImage, null, mobile.getDilateCount());
        // on lance ensuite la detection de cercle dans cette image
        CvSeq circles = cvHoughCircles( 
        		WorkingImage, //Input image
        	    mem, //Memory Storage
        	    CV_HOUGH_GRADIENT, //Detection method
        	    1, //Inverse ratio
        	    10, //Minimum distance between the centers of the detected circles
        	    100, //Higher threshold for canny edge detector
        	    10, //Threshold at the center detection stage
        	    mobile.getMinRad(),//min radius
        	    mobile.getMaxRad() //max radius
        );	
        for(int i = 0; i < circles.total(); i++){
        	// pour chaque cercle detecté
            CvPoint3D32f circle = new CvPoint3D32f(cvGetSeqElem(circles, i));
            CvPoint center = cvPointFrom32f(new CvPoint2D32f(circle.x(), circle.y()));
            // on fabrique un nouveau point contenant le centre du cercle et le timecode de la frame
            Point point = new Point(circle.x(), circle.y(), timecode);
            float rad = circle.z();
            // on verifie que le point appartient bien au referentielle
            if(referentiel.checkCordonne(point)) {
            		// on ajoute la mesure de son rayon au calcule de la mesure
            		mesure.addRadius(rad);
            		// on ajoute le point a la trajectoire
            		addPointToTrajectoire(point);
            }
            	
            
        }
       	cvReleaseImage(detectThrs);
       	cvReleaseImage(WorkingImage);
	}
	private void addPointToTrajectoire(Point point) {
		if(getTrajectoire() == null) {
			// si la trajectoire n'existe pas un la crée
			setTrajectoire(new Trajectoire(this.referentiel, this.mobile, series));
			getTrajectoire().addPoint(point);
		}else{
			// sinon on ajoute le point
			getTrajectoire().addPoint(point);
		}
	}
	private IplImage getThresholdImage(IplImage orgImg) {
		IplImage imgThreshold = cvCreateImage(orgImg.asCvMat().cvSize(), 8, 1);
		IplImage imgHSV = cvCreateImage(orgImg.asCvMat().cvSize(), 8, 3);
		cvCvtColor(orgImg, imgHSV, CV_BGR2HSV);
		// on extrait les couleur
		cvInRangeS(imgHSV, mobile.getHsvMin(), mobile.getHsvMax(), imgThreshold);
		// on la floute legerement pour eviter des disparité de pixel entre chaque image
    	cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 15,0,0,0);
    	// on libere l'image de travail pour eviter des problème de mémoire
       	cvReleaseImage(imgHSV);
    	return imgThreshold;
	}

	public Trajectoire getTrajectoire() {
		return trajectoire;
	}

	public void setTrajectoire(Trajectoire trajectoire) {
		this.trajectoire = trajectoire;
	}
	}

