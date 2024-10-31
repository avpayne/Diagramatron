package relativity.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

public abstract class NumberEditor {

	public record Range(double low, double high) {
	}

	public static final int STEPS = 500;
	protected static final Dimension SLIDER_DIMENSION = new Dimension(300, 20);

	private JLabel nameLabel;
	private JLabel unitsLabel;
	private JSlider slider;
	private NumberField valueField;
	private Range range;
	private boolean numberAdjusting = false;

	protected NumberEditor(String name, Double initialValue, Range range, String units) {
		this.range = range;

		nameLabel = new JLabel(name);

		slider = new JSlider(0, STEPS, getInteger(initialValue));
		slider.setPreferredSize(SLIDER_DIMENSION);
		slider.addChangeListener(changeEvent -> {
			if (!numberAdjusting) {
				valueField.setNumber(getDouble(slider.getValue()));
				update(getDouble(slider.getValue()));
			}
		});

		valueField = NumberField.getField();
		valueField.setColumns(5);
		valueField.setNumber(initialValue);
		valueField.addActionListener(actionEvent -> {
			numberAdjusting = true;
			slider.setValue(getInteger(valueField.getNumber()));
			update(valueField.getNumber());
			numberAdjusting = false;
		});

		unitsLabel = new JLabel(units);
	}

	public abstract void update(Double value);

	public NumberEditor layout(JPanel parent, int row) {
		addComponent(parent, nameLabel, 0, row, 1, 1, GridBagConstraints.CENTER);
		addComponent(parent, slider, 1, row, 1, 1, GridBagConstraints.CENTER);
		addComponent(parent, valueField, 2, row, 1, 1, GridBagConstraints.CENTER);
		addComponent(parent, unitsLabel, 3, row, 1, 1, GridBagConstraints.CENTER);
		return this;
	}

	private void addComponent(JPanel parent, Component child, int x, int y, int width, int height, int alignment) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.anchor = alignment;
		parent.add(child, gbc);
	}

	private int getInteger(Double value) {
		return (int) ((value - range.low) / (range.high - range.low) * STEPS);
	}

	private Double getDouble(Integer value) {
		return getGriddedValue(range.low + ((double) value / STEPS) * (range.high - range.low));
	}

	protected Range getRange() {
		return range;
	}

	protected void setRange(Range range) {
		this.range = range;
		numberAdjusting = true;
		slider.setValue(getInteger(valueField.getNumber()));
		numberAdjusting = false;
	}

	protected Double getGriddedValue(Double value) {
		return value;
	}

}
