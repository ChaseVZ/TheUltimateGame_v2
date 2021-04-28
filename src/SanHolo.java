import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class SanHolo extends Moveable {

    public SanHolo(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, 0, actionPeriod, animationPeriod);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Entity> bossTarget = world.findNearest(this.getPosition(), MainCharacter.class);
        long nextPeriod = super.getActionPeriod();

        if (bossTarget.isPresent())
        {
            Point tgtPos = bossTarget.get().getPosition();

            String suffix = getDirectionToTarget(tgtPos, getPosition());
            if (!(suffix.equals("U") || suffix.equals("D")))
            {
                setImageIndex(0);
                setImages(getV().getImageStore().getImageList("sanHolo"+ suffix));
            }


            if (moveTo(world, bossTarget.get(), scheduler))
            {
                SpawnQuake(tgtPos);
                nextPeriod += super.getActionPeriod();
            }
        }
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                nextPeriod);
    }

}

