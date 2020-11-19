package orientedSwapProcess;

/**
 * @dataAbstraction
 * 
 * An instance of this object represents a state (i.e. the position of the wires) of the OSP at a certain time.
 * We will use these objects to record the evolution of the OSP through time so that we can the OSP later (see OSPdrawer.java)
 */
public class Event {
	private int time;
	private NumberList state;
	
	public Event(int time, NumberList state) {
		this.time = time;
		this.state = state;
	}
	
	public double getTime() {
		return this.time;
	}
	
	public NumberList getState() {
		return this.state;
	}
}