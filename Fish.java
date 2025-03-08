import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

public class Fish {
    private static final int MIN_X = 100;
    private static final int MAX_X_OFFSET = 300;
    private static final int MIN_Y = 100;
    private static final int MAX_Y_OFFSET = 100;

    private static final int MAX_VELOCITY = 4;
    private static final int VELOCITY_CHANGE_LIMIT = 2;
    private static final int RANDOM_CHANGE_PROBABILITY = 7; // 1 in 7 chance

    // Detection and eating parameters
    private static final int FOOD_DETECTION_RANGE = 150;
    private static final int EATING_DISTANCE = 20;
    private static final int CHASE_SPEED = 6;

    // Hunger system parameters
    private static final int MAX_HUNGER = 1000;
    private static final int HUNGER_INCREASE_RATE = 2;
    private static final int FOOD_HUNGER_DECREASE = 300;
    private static final int HUNGER_WARNING = MAX_HUNGER * 3 / 4;

    private Component tank;
    private Image image1;
    private Image image2;
    private Point location;
    private Point velocity;
    private Rectangle edges;
    private Random random;
    private boolean chasingFood = false;
    private Food targetFood = null;

    // Hunger system variables
    private int hungerLevel = 0;
    private boolean isDead = false;

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

        // Start with a random hunger level so not all fish get hungry at the same time
        this.hungerLevel = random.nextInt(MAX_HUNGER / 2);
    }

    public boolean isDead() {
        return isDead;
    }

    public void updateHunger() {
        if (isDead)
            return;

        hungerLevel += HUNGER_INCREASE_RATE;
        if (hungerLevel >= MAX_HUNGER) {
            isDead = true;
        }
    }

    public void checkForFood(ArrayList<Food> foodList) {
        if (isDead)
            return;

        // If already chasing food, continue unless it's gone
        if (chasingFood && targetFood != null) {
            if (!foodList.contains(targetFood)) {
                chasingFood = false;
                targetFood = null;
            }
            return;
        }

        // Look for the closest food if not already chasing
        Food closest = null;
        double closestDistance = FOOD_DETECTION_RANGE;

        for (Food food : foodList) {
            double distance = calculateDistance(location.x, location.y, food.x, food.y);
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = food;
            }
        }

        if (closest != null) {
            targetFood = closest;
            chasingFood = true;
        }
    }

    private double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public boolean tryToEat(ArrayList<Food> foodList) {
        if (isDead)
            return false;

        if (targetFood != null && foodList.contains(targetFood)) {
            double distance = calculateDistance(
                    location.x + image1.getWidth(tank) / 2,
                    location.y + image1.getHeight(tank) / 2,
                    targetFood.x + targetFood.image.getWidth(tank) / 2,
                    targetFood.y + targetFood.image.getHeight(tank) / 2);

            if (distance < EATING_DISTANCE) {
                foodList.remove(targetFood);
                targetFood = null;
                chasingFood = false;

                // Reduce hunger when eating
                hungerLevel = Math.max(0, hungerLevel - FOOD_HUNGER_DECREASE);

                return true;
            }
        }
        return false;
    }

    public void swim() {
        if (isDead)
            return;

        if (chasingFood && targetFood != null) {
            // Calculate direction to food
            int centerX = location.x + image1.getWidth(tank) / 2;
            int centerY = location.y + image1.getHeight(tank) / 2;
            int foodCenterX = targetFood.x + targetFood.image.getWidth(tank) / 2;
            int foodCenterY = targetFood.y + targetFood.image.getHeight(tank) / 2;

            // Set velocity toward food
            if (centerX < foodCenterX) {
                velocity.x = Math.min(CHASE_SPEED, foodCenterX - centerX);
            } else {
                velocity.x = Math.max(-CHASE_SPEED, foodCenterX - centerX);
            }

            if (centerY < foodCenterY) {
                velocity.y = Math.min(CHASE_SPEED, foodCenterY - centerY);
            } else {
                velocity.y = Math.max(-CHASE_SPEED, foodCenterY - centerY);
            }
        } else {
            // Normal swimming behavior when not chasing food
            if (random.nextInt(RANDOM_CHANGE_PROBABILITY) == 0) {
                velocity.x += random.nextInt(VELOCITY_CHANGE_LIMIT * 2 + 1) - VELOCITY_CHANGE_LIMIT;
                velocity.x = Math.max(-MAX_VELOCITY, Math.min(MAX_VELOCITY, velocity.x));

                velocity.y += random.nextInt(VELOCITY_CHANGE_LIMIT * 2 + 1) - VELOCITY_CHANGE_LIMIT;
                velocity.y = Math.max(-MAX_VELOCITY, Math.min(MAX_VELOCITY, velocity.y));
            }
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
        if (isDead) {
            // Draw a dead fish (upside down)
            g.drawImage(velocity.x < 0 ? image1 : image2,
                    location.x, location.y + image1.getHeight(tank),
                    image1.getWidth(tank), -image1.getHeight(tank), tank);
            return;
        }

        // Draw regular fish
        g.drawImage(velocity.x < 0 ? image1 : image2, location.x, location.y, tank);

        // Draw hunger indicator if getting hungry
        if (hungerLevel > HUNGER_WARNING) {
            g.setColor(Color.RED);
            int indicatorSize = 5;
            g.fillOval(location.x + image1.getWidth(tank) / 2 - indicatorSize / 2,
                    location.y - indicatorSize - 2,
                    indicatorSize, indicatorSize);
        }
    }
}