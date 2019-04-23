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
	public boolean checkCordonne(int x, int y) {
		if(x > maxPixelX || y > maxPixelY || x < pixelXOrigine || y <pixelYOrigine)
			return false;
		return true;
	}
	
}
