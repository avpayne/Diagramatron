package relativity.transform;

import java.io.Serializable;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Scale implements TransformElement, Serializable {

	private static final long serialVersionUID = 2512012692923532059L;
	private final double scaleX;
	private final double scaleY;

	public Scale(double scaleX, double scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}

	public Scale(Scale scale) {
		this.scaleX = scale.scaleX;
		this.scaleY = scale.scaleY;
	}

	@Override
	public Vector2D transform(Vector2D point) {
		return new Vector2D(point.getX() * scaleX, point.getY() * scaleY);
	}

}
