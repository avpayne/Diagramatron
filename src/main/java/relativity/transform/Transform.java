package relativity.transform;

import java.io.Serializable;
import java.util.LinkedList;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Transform extends LinkedList<TransformElement> implements Serializable {

	private static final long serialVersionUID = 8751599460875004787L;

	public Transform()
	{
		add( new Scale(1,1));
	}
	
	public Transform(Transform t) {
		for (TransformElement element : t) {
			if (element instanceof Translation translation)
				add(new Translation(translation));
			else if (element instanceof Rotation rotation)
				add(new Rotation(rotation));
			else if (element instanceof Scale scale)
				add(new Scale(scale));
		}
	}

	public Vector2D transform(Vector2D point) {
		Vector2D result = point;
		for (TransformElement element : this) {
			result = element.transform(result);
		}
		return result;
	}

}
