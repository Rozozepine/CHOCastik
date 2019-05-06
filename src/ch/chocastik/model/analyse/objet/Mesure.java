package ch.chocastik.model.analyse.objet;

public class Mesure {
		private int nbPixel;
		private float distanceRelle;
		private int pixelYOrigine;
		private int maxPixelX;
		private int pixelXOrigine;
		private int maxPixelY;

		public Mesure() {
			this.setNbPixel(10);
			this.setDistanceRelle(10);
			this.setPixelXOrigine(0);
			this.setPixelYOrigine(0);
			this.setMaxPixelX(1800);
			this.setMaxPixelY(1800);
		}
		public Mesure(int nbPixel, float distanceRelle,int pixelXOrignie, int pixelYOrigine, int maxPixelX, int maxPixelY) {
			this.nbPixel = nbPixel;
			this.distanceRelle = distanceRelle;
			this.setPixelXOrigine(pixelXOrignie);
			this.setPixelYOrigine(pixelYOrigine);
			this.setMaxPixelX(maxPixelX);
			this.setMaxPixelY(maxPixelY);
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
		public int getMaxPixelY() {
			return maxPixelY;
		}
		public void setMaxPixelY(int maxPixelY) {
			this.maxPixelY = maxPixelY;
		}
		public int getMaxPixelX() {
			return maxPixelX;
		}
		public void setMaxPixelX(int maxPixelX) {
			this.maxPixelX = maxPixelX;
		}
		public int getPixelYOrigine() {
			return pixelYOrigine;
		}
		public void setPixelYOrigine(int pixelYOrigine) {
			this.pixelYOrigine = pixelYOrigine;
		}
		public int getPixelXOrigine() {
			return pixelXOrigine;
		}
		public void setPixelXOrigine(int pixelXOrigine) {
			this.pixelXOrigine = pixelXOrigine;
		}
		public void transformPointToRealPoint(Point point) {
			float coef = (distanceRelle/nbPixel);
			point.setX(point.getX()*coef);
			point.setY(point.getY()*coef);
			
		}
		public void calculatePixel() {
			this.nbPixel = Math.abs(pixelXOrigine-maxPixelX);
		}
}
