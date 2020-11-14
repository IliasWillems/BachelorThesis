package orientedSwapProcess;

import java.awt.Color;

/**
 * An instance of this class represents a number (in a list, see class 'NumberList'). 
 */
public class Number {
	/**
	 * The value this Number object represents
	 */
	private int value;
	/**
	 * The position in the list this Number object is in.
	 */
	private int position;
	/**
	 * The path that this Number has taken in the oriented swap proces
	 */
	private int[] path;
	
	private Color color;
	
	public Number(int value) {
		this.value = value;
	}
	
	public Number(int value, Color color) {
		this.value = value;
		this.color = color;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public int getPosition() {
		return position;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public int[] getPath() {
		if (this.path == null)
			return null;
		else {
			int[] copy = new int[path.length];
			for(int i = 0; i < path.length; i++) {
				copy[i] = this.path[i];
			}
			return copy;
		}
	}
	
	public void addToPath(int value) {
		if (this.path == null) {
			int[] newPath = new int[1];
			newPath[0] = value;
			this.path = newPath;
		} else {
			int[] newPath = new int[path.length + 1];
			for(int i = 0; i < path.length; i++) {
				newPath[i] = this.path[i];
			}
			newPath[path.length] = value;
			this.path = newPath;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Number other = (Number) obj;
		if (value != other.value)
			return false;
		return true;
	}
}
