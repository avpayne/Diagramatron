package relativity.ui;

import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import relativity.message.Broker;

public class DescriptionPanel extends AbstractSpaceTimePanel {

	private static final long serialVersionUID = 6199891895267866667L;
	private JTabbedPane tabbedPanel = new JTabbedPane();
	private JEditorPane displayPane;
	private JTextArea sourcePane = new JTextArea(12, 37);
	private transient Parser parser = Parser.builder().build();
	private transient HtmlRenderer renderer = HtmlRenderer.builder().build();

	protected DescriptionPanel(Broker broker) {
		super(broker);
		add(tabbedPanel);

		displayPane = new JEditorPane("text/html", "");
		displayPane.setMargin( new Insets(10,10,10,10) );
		tabbedPanel.add("Description", new JScrollPane(displayPane));
		tabbedPanel.add("Source", sourcePane);
		tabbedPanel.setSelectedIndex(0);

		sourcePane.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				displayPane.setText(renderer.render(parser.parse(sourcePane.getText())));
				if (diagram != null)
					diagram.setDescription(sourcePane.getText());
				displayPane.repaint();
			}
		});

		update();
	}

	@Override
	protected void update() {

		if (this.diagram != null && diagram.getDescription() != null) {
			Node document = parser.parse(diagram.getDescription());
			displayPane.setText(renderer.render(document));
			sourcePane.setText(diagram.getDescription());
			revalidate();
			repaint();
		}
	}

}
