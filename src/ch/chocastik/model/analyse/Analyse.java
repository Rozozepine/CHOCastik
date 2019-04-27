package ch.chocastik.model.analyse;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.bytedeco.javacpp.opencv_core.IplImage;
import ch.chocastik.controller.MainApp;
import ch.chocastik.model.analyse.objet.Mesure;
import ch.chocastik.model.analyse.objet.Mobile;
import ch.chocastik.model.analyse.objet.Referentiel;
import ch.chocastik.model.analyse.objet.Tracker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;

public class Analyse implements Runnable {
	private ObservableList<Mobile> mobileData = FXCollections.observableArrayList();
	private ArrayList<Tracker> listTraker;
	private Referentiel referentiel;
	private Mesure mesure;
	private ConcurrentLinkedQueue<IplImage> pileFrame;
	
	public Analyse(MainApp mainApp, ScatterChart<Number,Number> graphique) {
		this.listTraker = new ArrayList<Tracker>();
		this.mobileData = mainApp.getMobileData();
		this.referentiel = mainApp.getReferentiel();
		this.mesure = mainApp.getMesure();
		this.pileFrame = mainApp.getPileImage();
		for(Mobile mob: mobileData) {
			XYChart.Series<Number, Number> series = new XYChart.Series<>();
			series.setName(mob.getName());
			graphique.getData().add(series);
			listTraker.add(new Tracker(mob, this.referentiel, series));
		}
	}

	@Override
	public void run() {
		//tant que le Thread n'est pas stoppée on repete l'action
		while(!Thread.interrupted()) {
			IplImage frame = pileFrame.poll(); 
			if(frame != null) {
				for(Tracker trac: listTraker) 
					trac.detectCircle(frame, 0);
			}
		}
	}
		
	//Get et Set
}
