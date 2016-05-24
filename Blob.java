package server;

import java.util.Timer;
import java.util.TimerTask;

public class Blob {
	
	
	//x and y coordinates and size (area) of the blob
	private float x;
	private float y;
	private float size;
	
	//diameter of the blob
	private float diam;
	
	//stores the leftover change in x and y
	private float dX;
	private float dY; 
	
	//whether or not the blob can combine 
	private boolean combine = false;
	
	//timer for when the blob can recombine
	private Timer time;
	
	//constructor
	public Blob(float a, float b, float c, float d, float e) {
		
		x = a;
		y = b;
		diam = c;
		dX = d;
		dY = e;
		
		//start a timer for when it can recombine
		this.timerStart();
		
		//determine the area of the blob using the diameter
		size = (float)(Math.PI * Math.pow(diam/2, 2));
	}
	//methods for getting and setting values
	public float getSize() {
		return size;
	}
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	public float getDX() {
		return dX;
	}
	public float getDY() {
		return dY;
	}
	public void changeDX(float ddx) {
		
		//sometimes number will become NaN...
		if(Float.isNaN(ddx)) {
			//set leftover change in x to 0 when this happens
			dX = 0;
		}
		else {
			dX = ddx;
		}
	}
	public void changeDY(float ddy) {
		
		//sometimes number will become NaN...
		if(Float.isNaN(ddy)) {
			//set leftover change in y to 0 when this happens
			dY = 0;
		}
		else {
			dY = ddy;
		}
	}
	
	public float getDiam() {
		
		return diam;
	}
	public float getRadius() {
		
		return diam/2;
	}
	
	public void changeSize(float area) {
		
		//s is the size change
		size += area;
		diam = 2 * (float)Math.sqrt(size / Math.PI);
		
	}
	
	public void setSize(float newSize) {
		
		size = newSize;
		
		diam = 2 * (float)Math.sqrt(newSize / Math.PI);
		
		
	}
	public void setRadius(float r) {
		diam = 2 * r;
	}
	public void changeY(float yy) {
		y += yy;
	}
	public void changeX(float xx) {
		x += xx;
	}
	public void setX(float xx) {
		x = xx;
	}
	public void setY(float yy) {
		y = yy;
	}
	//method that returns whether or not that blob can combine with another blob
	public boolean canCombine() {
	
		return combine;
	}
	
	//sets whether or not the blob can combine
	public void setCombine(boolean comb) {
		combine = comb;
	}
	
	//method that starts the timer for when the blob can recombine
	public void timerStart() {
		
		//starts timer and makes blob not able to combine 
		combine = false;
		time = new Timer();
		
		//make a timer so the blob can combine after a certain period of time
		time.schedule(new TimerTask() { 
			public void run() { 
				combine = true;
			}
			//period of time is arbitrary now, will work on this while debugging
		}, 100*(long)size);
	}
	
}
