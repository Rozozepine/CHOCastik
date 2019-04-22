package ch.chocastik.model.cameras;

import java.io.ByteArrayInputStream;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import javafx.scene.image.Image;

public class Camera {
	private Calibration calib;
	private int src;
	private VideoCapture cap;
	private MatOfByte buffer;
	private int nbFrame;
	private int brightness;
	private int contrast;
	private int height;
	private int width;
	
	public Camera(int src) {
		this.cap = new VideoCapture();
		this.calib = new Calibration(10, 10);
		this.src = src;
		this.contrast = 1;
		this.brightness = 0;
		buffer = new MatOfByte();
	}
	public void openCapture() {
		this.cap.open(this.src);
	}
	public void grabFrame(Mat in) {
		if(this.cap.isOpened()) {
			try {
				this.cap.read(in);
				if(!in.empty()) {
					in.convertTo(in, -1, this.contrast, this.brightness);
					this.calib.unidstort(in);
				}
			}catch (Exception e) {
				System.err.print("ERROR");
				e.printStackTrace();
			}
		}
	}
	
	public void closeCapture() {
		this.cap.release();
	}
	public Image mat2Image(Mat frame)
	{
		try {
		Imgcodecs.imencode(".png", frame, this.buffer);
			return new Image(new ByteArrayInputStream(this.buffer.toArray()));
		}catch (Exception e) {
			System.out.print(e);
			return null;
		}
	}	
	public boolean isOpen() {
		return this.cap.isOpened();
	}
	public Calibration getCalib() {
		return calib;
	}
	public void setCalib(Calibration calib) {
		this.calib =  calib;
	}
	public int getNbFrame() {
		return nbFrame;
	}
	public void setNbFrame(int nbFrame) {
		this.nbFrame = nbFrame;
	}
	public int getBrightness() {
		return brightness;
	}
	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}

}
