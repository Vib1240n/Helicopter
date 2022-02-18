package org.csc133.a1;

import com.codename1.charts.util.ColorUtil;
import com.codename1.system.Lifecycle;
import com.codename1.ui.*;
import com.codename1.ui.geom.Point;
import com.codename1.ui.util.UITimer;

import java.util.Random;

import static com.codename1.ui.CN.*;

/**
 * This file was generated by <a href="https://www.codenameone.com/">Codename
 * One</a> for the purpose
 * of building native mobile applications using Java.
 */
public class AppMain extends Lifecycle {
	private Form current;

	@Override
	public void runApp() {
	}

	public void start() {
		if (current != null) {
			current.show();
			return;
		}
		new Game().show();
	}

	public void stop() {
		current = getCurrentForm();
		if (current instanceof Dialog) {
			((Dialog) current).dispose();
			current = getCurrentForm();
		}
	}

}

class Game extends Form implements Runnable {


	final static int Disp_H = Display.getInstance().getDisplayHeight();
	final static int Disp_W = Display.getInstance().getDisplayWidth();
	static Font font = Font.createSystemFont(Font.FACE_SYSTEM,
			Font.STYLE_PLAIN, SIZE_SMALL);
	private final gameWorld world;
	UITimer timer;
	private int tick;

	public Game() {
		world = new gameWorld();
		tick = 0;
		timer = new UITimer(this);
		timer.schedule(100, true, this);
		addKeyListener('Q', (evt) -> world.quit());
		addKeyListener(-93, (evt) -> Helicopter.movement(-93));
		addKeyListener(-94, (evt) -> Helicopter.movement(-94));
		addKeyListener(-91, (evt) -> Helicopter.movement(-91));
		addKeyListener(-92, (evt) -> Helicopter.movement(-92));
		addKeyListener('d', (evt) -> Helicopter.fillTank());
		addKeyListener('f', (evt) -> Fire.extinguishFire());
	}

	public static int getMin_disp() {
		return Math.min(Disp_H, Disp_W);
	}

	public static int getMax_disp() {
		return Math.max(Disp_H, Disp_W);
	}

	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(ColorUtil.BLACK);
		g.fillRect(0, 0, Game.Disp_W, Game.Disp_H);
		world.draw(g);
	}

	@Override
	public void run() {
		int return_tick = Tick();
		world.updateTick(return_tick);
		repaint();
	}

	public int Tick() {
		tick++;
		return tick;
	}


}

class Helicopter extends HeliPad {
	static Point location;
	static River river;
	static Point river_location;
	static Point fire_location;
	// static Fire fire;
	// private static int fire_size;
	// private static int fire_radius;
	private static int heli_radius;
	private static int speed;
	private static double angle;
	private static int endX;
	private static int endY;
	private static int startX;
	private static int startY;
	private static int water_tank;
	private static boolean isColliding;
	private static boolean isCollidingfire;
	private static Random rand;
	private final int heli_size;


	public Helicopter() {
		water_tank = 0;
		isColliding = false;
		river = new River();
		// fire = new Fire();
		// fire_location = fire.location();
		// fire_size = fire.size();
		//fire_radius = fire_size/2;
		isCollidingfire = false;
		river_location = river.getLocation();
		location = centerLocation;
		heli_size = 35;
		heli_radius = heli_size / 2;
		angle = Math.toRadians(90);
		endX = location.getX();
		endY = location.getY() + heli_radius * 3;
		startX = location.getX();
		startY = location.getY();
		rand = new Random();
	}

	public static void movement(int input) {
        /*
        movements
        */
		switch (input) {
			case -92 /* back */:
				if (speed > 0) {
					speed--;
				}
				break;
			case -91 /*Forward */:
				if (speed < 10) {
					speed++;
				}
				break;
			case -93 /*Left*/:
				angle += Math.toRadians(15);
				endX = (int) (endX + Math.cos(angle));
				endY = (int) (endY - Math.sin(angle));
				break;
			case -94 /*Right */:
				angle -= Math.toRadians(15);
				endY = (int) (endX - Math.sin(angle));
				endX = (int) (endY + Math.cos(angle));
				break;
			default:

		}

	}

	public int water_tank(){
		return water_tank;
	}

	public static void updateForward() {
		location.setX((int) (location.getX() + Math.cos(angle) * speed));
		location.setY((int) (location.getY() - Math.sin(angle) * speed));
		startX = location.getX();
		startY = location.getY();
		endY = (int) (location.getY() - Math.sin(angle));
		endX = (int) (location.getX() + Math.cos(angle) + heli_radius * 3);
	}

	public static void isCollison() {
		if (startX > river_location.getX() && startX < (river_location.getX() + river.get_river_width())) {
			isColliding =
					startY > river_location.getY() && startY < river_location.getY() + river.get_river_height();
		} else {
			isColliding = false;
		}
	}

	public static void isCollisionFire(Fire fire) {
		
		if(startX > fire.location().getX() && startX < ( fire.location().getX() + fire.size())){
			isCollidingfire = 
			startY > fire.location().getY() && startY < fire.location().getY() + fire.size();
			if(isCollidingfire && fire.size() > 0){
				fire.extinguishFire();
				water_tank -= 100;
			}
		}else{
			isCollidingfire = false;
		}
		isCollidingfire = true;
	}

	public static void fillTank() {
		if (isColliding && water_tank < 1000) {
			water_tank += 100;
		}
	}

	public void draw(Graphics g) {

		g.setFont(Game.font);
		g.setColor(ColorUtil.YELLOW);
		g.fillArc(startX - heli_radius, startY - heli_radius, heli_size,
				heli_size, 0, 360);
		g.setColor(ColorUtil.YELLOW);
		g.drawLine(startX, startY, endX, endY);
		g.drawString("Speed: " + speed, startX + 15, startY + 15);


		g.setColor(ColorUtil.YELLOW);
		g.drawString("Water: " + water_tank,
				centerLocation.getX() - boxSize / 2,
				(centerLocation.getY() + 40) + boxSize / 2);
	}
}

