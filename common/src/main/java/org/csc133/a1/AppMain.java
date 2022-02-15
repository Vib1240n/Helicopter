package org.csc133.a1;

import com.codename1.charts.util.ColorUtil;
import com.codename1.system.Lifecycle;
import com.codename1.ui.*;
import com.codename1.ui.geom.Point;
import com.codename1.ui.util.UITimer;

import java.util.ArrayList;
import java.util.Random;

import static com.codename1.ui.CN.SIZE_LARGE;
import static com.codename1.ui.CN.getCurrentForm;

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
            Font.STYLE_PLAIN, SIZE_LARGE);
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
        // TODO Auto-generated method stub
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
    private static int heli_radius;
    private static int speed;
    private static double angle;
    private static int endX;
    private static int endY;
    private static int startX;
    private static int startY;
    private static int water_tank;
    private static boolean isColliding;
    private final int heli_size;


    public Helicopter() {
        water_tank = 0;
        isColliding = false;
        river = new River();
        river_location = river.getLocation();
        location = centerLocation;
        heli_size = 35;
        heli_radius = heli_size / 2;
        angle = Math.toRadians(90);
        endX = location.getX();
        endY = location.getY() + heli_radius * 3;
        startX = location.getX();
        startY = location.getY();
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
                endX =
                        (int) (endX + Math.cos(angle));
                endY =
                        (int) (endY - Math.sin(angle));
                break;
            case -94 /*Right */:
                angle -= Math.toRadians(15);
                endY =
                        (int) (endX - Math.sin(angle));
                endX =
                        (int) (endY + Math.cos(angle));
                break;
            default:

        }

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
        if (startX > river_location.getX() && startX < (river_location.getX() + river.get_river_width())){
            if(startY > river_location.getY()&& startY < river_location.getY() + river.get_river_height()){
                isColliding = true;
            }
        }
        else{
            isColliding = false;
        }
    }

    public static void fillTank() {
        if (isColliding && water_tank < 1000000) {
            water_tank += 100;
        }
    }

    public void draw(Graphics g) {

        g.setFont(Game.font);
        g.setColor(ColorUtil.YELLOW);
        g.fillArc(startX - heli_radius, startY - heli_radius, heli_size,
                heli_size, 0, 360);
        g.setColor(ColorUtil.CYAN);
        g.drawLine(startX, startY, endX, endY);
        g.drawString("Speed: " + speed, startX + 15, startY + 15);
        g.drawString("X: " + startX, startX + 15, startY + 75);
        g.drawString("Y: " + startY, startX + 15, startY + 125);
        g.drawString("river Height: " + river.get_river_height(), (centerLocation.getX() + 400) - boxSize / 2, (centerLocation.getY() + 40) + boxSize / 2);
        g.drawString("river Height: " + river.get_river_height(), (centerLocation.getX() + 400) - boxSize / 2,
                (centerLocation.getY() + 40) + boxSize / 2);
        g.drawString("river Width: " + river.get_river_width(), (centerLocation.getX() + 400) - boxSize / 2,
                (centerLocation.getY() + 80) + boxSize / 2);
        g.setColor(ColorUtil.YELLOW);
        g.drawString("Water: " + water_tank,
                centerLocation.getX() - boxSize / 2,
                (centerLocation.getY() + 40) + boxSize / 2);

        g.setColor(ColorUtil.GREEN);
        g.drawRect(river_location.getX(), river_location.getY(), river.get_river_height(), river.get_river_width());
    }
}

class gameWorld {

