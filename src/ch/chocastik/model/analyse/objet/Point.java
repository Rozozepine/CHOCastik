package ch.chocastik.model.analyse.objet;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.Frame;

public class Point {
	private float x;
	private float y;
	private IplImage frame;
	private int timecode;
	
	public Point(float x, float y,IplImage frame, int timecode) {
		this.x = x;
		this.y = y;
		this.frame = frame;
		this.timecode = timecode;
	}

	public IplImage getFrame() {
		return frame;
	}

	public void setFrame(IplImage frame) {
		this.frame = frame;
	}

	public int getTimecode() {
		return timecode;
	}

	public void setTimecode(int timecode) {
		this.timecode = timecode;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}
	public double getDistance(Point point) {
		return Math.sqrt(Math.pow((point.getX() - this.x),2) + Math.pow((point.getY()-this.y),2));
	}
}
