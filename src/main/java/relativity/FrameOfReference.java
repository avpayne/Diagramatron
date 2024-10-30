package relativity;

import java.awt.Color;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class FrameOfReference implements Comparable<FrameOfReference>, Serializable {

	private static final long serialVersionUID = -751150688156709563L;

	private final String name;
	private final String color;
	private final Map<String, Double> relativeSpeedMap = new TreeMap<>();
	private List<AbstractEvent> eventList = new LinkedList<>();
	private List<WorldLine> worldLineList = new LinkedList<>();
	private transient MinkowskiDiagram diagram;

	public FrameOfReference() {
		Color nextColor = MinkowskiDiagram.getNextColor();
		this.color = String.format("#%02x%02x%02x", nextColor.getRed(), nextColor.getGreen(), nextColor.getBlue());
		name = MinkowskiDiagram.getNextFrameName();
	}

	public List<FrameOfReference> getOtherFrames() {
		List<FrameOfReference> frames = new LinkedList<>();
		for (String frameId : relativeSpeedMap.keySet())
			frames.add(diagram.getFrame(frameId));
		return frames;
	}

	public Color getColor() {
		return Color.decode(color);
	}

	public String getName() {
		return "S" + name;
	}

	public String getBaseName() {
		return name;
	}

	public Double getSpeedRelativeTo(FrameOfReference s) {
		return relativeSpeedMap.get(s.getName());
	}

	public void resetRelativeSpeed(FrameOfReference s) {
		for (FrameOfReference s1 : diagram.getFramesOfReference())
			for (FrameOfReference s2 : diagram.getFramesOfReference())
				if (s1 != this && s2 == s && s1 != s2) {
					s1.relativeSpeedMap.remove(s2.getName());
					s2.relativeSpeedMap.remove(s1.getName());
				}
	}

	public void setRelativeSpeed(FrameOfReference s, double v) {
		relativeSpeedMap.put(s.getName(), v);
		s.relativeSpeedMap.put(this.getName(), -v);

		for (FrameOfReference s1 : diagram.getFramesOfReference())
			for (FrameOfReference s2 : diagram.getFramesOfReference())
				if (s1 != this && s2 == s && s1 != s2 && !s1.relativeSpeedMap.containsKey(s2.getName())) {
					double v1 = relativeSpeedMap.get(s1.getName());
					double v2 = relativeSpeedMap.get(s2.getName());
					double v1p = (v1 - v2) / (1 - v1 * v2);
					s1.setRelativeSpeed(s2, v1p);
				}
	}

	public void removeRelativeFrame(FrameOfReference s) {
		relativeSpeedMap.remove(s.getName());
	}

	public List<DrawableElement> getForegroundDrawableElements() {
		List<DrawableElement> elements = new LinkedList<>();
		getFramedForegroundDrawableElements(elements, this);

		for (String s : relativeSpeedMap.keySet())
			getFramedForegroundDrawableElements(elements, diagram.getFrame(s));
		return elements;
	}

	public List<DrawableElement> getFramedForegroundDrawableElements(List<DrawableElement> elements, FrameOfReference s) {
		for (AbstractEvent event : s.eventList)
			event.getDrawableElements(elements, this, s);

		for (WorldLine worldLine : s.worldLineList)
			worldLine.getDrawableElements(elements, this, s);

		return elements;
	}

	public List<DrawableElement> getBackgroundDrawableElements() {
		List<DrawableElement> elements = new LinkedList<>();
		getFramedBackgroundDrawableElements(elements, this);

		for (String s : relativeSpeedMap.keySet())
			getFramedBackgroundDrawableElements(elements, diagram.getFrame(s));

		// Vector2D p1, Vector2D p2, Transform transform, boolean extendable, boolean
		// isArrow, String label, Color color
		elements.add(new EventLine(new Vector2D(0, 0), new Vector2D(1, 1), true, false, true, 2,
				"Lightline", Color.red));
		elements.add(new EventLine(new Vector2D(0, 0), new Vector2D(-1, 1), true, false, true, 2, null,
				Color.red));
		elements.add(new EventLine(new Vector2D(0, 0), new Vector2D(1, -1), true, false, true, 2, null,
				Color.red));
		elements.add(new EventLine(new Vector2D(0, 0), new Vector2D(-1, -1), true, false, true, 2,
				null, Color.red));

		return elements;
	}

	public List<DrawableElement> getFramedBackgroundDrawableElements(List<DrawableElement> elements,
			FrameOfReference s) {
		for (int i = 1; i < 500; i++) {
			int width = 0;
			if (i % 10 == 0 && diagram.getScale() >= 10)
				width = 1;
			if (i % 10 == 0 && diagram.getScale() < 10)
				width = 0;
			else if (i % 10 != 0 && diagram.getScale() < 10)
				width = -1;
			if (width >= 0) {
				elements.add(new EventLine(getV(s, getDiagramScale(s)).scalarMultiply(i),
						getV(s, getDiagramScale(s)).scalarMultiply(i).add(getU(s, getDiagramScale(s))), true, width,
						getBrighterColor(s.getColor())));
				elements.add(new EventLine(getU(s, getDiagramScale(s)).scalarMultiply(i),
						getU(s, getDiagramScale(s)).scalarMultiply(i).add(getV(s, getDiagramScale(s))), true, width,
						getBrighterColor(s.getColor())));
			}
		}

		elements.add(new EventLine(new Vector2D(0, 0), getU(s, getDiagramScale(s)), true, true, false, 2,
				"d" + s.name, s.getColor()));
		elements.add(new EventLine(new Vector2D(0, 0), getV(s, getDiagramScale(s)), true, true, false, 2,
				"ct" + s.name, s.getColor()));
		elements.add(new EventLine(new Vector2D(0, 0), getU(s, getDiagramScale(s)).negate(), true, true,
				false, 2, null, s.getColor()));
		elements.add(new EventLine(new Vector2D(0, 0), getV(s, getDiagramScale(s)).negate(), true, true,
				false, 2, null, s.getColor()));

		return elements;
	}

	private Color getBrighterColor(Color color) {
		return new Color(getBrighterValue(color.getRed()), getBrighterValue(color.getGreen()),
				getBrighterValue(color.getBlue()));
	}

	private int getBrighterValue(int value) {
		return (int) (value + (255 - value) * 0.85);
	}

	private double getTheta(FrameOfReference s) {
		if (s == this)
			return 0;
		return Math.atan(relativeSpeedMap.get(s.getName()));
	}

	public double getDiagramScale(FrameOfReference s) {
		if (s == this)
			return diagram.getScale();
		double x2 = 1d / (1d - Math.pow(Math.tan(getTheta(s)), 2));
		double ct2 = x2 - 1;
		return diagram.getScale() * Math.sqrt(x2 + ct2);

	}

	public double getScale(FrameOfReference s) {
		if (s == this)
			return 1d;
		double x2 = 1d / (1d - Math.pow(Math.tan(getTheta(s)), 2));
		double ct2 = x2 - 1;
		return Math.sqrt(x2 + ct2);

	}

	public Vector2D getU(FrameOfReference s, double scale) {
		return getN(new Vector2D(1, 0), getTheta(s)).scalarMultiply(scale);
	}

	public Vector2D getV(FrameOfReference s, double scale) {
		return getN(new Vector2D(0, 1), -1 * getTheta(s)).scalarMultiply(scale);
	}

	private Vector2D getN(Vector2D n, double theta) {
		n = n.normalize();
		return new Vector2D(n.getX() * Math.cos(theta) - n.getY() * Math.sin(theta),
				n.getY() * Math.cos(theta) + n.getX() * Math.sin(theta));
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof FrameOfReference s)
			return getName().equals(s.getName());
		return false;
	}

	@Override
	public int compareTo(FrameOfReference o) {
		return getName().compareTo(o.getName());
	}

	@Override
	public String toString() {
		return getName();
	}

	public void addEvent(AbstractEvent event) {
		eventList.add(event);
	}

	public void removeEvent(AbstractEvent event) {
		eventList.remove(event);
	}

	public List<AbstractEvent> getEvents() {
		return eventList;
	}

	public Map<FrameOfReference, Double> getRelativeSpeedMap() {
		Map<FrameOfReference, Double> map = new TreeMap<>();
		for (Entry<String, Double> entry : this.relativeSpeedMap.entrySet())
			map.put(diagram.getFrame(entry.getKey()), entry.getValue());
		return map;
	}

	public void addWorldLine(WorldLine worldLine) {
		worldLineList.add(worldLine);
	}

	public void removeWorldLine(WorldLine worldLine) {
		worldLineList.remove(worldLine);
	}

	public List<WorldLine> getWorldLines() {
		return worldLineList;
	}

	public void setDiagram(MinkowskiDiagram diagram) {
		this.diagram = diagram;
	}
	
}