class gameWorld {

	private final Random random = new Random();
	private final int rand;
	private final int fire_size_center;
	private final int fire_size_left;
	private final int fire_size_right;
	Helicopter heli;
	Point location_left;
	Point location_right;
	Point location_center;
	HeliPad pad;
	River river;
	Fire fire_center;
	Fire fire_right;
	Fire fire_left;

	public gameWorld() {
		/*
		 * Initiliazing variables
		 */
		heli = new Helicopter();
		pad = new HeliPad();
		river = new River();
		rand = random.nextInt(200);
        /*
        Fire Locations
         */
		location_left =
				new Point((HeliPad.centerLocation.getX() - Game.Disp_H / 2) + rand, (HeliPad.centerLocation.getY() - Game.Disp_W) + rand);
		location_right =
				new Point((HeliPad.centerLocation.getX() - Game.Disp_H / 2) + rand, (HeliPad.centerLocation.getY() - Game.Disp_W / 3) + rand); //Showing up on screen
		location_center =
				new Point((HeliPad.centerLocation.getX() + Game.Disp_H / 8) + rand, (HeliPad.centerLocation.getY() - Game.Disp_W / 3) + rand);
         /*
         Fire Sizes
         */
		fire_size_center = random.nextInt(100) + 200;
		fire_size_left = random.nextInt(100) + 150;
		fire_size_right = random.nextInt(50) + 100;
		/*
		 * Fires on the screen; class objects
		 */
		fire_center = new Fire(fire_size_center, location_center);
		fire_left = new Fire(fire_size_left, location_left);
		fire_right = new Fire(fire_size_right, location_right);
	}

	/**
	 *
	 */
	public void draw(Graphics g) {
		river.draw(g);
		fire_center.draw(g);
		fire_left.draw(g);
		fire_right.draw(g);
		pad.draw(g);
		heli.draw(g);
	}

	public void quit() {
		Display.getInstance().exitApplication();
	}

	public void updateTick(int timer) {
		Helicopter.updateForward();
		Helicopter.isCollison();
		Helicopter.isCollisionFire(fire_center);
		Helicopter.isCollisionFire(fire_left);
		Helicopter.isCollisionFire(fire_right);
		if (timer % 8 == 0) {
			fire_center.grow_fire();
			fire_left.grow_fire();
			fire_right.grow_fire();
		}
	}

}

class Display extends Form{
	Dialog d;

}
class Fire {
	Point Location;
	private static Random rand;
	private static Helicopter heli;
	private static int fire_size;

	public Fire(){
		//Empty Contructor 
		rand = new Random();
		heli = new Helicopter();
	}

	public Fire(int fire_size, Point p) {
		Location = p;
		this.fire_size = fire_size;

	}

	public Point location(){
		return Location;
	}

	public int size(){
		return fire_size;
	}

	public void grow_fire() {
		if (fire_size < 470) {
			fire_size += new Random().nextInt(5);
		}
	}

	public static void extinguishFire(){
		fire_size -= Math.min(heli.water_tank() / 5, rand.nextInt(heli.water_tank() / 3));
	}

	public void draw(Graphics g) {
		g.setColor(ColorUtil.MAGENTA);
		g.fillArc(Location.getX(), Location.getY(), fire_size, fire_size, 0,
				360);
		g.setFont(Game.font);
		g.drawString("" + fire_size, Location.getX() + fire_size + 10,
				Location.getY() + fire_size + 5);

		g.setColor(ColorUtil.YELLOW);
		g.drawString("x: " + Location.getX() + ", " + "y: " + Location.getY(),
				Location.getX(), Location.getY());

	}
}


class HeliPad {
	static Point centerLocation;
	private final int padSize;
	private final int radius;
	protected int boxSize;
	Point location;

	/*
	Helipad constructor
	 */
	public HeliPad() {
		boxSize = 200;
		padSize = 150;
		radius = padSize / 2;
		location = new Point((Game.Disp_W / 2) - 25,
				(Game.getMin_disp() / 2) + 500);
		centerLocation = new Point(location.getX() + boxSize / 2,
				location.getY() + boxSize / 2);
	}

	public Point getCenter() {
		return centerLocation;
	}

	public void draw(Graphics g) {
        /*
            Helipad border design
        */
		g.setColor(ColorUtil.GRAY);
		g.drawRect(location.getX(), location.getY(), 200, 200);

		/*
		 * Fuel label drawing
		 */
		g.setColor(ColorUtil.YELLOW);
		g.drawString("Fuel", centerLocation.getX() - boxSize / 2,
				(centerLocation.getY() + 5) + boxSize / 2);
        /*
            Helipad inner circle design
         */
		g.setColor(ColorUtil.GRAY);
		g.drawArc(centerLocation.getX() - radius,
				centerLocation.getY() - radius, padSize, padSize, 0, 360);
	}

}


//
class River {
	Point Location;
	private final int river_width;
	private final int river_height;

	public River() {
		Location = new Point(0, Game.Disp_H - 1300);
		river_height = 300;
		river_width = Game.Disp_W;
	}

	/**
	 * Getter methods for correct collision checking for helicopter
	 */
	public Point getLocation() {
		return Location;
	}

	public int get_river_width() {
		return river_width;
	}

	public int get_river_height() {
		return river_height;
	}
    /*
    Draw method for river
     */

	public void draw(Graphics g) {

		g.setColor(ColorUtil.BLUE);
		g.drawRect(Location.getX(), Location.getY(), river_width,
				river_height);
	}

}
