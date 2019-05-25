package ch.chocastik.model.analyse.objet;

public class Referentiel {
	private int pixelXOrigine;
	private int pixelYOrigine;
	private int maxPixelX;
	private int maxPixelY;
	

	public Referentiel() {
		this.setPixelXOrigine(0);
		this.setPixelYOrigine(0);
		this.setMaxPixelX(1920);
		this.setMaxPixelY(1080);
	}
	public Referentiel(int pixelXOrignie, int pixelYOrigine, int maxPixelX, int maxPixelY) {
		this.setPixelXOrigine(pixelXOrignie);
		this.setPixelYOrigine(pixelYOrigine);
		this.setMaxPixelX(maxPixelX);
		this.setMaxPixelY(maxPixelY);
	}
	public boolean checkCordonne(Point point) {
		if(point.getX() > getMaxPixelX() || point.getY() > getMaxPixelY() || point.getX() < getPixelXOrigine() || point.getY() <getPixelYOrigine())
			return false;
		return true;
	}
	public void transformToNaturalReferentiel(Point point) {
		point.setY(1080 - point.getY());
	}
	public void transformToNoneNaturalReferentiel(Point point) {
		point.setY(1080 - point.getY());
	}
	public void transformToRelatif(Point point) {
		point.setX(point.getX() - this.getPixelXOrigine());
		point.setY(point.getY() - this.getPixelYOrigine());
	}
	public void transformToNoneRelatif(Point point) {
		point.setX(point.getX() + this.getPixelXOrigine());
		point.setY(point.getY() + this.getPixelYOrigine());
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

	
}
