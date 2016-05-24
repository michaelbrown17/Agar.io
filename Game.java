package server;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

public class Game extends JPanel implements ActionListener, KeyListener {

	//max x and y values for the points (based on monitor size)
	public static int xMax;
	public static int yMax;
	
	//x and y positions of the mouse
	public static int xMouse = 0;
	public static int yMouse = 0;
	
	//sets the diameter of points on the screen
	public static final int pointDiam = 20;
	
	//stores the distance from the mouse to the blob
	public static float dist = 0;
	
	//stores the difference in x and y for the blobs
	public static float xDif = 0;
	public static float yDif = 0;
	
	//stores number of the user's blobs
	public static int numBlobs = 1;
	
	//define the initial diameter of the first blob
	public static final int initialDiam = 70;
		
	//array containing the multiple blobs that your blob could have 
		// structure: [x, y, size, leftover x, leftover y]
	public static ArrayList<Blob> mainBlob = new ArrayList<Blob>();
	
	//array containing points on the screen
	public static ArrayList<Points> points = new ArrayList<Points>();
	
	//indicates the number of dots on the screen
	public static final int numPoints = 100;
	
	//timer for refresh time
	Timer t = new Timer(10, this);

	// create a new font for the score
	private static Font sanSerifFont = new Font("SanSerif", Font.BOLD, 36);
	
	//function starts the timer
	public Game(double widthMax, double heightMax) {
		
		//create a new timer for refreshing the screen
		t.start();
		
		//add key listener (for space bar to split blob)
		addKeyListener(this);
		
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		
		//creates a new blob to the mainblob list		
		mainBlob.add(new Blob(900,400,initialDiam,0,0));
		
		//set the max x and y values equal to the width and height of the monitor (in pixels) (cast the double to an int)
		xMax = (int)widthMax;
		yMax = (int)heightMax;
		
		//add a certain amount of points to the map that will be used to help the blobs grow
		for(int i = 0; i < numPoints; i++) {
			
			//random object for random coordinates
			Random ran = new Random();
			
			//add a new point to the points arraylist
			points.add(new Points(ran.nextInt(xMax - pointDiam), ran.nextInt(yMax - pointDiam)));
			
			
		}
		
	}
	
	
	
	////////////////////////////////////////////////////
	
	
	///////////////   REFRESH FUNCTION     /////////////
	
	
	////////////////////////////////////////////////////
	
	
	//this function will refresh the screen
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		
		//go through each point in the points array and paint them
		for (int i = 0; i < points.size(); i++) {
			
			//if point was eaten in last refresh, repaint in a random space
			if (points.get(i).getSize() == 0) {
				
				//random object
				Random ran = new Random();
				
				//get random values for the x and y values for the new point
					// y and xMax - 24 instead of y and xMax + 1 because we
					// don't
					// want points to be outside of range...
				points.get(i).setX(ran.nextInt(xMax - 99));
				points.get(i).setY(ran.nextInt(yMax - 99));
				points.get(i).setDiam(pointDiam);
				
			}
			
			//color = red; fill the point that was present from last refresh
			g2.setColor(Color.red);
			g2.fillOval((int)points.get(i).X(), (int)points.get(i).Y(), (int)points.get(i).getDiam(), (int)points.get(i).getDiam());

		}
		
