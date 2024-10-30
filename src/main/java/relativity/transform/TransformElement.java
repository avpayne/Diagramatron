package relativity.transform;

import java.io.Serializable;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public interface TransformElement extends Serializable {
	public Vector2D transform(Vector2D point);
}
