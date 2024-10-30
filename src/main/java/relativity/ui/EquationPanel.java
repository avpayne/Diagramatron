package relativity.ui;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;

import relativity.AbstractEvent;
import relativity.FrameOfReference;
import relativity.message.Broker;
import relativity.message.Type;

public class EquationPanel extends AbstractSpaceTimePanel {

	private static final long serialVersionUID = -2330339604465706940L;
	private static final String GAMMA_EQUATION = "\\gamma_{[s1],[s2]}=\\frac{1}{1+\\frac{v_{[s1],[s2]}^2}{c^2}}=\\frac{1}{1+\\frac{[v]^2}{c^2}}=[f]";
	private static final String INVARIANT_EQUATION = "I_{[s1],[s2]}^2=(\\Delta d_{[s1],[s2]})^2-(\\Delta ct_{[s1],[s2]})^2=([d])^2-([ct])^2=[f]";
	private transient Border blackline = BorderFactory.createLineBorder(Color.black);

	public EquationPanel(Broker broker) {
		super(broker);
		update();
		setBackground(EDITOR_COLOR);
		broker.subscribe(Type.FRAME_VELOCITY_CHANGED, this);
	}

	public void update() {
		removeAll();
		Box box = Box.createHorizontalBox();
		Box vBox = Box.createVerticalBox();
		add(box);
		box.add(vBox);
		if (diagram != null && diagram.getS() != null) {
			for (FrameOfReference s : diagram.getS().getOtherFrames())
				vBox.add(buildGammaEquation(diagram.getS(), s));
			box.add(buildInvariantEquation(diagram.getS()));
			revalidate();
			repaint();
		}
	}

	public Box buildInvariantEquation(FrameOfReference s1) {
		Box box = Box.createVerticalBox();
		if (s1 != null)
			for (AbstractEvent e1 : s1.getEvents())
				for (FrameOfReference s2 : s1.getOtherFrames())
					for (AbstractEvent e2 : s2.getEvents()) {
						double dd = e2.getD() - e1.getD();
						double dct = e2.getCt() - e1.getCt();
						Map<String, String> substitutions = new HashMap<>();
						substitutions.put("[s1]", s1.getName().toLowerCase());
						substitutions.put("[s2]", s2.getName().toLowerCase());
						substitutions.put("[d]", FORMATTER.format(dd));
						substitutions.put("[ct]", FORMATTER.format(dct));
						substitutions.put("[f]", FORMATTER.format(dd * dd - dct - dct));
						box.add(renderEquation(INVARIANT_EQUATION, substitutions));
					}
		return box;
	}

	public JPanel buildGammaEquation(FrameOfReference s1, FrameOfReference s2) {
		double v = s1.getSpeedRelativeTo(s2);
		Map<String, String> substitutions = new HashMap<>();
		substitutions.put("[s1]", s1.getName().toLowerCase());
		substitutions.put("[s2]", s2.getName().toLowerCase());
		substitutions.put("[v]", FORMATTER.format(v));
		substitutions.put("[f]", FORMATTER.format(1d / (1 - v * v)));
		return renderEquation(GAMMA_EQUATION, substitutions);
	}

	public JPanel renderEquation(String laTex, Map<String, String> substitutions) {
		for (Entry<String, String> entry : substitutions.entrySet())
			laTex = laTex.replace(entry.getKey(), entry.getValue());

		TeXFormula formula = new TeXFormula(laTex);
		JPanel panel = new JPanel();
		panel.add(new JLabel(formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20)));
		panel.setBorder(blackline);
		return panel;
	}

}