		//go through each blob in your blob array
		for (int q = 0; q < mainBlob.size(); q++) {

			// if the blob in the array doesn't have any mass, continue to next
			// value in the array...avoids spaces in the array that don't correspond to blobs
			if (mainBlob.get(q).getSize() == 0) {
				continue;
			}
			
			//assign variables for values from the mainBlob array
			int x = (int) mainBlob.get(q).getX();
			int y = (int) mainBlob.get(q).getY();
			int diameter = (int) mainBlob.get(q).getDiam();
			int radius = diameter / 2;

			//color = blue; fill your blob on the screen
			g2.setColor(Color.blue);
			g2.fillOval(x, y, diameter, diameter);

			// get mouse coordinates
			PointerInfo a = MouseInfo.getPointerInfo();
			Point b = a.getLocation();

			// refine the mouse coordinate values
			xMouse = (int) b.getX();
			yMouse = (int) b.getY() - 25;

			// get the distance between the mouse and the center of the blob
			dist = (float) Math
					.sqrt(Math.pow(xMouse - (x + radius), 2) + Math.pow(yMouse - (y + radius), 2));

			// draw a line between the mouse and the center of the blob (for debugging purposes)
			g2.setColor(Color.green);
			g2.drawLine(xMouse, yMouse, x + radius, y + radius);

			// find the amount that the mainBlob[q] should move based on the
			// size of the mainBlob[q]
			xDif = (float) ((xMouse - (x + radius)) / dist * 250 / diameter);
			yDif = (float) ((yMouse - (y + radius)) / dist * 250 / diameter);
			
			// add leftover ints to the current xDif / yDif
						//xDif, yDif, getDX, and getDY get NaN after this statement...investigate further!
			xDif += mainBlob.get(q).getDX();
			yDif += mainBlob.get(q).getDY();
			
			//go through each points to see if the point is swallowed
			for (int i = 0; i < points.size(); i++) {

				// finds the coordinates of the point
				int centerXP = (int)(points.get(i).X() + points.get(i).getRadius());
				int centerYP = (int)(points.get(i).Y() + points.get(i).getRadius());

				// finds the coordinates of the center of the mainBlob[q]
				int centerXC = x + radius;
				int centerYC = y + radius;

				//get the distance between the center of the circle and the center of the point
				int res = (int) Math.sqrt(Math.pow(centerYP - centerYC, 2) + Math.pow(centerXP - centerXC, 2));

				// checks if the mainBlob[q] can absorb the point value
				if (res <= radius - 10 && diameter > points.get(i).getDiam()) {
					points.get(i).setSize(0);
					mainBlob.get(q).changeSize(200);
				}

			}
			
			// add more efficient algorithm to sort through all of the blobs
			for (int i = 0; i < mainBlob.size(); i++) {
						
				//make sure you're not referring to the same blob
				if (i != q) {
					
					// change this statement so that the sqrt bases the distance
					// off of the center of the circles
					int sqrt = (int) Math
							.sqrt(Math.pow((x + xDif) - mainBlob.get(i).getX(), 2) + Math.pow((y + yDif) - mainBlob.get(i).getY(), 2));
					
					//this prevents blobs from recombining 
							//if either blob under comparison is still waiting for combination, then break from the loop
					if (sqrt <= radius + mainBlob.get(i).getRadius() && (!mainBlob.get(i).canCombine() || !mainBlob.get(q).canCombine())) {
						xDif = 0;
						yDif = 0;
						 
						//since this is true only for these two blobs, continue with the rest of the ArrayList
						continue;
						
					} 
					//if the blobs are close enough and both blobs can combine...
					else if (sqrt <= Math.abs(radius - (mainBlob.get(i).getRadius())) && mainBlob.get(i).canCombine() && mainBlob.get(q).canCombine()) {
						
						
						// put in code that combines the two blobs
						if (mainBlob.get(q).getDiam() > mainBlob.get(i).getDiam()) {
							
							
							
							
							mainBlob.get(q).changeSize(mainBlob.get(i).getSize());
							
							
							//with this change, errors are thrown when splitting after recombining 
							mainBlob.remove(i);
							
							
						} else {
							mainBlob.get(i).changeSize(mainBlob.get(q).getSize());
							
							//with this change, errors are thrown when splitting after recombining
							mainBlob.remove(q);
							
							
						}
						
						//blob removed, decrease numBlobs by one
						numBlobs--;

					}
				}
			}
			// add leftover decs to the mainBlobs
			mainBlob.get(q).changeDX(xDif - (int) xDif);
			mainBlob.get(q).changeDY(yDif - (int) yDif);
								
			// change the x and y values of mainBlob based on the position of the mouse
			mainBlob.get(q).changeX((int) xDif);
			mainBlob.get(q).changeY((int) yDif);
			
					

		}
		
