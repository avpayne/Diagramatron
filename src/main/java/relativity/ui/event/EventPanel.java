package relativity.ui.event;

import java.awt.BorderLayout;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import relativity.AbstractEvent;
import relativity.Event;
import relativity.IntersectionEvent;
import relativity.MinkowskiDiagram;
import relativity.WorldLineEvent;
import relativity.WorldLineEvent.Anchor;
import relativity.message.Broker;
import relativity.message.Message;
import relativity.message.Type;
import relativity.ui.AbstractSpaceTimePanel;

public class EventPanel extends AbstractSpaceTimePanel {

	private static final long serialVersionUID = -6248072196901550580L;

	private Set<AbstractSpaceTimePanel> panels = new HashSet<>();
	private JPanel subPanel = new JPanel();
	private JButton addEventButton;
	private JButton addWorldLineEventButton;
	private JButton addIntersectionEventButton;

	public EventPanel(Broker broker) {
		super(broker);
		setLayout(new BorderLayout());

		broker.subscribe(Type.BASE_FRAME_CHANGED, this);
		broker.subscribe(Type.EVENT_ADDED, this);
		broker.subscribe(Type.EVENT_DELETED, this);
		broker.subscribe(Type.WORLD_LINE_ADDED, this);
		broker.subscribe(Type.WORLD_LINE_CHANGED, this);
		broker.subscribe(Type.WORLD_LINE_DELETED, this);

		Box box = Box.createHorizontalBox();
		add(box);
		addEventButton = new JButton("Add Event");
		box.add(addEventButton);
		addEventButton.addActionListener(actionEvent -> {
			if (diagram != null && diagram.getS() != null) {
				diagram.getS().addEvent(new Event(0d, 0d, MinkowskiDiagram.getNextEventName(), diagram.getS().getColor()));
				broker.publish(new Message(Type.EVENT_ADDED, null));
			}
		});

		addIntersectionEventButton = new JButton("Add Intersection");
		box.add(addIntersectionEventButton);
		addIntersectionEventButton.addActionListener(actionEvent -> {
			if (diagram != null && diagram.getS() != null) {
				diagram.getS().addEvent(new IntersectionEvent(null, null, MinkowskiDiagram.getNextEventName(), diagram.getS().getColor()));
				broker.publish(new Message(Type.EVENT_ADDED, null));
			}
		});

		addWorldLineEventButton = new JButton("WorldLine Event");
		box.add(addWorldLineEventButton);
		addWorldLineEventButton.addActionListener(actionEvent -> {
			if (diagram != null && diagram.getS() != null) {
				diagram.getS().addEvent(new WorldLineEvent(Anchor.TIME, null, 0d, 0d, MinkowskiDiagram.getNextEventName(), diagram.getS().getColor()));
				broker.publish(new Message(Type.EVENT_ADDED, null));
			}
		});

		add(new JScrollPane(subPanel), BorderLayout.CENTER);
		add(box, BorderLayout.NORTH);

		update();
	}

	@Override
	protected void update() {
		for (AbstractSpaceTimePanel panel : panels)
			panel.unsubscribe();
		panels.clear();
		subPanel.removeAll();

		addIntersectionEventButton.setEnabled(diagram != null && !diagram.getS().getWorldLines().isEmpty() && diagram.getWorldLines().size() > 1);
		addWorldLineEventButton.setEnabled(diagram != null && !diagram.getS().getWorldLines().isEmpty());

		Box box = Box.createVerticalBox();
		if (diagram != null && diagram.getS() != null)
			for (AbstractEvent abstractEvent : diagram.getS().getEvents()) {
				if (abstractEvent instanceof Event event) {
					AbstractSpaceTimePanel panel = new EventEditorPanel(diagram, event, broker);
					panels.add(panel);
					box.add(panel);
					box.add(Box.createVerticalStrut(10));
				} else if (abstractEvent instanceof IntersectionEvent event) {
					AbstractSpaceTimePanel panel = new IntersectionEventEditorPanel(diagram, event, broker);
					panels.add(panel);
					box.add(panel);
					box.add(Box.createVerticalStrut(10));
				} else if (abstractEvent instanceof WorldLineEvent event) {
					AbstractSpaceTimePanel panel = new WorldLineEventEditorPanel(diagram, event, broker);
					panels.add(panel);
					box.add(panel);
					box.add(Box.createVerticalStrut(10));
				}
			}

		subPanel.add(box);
		revalidate();
		repaint();
	}

}
