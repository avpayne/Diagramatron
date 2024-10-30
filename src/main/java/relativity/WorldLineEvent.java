package relativity;

import java.awt.Color;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class WorldLineEvent extends AbstractEvent {

	public enum Anchor {
		TIME, SPACE
	}

	private static final long serialVersionUID = -8694487148479724759L;

	private WorldLine worldLine;
	private Anchor anchor;

	public WorldLineEvent(Anchor anchor, WorldLine worldLine, Double d, Double ct, String label, Color color) {
		super(d, ct, label, color);
		this.anchor = anchor;
		this.worldLine = worldLine;
	}

	public WorldLine getWorldLine() {
		return worldLine;
	}

	public void setWorldLine(WorldLine worldLine) {
		this.worldLine = worldLine;
	}

	public Anchor getAnchor() {
		return anchor;
	}

	public void setAnchor(Anchor anchor) {
		this.anchor = anchor;
	}

	public boolean isOnWorldLine()
	{
		if( anchor==Anchor.TIME ){
			Double high=worldLine.isFinite()?worldLine.getEndEvent().getCt():Double.MAX_VALUE;
			Double low=worldLine.getStartEvent().getCt();
			return getCt()>=low && getCt()<=high;
		} else {
			Double high=worldLine.getV()>0?(worldLine.isFinite()?worldLine.getEndEvent().getD():Double.POSITIVE_INFINITY):worldLine.getStartEvent().getD();
			Double low=worldLine.getV()<0?(worldLine.isFinite()?worldLine.getEndEvent().getD():Double.NEGATIVE_INFINITY):worldLine.getStartEvent().getD();
			return getD()>=low && getD()<=high;
		}
	}
	
	@Override
	public List<DrawableElement> getDrawableElements(List<DrawableElement> elements, FrameOfReference r, FrameOfReference s) {
		
		if( worldLine==null )
			return elements;
		
		if( isOnWorldLine() ){
		
			if( anchor==Anchor.TIME )
				setD(worldLine.getStartEvent().getD()+(getCt()-worldLine.getStartEvent().getCt())*worldLine.getV());
			if( anchor==Anchor.SPACE )
				setCt(worldLine.getStartEvent().getCt()+(getD()-worldLine.getStartEvent().getD())/worldLine.getV());
			
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

}
