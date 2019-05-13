package ch.chocastik.model.analyse;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;

import ch.chocastik.controller.MainApp;
import ch.chocastik.model.analyse.objet.Mesure;
import ch.chocastik.model.analyse.objet.Mobile;
import ch.chocastik.model.analyse.objet.Referentiel;
import ch.chocastik.model.analyse.objet.Tracker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;

public class Analyse implements Runnable {
	private ObservableList<Mobile> mobileData = FXCollections.observableArrayList();
	private ArrayList<Tracker> listTraker;
	private Referentiel referentiel;
	private Mesure mesure;
	private ConcurrentLinkedQueue<Frame> pileFrame;
	private long startTime;
	final OpenCVFrameConverter.ToIplImage converterToIplImage = new OpenCVFrameConverter.ToIplImage();
	private MainApp mainApp;
	
	public Analyse(MainApp mainApp) {
		this.listTraker = mainApp.getListTraker();
		this.mobileData = mainApp.getMobileData();
		this.referentiel = mainApp.getReferentiel();
		this.mesure = mainApp.getMesure();
		this.pileFrame = mainApp.getPileImage();
		this.mainApp = mainApp;
		this.startTime = System.currentTimeMillis();
	}

	@Override
	public void run() {
		//tant que le Thread n'est pas stoppée on repete l'action
		while(mainApp.getThreadAnalyseFlag() || !pileFrame.isEmpty()) {
			Frame frame = pileFrame.poll(); 
			if(frame != null) {
				for(Tracker trac: listTraker) 
					trac.detectCircle(converterToIplImage.convert(frame),frame.timestamp-startTime);
			}
		}	
	}
		
	//Get et Set
}
