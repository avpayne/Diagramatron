package relativity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public abstract class AbstractEvent extends DrawableElement implements Serializable {

	private static final long serialVersionUID = -6263237074968079863L;
	private static final int RADIUS = 10;
	private Double d;
	private Double ct;
	private String label;

	protected AbstractEvent(Double d, Double ct, String label, Color color) {
		super(color);
		this.d = d;
		this.ct=ct;
		this.label = label;		
	}

	protected AbstractEvent(Vector2D point, String label, Color color) {
		super(color);
		this.d = point.getX();
		this.ct=point.getY();
		this.label = label;		
	}

	public Double getD() {
		return d;
	}

	public Double getCt() {
		return ct;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setD(Double d) {
		this.d = d;
	}

	public void setCt(Double ct) {
		this.ct = ct;
	}

	@Override
	public void draw(Graphics2D g2, Vector2D maxBounds, Vector2D minBounds) {
		g2.setColor(getColor());
		Vector2D p = getTransform().transform(new Vector2D( d, ct));
		if (p.getX() < maxBounds.getX() && p.getX() > minBounds.getX() && p.getY() < maxBounds.getY()
				&& p.getY() > minBounds.getY()) {
			g2.fillOval((int) (p.getX() - RADIUS / 2d), (int) (p.getY() - RADIUS / 2d), RADIUS, RADIUS);
			if (label != null)
				g2.drawString(label, (int) p.getX() + RADIUS + 5, (int) p.getY() + 5);
		}
	}


	public abstract List<DrawableElement> getDrawableElements(List<DrawableElement> elements, FrameOfReference r, FrameOfReference s);

}
