package relativity.ui.event;

import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import relativity.AbstractEvent;
import relativity.MinkowskiDiagram;
import relativity.message.Broker;
import relativity.message.Message;
import relativity.message.Subscriber;
import relativity.message.Type;
import relativity.ui.AbstractSpaceTimePanel;
import relativity.ui.DistanceEditor;
import relativity.ui.TimeEditor;

public class EventEditorPanel extends AbstractSpaceTimePanel {

	private static final long serialVersionUID = 1836441798606470281L;
	private AbstractEvent event;

	public EventEditorPanel(MinkowskiDiagram diagram, AbstractEvent event, Broker broker) {
		super(broker);
		setDiagram(diagram);
		setLayout(new GridBagLayout());
		this.event = event;
		setBackground(EDITOR_COLOR);

		update();
	}

	@Override
	protected void update() {
		removeAll();
		int row = 0;
		JTextField field = new JTextField(20);
		field.setText(event.getLabel());
		JButton deleteButton = new JButton("Delete");
		deleteButton.addActionListener(actionEvent -> {
			if (diagram != null && diagram.getS() != null) {
				diagram.getS().removeEvent(event);
				unsubscribe();
				broker.publish(new Message(Type.EVENT_DELETED, null));
			}
		});
		field.addActionListener(actionEvent -> {
			event.setLabel(field.getText());
			broker.publish(new Message(relativity.message.Type.EVENT_CHANGED, null));
		});

		addComponent(new JLabel("Label"), 0, row);
		addComponent(field, 1, row);
		addComponent(deleteButton, 2, row++);

		addChildSubscriber((Subscriber)(new DistanceEditor( event.getD(), diagram, broker ) {
			@Override
			public void update( Double value ) {
				event.setD( value );
				broker.publish(new Message(relativity.message.Type.EVENT_CHANGED, null));
			}	
		}.layout(this, row++) ) );

		addChildSubscriber((Subscriber)(new TimeEditor( event.getCt(), diagram, broker ) {
			@Override
			public void update( Double value ) {
				event.setCt( value );
				broker.publish(new Message(relativity.message.Type.EVENT_CHANGED, null));
			}	
		}.layout(this, row) ) );

		revalidate();
		repaint();
	}

}
