package orientedSwapProcess;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class OSPdrawer extends JPanel {
	
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
	
	public void paintComponent(Graphics g) {
		final int SIZE = 15;
		final int MAX_NMB_ITERATIONS = 100000;
		final int HORIZONTAL_SCALE = 12;
		final int VERTICAL_SCALE = 20;
		final int SPACING = (int) HORIZONTAL_SCALE/3;
		final int TOP = 20;
		final int LEFT= 20;
		
		super.paintComponent(g);
		
		// Get OSP
		OrientedSwapProcess OSP = new OrientedSwapProcess(SIZE);
		int numberOfIterations = 0;
		
		while(!OSP.Completed() && numberOfIterations < MAX_NMB_ITERATIONS) {
			OSP.doNextMoment();
			numberOfIterations++;
		}
		
		if (numberOfIterations == MAX_NMB_ITERATIONS)
			System.out.println("Maximum number of iterations reached");
		
		for(int j = 0; j < OSP.getEvolution().size(); j++) {
			Event event = OSP.getEvolution().get(j);
			if (lastEvent == null)
				lastEvent = event;
			else {
				NumberList lastState = lastEvent.getState();
				NumberList currentState = event.getState();
				
				for(int i = 0; i < SIZE; i++) {
					g.setColor(this.getColor(((float) i)/((float) SIZE)));
					// Connect last swap with current swap
					g.drawLine((int) (LEFT + lastEvent.getTime()*HORIZONTAL_SCALE + SPACING), TOP + lastState.getPositionOf(i)*VERTICAL_SCALE,
							(int) (LEFT + event.getTime()*HORIZONTAL_SCALE - SPACING), TOP + lastState.getPositionOf(i)*VERTICAL_SCALE);
					
					// Draw current swap
					g.drawLine((int) (LEFT + event.getTime()*HORIZONTAL_SCALE - SPACING), TOP + lastState.getPositionOf(i)*VERTICAL_SCALE,
							(int) (LEFT + event.getTime()*HORIZONTAL_SCALE + SPACING), TOP + currentState.getPositionOf(i)*VERTICAL_SCALE);
				}
				
				lastEvent = event;
			}
			
			// Draw the lines out at the end to make the figure nicer
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
	
	private Color getColor(float j) {
		float r = (float) Math.exp(-16*Math.pow((j-0.15),2));
		float g = (float) Math.exp(-16*Math.pow((j-0.50),2));
		float b = (float) Math.exp(-16*Math.pow((j-0.75),2));
		
		return new Color(r,g,b);
	}
}
