package relativity;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.io.Serializable;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class EventLine extends DrawableElement implements Serializable {

	private static final long serialVersionUID = 4394916592168656588L;
	private static final int D = 10;
	private static final int H = 5;

	private final Vector2D p1;
	private final Vector2D p2;
	private final boolean extendable;
	private final boolean isArrow;
	private final boolean isDashed;
	private String label;
	private final int thickness;

	public EventLine(Vector2D p1, Vector2D p2, boolean extendable, boolean isArrow, boolean isDashed, int thickness, String label, Color color) {
		super(color);
		this.p1 = p1;
		this.p2 = p2;
		this.extendable = extendable;
		this.isArrow = isArrow;
		this.isDashed = isDashed;
		this.label = label;
		this.thickness = thickness;

	}

	public EventLine(Vector2D p1, Vector2D p2, Color color) {
		this(p1, p2, false, false, false, 1, null, color);
	}

	public EventLine(Vector2D p1, Vector2D p2, int thickness, Color color) {
		this(p1, p2, false, false, false, thickness, null, color);
	}

	public EventLine(Vector2D p1, Vector2D p2, boolean extendable, Color color) {
		this(p1, p2, extendable, false, false, 1, null, color);
	}

	public EventLine(Vector2D p1, Vector2D p2, boolean extendable, int thickness, Color color) {
		this(p1, p2, extendable, false, false, thickness, null, color);
	}

	protected Vector2D getP1() {
		return p1;
	}

	protected Vector2D getP2() {
		return p2;
	}

	@Override
	public void draw(Graphics2D g2, Vector2D maxBounds, Vector2D minBounds) {
		Stroke defaultStroke = g2.getStroke();

		g2.setColor(getColor());
		Stroke stroke = new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
		if (isDashed)
			stroke = new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
		g2.setStroke(stroke);

		Vector2D tp1 = getTransform().transform(p1);
		Vector2D tp2 = getTransform().transform(p2);

		if ((tp1.getX() < maxBounds.getX() && tp1.getX() > minBounds.getX()
				|| tp2.getX() < maxBounds.getX() && tp2.getX() > minBounds.getX())
				&& (tp1.getY() < maxBounds.getY() && tp1.getY() > minBounds.getY()
						|| tp2.getY() < maxBounds.getY() && tp2.getY() > minBounds.getY())) {
			if (extendable) {
				double dx = (tp2.getX() - tp1.getX());
				double dy = (tp2.getY() - tp1.getY());
				double ratio = 1;
				if (dx > 0 && tp2.getX() < maxBounds.getX())
					ratio = ((maxBounds.getX() - tp1.getX()) / dx);
				if (dx < 0 && tp2.getX() > minBounds.getX())
					ratio = ((minBounds.getX() - tp1.getX()) / dx);
				if (dy > 0 && tp2.getY() < maxBounds.getY())
					ratio = ((maxBounds.getY() - tp1.getY()) / dy);
				if (dy < 0 && tp2.getY() > minBounds.getY())
					ratio = ((minBounds.getY() - tp1.getY()) / dy);
				tp2 = new Vector2D(tp1.getX() + dx * ratio, tp1.getY() + dy * ratio);

			}
			double dx = (tp2.getX() - tp1.getX());
			double dy = (tp2.getY() - tp1.getY());
			double ratio = 1;
			if (dx > 0 && tp2.getX() > maxBounds.getX())
				ratio = ((maxBounds.getX() - tp1.getX()) / dx);
			if (dx < 0 && tp2.getX() < minBounds.getX())
				ratio = ((minBounds.getX() - tp1.getX()) / dx);
			if (dy > 0 && tp2.getY() > maxBounds.getY())
				ratio = ((maxBounds.getY() - tp1.getY()) / dy);
			if (dy < 0 && tp2.getY() < minBounds.getY())
				ratio = ((minBounds.getY() - tp1.getY()) / dy);
			tp2 = new Vector2D(tp1.getX() + dx * ratio, tp1.getY() + dy * ratio);

			g2.drawLine((int) tp1.getX(), (int) tp1.getY(), (int) tp2.getX(), (int) tp2.getY());

			if (label != null)
				g2.drawString(label, (int) tp2.getX() + 5, (int) tp2.getY() + 5);

			if (isArrow) {
				double x1 = tp1.getX();
				double y1 = tp1.getY();
				double x2 = tp2.getX();
				double y2 = tp2.getY();

				double ddx = x2 - x1;
				double ddy = y2 - y1;
				double len = Math.sqrt(ddx * ddx + ddy * ddy);
				double xm = len - D;
				double xn = xm;
				double ym = H;
				double yn = -H;
				double x;
				double sin = ddy / len;
				double cos = ddx / len;

				x = xm * cos - ym * sin + x1;
				ym = xm * sin + ym * cos + y1;
				xm = x;

				x = xn * cos - yn * sin + x1;
				yn = xn * sin + yn * cos + y1;
				xn = x;

				int[] xpoints = { (int) x2, (int) xm, (int) xn };
				int[] ypoints = { (int) y2, (int) ym, (int) yn };

				g2.fillPolygon(xpoints, ypoints, 3);
			}
		}
		g2.setStroke(defaultStroke);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
