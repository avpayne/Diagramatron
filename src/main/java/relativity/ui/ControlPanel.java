package relativity.ui;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

import relativity.message.Broker;
import relativity.message.Type;
import relativity.ui.event.EventPanel;
import relativity.ui.frame.FramePanel;
import relativity.ui.worldline.WorldLinePanel;

public class ControlPanel extends AbstractSpaceTimePanel {

	private static final long serialVersionUID = 1354259775395645016L;
	private final FramePanel frameControlPanel;
	private final HeaderControlPanel headerControlPanel;
	private final WorldLinePanel worldLinePanel;
	private final EventPanel eventPanel;
	private final DescriptionPanel descriptionPanel;
	private JTabbedPane tabbedPanel;

	public ControlPanel(Broker broker) {
		super(broker);
		setLayout(new BorderLayout());
		frameControlPanel = new FramePanel(broker);
		headerControlPanel = new HeaderControlPanel(broker);
		worldLinePanel = new WorldLinePanel(broker);
		eventPanel = new EventPanel(broker);
		descriptionPanel = new DescriptionPanel(broker);

		tabbedPanel = new JTabbedPane();
		frameControlPanel.revalidate();
		tabbedPanel.add("Frame", frameControlPanel);
		tabbedPanel.add("WorldLines", worldLinePanel);
		tabbedPanel.add("Events", eventPanel);

		broker.subscribe(Type.BASE_FRAME_CHANGED, this);
		broker.subscribe(Type.FRAME_ADDED, this);
		broker.subscribe(Type.FRAME_DELETED, this);
		broker.subscribe(Type.DIAGRAM_LOADED, this);
		broker.subscribe(Type.NEW_DIAGRAM, this);

		update();
	}

	@Override
	protected void update() {
		removeAll();
		add(headerControlPanel, BorderLayout.NORTH);
		add(tabbedPanel, BorderLayout.CENTER);
		if (diagram != null && diagram.getDescription() != null && diagram.getDescription().length() > 0)
			add(descriptionPanel, BorderLayout.SOUTH);

		frameControlPanel.setDiagram(diagram);
		headerControlPanel.setDiagram(diagram);
		worldLinePanel.setDiagram(diagram);
		eventPanel.setDiagram(diagram);
		descriptionPanel.setDiagram(diagram);

		revalidate();
		repaint();
	}

}
