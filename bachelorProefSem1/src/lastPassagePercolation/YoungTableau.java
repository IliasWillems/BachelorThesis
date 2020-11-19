package lastPassagePercolation;

import java.util.ArrayList;
import java.util.Random;

public class YoungTableau {
	int size;
	ArrayList<ArrayList<Box>> tableau;
	
	public YoungTableau(int size, boolean random) {
		this.size = size - 1;
		
		tableau = new ArrayList<ArrayList<Box>>();
		if(random) {
		// Create a random Young tableau
		for(int i = size-1; i >= 0; i--) {
			tableau.add(new ArrayList<Box>());
			for(int j = 0; j <= i; j++) {
				Box box = new Box(getRandomExpDistrValue(1), new IntVector(i,j));
				tableau.get(i).add(box);
				}
			}
		} else {
			// Create a Young tableau filled with zeros
			for(int i = size-1; i >= 0; i--) {
				tableau.add(new ArrayList<Box>());
				for(int j = 0; j <= i; j++) {
					Box box = new Box(0, new IntVector(i,j));
					tableau.get(i).add(box);
					}
				}
		}
	}
	
	public double getValueAt(IntVector vector) {
		System.out.println(vector.getX() + ", " + vector.getY());
		if(vector.getX() + vector.getY() > this.size)
			throw new IllegalArgumentException("vector out of bounds");
		return this.tableau.get(vector.getX()).get(vector.getY()).getValue();
	}
	
	public void setValueAt(double value, IntVector vector) {
		if(vector.getX() + vector.getY() > this.size)
			throw new IllegalArgumentException("vector out of bounds");
		this.tableau.get(vector.getX()).get(vector.getY()).setValue(value);
	}
	
	public YoungTableau getLastPassageTableau() {
		YoungTableau LPPTableau = new YoungTableau(this.size+1, false);
		for(int i = 0; i <= this.size; i++) { // i = sum of coordinates along the (i-1)th diagonal
			for(int j = 0; j <= i; j++) {
				int currentX = j;
				int currentY = i-j;
				double LPPTime = 0; 
				if(currentX >= 1 && currentY >= 1) {
					LPPTime = Math.max(LPPTableau.getValueAt(new IntVector(currentX-1, currentY)), LPPTableau.getValueAt(new IntVector(currentX, currentY-1)))
							+ this.getValueAt(new IntVector(currentX, currentY));
				} else if(currentX == 0 && currentY > 0) {
					// System.out.println("current X = " + currentX + ", current Y = " + currentY);
					LPPTime = LPPTableau.getValueAt(new IntVector(currentX, currentY-1)) + this.getValueAt(new IntVector(currentX, currentY));
				} else if(currentY == 0 && currentX > 0){
					LPPTime = LPPTableau.getValueAt(new IntVector(currentX - 1, currentY)) + this.getValueAt(new IntVector(currentX, currentY));
				} else {
					LPPTime = this.getValueAt(new IntVector(currentX, currentY));
				}
				
				LPPTableau.tableau.get(currentX).get(currentY).setValue(LPPTime);
			}
		}
		
		return LPPTableau;
	}
	
	private static double getRandomExpDistrValue(int rate) {
		Random RNG = new Random();
		return Math.log(1 - RNG.nextDouble())/(-rate);
	}
	
	public void displayTableau() {
		for(int i = 0; i < this.size; i++) {
			for(int j = 0; j <= i; j++) {
				System.out.print(this.tableau.get(i).get(j).getValue() + " ");
			}
			System.out.println();
		}
	}
}
