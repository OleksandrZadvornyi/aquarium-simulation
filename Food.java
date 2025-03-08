import java.awt.Image;
import java.awt.Graphics;
import java.awt.Component;

class Food {
    int x, y;
    int speed = 2;
    Image image;
    boolean reachedBottom = false;

    public Food(int x, int y, Image image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public void fall() {
        if (!reachedBottom) {
            y += speed;
            if (y >= 450) { // Ліміт для падіння
                reachedBottom = true;
            }
        }
    }

    public void draw(Graphics g, Component c) {
        g.drawImage(image, x, y, c);
    }
}