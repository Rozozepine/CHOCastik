package ch.chocastik.model.analyse.objet;

import java.util.ArrayList;

public class Mesure {
		private int nbPixel = 0;
		private float distanceRelle = 10;
		private float coef;
		private ArrayList<Float> listRad = new ArrayList<Float>();
		
		public void transformPointToRealPoint(Point point) {
			setCoef((getDistanceRelle()/getNbPixel()));
			point.setX(point.getX()*getCoef());
			point.setY(point.getY()*getCoef());
			
		}
		
		public void addRadius(float rad) {
			listRad.add(rad);
		}
		
		public void calculateNbPixel() {
			float somme = 0;
			for(Float rad: listRad)
				somme = rad + somme;
			somme = somme/listRad.size();
			this.setNbPixel(Math.round(somme));
		}

		public float getDistanceRelle() {
			return distanceRelle;
		}

		public void setDistanceRelle(float distanceRelle) {
			this.distanceRelle = distanceRelle;
		}

		public int getNbPixel() {
			return nbPixel;
		}

		public void setNbPixel(int nbPixel) {
			this.nbPixel = nbPixel;
		}

		public float getCoef() {
			return coef;
		}

		public void setCoef(float coef) {
			this.coef = coef;
		}

}
