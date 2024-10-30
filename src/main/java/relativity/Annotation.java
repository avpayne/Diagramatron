package relativity;

import java.awt.Color;
import java.awt.Graphics2D;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Annotation extends DrawableElement {

	public enum AnnotationPosition {
		LEFT, RIGHT
	}

	private Vector2D point;
	private String label;
	private AnnotationPosition annotationPosition;

	protected Annotation(Vector2D point, String label, AnnotationPosition annotationPosition,
			Color color) {
		super(color);
		this.point = point;
		this.label = label;
		this.annotationPosition = annotationPosition;
	}

	@Override
	public void draw(Graphics2D g2, Vector2D maxBounds, Vector2D minBounds) {
		g2.setColor(getColor());
		Vector2D p = getTransform().transform(point);
		if (p.getX() < maxBounds.getX() && p.getX() > minBounds.getX() && p.getY() < maxBounds.getY()
				&& p.getY() > minBounds.getY()) {
			int y = (int) p.getY();
			for (String line : label.split("\n")) {
				int x = annotationPosition == AnnotationPosition.RIGHT ? ((int) p.getX() + 10)
						: ((int) p.getX() - g2.getFontMetrics().stringWidth(line) - 10);
				g2.drawString(line, x, y);
				y += g2.getFontMetrics().getHeight();
			}
		}
	}

}
