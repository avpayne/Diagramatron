package relativity.transform;

import java.io.Serializable;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Rotation implements TransformElement, Serializable {

	private static final long serialVersionUID = -2001618704229248220L;
	private double theta;

	public Rotation(double theta) {
		this.theta = theta;
	}

	public Rotation(Rotation rotation) {
		this.theta = rotation.theta;
	}

	@Override
	public Vector2D transform(Vector2D point) {
		return new Vector2D(point.getX() * Math.cos(theta) - point.getY() * Math.sin(theta),
				point.getY() * Math.cos(theta) + point.getX() * Math.sin(theta));
	}

}
