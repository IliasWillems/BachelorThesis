package orientedSwapProcess;

import java.util.ArrayList;

/**
 * An instance of this class stores an array of Number objects.
 */
public class NumberList {
	private ArrayList<Number> numberArray;
	
	public NumberList() {
		this.numberArray = new ArrayList<Number>();
	}
	
	public void add(int value) {
		for (Number number : this.numberArray) {
			if (number.getValue() == value)
				throw new IllegalArgumentException("The array can't contain duplicates");
		}
		this.numberArray.add(new Number(value));
	}
	
	public void add(Number number) {
		for (Number number_ : this.numberArray) {
			if (number_.getValue() == number.getValue())
				throw new IllegalArgumentException("The array can't contain duplicates");
		}
		this.numberArray.add(number);
	}
	
	public void remove(int position) {
		this.numberArray.remove(position);
	}
	
	public void remove(Number number) {
		this.numberArray.remove(number);
	}
	
	public int getValueAt(int position) {
		if (position >= this.getSize())
			throw new IllegalArgumentException("Index out of bounds");
		return this.numberArray.get(position).getValue();
	}
	
	public int getPositionOf(int value) {
		return this.numberArray.indexOf(new Number(value));
	}
	
	public int getSize() {
		return this.numberArray.size();
	}
	
	/**
	 * Swaps the element at the given position with the next element in the array if the element at the given position is smaller. Returns true iff elements were swapped.
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
	
	public NumberList copy() {
		NumberList copy = new NumberList();
		for(int i = 0; i < this.getSize(); i++) {
			copy.add(this.getValueAt(i));
		}
		return copy;
	}
	
	public int[] toArray() {
		int[] array = new int[this.numberArray.size()];
		for(int i = 0; i < this.numberArray.size(); i++) {
			array[i] = this.getValueAt(i);
		}
		return array;
	}
	
	public static NumberList generateSequenceOfLength(int length) {
		NumberList list = new NumberList();
		for(int i = 0; i < length; i++) {
			list.add(i);
		}
		return list;
	}
}
