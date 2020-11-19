package lastPassagePercolation;

public class Box {
	private double value;
	private IntVector coordinates;
	
	public Box(double d, IntVector coords) {
		this.value = d;
		this.coordinates = coords;
	}
	
	public double getValue() {
		return this.value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	public IntVector getCoordinates() {
		return this.coordinates.copy();
	}
}
