import java.util.Random;

public class OreFactory {

    private static final Random rand = new Random();
    private static int count = 0;

    public OreFactory(){}

    protected VirtualWorld getV() {return VirtualWorld.getVirtualWorld();}

    public void create(int LEVEL)    {

        while (count <= 6 * LEVEL)
        {
            Point openPt = new Point(32,4);

            while(getV().getWorld().getOccupancyCell(openPt) instanceof Obstacle)
            {
                openPt = new Point(12 + rand.nextInt(37 - 12), 7 + rand.nextInt(22 - 7));
            }

            Ore ore = new Ore("ore", openPt, getV().getImageStore().getImageList("BasicOre1"), 0, 100*LEVEL);
            getV().getWorld().addEntity(ore);
            count++;
        }
    }

    public int getCount() {return count;}
    public void setCount(int c) {count = c;}

}
