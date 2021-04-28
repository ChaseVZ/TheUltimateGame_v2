import processing.core.PImage;
import java.util.List;
import java.util.Optional;

public class Atlantis extends AnimatedEntity{

    private static final int ATLANTIS_ANIMATION_REPEAT_COUNT = 7;

    public Atlantis(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, 0, actionPeriod, 0);
    }


    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent(this,
                createAnimationAction(ATLANTIS_ANIMATION_REPEAT_COUNT),
                this.getAnimationPeriod());
    }
}

