package ch.chocastik.model.analyse.objet;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.Frame;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;

public class Point {
	private SimpleFloatProperty x;
	private SimpleFloatProperty y;
	private SimpleLongProperty timecode;

	public Point(float x, float y, long timecode) {
		this.x = new SimpleFloatProperty(x);
		this.y = new SimpleFloatProperty(y);
		this.timecode = new  SimpleLongProperty(timecode);

	}

	public long getTimecode() {
		return timecode.get();
	}

	public void setTimecode(long timecode) {
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
    public LongProperty timeProperty() {
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
