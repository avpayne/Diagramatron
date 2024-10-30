package relativity.ui.frame;

import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JTextField;

import relativity.FrameOfReference;
import relativity.MinkowskiDiagram;
import relativity.message.Broker;
import relativity.message.Message;
import relativity.message.Type;
import relativity.ui.AbstractSpaceTimePanel;
import relativity.ui.NumberEditor;
import relativity.ui.NumberEditor.Range;

public class FrameEditorPanel extends AbstractSpaceTimePanel {

	private static final long serialVersionUID = 4369691769470679336L;
	private FrameOfReference s;
	private GammaEquationPanel gammaPanel;

	protected FrameEditorPanel(MinkowskiDiagram diagram, FrameOfReference s, Broker broker) {
		super(broker);
		setDiagram(diagram);
		setLayout(new GridBagLayout());
		this.s = s;
		setBackground(EDITOR_COLOR);

		update();
	}

	@Override
	protected void update() {
		removeAll();

		int row = 0;
		if (gammaPanel == null && diagram.getS() != null)
			gammaPanel = new GammaEquationPanel(diagram.getS(), s, broker);

		JTextField field = new JTextField(20);
		field.setText(s.getName());
		JButton deleteButton = new JButton("Delete");
		deleteButton.addActionListener(actionEvent -> {
			if (diagram != null && diagram.getS() != null) {
				diagram.removeFrame(s);
				unsubscribe();
				gammaPanel.unsubscribe();
				broker.publish(new Message(Type.FRAME_DELETED, null));
			}
		});

		addComponent(gammaPanel, 0, row, 2, 1);
		addComponent(deleteButton, 2, row++);
		
		new NumberEditor( s.getName(), diagram.getS().getSpeedRelativeTo(s), new Range(-1d, 1d), "c" ) {
			@Override
			public void update( Double value ) {
				diagram.getS().resetRelativeSpeed(s);
				diagram.getS().setRelativeSpeed(s, value);
				broker.publish(new Message(Type.FRAME_VELOCITY_CHANGED, null));
			}	
		}.layout(this, row);

		revalidate();
		repaint();
	}

	@Override
	public void unsubscribe() {
		if (gammaPanel != null)
			gammaPanel.unsubscribe();
		super.unsubscribe();
	}

}
