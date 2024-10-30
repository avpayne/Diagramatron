package relativity.ui.frame;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import relativity.FrameOfReference;
import relativity.message.Broker;
import relativity.message.Type;
import relativity.ui.AbstractSpaceTimePanel;

public class GammaEquationPanel extends AbstractSpaceTimePanel {

	private static final long serialVersionUID = -2330339604465706940L;
	private static final String GAMMA_EQUATIONS = "\\gamma_{[s1],[s2]}=\\frac{1}{1+\\frac{v_{[s1],[s2]}^2}{c^2}}=\\frac{1}{1+\\frac{([v]\\ c)^2}{c^2}}=[f]";
	private transient Border blackline = BorderFactory.createLineBorder(Color.black);
	FrameOfReference s1;
	FrameOfReference s2;

	public GammaEquationPanel(FrameOfReference s1, FrameOfReference s2, Broker broker) {
		super(broker);
		setBackground(EDITOR_COLOR);
		broker.subscribe(Type.FRAME_VELOCITY_CHANGED, this);
		broker.subscribe(Type.BASE_FRAME_CHANGED, this);
		broker.subscribe(Type.FRAME_ADDED, this);
		broker.subscribe(Type.FRAME_DELETED, this);
		this.s1 = s1;
		this.s2 = s2;
		update();
	}

	@Override
	protected void update() {
		removeAll();
		add(buildGammaEquation(s1, s2));
		revalidate();
		repaint();
	}

	public JPanel buildGammaEquation(FrameOfReference s1, FrameOfReference s2) {
		double v = s1.getSpeedRelativeTo(s2);
		Map<String, String> substitutions = new HashMap<>();
		substitutions.put("[s1]", s1.getName().toLowerCase());
		substitutions.put("[s2]", s2.getName().toLowerCase());
		substitutions.put("[v]", FORMATTER.format(v));
		substitutions.put("[f]", FORMATTER.format(1d / (1 - v * v)));
		return renderEquation(GAMMA_EQUATIONS, substitutions);
	}

	public JPanel renderEquation(String laTex, Map<String, String> substitutions) {
		for (Entry<String, String> entry : substitutions.entrySet())
			laTex = laTex.replace(entry.getKey(), entry.getValue());

		TeXFormula formula = new TeXFormula(laTex);
		JPanel panel = new JPanel();
		TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
		JLabel label = new JLabel(icon);
		panel.add(label);
		panel.setBorder(blackline);
		panel.revalidate();
		return panel;
	}

}
