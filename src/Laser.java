import processing.core.PImage;
import java.util.List;

public class Laser extends ActiveEntity {

    private int range;
    private String direction;
    private int distance;
    private static Point prevPoint = new Point(42, 2);
    private static Point prevPrevPoint = new Point(42, 3);
    private static String SanHoloID = "sanHolo";
    private static String TerminatorID = "terminator";
    private static String TerminatorBrainID = "terminatorBrain";
    private static String BabyYodaID = "babyYoda";

    public Laser(String id, Point position, List<PImage> images, int imageIndex, int actionPeriod, int range, String direction){
        super(id, position, images, imageIndex, actionPeriod);
        this.range = range;
        this.direction = direction;
        this.distance = 0;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Point nextPos = getNextPoint(direction, getPosition());
        long nextPeriod = super.getActionPeriod();

        if (distance <= range)
        {

            if (moveTo(world, scheduler, nextPos, imageStore)) //if T then the laser needs to be destroyed otherwise continue
            {
                world.removeEntity(this);
                scheduler.unscheduleAllEvents(this);
            }
            else {
                world.moveEntity(this, nextPos);
                prevPrevPoint = prevPoint;
                prevPoint = getPosition();
            }


            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), nextPeriod);
            distance ++;
        }
        else {  //reached end of path
            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);
        }
    }


    public boolean moveTo(WorldModel world, EventScheduler scheduler, Point nextPos, ImageStore imageStore) {
        if(world.isOccupied(prevPoint) && ((world.getOccupancyCell(prevPoint) instanceof TerminatorClone) || (world.getOccupancyCell(prevPoint) instanceof BabyYoda)
                || world.getOccupancyCell(prevPoint) instanceof TerminatorBrain || world.getOccupancyCell(prevPoint) instanceof SanHolo))
        {
            removeEntity(world.getOccupancyCell(prevPoint), prevPoint, world, scheduler, imageStore);
            return true;
        }

//        if(world.isOccupied(prevPrevPoint) && ((world.getOccupancyCell(prevPrevPoint) instanceof TerminatorClone) || (world.getOccupancyCell(prevPrevPoint) instanceof BabyYoda
//                || world.getOccupancyCell(prevPrevPoint) instanceof TerminatorBrain || world.getOccupancyCell(prevPoint) instanceof SanHolo)))
//        {
//            removeEntity(world.getOccupancyCell(prevPrevPoint), prevPrevPoint, world, scheduler, imageStore);
//            return true;
//        }

        if (world.withinBounds(nextPos)) {
            if(world.isOccupied(nextPos))
            {
                Entity e = world.getOccupancyCell(nextPos);
                if (e instanceof BabyYoda || e instanceof TerminatorClone || e instanceof TerminatorBrain || e instanceof SanHolo) {
                    removeEntity(e, nextPos, world, scheduler, imageStore);
                    return true;
                }
                return true;
            }
            return false;
        }
        return true;
    }


    public void removeEntity(Entity e, Point pos, WorldModel world, EventScheduler scheduler, ImageStore imageStore)
    {
        incrementCurrency(e);
        if (e.getId().equals(TerminatorID))
        {
            if(getV().getTerminator().getMainBossSpawner() != null) {
                getV().getTerminator().getMainBossSpawner().scheduleActions(scheduler, world, imageStore);
                getV().getTerminator().getMainBossSpawner().decreaseCloneCount();
            }
        }

        world.removeEntity(e);
        scheduler.unscheduleAllEvents(e);
        SpawnQuake(pos);
    }

    public void incrementCurrency(Entity e)
    {
       ItemShop itemShop = ItemShop.getItemShop();

        String MobID = e.getId();

        if (MobID.equals(SanHoloID)) {
            getV().setSanHoloFlag(true);
            itemShop.SanHoloKill();
        }
        else if (MobID.equals(BabyYodaID))
            itemShop.BabyYodaKill();
        else if (MobID.equals(TerminatorID))
            itemShop.TerminatorKill();
        else if (MobID.equals(TerminatorBrainID)) {
            getV().incrementLevel();
            getV().setTerminatorBrainFlag(false);
            getV().setSanHoloFlag(false);
            itemShop.TerminatorBrainKill();
        }

    }


}
