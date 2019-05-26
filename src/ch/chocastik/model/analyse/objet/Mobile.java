package ch.chocastik.model.analyse.objet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import java.util.List;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

public class Mobile {
	private ObjectProperty<Color> color;
	private StringProperty nameExport;
    private CvScalar hsvMin;
    private CvScalar hsvMax;
    private  int ErodeCount = 3;
    private  int DilateCount = 3;
    private  int maxRad = 20;
    private  int minRad = 12;
    private int tolHue = 10;

    
	public Mobile(Color color2, String name) {
		this.nameExport = new SimpleStringProperty(name);
		this.setColor(new SimpleObjectProperty<Color>(color2));
	}

	public ObjectProperty<Color> colorProperty() {
		return color;
	}
	public StringProperty nameExportProperty() {
		return nameExport;
	}
	public String getName() {
		return this.nameExport.get();
	}
	public void setName(String name) {
		this.nameExport.set(name);
	}
	public void setColor(Color color, int tolHue) {
		this.color.set(color);
		this.tolHue = tolHue;
		int hue =(int) (color.getHue()/2);
		int tol = tolHue;
		setHsvMin(cvScalar(hue-tol, 0, 0, 0));
	    setHsvMax(cvScalar(hue+tol, 255, 255, 0));
	}
	public Color getColor() {
		return color.get();	
	}
	public int getTolHue() {
		return this.tolHue;
	}
	public void setColor(ObjectProperty<Color> color) {
		this.color = color;
	}
	public  int getMinRad() {
		return minRad;
	}
	public  void setMinRad(int minRad) {
		this.minRad = minRad;
	}
	public int getMaxRad() {
		return maxRad;
	}
	public void setMaxRad(int maxRad) {
		this.maxRad = maxRad;
	}
	public  int getErodeCount() {
		return ErodeCount;
	}
	public  void setErodeCount(int erodeCount) {
		this.ErodeCount = erodeCount;
	}
	public  int getDilateCount() {
		return DilateCount;
	}
	public void setDilateCount(int dilateCount) {
		this.DilateCount = dilateCount;
	}
	public CvScalar getHsvMax() {
		return hsvMax;
	}
	public void setHsvMax(CvScalar hsvMax) {
		this.hsvMax = hsvMax;
	}
	public CvScalar getHsvMin() {
		return hsvMin;
	}
	public void setHsvMin(CvScalar hsvMin) {
		this.hsvMin = hsvMin;
	}

}
