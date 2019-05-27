package ch.chocastik.model.analyse.objet;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.io.File;

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
	
	public void addPoint(Point point) {
			// on transforme le point pour le passer d'un referentielle à l'orgine en haut à gauche, a en bas à gauche
			this.referentiel.transformToNaturalReferentiel(point);
			// on transpose le point dans le nouveau referentielle
			this.referentiel.transformToRelatif(point);
			// on l'ajoute au graphe
			Platform.runLater(()->series.getData().add(new XYChart.Data<Number, Number>(point.getX(),point.getY())));
			// on l'ajoute a la liste des points
			this.listOfPoint.add(point);
	
	}
	public boolean exportTrajectoire(Mesure mesure, ArrayList<Tracker> listTraker, File file) {
		for(Tracker traker: listTraker) {
			if(this != traker.getTrajectoire()) {
				this.listOfPoint.retainAll(traker.getTrajectoire().getListOfPoint());
			}
		}
		// on calcule la moyenne trois points par trois points
		meanThreePoint();
		// on caclue la mesure
		mesure.calculateNbPixel();
		// on ecrit ensuite la liste des point dans le fichiers
		return writeToFile(file.getAbsolutePath(), mesure);
	}
	
	public void meanThreePoint() {
		ObservableList<Point> listOfPointTnp = FXCollections.observableArrayList();
		int compteur = 0;
		for(Point point: this.listOfPoint) {
			float tnpX = 0;
			float tnpY = 0;
			long tnpTime = 0;
			if(compteur == 0 || compteur == 1) {
				tnpX = tnpX + point.getX();
				tnpY = tnpY + point.getY();
				tnpTime = tnpTime + point.getTimecode();
				compteur ++;
			}
			if(compteur == 2) {
				compteur = 0;
				tnpX = tnpX/3;
				tnpY = tnpY/3;
				tnpTime = tnpTime/3;
				listOfPointTnp.add(new Point(tnpX, tnpY, tnpTime));
			}
		}
		this.listOfPoint = listOfPointTnp;
	}
	public boolean writeToFile(String path, Mesure mesure) {
		try {
			// on ouvre un pointeur en ecriure sur le fichier 
			PrintWriter writer = new PrintWriter(path+"\\" +mobile.getName()+".txt");
			// on ecrit le nom
			writer.println("Mobile name: "+ mobile.getName());
			// on ecrit le coeficient
			writer.println("Nombre de pixels : "+mesure.getNbPixel() + " Coefficient : "+mesure.getCoef());
			// on ajoute la liste des point
			for(Point point: listOfPoint) {
				// on transforme le point pour obtenir une valeur relle
				mesure.transformPointToRealPoint(point);
				writer.println(point.getTimecode()+":"+point.getX()+":"+point.getY());
			}
			// on ferme le fichier
			writer.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	 // ============ Get et Set ================ //
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
