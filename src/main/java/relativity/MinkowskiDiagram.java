package relativity;

import java.awt.Color;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

public class MinkowskiDiagram implements Serializable {

	public record Reference( String reference, boolean isUrl ) implements Serializable {}
	
	private static final long serialVersionUID = 1590392865913486015L;
	private static final Color[] COLORS = new Color[] { Color.black, Color.blue, Color.green.darker(), Color.magenta.darker(), Color.pink.darker() };
	private static Set<String> frameNameSet = new HashSet<>();
	private static Set<Color> colorSet = new HashSet<>();
	private static int worldLineNameCounter = 1;
	private static int eventNameCounter = 1;
	private static transient Random random = new Random();

	private final Map<String, FrameOfReference> frameOfReferenceMap = new TreeMap<>();
	private String s;
	private double scale = 50;
	private double xOffset = 0;
	private double yOffset = 0;
	private String description;
	private List<Reference> references;
	private transient boolean isDirty = false;
	
	private double scaledXStart;
	private double scaledXEnd;
	private double scaledYStart;
	private double scaledYEnd;
	

	public MinkowskiDiagram() {
		addFrame(new FrameOfReference());
		isDirty = false;
	}

	public FrameOfReference getS() {
		return frameOfReferenceMap.get(s);
	}

	public void setDiagramReferences() {
		for (FrameOfReference s1 : frameOfReferenceMap.values())
			s1.setDiagram(this);
	}
	
	public WorldLine getWorldLine( String worldLineId )
	{
		for( FrameOfReference frame: frameOfReferenceMap.values() )
			for( WorldLine worldLine: frame.getWorldLines() )
				if( worldLine.getLabel().equals( worldLineId ) )
					return worldLine;
		return null;
	}

	public List<WorldLine> getWorldLineCandidates( AbstractEvent event )
	{
		List<WorldLine> list = new LinkedList<>();
		FrameOfReference frame = null;
		for( FrameOfReference ss: getFramesOfReference())
			for( AbstractEvent e: ss.getEvents() )
				if( e==event)
					frame=ss;
		if( frame!=null )
			for( WorldLine worldLine: frame.getWorldLines() )
				list.add(worldLine);
		return list;
	}

	public List<WorldLine> getWorldLines()
	{
		List<WorldLine> list = new LinkedList<>();
		for( FrameOfReference frame: frameOfReferenceMap.values() )
			for( WorldLine worldLine: frame.getWorldLines() )
				list.add( worldLine );
		return list;
	}

	public FrameOfReference getFrameOfReferenceForWorldLine( String worldLineId )
	{
		for( FrameOfReference frame: frameOfReferenceMap.values() )
			for( WorldLine worldLine: frame.getWorldLines() )
				if( worldLine.getLabel().equals( worldLineId ) )
					return frame;
		return null;
	}

	public void setS(FrameOfReference s) {
		if (frameOfReferenceMap.containsKey(s.getName()))
			this.s = s.getName();
		else
			throw new IllegalArgumentException("Frame of Reference: " + s.getName() + " undefined");
		isDirty = true;
	}

	public FrameOfReference getFrame(String s) {
		return frameOfReferenceMap.get(s);
	}

	public void addFrame(FrameOfReference s) {
		frameOfReferenceMap.put(s.getName(), s);
		s.setDiagram(this);
		if (this.s == null)
			setS(s);
		isDirty = true;
	}

	public void removeFrame(FrameOfReference s) {
		frameOfReferenceMap.remove(s.getName());
		for (FrameOfReference s1 : frameOfReferenceMap.values())
			s1.removeRelativeFrame(s);

		frameNameSet.remove(s.getBaseName());
		colorSet.remove(s.getColor());
		isDirty = true;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
		isDirty = true;
	}

	public List<FrameOfReference> getFramesOfReference() {
		List<FrameOfReference> list = new LinkedList<>();
		list.addAll(frameOfReferenceMap.values());
		return list;
	}

	public double getXOffset() {
		return xOffset;
	}

	public void setXOffset(double xOffset) {
		this.xOffset = xOffset;
		isDirty = true;
	}

	public double getYOffset() {
		return yOffset;
	}

	public void setYOffset(double yOffset) {
		this.yOffset = yOffset;
		isDirty = true;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
		isDirty = true;
	}

	public List<Reference> getReferences() {
		return references;
	}

	public void addReference(Reference reference) {
		if (references == null)
			references = new LinkedList<>();
		references.add(reference);
		isDirty = true;
	}

	public static Color getNextColor() {
		Color color = COLORS[0];
		int i = 0;
		while (colorSet.contains(color)) {
			i++;
			if (i < COLORS.length)
				color = COLORS[i];
			else
				color = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
		}
		colorSet.add(color);
		return color;
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void clearDirty() {
		isDirty = false;
	}

	public static String getNextFrameName() {
		StringBuilder s = new StringBuilder("");

		while (frameNameSet.contains(s.toString()))
			s.append("'");

		frameNameSet.add(s.toString());
		return s.toString();
	}

	public static String getNextWorldLineName() {
		return "Worldline "+worldLineNameCounter++;
	}

	public static String getNextEventName() {
		return "Event "+eventNameCounter++;
	}
	
	public static void resetNameColor() {
		frameNameSet.clear();
		colorSet.clear();
	}

	public void setDirty() {
		isDirty=true;
	}

	public void setScaledLimits(double scaledXStart, double scaledXEnd, double scaledYStart, double scaledYEnd) {
		this.scaledXStart = scaledXStart;
		this.scaledXEnd = scaledXEnd;
		this.scaledYStart = scaledYStart;
		this.scaledYEnd = scaledYEnd;
	}

	public double getScaledXStart() {
		return scaledXStart;
	}

	public double getScaledXEnd() {
		return scaledXEnd;
	}

	public double getScaledYStart() {
		return scaledYStart;
	}

	public double getScaledYEnd() {
		return scaledYEnd;
	}

}
