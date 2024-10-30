package relativity.ui;

public abstract class VelocityEditor extends NumberEditor {

	protected VelocityEditor( Double initialValue) {
		super("v", initialValue, new Range(-1d,1d), "c");
	}

}
