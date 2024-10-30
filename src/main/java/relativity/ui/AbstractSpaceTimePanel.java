package relativity.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

import relativity.MinkowskiDiagram;
import relativity.message.Broker;
import relativity.message.Message;
import relativity.message.Subscriber;
import relativity.message.Type;

public abstract class AbstractSpaceTimePanel extends JPanel implements Subscriber {

	private static final long serialVersionUID = 4746196742355709629L;
	protected static final Color EDITOR_COLOR = new Color(220, 220, 220);
	protected static final DecimalFormat FORMATTER = new DecimalFormat("#0.000");

	protected MinkowskiDiagram diagram;
	protected final transient Broker broker;
	protected transient Set<Subscriber> childSubscribers = new HashSet<>();

	protected AbstractSpaceTimePanel(Broker broker) {
		this.broker = broker;
		broker.subscribe(Type.DIAGRAM_LOADED, this);
		broker.subscribe(Type.NEW_DIAGRAM, this);
	}

	public void setDiagram(MinkowskiDiagram diagram) {
		this.diagram = diagram;
	}

	public void unsubscribe() {
		broker.unsubscribe(this);
		for( Subscriber subscriber: childSubscribers )
			broker.unsubscribe(subscriber);
		childSubscribers.clear();
	}

	public void handleMessage(Message message) {
		if (message.type() == Type.DIAGRAM_LOADED || message.type() == Type.NEW_DIAGRAM)
			diagram = (MinkowskiDiagram) message.payload();
		update();
	}

	protected void addComponent(Component component, int x, int y, int width, int height) {
		addComponent(component, x, y, width, height, GridBagConstraints.CENTER);
	}

	protected void addComponent(Component component, int x, int y) {
		addComponent(component, x, y, 1, 1, GridBagConstraints.CENTER);
	}

	protected void addComponent(Component component, int x, int y, int width, int height, int alignment) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.anchor = alignment;
		add(component, gbc);
	}

	protected void addComponent(Component component, int x, int y, int alignment) {
		addComponent(component, x, y, 1, 1, alignment);
	}

	protected abstract void update();
	
	protected void addChildSubscriber( Subscriber subscriber )
	{
		childSubscribers.add(subscriber);
	}

}
