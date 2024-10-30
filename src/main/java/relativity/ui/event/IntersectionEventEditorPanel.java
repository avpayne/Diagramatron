package relativity.ui.event;

import java.awt.GridBagLayout;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import relativity.IntersectionEvent;
import relativity.MinkowskiDiagram;
import relativity.WorldLine;
import relativity.message.Broker;
import relativity.message.Message;
import relativity.message.Type;
import relativity.ui.AbstractSpaceTimePanel;

public class IntersectionEventEditorPanel extends AbstractSpaceTimePanel {

	private static final long serialVersionUID = 1836441798606470281L;
	private IntersectionEvent event;

	public IntersectionEventEditorPanel(MinkowskiDiagram diagram, IntersectionEvent event, Broker broker) {
		super(broker);
		setDiagram(diagram);
		setLayout(new GridBagLayout());
		this.event = event;
		setBackground(EDITOR_COLOR);
		broker.subscribe(Type.EVENT_ADDED, this);
		broker.subscribe(Type.EVENT_CHANGED, this);
		broker.subscribe(Type.EVENT_DELETED, this);

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

		JComboBox<WorldLine> worldLine1Selector = new JComboBox<>();
		JComboBox<WorldLine> worldLine2Selector = new JComboBox<>();
		Set<WorldLine> worldLines = new HashSet<>();
		for( WorldLine w: diagram.getWorldLineCandidates(event))
			worldLine1Selector.addItem(w);
		for( WorldLine w: diagram.getWorldLines())
		{
			worldLines.add(w);
			worldLine2Selector.addItem(w);
		}
		
		addComponent(new JLabel("Worldline 1:"), 0, row);
		addComponent(worldLine1Selector, 1, row++);
		addComponent(new JLabel("Worldline 2:"), 0, row);
		addComponent(worldLine2Selector, 1, row);
		if( worldLines.contains(event.getWorldLine1())) {
			worldLine1Selector.setSelectedItem(event.getWorldLine1());
		} else if( !worldLines.isEmpty() ) {
			event.setWorldLine1( (WorldLine)worldLine1Selector.getSelectedItem() );
			broker.publish(new Message(relativity.message.Type.EVENT_CHANGED, null));
		}
		
		if( worldLines.contains(event.getWorldLine2())) {
			worldLine2Selector.setSelectedItem(event.getWorldLine2());
		} else if( !worldLines.isEmpty()){
			event.setWorldLine2( (WorldLine)worldLine2Selector.getSelectedItem() );
			broker.publish(new Message(relativity.message.Type.EVENT_CHANGED, null));
		}
		
		if(worldLines.size()>1)
			worldLine2Selector.removeItem( worldLine1Selector.getSelectedItem() );
		
		worldLine1Selector.addActionListener(actionEvent->{
			event.setWorldLine1( (WorldLine)worldLine1Selector.getSelectedItem() );
			broker.publish(new Message(relativity.message.Type.EVENT_CHANGED, null));
		});
		worldLine2Selector.addActionListener(actionEvent->{
			event.setWorldLine2( (WorldLine)worldLine2Selector.getSelectedItem() );
			broker.publish(new Message(relativity.message.Type.EVENT_CHANGED, null));
		});

		revalidate();
		repaint();
	}

}
