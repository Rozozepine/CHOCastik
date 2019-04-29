package ch.chocastik.model.analyse.objet;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.Frame;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Point {
	private SimpleFloatProperty x;
	private SimpleFloatProperty y;
	private IplImage frame;
	private SimpleIntegerProperty timecode;

	public Point(float x, float y,IplImage frame, int timecode) {
		this.x = new SimpleFloatProperty(x);
		this.y = new SimpleFloatProperty(y);
		this.timecode = new  SimpleIntegerProperty(timecode);
		this.frame = frame;
		this.timecode.set(timecode);
	}

	public IplImage getFrame() {
		return frame;
	}
	public void setFrame(IplImage frame) {
		this.frame = frame;
	}

	public int getTimecode() {
		return timecode.get();
	}

	public void setTimecode(int timecode) {
		this.timecode.set(timecode);
	}

	public float getY() {
		return y.get();
	}

	public void setY(float y) {
		this.y.set(y);
	}

	public float getX() {
		return x.get();
	}

	public void setX(float x) {
		this.x.set(x);
	}
    public IntegerProperty timeProperty() {
        return timecode;
    }
    public FloatProperty yProperty() {
        return y;
    }
    public FloatProperty xProperty() {
        return x;
    }
	public double getDistance(Point point) {
		return Math.sqrt(Math.pow((point.getX() - this.x.get()),2) + Math.pow((point.getY()-this.y.get()),2));
	}
}
