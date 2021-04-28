import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class BabyYoda extends Moveable {

    private static final String QUAKE_KEY = "quake";

    public BabyYoda(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, 0, actionPeriod, animationPeriod);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Entity bossTarget = world.findNearest2(this.getPosition(), Ore.class);
        long nextPeriod = super.getActionPeriod();

        if(bossTarget == null)
            bossTarget = world.findNearest2(this.getPosition(), MainCharacter.class);

        if (bossTarget != null)
        {
            Point tgtPos = bossTarget.getPosition();
            if(bossTarget instanceof Ore)
            {
                ((Ore)bossTarget).setTargeted();
                ((Ore)bossTarget).setTargetee(this);
            }


            if (moveTo(world, bossTarget, scheduler))
            {
                Quake quake = new Quake(QUAKE_KEY, tgtPos, imageStore.getImageList(QUAKE_KEY), 7000, 1000);

                world.addEntity(quake);
                nextPeriod += super.getActionPeriod();
                quake.scheduleActions(scheduler, world, imageStore);
            }
        }
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                nextPeriod);
    }

//    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler){
//        OreFactory o = getV().getOre();
//        if (Point.adjacent(this.getPosition(), target.getPosition())){
//            if(target instanceof Ore) {
//                world.removeEntity(target);
//                scheduler.unscheduleAllEvents(target);
//                return true;
//            }
//            if (target instanceof MainCharacter)
//            {
//
//                if(getV().getMain().getH().takeDamage()) {
//                    getV().getMain().die();
//                    return true;
//                }
//
//            }
//            return false;
//        }
//        else
//        {
//            Point nextPos = nextPosition(world, target.getPosition());
//
//            if (!this.getPosition().equals(nextPos))
//            {
//                Optional<Entity> occupant = world.getOccupant( nextPos);
//                if (occupant.isPresent())
//                {
//                    scheduler.unscheduleAllEvents( occupant.get());
//                }
//
//                    world.moveEntity( this, nextPos);
//            }
//            return false;
//        }
//    }
}
