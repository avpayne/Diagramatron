package relativity.ui.event;

import java.awt.GridBagLayout;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import relativity.MinkowskiDiagram;
import relativity.WorldLine;
import relativity.WorldLineEvent;
import relativity.WorldLineEvent.Anchor;
import relativity.message.Broker;
import relativity.message.Message;
import relativity.message.Subscriber;
import relativity.message.Type;
import relativity.ui.AbstractSpaceTimePanel;
import relativity.ui.DistanceEditor;
import relativity.ui.TimeEditor;

public class WorldLineEventEditorPanel extends AbstractSpaceTimePanel {

	private static final long serialVersionUID = 1836441798606470281L;
	private WorldLineEvent event;

	public WorldLineEventEditorPanel(MinkowskiDiagram diagram, WorldLineEvent event, Broker broker) {
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

		JComboBox<WorldLine> worldLineSelector = new JComboBox<>();
		List<WorldLine> worldLines = new LinkedList<>();
		for( WorldLine w: diagram.getWorldLineCandidates(event)){
			worldLines.add(w);
			worldLineSelector.addItem(w);
		}
		addComponent(new JLabel("Worldline:"), 0, row);
		addComponent(worldLineSelector, 1, row++);
		if( worldLines.contains(event.getWorldLine()))
			worldLineSelector.setSelectedItem(event.getWorldLine());
		else if( !worldLines.isEmpty())
			event.setWorldLine(worldLines.get(0));
		worldLineSelector.addActionListener(actionEvent->{
			event.setWorldLine((WorldLine)worldLineSelector.getSelectedItem());
			broker.publish(new Message(relativity.message.Type.EVENT_CHANGED, null));
		});

		JComboBox<?> anchorSelector = new JComboBox<>( WorldLineEvent.Anchor.values() );
		anchorSelector.setSelectedItem( event.getAnchor() );
		addComponent(new JLabel("Anchor:"), 0, row);
		addComponent(anchorSelector, 1, row++);
		anchorSelector.addActionListener(actionEvent->{
			event.setAnchor( (Anchor)anchorSelector.getSelectedItem() );
			update();
			broker.publish(new Message(relativity.message.Type.EVENT_CHANGED, null));
		});
		
		if( event.getAnchor()==Anchor.SPACE )
		{
			addChildSubscriber((Subscriber)(new DistanceEditor( event.getD(), diagram, broker ) {
				@Override
				public void update( Double value ) {
					event.setD( value );
					broker.publish(new Message(relativity.message.Type.EVENT_CHANGED, null));
				}	
			}.layout(this, row++) ) );
		}

		if( event.getAnchor()==Anchor.TIME )
		{
			addChildSubscriber((Subscriber)(new TimeEditor( event.getCt(), diagram, broker ) {
				@Override
				public void update( Double value ) {
					event.setCt( value );
					broker.publish(new Message(relativity.message.Type.EVENT_CHANGED, null));
				}	
			}.layout(this, row) ) );
		}
		

		revalidate();
		repaint();
	}

}
