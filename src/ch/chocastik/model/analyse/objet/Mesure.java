package ch.chocastik.model.analyse.objet;

import java.util.ArrayList;

public class Mesure {
		private int nbPixel = 0;
		private float distanceRelle = 10;
		private ArrayList<Float> listRad = new ArrayList<Float>();
		
		public void transformPointToRealPoint(Point point) {
			float coef = (distanceRelle/nbPixel);
			point.setX(point.getX()*coef);
			point.setY(point.getY()*coef);
			
		}
		
		public void addRadius(float rad) {
			listRad.add(rad);
		}
		
		public void calculateNbPixel() {
			float somme = 0;
			for(Float rad: listRad)
				somme = rad + somme;
			somme = somme/listRad.size();
			this.nbPixel = Math.round(somme);
		}

}
