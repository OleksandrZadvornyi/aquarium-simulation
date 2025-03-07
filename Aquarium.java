import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

public class Aquarium extends Frame implements Runnable {
    MediaTracker tracker;
    Image aquariumImage;
    Image[] fishImages = new Image[2];

    Image memoryImage;
    Graphics memoryGraphics;

    Thread thread;

    int numberFish = 12;
    Vector<Fish> fishes = new Vector<Fish>();

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

        Fish fish;
        while (runOK) {
            for (int loopIndex = 0; loopIndex < numberFish; loopIndex++) {
                fish = (Fish) fishes.elementAt(loopIndex);
                fish.swim();
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
        fishImages[0] = Toolkit.getDefaultToolkit().getImage("fish1.png");
        tracker.addImage(fishImages[0], 0);
        fishImages[1] = Toolkit.getDefaultToolkit().getImage("fish2.png");
        tracker.addImage(fishImages[1], 0);
        aquariumImage = Toolkit.getDefaultToolkit().getImage("aquarium.jpg");
        tracker.addImage(aquariumImage, 0);
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

        thread = new Thread(this);
        thread.start();
    }

    public void update(Graphics g) {
        memoryGraphics.drawImage(aquariumImage, 0, 0, this);
        for (int loopIndex = 0; loopIndex < numberFish; loopIndex++) {
            fishes.elementAt(loopIndex).draw(memoryGraphics);
        }
        g.drawImage(memoryImage, 0, 0, this);
    }
}
