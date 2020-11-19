package orientedSwapProcess;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class represents/simulates an Oriented Swap Process (OSP) of a given size.
 * 
 * Upon creation, it represents the OSP at time 0, with the exponential clocks already set and the wires ordered in increasing order
 * (wire 1 at the top, wire n at the bottom). Intuitively speaking, it represents an OSP that is ready to be started.
 * 
 * The main method that is defined in this class is the 'doNextMoment()'-method. This method will calculate, for every increase in time, what changes need
 * to be made to the OSP.
 */
public class OrientedSwapProcess {
	
	/**
	 * This constant defines the step with which time is increased every time we call 'doNextMoment()'
	 */
	private final double ANIMATION_SPEED = 0.001;
	
	private int size;
	private double time;
	private int evolutionStep = 0;
	
	private double[] exponentialClocks;
	private NumberList currentNumberState;
	/**
	 * This Array of Event objects tracks the evolution of the OSP
	 */
	private ArrayList<Event> evolution;
	
	public OrientedSwapProcess(int size) {
		if (size < 1)
			throw new IllegalArgumentException("unvalid size");
		
		this.size = size;
		this.time = 0;
		
		this.exponentialClocks = generateExponentialClock(this.size-1, 1);
		
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
	
	/**
	 * This method increases the time by a preset increment and calculates if clocks rang during the elapsed time. If this is the case, the necessary
	 * swaps are performed. Due to the fact that computers can only represent discrete times, it can happen that two clocks ring during 'at the same time', i.e.
	 * that when we increase the time by the given time interval, more than one clock rings in the elapsed time. In this case we need to choose an order in which
	 * to swap wires. We will always attempt to swap wires that are higher up first.
	 */
	public void doNextMoment() {
		// Increase the time by the preset increment
		time += ANIMATION_SPEED;
		
		// Find out if (a) clock(s) rung during the elapsed time. If so, generate a new ringing time for the clock to ring
		NumberList clocksRung = new NumberList();
		for(int i = 0; i < exponentialClocks.length; i++) {
			
			if(this.time - ANIMATION_SPEED <= exponentialClocks[i] && exponentialClocks[i] < this.time) {
				clocksRung.add(i);
				this.exponentialClocks[i] += getExponentialValue(1);
			}
		}
		
		// If the process has just begun and doNextMoment() has just been called for the first time (i.e. this.time = ANIMATION_SPEED), then add
		// the initial state to the evolution (this will be useful when drawing the OSP; see OSPdrawer)
		if(this.time == ANIMATION_SPEED) {
			this.evolution.add(new Event(this.evolutionStep, this.getCurrentNumberState().copy()));
			evolutionStep++;
		}
		
		// For every clock that has rung during the elapsed time we make swaps if necessary. Note that when multiple clocks rang during the elapsed
		// time, we choose to attempt to swap the higher up wires first.
		for(int i = 0; i < clocksRung.getSize(); i++) {
			if(this.currentNumberState.orientedSwap(clocksRung.getValueAt(i))) {
				this.evolution.add(new Event(this.evolutionStep, this.getCurrentNumberState().copy()));
				evolutionStep++;
			}
		}
	}
	
	/**
	 * This method returns true iff the OSP has been completed, i.e. if the state represents the reverse permutation
	 * @return
	 */
	public boolean Completed() {
		boolean completed = true;
		for(int i = 0; i < this.currentNumberState.getSize() - 1; i++)
			if (this.currentNumberState.getValueAt(i) < this.currentNumberState.getValueAt(i+1))
				completed = false;
		
		return completed;
	}
	
	/**
	 * This method returns a value from the exponential distribution with given rate
	 * 
	 * @param rate = The rate of the exponential distribution
	 */
	private static double getExponentialValue(int rate) {
		Random RNG = new Random();
		return Math.log(1 - RNG.nextDouble())/(-rate);
	}
	
	/**
	 * This method returns a list of given size with values of type 'double' that are generated using an exponential distribution with given rate.
	 * 
	 * @param size = the size of the list of exponential values. For the OSP, this will be n-1 with n the size of the OSP
	 * @param rate = the rate of the exponential distribution
	 * @return
	 */
	private static double[] generateExponentialClock(int size, int rate) {
		double[] clock = new double[size];
		
		for(int i = 0; i < size; i++) {
			clock[i] = getExponentialValue(rate);
		}
		return clock;
	}
	
	/**
	 * Method only used for debugging
	 */
	public void printExponentialClocks() {
		for(int i = 0; i < this.exponentialClocks.length; i++) {
			System.out.println(exponentialClocks[i]);
		}
	}
}
