package relativity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import relativity.Annotation.AnnotationPosition;

public class WorldLine extends DrawableElement implements Serializable {

	private static final long serialVersionUID = 2187642114877818613L;
	private String label;
	private Double deltaCt;
	private Double v;
	private final Event startEvent;
	private final Event endEvent;
	private AnnotationPosition annotationPosition = AnnotationPosition.RIGHT;

	public WorldLine(Event startEvent, double v, String label, Color color) {
		super(color);
		this.v = Math.clamp(v, -1d, 1d);
		this.deltaCt = null;
		this.startEvent = startEvent;
		this.endEvent = null;
		this.label = label;
	}

	public WorldLine(Event startEvent, double v, double deltaCt, String label, Color color) {
		super(color);
		this.v = Math.clamp(v, -1d, 1d);
		this.deltaCt = deltaCt;
		this.startEvent = startEvent;
		this.label = label;
		this.endEvent = new Event(startEvent.getD() + this.v * deltaCt, startEvent.getCt() + deltaCt, null, color);
	}

	public Double getV() {
		return v;
	}
	
	public boolean isFinite()
	{
		return endEvent!=null;
	}

	public void setAnnotationPosition(AnnotationPosition annotationPosition) {
		this.annotationPosition = annotationPosition;
	}

	public void setV(double v) {
		this.v = Math.clamp(v, -1d, 1d);
		if (endEvent != null)
			endEvent.setD(startEvent.getD() + this.v * deltaCt);
	}

	public Double getDeltaCt() {
		return deltaCt;
	}

	public void setDeltaCt(double deltaCt) {
		this.deltaCt = deltaCt;
		this.endEvent.setD(startEvent.getD() + v * deltaCt);
		this.endEvent.setCt(startEvent.getCt() + deltaCt);
	}

	public void setD(double d) {
		startEvent.setD(d);
		if (endEvent != null)
			endEvent.setD(startEvent.getD() + v * deltaCt);
	}

	public double getD() {
		return startEvent.getD();
	}

	public void setCt(double ct) {
		startEvent.setCt(ct);
		if (endEvent != null)
			endEvent.setCt(startEvent.getCt() + deltaCt);
	}

	public double getCt() {
		return startEvent.getCt();
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
	public double getSpaceTimeInterval()
	{
		return Math.pow(getDeltaCt(),2)-Math.pow(getDeltaD(),2);
	}

	@Override
	public void draw(Graphics2D g2, Vector2D maxBounds, Vector2D minBounds) {
		startEvent.draw(g2, maxBounds, minBounds);
		if (endEvent != null)
			endEvent.draw(g2, maxBounds, minBounds);
	}

	public void getDrawableElements(List<DrawableElement> elements, FrameOfReference r, FrameOfReference s) {
		startEvent.getDrawableElements(elements, r, s);
		if (endEvent != null)
			endEvent.getDrawableElements(elements, r, s);

		Vector2D x1 = r.getU(s, r.getDiagramScale(s)).scalarMultiply(startEvent.getD());
		Vector2D y1 = r.getV(s, r.getDiagramScale(s)).scalarMultiply(startEvent.getCt());

		Color color = getColor();
		boolean isDashed = false;
		if (v > 0.99999 || v < -0.99999) {
			color = Color.red;
			isDashed = true;
		}

		if (endEvent != null) {
			Vector2D x2 = r.getU(s, r.getDiagramScale(s)).scalarMultiply(endEvent.getD());
			Vector2D y2 = r.getV(s, r.getDiagramScale(s)).scalarMultiply(endEvent.getCt());
			elements.add(new EventLine(x1.add(y1), x2.add(y2), false, false, isDashed, 3, null, color));
			Vector2D start = x1.add(y1);
			Vector2D end = x2.add(y2);
			Vector2D delta = end.add(start.negate()).scalarMultiply(0.5);
			Vector2D midPoint = start.add(delta);
			if (label != null)
				elements.add(new Annotation(midPoint, label, annotationPosition, color));
		} else {
			AbstractEvent event = new Event(startEvent.getD() + v, startEvent.getCt() + 1d, null, color);

			Vector2D x2 = r.getU(s, r.getDiagramScale(s)).scalarMultiply(event.getD());
			Vector2D y2 = r.getV(s, r.getDiagramScale(s)).scalarMultiply(event.getCt());
			elements.add(new EventLine(x1.add(y1), x2.add(y2), true, true, isDashed, 3, null, color));
			Vector2D start = x1.add(y1);
			Vector2D end = x2.add(y2);
			Vector2D delta = end.add(start.negate()).scalarMultiply(0.5);
			Vector2D midPoint = start.add(delta);
			if (label != null)
				elements.add(new Annotation(midPoint, label, annotationPosition, color));
		}
	}

	public double getDeltaD() {
		if (deltaCt != null)
			return endEvent.getD() - startEvent.getD();
		return Double.NaN;
	}
	
	@Override
	public String toString()
	{
		return label;
	}
	
	@Override
	public int hashCode()
	{
		return label.hashCode();
	}
	
	public boolean equals( Object o )
	{
		if( o instanceof WorldLine worldLine )
			return worldLine.label.equals(label);
		return false;
	}

	public Event getStartEvent() {
		return startEvent;
	}

	public Double getCtIntercept() {
		return startEvent.getCt()-getV()*startEvent.getD();
	}
	
	public Event getEndEvent() {
		return endEvent;
	}
	
	
}
