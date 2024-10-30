package relativity.examples;

import java.awt.Color;

import relativity.AbstractEvent;
import relativity.Event;
import relativity.FrameOfReference;
import relativity.IntersectionEvent;
import relativity.MinkowskiDiagram;
import relativity.WorldLine;
import relativity.WorldLineEvent;
import relativity.WorldLineEvent.Anchor;

public class CarGarageParadox extends MinkowskiDiagram {

	private static final long serialVersionUID = -4697187916382628338L;

	public CarGarageParadox() {
		super();
		FrameOfReference s = new FrameOfReference();
		this.addFrame(s);
		getS().setRelativeSpeed(s, 0.6d);

		WorldLine garageFront = new WorldLine(new Event(2d, 0d, null, Color.red), 0d, "FG", Color.red);
		getS().addWorldLine(garageFront);
		WorldLine garageBack = new WorldLine(new Event(3d, 0d, null, Color.red), 0d, "BG", Color.red);
		getS().addWorldLine(garageBack);
		WorldLine carBack = new WorldLine(new Event(1d, 0d, null, Color.black), 0d, "BC", Color.black);
		s.addWorldLine(carBack);
		WorldLine carFront = new WorldLine(new Event(2d, 0d, null, Color.black), 0d, "FC", Color.black);
		s.addWorldLine(carFront);

		AbstractEvent closeFrontDoor = new IntersectionEvent(garageFront, carBack, "A (CL FG)", getS().getColor());
		getS().addEvent(closeFrontDoor);
		AbstractEvent closeBackDoor = new WorldLineEvent( Anchor.TIME, garageBack, 3d, 2d, "B (OP BG)", getS().getColor());
		getS().addEvent(closeBackDoor);
		AbstractEvent arrivalAtFrontDoor = new IntersectionEvent(garageBack, carFront, "C (FC BG)", getS().getColor());
		getS().addEvent(arrivalAtFrontDoor);
		
		setScale(100);
		setXOffset(-2);
		setYOffset(-1);


		addReference( new Reference( "Relativity and Quanta, Marc M. Dignam, September 2024, Queen's University, Kingston Ont.", false ) );
		addReference( new Reference( "https://en.wikipedia.org/wiki/Ladder_paradox", true ) );
		setDescription("### Car-Garage Paradox" + "\n"
				+ "A car and a garage both have the same proper length Lo.  The garage   \n"
				+ "has a door at the front (FG) and a door at the back (BG).  Initially the   \n"
				+ "front door is open and the back door is closed.  The car speeds towards   \n"
				+ "the garage and a doorman standing at the garage is instructed to   \n"
				+ "close the front door (CL FG) and open the back door (OP BG) at the   \n"
				+ "precise time that the back of the car enters the garage.  According to   \n"
				+ "the doorman, one might expect that the car would be Lorentz-contracted   \n"
				+ "and so would fit easily into the garage while both doors are closed.   \n"
				+ "However, according to the driver of the car, one might expect that   \n"
				+ "the garage door would be Lorentz contracted and thus would be too   \n"
				+ "small to fit the car.  Who is correct? [1]\n");
	}
}
