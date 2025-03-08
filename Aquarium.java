import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

public class Aquarium extends Frame implements Runnable {
    MediaTracker tracker;
    Image aquariumImage;
    Image[] fishImages = new Image[2];
    Image foodImage;

    Image memoryImage;
    Graphics memoryGraphics;

    Thread thread;

    int initialFishCount = 12;
    Vector<Fish> fishes = new Vector<Fish>();
    ArrayList<Food> foodList = new ArrayList<>();

    boolean runOK = true;

    // Display text
    String statusText = "";
    long statusTextDisplayTime = 0;
    long STATUS_TEXT_DURATION = 3000; // 3 seconds

    public static void main(String[] args) {
        new Aquarium();
    }

    public void run() {
        Rectangle edges = new Rectangle(0 + getInsets().left, 0 + getInsets().top,
                getSize().width - (getInsets().left + getInsets().right),
                getSize().height - (getInsets().top + getInsets().bottom));

        for (int loopIndex = 0; loopIndex < initialFishCount; loopIndex++) {
            fishes.add(new Fish(fishImages[0], fishImages[1], edges, this));
            try {
                Thread.sleep(20);
            } catch (Exception exp) {
                System.out.println(exp.getMessage());
            }
        }

        int cycleCounter = 0;
        int aliveCount = fishes.size();

        while (runOK) {
            cycleCounter++;

            // Check for food and try eating
            for (Fish fish : fishes) {
                fish.checkForFood(foodList);
            }

            // Process fish movement and hunger
            int currentAliveCount = 0;
            for (Fish fish : fishes) {
                fish.swim();
                fish.tryToEat(foodList);

                // Update hunger every 5 cycles to slow down hunger rate
                if (cycleCounter % 5 == 0) {
                    fish.updateHunger();
                }

                if (!fish.isDead()) {
                    currentAliveCount++;
                }
            }

            // Display status message if fish died this cycle
            if (currentAliveCount < aliveCount) {
                int deadCount = aliveCount - currentAliveCount;
                statusText = deadCount + " fish died of hunger!";
                statusTextDisplayTime = System.currentTimeMillis();
                aliveCount = currentAliveCount;
            }

            // Process food movement
            for (Iterator<Food> iterator = foodList.iterator(); iterator.hasNext();) {
                Food food = iterator.next();
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

        // Draw all fish (dead or alive)
        for (Fish fish : fishes) {
            fish.draw(memoryGraphics);
        }

        // Draw all food
        for (Food food : foodList) {
            food.draw(memoryGraphics, this);
        }

        // Display status text if needed
        if (System.currentTimeMillis() - statusTextDisplayTime < STATUS_TEXT_DURATION) {
            memoryGraphics.setColor(Color.WHITE);
            memoryGraphics.setFont(new Font("Arial", Font.BOLD, 18));
            memoryGraphics.drawString(statusText, 20, 60);
        }

        g.drawImage(memoryImage, 0, 0, this);
    }
}