package orientedSwapProcess;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This method draws the evolution of an OrientedSwapProcess object. Some of this (like the 'main' method) is copied from the internet so
 * I can't properly explain what all of the code does.
 * 
 * Note: For some reason, the program sometimes fails (the figure it returns will not be correct). I haven't found why this happens
 */
public class OSPdrawer extends JPanel {
	/**
	 * This variable records the last previous the OSP was in.
	 */
	private Event lastEvent;

	public static void main(String[] args) {
        OSPdrawer drawer = new OSPdrawer();
        
        JFrame jf = new JFrame();
        jf.setTitle("Oriented swap process");
        jf.setSize(1500, 800);
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.add(drawer);
    }
	
	/**
	 * This method defines what the program has to draw.
	 */
	public void paintComponent(Graphics g) {
		
		// We define some constants (mostly concerning aesthetics) so that we can play with different sizes of OSP's, different scales, etc.
		final int SIZE = 15;
		final int MAX_NMB_ITERATIONS = 100000; // Mostly used for debugging; to avoid infinite loops
		final int HORIZONTAL_SCALE = 12;
		final int VERTICAL_SCALE = 20; 
		final int SPACING = (int) HORIZONTAL_SCALE/3; // This constant defines the space between consecutive swaps
		final int TOP = 20; // This constants defines how much space is left blank at the top of the drawing
		final int LEFT= 20; // This constants defines how much space is left blank at the left of the drawing
		
		super.paintComponent(g);
		
		// Simulate an OSP. 
		OrientedSwapProcess OSP = new OrientedSwapProcess(SIZE);
		int numberOfIterations = 0;
		while(!OSP.Completed() && numberOfIterations < MAX_NMB_ITERATIONS) {
			OSP.doNextMoment();
			numberOfIterations++;
		}
		
		if (numberOfIterations == MAX_NMB_ITERATIONS)
			System.out.println("Maximum number of iterations reached");
		
		// Draw the state of the OSP every time elements were swapped and connect corresponding elements from the last state and the current state
		for(int j = 0; j < OSP.getEvolution().size(); j++) {
			Event event = OSP.getEvolution().get(j);
			
			// If lastEvent == null, event represents the initial position, so we can't draw anything yet (see further)
			if (lastEvent == null)
				lastEvent = event;
			
			else {
				NumberList lastState = lastEvent.getState();
				NumberList currentState = event.getState();
				
				for(int i = 0; i < SIZE; i++) {
					// This line makes sure that every wire has its own color, which makes for a pretty picture in the end.
					g.setColor(this.getColor(((float) i)/((float) SIZE)));
					
					// Connect last state with current state
					g.drawLine((int) (LEFT + lastEvent.getTime()*HORIZONTAL_SCALE + SPACING), TOP + lastState.getPositionOf(i)*VERTICAL_SCALE,
							(int) (LEFT + event.getTime()*HORIZONTAL_SCALE - SPACING), TOP + lastState.getPositionOf(i)*VERTICAL_SCALE);
					
					// Draw swap where necessary
					g.drawLine((int) (LEFT + event.getTime()*HORIZONTAL_SCALE - SPACING), TOP + lastState.getPositionOf(i)*VERTICAL_SCALE,
							(int) (LEFT + event.getTime()*HORIZONTAL_SCALE + SPACING), TOP + currentState.getPositionOf(i)*VERTICAL_SCALE);
				}
				
				lastEvent = event;
			}
			
			// Draw the lines out at the end to make the figure nicer, again making sure that each wire still has its own color
			if(j == OSP.getEvolution().size() - 1) {
				NumberList currentState = event.getState();
				for(int i = 0; i < SIZE; i++) {
					g.setColor(getColor(((float) i) / (float) SIZE));
					g.drawLine((int) (LEFT + event.getTime()*HORIZONTAL_SCALE) + SPACING, TOP + currentState.getPositionOf(i)*VERTICAL_SCALE,
							(int) (LEFT + event.getTime()*HORIZONTAL_SCALE) + 25, TOP + currentState.getPositionOf(i)*VERTICAL_SCALE);
					}
			}
		}
	}
	
	/**
	 * This method returns for each wire 'indexed' with a number (type: float) j between 0 and 1 a corresponding color.
	 * 
	 * @param j = the 'index' of a wire; it is calculated as i/n where i is the actual index of the wire and n the size of the OSP
	 */
	private Color getColor(float j) {
		float r = (float) Math.exp(-16*Math.pow((j-0.15),2));
		float g = (float) Math.exp(-16*Math.pow((j-0.50),2));
		float b = (float) Math.exp(-16*Math.pow((j-0.75),2));
		
		return new Color(r,g,b);
	}
}
