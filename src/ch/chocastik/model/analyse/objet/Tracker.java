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
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;

public class Tracker {
	private OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
	private Mobile mobile;
	private ArrayList<Trajectoire> listOfTrajectoire;
	private Referentiel referentiel;
	
	public Tracker(Mobile mobile, Referentiel referentiel) {
		this.mobile = mobile;
		this.referentiel = referentiel;
		this.listOfTrajectoire = new ArrayList<Trajectoire>();
	}
	public void detectCircle(Frame frame, int timecode) {
		CvMemStorage mem = CvMemStorage.create();
		IplImage imgSrc = converter.convert(frame);
		IplImage detectThrs = getThresholdImage(imgSrc);
		IplImage WorkingImage = cvCreateImage(detectThrs.asCvMat().cvSize(), IPL_DEPTH_8U, 1);   
        cvErode(detectThrs, WorkingImage, null, mobile.getErodeCount());    
        cvDilate(WorkingImage, WorkingImage, null, mobile.getDilateCount());
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
        	
            CvPoint3D32f circle = new CvPoint3D32f(cvGetSeqElem(circles, i));
            CvPoint center = cvPointFrom32f(new CvPoint2D32f(circle.x(), circle.y()));
            int radius = Math.round(circle.z());  
            if(circle.x() > 0 && circle.y() > 0) {
            System.out.println("Center of the circle "+i+" " +circle.x()+"-"+circle.y());
            }
           
        }
	}
	private void addPointToTrajectoire(int x, int y, Frame frame, int timecode) {
		if(listOfTrajectoire.isEmpty()) {
			Trajectoire trajectoire = new Trajectoire(this.referentiel, this.mobile);
			Point point = new Point(x, y, frame, timecode);
			trajectoire.addPoint(point);
			listOfTrajectoire.add(trajectoire);
		}else {
			for(Trajectoire t: listOfTrajectoire) {
				
			}
		}
	}
	private IplImage getThresholdImage(IplImage orgImg) {
		IplImage imgThreshold = cvCreateImage(orgImg.asCvMat().cvSize(), 8, 1);
		IplImage imgHSV = cvCreateImage(orgImg.asCvMat().cvSize(), 8, 3);
		cvCvtColor(orgImg, imgHSV, CV_BGR2HSV);
		cvInRangeS(imgHSV, mobile.getHsvMin(), mobile.getHsvMax(), imgThreshold);
    	cvReleaseImage(imgHSV);
    	cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 15,0,0,0);
    	// save
    	return imgThreshold;
	}

	}

