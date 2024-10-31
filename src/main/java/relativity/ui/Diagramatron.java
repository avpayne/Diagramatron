package relativity.ui;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Taskbar;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import relativity.AbstractEvent;
import relativity.CachedInstancesTypeAdapterFactory;
import relativity.EventElementAdapter;
import relativity.MinkowskiDiagram;
import relativity.WorldLine;
import relativity.examples.CarGarageParadox;
import relativity.examples.TwinsParadox;
import relativity.message.Broker;
import relativity.message.Message;
import relativity.transform.TransformElement;
import relativity.transform.TransformElementAdapter;
import relativity.ui.event.EventEditorPanel;
import relativity.ui.event.IntersectionEventEditorPanel;
import relativity.ui.event.WorldLineEventEditorPanel;
import relativity.ui.frame.FrameEditorPanel;
import relativity.ui.frame.GammaEquationPanel;
import relativity.ui.worldline.WorldLineEditorPanel;

public class Diagramatron extends JFrame {

	private static final long serialVersionUID = -952529385841475133L;
	private static final String USER_HOME = "user.home";
	private transient Path currentFile;
	private transient Gson gson;
	private SpaceTimePanel spaceTimePanel;
	private ReferencesPanel referencesPanel;
	private static final String SECRET_PASSPHRASE = "engDadsRock!";
	private String enteredPhrase = "";
	private boolean unlocked = false;
	private transient BufferedImage backgroundImage;

	private JMenuBar mainMenuBar = new JMenuBar();
	private JMenu filesMenu = new JMenu("Files");
	private JMenu helpMenu = new JMenu("Help");
	private JMenu examplesMenu = new JMenu("Examples");
	private JMenu viewMenu = new JMenu("View");

	private JMenuItem twinsItem = new JMenuItem("Twins Paradox");
	private JMenuItem garageItem = new JMenuItem("Car-Garage Paradox");
	private JMenuItem aboutItem = new JMenuItem("About");
	private JMenuItem openItem = new JMenuItem("Open");
	private JMenuItem saveItem = new JMenuItem("Save");
	private JMenuItem saveAsItem = new JMenuItem("Save As");
	private JMenuItem newItem = new JMenuItem("New");
	private JMenuItem quitItem = new JMenuItem("Quit");
	private JMenuItem addBackgroundImageItem = new JMenuItem("Add Background Image");
	private JMenuItem removeBackgroundImageItem = new JMenuItem("Remove Background Image");
	private JMenuItem setGridItem = new JMenuItem("Set SpaceTime Rounding");

	public static void main(String[] args) {
		new Diagramatron();
	}

