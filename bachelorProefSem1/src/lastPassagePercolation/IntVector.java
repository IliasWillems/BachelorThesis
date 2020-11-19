package lastPassagePercolation;

public class IntVector {
	private int x;
	private int y;
	
	public IntVector(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public IntVector plus(IntVector other) {
		return new IntVector(this.getX() + other.getX(), this.getY() + other.getY());
	}
	
	public IntVector copy() {
		return new IntVector(this.getX(), this.getY());
	}
}
