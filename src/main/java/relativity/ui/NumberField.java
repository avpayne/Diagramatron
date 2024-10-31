package relativity.ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.SwingUtilities;

public class NumberField extends JFormattedTextField {

	public static final String NUMBER_FORMAT = "#0.000";
	public static final DecimalFormat FORMATTER = new DecimalFormat(NUMBER_FORMAT);

	private static final long serialVersionUID = -3793609763066162665L;

	private NumberField(NumberFormat format) {
		super(format);
		addKeyListener(new KeyAdapter() {
			String lastValue = "";

			@Override
			public void keyTyped(KeyEvent e) {
				SwingUtilities.invokeLater(() -> {
					try {
						if (!getText().trim().equals("") && !getText().trim().equals("-")) {
							Double.parseDouble(getText());
							lastValue = getText();
						}
					} catch (NumberFormatException exception) {
						setText(lastValue);
					}
				});
			}
		});
	}

	public static NumberField getField() {
		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMaximumFractionDigits(3);
		numberFormat.setMinimumFractionDigits(3);
		return new NumberField(numberFormat);
	}

	public Double getNumber() {
		return Double.valueOf(getText());
	}

	public void setNumber(Double value) {
		setText(FORMATTER.format(value));
	}

}
