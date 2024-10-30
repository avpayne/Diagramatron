package relativity.transform;

import java.io.Serializable;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Translation implements TransformElement, Serializable {

	private static final long serialVersionUID = -7855978865801955054L;
	private final double x;
	private final double y;

	public Translation(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Translation(Translation t) {
		this.x = t.x;
		this.y = t.y;
	}

	@Override
	public Vector2D transform(Vector2D point) {
		return new Vector2D(point.getX() + x, point.getY() + y);
	}

}
