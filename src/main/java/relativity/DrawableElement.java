package relativity;

import java.awt.Color;
import java.awt.Graphics2D;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import relativity.transform.Transform;
import relativity.transform.TransformElement;

public abstract class DrawableElement {

	private Transform transform;
	private final String color;

	protected DrawableElement() {
		transform = null;
		color = null;
	}

	protected DrawableElement(Color color) {
		this.color = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}

	public abstract void draw(Graphics2D g2, Vector2D maxBounds, Vector2D minBounds);

	protected Color getColor() {
		return Color.decode(color);
	}

	public Transform getTransform() {
		if( transform==null)
			return new Transform();
		return transform;
	}
	
	public void addElement( TransformElement element ) {
		if( transform==null)
			transform = new Transform();
		transform.add( element );
	}	

	public void setTransform(Transform transform) {
		this.transform = transform;
	}
	
	

}
