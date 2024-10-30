package relativity.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import relativity.DrawableElement;
import relativity.FrameOfReference;
import relativity.MinkowskiDiagram;
import relativity.message.Broker;
import relativity.message.Message;
import relativity.message.Type;
import relativity.transform.Scale;
import relativity.transform.Translation;

public class SpaceTimePanel extends AbstractSpaceTimePanel {

	private static final long serialVersionUID = 240884096703714173L;
	private static final int BUFFER = 40;
	private int xStart = 0;
	private int yStart = 0;
	private transient BufferedImage backgroundImage = null;

	public SpaceTimePanel(Broker broker) {
		super(broker);

		setPreferredSize(new Dimension(1000, 800));
		setDiagram(diagram);

		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				xStart = e.getX();
				yStart = e.getY();
			}

		});
		addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				int dx = e.getX() - xStart;
				int dy = e.getY() - yStart;
				diagram.setXOffset(diagram.getXOffset() + dx/diagram.getScale());
				diagram.setYOffset(diagram.getYOffset() - dy/diagram.getScale());
				xStart = e.getX();
				yStart = e.getY();
				writeScaledLimits();
				broker.publish(new Message( Type.OFFSET_CHANGED, null ) );
			}

		});
		addMouseWheelListener(mouseEvent -> {
			double d = mouseEvent.getPreciseWheelRotation();
			diagram.setScale(Math.max(1, diagram.getScale() + d));
			writeScaledLimits();
			broker.publish(new Message( Type.ZOOM_CHANGED, null ) );
		});

		broker.subscribe(Type.ALL, this);
		
		addComponentListener( new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				writeScaledLimits();
				broker.publish(new Message(Type.WINDOW_CHANGED, null));
			}
			
		});
	}

	private void writeScaledLimits()
	{
		if( diagram!=null ) {
			double scaledWidth = (getWidth()-4*BUFFER)/diagram.getScale();
			double scaledXStart = -scaledWidth/2+diagram.getXOffset();
			double scaledXEnd = scaledWidth/2+diagram.getXOffset();
			double scaledHeight = (getHeight()-4*BUFFER)/diagram.getScale();
			double scaledYStart = -scaledHeight/2+diagram.getYOffset();
			double scaledYEnd = scaledHeight/2+diagram.getYOffset();
			diagram.setScaledLimits( scaledXStart, scaledXEnd, scaledYStart, scaledYEnd );
		}
	}
	
	private DrawableElement addLocalTransforms(DrawableElement element) {
		element.addElement(new Scale(1, -1));
		int xOffset = diagram != null ? (int)(diagram.getXOffset()*diagram.getScale()) : 0;
		int yOffset = diagram != null ? (int)(diagram.getYOffset()*diagram.getScale()) : 0;
		element.addElement(new Translation(getSize().width / 2d + xOffset, getSize().height / 2d - yOffset));
		return element;
	}

	private void paintBackground(Graphics2D g2) {
		if (backgroundImage != null)
			g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), 0, 0, backgroundImage.getWidth(),
					backgroundImage.getHeight(), null);
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setColor(Color.white);
		g2.fillRect(0, 0, getSize().width, getSize().height);
		paintBackground(g2);

		for (DrawableElement component : getDrawableComponents())
			component.draw(g2, new Vector2D(getSize().width - (double) BUFFER, getSize().height - (double) BUFFER),
					new Vector2D(BUFFER, BUFFER));
	}

	private List<DrawableElement> getDrawableComponents() {
		List<DrawableElement> components = new LinkedList<>();
		if (diagram != null && diagram.getS() != null) {
			for (DrawableElement element : diagram.getS().getBackgroundDrawableElements())
				components.add(addLocalTransforms(element));
			for (DrawableElement element : diagram.getS().getForegroundDrawableElements())
				components.add(addLocalTransforms(element));
		}

		return components;
	}

	public List<FrameOfReference> getFramesOfReference() {
		if (diagram != null)
			return diagram.getFramesOfReference();
		return new LinkedList<>();
	}

	public FrameOfReference getS() {
		if (diagram != null)
			return diagram.getS();
		return null;
	}

	public void setS(FrameOfReference s) {
		if (diagram != null)
			diagram.setS(s);
	}

	public MinkowskiDiagram getDiagram() {
		return diagram;
	}

	@Override
	public void handleMessage(Message message) {
		writeScaledLimits();
		super.handleMessage(message);
		if (message.type() == Type.BACKGROUND_UPDATED)
			backgroundImage = (BufferedImage) message.payload();
		revalidate();
		repaint();
	}

	@Override
	protected void update() {
		//nothing to do
	}
	
	

}
