package ch.chocastik.model.cameras;

import java.util.ArrayList;
import java.util.List;

import org.opencv.calib3d.*;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;




public class Calibration {
	// Variable we need for calibration
	private	MatOfPoint3f obj;
	private boolean isCalibrated;
	private MatOfPoint2f imageCorners;
	private Mat intrinsic;
	private Mat distCoeffs;
	private List<Mat> imagePoints;
	private List<Mat> objectPoints;
	// number of calibration you need
	private int nbCornerHor;
	private int nbCornerVer;
	private Mat savedImage;
	private boolean isDisp;
	
	public Calibration(int nbCornerHor, int nbCornerVer){
		this.savedImage = new Mat();
		this.nbCornerHor = nbCornerHor;
		this.nbCornerVer = nbCornerVer;
		this.intrinsic = new Mat(3, 3, CvType.CV_32FC1);
		this.distCoeffs = new Mat();
		this.obj = new MatOfPoint3f();
		this.imageCorners = new MatOfPoint2f();
		this.imagePoints = new ArrayList<>();
		this.objectPoints = new ArrayList<>();
		this.intrinsic = new Mat(3, 3, CvType.CV_32FC1);
		this.distCoeffs = new Mat();
		this.setDisp(false);
	}
	public void drawMatCorner(Mat in, Mat out) {
		//init
		Mat grayImage = new Mat();
		in.copyTo(out);
		// convert color Mat frame to Gray and black frame
		Imgproc.cvtColor(in, grayImage, Imgproc.COLOR_BGR2GRAY);
		// we save the current size of the board
		Size sizeBoard = new Size(this.nbCornerHor, this.nbCornerVer);
		boolean found = Calib3d.findChessboardCorners(grayImage, sizeBoard,this.imageCorners,Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE + Calib3d.CALIB_CB_FAST_CHECK);
		//if we have found all corner we needed
		if(found) {
			TermCriteria term = new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 30, 0.1);
			Imgproc.cornerSubPix(grayImage, this.imageCorners, new Size(11, 11), new Size(-1, -1), term);
			Calib3d.drawChessboardCorners(out, sizeBoard, this.imageCorners, found);
			this.setDisp(true);
		}else {
			this.setDisp(false);
		}
	}
	public void addMat(Mat in) {
		Mat grayImage = new Mat();
		grayImage.copyTo(this.savedImage);
		// convert color Mat frame to Gray and black frame
		Imgproc.cvtColor(in, grayImage, Imgproc.COLOR_BGR2GRAY);
		// we save the current size of the board
		Size sizeBoard = new Size(this.nbCornerHor, this.nbCornerVer);
		boolean found = Calib3d.findChessboardCorners(grayImage, sizeBoard,this.imageCorners,Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE + Calib3d.CALIB_CB_FAST_CHECK);
		//if we have found all corner we needed
		if(found) {
			TermCriteria term = new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 30, 0.1);
			Imgproc.cornerSubPix(grayImage, this.imageCorners, new Size(11, 11), new Size(-1, -1), term);
			grayImage.copyTo(this.savedImage);
			System.out.println(this.savedImage.size());
			this.addValue();
			System.out.println("Add one image");
		}
	}
	public void unidstort(Mat in) {
		if(this.isCalibrated)
			Imgproc.undistort(in, in, intrinsic, distCoeffs);
	
	}
	public void updateSettings(int nbCornerHor, int nbCornerVer) {
		this.nbCornerHor = nbCornerHor;
		this.nbCornerVer = nbCornerVer;
		System.out.println(nbCornerHor+" , "+nbCornerVer);
		int numSquares = this.nbCornerHor * this.nbCornerVer;
		for (int j = 0; j < numSquares; j++)
			obj.push_back(new MatOfPoint3f(new Point3(j / this.nbCornerHor, j % this.nbCornerVer, 0.0f)));
	}
	
	public void calibrateCam() {
		List<Mat> rvecs = new ArrayList<>();
		List<Mat> tvecs = new ArrayList<>();
		intrinsic.put(0, 0, 1);
		intrinsic.put(1, 1, 1);
		// calibrate!
		System.out.print(this.savedImage.size());
		Calib3d.calibrateCamera(objectPoints, imagePoints, this.savedImage.size(), intrinsic, distCoeffs, rvecs, tvecs);
		this.isCalibrated = true;
	}
	
	private void addValue() {
		// save all the needed values
		this.imagePoints.add(imageCorners);
		this.objectPoints.add(obj);
	}
	public boolean getDisp() {
		return isDisp;
	}
	private void setDisp(boolean isDisp) {
		this.isDisp = isDisp;
	}
}