		//stores the score
		int score = 0;
		
		//add up the blob sizes to get the score
		for (int d = 0; d < mainBlob.size(); d++) {
			score += (mainBlob.get(d).getSize()/200);
		}
		
		//section of code for debugging the center of mass
			 // appears to be small errors when the main blob is small
		float[] coordinates = new float[2];
		
		//get the center of mass
		coordinates = centerOfMass(mainBlob);
		
		//fill an oval at the center of mass
		g2.setColor(Color.BLACK);
		g2.fillOval((int)coordinates[0] - 10, (int)coordinates[1] - 10, 20, 20);

		//print the score...placement of score is hard-wired for this computer
		    //come back to this later to make score position it self automatically in bottom left-hand corner
		g.setColor(Color.BLACK);
		g.setFont(sanSerifFont);
		g.drawString("Score: " + score, 35, yMax - 100);

	}

	// method repaints the canvas
	public void actionPerformed(ActionEvent e) {
		repaint();
	}

	
	/*
	 * 
	 * 
	 * When splitting, one blob sometimes freezes...fix this!
	 * 
	 * 
	 * 
	 */
	
	
	// space bar event makes split
	public void keyPressed(KeyEvent e) {
		
		//gets the key code
		int code = e.getKeyCode();
		
		//if key code is the space bar
			  //************************* this part will eventually present problems...work with debug
		if (code == KeyEvent.VK_SPACE  ) {
			
			
			//variable that stores number of blobs that have been formed
			int k = 0;
			
			//go through each blob
			for (int j = 0; j < numBlobs; j++) {
				
				//check if the blob's size is greater than 90 and the mainBlob array can hold at least one more value
				if (mainBlob.get(j).getDiam() >= 90) {
					
					
					//if so, cut the blob in half
					int resul = (int) mainBlob.get(j).getSize()/2;

					// gets split size
					mainBlob.get(j).changeSize(-resul);
					
					//set the values of the new blob after the split (dimensions of new blob right next to old blob)
					mainBlob.add(new Blob(mainBlob.get(j).getX() + mainBlob.get(j).getDiam(), 
							mainBlob.get(j).getY() + mainBlob.get(j).getDiam(), mainBlob.get(j).getDiam(), 0,0));
					
					//variable that stores number of blobs should increase by one 
					k++;
					
					//start the timer for the blob that just divided
					mainBlob.get(j).timerStart();
				
				}
			}
			//keep track of number of blobs
			numBlobs+=k;
			
			//print the size of each blob for debugging
			for (int xd = 0; xd < mainBlob.size(); xd++) {
				System.out.print(mainBlob.get(xd).getSize() + " ");
			}
			System.out.println();
		}
	}

	

	public void keyTyped(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}
	
	//this method determines the center of mass of the blob in order to better judge how the blob
	//	 moves across the screen 
	//   center of mass will be the center of the screen...help makes movement more like Agar.io
	public float[] centerOfMass(ArrayList<Blob> blobs) {
		
		//array that stores the final coordinates of the center of mass
		float[] coordinates = new float[2];
		
		//place first 
		coordinates[0] = blobs.get(0).getX() + blobs.get(0).getRadius();
		coordinates[1] = blobs.get(0).getY() + blobs.get(0).getRadius();
		
		//loop that goes through all values in the blob array
				// fix this so the for loop only goes through the blob values with a radius > 0
		        // to make it more efficient 
		for(int i = 1; i < blobs.size(); i++) {
			
			//if the blob actually has size
			if(blobs.get(i).getDiam() != 0) {
				
				//x coordinates
				coordinates[0]+=(blobs.get(i).getX() + blobs.get(i).getRadius() - coordinates[0])/2;
				//y coordinates
				coordinates[1]+=(blobs.get(i).getY() + blobs.get(i).getRadius() - coordinates[1])/2;
				
			}
		}
		
		//return the final coordinates of the center of mass
		return coordinates;		
	}
}


