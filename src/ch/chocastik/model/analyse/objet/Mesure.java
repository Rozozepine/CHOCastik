package ch.chocastik.model.analyse.objet;

public class Mesure {
		private int nbPixel;
		private float distanceRelle;
		private int pixelYOrigine;
		private int maxPixelX;
		private int pixelXOrigine;
		private int maxPixelY;

		public void transformPointToRealPoint(Point point) {
			float coef = (distanceRelle/nbPixel);
			point.setX(point.getX()*coef);
			point.setY(point.getY()*coef);
			
		}

}
