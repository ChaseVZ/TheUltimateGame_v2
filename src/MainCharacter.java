import com.sun.tools.javac.Main;
import processing.core.PImage;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MainCharacter extends ResourceEntity{

    private static MainCharacter m;

    private int WeaponRange;
    private HeartFactory h = new HeartFactory();

    private MainCharacter(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, 0, resourceLimit, resourceCount, actionPeriod, animationPeriod);
        this.WeaponRange = 4;
    }

    public static MainCharacter getMainCharacter() {
        if (m == null)
            return m = new MainCharacter("main_1", null, null, 0, 0, 0, 0);
        return m;
    }

    public HeartFactory getH() {return h;}
    public int getWeaponRange() {return WeaponRange;}
    public void incrementWeaponRange() {WeaponRange += 1;}
    public void LoadHearts() { h.loadHearts(); }

    public void move(WorldModel world, int horiz, int vert) {
        Point newPos = new Point(super.getPosition().x + horiz, super.getPosition().y + vert);

        if (world.withinBounds(newPos) && !(world.isOccupied(newPos))) {
            world.moveEntity(this, newPos);
            checkPortals(world);
        }

        else if (world.isOccupied(newPos)) {
            if (world.getOccupancyCell(newPos) instanceof Portal)
                ((Portal) world.getOccupancyCell(newPos)).PortalMePapa();
        }
    }

    public void checkPortals(WorldModel world)
    {
        Entity e = getV().getWorld().findNearest2(getPosition(), Portal.class);
        if(e != null) {
            if (world.withinReach(getPosition(), e.getPosition(), 4))
            {
                ((Portal)e).unSetArrow();
                ((Portal)e).setArrow();
            }
            else
            {
                ((Portal)e).unSetArrow();
            }
        }
    }


    public void die()
    {
        getV().getWorld().removeAllEntities();
        getV().getWorld().removeEntity( this);
        getV().getScheduler().unscheduleAllEvents( this);
        getV().mainDied();
    }

    public void Attack(String direction)
    {
        String key;

        if(direction.equals("up"))
            key = "LaserBlueUp";
        else if(direction.equals("left"))
            key = "LaserBlueLeft";
        else if(direction.equals("right")) {
            key = "LaserBlueRight";
            direction = "right";
        }
        else {
            key = "LaserBlueDown";
            direction = "down";
        }


        Point nextPos = getNextPoint(direction, getPosition());
        if(!getV().getWorld().isOccupied(nextPos)) {
            Laser l = new Laser("laser", nextPos, getV().getImageStore().getImageList(key), 0, 4000, WeaponRange, direction);
            getV().getWorld().addEntity(l);
            l.scheduleActions(getV().getScheduler(), getV().getWorld(), getV().getImageStore());
        }
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {}


}
