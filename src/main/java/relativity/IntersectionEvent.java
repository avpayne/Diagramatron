package relativity;

import java.awt.Color;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class IntersectionEvent extends AbstractEvent {

	private static final long serialVersionUID = -9127549558470234696L;
	private WorldLine worldLine1;
	private WorldLine worldLine2;

	public IntersectionEvent(WorldLine worldLine1, WorldLine worldLine2, String label, Color color) {
		super(0d, 0d, label, color);
		this.worldLine1 = worldLine1;
		this.worldLine2 = worldLine2;
	}

	public boolean isOnWorldLine(WorldLine worldLine, FrameOfReference s, FrameOfReference ss) {
		Double highCt = worldLine.isFinite() ? worldLine.getEndEvent().getCt() : Double.POSITIVE_INFINITY;
		Double lowCt = worldLine.getStartEvent().getCt();

		Double highD = worldLine.isFinite() ? worldLine.getEndEvent().getD() : Double.POSITIVE_INFINITY;
		Double lowD = worldLine.getStartEvent().getD();
		if( worldLine.getV()<0 ) {
			highD = worldLine.getStartEvent().getD();
			lowD = worldLine.isFinite() ? worldLine.getEndEvent().getD() : Double.NEGATIVE_INFINITY;
		}
		
		return (getCt() > lowCt && getCt() < highCt && getD() > lowD && getD() < highD);
	}

	@Override
	public List<DrawableElement> getDrawableElements(List<DrawableElement> elements, FrameOfReference r, FrameOfReference s) {

		if (worldLine1 == null || worldLine2 == null)
			return elements;

		boolean drawable = false;
		if (s.getWorldLines().contains(worldLine1)) {
			Vector2D w1p1 = new Vector2D( worldLine1.getStartEvent().getD(), worldLine1.getStartEvent().getCt());
			Vector2D w1p2 = new Vector2D( worldLine1.getStartEvent().getD() + worldLine1.getV(), worldLine1.getStartEvent().getCt() + 1d);

			for (FrameOfReference ss : s.getRelativeSpeedMap().keySet()) {
				if (ss.getWorldLines().contains(worldLine2)) {
					Vector2D u1 = s.getU(ss, s.getScale(ss) * worldLine2.getStartEvent().getD());
					Vector2D v1 = s.getV(ss, s.getScale(ss) * worldLine2.getStartEvent().getCt());
					Vector2D u2 = s.getU(ss, s.getScale(ss) * worldLine2.getStartEvent().getD() + worldLine2.getV());
					Vector2D v2 = s.getV(ss, s.getScale(ss) * worldLine2.getStartEvent().getCt() + 1d);
					Vector2D w2p1 = u1.add(v1);
					Vector2D w2p2 = u2.add(v2);

					Vector2D intersection = intersection(w1p1, w1p2, w2p1, w2p2);
					
					setD(intersection.getX());
					setCt(intersection.getY());
//					if( isOnWorldLine(worldLine1, s, s) && isOnWorldLine(worldLine2, s, ss) )
						drawable = true;
				}
			}
		}

		if( drawable )
		{
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
		}
		return elements;
	}

	private Vector2D intersection(Vector2D aStart, Vector2D aEnd, Vector2D bStart, Vector2D bEnd) {
		// Line AB represented as a1x + b1y = c1
		double a1 = aEnd.getY() - aStart.getY();
		double b1 = aStart.getX() - aEnd.getX();
		double c1 = a1 * (aStart.getX()) + b1 * (aStart.getY());

		// Line CD represented as a2x + b2y = c2
		double a2 = bEnd.getY() - bStart.getY();
		double b2 = bStart.getX() - bEnd.getX();
		double c2 = a2 * (bStart.getX()) + b2 * (bStart.getY());

		double determinant = a1 * b2 - a2 * b1;

		if (determinant == 0) {
			// The lines are parallel. This is simplified
			// by returning a pair of FLT_MAX
			return new Vector2D(Double.MAX_VALUE, Double.MAX_VALUE);
		} else {
			double x = (b2 * c1 - b1 * c2) / determinant;
			double y = (a1 * c2 - a2 * c1) / determinant;
			return new Vector2D(x, y);
		}
	}

	public WorldLine getWorldLine1() {
		return worldLine1;
	}

	public void setWorldLine1(WorldLine worldLine1) {
		this.worldLine1 = worldLine1;
	}

	public WorldLine getWorldLine2() {
		return worldLine2;
	}

	public void setWorldLine2(WorldLine worldLine2) {
		this.worldLine2 = worldLine2;
	}

}
