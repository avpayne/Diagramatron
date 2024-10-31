package relativity.ui;

import relativity.MinkowskiDiagram;
import relativity.message.Broker;
import relativity.message.Message;
import relativity.message.Subscriber;
import relativity.message.Type;

public abstract class TimeEditor extends NumberEditor implements Subscriber {

	private MinkowskiDiagram diagram;
	
	protected TimeEditor( Double initialValue, MinkowskiDiagram diagram, Broker broker ) {
		super("ct", initialValue, new Range(-100d,100d), "ly");
		this.diagram = diagram;
		if( diagram!=null )
			setRange(new Range(diagram.getScaledYStart(), diagram.getScaledYEnd()));
		
		broker.subscribe(Type.ZOOM_CHANGED, this);
		broker.subscribe(Type.OFFSET_CHANGED, this);
		broker.subscribe(Type.WINDOW_CHANGED, this);
		broker.subscribe(Type.DIAGRAM_LOADED, this);
		broker.subscribe(Type.NEW_DIAGRAM, this);
	}

	@Override
	public void handleMessage(Message message) {
		if( message.payload()!=null && message.payload() instanceof MinkowskiDiagram newDiagram )
			diagram = newDiagram;
		if( diagram!=null )
			setRange(new Range(diagram.getScaledYStart(), diagram.getScaledYEnd()));
	}

	@Override
	protected Double getGriddedValue( Double value )
	{
		if( diagram!=null && diagram.getGrid()!=null)
			return Math.round(value/diagram.getGrid())*diagram.getGrid();
		return value;
	}
	
}
