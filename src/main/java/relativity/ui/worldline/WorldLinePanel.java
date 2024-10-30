package relativity.ui.worldline;

import java.awt.BorderLayout;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import relativity.Event;
import relativity.MinkowskiDiagram;
import relativity.WorldLine;
import relativity.message.Broker;
import relativity.message.Message;
import relativity.message.Type;
import relativity.ui.AbstractSpaceTimePanel;

public class WorldLinePanel extends AbstractSpaceTimePanel {

	private static final long serialVersionUID = -7810053617724943390L;
	private final Set<WorldLineEditorPanel> panels = new HashSet<>();
	private final JPanel subPanel = new JPanel();

	public WorldLinePanel(Broker broker) {
		super(broker);
		setLayout(new BorderLayout());
		add(new JScrollPane(subPanel), BorderLayout.CENTER);
		broker.subscribe(Type.BASE_FRAME_CHANGED, this);
		broker.subscribe(Type.WORLD_LINE_ADDED, this);
		broker.subscribe(Type.WORLD_LINE_DELETED, this);

		Box box = Box.createHorizontalBox();
		add(box, BorderLayout.NORTH);
		JButton addButton1 = new JButton("Add (fixed)");
		box.add(addButton1);
		addButton1.addActionListener(actionEvent -> {
			if (diagram != null && diagram.getS() != null) {
				diagram.getS().addWorldLine(new WorldLine(new Event(0d, 0d, null, diagram.getS().getColor()),
								0.5, 1, MinkowskiDiagram.getNextWorldLineName(), diagram.getS().getColor()));
				broker.publish(new Message(Type.WORLD_LINE_ADDED, null));
			}
		});
		JButton addButton2 = new JButton("Add (open)");
		box.add(addButton2);
		addButton2.addActionListener(actionEvent -> {
			if (diagram != null && diagram.getS() != null) {
				diagram.getS().addWorldLine(new WorldLine(new Event(0d, 0d, null, diagram.getS().getColor()),
								0.5, MinkowskiDiagram.getNextWorldLineName(), diagram.getS().getColor()));
				broker.publish(new Message(Type.WORLD_LINE_ADDED, null));
			}
		});

		update();
	}

	@Override
	protected void update() {
		for (WorldLineEditorPanel panel : panels)
			panel.unsubscribe();
		panels.clear();
		subPanel.removeAll();

		Box box = Box.createVerticalBox();
		if (diagram != null && diagram.getS() != null)
			for (WorldLine worldLine : diagram.getS().getWorldLines()) {
				WorldLineEditorPanel panel = new WorldLineEditorPanel(worldLine, broker);
				panel.setDiagram(diagram);
				panels.add(panel);
				box.add(panel);
				box.add(Box.createVerticalStrut(10));
			}
		subPanel.add(box);
		revalidate();
		repaint();
	}

}