    private final Random random = new Random();
    private final int rand;
    //private final ArrayList<Fire> fires;
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
        /**
         * Initiliazing variables
         */
        heli = new Helicopter();
        pad = new HeliPad();
        river = new River();
        rand = random.nextInt(500) / 150;
        /*
        Fire Locations
         */
        location_left = new Point(rand + Game.Disp_W / 5,
                rand + Game.Disp_H / 4 - Game.Disp_H / 4);
        location_right = new Point(rand + Game.Disp_W,
                rand + Game.Disp_H / 2 - Game.Disp_W - 2);
        location_center = new Point(rand + Game.Disp_W, rand + Game.Disp_H / 2);
        /*
         * Fire Sizes
         */
        fire_size_center = random.nextInt(500) + 300;
        fire_size_left = random.nextInt(200) + 100;
        fire_size_right = random.nextInt(500) + 200;
        /*
         * Fires on the screen; class objects
         */
        fire_center = new Fire(fire_size_center, location_center);
        fire_left = new Fire(fire_size_left, location_left);
        fire_right = new Fire(fire_size_right, location_right);
        //fires = new ArrayList<>();
        /*
         * Adding fire locations and sizes into fire class objects
         */
//        fires.add(fire_center);
//        fires.add(fire_left);
//        fires.add(fire_right);
        //draw(g, fire_center);

    }

    /**
     * @param g
     */
    public void draw(Graphics g) {
        heli.draw(g);
        pad.draw(g);
        river.draw(g);
        fire_center.draw(g);
        fire_left.draw(g);
        fire_right.draw(g);
    }

    public void quit() {
        Display.getInstance().exitApplication();
    }

    public void updateTick(int timer) {
        Helicopter.updateForward();
        Helicopter.isCollison();
        if (timer % 8 == 0) {
            fire_center.grow_fire();
            fire_left.grow_fire();
            fire_right.grow_fire();
        }
    }

}


class Fire {
    Point Location;
    private int fire_size;

    public Fire() {
    }

    public Fire(int fire_size, Point p) {
        Location = p;
        this.fire_size = fire_size;

    }

    public void grow_fire() {
        if (fire_size < 470) {
            fire_size += new Random().nextInt(5);
        }
    }

    public void draw(Graphics g) {
        g.setColor(ColorUtil.MAGENTA);
        g.fillArc(Location.getX(), Location.getY(), fire_size, fire_size, 0,
                360);
        g.setFont(Game.font);
        g.drawString("" + fire_size, Location.getX() + fire_size + 10,
                Location.getY() + fire_size + 5);
        // g.drawString("" + Location.getX(), Location.getX() + fire_size +
        // 10, Location.getY() + fire_size + 30);
        // g.drawString("" + Location.getY(), Location.getX() + fire_size +
        // 10, Location.getY() + fire_size + 30);

    }
}


class HeliPad {
    static Point centerLocation;
    private final int padSize;
    private final int radius;
    protected int boxSize;
    Point location;

    public HeliPad() {
        boxSize = 200;
        padSize = 150;
        radius = padSize / 2;
        location = new Point((Game.Disp_W / 2) - 100,
                (Game.getMin_disp() / 2) + 700);
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

        g.setColor(ColorUtil.YELLOW);
        g.drawString("Fuel", centerLocation.getX() - boxSize / 2,
                (centerLocation.getY() + 5) + boxSize / 2);


        g.setColor(ColorUtil.GREEN);
        g.drawLine(centerLocation.getX(), centerLocation.getY(),
                location.getX(), location.getY());
        /*
            Helipad inner circle design 
         */
        g.setColor(ColorUtil.GRAY);
        g.drawArc(centerLocation.getX() - radius,
                centerLocation.getY() - radius, padSize, padSize, 0, 360);
    }

}

class River {
    Point Location;
    private int river_width;
    private int river_height;
    //Height = 2048
    //Width = 2732

    public River() {
        Location = new Point( 0, Game.Disp_H - 1700);
        river_height = 300;
        river_width = Game.Disp_W - 10;

        //For Debug purposes
        System.out.println("Height: " + Game.Disp_H);
        System.out.println("Width: " + Game.Disp_W);
        System.out.println("river x: " + Location.getX());
        System.out.println("river Y: " + Location.getY());
        System.out.println("Width rect: " + Game.Disp_W * 2);
        System.out.println("Height rect: " + Game.Disp_H / 8);
    }

    public Point getLocation() {
        return Location;
    }

    public int get_river_width() {
        return river_width;
    }

    public int get_river_height() {
        return river_height;
    }

    public void draw(Graphics g) {

        g.setColor(ColorUtil.BLUE);
        g.drawRect(Location.getX(), Location.getY(), river_width,
                river_height);
    }

}

//