	public Diagramatron() {
		super("SpaceTime (Minkowski) Diagram Editor");

		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		builder.registerTypeAdapter(TransformElement.class, new TransformElementAdapter());
		builder.registerTypeAdapter(AbstractEvent.class, new EventElementAdapter());
		builder.registerTypeAdapterFactory(new CachedInstancesTypeAdapterFactory(Set.of(WorldLine.class)));
		gson = builder.create();

		Broker broker = new Broker();
		spaceTimePanel = new SpaceTimePanel(broker);
		ControlPanel controlPanel = new ControlPanel(broker);
		referencesPanel = new ReferencesPanel(broker);

		initMenu(broker);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(controlPanel, BorderLayout.WEST);
		getContentPane().add(spaceTimePanel, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		controlPanel.setMinimumSize(controlPanel.getPreferredSize());

		new AboutWindow(this);

		MinkowskiDiagram diagram = new MinkowskiDiagram();
		broker.publish(new Message(relativity.message.Type.NEW_DIAGRAM, diagram));

		easterEgg();
		final Taskbar taskbar = Taskbar.getTaskbar();
		taskbar.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/DockIcon.jpg")));

	}

	private void easterEgg() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEvent -> {
			if (!keyEvent.isConsumed() && keyEvent.getID() == KeyEvent.KEY_TYPED && !unlocked) {
				String s = enteredPhrase + keyEvent.getKeyChar();
				if (SECRET_PASSPHRASE.startsWith(s))
					enteredPhrase = enteredPhrase + keyEvent.getKeyChar();
				else
					enteredPhrase = "";

				if (enteredPhrase.equals(SECRET_PASSPHRASE))
					unlock();
			}
			return false;
		});

	}

	private void unlock() {
		unlocked = true;
		ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("images/DadIcon.jpg"));
		JOptionPane.showMessageDialog(Diagramatron.this, "How right you are!", "Eng Dads Do Rock!", JOptionPane.INFORMATION_MESSAGE, icon);
	}

	private BufferedImage getBufferedImage(Image image) {
		BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D bGr = bi.createGraphics();
		bGr.drawImage(image, 0, 0, null);
		bGr.dispose();
		return bi;
	}

	private void initMenu(Broker broker) {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		setJMenuBar(mainMenuBar);
		filesMenu.add(openItem);
		filesMenu.add(saveItem);
		filesMenu.add(saveAsItem);
		filesMenu.add(newItem);
		filesMenu.add(quitItem);

		examplesMenu.add(twinsItem);
		examplesMenu.add(garageItem);

		viewMenu.add(addBackgroundImageItem);
		viewMenu.add(removeBackgroundImageItem);
		viewMenu.add(setGridItem);

		addBackgroundImageItem.addActionListener(actionEvent -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("SpaceTime Background Selection Dialog");
			fileChooser.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "png");
			fileChooser.addChoosableFileFilter(filter);
			fileChooser.setCurrentDirectory(new File(System.getProperty(USER_HOME)));
			if (fileChooser.showDialog(this, "Open Background file") == JFileChooser.APPROVE_OPTION) {
				String fileName = fileChooser.getSelectedFile().getPath();
				backgroundImage = getBufferedImage(new ImageIcon(fileName).getImage());
				broker.publish(new Message(relativity.message.Type.BACKGROUND_UPDATED, backgroundImage));
				revalidate();
				repaint();
				spaceTimePanel.getDiagram().setDirty();
			}

		});
		removeBackgroundImageItem.addActionListener(actionEvent -> {
			if (backgroundImage != null) {
				backgroundImage = null;
				broker.publish(new Message(relativity.message.Type.BACKGROUND_UPDATED, backgroundImage));
				revalidate();
				repaint();
				spaceTimePanel.getDiagram().setDirty();
			}
		});

		setGridItem.addActionListener(actionEvent -> {
			NumberField valueField = NumberField.getField();
			if( spaceTimePanel.getDiagram()!=null && spaceTimePanel.getDiagram().getGrid()!=null)
				valueField.setNumber( spaceTimePanel.getDiagram().getGrid() );
			int okCxl = JOptionPane.showConfirmDialog(this, valueField, "Enter Grid", JOptionPane.OK_CANCEL_OPTION);
			if (okCxl == JOptionPane.OK_OPTION) {
				Double grid = valueField.getNumber();
				if (spaceTimePanel.getDiagram() != null && grid!=null )
					spaceTimePanel.getDiagram().setGrid(Math.max(grid, 0.001));
			}

		});

		helpMenu.add(aboutItem);
		helpMenu.add(examplesMenu);

		aboutItem.addActionListener(actionEvent -> new AboutWindow());

		twinsItem.addActionListener(actionEvent -> {
			if (confirmUnsavedChanges()) {
				reset(broker);
				MinkowskiDiagram.resetNameColor();
				MinkowskiDiagram diagram = new TwinsParadox();
				update(diagram);
				broker.publish(new Message(relativity.message.Type.DIAGRAM_LOADED, diagram));
				diagram.clearDirty();
				ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("images/backgrounds/KSP Blueprint Light.png"));
				backgroundImage = getBufferedImage(icon.getImage());
				broker.publish(new Message(relativity.message.Type.BACKGROUND_UPDATED, backgroundImage));
			}
		});
		garageItem.addActionListener(actionEvent -> {
			if (confirmUnsavedChanges()) {
				reset(broker);
				MinkowskiDiagram.resetNameColor();
				MinkowskiDiagram diagram = new CarGarageParadox();
				update(diagram);
				broker.publish(new Message(relativity.message.Type.DIAGRAM_LOADED, diagram));
				diagram.clearDirty();
				backgroundImage = null;
				broker.publish(new Message(relativity.message.Type.BACKGROUND_UPDATED, backgroundImage));
			}
		});

		mainMenuBar.add(filesMenu);
		mainMenuBar.add(viewMenu);
		mainMenuBar.add(helpMenu);

		openItem.addActionListener(actionEvent -> open(broker));
		saveItem.addActionListener(actionEvent -> save());
		saveAsItem.addActionListener(actionEvent -> saveAs());
		newItem.addActionListener(actionEvent -> newDiagram(broker));
		quitItem.addActionListener(actionEvent -> quit());

	}

	private void quit() {
		if (confirmUnsavedChanges()) {
			System.exit(0);
		}
	}

	private void newDiagram(Broker broker) {
		if (confirmUnsavedChanges()) {
			reset(broker);
			MinkowskiDiagram.resetNameColor();
			MinkowskiDiagram diagram = new MinkowskiDiagram();
			update(diagram);
			broker.publish(new Message(relativity.message.Type.NEW_DIAGRAM, diagram));
			backgroundImage = null;
			broker.publish(new Message(relativity.message.Type.BACKGROUND_UPDATED, backgroundImage));
		}
	}

	private void open(Broker broker) {
		try {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("SpaceTime File Selection Dialog");
			fileChooser.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Spacetime Diagrams", "rel");
			fileChooser.addChoosableFileFilter(filter);
			fileChooser.setCurrentDirectory(new File(System.getProperty(USER_HOME)));
			if (fileChooser.showDialog(this, "Open SpaceTime file") == JFileChooser.APPROVE_OPTION) {
				reset(broker);
				MinkowskiDiagram.resetNameColor();
				String fileName = fileChooser.getSelectedFile().getPath();
				openZipFile(broker, fileName);
				revalidate();
				repaint();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void openZipFile(Broker broker, String zipFilePath) throws IOException {
		MinkowskiDiagram diagram = null;
		try (ZipFile zipFile = new ZipFile(zipFilePath)) {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (!entry.isDirectory() && entry.getName().equals("diagram.json")) {
					try (InputStream inputStream = zipFile.getInputStream(entry)) {
						String json = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
						diagram = gson.fromJson(json, MinkowskiDiagram.class);
						diagram.setDiagramReferences();
						update(diagram);
						diagram.clearDirty();
						broker.publish(new Message(relativity.message.Type.DIAGRAM_LOADED, diagram));
					}
				}
				if (!entry.isDirectory() && entry.getName().equals("background.png")) {
					try (InputStream inputStream = zipFile.getInputStream(entry)) {
						backgroundImage = ImageIO.read(inputStream);
						broker.publish(new Message(relativity.message.Type.BACKGROUND_UPDATED, backgroundImage));
					}
				}
			}
		}
	}

	private byte[] createZipByteArray() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ZipOutputStream zos = new ZipOutputStream(baos)) {

			ZipEntry entry = new ZipEntry("diagram.json");
			zos.putNextEntry(entry);
			zos.write(gson.toJson(spaceTimePanel.getDiagram()).getBytes());
			zos.closeEntry();

			if (backgroundImage != null) {
				entry = new ZipEntry("background.png");
				zos.putNextEntry(entry);
				ImageIO.write(backgroundImage, "png", zos);
				zos.closeEntry();
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return baos.toByteArray();
	}

	private void save() {
		if (currentFile != null)
			try {
				Files.write(currentFile, createZipByteArray());
				spaceTimePanel.getDiagram().clearDirty();
			} catch (IOException e) {
				e.printStackTrace();
			}
		else
			saveAs();

	}

	private void saveAs() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("SpaceTime File Save Dialog");
		fileChooser.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Spacetime Diagrams", "rel");
		fileChooser.addChoosableFileFilter(filter);
		fileChooser.setCurrentDirectory(new File(System.getProperty(USER_HOME)));
		if (fileChooser.showDialog(this, "Save SpaceTime file") == JFileChooser.APPROVE_OPTION) {
			String fileName = fileChooser.getSelectedFile().getPath();
			if (!fileName.endsWith(".rel"))
				fileName += ".rel";
			currentFile = Path.of(fileName);
			save();
		}
	}

	private void update(MinkowskiDiagram diagram) {
		getContentPane().remove(referencesPanel);
		if (diagram != null && diagram.getReferences() != null && !diagram.getReferences().isEmpty()) {
			getContentPane().add(referencesPanel, BorderLayout.SOUTH);
			revalidate();
			repaint();
		}
	}

	private void reset(Broker broker) {
		MinkowskiDiagram.resetNameColor();
		broker.unsubscribe(FrameEditorPanel.class);
		broker.unsubscribe(EventEditorPanel.class);
		broker.unsubscribe(IntersectionEventEditorPanel.class);
		broker.unsubscribe(WorldLineEventEditorPanel.class);
		broker.unsubscribe(WorldLineEditorPanel.class);
		broker.unsubscribe(GammaEquationPanel.class);
	}

	private boolean confirmUnsavedChanges() {
		if (spaceTimePanel.getDiagram() != null && spaceTimePanel.getDiagram().isDirty()) {
			int response = JOptionPane.showConfirmDialog(null, "Unsaved changes.  Do you want to proceed?", "Warning", JOptionPane.YES_NO_OPTION);
			return (response == JOptionPane.YES_OPTION);
		}
		return true;
	}

}
