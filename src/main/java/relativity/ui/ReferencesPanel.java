package relativity.ui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import relativity.MinkowskiDiagram.Reference;
import relativity.message.Broker;
import relativity.message.Message;
import relativity.message.Type;

public class ReferencesPanel extends AbstractSpaceTimePanel {

	private static final long serialVersionUID = -6262707633856377951L;

	protected ReferencesPanel(Broker broker) {
		super(broker);
		setLayout(new BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
		update();
		broker.subscribe(Type.DIAGRAM_LOADED, this);
		broker.subscribe(Type.NEW_DIAGRAM, this);
		setBackground(Color.white);
		setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	@Override
	protected void update() {
		removeAll();
		int refCount=1;
		if (diagram != null && diagram.getReferences() != null && !diagram.getReferences().isEmpty()) {
			add(new JLabel("References:"));
			for (Reference ref : diagram.getReferences()) {
				JLabel reference = new JLabel("\u2023["+ refCount++ +"] "+ref.reference());
				reference.setBackground(Color.white);
				reference.setForeground(ref.isUrl()?Color.blue:Color.black);
				add(reference);
				if( ref.isUrl() )
					reference.addMouseListener(new MouseAdapter() {
	
						@Override
						public void mouseClicked(MouseEvent e) {
							openReference(ref.reference());
						}
	
						@Override
						public void mouseEntered(MouseEvent e) {
							reference.setForeground(Color.blue.darker());
						}
	
						@Override
						public void mouseExited(MouseEvent e) {
							reference.setForeground(Color.blue);
						}
	
					});
			}
		}
	}

	public void openReference(String reference) {
		if (Desktop.isDesktopSupported()) {

			// making a desktop object
			Desktop desktop = Desktop.getDesktop();
			try {
				URI uri = new URI(reference);
				desktop.browse(uri);
			} catch (Exception excp) {
				excp.printStackTrace();
			}
		}
	}

	@Override
	public void handleMessage(Message message) {
		super.handleMessage(message);
		update();
		revalidate();
		repaint();

	}
}
