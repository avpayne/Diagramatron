package relativity.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class AboutWindow extends JFrame {
	private static final long serialVersionUID = -8543165878449685812L;
	private String version = "1.0";
	private String date = "Oct 19, 2024";
	private String author = "Sci93 EngPhys Dad";

	public AboutWindow() {
		this(null);
	}

	public AboutWindow(Diagramatron gui) {
		super("About SpaceTime");
		setUndecorated(true);
		setAlwaysOnTop(true);
		getContentPane().setLayout(new GridBagLayout());

		ImageIcon image = new ImageIcon(getClass().getClassLoader().getResource("images/Title.png"));
		JLabel applicationTitle = new JLabel(image);
		addComponent(applicationTitle, 0, 0, 2, 1, GridBagConstraints.CENTER);
		addRow(" Application: ", " SpaceTime Diagramatron", 1);
		addRow(" Version: ", " " + version, 2);
		addRow(" Date: ", " " + date, 3);
		addRow(" Author: ", " " + author, 4);
		addRow(" Image Credit: ", " NASA/JPL", 5);

		if (gui == null) {
			JButton button = new JButton("Dismiss");
			addComponent(button, 1, 7, GridBagConstraints.EAST);
			button.addActionListener(actionEvent -> dispose());
		}

		pack();
		setLocationRelativeTo(null);
		setVisible(true);

		if (gui != null) {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					gui.setVisible(true);
					AboutWindow.this.dispose();
				}

			}, 1500l);
		}

	}

	private void addRow(String label, String value, int row) {
		addComponent(new JLabel(label), 0, row, GridBagConstraints.WEST);
		addComponent(new JLabel(value), 1, row, GridBagConstraints.WEST);
	}

	private void addComponent(Component component, int x, int y, int width, int height, int alignment) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.anchor = alignment;
		getContentPane().add(component, gbc);
	}

	private void addComponent(Component component, int x, int y, int alignment) {
		addComponent(component, x, y, 1, 1, alignment);
	}

}
