package orientedSwapProcess;

import java.util.ArrayList;

/**
 * An instance of this class stores an array of Number objects. The abstract use of this class will be that it represents the state of the OSP
 * in the main program OrientedSwapProcess.java
 */
public class NumberList {
	private ArrayList<Number> numberArray;
	
	public NumberList() {
		this.numberArray = new ArrayList<Number>();
	}
	
	/**
	 * Adds the given value (converted to a Number object) to the end of the NumberArray.
	 * This method will throw an error if the number is already contained in the NumberArray
	 * 
	 * @param value = the number to add to the end of the array this object represents
	 */
	public void add(int value) {
		for (Number number : this.numberArray) {
			if (number.getValue() == value)
				throw new IllegalArgumentException("The array can't contain duplicates");
		}
		this.numberArray.add(new Number(value));
	}
	
	/**
	 * Adds the given Number object to the NumberArray. This method will throw an error if the value that the given Number object represents is already
	 * contained in the NumberArray
	 * 
	 * @param number = the Number object to add to the end of this array
	 */
	public void add(Number number) {
		for (Number number_ : this.numberArray) {
			if (number_.getValue() == number.getValue())
				throw new IllegalArgumentException("The array can't contain duplicates");
		}
		this.numberArray.add(number);
	}
	
	/**
	 * Removes the Number at the given position from the NumberList.
	 * 
	 * @param position = the position of the number to remove
	 */
	public void remove(int position) {
		this.numberArray.remove(position);
	}
	
	/**
	 * Removes the given Number from the NumberList.
	 * 
	 * @param number = the Number to remove
	 */
	public void remove(Number number) {
		this.numberArray.remove(number);
	}
	
	/**
	 * This method returns the value that the Number object at the given position in the NumberList represents.
	 * 
	 * @param position = the index of the Number object in the NumberList
	 */
	public int getValueAt(int position) {
		if (position >= this.getSize())
			throw new IllegalArgumentException("Index out of bounds");
		return this.numberArray.get(position).getValue();
	}
	
	/**
	 * This method returns the position of a given value in the NumberList;
	 * 
	 * @param value = the value of which the position is returned
	 * @return
	 */
	public int getPositionOf(int value) {
		return this.numberArray.indexOf(new Number(value));
	}
	
	public int getSize() {
		return this.numberArray.size();
	}
	
	/**
	 * Swaps the element at the given position with the next element in the array if the element at the given position is smaller. 
	 * Returns true iff elements were swapped.
	 */
	public boolean orientedSwap(int position) {
		boolean swapped = false;
		if (!(position >= 0 && position < this.numberArray.size() - 1))
			throw new IllegalArgumentException("unvalid position");
		
		int e1 = this.numberArray.get(position).getValue();
		int e2 = this.numberArray.get(position+1).getValue();
		if (e1 < e2) {
			this.numberArray.set(position, new Number(e2));
			this.numberArray.set(position+1, new Number(e1));
			swapped = true;
		}
		
		return swapped;
	}
	
	/**
	 * This method returns a copy of the NumberList this object represents.
	 */
	public NumberList copy() {
		NumberList copy = new NumberList();
		for(int i = 0; i < this.getSize(); i++) {
			copy.add(this.getValueAt(i));
		}
		return copy;
	}
	
	/**
	 * This method returns the NumberList that this object represents as an int[] object.
	 */
	public int[] toArray() {
		int[] array = new int[this.numberArray.size()];
		for(int i = 0; i < this.numberArray.size(); i++) {
			array[i] = this.getValueAt(i);
		}
		return array;
	}
	
	/**
	 * This method returns, for a given length, a NumberList that represents an increasing sequence of numbers, starting at 0.
	 * For example [0, 1, 2, 3, 4, 5] (length = 6)
	 */
	public static NumberList generateSequenceOfLength(int length) {
		NumberList list = new NumberList();
		for(int i = 0; i < length; i++) {
			list.add(i);
		}
		return list;
	}
}
