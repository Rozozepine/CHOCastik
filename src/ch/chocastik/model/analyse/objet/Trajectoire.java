package ch.chocastik.model.analyse.objet;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class Trajectoire {
	private ObservableList<Point> listOfPoint = FXCollections.observableArrayList();
	private Referentiel referentiel;
	private Mobile mobile;
	private int distanceMin;
	private  XYChart.Series<Number, Number> series;
	public Trajectoire(Referentiel referentiel, Mobile mobile, XYChart.Series<Number, Number> series) {
		this.series = series;
		this.distanceMin = 10;
		this.referentiel = referentiel;
		this.mobile = mobile;
	}
	public boolean chekAppartenance(Point point) {
		if(getListOfPoint().isEmpty()) {
			return false;
		}else{
			Point lastPoint = getListOfPoint().get(getListOfPoint().size()-1);
			if(lastPoint.getDistance(point) > this.getDistanceMin())
				return false;
			else
				return true;
			
		}
	}
	public boolean addPoint(Point point) {
		if(!getReferentiel().checkCordonne(point))
			return false;
		else {
			getReferentiel().transformToNaturalReferentiel(point);
			getReferentiel().transformToRelatif(point);
			Platform.runLater(()->series.getData().add(new XYChart.Data<Number, Number>(point.getX(),point.getY())));
			getListOfPoint().add(point);
			return true;
		}
	}
	public void exportTrajectoire(Mesure mesure, ArrayList<Tracker> listTraker) {
		try {
			PrintWriter writer = new PrintWriter("C:\\Users\\Rose\\Documents\\Projet\\Unige\\CHOCastik\\Resultat\\"+mobile.getName()+".txt");
			for(Point point: listOfPoint) {
				//mesure.transformPointToRealPoint(point);
				writer.println(point.getTimecode()+":"+point.getX()+":"+point.getY());
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Mobile getMobile() {
		return mobile;
	}
	public void setMobile(Mobile mobile) {
		this.mobile = mobile;
	}
	public Referentiel getReferentiel() {
		return referentiel;
	}
	public void setReferentiel(Referentiel referentiel) {
		this.referentiel = referentiel;
	}
	public ObservableList<Point> getListOfPoint() {
		return listOfPoint;
	}
	public void setListOfPoint(ObservableList<Point> listOfPoint) {
		this.listOfPoint = listOfPoint;
	}
	public int getDistanceMin() {
		return distanceMin;
	}
	public void setDistanceMin(int distanceMin) {
		this.distanceMin = distanceMin;
	}
	
}
