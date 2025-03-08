import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Random;

public class Fish {
    private static final int MIN_X = 100;
    private static final int MAX_X_OFFSET = 300;
    private static final int MIN_Y = 100;
    private static final int MAX_Y_OFFSET = 100;

    private static final int MAX_VELOCITY = 8;
    private static final int VELOCITY_CHANGE_LIMIT = 4;
    private static final int RANDOM_CHANGE_PROBABILITY = 7; // 1 in 7 chance

    private Component tank;
    private Image image1;
    private Image image2;
    private Point location;
    private Point velocity;
    private Rectangle edges;
    private Random random;

    public Fish(Image image1, Image image2, Rectangle edges, Component tank) {
        random = new Random(System.currentTimeMillis());
        this.tank = tank;
        this.image1 = image1;
        this.image2 = image2;
        this.edges = edges;

        // Initial random position within predefined range
        this.location = new Point(
                MIN_X + random.nextInt(MAX_X_OFFSET),
                MIN_Y + random.nextInt(MAX_Y_OFFSET));

        // Initial random velocity within range (-MAX_VELOCITY, MAX_VELOCITY)
        this.velocity = new Point(
                random.nextInt(MAX_VELOCITY * 2 + 1) - MAX_VELOCITY,
                random.nextInt(MAX_VELOCITY * 2 + 1) - MAX_VELOCITY);
    }

    public void swim() {
        // Occasionally adjust velocity
        if (random.nextInt(RANDOM_CHANGE_PROBABILITY) == 0) {
            velocity.x += random.nextInt(VELOCITY_CHANGE_LIMIT * 2 + 1) - VELOCITY_CHANGE_LIMIT;
            velocity.x = Math.max(-MAX_VELOCITY, Math.min(MAX_VELOCITY, velocity.x));

            velocity.y += random.nextInt(VELOCITY_CHANGE_LIMIT * 2 + 1) - VELOCITY_CHANGE_LIMIT;
            velocity.y = Math.max(-MAX_VELOCITY, Math.min(MAX_VELOCITY, velocity.y));
        }

        // Update position based on velocity
        location.x += velocity.x;
        location.y += velocity.y;

        // Boundary collision detection and response
        if (location.x < edges.x) {
            location.x = edges.x;
            velocity.x = -velocity.x;
        }
        if ((location.x + image1.getWidth(tank)) > (edges.x + edges.width)) {
            location.x = edges.x + edges.width - image1.getWidth(tank);
            velocity.x = -velocity.x;
        }
        if (location.y < edges.y) {
            location.y = edges.y;
            velocity.y = -velocity.y;
        }
        if ((location.y + image1.getHeight(tank)) > (edges.y + edges.height)) {
            location.y = edges.y + edges.height - image1.getHeight(tank);
            velocity.y = -velocity.y;
        }
    }

    public void draw(Graphics g) {
        // Select image based on movement direction
        g.drawImage(velocity.x < 0 ? image1 : image2, location.x, location.y, tank);
    }
}
