package relativity.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

public abstract class NumberEditor {

	public record Range( double low, double high ) {
	}
	
	public static final int STEPS = 500;
	public static final String NUMBER_FORMAT = "#0.000";
	protected static final Dimension SLIDER_DIMENSION = new Dimension(300, 20);
	protected static final DecimalFormat FORMATTER = new DecimalFormat(NUMBER_FORMAT);
	
	private JLabel nameLabel;
	private JLabel unitsLabel;
	private JSlider slider;
	private JFormattedTextField valueField;
	private Range range;
    private NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private boolean numberAdjusting = false;
	

	protected NumberEditor(String name, Double initialValue, Range range, String units ) {
		this.range=range;

		numberFormat.setMaximumFractionDigits(2);
		numberFormat.setMinimumFractionDigits(2);
		
		nameLabel = new JLabel(name);

		slider = new JSlider( 0, STEPS, getInteger(initialValue));
		slider.setPreferredSize(SLIDER_DIMENSION);
		slider.addChangeListener(changeEvent->{
			if(!numberAdjusting)
			{
				valueField.setValue(getDouble(slider.getValue()));
				update( getDouble(slider.getValue()) );
			}
		});

		valueField = new JFormattedTextField(numberFormat);
		valueField.setColumns(5);
		valueField.setValue(initialValue);
		valueField.addActionListener(actionEvent->{
			try {
				numberAdjusting = true;
				slider.setValue(getInteger(numberFormat.parse(valueField.getText()).doubleValue()));
				update(numberFormat.parse(valueField.getText()).doubleValue());
				numberAdjusting = false;
			} catch (ParseException e) {
				//do nothing
			}
		});
		
		unitsLabel = new JLabel(units);
	}
	
	public abstract void update( Double value );

	public NumberEditor layout( JPanel parent, int row ) {
		addComponent( parent, nameLabel, 0, row, 1, 1, GridBagConstraints.CENTER );
		addComponent( parent, slider, 1, row, 1, 1, GridBagConstraints.CENTER );
		addComponent( parent, valueField, 2, row, 1, 1, GridBagConstraints.CENTER );
		addComponent( parent, unitsLabel, 3, row, 1, 1, GridBagConstraints.CENTER );
		return this;
	}

	private void addComponent( JPanel parent, Component child, int x, int y, int width, int height, int alignment) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.anchor = alignment;
		parent.add(child, gbc);
	}
	
	private int getInteger( Double value ) {
		return (int)((value-range.low)/(range.high-range.low)*STEPS);
	}
	
	private Double getDouble( Integer value ) {
		return range.low+((double)value/STEPS)*(range.high-range.low);
	}

	protected Range getRange() {
		return range;
	}

	protected void setRange(Range range) {
		this.range = range;
		try {
			numberAdjusting = true;
			slider.setValue(getInteger(numberFormat.parse(valueField.getText()).doubleValue()));
			numberAdjusting = false;
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
