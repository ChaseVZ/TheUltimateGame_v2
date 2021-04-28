import java.util.Random;

public class BabyYodaFactory {

    private static int count = 0;
    private static final Random rand = new Random();

    public BabyYodaFactory() {}

    protected VirtualWorld getV() {return VirtualWorld.getVirtualWorld();}

    public void create(int LEVEL) {

        while (count < 2*LEVEL) {

            Point pos = new Point(32,4);

            while(getV().getWorld().getOccupancyCell(pos) instanceof Obstacle)
            {
                pos = new Point(12 + rand.nextInt(37 - 12), 7 + rand.nextInt(22 -7));
            }

            BabyYoda minion = new BabyYoda("babyYoda", pos, getV().getImageStore().getImageList("YodaMinionR"), 8000, 2000);
            getV().getWorld().addEntity(minion);
            minion.scheduleActions(getV().getScheduler(), getV().getWorld(), getV().getImageStore());
            count++;
        }
        count = 0;
    }
}
