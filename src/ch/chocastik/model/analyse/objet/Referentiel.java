package ch.chocastik.model.analyse.objet;

public class Referentiel {
	private int pixelXOrigine;
	private int pixelYOrigine;
	private int maxPixelX;
	private int maxPixelY;
	
	public Referentiel(int pixelXOrignie, int pixelYOrigine, int maxPixelX, int maxPixelY) {
		this.pixelXOrigine = pixelXOrignie;
		this.pixelYOrigine = pixelYOrigine;
		this.maxPixelX = maxPixelX;
		this.maxPixelY = maxPixelY;
	}
	public boolean checkCordonne(Point point) {
		if(point.getX() > maxPixelX || point.getY() > maxPixelY || point.getX() < pixelXOrigine || point.getY() <pixelYOrigine)
			return false;
		return true;
	}
	public void transformToRelatif(Point point) {
		point.setX(point.getX() - this.pixelXOrigine);
		point.setY(point.getY() - this.pixelYOrigine);
	}
	
}
