package relativity.ui.worldline;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import relativity.WorldLine;
import relativity.message.Broker;
import relativity.message.Message;
import relativity.message.Subscriber;
import relativity.message.Type;
import relativity.ui.AbstractSpaceTimePanel;
import relativity.ui.DistanceEditor;
import relativity.ui.NumberEditor;
import relativity.ui.NumberEditor.Range;
import relativity.ui.TimeEditor;
import relativity.ui.VelocityEditor;

public class WorldLineEditorPanel extends AbstractSpaceTimePanel {

	private static final long serialVersionUID = -6480611861040362590L;
	private WorldLine worldLine;

	protected WorldLineEditorPanel(WorldLine worldLine, Broker broker) {
		super(broker);
		setBackground(EDITOR_COLOR);
		setLayout(new GridBagLayout());
		this.worldLine = worldLine;
		update();
	}

	@Override
	protected void update() {
		JLabel siValue = new JLabel("");

		removeAll();
		int row = 0;

		JLabel nameLabel = new JLabel("Name");
		JTextField nameValue = new JTextField(20);
		nameValue.setText(worldLine.getLabel());
		nameValue.addActionListener(actionEvent -> {
			worldLine.setLabel(nameValue.getText());
			broker.publish(new Message(relativity.message.Type.WORLD_LINE_CHANGED, null));
		});
		addComponent(nameLabel, 0, row, GridBagConstraints.WEST);
		addComponent(nameValue, 1, row);

		JButton deleteButton = new JButton("Delete");
		addComponent(deleteButton, 2, row++);
		deleteButton.addActionListener(actionEvent -> {
			if (diagram != null && diagram.getS() != null) {
				diagram.getS().removeWorldLine(worldLine);
				broker.publish(new Message(Type.WORLD_LINE_DELETED, null));
				unsubscribe();
			}
		});

		addChildSubscriber((Subscriber)(new DistanceEditor( worldLine.getD(), diagram, broker ) {
			@Override
			public void update( Double value ) {
				worldLine.setD( value );
				broker.publish(new Message(relativity.message.Type.EVENT_CHANGED, null));
			}	
		}.layout(this, row++) ) );
		
		addChildSubscriber((Subscriber)(new TimeEditor( worldLine.getCt(), diagram, broker ) {
			@Override
			public void update( Double value ) {
				worldLine.setCt( value );
				broker.publish(new Message(relativity.message.Type.EVENT_CHANGED, null));
			}	
		}.layout(this, row++) ) );
		
		
		new VelocityEditor( worldLine.getV() ) {
			@Override
			public void update( Double value ) {
				worldLine.setV( value );
				broker.publish(new Message(relativity.message.Type.EVENT_CHANGED, null));
			}	
		}.layout(this, row++);
		
		
		if (worldLine.getDeltaCt() != null) {
			new NumberEditor( "Delta ct", worldLine.getDeltaCt(), new Range(0d, 100d), "ly" ) {
				@Override
				public void update( Double value ) {
					worldLine.setDeltaCt( value );
					broker.publish(new Message(relativity.message.Type.EVENT_CHANGED, null));
				}	
			}.layout(this, row++);
			
			if( worldLine.isFinite() )
			{
				JLabel siLabel = new JLabel("Spacetime Interval ^2");
				siValue.setText(FORMATTER.format(worldLine.getSpaceTimeInterval()) + " ly^2");
				addComponent(siLabel, 0, row, 2, 1, GridBagConstraints.WEST);
				addComponent(siValue, 2, row);
			}

		}

	}

	@Override
	public void handleMessage(Message message) {
		super.handleMessage(message);
		update();
	}

}
