import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Vector;

public class Aquarium extends Frame implements Runnable {
    MediaTracker tracker;
    Image aquariumImage;
    Image[] fishImages = new Image[2];
    Image foodImage;

    Image memoryImage;
    Graphics memoryGraphics;

    Thread thread;

    int numberFish = 12;
    Vector<Fish> fishes = new Vector<Fish>();
    ArrayList<Food> foodList = new ArrayList<>();

    boolean runOK = true;

    public static void main(String[] args) {
        System.out.println("Aquarium starting");
        new Aquarium();
    }

    public void run() {
        Rectangle edges = new Rectangle(0 + getInsets().left, 0 + getInsets().top,
                getSize().width - (getInsets().left + getInsets().right),
                getSize().height - (getInsets().top + getInsets().bottom));

        for (int loopIndex = 0; loopIndex < numberFish; loopIndex++) {
            fishes.add(new Fish(fishImages[0], fishImages[1], edges, this));
            try {
                Thread.sleep(20);
            } catch (Exception exp) {
                System.out.println(exp.getMessage());
            }
        }

        while (runOK) {
            for (Fish fish : fishes) {
                fish.moveTowardsFood(foodList);
                fish.swim();
            }
            for (Food food : foodList) {
                food.fall();
            }
            try {
                Thread.sleep(20);
            } catch (Exception exp) {
                System.out.println(exp.getMessage());
            }
            repaint();
        }
    }

    Aquarium() {
        setTitle("The Aquarium");
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                runOK = false;
                System.exit(0);
            }
        });

        tracker = new MediaTracker(this);
        fishImages[0] = Toolkit.getDefaultToolkit().getImage("fish-left.png")
                .getScaledInstance(50, 30, Image.SCALE_SMOOTH);
        fishImages[1] = Toolkit.getDefaultToolkit().getImage("fish-right.png")
                .getScaledInstance(50, 30, Image.SCALE_SMOOTH);
        aquariumImage = Toolkit.getDefaultToolkit().getImage("aquarium.jpg");
        foodImage = Toolkit.getDefaultToolkit().getImage("food.png")
                .getScaledInstance(10, 10, Image.SCALE_SMOOTH);

        tracker.addImage(fishImages[0], 0);
        tracker.addImage(fishImages[1], 0);
        tracker.addImage(aquariumImage, 0);
        tracker.addImage(foodImage, 0);

        try {
            tracker.waitForID(0);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        setSize(aquariumImage.getWidth(this), aquariumImage.getHeight(this));
        setResizable(false);
        setVisible(true);

        memoryImage = createImage(getSize().width, getSize().height);
        memoryGraphics = memoryImage.getGraphics();

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                foodList.add(new Food(e.getX(), e.getY(), foodImage));
                repaint();
            }
        });

        thread = new Thread(this);
        thread.start();
    }

    public void update(Graphics g) {
        memoryGraphics.drawImage(aquariumImage, 0, 0, this);
        for (Fish fish : fishes) {
            fish.draw(memoryGraphics);
        }
        for (Food food : foodList) {
            food.draw(memoryGraphics, this);
        }
        g.drawImage(memoryImage, 0, 0, this);
    }
}
