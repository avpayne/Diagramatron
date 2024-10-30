package relativity.ui.frame;

import java.awt.BorderLayout;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import relativity.FrameOfReference;
import relativity.message.Broker;
import relativity.message.Message;
import relativity.message.Type;
import relativity.ui.AbstractSpaceTimePanel;

public class FramePanel extends AbstractSpaceTimePanel {

	private static final long serialVersionUID = 392478611200570638L;

	private Set<FrameEditorPanel> panels = new HashSet<>();
	private JPanel subPanel = new JPanel();

	public FramePanel(Broker broker) {
		super(broker);
		setLayout(new BorderLayout());

		broker.subscribe(Type.BASE_FRAME_CHANGED, this);
		broker.subscribe(Type.FRAME_ADDED, this);
		broker.subscribe(Type.FRAME_DELETED, this);

		JButton addButton = new JButton("Add Frame of Reference");
		add(addButton, BorderLayout.NORTH);
		addButton.addActionListener(actionEvent -> {
			if (diagram != null) {
				FrameOfReference s = new FrameOfReference();
				diagram.addFrame(s);
				diagram.getS().setRelativeSpeed(s, 0.5);
				broker.publish(new Message(Type.FRAME_ADDED, null));
			}
		});
		add(new JScrollPane(subPanel), BorderLayout.CENTER);

		update();
	}

	@Override
	protected void update() {
		for (FrameEditorPanel panel : panels)
			panel.unsubscribe();
		panels.clear();
		subPanel.removeAll();

		Box box = Box.createVerticalBox();
		if (diagram != null && diagram.getS() != null)
			for (FrameOfReference s : diagram.getS().getOtherFrames()) {
				FrameEditorPanel panel = new FrameEditorPanel(diagram, s, broker);
				panels.add(panel);
				box.add(panel);
				box.add(Box.createVerticalStrut(10));
			}

		subPanel.add(box);
		revalidate();
		repaint();

	}

}
