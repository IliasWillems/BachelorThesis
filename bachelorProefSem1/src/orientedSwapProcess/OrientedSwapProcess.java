package orientedSwapProcess;

import java.util.ArrayList;
import java.util.Random;

public class OrientedSwapProcess {
	
	private final double ANIMATION_SPEED = 0.001;
	private final int CLOCK_SIZE = 10;
	
	private int size;
	private double time;
	private int evolutionStep = 0;
	
	private ArrayList<double[]> exponentialClocks;
	private NumberList currentNumberState;
	private ArrayList<Event> evolution;
	
	public OrientedSwapProcess(int size) {
		if (size < 1)
			throw new IllegalArgumentException("unvalid size");
		
		this.size = size;
		this.time = 0;
		
		this.exponentialClocks = new ArrayList<double[]>();
		for(int i = 0; i < size - 1; i++) {
			exponentialClocks.add(generateExponentialClock(this.CLOCK_SIZE, 1));
		}
		
		this.currentNumberState = NumberList.generateSequenceOfLength(size);
		this.evolution = new ArrayList<Event>();
	}
	
	public NumberList getCurrentNumberState() {
		return this.currentNumberState;
	}
	
	public double getTime() {
		return this.time;
	}
	
	public ArrayList<Event> getEvolution() {
		return this.evolution;
	}
	
	public void doNextMoment() {
		time += ANIMATION_SPEED;
		boolean clockRung = false;
		
		// Find out if (a) clock(s) rung during the elapsed time.
		NumberList clocksRung = new NumberList();
		
		for(double[] exponentialClock : this.exponentialClocks) {
			double closestLargerTime = exponentialClock[0];
			int i = 0;
			
			while(closestLargerTime < this.time && i < this.CLOCK_SIZE) {
				closestLargerTime = exponentialClock[i];
				i++;
			}
			
			if(closestLargerTime - ANIMATION_SPEED <= this.time) {
				clocksRung.add(this.exponentialClocks.indexOf(exponentialClock));
			}
		}
		
		if(this.time == ANIMATION_SPEED) {
			this.evolution.add(new Event(this.evolutionStep, this.getCurrentNumberState().copy()));
			evolutionStep++;
		}
		
		for(int i = 0; i < clocksRung.getSize(); i++) {
			if(this.currentNumberState.orientedSwap(clocksRung.getValueAt(i))) {
				this.evolution.add(new Event(this.evolutionStep, this.getCurrentNumberState().copy()));
				evolutionStep++;
			}
		}
		
		
		/*
		if(clocksRung.getSize() > 1) {
			throw new IllegalStateException("More than 1 clock rang during the elapsed time");
		}
		
		if (clocksRung.getSize() == 1) {
			this.currentNumberState.orientedSwap(clocksRung.getValueAt(0));
			clockRung = true;
		}
		
		if(clockRung | this.time == ANIMATION_SPEED) {
			evolution.add(new Event(this.evolutionStep, this.getCurrentNumberState().copy()));
			evolutionStep++;
		}
		*/
	}
	
	public boolean Completed() {
		boolean completed = true;
		for(int i = 0; i < this.currentNumberState.getSize() - 1; i++)
			if (this.currentNumberState.getValueAt(i) < this.currentNumberState.getValueAt(i+1))
				completed = false;
		
		return completed;
	}
	
	private static double[] generateExponentialClock(int size, int rate) {
		Random RNG = new Random();
		double[] clock = new double[size];
		
		for(int i = 0; i < size; i++) {
			double timeInterval = Math.log(1 - RNG.nextDouble())/(-rate);
			if (i == 0)
				clock[i] = timeInterval;
			else
				clock[i] = clock[i-1] + timeInterval;
		}
		return clock;
	}
	
	/**
	 * Method only used for debugging
	 */
	public void sysoutExpClockValues() {
		for (int i = 0; i < this.exponentialClocks.size(); i++) {
			expClockToString(this.exponentialClocks.get(i));
		}
	}
	
	/**
	 * Method only used for debugging
	 */
	private static void expClockToString(double[] exponentialClock) {
		String str = new String();
		for(int i = 0; i < exponentialClock.length; i++) {
			str += exponentialClock[i] + ", ";
		}
		System.out.println(str);
	}
	
}
