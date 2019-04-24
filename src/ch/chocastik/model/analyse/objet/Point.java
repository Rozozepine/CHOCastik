package ch.chocastik.model.analyse.objet;

import org.bytedeco.javacv.Frame;

public class Point {
	private int x;
	private int y;
	private Frame frame;
	private int timecode;
	
	public Point(int x, int y, Frame frame, int timecode) {
		this.x = x;
		this.y = y;
		this.frame = frame;
		this.timecode = timecode;
	}

	public Frame getFrame() {
		return frame;
	}

	public void setFrame(Frame frame) {
		this.frame = frame;
	}

	public int getTimecode() {
		return timecode;
	}

	public void setTimecode(int timecode) {
		this.timecode = timecode;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}
	public double getDistance(Point point) {
		return Math.sqrt((point.getX() - this.x)^2 + (point.getY()-this.y)^2);
	}
}
