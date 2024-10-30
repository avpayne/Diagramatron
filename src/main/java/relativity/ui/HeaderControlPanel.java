package relativity.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import relativity.FrameOfReference;
import relativity.message.Broker;
import relativity.message.Message;
import relativity.message.Type;

public class HeaderControlPanel extends AbstractSpaceTimePanel {

	private static final long serialVersionUID = 6345597378222828317L;

	protected HeaderControlPanel(Broker broker) {
		super(broker);
		this.setLayout(new GridBagLayout());
		broker.subscribe(Type.BASE_FRAME_CHANGED, this);
		broker.subscribe(Type.FRAME_ADDED, this);
		broker.subscribe(Type.FRAME_DELETED, this);

		update();

	}

	@Override
	protected void update() {
		removeAll();

		JComboBox<FrameOfReference> frameSelector = new JComboBox<>();
		if (diagram != null) {
			for (FrameOfReference frame : diagram.getFramesOfReference())
				frameSelector.addItem(frame);
			frameSelector.setSelectedItem(diagram.getS());
		}

		frameSelector.addItemListener(itemEvent -> {
			if (diagram != null) {
				diagram.setS((FrameOfReference) itemEvent.getItem());
				broker.publish(new Message(Type.BASE_FRAME_CHANGED, itemEvent.getItem()));
			}
		});

		addComponent(new JLabel(" Selected Frame of Reference"), 0, 0, 2, 1, GridBagConstraints.EAST);
		addComponent(frameSelector, 2, 0, 1, 1, GridBagConstraints.WEST);

		revalidate();
		repaint();
	}

}
