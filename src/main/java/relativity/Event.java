package relativity;

import java.awt.Color;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Event extends AbstractEvent {

	private static final long serialVersionUID = 2728189134469811115L;

	public Event(Double d, Double ct, String label, Color color) {
		super(d, ct, label, color);
	}

	public Event(Vector2D point, String label, Color color) {
		super(point, label, color);
	}
	
	public List<DrawableElement> getDrawableElements(List<DrawableElement> elements, FrameOfReference r, FrameOfReference s) {
		Vector2D x = r.getU(s, r.getDiagramScale(s)).scalarMultiply(getD());
		Vector2D y = r.getV(s, r.getDiagramScale(s)).scalarMultiply(getCt());
		elements.add(new EventLine(x, x.add(y), false, false, true, 1, null, s.getColor()));
		elements.add(new EventLine(y, y.add(x), false, false, true, 1, null, s.getColor()));
		for (FrameOfReference ss : s.getRelativeSpeedMap().keySet()) {
			Vector2D u = r.getU(ss, 1);
			Vector2D v = r.getV(ss, 1);

			double px = x.add(y).getX();
			double py = y.add(x).getY();
			double sv = (py - px * u.getY() / u.getX()) / (v.getY() - v.getX() * u.getY() / u.getX());
			double su = (px - sv * v.getX()) / u.getX();

			Vector2D xx = u.scalarMultiply(su);
			Vector2D yy = v.scalarMultiply(sv);
			elements.add(new EventLine(xx, xx.add(yy), false, false, true, 1, null, ss.getColor()));
			elements.add(new EventLine(yy, yy.add(xx), false, false, true, 1, null, ss.getColor()));

		}
		elements.add(new Event(x.add(y), this.getLabel(), this.getColor()));
		return elements;
	}
}
