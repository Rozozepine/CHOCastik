package ch.chocastik.model.analyse.objet;

import java.util.ArrayList;

public class Trajectoire {
	private ArrayList<Point> listOfPoint;
	private Referentiel referentiel;
	private Mobile mobile;
	private int distanceMin;
	public Trajectoire(Referentiel referentiel, Mobile mobile) {
		this.listOfPoint = new ArrayList<Point>();
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
			getReferentiel().transformToRelatif(point);
			getListOfPoint().add(point);
			return true;
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
	public ArrayList<Point> getListOfPoint() {
		return listOfPoint;
	}
	public void setListOfPoint(ArrayList<Point> listOfPoint) {
		this.listOfPoint = listOfPoint;
	}
	public int getDistanceMin() {
		return distanceMin;
	}
	public void setDistanceMin(int distanceMin) {
		this.distanceMin = distanceMin;
	}
	
}
