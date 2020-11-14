package orientedSwapProcess;

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