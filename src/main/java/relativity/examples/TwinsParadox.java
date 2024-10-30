package relativity.examples;

import relativity.Annotation.AnnotationPosition;
import relativity.Event;
import relativity.FrameOfReference;
import relativity.MinkowskiDiagram;
import relativity.WorldLine;

public class TwinsParadox extends MinkowskiDiagram {

	private static final long serialVersionUID = -4697187916382628338L;

	public TwinsParadox() {
		super();
		FrameOfReference s = new FrameOfReference();
		this.addFrame(s);
		getS().setRelativeSpeed(s, 0.8d);

		for (int i = 1; i < 10; i++) {
			WorldLine lightPing = new WorldLine(new Event(0d, 5d * i, null, getS().getColor()), 1d,
					"Ping " + i, getS().getColor());
			getS().addWorldLine(lightPing);
		}

		WorldLine outwardTrip = new WorldLine(new Event(0d, 0d, null, getS().getColor()), 0.8d, 25,
				"Outward Trip", getS().getColor());
		getS().addWorldLine(outwardTrip);
		WorldLine returnTrip = new WorldLine(new Event(20d, 25d, null, getS().getColor()), -0.8d, 25,
				"Return Trip", getS().getColor());
		getS().addWorldLine(returnTrip);
		WorldLine stationaryObserver = new WorldLine(new Event(0d, 0d, null, getS().getColor()), 0.0d,
				50, "Stationary\nObserver", getS().getColor());
		stationaryObserver.setAnnotationPosition(AnnotationPosition.LEFT);
		getS().addWorldLine(stationaryObserver);

		setYOffset(-50);
		setScale(6);

		addReference( new Reference( "Relativity and Quanta, Marc M. Dignam, September 2024, Queen's University, Kingston Ont.", false ) );
		addReference( new Reference( "https://en.wikipedia.org/wiki/Twin_paradox", true ) );
		setDescription( "### Twin Paradox\n" 
						+ "\n" 
						+ "One of the most famous of the apparent \"paradoxes\" in relativity is    \n"
						+ "the \"twin paradox\".  Two twins are 20 years old.  One travels with a   \n"
						+ "speed of v=0.8c to a planet that is 20 light years away and then   \n"
						+ "over a very short period of time she reverses the velocity of her   \n"
						+ "rocket and returns home to earth ad the same speed, v.  Her twin   \n"
						+ "brother simply stays home on earth.  What is the age of each of the   \n"
						+ "twins when the space traveller returns home? [1]");
	}
}
