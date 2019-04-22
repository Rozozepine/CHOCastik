package ch.chocastik.model.analyse.objet;

public class Point {
	private int x;
	private int y;
	private final long startTime;
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
		this.startTime = System.currentTimeMillis();
	}
}
